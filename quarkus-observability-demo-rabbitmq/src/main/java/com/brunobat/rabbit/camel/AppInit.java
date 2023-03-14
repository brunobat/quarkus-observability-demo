

package com.brunobat.rabbit.camel;

import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class AppInit {

    private static final Logger log = LoggerFactory.getLogger(AppInit.class);

    @Inject
    private CamelRoutesSetup routesSetup;

    public void onStartup(@Observes final StartupEvent ev) throws Exception {
        log.info("Initializing camel routes.");
        routesSetup.setup();
        routesSetup.start();
    }

}
