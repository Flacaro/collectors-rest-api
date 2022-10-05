package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.repositories.CollectionsRepository;

@Service
public class CollectionService {
    
    
    private final CollectionsRepository collectionsRepository;

    public CollectionService(CollectionsRepository collectionsRepository) {
        this.collectionsRepository = collectionsRepository;
    }

    public List<CollectionEntity> getAll(int page, int size, Optional<String> optionalName) {

        return optionalName
        .map(name -> this.collectionsRepository.findByName(name))
        .map(collectionOptional -> collectionOptional
            .map(collection -> List.of(collection))
            .orElseGet(() -> List.of())
        )
        .orElseGet(() -> this.collectionsRepository.findAll(PageRequest.of(page, size)).toList());
        
    }
    
    public Optional<CollectionEntity> getCollectionById(Long id) {
        return this.collectionsRepository.findById(id);
    }

    // public List<CollectionEntity> getCollectorCollections(int page, int size, Optional<Long> optionalId) {
    //     return optionalId
    //     .map(id -> this.collectionsRepository.findByCollectorId(id))
    //     .map(collectionOptional -> collectionOptional
    //         .map(collection -> List.of(collection))
    //         .orElseGet(() -> List.of())
    //     )
    //     .orElseGet(() -> this.collectionsRepository.findAll(PageRequest.of(page, size)).toList());
    // }

}

