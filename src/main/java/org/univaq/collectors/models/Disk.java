package org.univaq.collectors.models;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

@Entity
public class Disk {
    
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    private String label;

    @NonNull
    private String diskType;

    @NonNull
    private String state;

    @NonNull
    private String format;

    @NonNull
    private Integer barcode;

    @NonNull
    private Integer duplicate;

    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getBarcode() {
        return barcode;
    }

    public void setBarcode(Integer barcode) {
        this.barcode = barcode;
    }

    public Integer getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Integer duplicate) {
        this.duplicate = duplicate;
    }

}

    

    

    
}






