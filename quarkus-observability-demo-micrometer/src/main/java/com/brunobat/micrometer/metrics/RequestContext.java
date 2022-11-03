package com.brunobat.micrometer.metrics;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
//import javax.ws.rs.core.Context;

@RequestScoped
public class RequestContext {

    @Inject
    io.vertx.core.http.HttpServerRequest httpServerRequest;

    public String getSomethingFromRequest() {
        return httpServerRequest.uri();
    }
}
