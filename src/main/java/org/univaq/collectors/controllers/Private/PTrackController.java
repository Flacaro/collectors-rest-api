package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;
import org.univaq.collectors.services.TrackService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/collections/{collectionId}/disks/{diskId/tracks}")
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
    public ResponseEntity<List<TrackEntity>> getTracksofDisk(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Principal principal
    ){
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var collection = this.collectionService.getCollectorCollectionById(collector.getId(),collectionId);
        if (collection.isPresent()){
            var disk = this.diskService.getPersonalDisksFromCollection(collection.get().getId());
            var result = this.trackService.getPersonalTrackFromDisk()
        }

    }

}





/*
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName()); //servizio che prende collezionista dal email
        var collection = this.collectionService.getCollectorCollectionById(collector.getId(), collectionId); //collezioni mie personali tramite query
        if (collection.isPresent()) {
            var result = this.diskService.getPersonalDisksFromCollection(collection.get().getId());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DiskEntity> saveDisk(
            @RequestBody DiskEntity disk,
            @PathVariable("collectionId") Long collectionId,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());

        var optionalDisk = this.diskService.saveDisk(disk, collectionId, collector.getId());
        return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{diskId}")
    public ResponseEntity<CollectionEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        this.diskService.deleteDiskById(collector.getId(), collectionId, diskId);
        return ResponseEntity.ok().build();
    }
}
//update disco: aggiorna dati disco