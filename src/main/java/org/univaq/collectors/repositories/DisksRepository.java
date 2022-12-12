package org.univaq.collectors.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;

public interface DisksRepository extends PagingAndSortingRepository<DiskEntity, Long>, JpaRepository<DiskEntity, Long> {
    public Optional<DiskEntity> findByTitle (String title);

//    public Optional<List<DiskEntity>> findAllDiskFromPublicCollection(PageRequest pageRequest);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1")
    public Optional<List<DiskEntity>> findDisksFromCollectionId(Long collectionId);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 AND d.id = ?2")
    public Optional<DiskEntity> findDiskByIdFromCollectionId(Long collectionId, Long diskId);

    @Query("SELECT d FROM disk d WHERE d.collection.id = ?1 and d.year = ?2 and d.format like %?3% and d.author like %?4% and d.genre like %?5% and d.title like %?6%")
    public Optional<List<DiskEntity>> getDisksByYearFormatAuthorGenreTitleFromPublicCollection(Long collectionId, Long year, String format, String author, String genre, String title, PageRequest pageRequest);

    @Query("SELECT d FROM disk d WHERE d.collection.isPublic = true and d.year = ?2 and d.format like %?3% and d.author like %?4% and d.genre like %?5%")
    public Optional<List<DiskEntity>> getDisksByYearFormatAuthorGenreTitleFromPublicCollections(Long year, String format, String author, String genre, String title, PageRequest pageRequest);


}
