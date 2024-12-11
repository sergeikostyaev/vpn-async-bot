package vp.botv.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import vp.botv.command.Command;
import vp.botv.command.MessageEditor;
import vp.botv.monitoring.Monitoring;
import vp.botv.queuing.QueueWriter;
import vp.botv.service.ClientService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Optional.ofNullable;
import static vp.botv.common.VpnSubscription.ERROR;
import static vp.botv.common.VpnSubscription.values;

@Component
@RequiredArgsConstructor
public class SuccessfulPaymentCommand extends MessageEditor implements Command {

    private final QueueWriter messageQueueService;

    private final ClientService clientService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    @Monitoring("SUCCESS_PAYMENTS")
    public void execute(Update update) {
        executorService.submit(() -> {
            SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();

            long chatId = update.getMessage().getChatId();
            long clientId = ofNullable(update.getMessage()).map(Message::getFrom).map(User::getId).orElse(0L);
            long totalAmount = update.getMessage().getSuccessfulPayment().getTotalAmount();

            String telegramLink = ofNullable(update.getMessage()).map(Message::getFrom).map(User::getUserName).orElse("");
            String telegramName = ofNullable(update.getMessage()).map(Message::getFrom).map(User::getFirstName).orElse("")
                    .concat(" ")
                    .concat(ofNullable(update.getMessage()).map(Message::getFrom).map(User::getLastName).orElse(""));

            var subscription = Arrays.stream(values()).filter(code -> code.name()
                    .equals(successfulPayment.getInvoicePayload())).findFirst().orElse(ERROR);

            String keyId = "U".concat(String.valueOf(clientId)).concat("C".concat(String.valueOf(chatId)))
                    .concat(UUID.randomUUID().toString());

            messageQueueService.write(buildMessage(chatId, "Идентификатор ключа: ".concat(keyId)));

            String code = clientService.createClient(clientId, chatId, telegramLink, telegramName, subscription.name(), keyId, totalAmount / 100);

            SendMessage sendMessage = buildMarkdownMessage(
                    chatId, getPurchaseText(subscription.getDescription(),
                            ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime()
                                    .plusDays(subscription.getDays()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")))
            );

            messageQueueService.write(sendMessage);
            messageQueueService.write(buildMessage(chatId, checkCode(code)));
        });

    }
}
