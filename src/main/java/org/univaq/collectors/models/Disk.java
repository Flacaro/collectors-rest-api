package org.univaq.collectors.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;


@Entity(name = "disks")
public class Disk {
    
    @Id
    @GeneratedValue
    private Long diskId;

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

    

    public Disk(Long diskId, @NotBlank String title, @NotBlank String author, @NotBlank String label,
            @NotBlank String diskType, @NotBlank String state, @NotBlank String format, @NotBlank Integer barcode,
            @NotBlank Integer duplicate) {
        this.diskId = diskId;
        this.title = title;
        this.author = author;
        this.label = label;
        this.diskType = diskType;
        this.state = state;
        this.format = format;
        this.barcode = barcode;
        this.duplicate = duplicate;
    }

    public Disk() {}


    public Long getDiskId() {
        return diskId;
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

    public void addDisk(Disk disk){
        Disk.add(disk);
    } 

}







