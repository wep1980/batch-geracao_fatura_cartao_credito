package br.com.wepdev.faturacartaocreditojob.reader;

import br.com.wepdev.faturacartaocreditojob.dominio.FaturaCartaoCredito;
import br.com.wepdev.faturacartaocreditojob.dominio.Transacao;
import org.springframework.batch.item.*;

public class FaturaCartaoCreditoReader implements ItemStreamReader<FaturaCartaoCredito> {


    private ItemStreamReader<Transacao> delegate; // Padrao delegate de leitor
    private Transacao transacaoAtual;

    /**
     *  Construtor que inicializa o delegate
     */
    public FaturaCartaoCreditoReader(ItemStreamReader<Transacao> delegate){
        this.delegate = delegate;
    }


    @Override
    public FaturaCartaoCredito read() throws Exception {
        if (transacaoAtual == null)
            transacaoAtual = delegate.read();

        FaturaCartaoCredito faturaCartaoCredito = null;
        Transacao transacao = transacaoAtual;
        transacaoAtual = null;

        if (transacao != null) { // Adicionando todas as faturas em uma unica fatura
            faturaCartaoCredito = new FaturaCartaoCredito();
            faturaCartaoCredito.setCartaoCredito(transacao.getCartaoCredito());
            faturaCartaoCredito.setCliente(transacao.getCartaoCredito().getCliente());
            faturaCartaoCredito.getTransacoes().add(transacao);

            while (isTransacaoRelacionada(transacao))
         faturaCartaoCredito.getTransacoes().add(transacaoAtual);
        }
        return faturaCartaoCredito;
    }

    /**
     * metodo que sempre espia a proxima transação
     * @param transacao
     * @return
     */
    private boolean isTransacaoRelacionada(Transacao transacao) throws Exception {
        return peek() != null && transacao.getCartaoCredito().getNumeroCartaoCredito() == transacao.getCartaoCredito().getNumeroCartaoCredito();
    }


    private Transacao peek() throws Exception {
        transacaoAtual = delegate.read();
        return transacaoAtual;
    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
         delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);

    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();

    }
}
