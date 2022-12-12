package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.type.EntityType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.TrackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("public/")
public class TrackController {

    private final TrackService trackService;
    private final SerializeWithView serializeWithView;

    public TrackController(TrackService trackService, SerializeWithView serializeWithView) {
        this.trackService = trackService;
        this.serializeWithView = serializeWithView;
    }

//    @GetMapping(value = "/tracks", produces = "application/json")
//    public ResponseEntity<String> getTracksByTitle (
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String artist,
//            @RequestParam(required = false) String album,
//            @RequestParam(required = false) String band,
//            @RequestParam(required = false) String compositor,
//            @RequestParam(required = false) String view
//    ) {
//        try {
//            if (title != null) {
//                var tracks = trackService.getTracksByTitle(title, page, size);
//                return getStringResponseEntity(view, tracks);
//            }
//        } catch (JsonProcessingException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
//    }

    @GetMapping(value = "/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getTracksFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @RequestParam(required = false) String view
    ) {
        try {
            var tracks = trackService.getTracksFromPublicCollection(collectionId, diskId);
            return getStringResponseEntityTrack(view, tracks);
        }  catch(JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @GetMapping(value = "/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getTrackFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @PathVariable Long trackId,
            @RequestParam(required = false) String view
    ) {
        try {
            var track = trackService.getTrackFromPublicCollection(collectionId, diskId, trackId);
            if (track.isPresent()) {
                if (view != null && view.equals("private")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, track));
                } else {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PUBLIC, track));
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value ="/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPublicTracks(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String view
    ) {
       try {
           if (title == null) {
                var tracks = trackService.getTracksFromPublicCollection(collectorId, collectionId, diskId);
                return getStringResponseEntityTrack(view, tracks);
           }
                var track = trackService.getTrackByTitleFromPublicCollections(collectorId, collectionId, diskId, title);
                return getStringResponseEntity(view, track);
       } catch (JsonProcessingException e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
       }
    }

    @GetMapping(value ="/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getPublicTrackById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            @RequestParam(required = false) String view
    ) {
        try {
            var track = trackService.getTrackFromPublicCollection(collectorId, collectionId, diskId, trackId);
            if (track.isPresent()) {
                if (view != null && view.equals("private")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, track));
                } else {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.TRACK, SerializeWithView.ViewType.PRIVATE, track));
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> getStringResponseEntityTrack(@RequestParam(required = false) String view, Optional<List<TrackEntity>> publicTracks) throws JsonProcessingException {
        if (publicTracks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, publicTracks.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, publicTracks.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<TrackEntity> publicTrack) throws JsonProcessingException {
        if (publicTrack.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, publicTrack.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, publicTrack.get()));
        }
    }


}
