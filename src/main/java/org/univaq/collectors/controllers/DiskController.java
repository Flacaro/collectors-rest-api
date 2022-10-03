package org.univaq.collectors.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.Disk;
import org.univaq.collectors.services.DiskService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping ("/disks")
public class DiskController {

    private final DiskService diskService;

    public DiskController(DiskService diskService){
        this.diskService = diskService;
    }

    @GetMapping
    public ResponseEntity<List<Disk>>getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> title
    ){
        return ResponseEntity.ok(this.diskService.getAll(page, size, title));
    }
}


