package org.univaq.collectors.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;

// Repository<Domain class, ID type of the domain class>
public interface CollectorsRepository extends PagingAndSortingRepository<CollectorEntity, Long>, JpaRepository<CollectorEntity, Long> {
    // findBy<field name>
    public Optional<CollectorEntity> findByEmail(String email);

    public Optional<CollectorEntity> findByUsername(String username);

    @Query("select c from collector c where c.id = ?1 and c.favourites = ?2")
    public List<CollectionEntity> findCollectionByIdInFavouritesList(Long collectorId, Long collectionId);

    
}
