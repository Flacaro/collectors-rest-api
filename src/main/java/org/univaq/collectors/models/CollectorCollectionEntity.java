package org.univaq.collectors.models;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;


@Entity(name = "collector_collection")
public class CollectorCollectionEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private CollectorEntity collector;

    @ManyToOne
    @JsonBackReference
    private CollectionEntity collection;
    

    private boolean isOwner;

    
    public CollectorCollectionEntity() {
    }

    public CollectorCollectionEntity(CollectorEntity collector, CollectionEntity collection) {
        this.collector = collector;
        this.collection = collection;
    }


    public CollectorCollectionEntity(CollectorEntity collector, CollectionEntity collection, boolean isOwner) {
        this.collector = collector;
        this.collection = collection;
        this.isOwner = isOwner;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public CollectorEntity getCollector() {
        return collector;
    }

    public Long getId() {
        return id;
    }

    public void setCollector(CollectorEntity collectors) {
        this.collector = collectors;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collections) {
        this.collection = collections;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectorCollectionEntity that = (CollectorCollectionEntity) o;
        return isOwner == that.isOwner && id.equals(that.id) && collector.equals(that.collector) && collection.equals(that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, collector, collection, isOwner);
    }
}
