package com.linkshortener.controller;

import com.linkshortener.entity.Link;
import com.linkshortener.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LinkControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LinkService linkService;

    @Test
    void shouldRedirectToUrl() throws Exception {
        when(linkService.getLinkByAlias(anyString())).thenReturn(Optional.of(new Link("fullLink", "alias")));

        this.mockMvc.perform(get("/{alias}", "alias")).andExpect(status().isFound());

        verify(linkService).getLinkByAlias(anyString());
    }

    @Test
    void shouldNotRedirectToNotFoundedUrl() throws Exception {
        when(linkService.getLinkByAlias(anyString())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/{alias}", "alias")).andExpect(status().isNotFound());

        verify(linkService).getLinkByAlias(anyString());
    }

    @Test
    @WithMockUser
    void shouldGetAllLinks() throws Exception {
        this.mockMvc.perform(get("/api/links")).andExpect(status().isOk());

        verify(linkService).getAllLinks();
    }

    @Test
    void shouldNotGetAllLinksToAnonymous() throws Exception {
        this.mockMvc.perform(get("/api/links")).andExpect(status().is3xxRedirection());

        verify(linkService, never()).getAllLinks();
    }

    @Test
    void shouldAddLink() throws Exception {
        this.mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullLink\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\", \"alias\":  \"linkShortener.com/youtube\"}")).andExpect(status().isOk());

        verify(linkService).addLink(any());
    }

    @Test
    void shouldNotAddLinkWhenThisAliasAlreadyExist() throws Exception {
        when(linkService.getLinkByAlias(anyString())).thenReturn(Optional.of(new Link()));

        this.mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullLink\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\", \"alias\":  \"linkShortener.com/youtube\"}")).andExpect(status().isConflict());

        verify(linkService, never()).addLink(any());
    }

    @Test
    void shouldNotAddInvalidLink() throws Exception {
        this.mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullLink\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\"}")).andExpect(status().isBadRequest());

        verify(linkService, never()).addLink(any());
    }

    @Test
    @WithMockUser
    void shouldRemoveLink() throws Exception {
        when(linkService.getLinkById(anyLong())).thenReturn(Optional.of(new Link()));
        this.mockMvc.perform(delete("/api/links/{id}", 1L)).andExpect(status().isOk());

        verify(linkService).removeLink(anyLong());
    }

    @Test
    @WithMockUser
    void shouldNotRemoveLinkWhenNotFound() throws Exception {
        when(linkService.getLinkById(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/api/links/{id}", 1L)).andExpect(status().isNotFound());

        verify(linkService, never()).removeLink(anyLong());
    }

    @Test
    void shouldNotRemoveLinkToAnonymous() throws Exception {
        this.mockMvc.perform(delete("/api/links/{id}", 1L)).andExpect(status().is3xxRedirection());

        verify(linkService, never()).removeLink(anyLong());
    }

    @Test
    @WithMockUser
    void shouldRemoveAllLinks() throws Exception {
        this.mockMvc.perform(delete("/api/links")).andExpect(status().isOk());

        verify(linkService).removeAllLinks();
    }

    @Test
    void shouldNotRemoveAllLinksToAnonymous() throws Exception {
        this.mockMvc.perform(delete("/api/links")).andExpect(status().is3xxRedirection());

        verify(linkService, never()).removeAllLinks();
    }
}