package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Shoe;
import edu.ucsb.cs156.example.repositories.ShoeRepository;

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

//import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ShoeController.class)
@Import(TestConfig.class)
public class ShoeControllerTests extends ControllerTestCase {

        @MockBean
        ShoeRepository shoeRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/ucsbdates/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/shoes/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/shoes/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/shoes?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/shoes/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/shoes/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/shoes/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                //LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

                Shoe shoe = Shoe.builder()
                                .name("Jordan")
                                .color("Red")
                                .brand("Nike")
                                .build();

                when(shoeRepository.findById(eq(7L))).thenReturn(Optional.of(shoe));

                // act
                MvcResult response = mockMvc.perform(get("/api/shoes?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(shoeRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(shoe);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(shoeRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/shoes?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(shoeRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Shoe with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_shoes() throws Exception {

                // arrange
                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Shoe shoe1 = Shoe.builder()
                                .name("Jordan")
                                .color("Red")
                                .brand("Nike")
                                .build();

                //LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                Shoe shoe2 = Shoe.builder()
                                .name("Yeezy")
                                .color("Gray")
                                .brand("Adidas")
                                .build();

                ArrayList<Shoe> expectedShoes = new ArrayList<>();
                expectedShoes.addAll(Arrays.asList(shoe1, shoe2));

                when(shoeRepository.findAll()).thenReturn(expectedShoes);

                // act
                MvcResult response = mockMvc.perform(get("/api/shoes/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(shoeRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedShoes);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_shoe() throws Exception {
                // arrange

                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Shoe shoe1 = Shoe.builder()
                                .name("Jordan")
                                .color("Red")
                                .brand("Nike")
                                .build();


                when(shoeRepository.save(eq(shoe1))).thenReturn(shoe1);

                // act
                MvcResult response = mockMvc.perform(
        post("/api/shoes/post?name=Jordan&color=Red&brand=Nike")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(shoeRepository, times(1)).save(shoe1);
                String expectedJson = mapper.writeValueAsString(shoe1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_shoe() throws Exception {
                // arrange

                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Shoe shoe1 = Shoe.builder()
                                .name("Jordan")
                                .color("Red")
                                .brand("Nike")
                                .build();


                when(shoeRepository.findById(eq(15L))).thenReturn(Optional.of(shoe1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/shoes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(shoeRepository, times(1)).findById(15L);
                verify(shoeRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Shoe with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_shoe_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(shoeRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/shoes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(shoeRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Shoe with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_shoe() throws Exception {
                // arrange

                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                //LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

                Shoe shoeOrig = Shoe.builder()
                                .name("Jordan")
                                .color("Red")
                                .brand("Nike")
                                .build();

                Shoe shoeEdited = Shoe.builder()
                                .name("Yeezy")
                                .color("Gray")
                                .brand("Adidas")
                                .build();

                String requestBody = mapper.writeValueAsString(shoeEdited);

                when(shoeRepository.findById(eq(67L))).thenReturn(Optional.of(shoeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/shoes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(shoeRepository, times(1)).findById(67L);
                verify(shoeRepository, times(1)).save(shoeEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_shoe_that_does_not_exist() throws Exception {
                // arrange

                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Shoe shoeEdited = Shoe.builder()
                                .name("Yeezy")
                                .color("Gray")
                                .brand("Adidas")
                                .build();

                String requestBody = mapper.writeValueAsString(shoeEdited);

                when(shoeRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/shoes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(shoeRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Shoe with id 67 not found", json.get("message"));

        }
}