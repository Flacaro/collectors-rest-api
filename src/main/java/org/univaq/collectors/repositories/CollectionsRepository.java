package org.univaq.collectors.repositories;


import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;

public interface CollectionsRepository extends PagingAndSortingRepository<CollectionEntity, Long> {
    
    public Optional<CollectionEntity> findByName(String name);

}
