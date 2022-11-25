package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.DiskEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;
import org.univaq.collectors.services.DiskService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/disks")
public class PDiskController {

    private final DiskService diskService;
    private final CollectionService collectionService;
    private final CollectorService collectorService;

    public PDiskController(DiskService diskService, CollectionService collectionService, CollectorService collectorService) {
        this.diskService = diskService;
        this.collectionService = collectionService;
        this.collectorService = collectorService;
    }

    //prendo tutti i dischi dell'utente loggato
    @GetMapping
    public ResponseEntity<List<DiskEntity>> getPersonalDisks(Principal principal,Long collectionId) { //dal tokan prendo l'utente loggato principal
        var collector = this.collectorService.getCollectorByEmail(principal.getName()); //servizio che prende collezionista dal email
        var collection = this.collectionService.getCollectorCollectionById(collector.getId(), collectionId); //collezioni mie personali tramite query
        if (collection.isPresent()){
            var result = this.diskService.getPersonalDisks(collection.get().getId());
            return ResponseEntity.ok(result);} //ritorna collezioni mie
        return ResponseEntity.notFound().build();

    }
    //aggiungi nuovo disco in collection
    @PostMapping("/disks")
    public ResponseEntity<DiskEntity> saveDisk(
            @RequestBody DiskEntity disk,
            Principal principal,
            Long collectionId
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());

        var optionalDisk = this.diskService.saveDisk(disk, collectionId, collector.getId());
        return optionalDisk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
   //salva elimina aggiorna

