package vp.botv.loadbalancer.impl;

import org.springframework.stereotype.Component;
import vp.botv.loadbalancer.LoadBalancer;

import java.util.List;

@Component
public class LoadBalancerImpl implements LoadBalancer {

    private final List<String> endpoints;

    private int endpointIndex = 0;

    public LoadBalancerImpl() {
        this.endpoints = List.of("http://45.138.74.165", "http://62.60.238.151", "http://85.192.42.111", "http://95.163.153.244");
    }

    @Override
    public String getEndpoint() {

        String endpoint = endpoints.get(endpointIndex);

        endpointIndex++;

        if (endpointIndex >= endpoints.size()) {
            endpointIndex = 0;
        }

        return endpoint;
    }
}