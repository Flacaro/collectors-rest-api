package org.univaq.collectors.repositories;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectorEntity;

// Repository<Domain class, ID type of the domain class>
public interface CollectorsRepository extends PagingAndSortingRepository<CollectorEntity, Long> {
    // findBy<field name>
    public Optional<CollectorEntity> findByEmail(String email);


    
}
