package org.univaq.collectors.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.Collection;

public interface CollectionsRepository extends PagingAndSortingRepository<Collection, Long> {
    
    List<Collection> findByCollectorId(Long collectorId);

}
