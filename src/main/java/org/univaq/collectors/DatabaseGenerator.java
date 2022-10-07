package org.univaq.collectors;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.repositories.DisksRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class DatabaseGenerator {

//     //questo e' come l'@Autowired
//     private final CollectorsRepository collectorRepository;
//     private final CollectionsRepository collectionsRepository;

//     public DatabaseGenerator(CollectorsRepository collectorRepository, CollectionsRepository collectionsRepository) {
//         this.collectorRepository = collectorRepository;
//         this.collectionsRepository = collectionsRepository;
//     }

//     public void generateDatabase() {

//         var collection = new CollectionEntity(null, "My Collection", "private", false, List.of());
//         var collector = new CollectorEntity(
//                 null,
//                 "Mario",
//                 "Rossi",
//                 LocalDate.of(1990, 1, 1),
//                 "mario.rossi",
//                 "mario@gmail.com",
//                 new BCryptPasswordEncoder().encode("secret"),
//                 List.of()
//         );



//         var savedCollector = collectorRepository.saveAndFlush(collector);
//         var savedCollection = collectionsRepository.saveAndFlush(collection);
//         var collectorCollection = new CollectorCollectionEntity(savedCollector, savedCollection, true);

        
// //        collector.getCollections().add(collectorCollection);
// //        collection.getCollectors().add(collectorCollection);
// //fare la find e poi fare le operazioni di .add
//         collectionsRepository.flush();
//     }
}
