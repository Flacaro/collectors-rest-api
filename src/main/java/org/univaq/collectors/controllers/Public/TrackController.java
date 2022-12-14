package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.TrackService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public")
public class TrackController {

    private final TrackService trackService;
    private final SerializeWithView serializeWithView;

    public TrackController(TrackService trackService, SerializeWithView serializeWithView) {
        this.trackService = trackService;
        this.serializeWithView = serializeWithView;
    }


    @GetMapping(value = "collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getTracksFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String compositor,
            @RequestParam(required = false) String view
    ) {
        try {
            var tracksByParameters = new ArrayList<TrackEntity>();
            var tracks = trackService.getTracksFromPublicCollection(collectionId, diskId);
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


    @GetMapping(value = "collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getTrackFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @PathVariable Long trackId,
            @RequestParam(required = false) String view
    ) {
        var optionalTrack = trackService.getTrackFromPublicCollection(collectionId, diskId, trackId);
            try {
                if (optionalTrack.isPresent()) {
                return getStringResponseEntity(view, optionalTrack);
                }

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value ="collectors/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPublicTracks(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String compositor,
            @RequestParam(required = false) String view
    ) {
        var tracksByParameters = new ArrayList<TrackEntity>();
        var tracks = trackService.getTracksFromPublicCollection(collectorId, collectionId, diskId);
           try {
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
       } catch (JsonProcessingException e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
       }
         return ResponseEntity.ok().build();
    }

    @GetMapping(value ="collectors/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getPublicTrackById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            @RequestParam(required = false) String view
    ) {
        var track = trackService.getTrackFromPublicCollection(collectorId, collectionId, diskId, trackId);
            try {
                if (track.isPresent()) {
                    return getStringResponseEntity(view, track);
                }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> getStringResponseEntityTrack(@RequestParam(required = false) String view, Optional<List<TrackEntity>> publicTracks) throws JsonProcessingException {
        if (publicTracks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No public tracks found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, publicTracks.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PUBLIC, publicTracks.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<TrackEntity> publicTrack) throws JsonProcessingException {
        if (publicTrack.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No public track found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, publicTrack.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PUBLIC, publicTrack.get()));
        }
    }


}
