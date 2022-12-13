package org.univaq.collectors.controllers.Public;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;


@RestController
@RequestMapping("/public")
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
        var collectionsByParameters = new ArrayList<CollectionEntity>();
        var publicCollections = collectionService.getAllPublicCollections(page, size);
            try {
                if (publicCollections.isPresent()) {
                    if (name == null && type == null) {
                        return getStringResponseEntity(view, publicCollections);
                    } else {
                        var result = this.collectionService.getCollectionsByParameters(name, type);
                            for (CollectionEntity collection : result) {
                                if (publicCollections.get().contains(collection)) {
                                    collectionsByParameters.add(collection);

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


    @GetMapping(value = "collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getPublicCollectionById(
            @PathVariable Long collectionId,
            @RequestParam(required = false) String view
    ) {
        try {
            var collection = collectionService.getPublicCollectionById(collectionId);
            if (collection.isEmpty()) {
                return getStringResponseEntityCollection(view, collection);
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value ="collectors/{collectorId}/collections", produces = "application/json")
    public ResponseEntity<String> getPublicCollectorCollections(
            @PathVariable("collectorId") Long collectorId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view
    ) {
        try {
            var publicCollectionsOfCollectorByParameters = new ArrayList<CollectionEntity>();
            var collections = collectionService.getPublicCollections(collectorId);
            if(collections.isPresent()) {
                if (name == null && type == null) {
                    return getStringResponseEntity(view, collections);
                } else {
                    var result = this.collectionService.getCollectionsByParameters(name, type);
                        for (CollectionEntity collection : result) {
                                if (collections.get().contains(collection)) {
                                    publicCollectionsOfCollectorByParameters.add(collection);

                                }

                    }
                    return getStringResponseEntity(view, Optional.of(publicCollectionsOfCollectorByParameters));
                }
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value ="collectors/{collectorId}/collections/{collectionId}", produces = "application/json")
    public ResponseEntity<String> getPublicCollectorCollectionById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId,
            @RequestParam(required = false) String view
    ) {
        try {
            var collection = this.collectionService.getPublicCollectionById(collectorId, collectionId);
            if (collection.isEmpty()) {
                return getStringResponseEntityCollection(view, collection);
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }




    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<List<CollectionEntity>> publicCollections) throws JsonProcessingException {
        if (publicCollections.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No public collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, publicCollections.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, publicCollections.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntityCollection(@RequestParam(required = false) String view, Optional<CollectionEntity> publicCollection) throws JsonProcessingException {
        if (publicCollection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No public collection found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PRIVATE, publicCollection.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTION, SerializeWithView.ViewType.PUBLIC, publicCollection.get()));
        }
    }



}






