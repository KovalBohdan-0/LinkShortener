package com.linkshortener.repository;

import com.linkshortener.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Class that allows to control database group entities.
 *
 * @author Bohdan Koval
 * @see Group
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByCode(String code);

    Set<Group> findGroupByUsersId(long id);

    @Modifying
    @Query("UPDATE Group u SET u.code = :code WHERE u.id = :id")
    void update(@Param(value = "id") long id, @Param(value = "code") String code);

}
