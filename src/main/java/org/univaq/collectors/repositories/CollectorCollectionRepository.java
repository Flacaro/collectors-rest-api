package org.univaq.collectors.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;

import java.util.List;
import java.util.Optional;

public interface CollectorCollectionRepository extends JpaRepository<CollectionEntity, Long> {

    @Query("select c from collector_collection c where c.collector.id = ?1")
    List<CollectorCollectionEntity> getCollectionsByCollectorId(Long collectorId);

    @Query("select c from collector_collection c where c.collector.id = ?1 and c.collection.id=?2" )
    Optional<CollectorCollectionEntity> findCollectionByIdAndCollectorById (Long collectorId, Long collectionId);

    @Query("select c from collector_collection c where c.collector.id = ?1 and c.collection.isPublic = true")
    List<CollectorCollectionEntity> getPublicCollectionsByCollectorId(Long collectorId);

}
