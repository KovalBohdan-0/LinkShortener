package com.linkshortener.dao;

import com.linkshortener.entity.Link;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public Optional<Link> getLinkByAlias(String alias) {
        TypedQuery<Link> query = entityManager.createQuery("FROM Link link WHERE link.alias = :alias ", Link.class);
        query.setParameter("alias", alias);
        List<Link> list = query.getResultList();

        if (list.size() == 0) {
            LOGGER.warn("Link with alias :{} was not found", alias);

            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public List<Link> getAll() {
        return entityManager.createQuery( "FROM Link", Link.class )
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

    @Override
    public void deleteAll() {
        Query query = entityManager.createQuery("DELETE FROM Link");
        query.executeUpdate();
    }

    public void deleteAllByUserId(long id) {
        Query query = entityManager.createQuery("DELETE FROM Link link WHERE link.user.id = :userId").setParameter("userId", id);
        query.executeUpdate();
    }
}
