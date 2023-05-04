
package com.brunobat.rabbit.camel;


import com.brunobat.rabbit.data.LegumeItem;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@ApplicationScoped
public class MessageSender {

    @Inject
    private CamelContext context;

//    @Inject
//    Tracer tracer;
    @Inject
    OpenTelemetry telemetry;

    @WithSpan
    public String send(final LegumeItem legumeItem) {
        try (final ProducerTemplate template = context.createProducerTemplate()) {
            template.sendBodyAndHeaders("direct:rabbitMQ", legumeItem.getName().getBytes(UTF_8), getTraceContext());
            log.info("Message sent: {}", legumeItem.getName());
        } catch (IOException e) {
            log.warn("Error sending message", e);
        }
        return legumeItem.getName();
    }

    private Map<String, Object> getTraceContext() {
        TracerProvider tracerProvider = telemetry.getTracerProvider();

//        Tracer tracer =
//                OpenTelemetry.getTracer("instrumentation-library-name", "1.0.0");
//
//        final Map<String, Object> openTracingContext = new HashMap<>();
//
//        tracer.inject(tracer.activeSpan().context(), Format.Builtin.TEXT_MAP, new TextMap() {
//            @Override
//            public Iterator<Map.Entry<String, String>> iterator() {
//                throw new UnsupportedOperationException("TextMapInjectAdapter should only be used with Tracer.inject()");
//            }
//
//            @Override
//            public void put(final String s, final String s1) {
//                openTracingContext.put(s, s1);
//            }
//        });
//
//        return openTracingContext;
        return Collections.emptyMap();
    }

}
