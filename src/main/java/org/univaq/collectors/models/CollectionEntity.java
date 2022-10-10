package org.univaq.collectors.models;
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


    @OneToMany(
            mappedBy = "collections",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CollectorCollectionEntity> collectors = new ArrayList<>();




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

    public String setStatus(String status) {
        this.status = status;
        return status;
    }

    public void addCollectorCollection(CollectorEntity collector) {
        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
        collectors.add(collectorCollection);
//        collector.getCollections().add(collectorCollection);
    }

    //update a collection of collector
//    public void updateCollectorCollection(CollectorEntity collector, String status) {
//        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
//        var collection = collectorCollection.getCollections();
//        collection.setName(collection.getName());
//        collection.setStatus(status);
//        collection.addCollectorCollection(collector);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionEntity that = (CollectionEntity) o;
        return id.equals(that.id) && Objects.equals(name, that.name) && Objects.equals(status, that.status) && Objects.equals(collectors, that.collectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, collectors);
    }
}
