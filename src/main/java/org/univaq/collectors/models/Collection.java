package org.univaq.collectors.models;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Entity(name = "collections")
public class Collection {
    
    @Id
    @GeneratedValue
    private Long collectionId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String status;

    private boolean shared;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "collectorId", referencedColumnName = "collectorId", nullable = false)
    // private Collector collector;


    public Collection (Long collectionId, String name, String status,Boolean shared) {
        this.collectionId = collectionId;
        this.name = name;
        this.status = status;
        this.shared = shared;
    //     this.collector = collector;
     }

    public Collection(){}

    public Long getCollectionId() {
        return collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public void setCollectors(Collector collector) {
    } 

    // public Collector getCollectors() {
    //     return collector;
    // }

    // public void setCollector(Collector collector) {
    //     this.collector = collector;
    // }

    @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Collection collection = (Collection) o;
		return Objects.equals(collectionId, collection.collectionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collectionId);
	}


    

}
