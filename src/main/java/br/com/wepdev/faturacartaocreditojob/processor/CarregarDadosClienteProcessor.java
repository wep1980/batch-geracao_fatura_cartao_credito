package br.com.wepdev.faturacartaocreditojob.processor;

import br.com.wepdev.faturacartaocreditojob.dominio.Cliente;
import br.com.wepdev.faturacartaocreditojob.dominio.FaturaCartaoCredito;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CarregarDadosClienteProcessor implements ItemProcessor<FaturaCartaoCredito, FaturaCartaoCredito> {

    // Utilizado para pegar acessar serviços externos
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public FaturaCartaoCredito process(FaturaCartaoCredito faturaCartaoCredito) throws Exception {

        String uri = String.format("http://my-json-server.typicode.com/giuliana-bezerra/demo/profile/%d", faturaCartaoCredito.getCliente().getId());

        ResponseEntity<Cliente> response = restTemplate.getForEntity(uri, Cliente.class); // Fazendo o binding do json pora o objeto Cliente

        if(response.getStatusCode() != HttpStatus.OK)
            throw new ValidationException("Cliente não encontrado!");

        faturaCartaoCredito.setCliente(response.getBody()); // Preenchendo os dados do cliente com o body da resposta

        return faturaCartaoCredito;
    }
}
