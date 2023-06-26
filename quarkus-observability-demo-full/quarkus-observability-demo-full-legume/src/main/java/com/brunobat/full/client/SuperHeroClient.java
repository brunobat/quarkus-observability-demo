package com.brunobat.full.client;

import com.brunobat.full.data.LegumeItem;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;


@Path("/heroes")
@RegisterRestClient()
@RegisterProvider(MeterProvider.class)
public interface SuperHeroClient {

    @POST
    @Path("/legume")
    @Consumes(TEXT_PLAIN)
    void notifyAdd(String legumeName);
}