package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("public/collectors")
public class CollectorController {

    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;
    private final CollectionService collectionService;
    private final DiskService diskService;
    private final ObjectMapper objectMapper;

    public CollectorController(CollectorService collectorService, CollectionService collectionService, DiskService diskService, ObjectMapper objectMapper) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.diskService = diskService;
        this.objectMapper = objectMapper;
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
    public ResponseEntity<List<CollectionEntity>> getCollectorCollections(
            @PathVariable("collectorId") Long collectorId
    ) {
        var result = this.collectionService.getPublicCollectionsByCollectorId(collectorId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{collectorId}/collections/{collectionId}/disks/{diskId}")
    public ResponseEntity<Optional<DiskEntity>> getDiskFromPublicCollection(
        @PathVariable ("collectorId") Long collectorId,
        @PathVariable ("collectionId") Long collectionId,
        @PathVariable ("diskId") Long diskId
    ){
        var result = this.diskService.getDiskByIdFromPublicCollection(collectionId, collectorId, diskId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value ="/{collectorId}/collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getCollectorCollectionById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String view
    ) {
        var result = this.collectionService.getCollectorCollectionById(collectorId, collectionId);
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
