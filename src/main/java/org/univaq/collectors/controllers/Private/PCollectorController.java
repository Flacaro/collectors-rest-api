package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.univaq.collectors.UserView;
import org.univaq.collectors.controllers.requests.payload.FavouritePayload;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectorService;

@RestController
@RequestMapping("/collectors")
public class PCollectorController {

    private final CollectorService collectorService;
    private final ObjectMapper objectMapper;

    public PCollectorController(CollectorService collectorService, ObjectMapper objectMapper) {
        this.collectorService = collectorService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/collections",produces = "application/json")
    public ResponseEntity<String> getPersonalCollections(
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var collections = this.collectorService.getPersonalCollections(authentication);
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

    @GetMapping("/profile")
    public ResponseEntity<CollectorEntity> getCollector(
            Authentication authentication
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        return ResponseEntity.ok(collector);
    }

    @GetMapping(value ="/favourites", produces = "application/json")
    public ResponseEntity<String> getFavouritesCollections(
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var favourites = this.collectorService.getFavouritesCollections(authentication);
        try {
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(favourites)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(favourites)
                );
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/collections/favourites")
    public ResponseEntity<CollectionEntity> addCollectionToFavourites(
            @RequestBody FavouritePayload favouritePayload,
            Authentication authentication
    ) {

        this.collectorService.addCollectionInFavouritesList(authentication.getName(), favouritePayload);

        return ResponseEntity.ok().build();
    }


}
