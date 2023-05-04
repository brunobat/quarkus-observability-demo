package com.brunobat.activemq.superhero.resource;

import com.brunobat.activemq.superhero.data.HeroItem;
import com.brunobat.activemq.superhero.model.Hero;
import com.brunobat.activemq.superhero.repository.HeroRepository;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Slf4j
public class HeroResource implements HeroApi {

    @Inject
    HeroRepository repository;

    public List<HeroItem> list(final String originalName, int pageIndex) {
        if (originalName == null || originalName.isBlank()) {
            log.info("someone asked for a list for index: " + pageIndex);
            return repository.listHeroes(pageIndex).stream()
                    .map(this::getHeroItem)
                    .collect(toList());
        }else {
            return findByOriginalName(originalName);
        }
    }

    private List<HeroItem> findByOriginalName(final String originalName) {
        return repository.findByOriginalName(originalName).stream()
                .map(this::getHeroItem)
                .collect(toList());
    }

    private HeroItem getHeroItem(Hero h) {
        return HeroItem.builder()
                .name(h.getName())
                .originalName(h.getOriginalName())
                .id(h.getId())
                .capeType(h.getCapeType())
                .build();
    }
}
