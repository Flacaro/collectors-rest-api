package org.univaq.collectors.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Entity(name = "disk")
public class DiskEntity {
    
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @NotBlank
    @Column(nullable = false)
    private String label;

    @NotBlank
    @Column(nullable = false)
    private String diskType;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @NotBlank
    @Column(nullable = false)
    private String format;

    @NotBlank
    @Column(nullable = true)
    private Integer barcode;

    @NotBlank
    @Column(nullable = false)
    private Integer duplicate;

    @ManyToOne()
    private CollectionEntity collection;


    public DiskEntity() {
    }
    

    public DiskEntity(Long id, String title, String author, String label, String diskType,  String state, String format, Integer barcode, Integer duplicate, CollectionEntity collection) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.label = label;
        this.diskType = diskType;
        this.state = state;
        this.format = format;
        this.barcode = barcode;
        this.duplicate = duplicate;
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
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(label, that.label) && Objects.equals(diskType, that.diskType) && Objects.equals(state, that.state) && Objects.equals(format, that.format) && Objects.equals(barcode, that.barcode) && Objects.equals(duplicate, that.duplicate) && Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, label, diskType, state, format, barcode, duplicate, collection);
    }


}







