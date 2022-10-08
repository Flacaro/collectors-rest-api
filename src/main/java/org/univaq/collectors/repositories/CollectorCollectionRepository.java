package org.univaq.collectors.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.univaq.collectors.models.CollectorCollectionEntity;

import java.util.List;

public interface CollectorCollectionRepository extends CrudRepository<CollectorCollectionEntity, Long> {

    @Query("select c from collector_collection c where c.collectors.id = ?1")
    List<CollectorCollectionEntity> getCollectionByCollectorId(Long collectorId);

}
