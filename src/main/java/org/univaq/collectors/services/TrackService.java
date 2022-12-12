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

//    public List<TrackEntity> getAll(int page, int size, Optional<String> optionalTitle) {
//        return optionalTitle
//                .map(this.trackRepository::findByTitle)
//                .map(trackOptional -> trackOptional
//                        .map(List::of)
//                        .orElseGet(List::of)
//                )
//                .orElseGet(() -> this.trackRepository.findAll(PageRequest.of(page, size)).toList());
//    }

    //ritorna tracce dato un disco
    public List<TrackEntity> getPersonalTracksFromDisk(Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            //controlla se è l'ower
            var collector = optionalCollector.get();
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection")
            );
        }
        var disk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
        if (disk.isPresent()) {
            var trackList = this.trackRepository.findTrackFromDiskId(diskId);
            if (trackList.isPresent()) {
                return trackList.get();
            }
        }

        return List.of();
    }

    //salva traccia
    public Optional<TrackEntity> saveTrack(TrackEntity track, Long diskId, Long collectionId, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName()); //trovo utente dal email fornito il nome
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get(); //prendi collezionista
            //vedo se collezionistaPreso = owerCollezione -> nella tabella collezioniCollezionista
            collectorCollectionRepository.hasCollectionAndIsOwner(collector.getId(), collectionId
            ).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection")
            );//tramite tabella prendo che la collezione associata al collezionista

            var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId); //vedo se IdDisco sta nella collezione-> posso farlo così opp. con metodo Personal
            if (optionalDisk.isPresent()) {
                var disk = optionalDisk.get();  //se è presente il Idisco nella collezione prendo disco e modifico
                //aggiungi traccia a disco
                track.setDisk(disk);

                return Optional.of(this.trackRepository.save(track));
            }
        }
        return Optional.of(track);
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
    public ResponseEntity<TrackEntity> updateTrack(TrackEntity track, Long trackId, Long diskId, Long collectionId, Authentication authentication) {
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
        if (optionalDisk.isPresent()) {
            var optionalTrack = this.trackRepository.findById(trackId);
            if (optionalTrack.isPresent()) {
                var updateTrack = optionalTrack.get();
                updateTrack.setTitle(track.getTitle());
                updateTrack.setArtist(track.getArtist());
                updateTrack.setAlbum(track.getAlbum());
                updateTrack.setBand(track.getBand());
                updateTrack.setCompositor(track.getCompositor());
                return new ResponseEntity<>(trackRepository.saveAndFlush(updateTrack), HttpStatus.OK);

            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Optional<TrackEntity> getPersonalTrackByIdFromDiskId(Long trackId, Long diskId, Long collectionId, Authentication authentication) {
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
        if (optionalDisk.isPresent()) {
                var optionalTrack = this.trackRepository.findById(trackId);
                if (optionalTrack.isPresent()) {
                    return optionalTrack;
                }

            }
        return Optional.empty();
    }

    public Optional<List<TrackEntity>> getTracksFromPublicCollection(Long collectorId, Long collectionId, Long diskId) {
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


    public Optional<TrackEntity> getTrackFromPublicCollection(Long collectorId, Long collectionId, Long diskId, Long trackId) {
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



    public Optional<TrackEntity> getTrackByTitleFromPublicCollections(Long collectorId, Long collectionId, Long diskId, String title) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollection = collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollection.isPresent()) {
                var collection = collectorCollection.get();
                if (collection.getCollection().isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var optionalTrack = this.trackRepository.findTrackByTitleFromDiskId(diskId, title);
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
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");

    }

//    public Optional<TrackEntity> getTracksByTitle( String title, int page, int size) {
//        var optionalTracks = this.trackRepository.findByTitle(title, page, size);
//        if (optionalTracks.isPresent()) {
//            return optionalTracks;
//        }
//        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
//    }


}
