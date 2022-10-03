package org.univaq.collectors.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

import ch.qos.logback.classic.Logger;

@RestDisk
@RequestMapping ("/disks")
pubblic class DisksControllers {

    private final Logger logger = LoggerFactor

}

public class DiskController {
    @RequestMapping (add = RequestMethod.Post , value="/disks")
    public void addDisk(){
        diskServices.addDisk(disk);

        }
}






    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;

    // Dependency Injection
    // La lista delle dipendenze verra' fornita da Spring Boot
    // Non c'e' quindi bisogno di fare
    // this.collectorsRepository = new CollectorsRepository()
    public CollectorsController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    
    @GetMapping
    public ResponseEntity<List<Collector>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page, 
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> email
    ) {
        return ResponseEntity.ok(this.collectorService.getAll(page, size, email));
    }

}
