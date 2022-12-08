package org.univaq.collectors.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.Examples;
import org.univaq.collectors.models.CollectorCollectionEntity;
import org.univaq.collectors.repositories.CollectionsRepository;
import org.univaq.collectors.repositories.CollectorsRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CollectionServiceTest {

    @Autowired
    private CollectionsRepository collectionsRepository;

    @Autowired
    private CollectorsRepository collectorsRepository;

    @MockBean
    private Authentication authentication;

    // sut -> System Under Test
    @Autowired
    private CollectionService sut;



    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsNotTheOwner() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(2, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors2.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertTrue(collection.isPresent());

        assertEquals(1, collection.get().getCollectionsCollectors().size());
        assertEquals(savedCollectors.getEmail(), collection.get().getCollectionsCollectors().get(0).getCollector().getEmail());

    }



    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsOwner() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(2, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertFalse(collection.isPresent());

    }


    @Test
    @Transactional
    void should_not_unshareCollectorsFromCollection_when_CollectorsAreNotInTheCollection() {
        var mario = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var maria = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(maria, savedCollection, true));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(mario.getEmail());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sut.unshareCollection(
                        List.of(maria.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

    }


    @Test
    @Transactional
    void should_not_unshareCollectorsFromCollection_when_collectorsListIsEmpty() {
        var mario = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var maria = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(maria, savedCollection, true));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(mario.getEmail());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sut.unshareCollection(
                        List.of(),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    }


    // testi con 3 utenti e un array di collectionId > 1
    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsOwnerAndWantToDeleteAListOfUserExeptHim() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));
        collectors.add(new CollectorCollectionEntity(savedCollectors3, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(3, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors2.getId(), savedCollectors3.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertTrue(collection.isPresent());

    }

    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsOwnerAndWantToDeleteAListOfUserWithOwner() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));
        collectors.add(new CollectorCollectionEntity(savedCollectors3, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(3, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors.getId(), savedCollectors2.getId(), savedCollectors3.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertFalse(collection.isPresent());

    }

    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsNotOwnerAndWantToDeleteAListOfUser() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));
        collectors.add(new CollectorCollectionEntity(savedCollectors3, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(3, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors2.getEmail());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sut.unshareCollection(
                        List.of(savedCollectors.getId(), savedCollectors2.getId(), savedCollectors3.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

    }


    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorToRemoveIsNotInListOfCollectorsCollection() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(2, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sut.unshareCollection(
                        List.of(savedCollectors3.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsOwnerOfCollectionAndIsTheOnlyOneToRemove() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));
        collectors.add(new CollectorCollectionEntity(savedCollectors3, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(3, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertFalse(collection.isPresent());
    }


    @Test
    @Transactional
    void should_unshareCollectorsFromCollection_when_collectorIsOwnerOfCollectionAndWantToDeleteOneCollectorFromCollectionList() {
        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
        var savedCollectors2 = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
        var savedCollectors3 = collectorsRepository.findByEmail("daniele@neri.com").orElseThrow();

        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());

        var collectors = new ArrayList<CollectorCollectionEntity>();
        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
        collectors.add(new CollectorCollectionEntity(savedCollectors2, savedCollection, false));
        collectors.add(new CollectorCollectionEntity(savedCollectors3, savedCollection, false));

        savedCollection.setCollectionsCollectors(collectors);

        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);

        assertNotNull(collectionWithCollectors);

        assertEquals(3, collectionWithCollectors.getCollectionsCollectors().size());


        when(authentication.getName()).thenReturn(savedCollectors.getEmail());

        assertDoesNotThrow(
                () -> sut.unshareCollection(
                        List.of(savedCollectors2.getId()),
                        collectionWithCollectors.getId(),
                        authentication
                )
        );

        var collection = this.collectionsRepository.findById(savedCollection.getId());

        assertTrue(collection.isPresent());
    }

//    @Test
//    @Transactional
//    void should_addCollectionToFavouritesList_when_collectorIsOwnerOfCollectionAndWantToAddToFavouritesList() {
//        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
//
//        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());
//
//        var collectors = new ArrayList<CollectorCollectionEntity>();
//        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
//
//
//        savedCollection.setCollectionsCollectors(collectors);
//
//        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);
//
//        assertNotNull(collectionWithCollectors);
//
//        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());
//
//        var collectionOptional = this.collectionsRepository.findById(savedCollection.getId());
//
//        assertTrue(collectionOptional.isPresent());
//
//        var collection = collectionOptional.get();
//
//
//        when(authentication.getName()).thenReturn(savedCollectors.getEmail());
//
//        assertDoesNotThrow(
//                () -> sut.addCollectionInFavouritesList(
//                        savedCollectors.getId(),
//                        collection.getId()
//                )
//        );
//
//        assertTrue(collectorsRepository.findById(savedCollectors.getId()).get().getFavouritesCollections().contains(collection));
//    }
//
//    @Test
//    @Transactional
//    void should_addCollectionToFavouritesList_when_collectorIsNotOwnerOfCollectionAndWantToAddToFavouritesList() {
//        var mario = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
//        var maria = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
//
//        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());
//
//        var collectors = new ArrayList<CollectorCollectionEntity>();
//        collectors.add(new CollectorCollectionEntity(maria, savedCollection, false));
//
//
//        savedCollection.setCollectionsCollectors(collectors);
//
//        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);
//
//        assertNotNull(collectionWithCollectors);
//
//        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());
//
//        var collectionOptional = this.collectionsRepository.findById(savedCollection.getId());
//
//        assertTrue(collectionOptional.isPresent());
//
//        var collection = collectionOptional.get();
//
//        assertTrue(collection.isPublic());
//
//        when(authentication.getName()).thenReturn(maria.getEmail());
//
//        assertDoesNotThrow(
//                () -> sut.addCollectionInFavouritesList(
//                        maria.getId(),
//                        collection.getId()
//                )
//        );
//
//        assertTrue(collectorsRepository.findById(maria.getId()).get().getFavouritesCollections().contains(collection));
//    }
//
//    @Test
//    @Transactional
//    void should_addCollectionToFavouritesList_when_collectorIsOwnerOfCollectionAndCollectionIsAlreadyInFavouritesList() {
//        var savedCollectors = collectorsRepository.findByEmail("mario@rossi.com").orElseThrow();
//
//
//        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());
//
//        var collectors = new ArrayList<CollectorCollectionEntity>();
//        collectors.add(new CollectorCollectionEntity(savedCollectors, savedCollection, true));
//
//
//        savedCollection.setCollectionsCollectors(collectors);
//
//        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);
//
//        assertNotNull(collectionWithCollectors);
//
//        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());
//
//        var collectionOptional = this.collectionsRepository.findById(savedCollection.getId());
//
//        assertTrue(collectionOptional.isPresent());
//
//        var collection = collectionOptional.get();
//
//        savedCollectors.addFavouritesCollection(collection);
//
//        assertTrue(savedCollectors.getFavouritesCollections().contains(collection));
//
//        when(authentication.getName()).thenReturn(savedCollectors.getEmail());
//
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> sut.addCollectionInFavouritesList(
//                        savedCollectors.getId(),
//                        collection.getId()
//                )
//        );
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//    }
//
//
//    @Test
//    @Transactional
//    void should_addCollectionToFavouritesList_when_collectorIsNotOwnerOfCollectionAndCollectionIsAlreadyInFavouritesList() {
//        var maria = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
//
//
//        var savedCollection = this.collectionsRepository.save(Examples.collectionExample());
//
//        var collectors = new ArrayList<CollectorCollectionEntity>();
//        collectors.add(new CollectorCollectionEntity(maria, savedCollection, false));
//
//
//        savedCollection.setCollectionsCollectors(collectors);
//
//        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);
//
//        assertNotNull(collectionWithCollectors);
//
//        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());
//
//        var collectionOptional = this.collectionsRepository.findById(savedCollection.getId());
//
//        assertTrue(collectionOptional.isPresent());
//
//        var collection = collectionOptional.get();
//
//        assertTrue(collection.isPublic());
//
//        maria.addFavouritesCollection(collection);
//
//        assertTrue(maria.getFavouritesCollections().contains(collection));
//
//        when(authentication.getName()).thenReturn(maria.getEmail());
//
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> sut.addCollectionInFavouritesList(
//                        maria.getId(),
//                        collection.getId()
//                )
//        );
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//    }
//
//    @Test
//    @Transactional
//    void should_addCollectionToFavouritesList_when_collectorIsNotOwnerOfCollectionAndCollectionIsNotPublic() {
//        var maria = collectorsRepository.findByEmail("maria@bianchi.com").orElseThrow();
//
//
//        var savedCollection = this.collectionsRepository.save(Examples.collectionExample2());
//
//        var collectors = new ArrayList<CollectorCollectionEntity>();
//        collectors.add(new CollectorCollectionEntity(maria, savedCollection, false));
//
//
//        savedCollection.setCollectionsCollectors(collectors);
//
//        var collectionWithCollectors = this.collectionsRepository.save(savedCollection);
//
//        assertNotNull(collectionWithCollectors);
//
//        assertEquals(1, collectionWithCollectors.getCollectionsCollectors().size());
//
//        var collectionOptional = this.collectionsRepository.findById(savedCollection.getId());
//
//        assertTrue(collectionOptional.isPresent());
//
//        var collection = collectionOptional.get();
//
//        assertFalse(collection.isPublic());
//
//        when(authentication.getName()).thenReturn(maria.getEmail());
//
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> sut.addCollectionInFavouritesList(
//                        maria.getId(),
//                        collection.getId()
//                )
//        );
//
//        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
//    }



}