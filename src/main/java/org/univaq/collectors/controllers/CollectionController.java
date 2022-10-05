package org.univaq.collectors.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;


@RestController
@RequestMapping("/collections")
public class CollectionController {


    private final CollectorService collectorService;
    private final CollectionService collectionService;
    
    public CollectionController(CollectorService collectorService, CollectionService collectionService) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
    }

   
    @GetMapping
    public ResponseEntity<List<CollectionEntity>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page, 
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> name
    ) {
        return ResponseEntity.ok(this.collectionService.getAll(page, size, name));
    }
    
    @GetMapping("/{collectionId}")
    public ResponseEntity<Optional<CollectionEntity>> getCollectionId(@PathVariable("collectionId") Long id) {
        return ResponseEntity.ok(this.collectionService.getCollectionById(id));
    }



    
}
