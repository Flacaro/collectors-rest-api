package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorsCollectionsRepository;
import org.univaq.collectors.repositories.CollectorsRepository;

@Service
public class CollectionService {
    
    
    private final CollectionsRepository collectionsRepository;
    private final CollectorsCollectionsRepository collectorsCollectionsRepository;
    private final CollectorsRepository collectorsRepository;

    public CollectionService(CollectionsRepository collectionsRepository, CollectorsCollectionsRepository collectorsCollectionsRepository, CollectorsRepository collectorsRepository) {
        this.collectionsRepository = collectionsRepository;
        this.collectorsCollectionsRepository = collectorsCollectionsRepository;
        this.collectorsRepository = collectorsRepository;
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


    //.stream() (flusso) si usa per processare una lista di oggetti, e' una sequenza di oggetti che supporta
    //vari metodi per processare i dati
    //non e' una struttura dati
    public List<CollectionEntity> getCollectionByCollectorId(Long collectorId) {
        return this.collectorsCollectionsRepository.getCollectionByCollectorId(collectorId).stream()
                .map(CollectorCollectionEntity::getCollections)
                .toList();
    }

    public Optional<CollectionEntity> save(CollectionEntity collection, Long collectorId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        
        //visto che il collector e' un optional, devo controllare se e' presente
        if(optionalCollector.isPresent()) {

            var collector = optionalCollector.get();

            var savedCollection = this.collectionsRepository.save(collection);

            savedCollection.addCollectionToCollector(collector);

            this.collectionsRepository.flush();

            return Optional.of(savedCollection);
        }

        return Optional.empty();
    }

}

