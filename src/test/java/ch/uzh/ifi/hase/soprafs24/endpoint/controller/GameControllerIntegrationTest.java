package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.junit.jupiter.api.AfterAll;
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
    static User testUser = null;
    static String gameId = null;

    @BeforeAll
    static void setup() throws IOException {
        Request request = new Request.Builder()
                .url(serverURL)
                .get()
                .build();

        try {
            httpClient.newCall(request).execute();
        } catch (Exception e) {
            fail("Server is not running"); // run local host to use this tests
        }

        // Post request to /users with username = "test" and password = "test"
        RequestBody body = RequestBody.create("{ \"username\": \"testUser\",  \"password\": \"12345678\" }", JSON);

        Request postRequest = new Request.Builder()
                .url(serverURL + "/users")
                .post(body)
                .build();

        RequestBody body2 = RequestBody.create("{ \"username\": \"testUser\",  \"password\": \"12345678\" }", JSON);
        Request loginRequest = new Request.Builder()
                .url(serverURL + "/login")
                .post(body2)
                .build();

        String json = null;
        try (Response response = httpClient.newCall(postRequest).execute()) {

            if (201 != response.code()) {// user creation not possible
                try (Response loginResponse = httpClient.newCall(loginRequest).execute()) {
                    json = getResponseBody(loginResponse);
                }
                try {
                    JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
                    String userJson = jsonNode.get("user").toString();
                    testUser = mapper.readValue(userJson, User.class);
                } catch (Exception e) {
                    fail("failed to parse user login");
                }
            } else {
                json = getResponseBody(response);
                try {
                    testUser = mapper.readValue(json, User.class);
                } catch (Exception e) {
                    fail("failed to parse user creation");
                }
            }

        } catch (Exception e) {
            fail("failed to create user");
        }

        assertNotNull(testUser.getToken());
    }

    // @AfterAll
    static void tearDown() throws IOException {
        String credentials = String.format("{ \"id\": \"%s\", \"token\": \"%s\" }",
                testUser.getId(), testUser.getToken());
        RequestBody body = RequestBody.create(credentials, JSON);
        // delete user
        Request deleteRequest = new Request.Builder()
                .url(serverURL + "/users")
                .delete(body)
                .build();
        httpClient.newCall(deleteRequest).execute();
    }

    // @BeforeEach
    @Test
    void createGame() throws IOException {
        gameId = null;
        String credentials = String.format("{ \"id\": \"%s\", \"token\": \"%s\" }",
                testUser.getId(), testUser.getToken());
        RequestBody body = RequestBody.create(credentials, JSON);
        Request postRequest = new Request.Builder()
                .url(serverURL + "/game/create")
                .post(body)
                .build();
        try (Response response = httpClient.newCall(postRequest).execute()) {
            assertEquals(201, response.code());
            var json = getResponseBody(response);
            gameId = mapper.readValue(json, JsonNode.class).get("gameId").asText();
        } catch (Exception e) {
            fail("failed to create game" + e);
        }
        assertNotNull(gameId);
    }

    @Test
    void assertTrue() {// placeholder
        assertEquals(1, 1);
    }

    // @Test
    void game1() {

    }

    private static String getResponseBody(Response response) throws IOException {
        StringBuilder responseString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line).append("\n");
            }
            return responseString.toString();
        }
    }
}
