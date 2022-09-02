package com.brunobat.activemq.superhero.message;

import com.brunobat.activemq.superhero.model.CapeType;
import com.brunobat.activemq.superhero.model.Hero;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.transaction.Transactional.TxType.REQUIRED;

@ApplicationScoped
@Slf4j
public class SuperHeroCreator implements Runnable {

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    @Inject
    private EntityManager entityManager;

    @Inject
    ConnectionFactory connectionFactory;

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("heroes"));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    // receive returns `null` if the JMSConsumer is closed
                    return;
                }
                String legumeName = message.getBody(String.class);
                add(legumeName);
                System.out.println("Received a Legume: " + legumeName);
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
