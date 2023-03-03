package com.linkshortener.dao;

import com.linkshortener.entity.Link;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LinkDao.class)
class LinkDaoTest {
    @Autowired
    private LinkDao linkDao;

    @Test
    void shouldGetLinkById() {
        Optional<Link> link = linkDao.get(1);

        boolean isFound = link.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetLinkByAlias() {
        Optional<Link> link = linkDao.getLinkByAlias("alias");

        boolean isFound = link.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetUsersLinkByAlias() {
        Optional<Link> link = linkDao.getUsersLinkByAlias("alias", 1);

        boolean isFound = link.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetAllLinks() {
        List<Link> links = linkDao.getAll();

        boolean containTwoLinks = links.size() == 1;

        assertThat(containTwoLinks).isTrue();
    }

    @Test
    void shouldSaveLink() {
        Link Link = new Link("https://www.youtube.com", "service/you.com");
        linkDao.save(Link);

        boolean savedLinkIsFound = linkDao.getLinkByAlias("service/you.com").isPresent();

        assertThat(savedLinkIsFound).isTrue();
    }

    @Test
    void shouldUpdateLink() {
        Optional<Link> link = linkDao.getLinkByAlias("alias");
        link.orElseThrow().setAlias("updatedLink");
        linkDao.update(link.get());

        boolean updatedLinkIsFound = linkDao.getLinkByAlias("updatedLink").isPresent();
        boolean previousLinkIsFound = linkDao.getLinkByAlias("alias").isPresent();

        assertThat(updatedLinkIsFound).isTrue();
        assertThat(previousLinkIsFound).isFalse();
    }

    @Test
    void shouldDeleteLink() {
        Optional<Link> link = linkDao.getLinkByAlias("alias");
        linkDao.delete(link.orElseThrow());

        boolean deletedLinkIsFound = linkDao.getLinkByAlias("service/t.com").isPresent();

        assertThat(deletedLinkIsFound).isFalse();
    }

    @Test
    void shouldDeleteAllLinks() {
        linkDao.deleteAll();

        boolean isDeleted = linkDao.getAll().size() == 0;

        assertThat(isDeleted).isTrue();
    }
}