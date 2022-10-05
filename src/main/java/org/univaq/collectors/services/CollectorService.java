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

    // if(optionalEmail.isPresent()) {
    //     var collectorOptional = this.collectorsRepository.findByEmail(optionalEmail.get());
    //     if(collectorOptional.isPresent()) {
    //       return List.of(collectorOptional.get());
    //     }
    // } else {
    //     return this.collectorsRepository.findAll(PageRequest.of(page, size)).toList();
    // }

        return optionalEmail
        .map(email -> this.collectorsRepository.findByEmail(email))
        .map(collectorOptional -> collectorOptional
            .map(collector -> List.of(collector))
            .orElseGet(() -> List.of())
        )
        .orElseGet(() -> this.collectorsRepository.findAll(PageRequest.of(page, size)).toList());
    }

    public Optional<CollectorEntity> getCollectorById(Long id) {
        return this.collectorsRepository.findById(id);
    }

    public void deleteCollector(Long collectorId) {
        this.collectorsRepository.deleteById(collectorId);
    }


    
}
