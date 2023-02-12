package com.linkshortener.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@Table(name = "links", schema = "public")
public class Link {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;
    @Basic
    @NotBlank(message = "Full link is mandatory")
    @Column(name = "full_link")
    private String fullLink;
    @Basic
    @NotBlank(message = "Alias is mandatory")
    @Column(name = "alias")
    private String alias;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Link() {

    }

    public Link(String fullLink, String alias) {
        this.fullLink = fullLink;
        this.alias = alias;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return id == link.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Link{" +
                "fullLink='" + fullLink + '\'' +
                ", alias='" + alias + '\'' +
                ", id=" + id +
                '}';
    }
}
