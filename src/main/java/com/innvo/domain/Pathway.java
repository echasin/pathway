package com.innvo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Pathway.
 */
@Entity
@Table(name = "pathway")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "pathway")
public class Pathway implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 25)
    @Column(name = "nameshort", length = 25, nullable = false)
    private String nameshort;

    @Size(max = 4000)
    @Column(name = "originjson", length = 4000)
    private String originjson;

    @Size(max = 4000)
    @Column(name = "destinationjson", length = 4000)
    private String destinationjson;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @Size(max = 25)
    @Column(name = "status", length = 25, nullable = false)
    private String status;

    @NotNull
    @Size(max = 50)
    @Column(name = "lastmodifiedby", length = 50, nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Size(max = 25)
    @Column(name = "domain", length = 25, nullable = false)
    private String domain;

    @OneToMany(mappedBy = "pathway")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Vector> vectors = new HashSet<>();

    @ManyToOne
    private Pathwayrecordtype pathwayrecordtype;

    @ManyToOne
    private Pathwayclass pathwayclass;

    @ManyToOne
    private Pathwaycategory pathwaycategory;

    @ManyToOne
    private Pathwaytype pathwaytype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Pathway name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameshort() {
        return nameshort;
    }

    public Pathway nameshort(String nameshort) {
        this.nameshort = nameshort;
        return this;
    }

    public void setNameshort(String nameshort) {
        this.nameshort = nameshort;
    }

    public String getOriginjson() {
        return originjson;
    }

    public Pathway originjson(String originjson) {
        this.originjson = originjson;
        return this;
    }

    public void setOriginjson(String originjson) {
        this.originjson = originjson;
    }

    public String getDestinationjson() {
        return destinationjson;
    }

    public Pathway destinationjson(String destinationjson) {
        this.destinationjson = destinationjson;
        return this;
    }

    public void setDestinationjson(String destinationjson) {
        this.destinationjson = destinationjson;
    }

    public String getDescription() {
        return description;
    }

    public Pathway description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public Pathway status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public Pathway lastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
        return this;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public Pathway lastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
        return this;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public Pathway domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Set<Vector> getVectors() {
        return vectors;
    }

    public Pathway vectors(Set<Vector> vectors) {
        this.vectors = vectors;
        return this;
    }

    public Pathway addVector(Vector vector) {
        this.vectors.add(vector);
        vector.setPathway(this);
        return this;
    }

    public Pathway removeVector(Vector vector) {
        this.vectors.remove(vector);
        vector.setPathway(null);
        return this;
    }

    public void setVectors(Set<Vector> vectors) {
        this.vectors = vectors;
    }

    public Pathwayrecordtype getPathwayrecordtype() {
        return pathwayrecordtype;
    }

    public Pathway pathwayrecordtype(Pathwayrecordtype pathwayrecordtype) {
        this.pathwayrecordtype = pathwayrecordtype;
        return this;
    }

    public void setPathwayrecordtype(Pathwayrecordtype pathwayrecordtype) {
        this.pathwayrecordtype = pathwayrecordtype;
    }

    public Pathwayclass getPathwayclass() {
        return pathwayclass;
    }

    public Pathway pathwayclass(Pathwayclass pathwayclass) {
        this.pathwayclass = pathwayclass;
        return this;
    }

    public void setPathwayclass(Pathwayclass pathwayclass) {
        this.pathwayclass = pathwayclass;
    }

    public Pathwaycategory getPathwaycategory() {
        return pathwaycategory;
    }

    public Pathway pathwaycategory(Pathwaycategory pathwaycategory) {
        this.pathwaycategory = pathwaycategory;
        return this;
    }

    public void setPathwaycategory(Pathwaycategory pathwaycategory) {
        this.pathwaycategory = pathwaycategory;
    }

    public Pathwaytype getPathwaytype() {
        return pathwaytype;
    }

    public Pathway pathwaytype(Pathwaytype pathwaytype) {
        this.pathwaytype = pathwaytype;
        return this;
    }

    public void setPathwaytype(Pathwaytype pathwaytype) {
        this.pathwaytype = pathwaytype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pathway pathway = (Pathway) o;
        if (pathway.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), pathway.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Pathway{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", nameshort='" + getNameshort() + "'" +
            ", originjson='" + getOriginjson() + "'" +
            ", destinationjson='" + getDestinationjson() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", lastmodifiedby='" + getLastmodifiedby() + "'" +
            ", lastmodifieddatetime='" + getLastmodifieddatetime() + "'" +
            ", domain='" + getDomain() + "'" +
            "}";
    }
}
