package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.RegionType;

public class Settings implements SettingView {

    private Long hostUserId;
    private Integer maxPlayers = 4;
    private Integer rounds = 4;
    
    //data filtering
    private List<LocationTypes> locationTypes;
    private double[][] regionAsPolygon;
    private String region;
    private RegionType regionType;
    private List<String> locationNames;

    // times in seconds
    private Integer questionTime = 5;
    private Integer guessingTime = 10;
    private Integer mapRevealTime = 5;
    private Integer leaderBoardTime = 5;
    // M2
    // Difficulty
    // Region

    public Settings(int maxPlayers, int rounds, int guessingTimePerRound) {
        this.maxPlayers = maxPlayers;
        this.rounds = rounds;
        this.guessingTime = guessingTimePerRound;
        this.locationTypes = new ArrayList<>();
    }

    public Settings(Long hostUserId) {
        this.hostUserId = hostUserId;
        this.locationTypes = new ArrayList<>();
    }

    public Settings() {
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public Integer getGuessingTime() {
        return guessingTime;
    }

    public void setGuessingTime(Integer guessingTimePerRound) {
        this.guessingTime = guessingTimePerRound;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void update(Settings settings) {
        if (settings.getMaxPlayers() != null) {
            setMaxPlayers(settings.getMaxPlayers());
        }
        if (settings.getRounds() != null) {
            setRounds(settings.getRounds());
        }
        if (settings.getGuessingTime() != null) {
            setGuessingTime(settings.getGuessingTime());
        }
        if (settings.getLocationTypes() != null && !settings.getLocationTypes().isEmpty()) {
            setLocationTypes(settings.getLocationTypes());
        }
        if (settings.getRegionAsPolygon() != null &&
                settings.getRegionAsPolygon().length > 2 && // polygon has at least 3 points
                settings.getRegionAsPolygon()[0].length == 2) {// a point has two coordinates
            setRegionAsPolygon(settings.getRegionAsPolygon());
        }
        if (settings.getRegion() != null) {
            if (settings.getRegionType() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RegionType must be provided when using region name: " + settings.getRegion() + " " + settings.getRegionType());
            }
            setRegion(settings.getRegion());
            setRegionType(settings.getRegionType());
        }
        if (settings.getLocationNames() != null && !settings.getLocationNames().isEmpty()) {
            setLocationNames(settings.getLocationNames());
        }
    }

    public Integer getQuestionTime() {
        return questionTime;
    }

    public Integer getMapRevealTime() {
        return mapRevealTime;
    }

    public Integer getLeaderBoardTime() {
        return leaderBoardTime;
    }

    public Integer getRoundTime() {
        return questionTime + mapRevealTime + leaderBoardTime + guessingTime;
    }

    public Integer getTotalTime() {
        return rounds * getRoundTime();
    }

    public void setQuestionTime(int questionTime2) {
        this.questionTime = questionTime2;
    }

    public void setMapRevealTime(int mapRevealTime2) {
        this.mapRevealTime = mapRevealTime2;
    }

    public void setLeaderBoardTime(int leaderBoardTime2) {
        this.leaderBoardTime = leaderBoardTime2;
    }

    public List<LocationTypes> getLocationTypes() {
        return locationTypes;
    }

    public void setLocationTypes(List<LocationTypes> locationTypes) {
        this.locationTypes = locationTypes;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public double[][] getRegionAsPolygon() {
        return regionAsPolygon;
    }

    public void setRegionAsPolygon(double[][] regionAsPolygon) {
        this.regionAsPolygon = regionAsPolygon;
    }

    public void setQuestionTime(Integer questionTime) {
        this.questionTime = questionTime;
    }

    public void setMapRevealTime(Integer mapRevealTime) {
        this.mapRevealTime = mapRevealTime;
    }

    public void setLeaderBoardTime(Integer leaderBoardTime) {
        this.leaderBoardTime = leaderBoardTime;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
