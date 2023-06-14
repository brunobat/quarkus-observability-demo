package com.brunobat.full.client;

import com.brunobat.full.data.LegumeItem;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;


@Path("/legumes")
@RegisterRestClient()
@RegisterProvider(MeterProvider.class)
public interface FooClient {

    @GET
    List<LegumeItem> findFoo();
}