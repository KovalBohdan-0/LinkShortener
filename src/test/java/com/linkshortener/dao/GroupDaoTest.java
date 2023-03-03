package com.linkshortener.dao;

import com.linkshortener.entity.Group;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({GroupDao.class, UserDao.class})
class GroupDaoTest {
    @Autowired
    private GroupDao groupDao;

    @Test
    void shouldGetGroupById() {
        Optional<Group> group = groupDao.get(1);

        boolean isFound = group.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetGroupByCode() {
        Optional<Group> group = groupDao.getByCode("ADMIN");

        boolean isFound = group.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldNotGetGroupByNotExistingCode() {
        Optional<Group> group = groupDao.getByCode("Not present");

        boolean isNotFound = group.isEmpty();

        assertThat(isNotFound).isTrue();
    }

    @Test
    void shouldGetGroupByUserId() {
        Set<Group> foundGroups = groupDao.getGroupsByUserId(1);

        boolean groupIsFound = foundGroups.contains(groupDao.getByCode("ADMIN").orElseThrow());

        assertThat(groupIsFound).isTrue();
    }

    @Test
    void shouldGetAllGroups() {
        List<Group> groups = groupDao.getAll();

        boolean containTwoGroups = groups.size() == 2;

        assertThat(containTwoGroups).isTrue();
    }

    @Test
    void shouldSaveGroup() {
        Group group = new Group("User1", "User role");
        boolean firstGroupIsSaved = groupDao.getAll().size() == 2;
        groupDao.save(group);

        boolean groupIsSaved = groupDao.getAll().size() == 3;

        assertThat(firstGroupIsSaved).isTrue();
        assertThat(groupIsSaved).isTrue();
    }

    @Test
    void shouldUpdateGroup() {
        Group group = groupDao.get(1).orElseThrow();
        group.setCode("Updated");
        groupDao.update(group);

        Group updatedGroup = groupDao.get(1).orElseThrow();
        boolean isSaved = updatedGroup.getCode().equals("Updated");

        assertThat(isSaved).isTrue();
    }

    @Test
    void shouldDeleteRole() {
        Group group = groupDao.get(1).orElseThrow();
        groupDao.delete(group);

        Optional<Group> deletedUser = groupDao.get(group.getId());
        boolean deletedGroupIsNotPresent = deletedUser.isEmpty();

        assertThat(deletedGroupIsNotPresent).isTrue();
    }

    @Test
    void shouldDeleteAllRoles() {
        groupDao.deleteAll();

        boolean isDeleted = groupDao.getAll().size() == 0;

        assertThat(isDeleted).isTrue();
    }

}