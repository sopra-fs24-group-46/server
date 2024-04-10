package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.game.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    // @Test
    public void testGetGameViewFromFrontend_ValidGameId_ReturnsGameView() throws Exception {
        // Arrange
        String gameId = "1";

        // Act
        String result = mockMvc.perform(get("/" + gameId + "/getView/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testGetGameViewFromFrontend_InvalidGameId_ReturnsNotFound() throws Exception {
        // Arrange
        String gameId = "invalidId";

        // Act
        mockMvc.perform(get("/" + gameId + "/getView/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // @Test
    public void testUpdateGameSettingsFromFrontend_ValidGameId_ReturnsNoContent() throws Exception {
        // Arrange
        String gameId = "1";
        String jsonPayload = "Example JSON Payload from frontend";

        // Act
        mockMvc.perform(put("/" + gameId + "/updateSettings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isNoContent());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUpdateGameSettingsFromFrontend_InvalidGameId_ReturnsNotFound() throws Exception {
        // Arrange
        String gameId = "invalidId";
        String jsonPayload = "Example JSON Payload from frontend";

        // Act
        mockMvc.perform(put("/" + gameId + "/updateSettings/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isNotFound());
    }

    // @Test
    public void testGetGameViewFromFrontend_ValidGameId_ReturnsOk() throws Exception {
        // Arrange
        String gameId = "1";

        // Act
        mockMvc.perform(get("/" + gameId + "/getView/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // @Test
    public void testJoinGameFromFrontend_ValidGameId_ReturnsNoContent() throws Exception {
        // Arrange
        String gameId = "1";
        String username = "test_user";
        String jsonPayload = "{\"username\": \"" + username + "\"}";

        // Act
        mockMvc.perform(put("/" + gameId + "/join/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testJoinGameFromFrontend_InvalidGameId_ReturnsNotFound() throws Exception {
        // Arrange
        String gameId = "invalidId";
        String username = "test_user";
        String jsonPayload = "{\"username\": \"" + username + "\"}";

        // Act
        mockMvc.perform(put("/" + gameId + "/join/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isNotFound());
    }
}
