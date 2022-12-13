package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/private")
public class PDiskController {

    private final DiskService diskService;

    private final CollectorService collectorService;
    private final SerializeWithView serializeWithView;

    public PDiskController(DiskService diskService, CollectorService collectorService,SerializeWithView serializeWithView) {
        this.diskService = diskService;
        this.collectorService = collectorService;
        this.serializeWithView = serializeWithView;
    }

    @GetMapping("collectors/{collectorId}/collections/{collectionId}/disks")
    public ResponseEntity<String> getDiskFromProfile(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String band,
            Authentication authentication,
            @RequestParam(required = false) String view
            ) {
        try {
            //devo controllare che il collectorId e' uguale all'id del autenticato
            var collector = collectorService.getById(collectorId);
            if(collector.isPresent()) {
                if (collector.get().getEmail().equals(authentication.getName())) {

                var disks = this.diskService.getPersonalDisksFromCollection(collectionId, authentication);
                var disksByParameters = new ArrayList<DiskEntity>();
                if (disks.isPresent()) {
                    if (title == null && author == null && format == null && year == null && genre == null && artist == null && band == null) {
                        return getStringResponseEntity(view, disks);
                    } else {
                        var diskWithParameters = this.diskService.getDisksByParameters(year, format, author, genre, title, artist, band);
                        if (diskWithParameters.isPresent()) {
                            for (DiskEntity disk : diskWithParameters.get()) {
                                if (disks.get().contains(disk)) {
                                    disksByParameters.add(disk);
                                }
                            }
                            return getStringResponseEntity(view, Optional.of(disksByParameters));
                        } else {

                            return getStringResponseEntity(view, Optional.of(disksByParameters));
                        }
                    }
                }
                }
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();

    }

    @GetMapping("collectors/{collectorId}/collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<String> getDiskFromProfile(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        try {
            //devo controllare che il collectorId e' uguale all'id del autenticato
            var collector = collectorService.getById(collectorId);
            if(collector.isPresent()) {
                if (collector.get().getEmail().equals(authentication.getName())) {
                    var disk = this.diskService.getPersonalDiskByIdFromCollectionId(diskId, collectionId, authentication);
                    if (disk.isPresent()) {
                        return getStringResponseEntityDisk(view, disk);
                    }
                    }
                }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "collections/{collectionId}/disks" , produces = "application/json")
    public ResponseEntity<String> getDisksOfCollection(
            @PathVariable ("collectionId") Long collectionId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String band,
            Authentication authentication,
            @RequestParam(required = false) String view
    ){
        try {

            var disks = this.diskService.getPersonalDisksFromCollection(collectionId, authentication);
            var disksByParameters = new ArrayList<DiskEntity>();
            if (disks.isPresent()) {
                if(title == null && author == null && format == null && year == null && genre == null && artist == null && band == null) {
                    return getStringResponseEntity(view, disks);
                } else {
                    var diskWithParameters = this.diskService.getDisksByParameters(year, format, author, genre, title, artist, band);
                    if (diskWithParameters.isPresent()) {
                        for (DiskEntity disk : diskWithParameters.get()) {
                            if (disks.get().contains(disk)) {
                                disksByParameters.add(disk);
                            }
                        }
                        return getStringResponseEntity(view, Optional.of(disksByParameters));
                    } else {

                        return getStringResponseEntity(view, Optional.of(disksByParameters));
                    }
                }
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


   //ma co la post serve la view?
    @PostMapping(value = "collections/{collectionId}/disks", produces = "application/json")
    public ResponseEntity<String> saveDisk(
            Authentication authentication,
            @RequestBody DiskEntity disk,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String view
    ) {
        try {
            var optionalDisk = this.diskService.saveDisk(disk, collectionId, authentication);
            if (optionalDisk.isPresent()) {
                return getStringResponseEntityDisk(view, optionalDisk);
                }
            }catch(JsonProcessingException e){
                return ResponseEntity.internalServerError().build();
            }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<DiskEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication
    ) {
        this.diskService.deleteDiskById(authentication, collectionId, diskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "collections/{collectionId}/disks/{diskId}", produces = "application/json")
    public ResponseEntity<String> getDiskOfCollection(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        try {
            var disk = this.diskService.getPersonalDiskByIdFromCollectionId(diskId, collectionId, authentication);
            if (disk.isPresent()) {
                return getStringResponseEntityDisk(view, disk);
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();

    }



    @PutMapping(value = "collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<DiskEntity> updateDiskById(
            @RequestBody DiskEntity disk,
            @PathVariable ("diskId")  Long diskId,
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication
    ) {
        var updatedDisk = this.diskService.updateDisk(disk, diskId, collectionId, authentication);
        return updatedDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<List<DiskEntity>> disks) throws JsonProcessingException {
        if (disks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No disks found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, disks.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, disks.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntityDisk(@RequestParam(required = false) String view, Optional<DiskEntity> disk) throws JsonProcessingException {
        if (disk.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No disk found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, disk.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, disk.get()));
        }
    }
}

