package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;
import org.univaq.collectors.services.TrackService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/collections/{collectionId}/disks/{diskId}/tracks")
public class PTrackController {

    private final TrackService trackService;
    private final DiskService diskService;
    private final CollectorService collectorService;
    private final CollectionService collectionService;

    //costruttore
    public PTrackController(TrackService trackService, DiskService diskService, CollectorService collectorService, CollectionService collectionService) {
        this.trackService = trackService;
        this.diskService = diskService;
        this.collectorService = collectorService;
        this.collectionService = collectionService;
    }

    //metodi
    @GetMapping
    public ResponseEntity<List<TrackEntity>> getPersonalTracksOfDisk(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication
    ) {
        var optionalTracks = this.trackService.getPersonalTracksFromDisk(diskId, collectionId, authentication);
        return ResponseEntity.ok(optionalTracks);
    }

    @PostMapping
    public ResponseEntity<TrackEntity> saveTrack(
            @RequestBody TrackEntity track,
            @PathVariable("diskId") Long diskId,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        var optionalTrack = this.trackService.saveTrack(track, diskId, collectionId, authentication);
        return optionalTrack.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<TrackEntity> deleteTrackById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable ("trackId") Long trackId,
            Authentication authentication
    ){
        this.trackService.deleteTrack (trackId, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{trackId}")
    public ResponseEntity<TrackEntity> updateTrackById(
            @RequestBody TrackEntity track, //contiene dati ma non rappresenta una traccia nel db trackDTo
            @PathVariable ("trackId") Long trackId,
            @PathVariable ("diskId") Long diskId,
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication
    ){
        this.trackService.updateTrack(track, trackId, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackEntity> getTrackById(
            @PathVariable("trackId") Long trackId,
            @PathVariable("diskId") Long diskId,
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication
    ){
        var track =this.trackService.getPersonalTrackByIdFromDiskId(trackId, diskId, collectionId, authentication);
        return ResponseEntity.of(track);
    }

}
