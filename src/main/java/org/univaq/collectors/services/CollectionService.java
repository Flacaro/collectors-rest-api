package org.univaq.collectors.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;

import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;

@Service
public class CollectionService {


    private final CollectionsRepository collectionsRepository;

    private final CollectorsRepository collectorsRepository;

    private final CollectorCollectionRepository collectorCollectionRepository;

    public CollectionService(
            CollectionsRepository collectionsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectorsRepository collectorsRepository
    ) {
        this.collectionsRepository = collectionsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectorsRepository = collectorsRepository;
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
    public Optional<CollectionEntity> getCollectorCollectionById( Long collectorId, Long collectionId) {
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



}




