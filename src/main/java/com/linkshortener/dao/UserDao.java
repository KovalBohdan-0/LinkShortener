package com.linkshortener.dao;

import com.linkshortener.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class that allows to control database user entities.
 *
 * @author Bohdan Koval
 * @see com.linkshortener.dao.Dao
 * @see User
 */
@Repository
public class UserDao implements Dao<User>{
    @PersistenceContext
    private EntityManager entityManager;

    public UserDao() {

    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    /**
     * Returns group by username, if nothing found, returns empty Optional
     *
     * @param username the code of user
     * @return the found user
     */
    public Optional<User> getByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("FROM users user WHERE user.email = :email ", User.class);
        query.setParameter("email", username);
        List<User> list = query.getResultList();

        if (list.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public List<User> getAll() {
        return entityManager.createQuery( "FROM users", User.class ).getResultList();
    }

    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        entityManager.remove(user);
    }

    @Override
    public void deleteAll() {
        Query queryToDeleteLinks = entityManager.createQuery("DELETE FROM Link");
        Query query = entityManager.createQuery("DELETE FROM users");
        queryToDeleteLinks.executeUpdate();
        query.executeUpdate();
    }
}
