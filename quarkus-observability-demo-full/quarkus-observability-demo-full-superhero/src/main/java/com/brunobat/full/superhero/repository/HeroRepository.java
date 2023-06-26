package com.brunobat.full.superhero.repository;

import com.brunobat.full.superhero.model.Hero;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class HeroRepository implements PanacheRepository<Hero> {

    @Inject
    EntityManager manager;

    public List<Hero> listHeroes(int pageIndex) {
        return find("SELECT h FROM Hero h").page(pageIndex, 20).list();
    }

    public List<Hero> findByOriginalName(final String originalName) {
        return find("originalName", originalName).list();
    }

    public Hero create(Hero hero) {
        persist(hero);
        return hero;
    }
}
