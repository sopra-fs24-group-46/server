package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class CreateGameResponseDTO {
    private String gameId;
    private String playerId; // id of the player who created the game

    public String getGameId() {

        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
