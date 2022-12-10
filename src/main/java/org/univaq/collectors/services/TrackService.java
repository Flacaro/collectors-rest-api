package org.univaq.collectors.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.repositories.*;

import java.util.List;
import java.util.Optional;

@Service
public class TrackService {

    private final TracksRepository trackRepository;
    private final CollectionsRepository collectionsRepository;
    private final DisksRepository disksRepository;
    private final CollectorsRepository collectorsRepository;
    private final CollectorCollectionRepository collectorCollectionRepository;

    public TrackService(TracksRepository trackRepository, CollectionsRepository collectionsRepository, DisksRepository disksRepository, CollectorsRepository collectorsRepository, CollectorCollectionRepository collectorCollectionRepository) {
        this.trackRepository = trackRepository;
        this.collectionsRepository = collectionsRepository;
        this.disksRepository = disksRepository;
        this.collectorsRepository = collectorsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
    }

    public List<TrackEntity> getAll(int page, int size, Optional<String> optionalTitle) {
        return optionalTitle
                .map(title -> this.trackRepository.findByTitle(title))
                .map(trackOptional -> trackOptional
                        .map(track -> List.of(track))
                        .orElseGet(() -> List.of())
                )
                .orElseGet(() -> this.trackRepository.findAll(PageRequest.of(page, size)).toList());
    }

    //ritorna tracce dato un disco
    public Optional<List<TrackEntity>> getPersonalTracksFromDisk(Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository.
                    findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var disk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (disk.isPresent()) {
                        var trackList = this.trackRepository.findTrackFromDiskId(diskId);
                        if (trackList.isPresent()) {
                            return Optional.of(trackList.get());
                        } else
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track List not found");
                    } else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk is not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the Owner");
            } else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not found");
        }else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Collector is not found");
    }

    //salva traccia
    public Optional<TrackEntity> saveTrack(TrackEntity track, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trovo utente dal email fornito il nome
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get(); //prendi collezionista
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId); //vedo se IdDisco sta nella collezione-> posso farlo così opp. con metodo Personal
                    if (optionalDisk.isPresent()) {
                        var disk = optionalDisk.get();  //se è presente il Idisco nella collezione prendo disco e modifico
                        //aggiungi traccia a disco
                        track.setDisk(disk);
                        return Optional.of(this.trackRepository.save(track));
                    } else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You aren't the Owner");
            }else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not found");
        }else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Collector is not found");
    }

    //elimina traccia
    public void deleteTrack(Long trackId, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trovo utente dal email fornito il nome
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection")
            );
        }
        var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
        if (optionalDisk.isPresent()) {
            //var disk = optionalDisk.get();
            var optionTrack = this.trackRepository.findById(trackId);
            if (optionTrack.isPresent()) {
                var track = optionTrack.get();
                this.trackRepository.delete(track);
            }
        }
    }

    //aggionamento traccia
    public Optional<TrackEntity> updateTrack(TrackEntity track, Long trackId, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trovo utente dal email fornito il nome
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get(); //prendi collezionista
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId); //vedo se IdDisco sta nella collezione-> posso farlo così opp. con metodo Personal
                    if (optionalDisk.isPresent()) {
                        var optionalTrack = this.trackRepository.findById(trackId);
                        if (optionalTrack.isPresent()) {
                            var updateTrack = optionalTrack.get();
                            updateTrack.setTitle(track.getTitle());
                            updateTrack.setArtist(track.getArtist());
                            updateTrack.setAlbum(track.getAlbum());
                            updateTrack.setBand(track.getBand());
                            updateTrack.setCompositor(track.getCompositor());
                            //(trackRepository.saveAndFlush(updateTrack));
                            return Optional.of(this.trackRepository.saveAndFlush(updateTrack));
                        } else
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track is not found");
                    } else
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk is not found");
                } else
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the Owner");
            } else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not found");
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector is not found");
    }

    public Optional<TrackEntity> getPersonalTrackByIdFromDiskId(Long trackId, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trovo utente dal email fornito il nome
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get(); //prendi collezionista
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var optionalTrack = this.trackRepository.findById(trackId);
                        if (optionalTrack.isPresent()) {
                            return optionalTrack;
                        }else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Track is not found");
                    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk is not found");
                } else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the Owner");
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not found");
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector is not found");
    }

    public Optional<List<TrackEntity>> getTracksFromPublicCollectionOfCollector(Long collectorId, Long collectionId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollection = collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollection.isPresent()) {
                var collection = collectorCollection.get();
                if (collection.getCollection().isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var optionalTracks = this.trackRepository.findTrackFromDiskId(diskId);
                        if (optionalTracks.isPresent()) {
                            return optionalTracks;
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }


    public Optional<TrackEntity> getTrackFromPublicCollectionOfCollector(Long collectorId, Long collectionId, Long diskId, Long trackId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollection = collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollection.isPresent()) {
                var collection = collectorCollection.get();
                if (collection.getCollection().isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var optionalTrack = this.trackRepository.findTrackFromDiskId(diskId);
                        if (optionalTrack.isPresent()) {
                            var tracks = optionalTrack.get();
                            for (TrackEntity t : tracks) {
                                if (t.getId().equals(trackId)) {
                                    return Optional.of(t);
                                }
                            }
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }



    public Optional<List<TrackEntity>> getTracksFromPublicCollection(Long collectionId, Long diskId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                if (optionalDisk.isPresent()) {
                    var optionalTracks = this.trackRepository.findTrackFromDiskId(diskId);
                    if (optionalTracks.isPresent()) {
                        return optionalTracks;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }


    public Optional<TrackEntity> getTrackFromPublicCollection(Long collectionId, Long diskId, Long trackId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isPublic()) {
                var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                if (optionalDisk.isPresent()) {
                    var optionalTrack = this.trackRepository.findTrackFromDiskIdAndTrackId(diskId, trackId);
                    if (optionalTrack.isPresent()) {
                        return optionalTrack;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }
}
