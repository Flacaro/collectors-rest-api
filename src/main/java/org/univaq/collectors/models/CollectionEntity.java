package org.univaq.collectors.models;
import com.fasterxml.jackson.annotation.*;
import org.univaq.collectors.UserView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity(name = "collection")
@Table(
        indexes = {
                @Index(name = "collection_name_index", columnList = "name"),
                @Index(name = "collector_type_index", columnList = "type")
        }
)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class CollectionEntity {

    @JsonView(UserView.Public.class)
    @Id
    @GeneratedValue
    private Long id;

    @JsonView(UserView.Public.class)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    //il tipo della collection: rock, pop ecc.
    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private String type;

    @JsonView(UserView.Public.class)
    @Column(nullable = false)
    private boolean isPublic;


    @JsonView(UserView.Private.class)
    @OneToMany(
            mappedBy = "collection",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference  //-> dico a json che una collezione molti a molti
    private List<CollectorCollectionEntity> collectionsCollectors = new ArrayList<>(); //lista di collezionisti con cui condivido la collezione



    public CollectionEntity() {
    }

    public CollectionEntity(Long id, String name, boolean isPublic, String type) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
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

//prendo collezioni pubbliche prima controllo
    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<CollectorCollectionEntity> getCollectionsCollectors() {
        return collectionsCollectors;
    }

    public void setCollectionsCollectors(List<CollectorCollectionEntity> collectors) {
        this.collectionsCollectors = collectors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addCollectorCollection(CollectorEntity collector) {
        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
        collectionsCollectors.add(collectorCollection);
    }



    //update a collection of collector
    public void updateCollectorCollection(String name, String type, boolean isPublic) {
        this.name = name;
        this.type = type;
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "CollectionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isPublic=" + isPublic +
                ", type=" + type +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionEntity that = (CollectionEntity) o;
        return isPublic == that.isPublic && id.equals(that.id) && Objects.equals(name, that.name)  && Objects.equals(collectionsCollectors, that.collectionsCollectors) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPublic, collectionsCollectors, type);
    }
}
