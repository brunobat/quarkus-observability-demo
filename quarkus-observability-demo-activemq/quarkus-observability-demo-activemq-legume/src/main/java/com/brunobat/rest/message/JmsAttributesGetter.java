package com.brunobat.rest.message;

import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesGetter;

import javax.annotation.Nullable;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

enum JmsAttributesGetter implements MessagingAttributesGetter<Message, Message> {
    INSTANCE;

    @Nullable
    @Override
    public String system(Message message) {
        return "jms";
    }

    @Nullable
    @Override
    public String destinationKind(Message message) {
        Destination jmsDestination = null;
        try {
            jmsDestination = message.getJMSDestination();
        } catch (Exception ignored) {
            // Ignore
        }

        if (jmsDestination == null) {
            return null;
        }

        if (jmsDestination instanceof Queue) {
            return "queue";
        }
        if (jmsDestination instanceof Topic) {
            return "topic";
        }
        return null;
    }

    @Nullable
    @Override
    public String destination(Message message) {
        Destination jmsDestination = null;
        try {
            jmsDestination = message.getJMSDestination();
        } catch (Exception ignored) {
            // Ignore
        }

        if (jmsDestination == null) {
            return null;
        }

        if (jmsDestination instanceof Queue) {
            try {
                return ((Queue) jmsDestination).getQueueName();
            } catch (JMSException e) {
                return "unknown";
            }
        }
        if (jmsDestination instanceof Topic) {
            try {
                return ((Topic) jmsDestination).getTopicName();
            } catch (JMSException e) {
                return "unknown";
            }
        }
        return null;
    }

    @Override
    public boolean temporaryDestination(Message message) {
        return false;
    }

    @Nullable
    @Override
    public String protocol(Message message) {
        return null;
    }

    @Nullable
    @Override
    public String protocolVersion(Message message) {
        return null;
    }

    @Nullable
    @Override
    public String url(Message message) {
        return null;
    }

    @Nullable
    @Override
    public String conversationId(Message message) {
        try {
            return message.getJMSCorrelationID();
        } catch (JMSException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public Long messagePayloadSize(Message message) {
        return null;
    }

    @Nullable
    @Override
    public Long messagePayloadCompressedSize(Message message) {
        return null;
    }

    @Nullable
    @Override
    public String messageId(Message message, @Nullable Message message2) {
        return null;
    }
}
