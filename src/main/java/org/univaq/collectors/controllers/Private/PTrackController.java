package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.TrackService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/private")
public class PTrackController {

    private final TrackService trackService;

    private final CollectorService collectorService;
    private final SerializeWithView serializeWithView;

    //costruttore
    public PTrackController(TrackService trackService, SerializeWithView serializeWithView, CollectorService collectorService) {
        this.trackService = trackService;
        this.serializeWithView = serializeWithView;
        this.collectorService = collectorService;
    }

    //metodi
    @GetMapping(value = "/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPersonalTracksOfDisk(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String compositor,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        try {
            var tracks = trackService.getPersonalTracksFromDisk(diskId, collectionId, authentication);
            var tracksByParameters = new ArrayList<TrackEntity>();
            if(tracks.isPresent()) {
                if (title == null && artist == null && album == null && band == null && compositor == null) {
                    return getStringResponseEntityTrack(view, tracks);
                } else {
                    var result = this.trackService.getTracksByParameters(title, artist, album, band, compositor);
                        for (TrackEntity track : result) {
                            if (tracks.get().contains(track)) {
                                tracksByParameters.add(track);
                        }
                        return getStringResponseEntityTrack(view, Optional.of(tracksByParameters));
                    }
                }
            }
        }  catch(JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/collections/{collectionId}/disks/{diskId}/tracks",produces = "application/json")
    public ResponseEntity<String> saveTrack(
            @RequestBody TrackEntity track,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view

    ) {
        try {
            var optionalTrack = this.trackService.saveTrack(track, diskId, collectionId, authentication);
            if(optionalTrack.isPresent()) {
                return getStringResponseEntity(view, optionalTrack);
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/collections/{collectionId}/disks/{diskId}/tracks/{trackId}")
    public ResponseEntity<TrackEntity> deleteTrackById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            Authentication authentication
    ) {
        this.trackService.deleteTrack(trackId, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> updateTrackById(
            @RequestBody TrackEntity track, //contiene dati ma non rappresenta una traccia nel db trackDTo
            @PathVariable("trackId") Long trackId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String view,
            Authentication authentication
    ) {
        var optionalTrack = this.trackService.updateTrack(track, trackId, diskId, collectionId, authentication);
        try {
            if(optionalTrack.isPresent()) {
                return getStringResponseEntity(view, optionalTrack);
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getTrackById(
            @PathVariable("trackId") Long trackId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var track = trackService.getPersonalTrackByIdFromDiskId(trackId, diskId, collectionId, authentication);
            try {
                if (track.isPresent()) {
                return getStringResponseEntity(view, track);
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value ="collectors/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPublicTracks(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String compositor,
            @RequestParam(required = false) String view
    ) {
        var collector = collectorService.getById(collectorId);
            try {
                if (collector.isPresent()) {
                if (collector.get().getEmail().equals(authentication.getName())) {
                    var tracks = trackService.getPersonalTracksFromDisk(diskId, collectionId, authentication);
                    var tracksByParameters = new ArrayList<TrackEntity>();
                    if (tracks.isPresent()) {
                        if (title == null && artist == null && album == null && band == null && compositor == null) {
                            return getStringResponseEntityTrack(view, tracks);
                        } else {
                            var result = this.trackService.getTracksByParameters(title, artist, album, band, compositor);
                                for (TrackEntity track : result) {
                                    if (tracks.get().contains(track)) {
                                        tracksByParameters.add(track);
                                    }
                                return getStringResponseEntityTrack(view, Optional.of(tracksByParameters));
                            }
                        }
                    }
                }
            }
        } catch(JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value ="collectors/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getPublicTrackById(
            Authentication authentication,
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            @RequestParam(required = false) String view
    ) {
        var collector = collectorService.getById(collectorId);
            try {
                if (collector.isPresent()) {
                if (collector.get().getEmail().equals(authentication.getName())) {
                    var track = trackService.getPersonalTrackByIdFromDiskId(trackId, diskId, collectionId, authentication);
                    if (track.isPresent()) {
                        return getStringResponseEntity(view, track);
                    }
                }
            }

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }



    private ResponseEntity<String> getStringResponseEntityTrack(@RequestParam(required = false) String view, Optional<List<TrackEntity>> publicTracks) throws JsonProcessingException {
        if (publicTracks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No tracks found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, publicTracks.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PUBLIC, publicTracks.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<TrackEntity> track) throws JsonProcessingException {
        if (track.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No track found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, track.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PUBLIC, track.get()));
        }
    }

}