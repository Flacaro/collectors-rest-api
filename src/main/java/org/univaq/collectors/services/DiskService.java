package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.DisksRepository;

@Service
public class DiskService {

    private final DisksRepository disksRepository;
    private final CollectionsRepository collectionsRepository;

    public DiskService(
        DisksRepository disksRepository,
        CollectionsRepository collectionsRepository
        ) {
        this.disksRepository = disksRepository;
        this.collectionsRepository = collectionsRepository;
    
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

    public Optional<DiskEntity> saveDisk(DiskEntity newdisk, Long diskId) {
        var optionalCollection = this.collectionsRepository.findById(diskId);
        if (optionalCollection.isPresent()){

            var collection = optionalCollection.get();

            var savedDisk = this.disksRepository.save(newdisk);

            savedDisk.addDisk(newdisk);

            this.disksRepository.flush();

            return Optional.of(savedDisk);
        }
        return Optional.empty();
    }
}

