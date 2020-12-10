package ru.nazariene.elasticmonitor.configuration;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ru.nazariene.elasticmonitor.configuration.properties.ElasticsearchProperties;

@Configuration
@Profile("!dev")
public class ElasticsearchConfiguration {

    @Bean
    public RestHighLevelClient restHighLevelClient(
            @Autowired ElasticsearchProperties elasticProperties,
            @Autowired(required = false) RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback
    ) {
        RestClientBuilder clientBuilder = RestClient.builder(
                new HttpHost(elasticProperties.getHost(),
                        elasticProperties.getPort(),
                        "https"));

        if (httpClientConfigCallback != null) {
            clientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
        }

        return new RestHighLevelClient(clientBuilder);
    }

    @Bean
    public RestClient restClient(
            @Autowired ElasticsearchProperties elasticProperties,
            @Autowired(required = false) RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback
    ) {
        return restHighLevelClient(elasticProperties, httpClientConfigCallback).getLowLevelClient();
    }

}
