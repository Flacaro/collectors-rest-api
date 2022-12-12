package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/private")
public class PCollectionController {

    private final CollectionService collectionService;

    private final SerializeWithView serializeWithView;

    public PCollectionController(CollectionService collectionService, SerializeWithView serializeWithView) {
        this.collectionService = collectionService;
        this.serializeWithView = serializeWithView;
    }


    @GetMapping(value = "/collections", produces = "application/json")
    public ResponseEntity<String> getAllPrivateCollections(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view,
            Authentication authentication
    ) {
        var collectionsByParameters = new ArrayList<CollectionEntity>();
        var collections = collectionService.getAllPersonalCollections(page, size, authentication);
        try {
            if (collections.isPresent()) {
                    if (name == null && type == null) {
                    return getStringResponseEntity(view, collections);
                } else {
                    var result = this.collectionService.getCollectionsByParameters(name, type);
                    if (result.isPresent()) {
                        for (CollectionEntity collection : result.get()) {
                            if (collections.get().contains(collection)) {
                                collectionsByParameters.add(collection);
                            }
                        }
                        return getStringResponseEntity(view, Optional.of(collectionsByParameters));
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }



    @GetMapping(value = "/collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getAllPrivateCollections(
            @RequestParam(required = false) String view,
            @PathVariable Long collectionId,
            Authentication authentication
    ) {
        var collection = collectionService.getCollectionById(collectionId, authentication);
            try {
                if (collection.isPresent()) {
                return getStringResponseEntityCollection(view, collection);
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/collections", produces = "application/json")
    public ResponseEntity<String> saveCollectorCollection(
            @RequestBody CollectionEntity collection,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var savedCollection = collectionService.saveCollectorCollection(collection, authentication);
            try {
                if (savedCollection.isPresent()) {
                return getStringResponseEntityCollection(view, savedCollection);
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    //vedere perche non elimina la collection se e' nei preferiti
    @DeleteMapping("/collections/{collectionId}")
    public ResponseEntity<CollectionEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        this.collectionService.deleteCollectorCollectionById(authentication, collectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value ="/collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> updateCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionEntity collection,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var optionalCollection = this.collectionService.updateCollectorCollectionById(authentication, collectionId, collection);
            try {
                if(optionalCollection.isPresent()){
                return getStringResponseEntityCollection(view, optionalCollection);
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/collections/{collectionId}/share")
    public ResponseEntity<String> shareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds,
            @RequestParam(required = false) String view,
            Authentication authentication
    ) {
        var optionalCollection = this.collectionService.shareCollection(collectorIds, collectionId,authentication);
       try {
           if (optionalCollection.isPresent()) {
               return getStringResponseEntityCollection(view, optionalCollection);
           }
       } catch (JsonProcessingException e) {
           return ResponseEntity.internalServerError().build();
       }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/collections/{collectionId}/unshare")
    public ResponseEntity<CollectionEntity> unshareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds,
            Authentication authentication
    ) {
        this.collectionService.unshareCollection(collectorIds, collectionId, authentication);
        return ResponseEntity.ok().build();
    }


    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<List<CollectionEntity>> publicCollections) throws JsonProcessingException {
        if (publicCollections.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, publicCollections.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, publicCollections.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntityCollection(@RequestParam(required = false) String view, Optional<CollectionEntity> publicCollection) throws JsonProcessingException {
        if (publicCollection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, publicCollection.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, publicCollection.get()));
        }
    }
}

