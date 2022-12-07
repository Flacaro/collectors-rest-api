package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/collections")
public class PCollectionController {

    private final CollectorService collectorService;
    private final CollectionService collectionService;

    private final ObjectMapper objectMapper;

    public PCollectionController(CollectorService collectorService, CollectionService collectionService, ObjectMapper objectMapper) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.objectMapper = objectMapper;
    }
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getPersonalCollections(
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {

        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        var collections = this.collectorService.getPersonalCollections(collector.getId(), authentication);

        try {
            // Aggiungendo il query parameter alla richiesta, ?view=private
            // si ottiene la vista privata, altrimenti la pubblica
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(collections)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(collections)
                );
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }

    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<String> saveCollectorCollection(
            @RequestBody CollectionEntity collection,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        var result = this.collectionService.saveCollectorCollection(collection, collector.getId());
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


    @DeleteMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        this.collectionService.deleteCollectorCollectionById(collector.getId(), collectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> updateCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionEntity collection,
            Authentication authentication
    ) {
        this.collectionService.updateCollectorCollectionById(authentication, collectionId, collection);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{collectionId}/share")
    public ResponseEntity<CollectionEntity> shareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds,
            Authentication authentication
    ) {
        var result = this.collectionService.shareCollection(collectorIds, collectionId,authentication);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{collectionId}/unshare")
    public ResponseEntity<CollectionEntity> unshareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds,
            Authentication authentication
    ) {
        this.collectionService.unshareCollection(collectorIds, collectionId, authentication);
        return ResponseEntity.ok().build();
    }

    //fare like


}

