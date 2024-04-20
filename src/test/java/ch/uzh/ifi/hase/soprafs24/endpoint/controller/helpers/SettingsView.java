package ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Request;

public class SettingsView {

    ObjectMapper mapper = new ObjectMapper();
    final RequestExecutor executor;
    Request getSettings;
    private JsonNode json;

    public void setGameId(String gameId) {
        getSettings = new Request.Builder()
                .url(executor.serverURL + "/game/" + gameId + "/settings")
                .get()
                .build();
    }

    public SettingsView(RequestExecutor executor) {
        this.executor = executor;
    }

    public Integer getGuessingTime() {
        return json.get("guessingTime").asInt();
    }

    public Integer getMaxPlayers() {
        return json.get("maxPlayers").asInt();
    }

    public void update() {
        String string = executor.executeRequest(getSettings, 200, "Failed to get game settings");
        try {
            this.json = mapper.readValue(string, JsonNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse SettingsView JSON: " + e.getMessage());
        }
    }

    public int getRounds() {
        return json.get("rounds").asInt();
    }
}