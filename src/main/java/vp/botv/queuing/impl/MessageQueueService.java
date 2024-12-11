package vp.botv.queuing.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import vp.botv.queuing.QueueReader;
import vp.botv.queuing.QueueWriter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class MessageQueueService implements QueueReader, QueueWriter {

    private final BlockingQueue<BotApiMethod<?>> messageQueue = new ArrayBlockingQueue<>(100);

    @Override
    public BotApiMethod<?> read() {
        try {
            return messageQueue.take();
        } catch (InterruptedException e) {
            log.error("ERROR: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(BotApiMethod<?> botApiMethod) {
        messageQueue.add(botApiMethod);
    }
}
