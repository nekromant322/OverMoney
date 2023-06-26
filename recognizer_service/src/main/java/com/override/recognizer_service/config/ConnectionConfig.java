package com.override.recognizer_service.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Configuration
public class ConnectionConfig {
    @Autowired
    private WitAIProperties witAIProperties;

    @Bean
    @ConditionalOnExpression("#{!environment.getProperty('spring.profiles.active').contains('dev')}")
    @SneakyThrows
    public HttpURLConnection getConnection() {
        String query = String.format(
                witAIProperties.getVersionParam(),
                URLEncoder.encode(witAIProperties.getVersion(), witAIProperties.getCharset())
        );

        URLConnection connectionURL =
                new URL(witAIProperties.getUrl() +
                        witAIProperties.getParamSeparator() +
                        query).openConnection();
        HttpURLConnection connection = (HttpURLConnection) connectionURL;
        connection.setRequestMethod(witAIProperties.getMethod());
        connection.setRequestProperty(witAIProperties.getAuthProperty(), witAIProperties.getToken());
        connection.setRequestProperty(witAIProperties.getContentTypeProperty(),
                witAIProperties.getContentTypeValue());
        connection.setDoOutput(true);

        return connection;
    }
}
