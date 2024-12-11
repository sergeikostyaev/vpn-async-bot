package vp.botv.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vp.botv.common.VpnSubscription;
import vp.botv.dto.IntegrationRequestDto;
import vp.botv.dto.IntegrationResponseDto;
import vp.botv.dto.TokenKeeperDto;
import vp.botv.entity.Client;
import vp.botv.entity.Purchase;
import vp.botv.loadbalancer.LoadBalancer;
import vp.botv.repository.ClientRepository;
import vp.botv.repository.PurchaseRepository;
import vp.botv.service.ClientService;
import vp.botv.webclient.WebClientService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static vp.botv.common.Constants.FREE_TRIAL;
import static vp.botv.common.Constants.FREE_TRIAL_DAYS;
import static vp.botv.common.VpnSubscription.ERROR;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final WebClientService integrationWebClient;

    private final ClientRepository clientRepository;

    private final PurchaseRepository purchaseRepository;

    private final LoadBalancer loadBalancer;

    private static final Integer LIMIT_IP = 1;

    private static final Integer TOTAL_GB = 0;

    public String createClient(Long clientId, Long chatId, String telegramLink, String telegramName, String subscription, String keyId, Long totalAmount) {

        Client client = clientRepository.findByClientTelegramId(clientId);

        if (isNull(client)) {
            client = clientRepository.save(buildNewClient(LocalDate.now(), clientId));
        }

        if (client.getGotFreeTrial() && FREE_TRIAL.equals(subscription)) {
            return null;
        }

        String integrationLink = loadBalancer.getEndpoint();

        String expTime = FREE_TRIAL.equals(subscription) ? getFreeTrialExpTimeString() : extractExpTime(subscription);

        Purchase purchase = buildPurchase(
                subscription, totalAmount, clientId, chatId, telegramLink, telegramName,
                keyId, LocalDateTime.parse(expTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), null, client, integrationLink
        );

        purchase = purchaseRepository.save(purchase);

        if (ERROR.name().equals(subscription)) {
            return null;
        }

        IntegrationRequestDto integrationRequestDto = buildIntegrationRequestDto(keyId, subscription, expTime);

        TokenKeeperDto tokenKeeperDto = integrationWebClient.callToken(integrationLink);

        if (isNull(tokenKeeperDto)) {
            return null;
        }

        IntegrationResponseDto integrationResponseDto = integrationWebClient.callIntegration(integrationLink, tokenKeeperDto.getToken(), integrationRequestDto);

        if (isNull(integrationResponseDto) || isNull(integrationResponseDto.getCode())) {
            return null;
        }

        purchase.setSuccess(true);
        purchaseRepository.save(purchase);

        client.setTotalPaid(client.getTotalPaid() + totalAmount);
        client.setTotalPurchases(client.getTotalPurchases() + 1);

        if (FREE_TRIAL.equals(subscription)) {
            client.setGotFreeTrial(true);
        }

        clientRepository.save(client);

        return integrationResponseDto.getCode();
    }

    @Override
    public Boolean clientFreeTrialAccessUsed(Long clientId) {
        Client client = clientRepository.findByClientTelegramId(clientId);

        return nonNull(client) && client.getGotFreeTrial();
    }

    private Purchase buildPurchase(
            String subscriptionType,
            Long subscriptionPrice,
            Long clientTelegramId,
            Long chatId,
            String telegramLink,
            String telegramName,
            String keyId,
            LocalDateTime expTime,
            String promocode,
            Client client,
            String integrationLink
    ) {
        return Purchase.builder()
                .subscriptionType(subscriptionType)
                .subscriptionPrice(subscriptionPrice)
                .clientTelegramId(clientTelegramId)
                .chatId(chatId)
                .telegramLink(telegramLink)
                .telegramName(telegramName)
                .purchaseDateTime(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime())
                .keyId(keyId)
                .isClientWarned(false)
                .expTime(expTime)
                .promocode(promocode)
                .success(false)
                .integrationLink(integrationLink)
                .client(client)
                .build();
    }

    private Client buildNewClient(LocalDate registrationDate, Long clientTelegramId) {
        return Client.builder()
                .registrationDate(registrationDate)
                .clientTelegramId(clientTelegramId)
                .totalPaid(0L)
                .gotFreeTrial(false)
                .totalPurchases(0L)
                .build();
    }

    private IntegrationRequestDto buildIntegrationRequestDto(String keyId, String subscription, String expTime) {
        return IntegrationRequestDto.builder()
                .email(keyId)
                .id(UUID.randomUUID().toString())
                .expTime(expTime)
                .limit_ip(LIMIT_IP)
                .total_gb(TOTAL_GB)
                .build();
    }

    private String extractExpTime(String subscription) {
        Integer days = Arrays.stream(VpnSubscription.values())
                .filter(sub -> sub.name().equals(subscription))
                .findFirst()
                .map(VpnSubscription::getDays)
                .orElse(1);

        return ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime().plusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    private String getFreeTrialExpTimeString() {
        return ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime().plusDays(FREE_TRIAL_DAYS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
