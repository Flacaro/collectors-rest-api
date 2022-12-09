package org.univaq.collectors.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    //prendi tutti dischi pubblici
    //inserire se collezione da cui prendi dischi è pubblica o private se privata non mostrare: richiamto in diskCOntroller
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

    //salvo disco
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

    //elimina disco dalla collezione privata
    public void deleteDiskById(Long collectionId, Long collectorId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);   //trovo il collezionista
        if (optionalCollector.isPresent()) { //se il collezionista e' presente
            var collector = optionalCollector.get(); //prendo il collezionista
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId); //trovo la collezione del collezionista

            if (collectorCollectionOptional.isPresent()) { //se la collezione e' presente
                var collectorCollection = collectorCollectionOptional.get();

                var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                if (optionalDisk.isPresent()) {
                    var disk = optionalDisk.get();
                    this.disksRepository.delete(disk);
                }

            }
        }
    }


    public Optional<DiskEntity> getDiskByIdFromPublicCollection(Long collectionId, Long collectorId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);   //trova id del collezionista
        if (optionalCollector.isPresent()) { //se id è presente
            var collector = optionalCollector.get(); //collezionista
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId); //trova la collezione dall'id del collezionista

            if (collectorCollectionOptional.isPresent()) { //se la collezione è presente
                var collectorCollection = collectorCollectionOptional.get();
                var collection = collectorCollection.getCollection(); //prendi la collezione
                if (collection.isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var disk = optionalDisk.get(); //disco
                        return Optional.of(disk);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //prendi i dischi personali dalla collezione
    public List<DiskEntity> getPersonalDisksFromCollection(Long collectionId, Authentication authentication) {
        var optionalcollector = collectorsRepository.findByEmail(authentication.getName());
        if (optionalcollector.isPresent()) {
            var collector = optionalcollector.get();
            var optionalCollectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (optionalCollectorCollection.isPresent()) {
                var collectorCollection = optionalCollectorCollection.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var diskList = this.disksRepository.findDisksFromCollectionId(collectionId);
                    if (diskList.isPresent()) {
                        return diskList.get();
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk List not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
            }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
    }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
}


    //metodo per prendere disco da collezione passato id disco e collezione è mia  ;
    public Optional<DiskEntity> getPersonalDiskByIdFromCollectionId(Long diskId, Long collectionId, Authentication authentication ) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trova utente per email
        if (optionalCollector.isPresent()) { //se utente è presente prendilo get
            var collector = optionalCollector.get();
            //collezionista = owner collezione
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection")
            );
        }
        //prendo disco da id collezione
        var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
        if (optionalDisk.isPresent()) {
            var disk = optionalDisk.get();
            disksRepository.findById(diskId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        }
        return optionalDisk;
    }

    public ResponseEntity<DiskEntity> updateDisk (DiskEntity disk, Long diskId, Long collectionId, Authentication authentication){
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            //collezionista = ower
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not owner of this collection")
            );
        }
        var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
        if (optionalDisk.isPresent()){
            var updateDisk = optionalDisk.get();
            updateDisk.setTitle(disk.getTitle());
            updateDisk.setAuthor(disk.getAuthor());
            updateDisk.setLabel(disk.getLabel());
            updateDisk.setState(disk.getState());
            updateDisk.setFormat(disk.getFormat());
            updateDisk.setBarcode(disk.getBarcode());
            updateDisk.setDuplicate(disk.getDuplicate());
            updateDisk.setYear(disk.getYear());
            return new ResponseEntity<>(disksRepository.saveAndFlush(updateDisk), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


