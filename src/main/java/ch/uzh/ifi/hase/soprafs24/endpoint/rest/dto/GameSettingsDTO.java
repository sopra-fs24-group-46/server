package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;

public class GameSettingsDTO {

    // credentials
    private long id;// user id
    private String token;// user token

    // game settings
    private int maxPlayers;
    private int rounds;
    private List<LocationTypes> locationTypes;

    private Integer questionTime;
    private Integer guessingTime;
    private Integer mapRevealTime;
    private Integer leaderBoardTime;

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

    public Integer getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Integer questionTime) {
        this.questionTime = questionTime;
    }

    public void setGuessingTime(Integer guessingTime) {
        this.guessingTime = guessingTime;
    }

    public Integer getMapRevealTime() {
        return mapRevealTime;
    }

    public void setMapRevealTime(Integer mapRevealTime) {
        this.mapRevealTime = mapRevealTime;
    }

    public Integer getLeaderBoardTime() {
        return leaderBoardTime;
    }

    public void setLeaderBoardTime(Integer leaderBoardTime) {
        this.leaderBoardTime = leaderBoardTime;
    }

    public List<LocationTypes> getLocationTypes() {
        return locationTypes;
    }

    public void setLocationTypes(List<LocationTypes> locationTypes) {
        this.locationTypes = locationTypes;
    }

}
