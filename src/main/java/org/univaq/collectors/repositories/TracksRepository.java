package org.univaq.collectors.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.univaq.collectors.models.TrackEntity;

public interface TracksRepository extends PagingAndSortingRepository<TrackEntity, Long>, JpaRepository<TrackEntity, Long> {

}
