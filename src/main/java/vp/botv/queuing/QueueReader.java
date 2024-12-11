package vp.botv.queuing;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface QueueReader {
    BotApiMethod<?> read();
}
