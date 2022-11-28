package org.univaq.collectors.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectorEntity;

// Repository<Domain class, ID type of the domain class>
public interface CollectorsRepository extends PagingAndSortingRepository<CollectorEntity, Long>, JpaRepository<CollectorEntity, Long> {
    // findBy<field name>
    public Optional<CollectorEntity> findByEmail(String email);

    public Optional<CollectorEntity> findByUsername(String username);

    
}
