package org.univaq.collectors.models;
import java.util.ArrayList;
import java.util.List;
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

    private boolean shared;


    @OneToMany(
            mappedBy = "collections",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CollectorCollectionEntity> collectors = new ArrayList<>();


    public CollectionEntity(Long id, String name, String status, boolean shared, List<CollectorCollectionEntity> collectors) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.shared = shared;
        this.collectors = collectors;
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

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public List<CollectorCollectionEntity> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<CollectorCollectionEntity> collectors) {
        this.collectors = collectors;
    }


    public void addCollectionToCollector(CollectorEntity collector) {
        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
        collectors.add(collectorCollection);
    
    }


    public void removeCollectionOfCollector(CollectorEntity collector) {
        CollectorCollectionEntity collectorCollection = new CollectorCollectionEntity(collector, this, true);
        collectors.remove(collectorCollection);
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + (shared ? 1231 : 1237);
        result = prime * result + ((collectors == null) ? 0 : collectors.hashCode());
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
        CollectionEntity other = (CollectionEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (shared != other.shared)
            return false;
        if (collectors == null) {
            if (other.collectors != null)
                return false;
        } else if (!collectors.equals(other.collectors))
            return false;
        return true;
    }



    @Override
    public String toString() {
        return "Collection [id=" + id + ", name=" + name + ", status=" + status + ", shared=" + shared + ", collectors="
                + collectors + "]";
    }

    

    
  
    

}
