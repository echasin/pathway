package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Vector.
 */
@Entity
@Table(name = "vector")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "vector")
public class Vector implements Serializable {

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

    @ManyToOne
    private Pathwayrecordtype pathwayrecordtype;

    @ManyToOne
    private Pathwayclass pathwayclass;

    @ManyToOne
    private Pathwaycategory pathwaycategory;

    @ManyToOne
    private Pathwaytype pathwaytype;

    @ManyToOne
    private Pathway pathway;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Vector name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameshort() {
        return nameshort;
    }

    public Vector nameshort(String nameshort) {
        this.nameshort = nameshort;
        return this;
    }

    public void setNameshort(String nameshort) {
        this.nameshort = nameshort;
    }

    public String getOriginjson() {
        return originjson;
    }

    public Vector originjson(String originjson) {
        this.originjson = originjson;
        return this;
    }

    public void setOriginjson(String originjson) {
        this.originjson = originjson;
    }

    public String getDestinationjson() {
        return destinationjson;
    }

    public Vector destinationjson(String destinationjson) {
        this.destinationjson = destinationjson;
        return this;
    }

    public void setDestinationjson(String destinationjson) {
        this.destinationjson = destinationjson;
    }

    public String getDescription() {
        return description;
    }

    public Vector description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public Vector status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public Vector lastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
        return this;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public Vector lastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
        return this;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public Vector domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Pathwayrecordtype getPathwayrecordtype() {
        return pathwayrecordtype;
    }

    public Vector pathwayrecordtype(Pathwayrecordtype pathwayrecordtype) {
        this.pathwayrecordtype = pathwayrecordtype;
        return this;
    }

    public void setPathwayrecordtype(Pathwayrecordtype pathwayrecordtype) {
        this.pathwayrecordtype = pathwayrecordtype;
    }

    public Pathwayclass getPathwayclass() {
        return pathwayclass;
    }

    public Vector pathwayclass(Pathwayclass pathwayclass) {
        this.pathwayclass = pathwayclass;
        return this;
    }

    public void setPathwayclass(Pathwayclass pathwayclass) {
        this.pathwayclass = pathwayclass;
    }

    public Pathwaycategory getPathwaycategory() {
        return pathwaycategory;
    }

    public Vector pathwaycategory(Pathwaycategory pathwaycategory) {
        this.pathwaycategory = pathwaycategory;
        return this;
    }

    public void setPathwaycategory(Pathwaycategory pathwaycategory) {
        this.pathwaycategory = pathwaycategory;
    }

    public Pathwaytype getPathwaytype() {
        return pathwaytype;
    }

    public Vector pathwaytype(Pathwaytype pathwaytype) {
        this.pathwaytype = pathwaytype;
        return this;
    }

    public void setPathwaytype(Pathwaytype pathwaytype) {
        this.pathwaytype = pathwaytype;
    }

    public Pathway getPathway() {
        return pathway;
    }

    public Vector pathway(Pathway pathway) {
        this.pathway = pathway;
        return this;
    }

    public void setPathway(Pathway pathway) {
        this.pathway = pathway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vector vector = (Vector) o;
        if (vector.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vector.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Vector{" +
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
