package org.univaq.collectors.services;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.repositories.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

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

    public List<TrackEntity> getTracksByParameters(String title, String artist, String album, String band, String compositor) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("title", contains().ignoreCase())
                .withMatcher("artist", contains().ignoreCase())
                .withMatcher("album", contains().ignoreCase())
                .withMatcher("band", contains().ignoreCase())
                .withMatcher("compositor", contains().ignoreCase());

        TrackEntity example = new TrackEntity();
        example.setTitle(title);
        example.setArtist(artist);
        example.setAlbum(album);
        example.setBand(band);
        example.setCompositor(compositor);

        return this.trackRepository.findAll(Example.of(example, matcher));
    }

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
                        var trackList = this.trackRepository.findTracksFromDiskId(diskId);
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
            var collectorCollection = collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if(collectorCollection.isPresent()) {
            var isOwner = collectorCollection.get().isOwner();
            if(isOwner) {
                var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                if (optionalDisk.isPresent()) {
                    var optionTrack = this.trackRepository.findById(trackId);
                    if (optionTrack.isPresent()) {
                        var track = optionTrack.get();
                        this.trackRepository.delete(track);
                    }
                } else
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
            }
            else
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the Owner");
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection is not found");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Collector is not found");
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
                            var trackToUpdate = optionalTrack.get();
                            trackToUpdate.updateTrack(track);
                            return Optional.of(this.trackRepository.save(trackToUpdate));
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

    public Optional<List<TrackEntity>> getTracksFromPublicCollection(Long collectorId, Long collectionId, Long diskId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollectionOptional = collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollectionOptional.isPresent()) {
                var collectorCollection = collectorCollectionOptional.get();
                var collection = collectorCollection.getCollection();
                if (collection.isPublic()) {
                    var optionalDisk = this.disksRepository.findDiskByIdFromCollectionId(collectionId, diskId);
                    if (optionalDisk.isPresent()) {
                        var optionalTracks = this.trackRepository.findTracksFromDiskId(diskId);
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
                        var optionalTrack = this.trackRepository.findTracksFromDiskId(diskId);
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
                    var optionalTracks = this.trackRepository.findTracksFromDiskId(diskId);
                    if (optionalTracks.isPresent()) {
                        return optionalTracks;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk not found");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
            }
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
