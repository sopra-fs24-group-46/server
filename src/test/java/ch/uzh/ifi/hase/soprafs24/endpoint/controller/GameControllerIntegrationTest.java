package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;
import net.minidev.json.JSONArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
    final static String serverURL = "http://localhost:8080";
    final static OkHttpClient httpClient = new OkHttpClient();
    final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final static ObjectMapper mapper = new ObjectMapper();
    static TestUser testUser = null;
    static String gameId = null;
    static String playerId = null;

    // common requests
    static Request openLobby = null;
    static Request startGame = null;
    static Request getGameView = null;

    @Test
    void assertTrue() {
        // if this isn't running something is wrong with
        // BeforeAll, BeforeEach, AfterEach, or AfterAll
        assertEquals(1, 1);
    }

    // how to properly catch errors so I understand them
    @Test
    void joinGame() {
        Request joinGame = new Request.Builder()
                .url(serverURL + "/game/" + gameId + "/join")
                .post(body("{ \"displayName\": \"test\" }"))
                .build();

        executeRequest(joinGame, 400, "Should not be able to join");
        executeRequest(openLobby, 204, "failed to open lobby");
        executeRequest(joinGame, 302, "failed to join");
    }

    @BeforeAll
    static void BeforeAll() throws IOException {
        checkIfLocalHostIsRunning(); // these tests depend on the server to be running at serverURL

        Request getUsers = new Request.Builder()
                .url(serverURL + "/users")
                .build();

        String usersJson = getResponseBody(httpClient.newCall(getUsers).execute());
        List<JsonNode> users = mapper.readValue(usersJson, new TypeReference<List<JsonNode>>() {
        });

        boolean testUserExists = users.stream()
                .anyMatch(u -> "testUser".equals(u.get("username").asText()));

        Request createUser = new Request.Builder()
                .url(serverURL + "/users")
                .post(body("{ \"username\": \"testUser\",  \"password\": \"12345678\" }"))
                .build();

        Request login = new Request.Builder()
                .url(serverURL + "/login")
                .post(body("{ \"username\": \"testUser\",  \"password\": \"12345678\" }"))
                .build();

        String userJson = null;
        if (testUserExists) {
            String json = executeRequest(login, HttpStatus.OK.value(), "Login failed");
            JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
            userJson = jsonNode.get("user").toString();
        } else {
            userJson = executeRequest(createUser, 201, "User creation failed");
        }

        testUser = TestUser.from(mapper.readValue(userJson, User.class));

        assertNotNull(testUser.getToken());

    }

    @AfterAll
    static void afterAll() throws IOException {
        Request deleteUser = new Request.Builder()
                .url(serverURL + "/users")
                .delete(body(testUser.credentialsJSON()))
                .build();
        httpClient.newCall(deleteUser).execute();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        checkIfLocalHostIsRunning();

        gameId = null;
        Request postRequest = new Request.Builder()
                .url(serverURL + "/game/create")
                .post(body(testUser.credentialsJSON()))
                .build();

        var json = executeRequest(postRequest, 201, "Failed to create game");

        JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
        gameId = jsonNode.get("gameId").asText();
        playerId = jsonNode.get("playerId").asText();

        assertNotNull(gameId);

        openLobby = new Request.Builder()
                .url(serverURL + "/game/" + gameId + "/openLobby")
                .post(body(testUser.credentialsJSON()))
                .build();

        startGame = new Request.Builder()
                .url(serverURL + "/game/" + gameId + "/start")
                .post(body(testUser.credentialsJSON()))
                .build();

        getGameView = new Request.Builder()
                .url(serverURL + "/game/" + gameId + "/getView")
                .get()
                .build();
    }

    @AfterEach
    void afterEach() throws IOException {
        if (gameId != null) {
            Request deleteRequest = new Request.Builder()
                    .url(serverURL + "/game/" + gameId)
                    .delete(body(testUser.credentialsJSON()))
                    .build();
            executeRequest(deleteRequest, 204, "Failed to delete game");
        }
    }

    private static String getResponseBody(Response response) throws IOException {
        StringBuilder responseString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line).append("\n");
            }
            String json = responseString.toString();
            json = json.replace("\n", "");
            return json;
        }
    }

    private static void failAndPrint(String prefix, String json) {
        try {
            String message;
            try {
                JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
                if (jsonNode.has("message")) {
                    message = jsonNode.get("message").asText();
                } else {
                    message = json;
                }
            } catch (Exception e) {
                message = json;
            }
            message = prefix + ": " + message;
            System.out.println(message);
            fail(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage());
        }
    }

    private static String executeRequest(Request request, int expectedResponseCode, String failMsg) {
        String json = null;
        try (Response response = httpClient.newCall(request).execute()) {
            json = getResponseBody(response);
            if (response.code() != expectedResponseCode) {
                failAndPrint(failMsg + ", HttpCode: " + response.code(), json);
            }
        } catch (Exception e) {
            fail(e);
        }
        return json;
    }

    private static RequestBody body(String json) {
        return RequestBody.create(json, JSON);
    }

    private static void checkIfLocalHostIsRunning() {
        Request request = new Request.Builder()
                .url(serverURL)
                .get()
                .build();

        try {
            httpClient.newCall(request).execute();
        } catch (Exception e) {

            fail("Server is not running"); // run local host to use this tests
        }
    }
}

class TestUser extends User {

    private TestUser() {
    }

    public static TestUser from(User user) {
        TestUser testUser = new TestUser();
        testUser.setId(user.getId());
        testUser.setUsername(user.getUsername());
        testUser.setToken(user.getToken());
        return testUser;
    }

    public String credentialsJSON() {
        return String.format("{ \"id\": \"%s\", \"token\": \"%s\" }",
                this.getId(), this.getToken());
    }
}
