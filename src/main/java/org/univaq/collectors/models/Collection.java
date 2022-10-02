package org.univaq.collectors.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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


    public Collection (Long collectionId, String name, String status,Boolean shared ){
        this.collectionId = collectionId;
        this.name = name;
        this.status = status;
        this.shared = shared;
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

    

}
