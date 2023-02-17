package com.linkshortener.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface that allows to control database entities.
 *
 * @author Bohdan Koval
 */
@Repository
public interface Dao<T> {
    /**
     * Returns entity by id, if not found returns empty Optional
     *
     * @param id the id of entity
     */
    Optional<T> get(long id);

    /**
     * Returns all entities, if not found returns empty List
     */
    List<T> getAll();

    /**
     * Saves a given entity.
     *
     * @param entity the entity to save, must not be null
     */
    void save(T entity);

    /**
     * Updates a given entity.
     *
     * @param entity the entity to update, must not be null
     */
    void update(T entity);

    /**
     * Deletes a given entity.
     *
     * @param entity the entity to delete, must not be null
     */
    void delete(T entity);

    /**
     * Deletes all entities of this type.
     */
    void deleteAll();
}
