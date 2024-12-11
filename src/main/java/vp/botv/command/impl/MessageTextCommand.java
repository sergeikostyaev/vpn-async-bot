package vp.botv.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import vp.botv.command.Command;
import vp.botv.command.MessageEditor;
import vp.botv.monitoring.Monitoring;
import vp.botv.queuing.QueueWriter;
import vp.botv.service.ClientService;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class MessageTextCommand extends MessageEditor implements Command {

    private final QueueWriter messageQueueService;

    private final ClientService clientService;

    @Override
    @Monitoring("TEXT")
    public void execute(Update update) {
        String userMessageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        long clientId = ofNullable(update.getMessage().getFrom()).map(User::getId).orElse(0L);

        if ("/start".equals(userMessageText)) {
            Boolean clientFreeTrialAccessUsed = clientService.clientFreeTrialAccessUsed(clientId);

            SendMessage mainMenuMessage = sendMainMenu(chatId, getWelcomeText(), clientFreeTrialAccessUsed);
            messageQueueService.write(mainMenuMessage);
        }
    }
}
