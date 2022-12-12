package org.univaq.collectors.models;
import com.fasterxml.jackson.annotation.JsonView;
import org.univaq.collectors.UserView;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity(name = "track")

@Table(
        indexes = {
                @Index(name = "collection_name_index", columnList = "title"),
                @Index(name = "collector_type_index", columnList = "artist"),
                @Index(name = "collector_type_index", columnList = "album"),
                @Index(name = "collector_type_index", columnList = "band"),
                @Index(name = "collector_type_index", columnList = "compositor")

        }
)

public class TrackEntity {

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
    private String artist;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String album;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String band;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false)
    private String compositor;

    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private double time;

    @JsonView(UserView.Private.class)
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

    public void updateTrack (TrackEntity track) {
        this.title = track.getTitle();
        this.artist = track.getArtist();
        this.album = track.getAlbum();
        this.band = track.getBand();
        this.compositor = track.getCompositor();
        this.time = track.getTime();
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