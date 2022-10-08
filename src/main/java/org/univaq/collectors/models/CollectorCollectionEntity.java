package org.univaq.collectors.models;
import java.io.Serializable;

import javax.persistence.*;


@Entity(name = "collector_collection")
public class CollectorCollectionEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private CollectorEntity collectors;

    @ManyToOne
    private CollectionEntity collections;
    

    private boolean isOwner;
    
    public CollectorCollectionEntity() {
    }

    public CollectorCollectionEntity(CollectorEntity collectors, CollectionEntity collection) {
        this.collectors = collectors;
        this.collections = collection;
    }


    public CollectorCollectionEntity(CollectorEntity collectors, CollectionEntity collection, boolean isOwner) {
        this.collectors = collectors;
        this.collections = collection;
        this.isOwner = isOwner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CollectorEntity getCollectors() {
        return collectors;
    }

    public void setCollectors(CollectorEntity collectors) {
        this.collectors = collectors;
    }

    public CollectionEntity getCollections() {
        return collections;
    }

    public void setCollections(CollectionEntity collections) {
        this.collections = collections;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
