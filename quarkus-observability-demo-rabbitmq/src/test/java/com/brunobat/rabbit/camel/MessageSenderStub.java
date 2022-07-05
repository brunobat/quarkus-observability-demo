
package com.brunobat.rabbit.camel;

import com.brunobat.rabbit.data.LegumeItem;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

@Alternative
@Priority(1)
@Singleton
public class MessageSenderStub extends MessageSender {

    public String send(final LegumeItem legumeItem) {
        return "";
    }
}
