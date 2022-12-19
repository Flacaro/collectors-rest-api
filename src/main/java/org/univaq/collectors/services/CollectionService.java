package org.univaq.collectors.services;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Service
public class CollectionService {


    private final CollectionsRepository collectionsRepository;

    private final CollectorsRepository collectorsRepository;

    private final CollectorCollectionRepository collectorCollectionRepository;


    public CollectionService(
            CollectionsRepository collectionsRepository,
            CollectorCollectionRepository collectorCollectionRepository,
            CollectorsRepository collectorsRepository

    ) {
        this.collectionsRepository = collectionsRepository;
        this.collectorCollectionRepository = collectorCollectionRepository;
        this.collectorsRepository = collectorsRepository;
    }


    public Optional<CollectionEntity> getCollectionById(Long collectionId, Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName());
        if (collector.isPresent()) {
            var collectorCollection = collectorCollectionRepository.findCollectionByCollectorId(collector.get().getId());
            if (collectorCollection.isPresent()) {
                return collectionsRepository.findById(collectionId);
            }
        }
        return collectionsRepository.findById(collectionId);
    }

    public Optional<List<CollectionEntity>> getAllPersonalCollections (int page, int size, Authentication authentication) {
        var collector = collectorsRepository.findByEmail(authentication.getName());
        if (collector.isPresent()) {
            var collectorCollections = collectorCollectionRepository.findCollectionsByCollectorId(collector.get().getId(), PageRequest.of(page, size));
            var collections = new ArrayList<CollectionEntity>();
            if(collectorCollections.isPresent()) {
                var collectorCollection = collectorCollections.get();
                for (CollectorCollectionEntity collectorCollectionEntity : collectorCollection) {
                    collections.add(collectorCollectionEntity.getCollection());
                }
                return Optional.of(collections);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No collections found");

            }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No collector found");

    }


    public Optional<List<CollectionEntity>> getAllPublicCollections(int page, int size) {
        return collectionsRepository.getAllPublicCollections(PageRequest.of(page, size));
    }


    public List<CollectionEntity> getCollectionsByParameters(String name, String type) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("name", contains().ignoreCase())
                .withMatcher("type", contains().ignoreCase());
        CollectionEntity example = new CollectionEntity();
        example.setName(name);
        example.setType(type);

        return this.collectionsRepository.findAll(Example.of(example, matcher));
    }


    public Optional<CollectionEntity> getPublicCollectionById(Long collectionId) {
        var optionalCollection = this.collectionsRepository.findById(collectionId);
        if (optionalCollection.isPresent()) {
            var collection = optionalCollection.get();
            if (collection.isVisible()) {
                return optionalCollection;
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Optional<CollectionEntity> getPublicCollectionById(Long collectorId, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var collectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collectorId, collectionId);
            if (collectorCollection.isPresent()) {
                var collection = collectorCollection.get().getCollection();
                if (collection.isVisible()) {
                    return Optional.of(collection);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection is not public");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }

    //Stream filter(Predicate predicate) restituisce un flusso costituito dagli elementi di questo flusso
    // che corrispondono al predicato specificato.
    public Optional<List<CollectionEntity>> getPublicCollections(Long collectorId) {
        List<CollectionEntity> publicCollections = new ArrayList<>();
        var optionalCollector = this.collectorsRepository.findById(collectorId);
        if (optionalCollector.isPresent()) {
            var publicCollectorCollections = this.collectorCollectionRepository.findPublicCollectionsByCollectorId(collectorId);
            if (publicCollectorCollections.isPresent()) {
                for (var publicCollectorCollection : publicCollectorCollections.get()) {
                    var collectionEntity = this.collectionsRepository.findById(publicCollectorCollection.getCollection().getId());
                    collectionEntity.ifPresent(publicCollections::add);
                }
                return Optional.of(publicCollections);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
    }

    public Optional<CollectionEntity> saveCollectorCollection(CollectionEntity collection, Authentication authentication) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());

        if (optionalCollector.isPresent()) {
            var savedCollection = this.collectionsRepository.save(collection);
            savedCollection.addCollectorCollection(optionalCollector.get());
            this.collectionsRepository.flush();
            return Optional.of(savedCollection);
        }
         else {
             throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }
    }



    //vedere
    public void deleteCollectorCollectionById(Authentication authentication, Long collectionId) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var optionalCollectorCollection = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (optionalCollectorCollection.isPresent()) {
                var collectorCollection = optionalCollectorCollection.get();
                var collection = collectorCollection.getCollection();
                var collectors = this.collectorsRepository.findAll();
                var isOwner = collectorCollection.isOwner();
                if (isOwner) {
                    for (var collectorEntity : collectors) {
                        var favourites = collectorEntity.getFavourites();
                        favourites.removeIf(favourite -> favourite.getId().equals(collection.getId()));
                        this.collectorsRepository.flush();
                    }
                    this.collectorCollectionRepository.delete(collection);
                    this.collectionsRepository.delete(collection);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
                }

            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
            }
        }
        else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
        }
    }

    public Optional<CollectionEntity> updateCollectorCollectionById(Authentication authentication, Long collectionId, CollectionEntity collection) {
        var optionalCollector = this.collectorsRepository.findByEmail(authentication.getName());
        if (optionalCollector.isPresent()) {
            var collector = optionalCollector.get();
            var collectorCollectionOptional = this.collectorCollectionRepository.findCollectionByIdAndCollectorById(collector.getId(), collectionId);
            if (collectorCollectionOptional.isPresent() && collectorCollectionOptional.get().getCollection().getId().equals(collectionId)) {
                var collectorCollection = collectorCollectionOptional.get();
                if (collectorCollection.isOwner()) {
                    var collectionToUpdate = collectorCollection.getCollection();
                    collectionToUpdate.updateCollectorCollection(collection.getName(), collection.getType(), collection.isVisible());
                    this.collectionsRepository.save(collectionToUpdate);
                    return Optional.of(collectionToUpdate);
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
                    }
            }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
    }


    //prendo la lista di id dei collezionisti
    public Optional<CollectionEntity> shareCollection(List<Long> collectorsIds, Long collectionId, Authentication authentication) {
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
            var collectorCollections = collectorCollectionRepository.findCollectionByIdAndCollectorById(authenticateCollector.getId(), collectionId);
            if(collectorCollections.isPresent()) {
            var isOwner = this.isCollectorOwnerOfCollection(authenticateCollector, collection);

            if (isOwner) {
                var owner = collectorCollections.get().getCollector();
                var collectionOfOwner = collectorCollections.get().getCollection();

                if (authenticateCollector.getId().equals(owner.getId()) && collectionOfOwner.getId().equals(collectionId)) {
                    //controllo se la collection e' gia' condivisa con gli utenti che voglio aggiungere
                    for (var collector : collectors) {
                        if (!collectorsInCollection.contains(collector)) {
                            listCollectorCollection.add(new CollectorCollectionEntity(collector, collection, false));
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection already shared with this collector");
                        }
                    }
                    collection.setCollectionsCollectors(listCollectorCollection);
                    collectionsRepository.save(collection);
                    return Optional.of(collection);
                }
            }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this collection");
            }

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector not found");
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










