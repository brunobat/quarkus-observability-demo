package com.brunobat.opentracing.resource;

import com.brunobat.opentracing.data.LegumeItem;
import com.brunobat.opentracing.data.LegumeNew;
import com.brunobat.opentracing.model.Legume;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
@Slf4j
public class LegumeResource implements LegumeApi {

    @Inject
    EntityManager manager;


    @Inject
    Tracer otelTracer;

    @Transactional
    public Response provision() {
        final LegumeNew carrot = LegumeNew.builder()
                .name("Carrot")
                .description("Root vegetable, usually orange")
                .build();
        final LegumeNew zucchini = LegumeNew.builder()
                .name("Zucchini")
                .description("Summer squash")
                .build();
        return Response.status(CREATED).entity(asList(
                addLegume(carrot),
                addLegume(zucchini))).build();
    }

    @Transactional
    public Response add(@Valid final LegumeNew legumeNew) {
        return Response.status(CREATED).entity(addLegume(legumeNew)).build();
    }

    @Transactional
    public Response delete(@NotEmpty final String legumeId) {
        return find(legumeId)
                .map(legume -> {
                    manager.remove(legume);
                    return Response.status(NO_CONTENT).build();
                })
                .orElse(Response.status(NOT_FOUND).build());
    }

    @WithSpan("From Otel @WithSpan Annotation - Get Legumes list")
    public List<LegumeItem> list() {
        log.info("someone asked for a list");

        Span incomingSpan = Span.current();
        incomingSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, LegumeResource.class.getName());

        Span dbSpan = otelTracer.spanBuilder("Manual span - Go to DB and get the data").startSpan();
        try (Scope scope = dbSpan.makeCurrent()) {
            return manager.createQuery("SELECT l FROM Legume l").getResultList();
        } catch (Exception e) {
            dbSpan.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            dbSpan.end();
        }
    }

    private Optional<LegumeItem> find(final String legumeId) {
        return Optional.ofNullable(manager.find(Legume.class, legumeId))
                .map(legume -> LegumeItem.builder()
                        .id(legume.getId())
                        .name(legume.getName())
                        .description(legume.getDescription())
                        .build());
    }

    private LegumeItem addLegume(final @Valid LegumeNew legumeNew) {
        final Legume legumeToAdd = Legume.builder()
                .name(legumeNew.getName())
                .description((legumeNew.getDescription()))
                .build();

        final Legume addedLegume = manager.merge(legumeToAdd);

        final LegumeItem legumeItem = LegumeItem.builder()
                .id(addedLegume.getId())
                .name(addedLegume.getName())
                .description(addedLegume.getDescription())
                .build();
        log.info("created " + legumeItem);
        return legumeItem;
    }
}
