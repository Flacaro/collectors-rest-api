package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/private")
public class PCollectorController {

    private final CollectorService collectorService;

    private final CollectionService collectionService;
    private final SerializeWithView serializeWithView;

    public PCollectorController(CollectorService collectorService, CollectionService collectionService, SerializeWithView serializeWithView) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.serializeWithView = serializeWithView;
    }


    @GetMapping(value = "/collectors/profile", produces = "application/json")
    public ResponseEntity<String> getCollector(
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
            try {
                if (collector.isPresent()) {
                    return getStringResponseEntityCollector(view, collector);
                }
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping(value ="/collectors/favourites", produces = "application/json")
    public ResponseEntity<String> getFavouritesCollections(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false,  defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view
    ) {
        var favouritesByParameters = new ArrayList<CollectionEntity>();
        var favourites = this.collectorService.getFavouritesCollections(authentication);
            try {
                if (favourites.isPresent()) {
                if (name == null && type == null) {
                    return getStringResponseEntity(view, favourites);
                } else {
                    var result = this.collectionService.getCollectionsByParameters(name, type);
                    if (result.isPresent()) {
                        for (CollectionEntity collection : result.get()) {
                            if (favourites.get().contains(collection)) {
                                favouritesByParameters.add(collection);
                            }
                        }
                        return getStringResponseEntity(view, Optional.of(favouritesByParameters));
                    }
                }
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/collectors/collections/favourites")
    public ResponseEntity<CollectionEntity> addCollectionToFavourites(
            @RequestBody FavouritePayload favouritePayload,
            Authentication authentication
    ) {

        this.collectorService.addCollectionInFavouritesList(authentication.getName(), favouritePayload);

        return ResponseEntity.ok().build();
    }



    private ResponseEntity<String> getStringResponseEntity(@RequestParam(required = false) String view, Optional<List<CollectionEntity>> collectors) throws JsonProcessingException {
        if (collectors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collector found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PRIVATE, collectors.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PUBLIC, collectors.get()));
        }
    }

    private ResponseEntity<String> getStringResponseEntityCollector(@RequestParam(required = false) String view, Optional<CollectorEntity> collector) throws JsonProcessingException {
        if (collector.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collector found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PRIVATE, collector.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PUBLIC, collector.get()));
        }
    }

}
