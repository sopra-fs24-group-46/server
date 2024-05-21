package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

public class NextGameDTO {
    private String gameId;

    public NextGameDTO(String string) {
        gameId = string;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
