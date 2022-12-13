package com.brunobat.micrometer.client;

import io.micrometer.core.instrument.MeterRegistry;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
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
