package org.univaq.collectors.repositories;


import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.DiskEntity;

public interface DisksRepository extends PagingAndSortingRepository<DiskEntity, Long> {
    public Optional<DiskEntity> findByTitle (String title);
    
}
