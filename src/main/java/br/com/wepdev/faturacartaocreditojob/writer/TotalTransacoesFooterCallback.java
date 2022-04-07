package br.com.wepdev.faturacartaocreditojob.writer;

import br.com.wepdev.faturacartaocreditojob.dominio.FaturaCartaoCredito;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.file.FlatFileFooterCallback;


import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.List;

/**
 * Classe que executa a logica do valor total que e exibido no rodape do arquivo de escrita
 */
public class TotalTransacoesFooterCallback implements FlatFileFooterCallback {

    private Double total = 0.0;

    /**
     * Metodo que escreve o total no rodapé
     * @param writer
     * @throws IOException
     */
    @Override
    public void writeFooter(Writer writer) throws IOException {

        writer.write(String.format("\n%121s", "Total: " + NumberFormat.getCurrencyInstance().format(total)));
    }


    /**
     * Evento que acontece antes da escrita no arquivo, para que seja somados os valores de cada fatura
     * @param faturas
     */
    @BeforeWrite
    public void beforeWrite(List<FaturaCartaoCredito> faturas){

        for(FaturaCartaoCredito faturaCartaoCredito : faturas)
            total += faturaCartaoCredito.getTotal();
    }

    /**
     * Metodo que zera o valor total depois da escrita de cada fatura
     * @param chunkContext
     */
    @AfterChunk
    public void afterChunk(ChunkContext chunkContext){
        total = 0.0;

    }
}
