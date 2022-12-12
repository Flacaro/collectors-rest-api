package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.DiskService;

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
            @RequestParam(required = false) String view
    ) {
        try {
            // merge query params with AND string

            var result = this.diskService.getDiskByParameters(year, format, author, genre, title);

//        return ResponseEntity.ok(result);
        return getStringResponseEntityDisk(view, result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

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
                if (view == null) {
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
            @RequestParam(required = false) String view
    ) {
        try {
            if (year == null && format == null && author == null && genre == null && title == null) {
                var disks = diskService.getDisksFromPublicCollection(collectorId, collectionId);
                return getStringResponseEntityDisk(view, disks);
            }
            if (year != null) {
                var disksByYear = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, year, null, null, null, null, page, size);
                return getStringResponseEntityDisk(view, disksByYear);
            }

            if (format != null) {
                var disksByFormat = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, null, format, null, null, null, page, size);
                return getStringResponseEntityDisk(view, disksByFormat);
            }

            if (author != null) {
                var disksByAuthor = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, null, null, author, null, null, page, size);
                return getStringResponseEntityDisk(view, disksByAuthor);
            }

            if (genre != null) {
                var disksByAuthor = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, null, null, null, genre, null, page, size);
                return getStringResponseEntityDisk(view, disksByAuthor);
            }
            if (title != null) {
                var disksByAuthor = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, null, null, null, null, title, page, size);
                return getStringResponseEntityDisk(view, disksByAuthor);
            }
                var disksByYearFormatAuthorGenreTitle = diskService.getDisksByYearFormatAuthorGenreTitleFromPublicCollectionById(collectionId, year, format, author, genre, title, page, size);
                return getStringResponseEntityDisk(view, disksByYearFormatAuthorGenreTitle);


            } catch(JsonProcessingException e){
                return ResponseEntity.internalServerError().build();
            }

        }


        @GetMapping(value = "collectors/{collectorId}/collections/{collectionId}/disks/{diskId}", produces = "application/json")
        public ResponseEntity<String> getDiskFromPublicCollection (
                @PathVariable("collectorId") Long collectorId,
                @PathVariable("collectionId") Long collectionId,
                @PathVariable("diskId") Long diskId,
                @RequestParam(required = false) String view
    ){
            try {
                var disk = diskService.getDiskByIdFromPublicCollection(collectorId, collectionId, diskId);
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
