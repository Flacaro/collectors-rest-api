package org.univaq.collectors.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.springframework.lang.NonNull;

@Entity
public class Track {
    
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String artist;

    @NonNull
    private String album;

    @NonNull
    private String band;

    @NonNull
    private String compositor;

    @NonNull
    private long time;

    public Track(Long id, String title, String artist, String album, String band, String compositor, long time) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.band = band;
        this.compositor = compositor;
        this.time = time;
    }

    public Track() {}
    
    public Long getId() {
        return id;
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
