package com.brunobat.full.superhero.resource;

import com.brunobat.full.superhero.data.HeroItem;
import com.brunobat.full.superhero.model.CapeType;
import com.brunobat.full.superhero.model.Hero;
import com.brunobat.full.superhero.repository.HeroRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.ws.rs.core.Response.Status.CREATED;
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

    @Transactional(REQUIRED)
    public Response add(final String legumeName) {
        final Hero hero = Hero.builder()
                .name("SUPER-" + legumeName)
                .originalName(legumeName)
                .capeType(CapeType.SUPERMAN)
                .build();

        final Hero createdHero = repository.create(hero);
        log.info("hero created: {}", createdHero);

        return Response.status(CREATED)
                .entity(HeroItem.builder()
                        .id(createdHero.getId())
                        .name(createdHero.getName())
                        .originalName(createdHero.getOriginalName())
                        .capeType(createdHero.getCapeType())
                        .build())
                .build();
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
