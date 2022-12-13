package org.univaq.collectors.services;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
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
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Optional<List<DiskEntity>> getDisksFromPublicCollection (Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var optionalCollection = this.collectionsRepository.findById(collectionId);
            if (optionalCollection.isPresent()) {
                var collection = optionalCollection.get();
                var collectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collection.getId());
                if(collectorCollection.isPresent()) {
                    if (collectorCollection.get().getCollection().isPublic()) {
                        var disks = this.disksRepository.findDisksFromCollectionId(collectorCollection.get().getCollection().getId());
                        if (disks.isPresent()) {
                            return disks;
                        } else {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
                        }
                    }
                }
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public Optional<DiskEntity> saveDisk(DiskEntity disk, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository
                    .findCollectionByIdAndCollectorById(collector.getId(), collectionId);

            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var collection = collectorCollection.getCollection();
                    disk.setCollection(collection);
                    //var savedDisk = this.disksRepository.save(disk);
                    return Optional.of(this.disksRepository.save(disk));
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You aren't the owner of this collection");
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
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
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
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
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }

    public List<DiskEntity> getDisksByParameters(Long year, String format, String author, String genre, String title, String artist, String band) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("year", contains().ignoreCase())
                .withMatcher("format", contains().ignoreCase())
                .withMatcher("author", contains().ignoreCase())
                .withMatcher("genre", contains().ignoreCase())
                .withMatcher("title", contains().ignoreCase())
                .withMatcher("artist", contains().ignoreCase())
                .withMatcher("band", contains().ignoreCase());

        DiskEntity example = new DiskEntity();
        example.setYear(year);
        example.setFormat(format);
        example.setAuthor(author);
        example.setGenre(genre);
        example.setTitle(title);
        example.setArtist(artist);
        example.setBand(band);

        return this.disksRepository.findAll(Example.of(example, matcher));
    }


    //prendi i dischi personali dalla collezione
    public Optional<List<DiskEntity>> getPersonalDisksFromCollection(Long collectionId, Authentication authentication) {
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
                        return diskList;
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk List not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
            } else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }



    //metodo per prendere disco da collezione passato id disco e collezione è mia  ;
    public Optional<DiskEntity> getPersonalDiskByIdFromCollectionId(Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trova utente per email
        if (optionalCollector.isPresent()) { //se utente è presente prendilo get
            var collector = optionalCollector.get();
            var collectorCollection = collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollection.isPresent()) {
                var isOwner = collectorCollection.get().isOwner();
                if (isOwner && collectionId.equals(collectorCollection.get().getCollection().getId())) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        return optionalDisk;
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You aren't the owner of this collection");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }


    public Optional<DiskEntity> updateDisk(DiskEntity disk, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository.
                    findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var diskToUpdate = optionalDisk.get();
                        diskToUpdate.updateDisk(disk);
                        disksRepository.saveAndFlush(diskToUpdate);
                        return Optional.of(diskToUpdate);
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");

                } else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner");

            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");

        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");

    }
}

