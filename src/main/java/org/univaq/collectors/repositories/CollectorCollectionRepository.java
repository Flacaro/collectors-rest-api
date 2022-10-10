package org.univaq.collectors.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.univaq.collectors.models.CollectorCollectionEntity;

import java.util.List;
import java.util.Optional;

public interface CollectorCollectionRepository extends CrudRepository<CollectorCollectionEntity, Long> {

    @Query("select c from collector_collection c where c.collector.id = ?1")
    List<CollectorCollectionEntity> getCollectionByCollectorId(Long collectorId);

    @Query("select c from collector_collection c where c.collector.id = ?1 and c.collection.id=?2" )
    Optional<CollectorCollectionEntity> findCollectionByIdAndCollectorById (Long collectorId, Long collectionId);

}
