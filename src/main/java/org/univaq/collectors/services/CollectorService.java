package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.repositories.CollectorsRepository;


import org.univaq.collectors.models.CollectorEntity;

@Service
public class CollectorService {
    
    private final CollectorsRepository collectorsRepository;

    public CollectorService(CollectorsRepository collectorsRepository) {
        this.collectorsRepository = collectorsRepository;
    }


    public List<CollectorEntity> getAll(int page, int size, Optional<String> optionalEmail) {
        return optionalEmail
        .map(this.collectorsRepository::findByEmail)
        .map(collectorOptional -> collectorOptional
            .map(List::of)
            .orElseGet(List::of)
        )
        .orElseGet(() -> this.collectorsRepository.findAll(PageRequest.of(page, size)).toList());
    }

    public CollectorEntity getCollectorByEmail(String email) {
        var collector = this.collectorsRepository.findByEmail(email);
        return collector.orElse(null);
    }

    public CollectorEntity findByUsername(String username) {
        var collector = this.collectorsRepository.findByUsername(username);
        return collector.orElse(null);
    }





}
