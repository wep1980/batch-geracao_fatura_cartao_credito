package br.com.wepdev.faturacartaocreditojob.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


    /**
     * Configuracao para o banco do spring batch
     * @return
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource springDataSource(){
        return DataSourceBuilder.create().build();
    }


    /**
     * Banco da aplicação
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSource appDataSource(){
        return DataSourceBuilder.create().build();
    }
}

