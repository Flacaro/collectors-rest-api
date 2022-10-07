package org.univaq.collectors.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;

public interface CollectionsRepository extends PagingAndSortingRepository<CollectionEntity, Long>, JpaRepository<CollectionEntity, Long> {
    
    public Optional<CollectionEntity> findByName(String name);



}
