package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class CreateGameDTO {

    private Long id;
    private String token;
    private String gameId;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public CreateGameDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long userId) {
        this.id = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
