package vp.botv.bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vp.botv.command.dispatcher.CommandDispatcher;
import vp.botv.configuration.BotConfiguration;
import vp.botv.queuing.QueueReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;

    private final CommandDispatcher commandDispatcher;

    private final QueueReader messageQueueService;


    @PostConstruct
    private void initMessaging() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Thread(() -> {
            Thread.currentThread().setName("Message processor thread");
            log.info("Registered thread: {}", Thread.currentThread().getName());
            while (true) {
                try {
                    BotApiMethod<?> sendMessage = messageQueueService.read();
                    log.info("Sending message with thread: {}", Thread.currentThread().getName());

                    sendBotApiAnyMethod(sendMessage);
                } catch (Exception e) {
                    log.error("ERROR: {}", e.getMessage());
                }
            }
        }));
    }

    @Override
    public void onUpdateReceived(Update update) {
        commandDispatcher.dispatch(update);
    }

    private void sendBotApiAnyMethod(BotApiMethod<?> sendMethod) {
        try {
            execute(sendMethod);
        } catch (TelegramApiException e) {
            log.error("ERROR: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }

}
