package org.univaq.collectors.controllers.Public;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.DiskService;


@RestController
@RequestMapping("public/collections")
public class CollectionController {

    private final CollectionService collectionService;
    
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

   
     @GetMapping
     public ResponseEntity<List<CollectionEntity>> getAll(
//         @RequestParam(required = false, defaultValue = "0") Integer page,
//         @RequestParam(required = false, defaultValue = "10") Integer size,
         @RequestParam() Optional<String> name
     ) {
            return ResponseEntity.ok(this.collectionService.getAll(name));
     }

//     @GetMapping("/{collectionId}/disks")
//        public ResponseEntity<List<DiskEntity>> getDisksOfPublicCollection(
//                @PathVariable("collectionId") Long collectionId
//        ) {
//            var result = this.collectionService.getPublicDisks(collectionId);
//            return ResponseEntity.ok(result);
//
//        }



}
