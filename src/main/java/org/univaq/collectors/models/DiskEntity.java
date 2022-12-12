package org.univaq.collectors.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.univaq.collectors.UserView;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Entity(name = "disk")
public class DiskEntity {

    @JsonView(UserView.Public.class)
    @Id
    @GeneratedValue
    private Long id;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String title;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String author;


    //casa editrice
    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String label;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String state;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String format;

    @JsonView(UserView.Public.class)
    @Column()
    private Integer barcode;

    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private Integer duplicate;

    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private Long year;

    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private String genre;


    @JsonView(UserView.Private.class)
    @ManyToOne()
    private CollectionEntity collection;


    public DiskEntity() {
    }
    

    public DiskEntity(Long id, String title, String author, String label,  String state, String format, Integer barcode, Integer duplicate,Long year, String genre, CollectionEntity collection) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.label = label;
        this.state = state;
        this.format = format;
        this.barcode = barcode;
        this.duplicate = duplicate;
        this.year = year;
        this.genre = genre;
        this.collection = collection;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getYear(){ return year; }

    public void setYear(Long year){this.year = year;}

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiskEntity disk = (DiskEntity) o;
        return Objects.equals(id, disk.id) && Objects.equals(title, disk.title) && Objects.equals(author, disk.author) && Objects.equals(label, disk.label) && Objects.equals(state, disk.state) && Objects.equals(format, disk.format) && Objects.equals(barcode, disk.barcode) && Objects.equals(duplicate, disk.duplicate) && Objects.equals(year, disk.year) && Objects.equals(genre, disk.genre) && Objects.equals(collection, disk.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, label, state, format, barcode, duplicate, year, genre, collection);
    }
}







