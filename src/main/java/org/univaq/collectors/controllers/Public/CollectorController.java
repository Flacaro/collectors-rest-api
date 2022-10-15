package org.univaq.collectors.controllers.Public;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("public/collectors")
public class CollectorController {

    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;
    private final CollectionService collectionService;
    private final DiskService diskService;

    public CollectorController(CollectorService collectorService, CollectionService collectionService, DiskService diskService) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.diskService = diskService;
    }


    @GetMapping
    public ResponseEntity<List<CollectorEntity>> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam() Optional<String> email
    ) {
        return ResponseEntity.ok(this.collectorService.getAll(page, size, email));
    }

    @GetMapping("/{collectorId}/collections")
    public ResponseEntity<List<CollectionEntity>> getCollectorCollections(@PathVariable("collectorId") Long collectorId) {
        var result = this.collectionService.getPublicCollectionsByCollectorId(collectorId);
        return ResponseEntity.ok(result);
    }



    //aggiungi nuovo disco in collection
//    @PostMapping("/{collectorId}/collections/{collectionId}/disks")
//    public ResponseEntity<DiskEntity> saveDisk(
//            @PathVariable("collectorId") Long collectorId,
//            @PathVariable("collectionId") Long collectionId,
//            @RequestBody DiskEntity disk
//    ) {
//        var optionalDisk = this.diskService.saveDisk(disk, collectionId, collectorId);
//        return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    //elimina disco dalla colezione 
//    @DeleteMapping("/{collectorId}/collections/{collectionId}/disks/{diskId}")
//    public ResponseEntity<DiskEntity> deleteDiskById(
//        @PathVariable ("collectorId") Long collectorId,
//        @PathVariable ("collectionId") Long collectionId,
//        @PathVariable ("diskId") Long diskId
//    ){
//         this.diskService.deleteDiskById( collectionId, collectorId, diskId);
//        return ResponseEntity.ok().build();
//    }

    //visualizza disco specifico
    @GetMapping("/{collectorId}/collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<Optional<DiskEntity>> getDiskId(
        @PathVariable ("collectorId") Long collectorId,
        @PathVariable ("collectionId") Long collectionId,
        @PathVariable ("diskId") Long diskId
    ){
        var result = this.diskService.getDiskId(collectionId, collectorId, diskId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{collectorId}/collections/{collectionId}")
    public ResponseEntity<Optional<CollectionEntity>> getCollectorCollectionById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId
    ) {
        var result = this.collectionService.getCollectorCollectionById(collectorId, collectionId);
        return ResponseEntity.ok(result);
    }



}
