package org.univaq.collectors.models;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity(name = "track")
public class TrackEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String artist;

    @NotBlank
    @Column(nullable = false)
    private String album;

    @NotBlank
    @Column(nullable = false)
    private String band;

    @NotBlank
    @Column(nullable = false)
    private String compositor;

    @Column(nullable = false)
    private double time;

    @ManyToOne()
    private DiskEntity disk;


    public TrackEntity(Long id, String title, String artist, String album, String band, String compositor, double time, DiskEntity disk) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.band = band;
        this.compositor = compositor;
        this.time = time;
        this.disk = disk;
    }

    public TrackEntity() {
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    public double getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public DiskEntity getDisk() {
        return disk;
    }

    public void setDisk(DiskEntity disk) {
        this.disk = disk;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackEntity that = (TrackEntity) o;
        return time == that.time && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(artist, that.artist) && Objects.equals(album, that.album) && Objects.equals(band, that.band) && Objects.equals(compositor, that.compositor) && Objects.equals(disk, that.disk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, album, band, compositor, time, disk);
    }
}