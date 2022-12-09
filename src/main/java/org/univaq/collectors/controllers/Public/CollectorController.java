package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.univaq.collectors.services.TrackService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("public/collectors")
public class CollectorController {

    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;
    private final CollectionService collectionService;
    private final DiskService diskService;

    private final TrackService trackService;
    private final ObjectMapper objectMapper;

    public CollectorController(CollectorService collectorService, CollectionService collectionService, DiskService diskService, TrackService trackService, ObjectMapper objectMapper) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.diskService = diskService;
        this.trackService = trackService;
        this.objectMapper = objectMapper;
    }


    @GetMapping
    public ResponseEntity<List<CollectorEntity>> getAllCollectors(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam() Optional<String> email
    ) {
        return ResponseEntity.ok(this.collectorService.getAllCollectors(page, size));
    }

    @GetMapping("/{collectorId}")
    public ResponseEntity<CollectorEntity> getCollectorById(
            @PathVariable Long collectorId
    ) {
        return ResponseEntity.ok(this.collectorService.getById(collectorId));
    }

    @GetMapping("/{email}")
    public ResponseEntity<CollectorEntity> getCollectorByEmail(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(this.collectorService.getCollectorByEmail(email));
    }

    @GetMapping("/{username}")
    public ResponseEntity<CollectorEntity> getCollectorByUsername(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(this.collectorService.getCollectorByUsername(username));
    }

    @GetMapping(value ="/{collectorId}/collections", produces = "application/json")
    public ResponseEntity<String> getPublicCollectorCollections(
            @PathVariable("collectorId") Long collectorId,
            @RequestParam(required = false) String view
    ) {
        var result = this.collectionService.getPublicCollectionsByCollectorId(collectorId);
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


    @GetMapping("/{collectorId}/collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<Optional<DiskEntity>> getDiskFromPublicCollection(
        @PathVariable ("collectorId") Long collectorId,
        @PathVariable ("collectionId") Long collectionId,
        @PathVariable ("diskId") Long diskId
    ){
        var result = this.diskService.getDiskByIdFromPublicCollectionOfACollector(collectorId, collectionId, diskId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value ="/{collectorId}/collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getPublicCollectorCollectionById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String view
    ) {
        var result = this.collectorService.getPublicCollectorCollectionById(collectorId, collectionId);
        try {
            // Aggiungendo il query parameter alla richiesta, ?view=private
            // si ottiene la vista privata, altrimenti la pubblica
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

    @GetMapping(value ="/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPublicTracks(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String view
    ) {
        var result = this.trackService.getTracksFromPublicCollectionOfCollector(collectorId, collectionId, diskId);
        try {
            // Aggiungendo il query parameter alla richiesta, ?view=private
            // si ottiene la vista privata, altrimenti la pubblica
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

    @GetMapping(value ="/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks/{trackId}", produces = "application/json")
    public ResponseEntity<String> getPublicTrack(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @PathVariable("trackId") Long trackId,
            @RequestParam(required = false) String view
    ) {
        var optionalTrack = this.trackService.getTrackFromPublicCollectionOfCollector(collectorId, collectionId, diskId, trackId);
        var result = optionalTrack.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        try {
            // Aggiungendo il query parameter alla richiesta, ?view=private
            // si ottiene la vista privata, altrimenti la pubblica
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
