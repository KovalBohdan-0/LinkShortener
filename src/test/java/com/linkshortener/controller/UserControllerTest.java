package com.linkshortener.controller;

import com.linkshortener.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(username="admin", authorities = {"ADMIN"})
    void shouldGetUsersByAdmin() throws Exception {
        this.mockMvc.perform(get("/api/users")).andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(username="user", authorities = {"USER"})
    void shouldNotGetUsersByUser() throws Exception {
        this.mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@gmail.com\", \"password\":  \"pass\"}"))
                .andExpect(status().isOk());

        verify(userService).addUser(any(), any());
    }

    @Test
    void shouldNotRegisterInvalidUser() throws Exception {
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@gmail.com\"}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(), any());
    }

    @Test
    @WithMockUser(username="admin", authorities = {"ADMIN"})
    void shouldRegisterAdmin() throws Exception {
        this.mockMvc.perform(post("/api/users/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"admin@gmail.com\", \"password\":  \"pass\"}"))
                .andExpect(status().isOk());

        verify(userService).addUser(any(), any());
    }


    @Test
    @WithMockUser(username="admin", authorities = {"ADMIN"})
    void shouldNotRegisterInvalidAdmin() throws Exception {
        this.mockMvc.perform(post("/api/users/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"admin@gmail.com\"}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(), any());
    }

    @Test
    @WithMockUser(username="user", authorities = {"USER"})
    void shouldNotRegisterAdminByUser() throws Exception {
        this.mockMvc.perform(post("/api/users/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"admin@gmail.com\", \"password\":  \"pass\"}"))
                .andExpect(status().isForbidden());

        verify(userService, never()).addUser(any(), any());
    }

    @Test
    @WithMockUser(username="admin", authorities = {"ADMIN"})
    void shouldDeleteUsersByAdmin() throws Exception {
        this.mockMvc.perform(delete("/api/users")).andExpect(status().isOk());

        verify(userService).removeAllUsers();
    }

    @Test
    @WithMockUser(username="user", authorities = {"USER"})
    void shouldNotDeleteUsersByUser() throws Exception {
        this.mockMvc.perform(delete("/api/users")).andExpect(status().isForbidden());

        verify(userService, never()).removeAllUsers();
    }

    @Test
    void shouldLogin() throws Exception {
        this.mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@gmail.com\", \"password\":  \"pass\"}"))
                .andExpect(status().isOk());

        verify(userService).getRegistrationResponse(any());
    }

    @Test
    void shouldNotLoginToInvalidUser() throws Exception {
        this.mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@gmail.com\"}"))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
        verify(userService, never()).getRegistrationResponse(any());
    }
}