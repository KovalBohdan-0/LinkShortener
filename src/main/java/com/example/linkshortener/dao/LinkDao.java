package com.example.linkshortener.dao;

import com.example.linkshortener.entity.Link;
import com.example.linkshortener.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO make one implementation
@Repository
public class LinkDao implements Dao<Link> {

    private final static Logger LOGGER = LoggerFactory.getLogger(LinkDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    public LinkDao() {

    }

    @Override
    public Optional<Link> get(long id) {
        return Optional.ofNullable(entityManager.find(Link.class, id));
    }

    public Optional<Link> getLinkByShortLink(String shortLink) {
        TypedQuery<Link> query = entityManager.createQuery("FROM Link u WHERE u.shortLink = :shortLink ", Link.class);
        query.setParameter("shortLink", shortLink);
        List<Link> list = query.getResultList();

        if (list.size() == 0) {
            LOGGER.info("Link with this short link was not found :" + shortLink);
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public List<Link> getAll() {
        return entityManager.createQuery( "from Link", Link.class )
                .getResultList();
    }

    @Override
    public void save(Link link) {
        entityManager.persist(link) ;
    }

    @Override
    public void update(Link link) {
        entityManager.merge(link);
    }

    @Override
    public void delete(Link link) {
        entityManager.remove(link);
    }
}
