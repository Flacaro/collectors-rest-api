package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import javax.swing.text.html.parser.Entity;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/collections/{collectionId}/disks")
public class PDiskController {

    private final DiskService diskService;
    private final CollectionService collectionService;
    private final CollectorService collectorService;
    private final ObjectMapper objectMapper;

    public PDiskController(DiskService diskService, CollectionService collectionService, CollectorService collectorService, ObjectMapper objectMapper) {
        this.diskService = diskService;
        this.collectionService = collectionService;
        this.collectorService = collectorService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<String> saveDisk(
            @RequestBody DiskEntity disk,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {

        var optionalDisk = this.diskService.saveDisk(disk, collectionId, authentication);
        var result = optionalDisk.get();
        //return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        try{
            if ("private".equals(view)){
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            }else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{diskId}")
    public ResponseEntity<DiskEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        this.diskService.deleteDiskById(collector.getId(), collectionId, diskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{diskId}", produces = "application/json")
    public ResponseEntity<String> getDiskOfCollection(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {
        var disk = this.diskService.getPersonalDiskByIdFromCollectionId(diskId, collectionId, authentication);
        try {
            if("private".equals(view)){
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(disk)
                );
            }else{
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(disk)
                );
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getDisksOfCollection(
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ){
        var disks = this.diskService.getPersonalDisksFromCollection(collectionId, authentication);
        var result = disks.get();
        try{
            if ("private".equals(view)){
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(result)
                );
            }else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(result)
                );
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/{diskId}")
    public ResponseEntity<Entity> uptadeDiskById(
            @RequestBody DiskEntity disk,
            @PathVariable ("diskId")  Long diskId,
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication
    ) {
        this.diskService.updateDisk(disk, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }
}

