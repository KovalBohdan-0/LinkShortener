package com.linkshortener.dao;

import com.linkshortener.entity.Group;
import com.linkshortener.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({GroupDao.class, UserDao.class})
class GroupDaoTest {
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    @Rollback(false)
    void setUp() {
        groupDao.save(new Group("Manager", "Manager role"));
    }

    @Test
    void shouldGetGroupById() {
        long idOfTestedGroup = groupDao.getAll().get(0).getId();
        Optional<Group> group = groupDao.get(idOfTestedGroup);

        boolean isFound = group.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetGroupByCode() {
        Optional<Group> group = groupDao.getByCode("Manager");

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
        User user = new User("email", "pass");
        user.setRoles(Set.of(groupDao.getByCode("Manager").orElseThrow()));
        userDao.save(user);
        long userId = userDao.getByUsername("email").get().getId();
        Set<Group> foundGroups = groupDao.getGroupsByUserId(userId);

        boolean groupIsFound = foundGroups.contains(groupDao.getByCode("Manager").orElseThrow());

        assertThat(groupIsFound).isTrue();
    }

    @Test
    void shouldGetAllGroups() {
        groupDao.save(new Group("Customer", "Customer role"));
        List<Group> groups = groupDao.getAll();

        boolean containTwoGroups = groups.size() == 2;

        assertThat(containTwoGroups).isTrue();
    }

    @Test
    void shouldSaveGroup() {
        Group group = new Group("User", "User role");
        boolean firstGroupIsSaved = groupDao.getAll().size() == 1;
        groupDao.save(group);

        boolean groupIsSaved  = groupDao.getAll().size() == 2;

        assertThat(firstGroupIsSaved).isTrue();
        assertThat(groupIsSaved).isTrue();
    }

    @Test
    void shouldUpdateGroup() {
        Group group = groupDao.getAll().get(0);
        group.setCode("Updated");
        groupDao.update(group);

        Group updatedGroup = groupDao.getAll().get(0);
        boolean isSaved = updatedGroup.getCode().equals("Updated");

        assertThat(isSaved).isTrue();
    }

    @Test
    void shouldDeleteRole() {
        Group group = groupDao.getAll().get(0);
        groupDao.delete(group);

        Optional<Group> deletedUser = groupDao.get(group.getId());
        boolean deletedGroupIsNotPresent = deletedUser.isEmpty();

        assertThat(deletedGroupIsNotPresent).isTrue();
    }

    @Test
    void shouldDeleteAllRoles() {
        groupDao.save(new Group("deleteAll", "to delete all"));
        groupDao.deleteAll();

        boolean isDeleted = groupDao.getAll().size() == 0;

        assertThat(isDeleted).isTrue();
    }

}