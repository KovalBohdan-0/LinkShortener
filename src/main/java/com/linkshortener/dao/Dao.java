package com.linkshortener.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Dao<T> {
    Optional<T> get(long id);

    List<T> getAll();

    void save(T t);

    void update(T t);

    void delete(T t);

    void deleteAll();
}
