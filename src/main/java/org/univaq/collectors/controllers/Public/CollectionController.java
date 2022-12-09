package org.univaq.collectors.controllers.Public;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.UserView;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.DiskService;
import org.univaq.collectors.services.TrackService;


@RestController
@RequestMapping("public/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final DiskService diskService;

    private final TrackService trackService;

    private final ObjectMapper objectMapper;
    
    public CollectionController(CollectionService collectionService, DiskService diskService, TrackService trackService, ObjectMapper objectMapper) {
        this.collectionService = collectionService;
        this.diskService = diskService;
        this.trackService = trackService;
        this.objectMapper = objectMapper;
    }

   
     @GetMapping(produces = "application/json")
     public ResponseEntity<String> getAllPublicCollectionsByName(
         @RequestParam(required = false, defaultValue = "0") Integer page,
         @RequestParam(required = false, defaultValue = "10") Integer size,
         @RequestParam() Optional<String> name,
         @RequestParam(required = false) String view
     ) {
        var optionalListOfCollection = this.collectionService.getAllPublicCollectionsByName(name, page, size);
        if(optionalListOfCollection.isPresent()) {
            var result = optionalListOfCollection.get();
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
        } else {
            return ResponseEntity.notFound().build();
        }

     }

    @GetMapping(value = "/{collectionId}",produces = "application/json")
    public ResponseEntity<String> getPublicCollectionById(
            @PathVariable Long collectionId,
            @RequestParam(required = false) String view
    ) {
        var optionalCollection = this.collectionService.getPublicCollectionById(collectionId);
        var collection = optionalCollection.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(collection)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(collection)
                );
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping(value = "/{collectionId}/disks",produces = "application/json")
    public ResponseEntity<String> getDisksFromPublicCollection(
            @PathVariable Long collectionId,
            @RequestParam(required = false) String view
    ) {
        var optionalListOfDisks = this.diskService.getDisksFromPublicCollection(collectionId);
        var result = optionalListOfDisks.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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


    @GetMapping(value = "/{collectionId}/disks/{diskId}",produces = "application/json")
    public ResponseEntity<String> getDiskFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @RequestParam(required = false) String view
    ) {
        var optionalDisk = this.diskService.getDiskByIdFromPublicCollection(collectionId, diskId);
        var disk = optionalDisk.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(disk)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(disk)
                );
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping(value = "/{collectionId}/disks/{diskId}/tracks",produces = "application/json")
    public ResponseEntity<String> getTracksFromPublicCollection(
            @PathVariable Long collectionId,
            @PathVariable Long diskId,
            @RequestParam(required = false) String view
    ) {
        var optionalListOfTracks = this.trackService.getTracksFromPublicCollection(collectionId, diskId);
        var result = optionalListOfTracks.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

        @GetMapping(value = "/{collectionId}/disks/{diskId}/tracks/{trackId}",produces = "application/json")
        public ResponseEntity<String> getTrackFromPublicCollection(
                @PathVariable Long collectionId,
                @PathVariable Long diskId,
                @PathVariable Long trackId,
                @RequestParam(required = false) String view
    ) {
            var optionalListOfTracks = this.trackService.getTrackFromPublicCollection(collectionId, diskId, trackId);
            var result = optionalListOfTracks.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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






