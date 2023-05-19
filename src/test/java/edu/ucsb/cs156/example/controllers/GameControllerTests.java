package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Game;
import edu.ucsb.cs156.example.repositories.GameRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = GameController.class)
@Import(TestConfig.class)
public class GameControllerTests extends ControllerTestCase {

    @MockBean
    GameRepository gameRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/Games/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/games/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/games/all"))
                .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/games?name=doom"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/Games/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/games/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/games/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        // arrange

        Game game = game.builder()
                .name("Doom")
                .publisher("id")
                .rating("E")
                .build();

        when(gameRepository.findById(eq("Doom"))).thenReturn(Optional.of(game));

        // act
        MvcResult response = mockMvc.perform(get("/api/games?name=Doom"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(gameRepository, times(1)).findById(eq("Doom"));
        String expectedJson = mapper.writeValueAsString(game);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

@WithMockUser(roles = { "USER" })
@Test
public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

// arrange

when(gameRepository.findById(eq("dne"))).thenReturn(Optional.empty());

// act
MvcResult response = mockMvc.perform(get("/api/games?name=dne"))
.andExpect(status().isNotFound()).andReturn();

// assert

verify(gameRepository, times(1)).findById(eq("dne"));
Map<String, Object> json = responseToJson(response);
assertEquals("EntityNotFoundException", json.get("type"));
assertEquals("Game with id dne not found", json.get("message"));
}

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_games() throws Exception {

        // arrange

        Game doom = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("E")
                .build();

        Game mario = Game.builder()
                .name("Mario")
                .publisher("Nintendo")
                .gender("E")
                .build();

        ArrayList<Game> expectedGames = new ArrayList<>();
        expectedGames.addAll(Arrays.asList(doom, mario));

        when(gameRepository.findAll()).thenReturn(expectedGames);

        // act
        MvcResult response = mockMvc.perform(get("/api/games/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(gameRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedGames);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_game() throws Exception {
        // arrange

        Game doom = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("E")
                .build();

        when(gameRepository.save(eq(doom))).thenReturn(doom);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/games/post?name=Doom&publisher=id&rating=E")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(gameRepository, times(1)).save(doom);
        String expectedJson = mapper.writeValueAsString(doom);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_game() throws Exception {
        // arrange

        Game doom = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("E")
                .build();

        when(gameRepository.findById(eq("Doom"))).thenReturn(Optional.of(doom));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/games?name=Doom")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(gameRepository, times(1)).findById("Doom");
        verify(gameRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("Game with id Doom deleted", json.get("message"));
    }

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_tries_to_delete_non_existant_game_and_gets_right_error_message()
throws Exception {
// arrange

when(gameRepository.findById(eq("dne"))).thenReturn(Optional.empty());

// act
MvcResult response = mockMvc.perform(
delete("/api/games?name=dne")
                .with(csrf()))
.andExpect(status().isNotFound()).andReturn();

// assert
verify(gameRepository, times(1)).findById("dne");
Map<String, Object> json = responseToJson(response);
assertEquals("Game with id dne not found", json.get("message"));
}

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_game() throws Exception {
        // arrange

        Game doomOriginal = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("E")
                .build();

        Game doomEdited = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("M")
                .build();


        String requestBody = mapper.writeValueAsString(doomEdited);

        when(gameRepository.findById(eq("Doom"))).thenReturn(Optional.of(doomOriginal));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/games?name=Doom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(gameRepository, times(1)).findById("Doom");
        verify(gameRepository, times(1)).save(doomEdited); // should be saved with updated info
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_game_that_does_not_exist() throws Exception {
        // arrange

        Game doomEdited = Game.builder()
                .name("Doom")
                .publisher("id")
                .rating("M")
                .build();

        String requestBody = mapper.writeValueAsString(doomEdited);

        when(gameRepository.findById(eq("Doom"))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/games?name=Doom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(gameRepository, times(1)).findById("Doom");
        Map<String, Object> json = responseToJson(response);
        assertEquals("Game with id Doom not found", json.get("message"));

    }
}
