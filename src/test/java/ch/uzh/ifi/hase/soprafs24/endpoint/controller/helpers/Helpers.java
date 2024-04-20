package ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.user.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Helpers {
    final OkHttpClient httpClient = new OkHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    final RequestExecutor executor;
    final GameView gameView;

    public Helpers(RequestExecutor executor, GameView gameView) {
        this.gameView = gameView;
        this.executor = executor;
    }

    public TestUser createTestUser() throws IOException {
        Request getUsers = new Request.Builder()
                .url(executor.serverURL + "/users")
                .build();

        String usersJson = executor.getResponseBody(httpClient.newCall(getUsers).execute());
        List<JsonNode> users = mapper.readValue(usersJson, new TypeReference<List<JsonNode>>() {
        });

        boolean testUserExists = users.stream()
                .anyMatch(u -> "testUser".equals(u.get("username").asText()));

        Request createUser = new Request.Builder()
                .url(executor.serverURL + "/users")
                .post(executor.body("{ \"username\": \"testUser\",  \"password\": \"12345678\" }"))
                .build();

        Request login = new Request.Builder()
                .url(executor.serverURL + "/login")
                .post(executor.body("{ \"username\": \"testUser\",  \"password\": \"12345678\" }"))
                .build();

        String userJson = null;
        if (testUserExists) {
            String json = executor.executeRequest(login, HttpStatus.OK.value(), "Login failed");
            JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
            userJson = jsonNode.get("user").toString();
        } else {
            userJson = executor.executeRequest(createUser, 201, "User creation failed");
        }
        return TestUser.from(mapper.readValue(userJson, User.class));
    }

    public void deleteUser(TestUser testUser) throws IOException {
        Request deleteUser = new Request.Builder()
                .url(executor.serverURL + "/users")
                .delete(executor.body(testUser.credentialsJSON()))
                .build();
        httpClient.newCall(deleteUser).execute();
    }

    public JsonNode createGame(TestUser testUser) throws IOException {
        Request postRequest = new Request.Builder()
                .url(executor.serverURL + "/game/create")
                .post(executor.body(testUser.credentialsJSON()))
                .build();

        var json = executor.executeRequest(postRequest, 201, "Failed to create game");

        JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
        return jsonNode;
    }

    public void deleteGame(String gameId,  TestUser testUser) {
        if (gameId != null) {
            Request deleteRequest = new Request.Builder()
                    .url(executor.serverURL + "/game/" + gameId)
                    .delete(executor.body(testUser.credentialsJSON()))
                    .build();
            executor.executeRequest(deleteRequest, 204, "Failed to delete game");
        }
    }

    public void waitFor(GameState state) throws InterruptedException {
        System.out.println("waiting for " + state.toString());
        waitFor(() -> gameView.getGameState(), state.toString());
    }

    public void waitFor(RoundState state) throws InterruptedException {
        System.out.println("waiting for " + state.toString());
        waitFor(() -> gameView.getRoundState(), state.toString());
    }

    public void waitFor(Supplier<String> gameViewField, String state) throws InterruptedException {
        for (int i = 0; i < 30; i++) {// wait for the state
            gameView.update();
            if (gameViewField.get().equals(state)) {
                System.out.println("found state " + state + " after " + i / 2 + " seconds");
                break;
            }
            Thread.sleep(500);
        }
    }
}