package com.example.linkshortener.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "links", schema = "public", catalog = "link_shortener")
public class Link {
    @Basic
    @NotBlank(message = "Full link is mandatory")
    @Column(name = "full_link")
    private String fullLink;
    @Basic
    @NotBlank(message = "Short link is mandatory")
    @Column(name = "short_link")
    private String shortLink;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;
    @Basic
    @Column(name = "user_id")
    @Min(value=1,message="User id is mandatory")
    private long userId;

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link that = (Link) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (fullLink != null ? !fullLink.equals(that.fullLink) : that.fullLink != null) return false;
        if (shortLink != null ? !shortLink.equals(that.shortLink) : that.shortLink != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fullLink != null ? fullLink.hashCode() : 0;
        result = 31 * result + (shortLink != null ? shortLink.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}
