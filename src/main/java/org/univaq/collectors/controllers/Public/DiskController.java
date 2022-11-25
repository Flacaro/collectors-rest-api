package org.univaq.collectors.controllers.Public;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.DiskService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping ("public/disks")
public class DiskController {

    private final DiskService diskService;

    public DiskController(DiskService diskService){
        this.diskService = diskService;
    }

    //prendo i dischi pubblici
    @GetMapping
    public ResponseEntity<List<DiskEntity>>getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> title
    ){
        return ResponseEntity.ok(this.diskService.getAll(page, size, title));
    }

//dal disco vedo se è pubblico e poi se è pubblico prendo
}
