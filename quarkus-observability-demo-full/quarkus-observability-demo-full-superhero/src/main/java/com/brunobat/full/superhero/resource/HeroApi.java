package com.brunobat.full.superhero.resource;

import com.brunobat.full.superhero.data.HeroItem;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

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


    @POST
    @Path("/legume")
    @Operation(
            operationId = "AddSuperhero",
            summary = "Add a Superhero"
    )
    @RequestBody(
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = String.class)),
            description = "The Superhero original name",
            required = true
    )
    @APIResponse(
            responseCode = "201",
            description = "Superhero created",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = HeroItem.class, ref = "hero_item"))
    )
    @APIResponse(
            name = "notFound",
            responseCode = "400",
            description = "Superhero data is invalid"
    )
    @APIResponse(
            name = "notFound",
            responseCode = "404",
            description = "Superhero provision not found"
    )
    @APIResponse(
            name = "internalError",
            responseCode = "500",
            description = "Internal Server Error"
    )
    @Consumes(TEXT_PLAIN)
    public Response add(final String legume);

}
