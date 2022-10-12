package org.univaq.collectors.services;

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

    public List<CollectionEntity> getAll(int page, int size, Optional<String> optionalname) {
        return optionalname
                .map(name -> this.collectionsRepository.findByName(name))
                .map(collectionOptional -> collectionOptional
                        .map(collection -> List.of(collection))
                        .orElseGet(() -> List.of())
                )
                .orElseGet(() -> this.collectionsRepository.findAll(PageRequest.of(page, size)).toList());
    }


    public List<CollectionEntity> getCollectionsByCollectorId(Long collectorId) {
        return this.collectorCollectionRepository.getCollectionByCollectorId(collectorId).stream()
                .map(CollectorCollectionEntity::getCollection)
                .toList();
    }

    public Optional<CollectionEntity> getCollectorCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var optionalCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (optionalCollection.isPresent() && optionalCollection.get().getCollection().getId().equals(collectionId)) {
                return this.collectionsRepository.findById(collectionId);
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

    //Stream filter(Predicate predicate) restituisce un flusso costituito dagli elementi di questo flusso
    // che corrispondono al predicato specificato.
//    public Optional<CollectionEntity> getCollectorCollectionById(Long collectorId, Long collectionId) {
//        return this.collectorCollectionRepository.getCollectionByCollectorId(collectorId).stream()
//                .map(CollectorCollectionEntity::getCollection)
//                .filter(collection -> collection.getId().equals(collectionId))
//                .findFirst();
//    }

    public void deleteCollectorCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
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
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent() && collectorCollectionOptional.get().getCollection().getId().equals(collectionId)) {
                var collectionToUpdate = this.collectionsRepository.findById(collection.getId()).get();
                collectionToUpdate.updateCollectorCollection(collection.getName(), collection.getStatus());
                return Optional.of(this.collectionsRepository.save(collectionToUpdate));

            }
        }
        return Optional.empty();
    }



}




