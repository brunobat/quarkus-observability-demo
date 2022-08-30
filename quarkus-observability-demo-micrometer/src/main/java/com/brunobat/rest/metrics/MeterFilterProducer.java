package com.brunobat.rest.metrics;


import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class MeterFilterProducer {

    //    private static AtomicInteger differentValueOnEachCall = new AtomicInteger(0);
    @Inject
    Provider<RequestContext> context;

    @Inject
    BeanManager beanManager;

    @Produces
    @Singleton
    public MeterFilter customMeterFilter() {
//        final String targetMetric = "http.server.requests.seconds.sum";
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                try {
                    Tag newTag = Tag.of("differentValueOnEachCall", String.valueOf(getSomethingFromRequest()));
                    return id.withTag(newTag);
                } catch (ContextNotActiveException e) {
                    return id.withTag(Tag.of("differentValueOnEachCall", "empty"));
                }
            }
        };
    }

    private String getSomethingFromRequest() {
        return CDI.current().select(RequestContext.class).get().getSomethingFromRequest();
    }
}
