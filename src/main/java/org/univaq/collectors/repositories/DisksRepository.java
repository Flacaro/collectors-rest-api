package org.univaq.collectors.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;

public interface DisksRepository extends PagingAndSortingRepository<DiskEntity, Long>, JpaRepository<DiskEntity, Long> {
    public Optional<DiskEntity> findByTitle (String title);

    @Query("SELECT d from disk d where d.collection.id = ?1 ")
    public Optional<DiskEntity> findDiskByIdFromCollectionId(Long collectionId);
}
