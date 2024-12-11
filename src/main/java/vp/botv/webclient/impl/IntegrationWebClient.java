package vp.botv.webclient.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import vp.botv.dto.IntegrationRequestDto;
import vp.botv.dto.IntegrationResponseDto;
import vp.botv.dto.TokenKeeperDto;
import vp.botv.webclient.WebClientService;

@Component
public class IntegrationWebClient implements WebClientService {

    @Value("${integration.prime-token}")
    private String primeToken;

    @Override
    public TokenKeeperDto callToken(String url) {
        try {

            RestClient client = RestClient.builder()
                    .baseUrl(url)
                    .defaultHeader("Authorization-P", primeToken)
                    .build();

            return client.get()
                    .uri("/token")
                    .retrieve()
                    .body(TokenKeeperDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public IntegrationResponseDto callIntegration(String url, String token, IntegrationRequestDto integrationRequestDto) {
        try {

            RestClient client = RestClient.builder()
                    .baseUrl(url)
                    .defaultHeader("Authorization-P", primeToken)
                    .build();

            return client.post()
                    .uri("/api/c")
                    .body(integrationRequestDto)
                    .header("Authorization", token)
                    .retrieve()
                    .body(IntegrationResponseDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}
