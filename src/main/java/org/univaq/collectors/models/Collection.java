package org.univaq.collectors.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.springframework.lang.NonNull;

import antlr.collections.List;

@Entity 
public class Collection {
    
    @Id
    @GeneratedValue
    private Long collectionId;

    @NotBlank
    private String name;

    @NotBlank
    private String collector; 

    @NotBlank
    private String status;

    private boolean shared;


    public Collection (Long collectionId, String name, String collector, String status,Boolean shared ){
        this.collectionId = collectionId;
        this.name = name;
        this.collector = collector;
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

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
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
