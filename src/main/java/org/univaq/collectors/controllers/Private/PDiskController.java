package org.univaq.collectors.controllers.Private;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/collections/{collectionId}/disks")
public class PDiskController {

    private final DiskService diskService;
    private final CollectionService collectionService;
    private final CollectorService collectorService;

    public PDiskController(DiskService diskService, CollectionService collectionService, CollectorService collectorService) {
        this.diskService = diskService;
        this.collectionService = collectionService;
        this.collectorService = collectorService;
    }
//ritorna lista di dischi data una collezione
    @GetMapping
    public ResponseEntity<List<DiskEntity>> getDisksOfCollection(
            @PathVariable("collectionId") Long collectionId,
            Principal principal
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
    public ResponseEntity<DiskEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        this.diskService.deleteDiskById(collector.getId(), collectionId, diskId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{diskId}")
    public ResponseEntity<DiskEntity> getDiskOfCollection(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication
    ) {
        var disk = this.diskService.getPersonalDiskByIdFromCollectionId(diskId, collectionId, authentication);

        return ResponseEntity.of(disk);
    }
}
//update disco: aggiorna dati disco


