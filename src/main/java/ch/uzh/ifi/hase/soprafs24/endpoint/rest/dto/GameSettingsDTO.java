package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class GameSettingsDTO {

    private int maxPlayers;
    private int rounds;
    private int guessingTime;
    private long host;
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

    public long getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }
    
}
