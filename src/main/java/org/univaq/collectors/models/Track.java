package org.univaq.collectors.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity(name = "tracks")
public class Track {
    
    @Id
    @GeneratedValue
    private Long trackId;

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
    private long time;

    public Track(Long trackId, String title, String artist, String album, String band, String compositor, long time) {
        this.trackId = trackId;
        this.artist = artist;
        this.album = album;
        this.band = band;
        this.compositor = compositor;
        this.time = time;
    }

    public Track() {}
    
    public Long getTrackId() {
        return trackId;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    

}
