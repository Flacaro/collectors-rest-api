package org.univaq.collectors.models;
import java.io.Serializable;

import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name = "collector_collection")
public class CollectorCollectionEntity implements Serializable{

    @Id
    @ManyToOne()
    @JoinColumn(name = "id")
    private CollectorEntity collectors;

    @Id
    @ManyToOne()
    @JoinColumn(name = "id")
    private CollectionEntity collection;
    

    private boolean isOwner;
    
    public CollectorCollectionEntity() {
    }

    public CollectorCollectionEntity(CollectorEntity collectors, CollectionEntity collection, boolean isOwner) {
        this.collectors = collectors;
        this.collection = collection;
        this.isOwner = isOwner;
    }

    public CollectorEntity getCollectors() {
        return collectors;
    }

    public void setCollectors(CollectorEntity collectors) {
        this.collectors = collectors;
    }

    public CollectionEntity getCollection() {
        return collection;
    }


    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collectors == null) ? 0 : collectors.hashCode());
        result = prime * result + ((collection == null) ? 0 : collection.hashCode());
        result = prime * result + (isOwner ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CollectorCollectionEntity other = (CollectorCollectionEntity) obj;
        if (collectors == null) {
            if (other.collectors != null)
                return false;
        } else if (!collectors.equals(other.collectors))
            return false;
        if (collection == null) {
            if (other.collection != null)
                return false;
        } else if (!collection.equals(other.collection))
            return false;
        if (isOwner != other.isOwner)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CollectorCollectionEntity [collectors=" + collectors + ", collection=" + collection + ", isOwner="
                + isOwner + "]";
    }

   





    
}
