package org.univaq.collectors.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.models.TrackEntity;

import java.util.List;
import java.util.Optional;

public interface TracksRepository extends PagingAndSortingRepository<TrackEntity, Long>, JpaRepository<TrackEntity, Long> {
    public Optional<TrackEntity> findByTitle (String title);

    @Query("SELECT d FROM track d WHERE d.disk.id = ?1")
    public Optional<List<TrackEntity>> findTrackFromDiskId(Long diskId);

}
//devo aggiungere anche collectionId
