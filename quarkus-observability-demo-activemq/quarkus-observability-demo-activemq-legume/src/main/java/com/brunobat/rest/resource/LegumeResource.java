package com.brunobat.rest.resource;


import com.brunobat.rest.LegumeRepository;
import com.brunobat.rest.data.LegumeItem;
import com.brunobat.rest.data.LegumeNew;
import com.brunobat.rest.message.MessageSender;
import com.brunobat.rest.model.Legume;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
@Slf4j
public class LegumeResource implements LegumeApi {

    @Inject
    LegumeRepository repository;

    @Inject
    MessageSender messageSender;

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
                repository.remove(legume);
                return Response.status(NO_CONTENT).build();
            })
            .orElse(Response.status(NOT_FOUND).build());
    }

    public List<LegumeItem> list(int pageIndex) {
        log.info("someone asked for a list for index: " + pageIndex);
        return repository.listLegumes(pageIndex)
                         .map(this::getLegumeItem)
                         .collect(Collectors.toList());
    }

    private Optional<LegumeItem> find(final String legumeId) {
        return repository.find("id", legumeId).stream()
                         .map(this::getLegumeItem)
                         .findFirst();
    }

    private LegumeItem addLegume(final @Valid LegumeNew legumeNew) {
        final Legume legumeToAdd = Legume.builder()
                                         .name(legumeNew.getName())
                                         .description((legumeNew.getDescription()))
                                         .build();

        final Legume addedLegume = repository.merge(legumeToAdd);
        final LegumeItem legumeItem = getLegumeItem(addedLegume);

        messageSender.send(legumeItem.getName());

        return legumeItem;
    }

    private LegumeItem getLegumeItem(final Legume addedLegume) {
        return LegumeItem.builder()
                         .id(addedLegume.getId())
                         .name(addedLegume.getName())
                         .description(addedLegume.getDescription())
                         .build();
    }
}
