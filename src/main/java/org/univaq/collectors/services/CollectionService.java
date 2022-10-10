package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

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

    // public List<CollectionEntity> getAll(int page, int size, Optional<String> optionalname) {
    //         return optionalname
    //         .map(name -> this.collectionsRepository.findByName(name))
    //         .map(collectorOptional -> collectorOptional
    //             .map(collector -> List.of(collector))
    //             .orElseGet(() -> List.of())
    //         )
    //         .orElseGet(() -> this.collectionsRepository.findAll(PageRequest.of(page, size)).toList());
    //     }
    

    public List<CollectionEntity> getCollectionByCollectorId(Long collectorId) {
        return this.collectorCollectionRepository.getCollectionByCollectorId(collectorId).stream()
                .map(CollectorCollectionEntity::getCollections)
                .toList();
    }

    public Optional<CollectionEntity> saveCollectorCollection(CollectionEntity collection, Long collectorId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if(optionalCollector.isPresent()) {

            var collector = optionalCollector.get();

            var savedCollection = this.collectionsRepository.save(collection);

            savedCollection.addCollectorCollection(collector);

            this.collectionsRepository.flush();

            return Optional.of(savedCollection);
        }

        return Optional.empty();
    }

    public Optional<CollectionEntity> getCollectorCollectionById(Long collectorId, Long collectionId) {
        return this.collectorCollectionRepository.getCollectionByCollectorId(collectorId).stream()
                .map(CollectorCollectionEntity::getCollections)
                .filter(collection -> collection.getId().equals(collectionId))
                .findFirst();
    }

}

