package org.univaq.collectors.controllers;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.Collector;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.security.CustomUserDetails;
import org.univaq.collectors.services.PaginationService;

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
    public ResponseEntity<List<Collector>> getAll(
        @RequestParam(required = false) Integer page, 
        @RequestParam(required = false) Integer size,
        Principal principal
    ) {
        logger.info("Principal email: {}", principal.getName());

        var pageRequest = PaginationService.getPageRequestOrDefault(page, size);

        Page<Collector> collectors = collectorsRepository.findAll(pageRequest);
        
        return ResponseEntity.ok(collectors.getContent());
    }


}
