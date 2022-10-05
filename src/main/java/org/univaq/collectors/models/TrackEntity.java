package org.univaq.collectors.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

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
    private long time;


    
    public TrackEntity(Long id, @NotBlank String title, @NotBlank String artist, @NotBlank String album,
            @NotBlank String band, @NotBlank String compositor, long time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.band = band;
        this.compositor = compositor;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((artist == null) ? 0 : artist.hashCode());
        result = prime * result + ((album == null) ? 0 : album.hashCode());
        result = prime * result + ((band == null) ? 0 : band.hashCode());
        result = prime * result + ((compositor == null) ? 0 : compositor.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrackEntity other = (TrackEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (artist == null) {
            if (other.artist != null)
                return false;
        } else if (!artist.equals(other.artist))
            return false;
        if (album == null) {
            if (other.album != null)
                return false;
        } else if (!album.equals(other.album))
            return false;
        if (band == null) {
            if (other.band != null)
                return false;
        } else if (!band.equals(other.band))
            return false;
        if (compositor == null) {
            if (other.compositor != null)
                return false;
        } else if (!compositor.equals(other.compositor))
            return false;
        if (time != other.time)
            return false;
        return true;
    }

    
    
}
