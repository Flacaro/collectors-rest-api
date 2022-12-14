package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.controllers.requests.payload.FavouriteDiskPayload;
import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/private")
public class PCollectorController {

    private final CollectorService collectorService;

    private final CollectionService collectionService;
    private final DiskService diskService;
    private final SerializeWithView serializeWithView;

    public PCollectorController(CollectorService collectorService, CollectionService collectionService, DiskService diskService, SerializeWithView serializeWithView) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
        this.diskService = diskService;
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


    @GetMapping(value = "/collectors/favourites", produces = "application/json")
    public ResponseEntity<String> getFavouritesCollections(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
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
                    for (CollectionEntity collection : result) {
                        if (favourites.get().contains(collection)) {
                            favouritesByParameters.add(collection);
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

    @GetMapping(value = "/collectors/disks/favourites", produces = "application/json")
    public ResponseEntity<String> getFavouritesDisks(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String band,
            @RequestParam(required = false) String view

    ) {
        var favouritesByParameters = new ArrayList<DiskEntity>();
        var favouritesDisk = this.collectorService.getFavouritesDisks(authentication);
        try {
            if (favouritesDisk.isPresent()) {
                if (year == null && format == null && author == null && genre == null && title == null && artist == null && band == null) {
                    return getStringResponseEntityDisks(view, favouritesDisk);
                } else {
                    var result = this.diskService.getDisksByParameters(year, format, author, genre, title, artist, band);
                    for (DiskEntity disk : result) {
                        if (favouritesDisk.get().contains(disk)) {
                            favouritesByParameters.add(disk);
                        }
                        return getStringResponseEntityDisks(view, Optional.of(favouritesByParameters));
                    }
                }
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/collectors/collections/{collectionId}/disks/favourites")
    public ResponseEntity<DiskEntity> addDiskInFavouritesDiskList(
            Authentication authentication,
            @RequestBody FavouriteDiskPayload favouriteDiskPayload,
            @PathVariable("collectionId") Long collectionId

    ) {
        this.collectorService.addDiskInFavouritesDiskList(authentication.getName(), collectionId, favouriteDiskPayload);
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

    private ResponseEntity<String> getStringResponseEntityDisks(@RequestParam(required = false) String view, Optional<List<DiskEntity>> collectors) throws JsonProcessingException {
        if (collectors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collector found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PRIVATE, collectors.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PUBLIC, collectors.get()));
        }
    }
}
