package vp.botv.monitoring;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import vp.botv.monitoring.processor.MonitoringProcessor;

@Aspect
@Component
@RequiredArgsConstructor
public class MonitoringAspect {

    private final MonitoringProcessor monitoringProcessor;

    @Around("@annotation(monitoring)")
    public Object handleMonitoring(ProceedingJoinPoint proceedingJoinPoint, Monitoring monitoring) {

        String metricName = monitoring.value();
        Object result = null;
        long time = System.currentTimeMillis();

        monitoringProcessor.notifyTotal(metricName);

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            monitoringProcessor.notifyError(metricName);
            throw new RuntimeException(e);
        }

        monitoringProcessor.notifySuccess(metricName, System.currentTimeMillis() - time);

        return result;
    }


}
