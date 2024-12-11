package vp.botv.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import vp.botv.command.Command;
import vp.botv.command.MessageEditor;
import vp.botv.common.VpnSubscription;
import vp.botv.monitoring.Monitoring;
import vp.botv.queuing.QueueWriter;
import vp.botv.service.ClientService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Optional.ofNullable;
import static vp.botv.common.Constants.FREE_TRIAL;
import static vp.botv.common.Constants.FREE_TRIAL_DAYS;
import static vp.botv.common.Constants.FREE_TRIAL_DESCRIPTION;

@Component
@RequiredArgsConstructor
public class CallbackQueryCommand extends MessageEditor implements Command {

    @Value("${payments.tg-payment}")
    private String payment;

    private final QueueWriter messageQueueService;

    private final ClientService clientService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    @Monitoring("COMMAND")
    public void execute(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long clientId = ofNullable(update.getCallbackQuery()).map(CallbackQuery::getFrom).map(User::getId).orElse(0L);

        if ("info".equals(callbackData)) {
            boolean clientFreeTrialAccessUsed = clientService.clientFreeTrialAccessUsed(clientId);
            SendMessage mainMenuMessage = sendMainMenu(chatId, "[Инструкция по подключению](https://telegra.ph/Instrukciya-po-podklyucheniyu-Prive-VPN-11-13)", clientFreeTrialAccessUsed);
            messageQueueService.write(mainMenuMessage);
        } else if ("get_vpn".equals(callbackData)) {
            SendMessage purchaseMenuMessage = sendPurchaseMenu(chatId, "Выберите тариф");
            messageQueueService.write(purchaseMenuMessage);
        } else if ("get_vpn_server".equals(callbackData)) {
            SendMessage message = buildMessage(chatId, getVpnServerPurchaseText());
            messageQueueService.write(message);
        } else if ("get_free_vpn".equals(callbackData)) {
            boolean clientFreeTrialAccessUsed = clientService.clientFreeTrialAccessUsed(clientId);

            if (clientFreeTrialAccessUsed) {
                messageQueueService.write((sendPurchaseMenu(chatId, getRunOutOfFreeSubscriptions())));
            } else {
                executorService.submit(() -> processFreeTrialSubscription(clientId, chatId, update));
            }
        } else {
            Optional<VpnSubscription> vpnSubscriptionOptional = Arrays.stream(VpnSubscription.values())
                    .filter(sub -> sub.name().equals(callbackData))
                    .findFirst();

            vpnSubscriptionOptional.ifPresent(vpnSubscription -> {
                SendInvoice sendInvoice = buildInvoice(chatId, payment, vpnSubscription.toString(), vpnSubscription.getPrice());
                messageQueueService.write(sendInvoice);
            });
        }
    }

    private void processFreeTrialSubscription(long clientId, long chatId, Update update) {

        String telegramLink = ofNullable(update.getCallbackQuery()).map(CallbackQuery::getFrom).map(User::getUserName).orElse("");
        String telegramName = ofNullable(update.getCallbackQuery()).map(CallbackQuery::getFrom).map(User::getFirstName).orElse("")
                .concat(" ")
                .concat(ofNullable(update.getCallbackQuery()).map(CallbackQuery::getFrom).map(User::getLastName).orElse(""));

        String keyId = "U".concat(String.valueOf(clientId)).concat("C".concat(String.valueOf(chatId)))
                .concat(UUID.randomUUID().toString());

        String code = clientService.createClient(clientId, chatId, telegramLink, telegramName, FREE_TRIAL, keyId, 0L);

        messageQueueService.write(buildMessage(chatId, "Идентификатор ключа: ".concat(keyId)));
        SendMessage sendMessage = buildMarkdownMessage(
                chatId, getPurchaseText(FREE_TRIAL_DESCRIPTION,
                        ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime()
                                .plusDays(FREE_TRIAL_DAYS).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")))
        );

        messageQueueService.write(sendMessage);
        messageQueueService.write(buildMarkdownMessage(chatId, checkCode(code)));
    }
}
