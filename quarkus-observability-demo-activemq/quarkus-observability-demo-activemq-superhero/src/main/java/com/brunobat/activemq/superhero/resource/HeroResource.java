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

    public List<HeroItem> list(final String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return manager.createQuery("SELECT h FROM Hero h", Hero.class).getResultList().stream()
                    .map(this::getHeroItem)
                    .collect(toList());
        }else {
            return findByOriginalName(originalName);
        }
    }

    private List<HeroItem> findByOriginalName(final String originalName) {
        return manager.createQuery("SELECT h FROM Hero h WHERE h.originalName = :originalName", Hero.class)
                .setParameter("originalName", originalName).getResultList().stream()
                .map(this::getHeroItem)
                .collect(toList());
    }

    private HeroItem getHeroItem(Hero h) {
        return HeroItem.builder()
                .name(h.getName())
                .id(h.getId())
                .capeType(h.getCapeType())
                .build();
    }
}
