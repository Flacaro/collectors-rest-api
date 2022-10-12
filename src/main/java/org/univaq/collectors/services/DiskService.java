package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.repositories.DisksRepository;

@Service
public class DiskService {

    private final DisksRepository disksRepository;
    private final CollectionsRepository collectionsRepository;
    private final CollectorsRepository collectorsRepository;
    private final CollectorCollectionRepository collectorCollectionRepository;

    public DiskService(
            DisksRepository disksRepository,
            CollectionsRepository collectionsRepository,
            CollectorsRepository collectorsRepository,
            CollectorCollectionRepository collectorCollectionRepository
    ) {
        this.disksRepository = disksRepository;
        this.collectionsRepository = collectionsRepository;
        this.collectorsRepository = collectorsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
    }

    public List<DiskEntity> getAll(int page, int size, Optional<String> optionalTitle) {
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

    public Optional<DiskEntity> saveDisk(DiskEntity disk, Long collectionId, Long collectorId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId);

            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();

                disk.setCollection(collectorCollection.getCollection());

                var savedDisk = this.disksRepository.save(disk);

                return Optional.of(savedDisk);
            }
        }

        return Optional.empty();
    }
//elimina disco da collezione

    public void deleteDiskById(Long collectionId, Long collectorId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);   //trova id del collezionista
        if (optionalCollector.isPresent()) { //se id è presente
            var collector =optionalCollector.get(); //prendi id
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId); //trova id della collezione che ha id collezionista
           
            if (collectorCollectionOptional.isPresent()){//se id collezione è presente
                var collectorCollection = collectorCollectionOptional.get();

                var optionalDisk = this.disksRepository.findById(diskId);
                if (optionalDisk.isPresent()){
                    var disk = optionalDisk.get();
                    this.disksRepository.delete(disk);
                }

            } 
        }
    }

    public Optional<DiskEntity> getDiskId(Long collectionId, Long collectorId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);   //trova id del collezionista
        if (optionalCollector.isPresent()) { //se id è presente
            var collector =optionalCollector.get(); //prendi id
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId); //trova id della collezione che ha id collezionista
           
            if (collectorCollectionOptional.isPresent()){//se id collezione è presente
                var collectorCollection = collectorCollectionOptional.get();

                var optionalDisk = this.disksRepository.findById(diskId);
                if (optionalDisk.isPresent()){
                    var disk = optionalDisk.get(); //preso id del disco 
                    return Optional.of(disk);
                }  

            }
    }
        return Optional.empty();
}
}


