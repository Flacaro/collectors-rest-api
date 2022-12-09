package org.univaq.collectors.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorCollectionRepository;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.repositories.DisksRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {


    private final CollectionsRepository collectionsRepository;

    private final CollectorsRepository collectorsRepository;

    private final CollectorCollectionRepository collectorCollectionRepository;

    private final DisksRepository disksRepository;

    public CollectionService(
            CollectionsRepository collectionsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectorsRepository collectorsRepository,
            DisksRepository disksRepository

    ) {
        this.collectionsRepository = collectionsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectorsRepository = collectorsRepository;
        this.disksRepository = disksRepository;
    }

    public List<CollectionEntity> getAllPublicCollectionsByName(Optional<String> optionalname, Integer page, Integer size) {
        var optionalName = optionalname.orElse("");
        if (optionalName.isEmpty()) {
            return this.collectionsRepository.getPublicCollections(PageRequest.of(page, size));

        } else {
            return this.collectionsRepository.getPublicCollectionsByName(optionalName, PageRequest.of(page, size));

        }

    }


    //Stream filter(Predicate predicate) restituisce un flusso costituito dagli elementi di questo flusso
    // che corrispondono al predicato specificato.
    public List<CollectionEntity> getPublicCollectionsByCollectorId(Long collectorId) {
        List<CollectionEntity> publicCollections = new ArrayList<>();
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var publicCollectorCollections = this.collectorCollectionRepository.getPublicCollectionsByCollectorId(collectorId);
            for (var publicCollectorCollection : publicCollectorCollections) {
                var collectionEntity = this.collectionsRepository.findById(publicCollectorCollection.getCollection().getId());
                collectionEntity.ifPresent(publicCollections::add);
            }

            return publicCollections;
        }
        return List.of();
    }


    public CollectionEntity saveCollectorCollection(CollectionEntity collection, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());

        if (optionalCollector.isPresent()) {
            var savedCollection = this.collectionsRepository.save(collection);
            savedCollection.addCollectorCollection(optionalCollector.get());
            this.collectionsRepository.flush();
            return savedCollection;
        }
         else {
             throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }
    }


    //perche sta qua?
//    public List<DiskEntity> getPublicDisks(Long collectionId) {
//        var optionalCollection = this.collectionsRepository.findById(collectionId);
//        if (optionalCollection.isPresent()) {
//            var collection = optionalCollection.get();
//            if (collection.isPublic()) {
//                var optionalDisk = this.disksRepository.findDisksFromCollectionId(collectionId);
//                if (optionalDisk.isPresent()) {
//                    return optionalDisk.get();
//                }
//            }
//        }
//        return List.of();
//    }


    public void deleteCollectorCollectionById(Authentication authentication, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var optionalCollectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (optionalCollectorCollection.isPresent()) {
                var collectorCollection = optionalCollectorCollection.get();
                var collection = collectorCollection.getCollection();
                var isOwner = collectorCollection.isOwner();
                if(isOwner) {
                    this.collectionsRepository.deleteById(collectionId);
                    this.collectorCollectionRepository.delete(collection);
                }
                else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }
    }

    public CollectionEntity updateCollectorCollectionById(Authentication authentication, Long collectionId, CollectionEntity collection) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent() && collectorCollectionOptional.get().getCollection().getId().equals(collectionId)) {
                var collectorCollection = collectorCollectionOptional.get();
                if (collectorCollection.isOwner()) {
                    var collectionToUpdate = collectorCollection.getCollection();
                    collectionToUpdate.updateCollectorCollection(collection.getName(), collection.getStatus(), collection.isPublic());
                    this.collectionsRepository.save(collectionToUpdate);
                    return collectionToUpdate;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
                    }
            } else  {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }
        return collection;

    }


    //prendo la lista di id dei collezionisti
    public CollectionEntity shareCollection(List<Long> collectorsIds, Long collectionId, Authentication authentication) {
        var optionalAuthenticateCollector = this.collectorsRepository.findByEmail(authentication.getName());
        List<CollectorEntity> collectors = new ArrayList<>();

        //lista di id degli utenti con cui voglio condividere la collection
        for (var id : collectorsIds) {
            collectorsRepository.findById(id).ifPresentOrElse(
                    collectors::add,
                    () -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
                    }
            );
        }
        Optional<CollectionEntity> collectionOptional = collectionsRepository.findById(collectionId);

        if (collectionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
        }

        //prendo la collection
        var collection = collectionOptional.get();
        var listCollectorCollection = collection.getCollectionsCollectors();
        //lista dei collezionisti che sono gia nella collection
        var collectorsInCollection = listCollectorCollection.stream().map(CollectorCollectionEntity::getCollector).toList();
        if (optionalAuthenticateCollector.isPresent()) {
            //prendo il collezionista loggato
            var authenticateCollector = optionalAuthenticateCollector.get();
            //controllo se l'utente loggato e' il proprietario della collection
            var owner = collectorCollectionRepository.hasCollectionAndIsOwner(authenticateCollector.getId(), collectionId);
            if (owner.isPresent()) {
                if (authenticateCollector.getId().equals(owner.get().getCollector().getId()) && owner.get().getCollection().getId().equals(collectionId)) {
                    //controllo se la collection e' gia' condivisa con gli utenti che voglio aggiungere
                    for (var collector : collectors) {
                        if (!collectorsInCollection.contains(collector)) {
                            listCollectorCollection.add(new CollectorCollectionEntity(collector, collection, false));
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection already shared with this collector");
                        }
                    }
                    collection.setCollectionsCollectors(listCollectorCollection);
                    return collectionsRepository.save(collection);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
            }

        }
        return collection;
    }


    public void unshareCollection(List<Long> collectorsIds, Long collectionId, Authentication authentication) {

        // 4. Controllo la lunghezza della lista di collezionisti da rimuovere, se è 0 -> 400
        if (collectorsIds.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must specify at least one collector ID");
        }

        // 1. Prendere dal DB l'utente autenticato
        var optionalAuthenticateCollector = this.collectorsRepository.findByEmail(authentication.getName());

        var loggedCollector = optionalAuthenticateCollector.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found")
        );

        // 2. Trovare se esiste la collection con l'id specificato
        var collection = this.collectionsRepository.findById(collectionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found")
        );

        // 3. Controllo che l'utente sia presente nella collection, se non esiste -> 403
        collection.getCollectionsCollectors().stream()
                .filter(cc -> cc.getCollector().getId().equals(loggedCollector.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not in this collection")
                );

        // 5. Se la lunghezza della lista degli ID e' uguale a 1 && l'ID e' uguale a quello dell'utente autenticato
        //    && l'utente autenticato non e' l'owner della collection lo rimuovo dalla collection
        if (collectorsIds.size() == 1) {
            var collectorId = collectorsIds.get(0);

            // se l'ID da rimuovere non e' presente nella lista allora -> 404
            var collectorCollection = collection.getCollectionsCollectors().stream()
                    .filter(cc -> cc.getCollector().getId().equals(collectorId))
                    .findFirst()
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found in collection")
                    );

            if (collectorCollection.isOwner()) {
                // elimino la collection
                this.collectionsRepository.delete(collection);
                return;
            } else {
                // rimuovo il collector dalla collection
                collection.getCollectionsCollectors().remove(collectorCollection);
                this.collectionsRepository.save(collection);
                return;
            }
        }

        // 6. Se la lunghezza della lista degli ID e' maggiore di 1 devo controllare che l'utente autentica sia l'owner della collection
        //    se non lo e' -> 403. Altrimenti controllo che l'intera lista di collezionisti da rimuovere sia presente nella collection,
        //    se lo e' -> rimuovo i collezionisti, altrimenti -> 404 (si potrebbe rimuovere solamente quelli presenti nella collection)

        // Controllo che la lista di collezionisti da rimuovere sia presente nella collection
        var collectorsToRemove = collection.getCollectionsCollectors().stream()
                .filter(cc -> collectorsIds.contains(cc.getCollector().getId()))
                .toList();

        if (collectorsToRemove.size() != collectorsIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some collectors are not in this collection");
        }

        // Controllo se nella lista di collezionisti da rimuovere l'utente autenticato e' presente
        var isLoggedUserInCollectorsToRemove = collectorsToRemove.stream()
                .anyMatch(cc -> cc.getCollector().getId().equals(loggedCollector.getId()));

        var isLoggedUserOwner = this.isCollectorOwnerOfCollection(loggedCollector, collection);

        if(isLoggedUserOwner) {
            if(isLoggedUserInCollectorsToRemove) {
                this.collectionsRepository.delete(collection);
            } else {
                collection.getCollectionsCollectors().removeAll(collectorsToRemove);
                this.collectionsRepository.save(collection);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
        }

    }


    private boolean isCollectorOwnerOfCollection(CollectorEntity collector, CollectionEntity collection) {
        return collection.getCollectionsCollectors().stream()
                .filter(cc -> cc.getCollector().getId().equals(collector.getId()))
                .findFirst()
                .map(CollectorCollectionEntity::isOwner)
                .orElse(false);
    }




}





