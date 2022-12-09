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


    public List<CollectorEntity> getAllCollectors(int page, int size) {
        return this.collectorsRepository.findAll(PageRequest.of(page, size)).toList();
    }

    public CollectorEntity getCollectorByEmail(String email) {
        var collector = this.collectorsRepository.findByEmail(email);
        return collector.orElse(null);
    }

    public CollectorEntity findByUsername(String username) {
        var collector = this.collectorsRepository.findByUsername(username);
        return collector.orElse(null);
    }

    public CollectionEntity getPublicCollectorCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollection.isPresent()) {
                var collection = collectorCollection.get().getCollection();
                if (collection.isPublic()) {
                    return collection;
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not public");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found for this collector");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }

    public List<CollectionEntity> getPersonalCollections(Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName());
        if (collector.isPresent()) {
            var collectionList = this.collectorCollectionRepository.getCollectionsByCollectorId(collector.get().getId());
            return collectionList.stream().map(CollectorCollectionEntity::getCollection).toList();
        }
        return List.of();
    }


    public void addCollectionInFavouritesList(String collectorEmail, FavouritePayload favouritePayload) {
        var collector = collectorsRepository.findByEmail(collectorEmail).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var collection = collectionsRepository.findById(favouritePayload.getCollectionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
        );
         if (collection.isPublic() || isCollectorInCollectionShareList(collector, collection)) {
            //se la collezione non e' privata e non e' gia nella lista oppure se sono nella lista di condivisione
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

    private boolean isCollectorInCollectionShareList (CollectorEntity collector, CollectionEntity collection) {
        return collection.getCollectionsCollectors().stream()
                .anyMatch(cc -> cc.getCollector().getId().equals(collector.getId()));
    }

    public List<CollectionEntity> getFavouritesCollections(Authentication authentication) {
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
