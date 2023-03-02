package com.linkshortener.dao;

import com.linkshortener.entity.Link;
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
@Import(LinkDao.class)
class LinkDaoTest {
    @Autowired
    private LinkDao linkDao;
    private long testLinkId = 0;

    @BeforeEach
    @Rollback(false)
    void setUp() {
        Link link = new Link("twitch.tv/", "service/t.com");
        linkDao.save(link);
        testLinkId = link.getId();
    }

    @Test
    void shouldGetLinkById() {
        Optional<Link> link = linkDao.get(testLinkId);

        boolean isFound = link.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetLinkByAlias() {
        Optional<Link> link = linkDao.getLinkByAlias("service/t.com");

        boolean isFound = link.isPresent();

        assertThat(isFound).isTrue();
    }

    @Test
    void shouldGetAllLinks() {
        linkDao.save(new Link("https://www.youtube.com", "service/you.com"));
        List<Link> links = linkDao.getAll();

        boolean containTwoLinks = links.size() == 2;

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
        Optional<Link> link = linkDao.getLinkByAlias("service/t.com");
        link.get().setAlias("updatedLink");
        linkDao.update(link.get());

        boolean updatedLinkIsFound = linkDao.getLinkByAlias("updatedLink").isPresent();
        boolean previousLinkIsFound = linkDao.getLinkByAlias("service/t.com").isPresent();

        assertThat(updatedLinkIsFound).isTrue();
        assertThat(previousLinkIsFound).isFalse();
    }

    @Test
    void shouldDeleteLink() {
        Optional<Link> link = linkDao.getLinkByAlias("service/t.com");
        linkDao.delete(link.get());

        boolean deletedLinkIsFound = linkDao.getLinkByAlias("service/t.com").isPresent();

        assertThat(deletedLinkIsFound).isFalse();
    }

    @Test
    void shouldDeleteAllLinks() {
        linkDao.save(new Link("linkToDelete", "shortLinkToDelete"));
        linkDao.deleteAll();

        boolean isDeleted = linkDao.getAll().size() == 0;

        assertThat(isDeleted).isTrue();
    }
}