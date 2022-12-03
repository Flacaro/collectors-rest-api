package org.univaq.collectors.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.repositories.DisksRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {


    private final CollectionsRepository collectionsRepository;

    private final CollectorsRepository collectorsRepository;

    private final CollectorCollectionRepository collectorCollectionRepository;

    private final DisksRepository disksRepository;

    public CollectionService(
            CollectionsRepository collectionsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectorsRepository collectorsRepository,
            DisksRepository disksRepository

    ) {
        this.collectionsRepository = collectionsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectorsRepository = collectorsRepository;
        this.disksRepository = disksRepository;
    }

    //lista collezioni pubbliche
    public List<CollectionEntity> getAll(Optional<String> optionalname) {
        //aggiungere page e size ma come?
        var optionalName = optionalname.orElse("");
        if (optionalName.isEmpty()) {
            return this.collectionsRepository.getPublicCollections();

        } else {
            return this.collectionsRepository.getPublicCollectionsByName();

        }
    }


    //Stream filter(Predicate predicate) restituisce un flusso costituito dagli elementi di questo flusso
    // che corrispondono al predicato specificato.
    public List<CollectionEntity> getPublicCollectionsByCollectorId(Long collectorId) {
        List<CollectionEntity> publicCollections = new ArrayList<>();
        var collector = this.collectorsRepository.findById(collectorId);
        if (collector.isPresent()) {
            var publicCollectorCollections = this.collectorCollectionRepository.getPublicCollectionsByCollectorId(collectorId);
            for (var publicCollectorCollection : publicCollectorCollections) {
                var collectionEntity = this.collectionsRepository.findById(publicCollectorCollection.getCollection().getId());
                if (collectionEntity.isPresent()) {
                    publicCollections.add(collectionEntity.get());
                }
            }

            return publicCollections;
        }
        return List.of();
    }


    public List<CollectionEntity> getPersonalCollections(Long collectorId) {
        var collector = this.collectorsRepository.findById(collectorId);
        if (collector.isPresent()) {
            var collectionList = this.collectorCollectionRepository.getCollectionsByCollectorId(collectorId);
            return collectionList.stream().map(CollectorCollectionEntity::getCollection).toList();
        }
        return List.of();
    }


    //restituisce una collezione specifica del collezinista
    public Optional<CollectionEntity> getCollectorCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                return Optional.of(collectorCollection.getCollection());
            }
        }
        return Optional.empty();
    }


    //controllo se l'id dell'utente e' quello dell'utente loggato
    public Optional<CollectionEntity> saveCollectorCollection(CollectionEntity collection, Long collectorId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {

            var savedCollection = this.collectionsRepository.save(collection);

            savedCollection.addCollectorCollection(optionalCollector.get());

            this.collectionsRepository.flush();

            return Optional.of(savedCollection);
        }

        return Optional.empty();
    }

    public List<DiskEntity> getPublicDisks(Long collectionId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var optionalDisk = this.disksRepository.findDisksFromCollectionId(collectionId);
                if (optionalDisk.isPresent()) {
                    return optionalDisk.get();
                }
            }
        }
        return List.of();
    }


    public void deleteCollectorCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var optionalCollection = this.collectionsRepository.findById(collectionId);
            if (optionalCollection.isPresent()) {
                var collection = optionalCollection.get();
                this.collectionsRepository.delete(collection);
            }
        }
    }

    public Optional<CollectionEntity> updateCollectorCollectionById(Long collectorId, Long collectionId, CollectionEntity collection) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollectionOptional.isPresent() && collectorCollectionOptional.get().getCollection().getId().equals(collectionId)) {
                var collectionToUpdate = this.collectionsRepository.findById(collection.getId()).get();
                collectionToUpdate.updateCollectorCollection(collection.getName(), collection.getStatus(), collection.isPublic());
                return Optional.of(this.collectionsRepository.save(collectionToUpdate));

            }
        }
        return Optional.empty();
    }

    //prendo la lista di id dei collezionisti
    public CollectionEntity shareCollection(List<Long> collectorsIds, Long collectionId) {
        List<CollectorEntity> collectors = new ArrayList<>();

        //lista di id degli utenti con cui voglio condividere la collection
        for (var id : collectorsIds) {
            collectorsRepository.findById(id).ifPresentOrElse(
                    collectors::add,
                    () -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
                    }
            );
        }
        Optional<CollectionEntity> collectionOptional = collectionsRepository.findById(collectionId);

        if (collectionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        }

        var collection = collectionOptional.get();

        //lista delle persone che ora sono nella collection
        var collectorCollection = collection.getCollectors();
        List<CollectorCollectionEntity> collectorsCollection = new ArrayList<>();

        for (var collector : collectors) {
            collectorsCollection.add(
                    new CollectorCollectionEntity(collector, collection, false)
            );
        }
        //concatenare le due liste di collectorCollection
        collectorCollection.addAll(collectorsCollection);
        collection.setCollectors(collectorCollection);

        return collectionsRepository.save(collection);
    }


    /*
        1) La lista deve contenere almeno un collezionista (owner)
        2) Solamente l'owner può aggiungere altri collezionisti
        3) Solamente l'owner può rimuovere altri collezionisti
     */
    public CollectionEntity unshareCollection(
            List<Long> collectorsIds,
            Long collectionId,
            Authentication authentication
    ) {

        var optionalAuthenticateCollector = collectorsRepository.findByEmail(authentication.getName());

        if(optionalAuthenticateCollector.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }

        var authenticateCollector = optionalAuthenticateCollector.get(); //prende l'email del collezionista
        // Punto 3)
        collectorCollectionRepository.hasCollectionAndIsOwner(
                authenticateCollector.getId(),
                collectionId
        ).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection")
        );

        //lista dei collezionisti che voglio rimuovere dalla lista dello share
        List<CollectorEntity> collectorsToRemove = new ArrayList<>();
        for (var id : collectorsIds) {
            var collectorById = collectorsRepository.findById(id);
            collectorById.ifPresentOrElse(
                    collectorsToRemove::add,
                    () -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
                    }
            );
        }

        var optionalCollection = collectionsRepository.findById(collectionId);

        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            //lista dei collezionisti che sono gia nella lista dello share
            var collectorCollectionList = collection.getCollectors();


            collectorCollectionList.removeIf(
                    collectorCollection -> collectorsToRemove.contains(collectorCollection.getCollector())
            );

            // Non bisogna assegnare la nuova lista ma mutare quella esistente!
            return this.collectionsRepository.save(collection);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        }



    }


}




