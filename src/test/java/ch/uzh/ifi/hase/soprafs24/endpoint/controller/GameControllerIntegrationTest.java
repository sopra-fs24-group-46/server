package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers.GameView;
import ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers.Helpers;
import ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers.RequestExecutor;
import ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers.SettingsView;
import ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers.TestUser;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;
import net.minidev.json.JSONArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@EnabledOnOs({ OS.WINDOWS, OS.MAC }) // cloud runs on linux therefore this is only runs locally
public class GameControllerIntegrationTest {
        final static String serverURL = "http://localhost:8080"; // test locally
        // final static String serverURL = "
        // https://sopra-fs24-group-46-server.oa.r.appspot.com"; // test deployed
        final static OkHttpClient httpClient = new OkHttpClient();
        final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final static ObjectMapper mapper = new ObjectMapper();
        final static RequestExecutor executor = new RequestExecutor(serverURL);

        static TestUser testUser = null;
        static String gameId = null;
        static String playerId = null;
        static SettingsView settingView = new SettingsView(executor);
        static GameView gameView = new GameView(executor);
        final static Helpers helpers = new Helpers(executor, gameView);

        // common requests
        static Request openLobby = null;
        static Request startGame = null;

        @Test
        void serverIsRunning() {
                // if this isn't running something is wrong with
                // BeforeAll, BeforeEach, AfterEach, or AfterAll
                executor.checkIfHostIsRunning();
        }

        // how to properly catch errors so I understand them
        @Test
        void joinGame() {
                Request joinGame = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{ \"displayName\": \"test\" }"))
                                .build();

                executor.executeRequest(joinGame, 400, "Should not be able to join");
                executor.executeRequest(openLobby, 204, "failed to open lobby");
                executor.executeRequest(joinGame, 200, "failed to join");
        }

        @Test
        void getSettings() throws JsonMappingException, JsonProcessingException {
                Request getSettings = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/settings")
                                .build();
                var jsonString = executor.executeRequest(getSettings, 200, "failed to get settings");
                JsonNode jsonNode = mapper.readValue(jsonString, JsonNode.class);
                assertNotNull(jsonNode.get("maxPlayers"));
                assertNotNull(jsonNode.get("rounds"));
                assertNotNull(jsonNode.get("questionTime"));
                assertNotNull(jsonNode.get("guessingTime"));
                assertNotNull(jsonNode.get("mapRevealTime"));
                assertNotNull(jsonNode.get("leaderBoardTime"));
        }

        @Test
        void lobbyIntegration() throws InterruptedException {
                Request joinGuest1 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest1\"}"))
                                .build();
                Request joinGuest2 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest2\"}"))
                                .build();
                Request joinGuest3 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest3\"}"))
                                .build();

                Request updateSettings = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/updateSettings")
                                .put(executor.body(String.format(
                                                "{\"id\": \"%s\", \"token\": \"%s\", \"maxPlayers\": 4, \"rounds\": 1, \"guessingTime\": 0 }",
                                                testUser.getId(), testUser.getToken())))
                                .build();

                gameView.update();
                assertEquals("SETUP", gameView.getGameState());
                executor.executeRequest(updateSettings, 204, "failed to update settings");

                settingView.update();
                assertEquals(1, settingView.getRounds());
                assertEquals(4, settingView.getMaxPlayers());
                assertEquals(0, settingView.getGuessingTime());

                executor.executeRequest(openLobby, 204, "failed to open lobby");
                gameView.update();
                assertEquals("LOBBY", gameView.getGameState());
                gameView.update();
                assertEquals(1, gameView.getNumberOfPlayers());
                var playerId2 = executor.executeRequest(joinGuest1, 200, "failed to join");
                gameView.update();
                assertEquals(2, gameView.getNumberOfPlayers());
                var playerId3 = executor.executeRequest(joinGuest2, 200, "failed to join");
                gameView.update();
                assertEquals(3, gameView.getNumberOfPlayers());
                var playerId4 = executor.executeRequest(joinGuest3, 200, "failed to join");
                gameView.update();
                assertEquals(4, gameView.getNumberOfPlayers());
                executor.executeRequest(joinGuest3, 400, "should have reached max players, but was able to join");

                Request leaveGame = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/leave")
                                .put(RequestBody.create(playerId4, MediaType.get("text/plain")))
                                .build();

                executor.executeRequest(leaveGame, 204, "failed to leave");
                gameView.update();
                assertEquals(3, gameView.getNumberOfPlayers());
                playerId4 = executor.executeRequest(joinGuest3, 200, "failed to join");
                gameView.update();
                assertEquals(4, gameView.getNumberOfPlayers());
                // start game

                try {
                        httpClient.newCall(startGame).execute();
                } catch (IOException e) {
                        fail("call failed");
                }

                helpers.waitFor(GameState.PLAYING);
                assertEquals("PLAYING", gameView.getGameState());

                helpers.waitFor(RoundState.QUESTION);
                helpers.waitFor(RoundState.GUESSING);
                helpers.waitFor(RoundState.MAP_REVEAL);
                helpers.waitFor(RoundState.LEADERBOARD);
                helpers.waitFor(GameState.ENDED);

                gameView.update();
                assertEquals("ENDED", gameView.getGameState());
        }

        @Test
        void gameIntegrationTest() throws InterruptedException {// simulating the game 2 from gameIntegration Test
                Request joinGuest1 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest1\"}"))
                                .build();
                Request joinGuest2 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest2\"}"))
                                .build();
                Request joinGuest3 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/join")
                                .post(executor.body("{\"displayName\":\"guest3\"}"))
                                .build();

                Request updateSettings = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/updateSettings")
                                .put(executor.body(String.format(
                                                "{\"id\": \"%s\", \"token\": \"%s\", \"maxPlayers\": 4, \"rounds\": 2, \"guessingTime\": 2 }",
                                                testUser.getId(), testUser.getToken())))
                                .build();

                executor.executeRequest(updateSettings, 204, "failed to update settings");

                settingView.update();
                gameView.update();
                assertEquals(2, settingView.getGuessingTime());
                assertEquals(NullNode.class, gameView.getCurrentQuestion().getClass());

                executor.executeRequest(openLobby, 204, "failed to open lobby");
                assertEquals(NullNode.class, gameView.getCurrentQuestion().getClass());
                var playerId2 = executor.executeRequest(joinGuest1, 200, "failed to join");
                var playerId3 = executor.executeRequest(joinGuest2, 200, "failed to join");
                var playerId4 = executor.executeRequest(joinGuest3, 200, "failed to join");
                gameView.update();
                assertEquals(4, gameView.getNumberOfPlayers());

                try {
                        httpClient.newCall(startGame).execute();
                } catch (IOException e) {
                        fail(e);
                }

                Request guessPlayer2 = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/guess")
                                .post(executor.body(
                                                "{\"playerId\": \"" + playerId2 + "\", \"x\": 600000, \"y\": 100000}"))
                                .build();

                gameView.update();
                settingView.update();
                assertEquals("PLAYING", gameView.getGameState());
                assertEquals(settingView.getRounds(), gameView.getQuestions().size());
                assertNotEquals(NullNode.class, gameView.getCurrentQuestion().getClass());

                helpers.waitFor(GameState.PLAYING);
                helpers.waitFor(RoundState.QUESTION);
                helpers.waitFor(RoundState.GUESSING);
                System.out.println(gameView.getCurrentRound());
                executor.executeRequest(guessPlayer2, HttpStatus.NO_CONTENT.value(), "failed to guess player2");
                helpers.waitFor(RoundState.MAP_REVEAL);
                helpers.waitFor(RoundState.LEADERBOARD);

                helpers.waitFor(RoundState.QUESTION);
                helpers.waitFor(RoundState.GUESSING);
                System.out.println(gameView.getCurrentRound());
                executor.executeRequest(guessPlayer2, HttpStatus.NO_CONTENT.value(), "failed to guess player2");
                helpers.waitFor(RoundState.MAP_REVEAL);
                helpers.waitFor(RoundState.LEADERBOARD);
                helpers.waitFor(GameState.ENDED);

                assertEquals("ENDED", gameView.getGameState());
                System.out.println(gameView.getJson());
        }

        @Test
        void minimalGame() throws InterruptedException {// simulating the game 2 from gameIntegration Test
                Request updateSettings = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/updateSettings")
                                .put(executor.body(String.format(
                                                "{\"id\": \"%s\", \"token\": \"%s\", \"maxPlayers\": 4, \"rounds\": 1, \"guessingTime\": 2 }",
                                                testUser.getId(), testUser.getToken())))
                                .build();

                executor.executeRequest(updateSettings, 204, "failed to update settings");

                settingView.update();
                gameView.update();
                assertEquals(2, settingView.getGuessingTime());
                assertEquals(NullNode.class, gameView.getCurrentQuestion().getClass());

                try {
                        httpClient.newCall(startGame).execute();
                } catch (IOException e) {
                        fail(e);
                }

                Request guess = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/guess")
                                .post(executor.body("{\"playerId\": \"\", \"x\": 600000, \"y\": 100000}"))
                                .build();

                gameView.update();
                settingView.update();
                assertEquals("PLAYING", gameView.getGameState());
                assertEquals(settingView.getRounds(), gameView.getQuestions().size());
                assertNotEquals(NullNode.class, gameView.getCurrentQuestion().getClass());

                helpers.waitFor(GameState.PLAYING);
                helpers.waitFor(RoundState.QUESTION);
                helpers.waitFor(RoundState.GUESSING);
                executor.executeRequest(guess, HttpStatus.BAD_REQUEST.value(),
                                "Guess should fail (no playerID given). But it worked. ");
                helpers.waitFor(RoundState.MAP_REVEAL);
                helpers.waitFor(RoundState.LEADERBOARD);
                helpers.waitFor(GameState.ENDED);

                assertEquals("ENDED", gameView.getGameState());
                System.out.println(gameView.getJson());
        }

        @BeforeAll
        static void BeforeAll() throws IOException {
                executor.checkIfHostIsRunning(); // these tests depend on the server to be running at serverURL
                testUser = helpers.createTestUser();
                assertNotNull(testUser.getToken());

        }

        @AfterAll
        static void afterAll() throws IOException {
                helpers.deleteUser(testUser);
        }

        @BeforeEach
        void beforeEach() throws IOException {
                gameId = null;
                playerId = null;
                JsonNode jsonNode = helpers.createGame(testUser);
                gameId = jsonNode.get("gameId").asText();
                playerId = jsonNode.get("playerId").asText();
                assertNotNull(gameId);
                assertNotNull(playerId);

                openLobby = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/openLobby")
                                .post(executor.body(testUser.credentialsJSON()))
                                .build();

                startGame = new Request.Builder()
                                .url(serverURL + "/game/" + gameId + "/start")
                                .post(executor.body(testUser.credentialsJSON()))
                                .build();

                settingView.setGameId(gameId);
                gameView.setGameId(gameId);
        }

        @AfterEach
        void afterEach() throws IOException {
                helpers.deleteGame(gameId, testUser);
        }

}
