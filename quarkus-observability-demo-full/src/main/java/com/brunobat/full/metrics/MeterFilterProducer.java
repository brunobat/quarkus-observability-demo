package com.brunobat.full.metrics;


import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;

import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@Singleton
public class MeterFilterProducer {

    @Produces
    @Singleton
    public MeterFilter customMeterFilter() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                try {
                    if (id.getName().startsWith("http.server.requests")) {
                        String somethingFromRequest = getSomethingFromRequest();
                        Tag newTag = Tag.of("differentValueOnEachCall", somethingFromRequest);
                        return id.withTag(newTag);
                    }
                } catch (ContextNotActiveException e) {
                    return id.withTag(Tag.of("differentValueOnEachCall", "fail"));
                }
                return id;
            }
        };
    }

    private String getSomethingFromRequest() {
        if (CDI.current().select(RequestContext.class).isResolvable()) {
            return CDI.current().select(RequestContext.class).get().getSomethingFromRequest();
        } else {
            return "empty";
        }
    }
}
