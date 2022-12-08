package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;

import java.util.List;

@RestController
@RequestMapping("/collectors")
public class PCollectorController {

    private final CollectorService collectorService;

    public PCollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @GetMapping("/profile")
    public ResponseEntity<CollectorEntity> getCollector(
            Authentication authentication
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        return ResponseEntity.ok(collector);
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<CollectionEntity>> getFavourites(
            Authentication authentication
    ) {
        var favourites = this.collectorService.getFavourites(authentication);
        return ResponseEntity.ok(favourites);
    }


    @PostMapping("/collections/favourites")
    public ResponseEntity<CollectionEntity> addToFavourites(
            @RequestBody FavouritePayload favouritePayload,
            Authentication authentication
    ) {

        this.collectorService.addCollectionInFavouritesList(authentication.getName(), favouritePayload);

        return ResponseEntity.ok().build();
    }


}
