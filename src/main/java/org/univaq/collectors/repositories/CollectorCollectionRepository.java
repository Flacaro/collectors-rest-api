package org.univaq.collectors.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;

import java.util.List;
import java.util.Optional;

public interface CollectorCollectionRepository extends JpaRepository<CollectionEntity, Long> {

    @Query("select c from collector_collection c where c.collector.id = ?1")
    Optional<List<CollectorCollectionEntity>> findCollectionsByCollectorId(Long collectorId, PageRequest pageRequest);

    @Query("select c from collector_collection c where c.collector.id = ?1 and c.collection.id=?2" )
    Optional<CollectorCollectionEntity> findCollectionByIdAndCollectorById (Long collectorId, Long collectionId);

    @Query("select c from collector_collection c where c.collector.id = ?1 and c.collection.isPublic = true")
    Optional<List<CollectorCollectionEntity>> findPublicCollectionsByCollectorId(Long collectorId);

    //prendo la collezione se sono l'owner
    @Query("select c from collector_collection c where c.collection.id = ?1 and c.isOwner = true" )
    Optional<List<CollectorCollectionEntity>> findCollectionByCollectorId(Long collectorId);


}
