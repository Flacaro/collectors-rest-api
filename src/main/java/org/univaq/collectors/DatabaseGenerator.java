package org.univaq.collectors;


import org.springframework.stereotype.Service;

@Service
public class DatabaseGenerator {

    //questo e' come l'@Autowired
//    private final CollectorsRepository collectorRepository;
//    private final CollectionsRepository collectionsRepository;
//
//    private final DisksRepository disksRepository;
//
//    private final TracksRepository tracksRepository;
//
//    public DatabaseGenerator(CollectorsRepository collectorRepository, CollectionsRepository collectionsRepository, DisksRepository disksRepository, TracksRepository tracksRepository) {
//        this.collectorRepository = collectorRepository;
//        this.collectionsRepository = collectionsRepository;
//        this.disksRepository = disksRepository;
//        this.tracksRepository = tracksRepository;
//    }
//
//    public void generateDatabase() {
//
//
//        CollectionEntity collection = new CollectionEntity(null, "My Collection", "private", false, List.of());
//        CollectorEntity collector = new CollectorEntity(
//                null,
//                "Mario",
//                "Rossi",
//                LocalDate.of(1990, 1, 1),
//                "mario.rossi",
//                "mario@gmail.com",
//                new BCryptPasswordEncoder().encode("secret"),
//                List.of()
//        );
//
//        DiskEntity disk = new DiskEntity(null, "back in black", "AC/DC", "rock", "rock", "good", "cd",123489508, 0, null);
//
//        //TrackEntity track = new TrackEntity(null, "back in black", "AC/DC", "back in black", "AC/DC", "AC/DC", 3.5, disk);
//
//        var savedCollection = collectionsRepository.saveAndFlush(collection);
//        var savedDisk = disksRepository.saveAndFlush(disk);
//
//
//
////        var savedCollector = collectorRepository.saveAndFlush(collector);
//
////
////        savedCollector.getCollections().add(collectorCollection);
////        savedCollection.getCollectors().add(collectorCollection);
////fare la find e poi fare le operazioni di .add
//        collectionsRepository.flush();
//        disksRepository.flush();
//    }

}
