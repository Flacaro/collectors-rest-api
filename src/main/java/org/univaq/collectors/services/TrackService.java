package org.univaq.collectors.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.repositories.TracksRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TrackService {

    private final TracksRepository trackRepository;

    public TrackService(TracksRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<TrackEntity> getAll (int page, int size, Optional<String> optionalTitle){
        return optionalTitle
                .map(title -> this.trackRepository.findByTitle(title))
                .map(trackOptional -> trackOptional
                        .map(track -> List.of(track))
                        .orElseGet(() -> List.of())
                )
                .orElseGet(() -> this.trackRepository.findAll(PageRequest.of(page, size)).toList());
    }

}
