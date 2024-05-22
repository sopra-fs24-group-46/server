package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.RegionType;

public class GameSettingsDTO {

    // credentials
    private long id;// user id
    private String token;// user token

    private String name;

    // game settings
    private int maxPlayers;
    private int rounds;

    // data filtering
    private List<LocationTypes> locationTypes;
    private double[][] regionAsPolygon;
    private String region;
    private RegionType regionType;
    private List<String> locationNames;

    // times
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

    public double[][] getRegionAsPolygon() {
        return regionAsPolygon;
    }

    public void setRegionAsPolygon(double[][] regionAsPolygon) {
        this.regionAsPolygon = regionAsPolygon;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public List<String> getLocationNames() {
        return locationNames;
    }

    public void setLocationNames(List<String> locationNames) {
        this.locationNames = locationNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
