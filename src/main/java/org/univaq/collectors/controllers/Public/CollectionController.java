package org.univaq.collectors.controllers.Public;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;


@RestController
@RequestMapping("public/")
public class CollectionController {

    private final CollectionService collectionService;

    private final SerializeWithView serializeWithView;


    public CollectionController(CollectionService collectionService, SerializeWithView serializeWithView) {
        this.collectionService = collectionService;
        this.serializeWithView = serializeWithView;
    }


    @GetMapping(value = "/collections",produces = "application/json")
    public ResponseEntity<String> getAllPublicCollections(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view
    ) {
        try {
            if (name == null && type == null) {
                var publicCollections = collectionService.getAllPublicCollections(page, size);
                return getStringResponseEntity(view, publicCollections);
            }

            if (name != null && type == null) {
                var publicCollectionByName = collectionService.getPublicCollectionsByNameAndType(name, null, page, size);
                return getStringResponseEntity(view, publicCollectionByName);
            }

            if (name == null) {
                var publicCollectionByType = collectionService.getPublicCollectionsByNameAndType(null, type, page, size);
                return getStringResponseEntity(view, publicCollectionByType);
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getPublicCollectionById(
            @PathVariable Long collectionId,
            @RequestParam(required = false) String view
    ) {
        try {
            var collection = collectionService.getPublicCollectionById(collectionId);
            if (collection.isEmpty()) {
                if (view != null && view.equals("private")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, collection));
                }
                ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, collection));
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


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
                var collections = this.collectionService.getPublicCollections(collectorId);
                return getStringResponseEntity(view, collections);
                }
            if (name != null && type == null) {
                var collectionsByName = this.collectionService.getPublicCollectionsByNameAndType(name, null, page, size);
                return getStringResponseEntity(view, collectionsByName);
            }
            if (name == null) {
                var collectionsByType = this.collectionService.getPublicCollectionsByNameAndType(null, type, page, size);
                return getStringResponseEntity(view, collectionsByType);
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
        try {
            var collection = this.collectionService.getPublicCollectionById(collectorId, collectionId);
            if (collection.isEmpty()) {
                if (view != null && view.equals("private")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, collection));
                }
                ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, collection));
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
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



}






