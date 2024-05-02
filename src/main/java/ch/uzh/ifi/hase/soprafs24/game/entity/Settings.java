package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;

public class Settings implements SettingView {

    private Long hostUserId;
    private Integer maxPlayers = 4;
    private Integer rounds = 4;

    private List<LocationTypes> locationTypes;
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
}
