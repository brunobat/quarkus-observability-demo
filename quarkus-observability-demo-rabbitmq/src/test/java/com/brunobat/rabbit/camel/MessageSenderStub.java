
package com.brunobat.rabbit.camel;

import com.brunobat.rabbit.data.LegumeItem;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;

@Alternative
@Priority(1)
@Singleton
public class MessageSenderStub extends MessageSender {

    public String send(final LegumeItem legumeItem) {
        return "";
    }
}
