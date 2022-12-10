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

    @JsonView(UserView.Private.class)
    @ManyToOne()
    private CollectionEntity collection;


    public DiskEntity() {
    }
    

    public DiskEntity(Long id, String title, String author, String label,  String state, String format, Integer barcode, Integer duplicate,Long year, CollectionEntity collection) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.label = label;
        this.state = state;
        this.format = format;
        this.barcode = barcode;
        this.duplicate = duplicate;
        this.year = year;
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
        DiskEntity that = (DiskEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(label, that.label) && Objects.equals(state, that.state) && Objects.equals(format, that.format) && Objects.equals(barcode, that.barcode) && Objects.equals(duplicate, that.duplicate) && Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, label, state, format, barcode, duplicate, collection);
    }


}







