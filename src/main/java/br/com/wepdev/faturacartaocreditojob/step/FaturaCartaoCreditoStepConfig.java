package br.com.wepdev.faturacartaocreditojob.step;

import br.com.wepdev.faturacartaocreditojob.dominio.FaturaCartaoCredito;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FaturaCartaoCreditoStepConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public Step faturaCartaoCreditoStep(
            ItemReader<FaturaCartaoCredito> lerTransacoesReader, // leitor que ler as transacoes armazenadas no banco de dados
            ItemProcessor<FaturaCartaoCredito, FaturaCartaoCredito> carregaDadosClienteProcessor, // Processador que carrega os dados do cliente de cada transação
            ItemWriter<FaturaCartaoCredito> escreverFaturaCartaoCredito) { // Escritor que escrece a fatura em arquivos

        return stepBuilderFactory
                .get("faturaCartaoCreditoStep")
                .<FaturaCartaoCredito, FaturaCartaoCredito>chunk(1)// ler faturaCartaoCredito e retorna para a escrita faturaCartaoCredito, tamanho do chunk 1, pois ele vai gerar 1 arquivo por cliente
                .reader(lerTransacoesReader)
                .processor(carregaDadosClienteProcessor)
                .writer(escreverFaturaCartaoCredito)
                .build();
    }

}
