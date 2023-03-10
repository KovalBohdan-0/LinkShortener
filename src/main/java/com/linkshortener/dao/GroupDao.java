package com.linkshortener.dao;

import com.linkshortener.entity.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class that allows to control database group entities.
 *
 * @author Bohdan Koval
 * @see com.linkshortener.dao.Dao
 * @see Group
 */
@Repository
public class GroupDao implements Dao<Group> {
    @PersistenceContext
    private EntityManager entityManager;

    public GroupDao() {

    }

    @Override
    public Optional<Group> get(long id) {
        return Optional.ofNullable(entityManager.find(Group.class, id));
    }

    /**
     * Returns group by code, if nothing found, returns empty Optional
     *
     * @param code the code of group
     * @return the found group
     */
    public Optional<Group> getByCode(String code) {
        TypedQuery<Group> query = entityManager.createQuery("FROM roles role WHERE role.code = :code ", Group.class);
        query.setParameter("code", code);
        List<Group> groupList = query.getResultList();

        if (groupList.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(groupList.get(0));
    }

    @Override
    public List<Group> getAll() {
        return entityManager.createQuery("FROM roles", Group.class)
                .getResultList();
    }

    /**
     * Returns groups by users' id, if nothing found, returns empty Set
     *
     * @param id the id of user
     * @return set of users group
     */
    @SuppressWarnings("unchecked")
    public Set<Group> getGroupsByUserId(long id) {
        Query query = entityManager.createQuery("SELECT role FROM roles role JOIN FETCH role.users user WHERE user.id =: user_id", Group.class).setParameter("user_id", id);

        return new HashSet<Group>(query.getResultList());
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

    @Override
    public void deleteAll() {
        Query query = entityManager.createQuery("DELETE FROM roles");
        query.executeUpdate();
    }
}
