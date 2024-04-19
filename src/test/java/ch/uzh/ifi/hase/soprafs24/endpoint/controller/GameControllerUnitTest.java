package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.PostGuessDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.game.GameService;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerUnitTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private GameService gameService;

        @Test
        public void getGameView() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                given(gameService.getGameView(Mockito.matches(gameId))).willReturn("{\"gameId\":\"1\"}");
                var postRequest = get("/game/" + gameId + "/getView/").contentType(MediaType.APPLICATION_JSON);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.gameId", is("1")));

                verify(gameService, Mockito.times(1)).getGameView(Mockito.matches(gameId));
                verify(gameService, Mockito.times(0)).getGameView(Mockito.matches("somethingElse"));
        }

        @Test
        public void updateSettings() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                GameSettingsDTO settingsDTO = new GameSettingsDTO();
                settingsDTO.setRounds(5);
                settingsDTO.setGuessingTime(30);
                settingsDTO.setId(1);

                String jsonPayload = new ObjectMapper().writeValueAsString(settingsDTO);

                var postRequest = put("/game/" + gameId + "/updateSettings/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).updateSettings(Mockito.matches(gameId), Mockito.notNull(),
                                Mockito.notNull());
                verify(gameService, Mockito.times(0)).updateSettings(Mockito.matches("somethingElse"),
                                Mockito.notNull(),
                                Mockito.notNull());
        }

        @Test
        public void createGameTest() throws Exception {
                // Arrange
                CredentialsDTO credentialsDTO = new CredentialsDTO();
                credentialsDTO.setId(1L);
                credentialsDTO.setToken("1");
                String jsonPayload = new ObjectMapper().writeValueAsString(credentialsDTO);
                var postRequest = post("/game/create/").contentType(MediaType.APPLICATION_JSON).content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isCreated());

                verify(gameService, Mockito.times(1)).createGame(Mockito.any());
        }

        @Test
        public void getAllGameIdsTest() throws Exception {
                // Arrange
                var postRequest = get("/game/").contentType(MediaType.APPLICATION_JSON);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isOk());

                verify(gameService, Mockito.times(1)).getAllGameIds();
        }

        @Test
        public void leaveGameTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                String playerId = "1";
                var postRequest = put("/game/" + gameId + "/leave/")
                                .contentType(MediaType.APPLICATION_JSON).content("playerId=" + playerId);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).leaveGame(Mockito.matches(gameId), Mockito.matches(playerId));
                verify(gameService, Mockito.times(0)).leaveGame(Mockito.matches("somethingElse"),
                                Mockito.matches(playerId));
        }

        @Test
        public void joinGameTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                String displayName = "player1";
                var postRequest = post("/game/" + gameId + "/join/")
                                .contentType(MediaType.APPLICATION_JSON).content("displayName=" + displayName);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isOk());

                verify(gameService, Mockito.times(1)).joinGame(Mockito.matches(gameId), Mockito.matches(displayName));
                verify(gameService, Mockito.times(0)).joinGame(Mockito.matches("somethingElse"),
                                Mockito.matches(displayName));
        }

        @Test
        public void openLobbyTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                CredentialsDTO credentialsDTO = new CredentialsDTO();
                credentialsDTO.setId(1L);
                credentialsDTO.setToken("1");
                String jsonPayload = new ObjectMapper().writeValueAsString(credentialsDTO);
                var postRequest = post("/game/" + gameId + "/openLobby/").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).openLobby(Mockito.matches(gameId), Mockito.any());
        }

        @Test
        public void startGameTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                CredentialsDTO credentialsDTO = new CredentialsDTO();
                credentialsDTO.setId(1L);
                credentialsDTO.setToken("1");
                String jsonPayload = new ObjectMapper().writeValueAsString(credentialsDTO);
                var postRequest = post("/game/" + gameId + "/start/").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).startGame(Mockito.matches(gameId), Mockito.any());
        }

        @Test
        public void deleteGameTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                CredentialsDTO credentialsDTO = new CredentialsDTO();
                credentialsDTO.setId(1L);
                credentialsDTO.setToken("1");
                String jsonPayload = new ObjectMapper().writeValueAsString(credentialsDTO);
                var postRequest = delete("/game/" + gameId + "/").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).deleteGame(Mockito.matches(gameId), Mockito.any());
        }

        @Test
        public void makeGuessTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                PostGuessDTO postGuessDTO = new PostGuessDTO();
                postGuessDTO.setX(1.0);
                postGuessDTO.setY(2.0);
                String jsonPayload = new ObjectMapper().writeValueAsString(postGuessDTO);
                var postRequest = post("/game/" + gameId + "/guess/").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isNoContent());

                verify(gameService, Mockito.times(1)).submitAnswer(Mockito.matches(gameId), Mockito.any(),
                                Mockito.any());
        }

        @Test
        public void getSettingsTest() throws Exception {
                // Arrange
                String gameId = "ab3501ds";
                var settings = new Settings();
                settings.setRounds(4);
                given(gameService.getSettingsView(Mockito.matches(gameId))).willReturn(settings);
                var postRequest = get("/game/" + gameId + "/settings/").contentType(MediaType.APPLICATION_JSON);

                // Act
                mockMvc.perform(postRequest)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.rounds", is(4)));

                verify(gameService, Mockito.times(1)).getSettingsView(Mockito.matches(gameId));
        }

        public static <T> org.hamcrest.Matcher<T> is(T value) {
                return org.hamcrest.core.Is.is(value);
        }
}
