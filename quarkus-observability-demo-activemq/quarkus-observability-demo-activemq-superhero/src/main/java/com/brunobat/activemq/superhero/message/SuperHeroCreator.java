package com.brunobat.activemq.superhero.message;

import com.brunobat.activemq.superhero.model.CapeType;
import com.brunobat.activemq.superhero.model.Hero;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingSpanNameExtractor;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.opentelemetry.instrumentation.api.instrumenter.messaging.MessageOperation.PROCESS;
import static io.quarkus.opentelemetry.runtime.config.OpenTelemetryConfig.INSTRUMENTATION_NAME;
import static javax.transaction.Transactional.TxType.REQUIRED;

@ApplicationScoped
@Slf4j
public class SuperHeroCreator implements Runnable {

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    @Inject
    private EntityManager entityManager;

    @Inject
    ConnectionFactory connectionFactory;
    @Inject
    OpenTelemetry telemetry;

    private static Instrumenter<Message, Message> getConsumerInstrumenter(final OpenTelemetry openTelemetry) {
        InstrumenterBuilder<Message, Message> serverInstrumenterBuilder = Instrumenter.builder(
                openTelemetry,
                INSTRUMENTATION_NAME,
                // The extractor gets the name and operation for the spans
                MessagingSpanNameExtractor.create(
                        //How to obtain data from the JMS message
                        JmsAttributesGetter.INSTANCE,
                        // We are receiving and processing the message
                        PROCESS));

        Instrumenter<Message, Message> messageInstrumenter = serverInstrumenterBuilder
                // extracts attribute data from the message and
                // sets the span attributes according to the semantic conventions
                .addAttributesExtractor(MessagingAttributesExtractor.create(
                        //How to obtain data from the JMS message
                        JmsAttributesGetter.INSTANCE,
                        // We are receiving and processing the message
                        PROCESS))
                .buildConsumerInstrumenter(new TextMapGetter<Message>() {
                    // Teach the instrumenter how to get attributes on the message.
                    // The context propagation uses the message metadata

                    @Override
                    public Iterable<String> keys(final Message carrier) {
                        try {
                            return Collections.list(carrier.getPropertyNames());
                        } catch (JMSException e) {
                            return Collections.emptyList();
                        }
                    }

                    @Nullable
                    @Override
                    public String get(@Nullable final Message carrier, final String key) {
                        Object value;
                        try {
                            value = carrier.getObjectProperty(key);
                        } catch (JMSException e) {
                            throw new IllegalStateException(e);
                        }
                        if (value instanceof String) {
                            return (String) value;
                        } else {
                            log.warn("fail to get property from message: {}", key);
                            return null;
                        }
                    }
                });
        return messageInstrumenter;
    }

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        Context parentOtelContext = Context.current();
        Instrumenter<Message, Message> consumerInstrumenter = getConsumerInstrumenter(telemetry);

        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("heroes"));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    // receive returns `null` if the JMSConsumer is closed
                    return;
                }
                Context spanContext = consumerInstrumenter
                        .start(parentOtelContext, message);

                String legumeName = message.getBody(String.class);
                add(legumeName);
                log.info("Received a Legume: " + legumeName);

                consumerInstrumenter
                        .end(spanContext, message, null, null);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(REQUIRED)
    Hero add(final String legumeName) {
        final Hero hero = Hero.builder()
                .name("SUPER-" + legumeName)
                .capeType(CapeType.SUPERMAN)
                .build();

        final Hero createdHero = entityManager.merge(hero);
        log.info("hero created: {}", createdHero);
        return createdHero;
    }
}
