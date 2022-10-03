package org.univaq.collectors.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.Collector;

// Repository<Domain class, ID type of the domain class>
public interface CollectorsRepository extends PagingAndSortingRepository<Collector, Long> {
    // findBy<field name>
    public Optional<Collector> findByEmail(String email);

    //public Optional<Collector> findAllCollections(List<Collection> collection);

    public Optional<Collector> findById(Long collectorId);

    
}
