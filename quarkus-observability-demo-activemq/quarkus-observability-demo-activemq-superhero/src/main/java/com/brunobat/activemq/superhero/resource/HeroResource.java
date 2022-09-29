package com.brunobat.activemq.superhero.resource;

import com.brunobat.activemq.superhero.data.HeroItem;
import com.brunobat.activemq.superhero.model.Hero;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class HeroResource implements HeroApi {

    @Inject
    EntityManager manager;

    public List<HeroItem> list() {

        return manager.createQuery("SELECT h FROM Hero h", Hero.class).getResultList().stream()
                      .map(h -> HeroItem.builder()
                                        .name(h.getName())
                                        .id(h.getId())
                                        .capeType(h.getCapeType())
                                        .build())
                      .collect(toList());
    }
}
