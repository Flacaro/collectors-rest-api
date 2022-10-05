package org.univaq.collectors.controllers;
import java.util.List;
import java.util.Optional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;


@RestController
@RequestMapping("/collectors")
public class CollectorsController {

    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;
    private final CollectionService collectionService;
    // Dependency Injection
    // La lista delle dipendenze verra' fornita da Spring Boot
    // Non c'e' quindi bisogno di fare
    // this.collectorsRepository = new CollectorsRepository()
    public CollectorsController(CollectorService collectorService, CollectionService collectionService) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
    }

    
    @GetMapping
    public ResponseEntity<List<CollectorEntity>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page, 
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> email
    ) {
        return ResponseEntity.ok(this.collectorService.getAll(page, size, email));
    }

    @GetMapping("/{collectorId}")
    public ResponseEntity<Optional<CollectorEntity>> getCollectorById(@PathVariable("collectorId") Long id) {
        return ResponseEntity.ok(this.collectorService.getCollectorById(id));
    }

    // @GetMapping("/{collectorId}/collections")
    // public ResponseEntity<List<CollectionEntity>> getCollectorsCollectionByCollectionId(@PathVariable("collectorId") Long id) {
    //     return ResponseEntity.ok(this.collectionService.getCollectorCollections(id));
    // }


}
