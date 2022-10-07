package org.univaq.collectors.models;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity(name = "collector_collection")
public class CollectorCollectionEntity implements Serializable {

    @Id
    private Long id;

    @ManyToOne()
    private CollectorEntity collectors;

    
    @ManyToOne()
    private CollectionEntity collections;
    

    private boolean isOwner;
    
    public CollectorCollectionEntity() {
    }

    public CollectorCollectionEntity(Long id, CollectorEntity collectors, CollectionEntity collections, boolean isOwner) {
        this.id = id;
        this.collectors = collectors;
        this.collections = collections;
        this.isOwner = isOwner;
    }

    

    public CollectorCollectionEntity(CollectorEntity collectors, CollectionEntity collections, boolean isOwner) {
        this.collectors = collectors;
        this.collections = collections;
        this.isOwner = isOwner;
    }

    public CollectorCollectionEntity(Long id, CollectorEntity collectors, CollectionEntity collections) {
        this.id = id;
        this.collectors = collectors;
        this.collections = collections;
    }

    
    public CollectorEntity getCollectors() {
        return collectors;
    }

    public Long getId() {
        return id;
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

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((collectors == null) ? 0 : collectors.hashCode());
        result = prime * result + ((collections == null) ? 0 : collections.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (collectors == null) {
            if (other.collectors != null)
                return false;
        } else if (!collectors.equals(other.collectors))
            return false;
        if (collections == null) {
            if (other.collections != null)
                return false;
        } else if (!collections.equals(other.collections))
            return false;
        if (isOwner != other.isOwner)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CollectorCollectionEntity [id=" + id + ", collectors=" + collectors + ", collections=" + collections
                + ", isOwner=" + isOwner + "]";
    }



   
   





    
}
