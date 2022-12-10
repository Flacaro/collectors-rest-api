package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
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


    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getAllCollectors(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String view
    ) {
        try {
        if(email == null && username == null) {
            var result = this.collectorService.getAllCollectors(page, size);
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        } else if(email != null && username == null) {
            var result = this.collectorService.getCollectorByEmail(email);
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        } else if(email == null) {
            var result = this.collectorService.getCollectorByUsername(username);
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{collectorId}")
    public ResponseEntity<CollectorEntity> getCollectorById(
            @PathVariable Long collectorId
    ) {
        return ResponseEntity.ok(this.collectorService.getById(collectorId));
    }


    //come si concatenano le query string?
    @GetMapping(value ="/{collectorId}/collections", produces = "application/json")
    public ResponseEntity<String> getPublicCollectorCollections(
            @PathVariable("collectorId") Long collectorId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view
    ) {
        try {
            if (name == null && type == null) {
                var result = this.collectionService.getPublicCollectionsByCollectorId(collectorId);
                if(result.isPresent()) {
                if ("private".equals(view)) {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Private.class).writeValueAsString(this.collectionService.getPublicCollectionsByCollectorId(collectorId))
                    );
                } else {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result.get())
                    );
                }
            }
            } else if (name != null && type == null) {
                var result = this.collectionService.getPublicCollectionOfCollectorByName(collectorId, name);
                if(result.isPresent()) {
                    if ("private".equals(view)) {
                        return ResponseEntity.ok(
                                objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result.get())
                        );
                    } else {
                        return ResponseEntity.ok(
                                objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result.get())
                        );
                    }
                }
            } else if (name == null) {
                var result = this.collectionService.getPublicCollectionsOfCollectorByType(collectorId, type);
                if (result.isPresent()) {
                    if ("private".equals(view)) {
                        return ResponseEntity.ok(
                                objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result.get())
                        );
                    } else {
                        return ResponseEntity.ok(
                                objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result.get())
                        );
                    }
                }
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
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

    @GetMapping(value = "/{collectorId}/collections/{collectionId}/disks", produces = "application/json")
    public ResponseEntity<String> getDisksFromPublicCollection(
            @PathVariable ("collectorId") Long collectorId,
            @PathVariable ("collectionId") Long collectionId,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String view
    ){
        Optional<List<DiskEntity>> optionalDisk = Optional.empty();
        try {
            if(year == null && format == null && author == null){
            optionalDisk = this.diskService.getDisksFromPublicCollectorCollection(collectorId, collectionId);
        }
        else if (year != null && format == null && author == null){
            optionalDisk = this.diskService.getDisksByYearFromPublicCollection(collectorId, collectionId, year);

        } else if (year == null && format != null && author == null){
            optionalDisk = this.diskService.getDisksByFormatFromPublicCollectionOfCollector(collectorId, collectionId, format);
            }
        if (optionalDisk.isPresent()) {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(optionalDisk.get())
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(optionalDisk.get())
                );
            }
        }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
       return ResponseEntity.ok().build();

    }



    @GetMapping(value = "/{collectorId}/collections/{collectionId}/disks/{diskId}", produces = "application/json")
    public ResponseEntity<String> getDiskFromPublicCollection(
            @PathVariable ("collectorId") Long collectorId,
            @PathVariable ("collectionId") Long collectionId,
            @PathVariable ("diskId") Long diskId,
            @RequestParam(required = false) String view
    ) {
        var optionalDisk = this.diskService.getDiskByIdFromPublicCollectionOfACollector(collectorId, collectionId, diskId);
        try {
            if(optionalDisk.isPresent()) {
                // Aggiungendo il query parameter alla richiesta, ?view=private
                // si ottiene la vista privata, altrimenti la pubblica
                if ("private".equals(view)) {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Private.class).writeValueAsString(optionalDisk.get())
                    );
                } else {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Public.class).writeValueAsString(optionalDisk.get())
                    );
                }
            }

            } catch (JsonProcessingException e) {
                return ResponseEntity.internalServerError().build();
            }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value ="/{collectorId}/collections/{collectionId}/disks/{diskId}/tracks", produces = "application/json")
    public ResponseEntity<String> getPublicTracks(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            @RequestParam(required = false) String view
    ) {
        var optionalListOfTracks = this.trackService.getTracksFromPublicCollectionOfCollector(collectorId, collectionId, diskId);
        try {
            if(optionalListOfTracks.isPresent()) {
                // Aggiungendo il query parameter alla richiesta, ?view=private
                // si ottiene la vista privata, altrimenti la pubblica
                if ("private".equals(view)) {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Private.class).writeValueAsString(optionalListOfTracks.get())
                    );
                } else {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Public.class).writeValueAsString(optionalListOfTracks.get())
                    );
                }
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
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
        try {
            if (optionalTrack.isPresent()) {
                // Aggiungendo il query parameter alla richiesta, ?view=private
                // si ottiene la vista privata, altrimenti la pubblica
                if ("private".equals(view)) {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Private.class).writeValueAsString(optionalTrack.get())
                    );
                } else {
                    return ResponseEntity.ok(
                            objectMapper.writerWithView(UserView.Public.class).writeValueAsString(optionalTrack.get())
                    );
                }

            }
            } catch(JsonProcessingException e){
                return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


}
