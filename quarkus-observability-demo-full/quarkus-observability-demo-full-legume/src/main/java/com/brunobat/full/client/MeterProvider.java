package com.brunobat.full.client;

import io.micrometer.core.instrument.MeterRegistry;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class MeterProvider implements ClientRequestFilter {
    @Inject
    MeterRegistry registry;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        registry.counter("http.client.requests",
                "client.class", requestContext.getClient().getClass().getName());
    }
}
