package vp.botv.monitoring.processor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitoringProcessorImpl implements MonitoringProcessor {

    private final MeterRegistry meterRegistry;


    @Override
    public void notifyTotal(String metricName) {
        String COUNTER_TOTAL_PATTERN = "COUNTER_TOTAL_%s";
        notifyCounter(String.format(COUNTER_TOTAL_PATTERN, metricName));
    }

    @Override
    public void notifySuccess(String metricName, Long duration) {
        String COUNTER_SUCCESS_PATTERN = "COUNTER_SUCCESS_%s";
        notifyCounter(String.format(COUNTER_SUCCESS_PATTERN, metricName));
        String TIMER_PATTERN = "TIMER_%s";
        notifyTimer(String.format(TIMER_PATTERN, metricName), duration);
    }

    @Override
    public void notifyError(String metricName) {
        String COUNTER_ERROR_PATTERN = "COUNTER_ERROR_%s";
        notifyCounter(String.format(COUNTER_ERROR_PATTERN, metricName));
    }

    private void notifyCounter(String metricName) {
        try {
            meterRegistry.counter(metricName).increment();
        } catch (Exception e) {
            log.info("notifyCounter error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void notifyTimer(String metricName, Long duration) {
        try {
            meterRegistry.timer(metricName).record(Duration.ofMillis(duration));
        } catch (Exception e) {
            log.info("notifyTimer error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
