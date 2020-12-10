package ru.nazariene.elasticmonitor.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import ru.nazariene.elasticmonitor.configuration.properties.ElasticsearchProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HttpClientConfiguration {

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    @Profile("!dev")
    public RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback(ElasticsearchProperties elasticProperties)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticProperties.getUsername(), elasticProperties.getPassword()));

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        log.info("Certificate validation is disabled");
        return httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            httpClientBuilder.setSSLContext(sslContext);
            httpClientBuilder.setSSLHostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost()));
            return httpClientBuilder;
        };
    }

}
