package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class GameSettingsDTO {

    // credentials
    private long id;// user id
    private String token;// user token

    // game settings
    private int maxPlayers;
    private int rounds;
    private int guessingTime;

    // Getters and Setters
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getGuessingTime() {
        return guessingTime;
    }

    public void setGuessingTime(int guessingTime) {
        this.guessingTime = guessingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
