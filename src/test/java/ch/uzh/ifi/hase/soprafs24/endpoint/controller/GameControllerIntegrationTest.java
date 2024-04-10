package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    @LocalServerPort
    private String gameId;

    @BeforeAll
    static void testServerIsRunning() throws IOException {
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

        try (Response response = httpClient.newCall(postRequest).execute()) {
            assertEquals(201, response.code());
            var json = getResponseBody(response);
            testUser = mapper.readValue(json, User.class);

        } catch (Exception e) {
            fail("failed to create user");
        }

        assertNotNull(testUser.getToken());
    }

    @AfterAll
    static void tearDown() throws IOException {
        String jsonString = String.format("{ \"id\": \"%s\", \"token\": \"%s\" }",
                testUser.getId(), testUser.getToken());
        RequestBody body = RequestBody.create(jsonString, JSON);
        // delete user
        Request deleteRequest = new Request.Builder()
                .url(serverURL + "/users")
                .delete(body)
                .build();
        httpClient.newCall(deleteRequest).execute();
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
