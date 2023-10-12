package com.brunobat.rest;

import com.brunobat.rest.model.Legume;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LegumeRepository implements PanacheRepository<Legume> {

    @WithSession
    public Uni<List<Legume>> listLegumes(int pageIndex) {
        return find("SELECT h FROM Legume h").page(pageIndex, 16).list();
    }

    public void removeLegume(final String legumeId) {
        find("SELECT h FROM Legume h WHERE h.id=?1", legumeId).firstResult().onItem().transform(legume -> {
            delete(legume);
            return legume;
        });
    }

    public Uni<Legume> createLegume(final Legume legumeToAdd) {
        return persist(legumeToAdd);
    }
}
