package com.brunobat.micrometer.metrics;


import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

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
