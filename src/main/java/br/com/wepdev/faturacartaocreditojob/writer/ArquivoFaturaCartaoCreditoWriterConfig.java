package br.com.wepdev.faturacartaocreditojob.writer;

import br.com.wepdev.faturacartaocreditojob.dominio.FaturaCartaoCredito;
import br.com.wepdev.faturacartaocreditojob.dominio.Transacao;

import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@Configuration
public class ArquivoFaturaCartaoCreditoWriterConfig {


    /**
     * Metodo escritor de multiplos arquivos
     * @return
     */
    @Bean
    public MultiResourceItemWriter<FaturaCartaoCredito> arquivosFaturaCartaoCredito(){
        return new MultiResourceItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivosFaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura")) // Nome do arquivo de escrita
                .itemCountLimitPerResource(1)
                .resourceSuffixCreator(suffixCreator())
                .delegate(arquivoFaturaCartaoCredito())
                .build();
    }


    /**
     * Metodo escritor de um unico arquivo
     * @return
     */
    private FlatFileItemWriter<FaturaCartaoCredito> arquivoFaturaCartaoCredito() {
        return new FlatFileItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivoFaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura.txt"))
                .lineAggregator(lineAggregador()) // Configura a logica de agregação de linha que exibe as transações da fatura do cartao no arquivo. O CORPO DO ARQUIVO
                .headerCallback(headerCallback()) // Criando o cabeçalho do arquivo
                .footerCallback(footerCallback()) // Criando o rodape do arquivo
                .build();
    }

    /**
     * Metodo que chama a classe que contem a logica do valor total que é escrito e exibido no arquivo de escrita.
     * Para totalizar os registros escritos e utilizado um listener, totaliza o valor antes da escrita
     * @return
     */
    @Bean
    public FlatFileFooterCallback footerCallback() {
        return new TotalTransacoesFooterCallback();
    }


    /**
     * Metodo que cria e escreve o cabeçalho do arquivo
     * @return
     */
    private FlatFileHeaderCallback headerCallback() {
        return new FlatFileHeaderCallback() {

            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.append(String.format("%121s\n", "Cartão XPTO"));
                writer.append(String.format("%121s\n\n", "Rua Vergueiro 131"));
            }
        };
    }

    
    /**
     * Metodo que configura a escrita no arquivo, o corpo do arquivo
     * @return
     */
    private LineAggregator<FaturaCartaoCredito> lineAggregador() {
        return new LineAggregator<FaturaCartaoCredito>() {

            @Override
            public String aggregate(FaturaCartaoCredito faturaCartaoCredito) {

                StringBuilder writer = new StringBuilder();
                writer.append(String.format("Nome: %s\n", faturaCartaoCredito.getCliente().getNome())); // Cabeçalho
                writer.append(String.format("Endereço: %s\n\n\n", faturaCartaoCredito.getCliente().getEndereco()));  // Cabeçalho
                writer.append(String.format("Fatura completa do cartão %d\n", faturaCartaoCredito.getCartaoCredito().getNumeroCartaoCredito()));  // Cabeçalho
                writer.append("--------------------------------------------------------------------------------------------------------------------------\n");  // FIM Cabeçalho
                writer.append("DATA DESCRICAO VALOR\n");
                writer.append("--------------------------------------------------------------------------------------------------------------------------\n");

                // Escrevendo todas as transações da Fatura
                for(Transacao transacao : faturaCartaoCredito.getTransacoes()){
                    writer.append(String.format("\n[%10s] %-80s - %s", // Formatacao para Data, descricao e valor
                            new SimpleDateFormat("dd/MM/yyyy").format(transacao.getData()),
                            transacao.getDescricao(),
                            NumberFormat.getCurrencyInstance().format(transacao.getValor())));
                }
                return writer.toString(); // Retornando a String construida com todas as transações
            }
        };
    }


    /**
     * Metodo que coloca o sufixo no final dos arquivos com formato txt
     * @return
     */
    private ResourceSuffixCreator suffixCreator() {
        return new ResourceSuffixCreator() {

            @Override
            public String getSuffix(int index) {
                return index + ".txt";
            }
        };
    }


}
