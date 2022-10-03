package org.univaq.collectors.repositories;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.Disk;

public interface DiskRepository extends PagingAndSortingRepository<Disk, Long> {
    public Iterable<Disk> findAll();
    
}
