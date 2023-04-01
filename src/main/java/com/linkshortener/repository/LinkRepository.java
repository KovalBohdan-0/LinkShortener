package com.linkshortener.repository;

import com.linkshortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class that allows to control database link entities.
 *
 * @author Bohdan Koval
 * @see Link
 */

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findLinksByAlias(String alias);

    Optional<Link> findLinkByUserIdAndAlias(long userId, String alias);

    @Modifying
    @Query("UPDATE Link u set u.alias = :alias, u.fullLink = :fullLink, u.views =:views where u.id = :id")
    void update(@Param(value = "id") long id, @Param(value = "alias") String alias, @Param("fullLink") String fullLink, @Param("views") Long views);

    void deleteAllByUserId(long userId);
}
