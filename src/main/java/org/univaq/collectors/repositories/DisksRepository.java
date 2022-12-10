package org.univaq.collectors.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;

public interface DisksRepository extends PagingAndSortingRepository<DiskEntity, Long>, JpaRepository<DiskEntity, Long> {
    public Optional<DiskEntity> findByTitle (String title);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1")
    public Optional<List<DiskEntity>> findDisksFromCollectionId(Long collectionId);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 AND d.id = ?2")
    public Optional<DiskEntity> findDiskByIdFromCollectionId(Long collectionId, Long diskId);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 and d.year = ?2")
    public Optional<List<DiskEntity>> findByYearFromCollectionId(Long collectionId, Long year);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 and d.format = ?2")
    public Optional<List<DiskEntity>> findByFormatFromCollectionId(Long collectionId, String format);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 and d.author = ?2")
    public Optional<List<DiskEntity>> findByAuthorFromCollectionId(Long collectionId, String author);
}
