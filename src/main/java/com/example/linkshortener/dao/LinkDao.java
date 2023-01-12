package com.example.linkshortener.dao;

import com.example.linkshortener.entity.Link;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class LinkDao implements Dao<Link> {

    private final static Logger LOGGER = LoggerFactory.getLogger(LinkDao.class);

    private final SessionFactory sessionFactory;

    public LinkDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Optional<Link> get(long id) {
        Session session = sessionFactory.getCurrentSession();
        Optional<Link> link = Optional.ofNullable(session.get(Link.class, id));

        return link;
    }

    @Override
    public List<Link> getAll() {
        Session session = sessionFactory.getCurrentSession();
        List<Link> links = session.createQuery("SELECT a FROM Link a", Link.class).getResultList();
        LOGGER.info(links.toString());

        return links;
    }

    @Transactional
    @Override
    public void save(Link link) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(link);
        session.flush();
    }

    @Transactional
    @Override
    public void update(Link link, String[] params) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(link);
        session.flush();
    }

    @Transactional
    @Override
    public void delete(Link link) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(link);
        session.flush();
    }
}
