package com.brunobat.micrometer.client;

import com.brunobat.micrometer.data.LegumeItem;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;


@Path("/legumes")
@RegisterRestClient()
@RegisterProvider(MeterProvider.class)
public interface FooClient {

    @GET
    List<LegumeItem> findFoo();
}