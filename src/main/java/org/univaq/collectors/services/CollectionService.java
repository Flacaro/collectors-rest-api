package org.univaq.collectors.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.univaq.collectors.models.Collection;
import org.univaq.collectors.repositories.CollectionsRepository;

@Service
public class CollectionService {
    
    private final CollectionsRepository collectionsRepository;

    public CollectionService(CollectionsRepository collectionsRepository) {
        this.collectionsRepository = collectionsRepository;
    }

    public List<Collection> getCollectorCollections(Long collectorId) {
        return collectionsRepository.findByCollectorId(collectorId);
    }
        
    }

