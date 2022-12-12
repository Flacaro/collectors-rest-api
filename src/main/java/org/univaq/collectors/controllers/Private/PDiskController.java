package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.DiskService;



@RestController
@RequestMapping("/collections/{collectionId}/disks")
public class PDiskController {

    private final DiskService diskService;
    private final ObjectMapper objectMapper;

    public PDiskController(DiskService diskService, ObjectMapper objectMapper) {
        this.diskService = diskService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<DiskEntity> saveDisk(
            @RequestBody DiskEntity disk,
            @PathVariable("collectionId") Long collectionId,
            Authentication authentication
    ) {
        var optionalDisk = this.diskService.saveDisk(disk, collectionId, authentication);
        return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{diskId}")
    public ResponseEntity<DiskEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @PathVariable("diskId") Long diskId,
            Authentication authentication
    ) {
        this.diskService.deleteDiskById(authentication, collectionId, diskId);
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
        try{
            if ("private".equals(view)){
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(disks)
                );
            }else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(disks)
                );
            }
        }catch (JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{diskId}")
    public ResponseEntity<DiskEntity> uptadeDiskById(
            @RequestBody DiskEntity disk,
            @PathVariable ("diskId")  Long diskId,
            @PathVariable ("collectionId") Long collectionId,
            Authentication authentication
    ) {
        this.diskService.updateDisk(disk, diskId, collectionId, authentication);
        return ResponseEntity.ok().build();
    }
}
