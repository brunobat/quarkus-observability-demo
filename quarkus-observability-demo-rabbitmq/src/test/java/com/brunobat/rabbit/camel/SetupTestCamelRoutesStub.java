

package com.brunobat.rabbit.camel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;

@Alternative
@Priority(1)
@Singleton
public class SetupTestCamelRoutesStub extends CamelRoutesSetup {

    private static final Logger log = LoggerFactory.getLogger(SetupTestCamelRoutesStub.class);

    @Override
    protected boolean autoStartRoutes() {
        return false;
    }

    @Override
    public void start() {
        log.info("RabbitMQ route disabled tests");
    }
}
