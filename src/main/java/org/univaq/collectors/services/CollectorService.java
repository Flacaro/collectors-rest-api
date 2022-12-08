package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;


import org.univaq.collectors.models.CollectorEntity;

@Service
public class CollectorService {
    
    private final CollectorsRepository collectorsRepository;
    private final CollectorCollectionRepository collectorCollectionRepository;

    private final CollectionsRepository collectionsRepository;

    public CollectorService(
            CollectorsRepository collectorsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectionsRepository collectionsRepository
    ) {
        this.collectorsRepository = collectorsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectionsRepository = collectionsRepository;
    }


    public List<CollectorEntity> getAll(int page, int size, Optional<String> optionalEmail) {
        return optionalEmail
        .map(this.collectorsRepository::findByEmail)
        .map(collectorOptional -> collectorOptional
            .map(List::of)
            .orElseGet(List::of)
        )
        .orElseGet(() -> this.collectorsRepository.findAll(PageRequest.of(page, size)).toList());
    }

    public CollectorEntity getCollectorByEmail(String email) {
        var collector = this.collectorsRepository.findByEmail(email);
        return collector.orElse(null);
    }

    public CollectorEntity findByUsername(String username) {
        var collector = this.collectorsRepository.findByUsername(username);
        return collector.orElse(null);
    }

    public List<CollectionEntity> getPersonalCollections(Long collectorId, Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName());
        if (collector.isPresent()) {
            var collectionList = this.collectorCollectionRepository.getCollectionsByCollectorId(collectorId);
            return collectionList.stream().map(CollectorCollectionEntity::getCollection).toList();
        }
        return List.of();
    }


    public boolean isCollectorOwnerOfCollection(CollectorEntity collector, CollectionEntity collection) {
        return collection.getCollectionsCollectors().stream()
                .filter(cc -> cc.getCollector().getId().equals(collector.getId()))
                .findFirst()
                .map(CollectorCollectionEntity::isOwner)
                .orElse(false);
    }


    public void addCollectionInFavouritesList(String collectorEmail, FavouritePayload favouritePayload) {
        var collector = collectorsRepository.findByEmail(collectorEmail).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var collection = collectionsRepository.findById(favouritePayload.getCollectionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
        );
        // controllo se il collezionista e' l'owner della collection
//        var isCollectorOwner = this.isCollectorOwnerOfCollection(collector, collection);
//        if (isCollectorOwner) {
//            //controllo se nella lista dei preferiti gia' c'e' la collection che voglio aggiungere
//            var isCollectionInFavourites = this.isCollectionInFavourites(collector, collection);
//            if (!isCollectionInFavourites) {
//                collector.addCollectionToFavourites(collection);
//                collectorsRepository.save(collector);
//            } else {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection already in favourites");
//            }
         if (collection.isPublic()) {
            //se la collezione non e' privata e non sono l'owner della collection e non e' gia nella lista
            //aggiungo la collezione ai preferiti
            var isCollectionInFavourites = this.isCollectionInFavourites(collector, collection);

            if (!isCollectionInFavourites) {
                collector.addCollectionToFavourites(collection);
                collectorsRepository.save(collector);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection already in favourites");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection and it is not public");
        }
    }

    private boolean isCollectionInFavourites (CollectorEntity collector, CollectionEntity collection) {
        return collector.getFavourites().stream()
                .anyMatch(c -> c.getId().equals(collection.getId()));
    }

    public List<CollectionEntity> getFavourites(Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var favourites = collector.getFavourites();
        return favourites.stream()
                .map(f -> f.getCollectionsCollectors().stream()
                        .map(CollectorCollectionEntity::getCollection)
                        .findFirst()
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
                        )
                )
                .toList();
    }



}
