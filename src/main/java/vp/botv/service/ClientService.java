package vp.botv.service;

public interface ClientService {

    String createClient(Long clientId, Long chatId, String telegramLink, String telegramName, String subscription, String keyId, Long totalAmount);

    Boolean clientFreeTrialAccessUsed(Long clientId);

}
