package com.brunobat.activemq.superhero.repository;

import com.brunobat.activemq.superhero.model.Hero;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

}
