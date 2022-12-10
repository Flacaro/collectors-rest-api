package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;
import org.univaq.collectors.services.TrackService;

@RestController
@RequestMapping("/collections/{collectionId}/disks/{diskId}/tracks")
public class PTrackController {

    private final TrackService trackService;
    private final DiskService diskService;
    private final CollectorService collectorService;
    private final CollectionService collectionService;
    private final ObjectMapper objectMapper;

    //costruttore
    public PTrackController(TrackService trackService, DiskService diskService, CollectorService collectorService, CollectionService collectionService, ObjectMapper objectMapper) {
        this.trackService = trackService;
        this.diskService = diskService;
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.objectMapper = objectMapper;
    }

    //metodi
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getPersonalTracksOfDisk(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var optionalTracks = this.trackService.getPersonalTracksFromDisk(diskId, collectionId, authentication);
        var result = optionalTracks.get();
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<String> saveTrack(
            @RequestBody TrackEntity track,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view

    ) {
        var optionalTrack = this.trackService.saveTrack(track, diskId, collectionId, authentication);
        // return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        var result = optionalTrack.get();
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @DeleteMapping("/{trackId}")
    public ResponseEntity<TrackEntity> deleteTrackById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            Authentication authentication
    ) {
        this.trackService.deleteTrack(trackId, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{trackId}")
    public ResponseEntity<TrackEntity> updateTrackById(
            @RequestBody TrackEntity track, //contiene dati ma non rappresenta una traccia nel db trackDTo
            @PathVariable("trackId") Long trackId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        var optionalTrack = this.trackService.updateTrack(track, trackId, diskId, collectionId, authentication);
        var result = optionalTrack.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{trackId}", produces = "application/json")
    public ResponseEntity<String> getTrackById(
            @PathVariable("trackId") Long trackId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var optionalTrack = this.trackService.getPersonalTrackByIdFromDiskId(trackId, diskId, collectionId, authentication);
        var result = optionalTrack.get();
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}