package vp.botv.command.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import vp.botv.command.Command;
import vp.botv.monitoring.Monitoring;
import vp.botv.queuing.QueueWriter;

@Component
@RequiredArgsConstructor
public class PreCheckoutCommand implements Command {

    private final QueueWriter messageQueueService;

    @Override
    @Monitoring("PRE_CHECKOUT")
    public void execute(Update update) {
        messageQueueService.write(new AnswerPreCheckoutQuery(update.getPreCheckoutQuery().getId(), true));
    }
}
