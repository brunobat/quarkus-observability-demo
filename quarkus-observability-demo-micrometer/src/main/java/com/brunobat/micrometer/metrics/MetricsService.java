package com.brunobat.micrometer.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MetricsService {

    @Inject
    MeterRegistry registry;

    public void incrementRequestStart() {
        registry.counter("incomming_user_request", "state", "started").increment();
    }

    public void incrementRequestDone(String statusCode) {
        registry.counter("incomming_user_request", "state", "done", "statusCode", statusCode).increment();
    }

    public void incrementRequestFailure(String errorCode) {
        registry.counter("incomming_user_request", "state", "failure", "errorCode", errorCode).increment();
    }
}