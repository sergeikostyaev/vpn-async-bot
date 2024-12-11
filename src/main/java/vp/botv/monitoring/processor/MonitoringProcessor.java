package vp.botv.monitoring.processor;

public interface MonitoringProcessor {

    void notifyTotal(String metricName);

    void notifySuccess(String metricName, Long duration);

    void notifyError(String metricName);

}
