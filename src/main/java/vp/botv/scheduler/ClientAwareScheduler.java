package vp.botv.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import vp.botv.common.VpnSubscription;
import vp.botv.entity.Purchase;
import vp.botv.queuing.QueueWriter;
import vp.botv.repository.PurchaseRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static vp.botv.common.Constants.FREE_TRIAL;
import static vp.botv.common.Constants.FREE_TRIAL_DESCRIPTION;
import static vp.botv.common.VpnSubscription.ERROR;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ClientAwareScheduler {

    private final PurchaseRepository purchaseRepository;

    private final QueueWriter messageQueueService;

    @Scheduled(cron = "0 */1 * * * *")
    public void ProcessClientExpiringSubscriptions() {

        LocalDateTime time = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime();

        List<Purchase> expiredSubscriptions = purchaseRepository.findAllByExpTimeIsBeforeAndIsClientWarnedFalse(time);

        expiredSubscriptions.stream().filter(sub -> !sub.getIsClientWarned()).forEach(sub -> {

            VpnSubscription subscription = getSubscription(sub.getSubscriptionType());

            messageQueueService.write(sendPurchaseMenu(
                    sub.getChatId(),
                    sub.getSubscriptionType().equals(FREE_TRIAL) ? getPurchaseOutOfTime(FREE_TRIAL_DESCRIPTION) : getPurchaseOutOfTime(subscription.getDescription())
            ));

            sub.setIsClientWarned(true);
            purchaseRepository.save(sub);
        });


    }

    protected SendMessage sendPurchaseMenu(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Arrays.stream(VpnSubscription.values()).filter(sub -> !sub.name().equals(ERROR.name())).forEach(sub -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(sub.getDescription());
            button.setCallbackData(sub.name());
            buttons.add(List.of(button));
        });

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Инструкция по подключению");
        button5.setCallbackData("info");
        buttons.add(List.of(button5));

        markup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markup);

        return sendMessage;
    }

    private VpnSubscription getSubscription(String subscription) {

        return Arrays.stream(VpnSubscription.values())
                .filter(sub -> sub.name().equals(subscription))
                .findFirst()
                .orElse(VpnSubscription.ERROR);
    }

    private String getPurchaseOutOfTime(String subscription) {
        return """
                ⏳ Услуга "*%s*", приобретенная вами, закончилась.
                                                 
                ❤️ Мы благодарим вас за доверие и за то, что выбрали наш сервис для безопасного и удобного интернет-соединения.
                            
                🔑 Если вас устроил наш сервис, вы можете приобрести подписку на VPN и продолжать пользоваеться безопасным и 
                анонимным доступом в интернет без лимитов и ограничений скорости.
                """.formatted(subscription);
    }

}
