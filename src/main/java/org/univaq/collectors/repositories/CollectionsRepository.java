package org.univaq.collectors.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;

public interface CollectionsRepository extends PagingAndSortingRepository<CollectionEntity, Long>,
        JpaRepository<CollectionEntity, Long> {


    @Query("select c from collection c where c.name like %?1% and c.isPublic = true")
    public List<CollectionEntity> getPublicCollectionsByName(String name, PageRequest pageRequest);

    @Query("select c from collection c where c.type like %?1% and c.isPublic = true")
    public List<CollectionEntity> getPublicCollectionsByType(String name, PageRequest pageRequest);

    @Query("select c from collection c where c.isPublic = true")
    public List<CollectionEntity> getAllPublicCollections(PageRequest pageRequest);


}
