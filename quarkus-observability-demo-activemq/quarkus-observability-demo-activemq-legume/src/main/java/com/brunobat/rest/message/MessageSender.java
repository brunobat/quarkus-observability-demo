package com.brunobat.rest.message;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingSpanNameExtractor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;

import static io.opentelemetry.instrumentation.api.instrumenter.messaging.MessageOperation.SEND;
import static io.quarkus.opentelemetry.runtime.config.OpenTelemetryConfig.INSTRUMENTATION_NAME;

@ApplicationScoped
@Slf4j
public class MessageSender {
    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    OpenTelemetry telemetry;

    private static Instrumenter<Message, Message> getProducerInstrumenter(final OpenTelemetry openTelemetry) {
        InstrumenterBuilder<Message, Message> serverInstrumenterBuilder = Instrumenter.builder(
                openTelemetry,
                INSTRUMENTATION_NAME,
                // The extractor gets the name and operation for the spans
                MessagingSpanNameExtractor.create(
                        //How to obtain data from the JMS message
                        JmsAttributesGetter.INSTANCE,
                        // We are sending data away
                        SEND));

        Instrumenter<Message, Message> messageInstrumenter = serverInstrumenterBuilder
                // extracts attribute data from the message and
                // sets the span attributes according to the semantic conventions
                .addAttributesExtractor(MessagingAttributesExtractor.create(
                        //How to obtain data from the JMS message
                        JmsAttributesGetter.INSTANCE,
                        // We are sending data away
                        SEND))
                .buildProducerInstrumenter((message, key, value) -> {
                    // Teach the instrumenter how to set attributes on the message.
                    // For context propagation using message metadata
                    if (message != null) {
                        try {
                            message.setObjectProperty(key, value);
                        } catch (JMSException e) {
                            // We don't want to abort because of this
                            log.warn("fail to ser property on message: {}", key, e);
                        }
                    }
                });
        return messageInstrumenter;
    }

    public void send(String str) {
        Context parentOtelContext = Context.current();
        Instrumenter<Message, Message> producerInstrumenter = getProducerInstrumenter(telemetry);

        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            Message msg = jmsContext.createTextMessage(str);
            Context spanContext = producerInstrumenter
                    .start(parentOtelContext, msg);
            jmsContext.createProducer()
                    .send(jmsContext.createQueue("heroes"), msg);
            log.info("sent message: " + str);
            producerInstrumenter.end(spanContext,msg,null, null);
        }
    }
}
