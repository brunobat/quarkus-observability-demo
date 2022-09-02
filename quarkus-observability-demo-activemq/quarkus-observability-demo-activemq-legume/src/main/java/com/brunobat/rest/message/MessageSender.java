package com.brunobat.rest.message;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;

@ApplicationScoped
@Slf4j
public class MessageSender {
    @Inject
    ConnectionFactory connectionFactory;

    public void send(String str) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue("heroes"), str);
            log.info("sent message: " + str);
        }
    }
}
