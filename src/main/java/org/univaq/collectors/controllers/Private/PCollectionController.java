package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/collections")
public class PCollectionController {

    private final CollectionService collectionService;

    private final ObjectMapper objectMapper;

    public PCollectionController(CollectionService collectionService, ObjectMapper objectMapper) {
        this.collectionService = collectionService;
        this.objectMapper = objectMapper;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<String> saveCollectorCollection(
            @RequestBody CollectionEntity collection,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var result = this.collectionService.saveCollectorCollection(collection, authentication);

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

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        this.collectionService.deleteCollectorCollectionById(authentication, collectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value ="/{collectionId}", produces = "application/json")
    public ResponseEntity<String> updateCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionEntity collection,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var result = this.collectionService.updateCollectorCollectionById(authentication, collectionId, collection);
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


}

