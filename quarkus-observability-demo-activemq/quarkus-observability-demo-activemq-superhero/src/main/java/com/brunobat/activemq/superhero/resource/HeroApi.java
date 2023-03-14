package com.brunobat.activemq.superhero.resource;

import com.brunobat.activemq.superhero.data.HeroItem;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/heroes")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface HeroApi {

    @Operation(
        operationId = "ListLegumes",
        summary = "List all super heroes"
    )
    @APIResponse(
        responseCode = "200",
        description = "The List with all super heroes"
    )
    @APIResponse(
        name = "notFound",
        responseCode = "404",
        description = "super heroes list not found"
    )
    @APIResponse(
        name = "internalError",
        responseCode = "500",
        description = "Internal Server Error"
    )
    @GET
    List<HeroItem> list(
            @Parameter(name = "legumeName", required = false)
            @QueryParam("legumeName")
            final String legumeName,
            @Parameter(name = "pageIndex", required = false)
            @QueryParam("pageIndex")
            final int pageIndex);
}
