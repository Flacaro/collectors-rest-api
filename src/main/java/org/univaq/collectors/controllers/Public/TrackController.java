package org.univaq.collectors.controllers.Public;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.TrackEntity;
import org.univaq.collectors.services.TrackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("public/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public ResponseEntity<List<TrackEntity>> getAllTrackByTitle(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam() Optional<String> title
    ){
        return ResponseEntity.ok(this.trackService.getAll(page, size, title));
    }

}
