package org.univaq.collectors.controllers.Public;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.univaq.collectors.UserView;
import org.univaq.collectors.services.CollectionService;


@RestController
@RequestMapping("public/collections")
public class CollectionController {

    private final CollectionService collectionService;

    private final ObjectMapper objectMapper;
    
    public CollectionController(CollectionService collectionService, ObjectMapper objectMapper) {
        this.collectionService = collectionService;
        this.objectMapper = objectMapper;
    }

   
     @GetMapping(produces = "application/json")
     public ResponseEntity<String> getAllPublicCollectionsByName(
         @RequestParam(required = false, defaultValue = "0") Integer page,
         @RequestParam(required = false, defaultValue = "10") Integer size,
         @RequestParam() Optional<String> name,
         @RequestParam(required = false) String view
     ) {
        var result = this.collectionService.getAllPublicCollectionsByName(name, page, size);
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




}
