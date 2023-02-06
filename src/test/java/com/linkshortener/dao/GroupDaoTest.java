package com.linkshortener.dao;

import com.linkshortener.entity.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(GroupDao.class)
class GroupDaoTest {
    @Autowired
    private GroupDao groupDao;

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

}