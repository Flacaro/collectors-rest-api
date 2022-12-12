package org.univaq.collectors.services;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.repositories.DisksRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

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

//    public Optional<List<DiskEntity>> findAllDiskFromPublicCollection(Integer page, Integer size) {
//        return disksRepository.findAllDiskFromPublicCollection(PageRequest.of(page, size));
//    }

    public Optional<List<DiskEntity>> getDisksFromPublicCollection(Long collectionId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var disks = this.disksRepository.findDisksFromCollectionId(collection.getId());
                if (disks.isPresent()) {
                    return disks;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public" );
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Optional<List<DiskEntity>> getDisksFromPublicCollection(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var optionalCollectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (optionalCollectorCollection.isPresent()) {
                var collection = optionalCollectorCollection.get().getCollection();
                if (collection.isPublic()) {
                    var disks = this.disksRepository.findDisksFromCollectionId(collectionId);
                    if (disks.isPresent()) {
                        return disks;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public" );
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collector is not public" );
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    //salvo disco
    public Optional<DiskEntity> saveDisk(DiskEntity disk, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
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
    public void deleteDiskById(Authentication authentication, Long collectionId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());   //trovo il collezionista
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


    public Optional<DiskEntity> getDiskByIdFromPublicCollection(Long collectionId, Long diskId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                if (optionalDisk.isPresent()) {
                    return optionalDisk;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public" );
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Optional<DiskEntity> getDiskByIdFromPublicCollection(Long collectorId, Long collectionId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                if (collectorCollection.getCollection().isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        return optionalDisk;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public" );
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found" );
    }

    public Optional<List<DiskEntity>> getDiskByParameters(Long year, String format, String author, String genre, String title) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("year", contains().ignoreCase())
                .withMatcher("format", contains().ignoreCase())
                .withMatcher("author", contains().ignoreCase())
                .withMatcher("genre", contains().ignoreCase())
                .withMatcher("title", contains().ignoreCase());

        DiskEntity example = new DiskEntity();
        example.setYear(year);
        example.setFormat(format);
        example.setAuthor(author);
        example.setGenre(genre);
        example.setTitle(title);

        return Optional.of(this.disksRepository.findAll(Example.of(example, matcher)));
    }

    public Optional<List<DiskEntity>> getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(Long collectionId, Long year, String format, String author, String genre, String title, Integer page, Integer size) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var optionalDisks = this.disksRepository.getDisksByYearFormatAuthorGenreTitleFromPublicCollection(collectionId, year, format, author, genre, title, PageRequest.of(page, size));
                if (optionalDisks.isPresent()) {
                    return optionalDisks;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public" );
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Optional<List<DiskEntity>> getDisksByYearFormatAuthorGenreTitleFromPublicCollections(Long year, String format, String author, String genre, String title, Integer page, Integer size) {
        var optionalDisks = this.disksRepository.getDisksByYearFormatAuthorGenreTitleFromPublicCollections(year, format, author, genre, title, PageRequest.of(page, size));
        if (optionalDisks.isPresent()) {
            return optionalDisks;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk List not found" );
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection" );
            } else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found" );
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found" );
    }


    //metodo per prendere disco da collezione passato id disco e collezione è mia  ;
    public Optional<DiskEntity> getPersonalDiskByIdFromCollectionId(Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trova utente per email
        if (optionalCollector.isPresent()) { //se utente è presente prendilo get
            var collector = optionalCollector.get();
            //collezionista = owner collezione
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection" )
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

    public ResponseEntity<DiskEntity> updateDisk(DiskEntity disk, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            //collezionista = ower
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not owner of this collection" )
            );
        }
        var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
        if (optionalDisk.isPresent()) {
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
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}



