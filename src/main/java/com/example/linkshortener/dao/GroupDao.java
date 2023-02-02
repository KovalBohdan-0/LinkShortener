package com.example.linkshortener.dao;

import com.example.linkshortener.entity.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class GroupDao implements Dao<Group> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupDao.class);
    @PersistenceContext
    private EntityManager entityManager;

    public GroupDao() {

    }

    @Override
    public Optional<Group> get(long id) {
        return Optional.ofNullable(entityManager.find(Group.class, id));
    }

    public Optional<Group> getByCode(String code) {
        TypedQuery<Group> query = entityManager.createQuery("FROM roles role WHERE role.code = :code ", Group.class);
        query.setParameter("code", code);
        List<Group> groupList = query.getResultList();

        if (groupList.size() == 0) {
            LOGGER.info("Group with this code was not found :" + code);
            return Optional.empty();
        }

        return Optional.of(groupList.get(0));
    }

    @Override
    public List<Group> getAll() {
        return entityManager.createQuery("FROM roles", Group.class)
                .getResultList();
    }

    public Set<Group> getGroupsByUserId(long id) {
        //TODO make method
        Query query = entityManager.createQuery("FROM roles role WHERE role.users = :user_id", Group.class).setParameter("user_id", id);
        Set<Group> userGroups = new HashSet<>(query.getResultList());
        LOGGER.info("Founded user with user groups: " + userGroups);
        return userGroups;
    }

    @Override
    public void save(Group group) {
        entityManager.persist(group);
    }

    @Override
    public void update(Group group) {
        entityManager.merge(group);
    }

    @Override
    public void delete(Group group) {
        entityManager.remove(group);
    }
}
