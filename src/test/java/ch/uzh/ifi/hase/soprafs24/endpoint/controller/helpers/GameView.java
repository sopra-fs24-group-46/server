package ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import okhttp3.Request;

public class GameView {
    private JsonNode json;
    private String rawJson;
    ObjectMapper mapper = new ObjectMapper();
    final RequestExecutor executor;
    Request getGameView;

    public void setGameId(String gameId) {
        getGameView = new Request.Builder()
                .url(executor.serverURL + "/game/" + gameId + "/getView")
                .get()
                .build();
    }

    public GameView(RequestExecutor executor) {
        this.executor = executor;
        json = null;
    }

    public String getCurrentRound() {
        return json.get("currentRound").asText();
    }

    public JsonNode getCurrentQuestion() {
        return mapper.convertValue(json.get("currentQuestion"), JsonNode.class);
    }

    public ArrayNode getQuestions() {
        return mapper.convertValue(json.get("questions"), ArrayNode.class);
    }

    public String getJson() {
        return rawJson;
    }

    public String getRoundState() {
        return json.get("roundState").asText();
    }

    public void update() {
        rawJson = executor.executeRequest(getGameView, 200, "Failed to get game view");
        try {
            this.json = mapper.readValue(rawJson, JsonNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse GameView JSON: " + e.getMessage());
        }
        // System.out.println(string);
    }

    public int getNumberOfPlayers() {
        return json.get("players").size();
    }

    public String getGameState() {
        return json.get("gameState").asText();
    }
}