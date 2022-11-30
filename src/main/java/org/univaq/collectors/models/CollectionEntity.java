package org.univaq.collectors.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.jsonwebtoken.lang.Assert;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

@Entity(name = "collection")
public class CollectionEntity {
    
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean isPublic;


    @OneToMany(
            mappedBy = "collection",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<CollectorCollectionEntity> collectors = new ArrayList<>();


    public CollectionEntity(Long id, String name, String status, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isPublic = isPublic;
    }

    public CollectionEntity() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

    }
//prendo collezioni pubbliche prima controllo
    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<CollectorCollectionEntity> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<CollectorCollectionEntity> collectors) {
        this.collectors = collectors;
    }
    public void addCollectorCollection(CollectorEntity collector) {
        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
        collectors.add(collectorCollection);
    }


    //update a collection of collector
    public void updateCollectorCollection(String name, String status, boolean isPublic) {
        CollectionEntity collectorCollection = new CollectionEntity();
        collectorCollection.setName(name);
        collectorCollection.setStatus(status);
        collectorCollection.setPublic(isPublic);
    }

    @Override
    public String toString() {
        return "CollectionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", isPublic=" + isPublic +
                ", collectors=" + collectors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionEntity that = (CollectionEntity) o;
        return isPublic == that.isPublic && id.equals(that.id) && name.equals(that.name) && status.equals(that.status) && collectors.equals(that.collectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, isPublic, collectors);
    }
}
