package com.linkshortener.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO add validation tests
@SpringBootTest
@AutoConfigureMockMvc
class LinkControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LinkController linkController;

    @Test
    @WithMockUser
    void shouldGetAllLinks() throws Exception {
        this.mockMvc.perform(get("/api/links")).andExpect(status().isOk());

        verify(linkController).getLinks();
    }

    @Test
    void shouldNotGetAllLinksToAnonymous() throws Exception {
        this.mockMvc.perform(get("/api/links")).andExpect(status().is3xxRedirection());

        verify(linkController, never()).getLinks();
    }

    @Test
    void shouldAddLink() throws Exception {
        this.mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullLink\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\", \"alias\":  \"linkShortener.com/youtube\"}")).andExpect(status().isOk());

        verify(linkController).addLink(any());
    }

    @Test
    @WithMockUser
    void shouldRemoveLink() throws Exception {
        this.mockMvc.perform(delete("/api/links/{id}", 1L)).andExpect(status().isOk());

        verify(linkController).removeLink(anyLong());
    }

    @Test
    void shouldNotRemoveLinkToAnonymous() throws Exception {
        this.mockMvc.perform(delete("/api/links/{id}", 1L)).andExpect(status().is3xxRedirection());

        verify(linkController, never()).removeLink(anyLong());
    }

    @Test
    @WithMockUser
    void shouldRemoveAllLinks() throws Exception {
        this.mockMvc.perform(delete("/api/links")).andExpect(status().isOk());

        verify(linkController).removeLinks();
    }

    @Test
    void shouldNotRemoveAllLinksToAnonymous() throws Exception {
        this.mockMvc.perform(delete("/api/links")).andExpect(status().is3xxRedirection());

        verify(linkController, never()).removeLinks();
    }
}