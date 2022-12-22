package org.univaq.collectors.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.controllers.requests.payload.FavouriteDiskPayload;
import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;


import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.repositories.DisksRepository;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Service
public class CollectorService {

    private final CollectorsRepository collectorsRepository;
    private final CollectorCollectionRepository collectorCollectionRepository;

    private final CollectionsRepository collectionsRepository;
    private final DisksRepository disksRepository;

    public CollectorService(
            CollectorsRepository collectorsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectionsRepository collectionsRepository,
            DisksRepository disksRepository
    ) {
        this.collectorsRepository = collectorsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectionsRepository = collectionsRepository;
        this.disksRepository = disksRepository;
    }


    public Optional<List<CollectorEntity>> getAllCollectors(int page, int size) {
        return Optional.of(collectorsRepository.findAll(PageRequest.of(page, size)).getContent());
    }

    public Optional<CollectorEntity> getCollectorByEmail(String email) {
        return this.collectorsRepository.findByEmail(email);
    }


    public Optional<CollectorEntity> getById(Long collectorId) {
        return this.collectorsRepository.findById(collectorId);
    }

    public List<CollectorEntity> getCollectorsByParameters(String email, String username) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("email", contains().ignoreCase())
                .withMatcher("username", contains().ignoreCase());

        CollectorEntity example = new CollectorEntity();
        example.setEmail(email);
        example.setUsername(username);

        return this.collectorsRepository.findAll(Example.of(example, matcher));
    }


    public List<CollectionEntity> getPersonalCollections(Authentication authentication, Integer page, Integer size) {
        var collector = collectorsRepository.findByEmail(authentication.getName());
        if (collector.isPresent()) {
            var collectionList = this.collectorCollectionRepository.findCollectionsByCollectorId(collector.get().getId(), PageRequest.of(page, size));
            if (collectionList.isPresent()) {
                var personalCollections = new ArrayList<CollectionEntity>();
                for (CollectorCollectionEntity collectorCollection : collectionList.get()) {
                    personalCollections.add(collectorCollection.getCollection());
                }
                return personalCollections;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No personal collections found");

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }


    public void addCollectionInFavouritesList(String collectorEmail, FavouritePayload favouritePayload) {
        var collector = collectorsRepository.findByEmail(collectorEmail).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var collection = collectionsRepository.findById(favouritePayload.getCollectionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
        );
        if (collection.isVisible() || isCollectorInCollectionShareList(collector, collection)) {
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

    private boolean isCollectionInFavourites(CollectorEntity collector, CollectionEntity collection) {
        return collector.getFavourites().stream()
                .anyMatch(c -> c.getId().equals(collection.getId()));
    }

    private boolean isCollectorInCollectionShareList(CollectorEntity collector, CollectionEntity collection) {
        return collection.getCollectionsCollectors().stream()
                .anyMatch(cc -> cc.getCollector().getId().equals(collector.getId()));
    }

    public Optional<List<CollectionEntity>> getFavouritesCollections(Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var favourites = collector.getFavourites();
        if (favourites.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No favourites collections found");
        }
        return Optional.of(favourites);

    }

    public void addDiskInFavouritesDiskList(String collectorEmail, Long collectionId, FavouriteDiskPayload favouriteDiskPayload) {
        var collector = collectorsRepository.findByEmail(collectorEmail).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var collection = collectionsRepository.findById(collectionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
        );
        if (collection.isVisible() || isCollectorInCollectionShareList(collector, collection)) {
            var disk = disksRepository.findById(favouriteDiskPayload.getDiskId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not Found")
            );
            //aggiungo disk ai preferiti
            var isDiskInFavourites = this.isDiskInFavourites(collector, disk);

            if (!isDiskInFavourites) {
                collector.addDiskToFavourites(disk);
                collectorsRepository.save(collector);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disk already in favourites");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection and it is not public");
        }
    }

    private boolean isDiskInFavourites(CollectorEntity collector, DiskEntity disk) {
        return collector.getFavouritesDisk().stream()
                .anyMatch(c -> c.getId().equals(disk.getId()));
    }

    public Optional<List<DiskEntity>> getFavouritesDisks(Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );
        var favouritesDisk = collector.getFavouritesDisk();
        if (favouritesDisk.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No favourites disks found");
        }
        return Optional.of(favouritesDisk);

    }

}