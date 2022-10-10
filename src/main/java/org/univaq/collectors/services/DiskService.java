package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.DisksRepository;

@Service
public class DiskService {

    private final DisksRepository disksRepository;
    public DiskService(DisksRepository disksRepository) {
        this.disksRepository = disksRepository;
    }
    
    public List<DiskEntity> getAll (int page, int size, Optional<String> optionalTitle){
       // if (optionalTitle.isPresent()){
        //   var diskOptional = disksRepository.findByTitle(optionalTitle.get());
        //  if (diskOptional.isEmpty()){
        //      return List.of(diskOptional.get());
        //    }
        return optionalTitle
        .map(title -> this.disksRepository.findByTitle(title))
        .map(diskOptional -> diskOptional
        .map(disk -> List.of(disk))
        .orElseGet(() -> List.of())
        )
        .orElseGet(() -> this.disksRepository.findAll(PageRequest.of(page, size)).toList());
    }

}


