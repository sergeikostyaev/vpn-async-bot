package vp.botv.webclient;

import vp.botv.dto.IntegrationRequestDto;
import vp.botv.dto.IntegrationResponseDto;
import vp.botv.dto.TokenKeeperDto;

public interface WebClientService {

    TokenKeeperDto callToken(String url);

    IntegrationResponseDto callIntegration(String url, String token, IntegrationRequestDto integrationRequestDto);

}
