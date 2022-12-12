package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.DiskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping ("public/")
public class DiskController {

    private final DiskService diskService;
    private final SerializeWithView serializeWithView;

    public DiskController(DiskService diskService, SerializeWithView serializeWithView) {
        this.diskService = diskService;
        this.serializeWithView = serializeWithView;
    }



//vedere come si fa la paginazione
    @GetMapping(value = "collections/{collectionId}/disks", produces = "application/json")
    public ResponseEntity<String> getDisksFromPublicCollection(
            @PathVariable Long collectionId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String view
    ) {
        try {
            var disksOfPublicCollectionByParameters = new ArrayList<DiskEntity>();
            var disks = diskService.getDisksFromPublicCollection(collectionId);
            if(disks.isPresent()) {
                if (year == null && format == null && author == null && genre == null && title == null) {
                    return getStringResponseEntityDisk(view, disks);
                } else {
                    var result = this.diskService.getDisksByParameters(year, format, author, genre, title, artist, band);
                    if (result.isPresent()) {
                        for (DiskEntity disk : result.get()) {
                            if (disks.get().contains(disk)) {
                                disksOfPublicCollectionByParameters.add(disk);
                            }
                        }
                        return getStringResponseEntityDisk(view, Optional.of(disksOfPublicCollectionByParameters));
                    }
                }
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();

    }


    @GetMapping(value = "collections/{collectionId}/disks/{diskId}", produces = "application/json")
    public ResponseEntity<String> getDiskFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @RequestParam(required = false) String view
    ) {
        try {
            var disk = diskService.getDiskByIdFromPublicCollection(collectionId, diskId);
            if (disk.isPresent()) {
                if (view != null && view.equals("public")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, disk));
                } else {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, disk));
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "collectors/{collectorId}/collections/{collectionId}/disks", produces = "application/json")
    public ResponseEntity<String> getDisksFromPublicCollection(
            @PathVariable ("collectorId") Long collectorId,
            @PathVariable ("collectionId") Long collectionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String view
    ) {
        var disksOfPublicCollectionByParameters = new ArrayList<DiskEntity>();
        var disks = diskService.getDisksFromPublicCollection(collectorId, collectionId);
        try {
            if(disks.isPresent()) {
                if (year == null && format == null && author == null && genre == null && title == null) {
                    return getStringResponseEntityDisk(view, disks);
                } else {
                    var result = this.diskService.getDisksByParameters(year, format, author, genre, title, artist, band);
                    if (result.isPresent()) {
                        for (DiskEntity disk : result.get()) {
                            if (disks.get().contains(disk)) {
                                disksOfPublicCollectionByParameters.add(disk);
                            }
                        }
                        return getStringResponseEntityDisk(view, Optional.of(disksOfPublicCollectionByParameters));
                        }
                    }
                }
            } catch(JsonProcessingException e){
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok().build();

        }


        @GetMapping(value = "collectors/{collectorId}/collections/{collectionId}/disks/{diskId}", produces = "application/json")
        public ResponseEntity<String> getDiskFromPublicCollection (
                @PathVariable("collectorId") Long collectorId,
                @PathVariable("collectionId") Long collectionId,
                @PathVariable("diskId") Long diskId,
                @RequestParam(required = false) String view
    ){
        var disk = diskService.getDiskByIdFromPublicCollection(collectorId, collectionId, diskId);
                    try {
                        if (disk.isPresent()) {
                    if (view != null && view.equals("public")) {
                        return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, disk));
                    } else {
                        return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, disk));
                    }
                }
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return ResponseEntity.ok().build();
    }



    private ResponseEntity<String> getStringResponseEntityDisk(@RequestParam(required = false) String view, Optional<List<DiskEntity>> publicDisks) throws JsonProcessingException {
        if (publicDisks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PRIVATE, publicDisks.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.DISK, SerializeWithView.ViewType.PUBLIC, publicDisks.get()));
        }
    }

}
