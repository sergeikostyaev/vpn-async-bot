package vp.botv.command.dispatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vp.botv.command.Command;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    @Qualifier("preCheckoutCommand")
    private final Command preCheckoutCommand;

    @Qualifier("successfulPaymentCommand")
    private final Command successfulPaymentCommand;

    @Qualifier("messageTextCommand")
    private final Command messageTextCommand;

    @Qualifier("callbackQueryCommand")
    private final Command callbackQueryCommand;

    public void dispatch(Update update) {
        if (update.hasPreCheckoutQuery()) {
            preCheckoutCommand.execute(update);
        } else if (update.hasMessage() && nonNull(update.getMessage().getSuccessfulPayment())) {
            successfulPaymentCommand.execute(update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            messageTextCommand.execute(update);
        } else if (update.hasCallbackQuery()) {
            callbackQueryCommand.execute(update);
        }
    }

}
