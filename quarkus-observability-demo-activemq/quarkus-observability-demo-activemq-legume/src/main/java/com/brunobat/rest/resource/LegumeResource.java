package com.brunobat.rest.resource;


import com.brunobat.rest.LegumeRepository;
import com.brunobat.rest.data.LegumeItem;
import com.brunobat.rest.data.LegumeNew;
import com.brunobat.rest.message.MessageSender;
import com.brunobat.rest.model.Legume;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
//@Slf4j
public class LegumeResource implements LegumeApi {

    @Inject
    LegumeRepository repository;

    @Inject
    MessageSender messageSender;

    @WithTransaction
    public Uni<Response> provision() {
        final LegumeNew carrot = LegumeNew.builder()
                .name("Carrot")
                .description("Root vegetable, usually orange")
                .build();
        final LegumeNew zucchini = LegumeNew.builder()
                .name("Zucchini")
                .description("Summer squash")
                .build();

        return Uni.combine().all().unis(
                        addLegume(carrot),
                        addLegume(zucchini))
                .asTuple()
                .onItem().transform(tuple -> {
                    List<LegumeItem> resultList = new ArrayList<>();
                    resultList.add(tuple.getItem1());
                    resultList.add(tuple.getItem2());
                    return Response.status(CREATED).entity(resultList).build();
                });
    }

    @WithTransaction
    public Uni<Response> add(@Valid final LegumeNew legumeNew) {
        return addLegume(legumeNew)
                .onItem().transform(legumeItem -> Response
                        .status(CREATED)
                        .entity(legumeItem)
                        .build());
    }

    @WithTransaction
    public Uni<Response> delete(@NotEmpty final String legumeId) {
        return find(legumeId)
                .onItem().transform(legumeOpt -> legumeOpt
                        .map(legume -> {
                            repository.removeLegume(legume.getId());
                            return Response.status(NO_CONTENT).build();
                        })
                        .orElse(Response.status(NOT_FOUND).build()));
    }

    public Uni<List<LegumeItem>> list(int pageIndex) {
//        log.info("someone asked for a list for index: " + pageIndex);
        return repository.listLegumes(pageIndex)
                .onItem().transform(legumes -> legumes.stream()
                        .map(this::getLegumeItem)
                        .collect(Collectors.toList()));
    }

    private Uni<Optional<LegumeItem>> find(final String legumeId) {
        return repository.find("id", legumeId).list()
                .onItem().transform(legumes -> legumes.stream()
                        .map(this::getLegumeItem)
                        .findFirst());
    }

    private Uni<LegumeItem> addLegume(final LegumeNew legumeNew) {
        final Legume legumeToAdd = Legume.builder()
                .name(legumeNew.getName())
                .description((legumeNew.getDescription()))
                .build();

        final Uni<Legume> addedLegume = repository.createLegume(legumeToAdd);

        return addedLegume.onItem().transform(legume -> {
            LegumeItem legumeItem = getLegumeItem(legume);
            messageSender.send(legumeItem.getName());
            return legumeItem;
        });
    }

    private LegumeItem getLegumeItem(final Legume addedLegume) {
        return LegumeItem.builder()
                .id(addedLegume.getId())
                .name(addedLegume.getName())
                .description(addedLegume.getDescription())
                .build();
    }
}
