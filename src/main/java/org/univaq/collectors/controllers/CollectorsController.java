package org.univaq.collectors.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.Collector;
import org.univaq.collectors.repositories.CollectorsRepository;

@RestController
@RequestMapping("/collectors")
public class CollectorsController {

    private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorsRepository collectorsRepository;

    // Dependency Injection
    // La lista delle dipendenze verra' fornita da Spring Boot
    // Non c'e' quindi bisogno di fare
    // this.collectorsRepository = new CollectorsRepository()
    public CollectorsController(CollectorsRepository collectorsRepository) {
        this.collectorsRepository = collectorsRepository;
    }

    
    @GetMapping
    public ResponseEntity<List<Collector>> getAll() {
        logger.info("Ciao");
        Page<Collector> collectors = collectorsRepository.findAll(PageRequest.of(0, 10));
        return ResponseEntity.ok(collectors.getContent());
    }

}
