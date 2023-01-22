package com.example.linkshortener.dao;

import com.example.linkshortener.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User>{
    @PersistenceContext
    private EntityManager entityManager;
    private final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public UserDao() {

    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public User getByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("FROM users u WHERE u.email = :email ", User.class);
        query.setParameter("email", username);
        List<User> list = query.getResultList();

        if (list.size() == 0) {
            LOGGER.error("User with this email was not found :" + username);
            throw new UsernameNotFoundException("User with this email was not found :" + username);
        }

        return list.get(0);
    }

    @Override
    public List<User> getAll() {
        return entityManager.createQuery( "from users", User.class ).getResultList();
    }

    @Override
    public void save(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
