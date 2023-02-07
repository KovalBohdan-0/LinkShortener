package com.linkshortener.service;

import com.linkshortener.dao.LinkDao;
import com.linkshortener.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class LinkServiceTest {
    @InjectMocks
    private LinkService linkService;
    @Mock
    private LinkDao linkDao;
    @Mock
    private UserDao userDao;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkDao, userDao);
    }

    @Test
    void getAllLinks() {
    }
}