package com.innowise.orderservice.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager manager) {
        return requestTemplate -> {

            OAuth2AuthorizeRequest request =
                    OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                            .principal("order-service")
                            .build();

            OAuth2AuthorizedClient client = manager.authorize(request);

            if (client != null) {
                String token = client.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}