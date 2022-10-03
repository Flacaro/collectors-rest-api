package org.univaq.collectors.repositories;


import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.Disk;

public interface DisksRepository extends PagingAndSortingRepository<Disk, Long> {
    public Optional<Disk> findByTitle (String title);
    
}
