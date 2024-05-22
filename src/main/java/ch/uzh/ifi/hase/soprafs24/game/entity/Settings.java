package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.RegionType;

@Entity
@Table(name = "SETTINGS")
public class Settings implements SettingView, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "HOST_USER_ID")
    private Long hostUserId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MAX_PLAYERS")
    private Integer maxPlayers;
    @Column(name = "ROUNDS")
    private Integer rounds;

    // data filtering
    @ElementCollection
    @Column(name = "LOCATION_TYPE")
    private List<LocationTypes> locationTypes;
    @Column(name = "REGION_AS_POLYGON")
    private double[][] regionAsPolygon;
    @Column(name = "REGION")
    private String region;
    @Enumerated(EnumType.STRING)
    @Column(name = "REGION_TYPE")
    private RegionType regionType;
    @ElementCollection
    @Column(name = "LOCATION_NAME")
    private List<String> locationNames;

    // times in seconds
    @Column(name = "QUESTION_TIME")
    private Integer questionTime;
    @Column(name = "GUESSING_TIME")
    private Integer guessingTime;
    @Column(name = "MAP_REVEAL_TIME")
    private Integer mapRevealTime;
    @Column(name = "LEADER_BOARD_TIME")
    private Integer leaderBoardTime;
    // M2
    // Difficulty
    // Region

    public static Settings defaultSettings() {
        var settings = new Settings();
        settings.maxPlayers = 4;
        settings.rounds = 4;

        settings.questionTime = 10;
        settings.guessingTime = 10;
        settings.mapRevealTime = 10;
        settings.leaderBoardTime = 5;

        settings.locationTypes = List.of(LocationTypes.values());
        return settings;
    }

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
        if (settings.getQuestionTime() != null) {
            setQuestionTime(settings.getQuestionTime());
        }
        if (settings.getGuessingTime() != null) {
            setGuessingTime(settings.getGuessingTime());
        }
        if (settings.getMapRevealTime() != null) {
            setMapRevealTime(settings.getMapRevealTime());
        }
        if (settings.getLeaderBoardTime() != null) {
            setLeaderBoardTime(settings.getLeaderBoardTime());
        }
        if (settings.getLocationTypes() != null && !settings.getLocationTypes().isEmpty()) {
            setLocationTypes(settings.getLocationTypes());
        }

        if (settings.getRegionAsPolygon() != null &&
                settings.getRegionAsPolygon().length > 2 && // polygon has at least 3 points
                settings.getRegionAsPolygon()[0].length == 2) {// a point has two coordinates
            setRegionAsPolygon(settings.getRegionAsPolygon());
        } else
            setRegionAsPolygon(null);// disable filtering

        if (settings.getRegion() != null) {
            if (settings.getRegionType() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "RegionType must be provided when using region name: " + settings.getRegion() + " "
                                + settings.getRegionType());
            }
            setRegion(settings.getRegion());
            setRegionType(settings.getRegionType());
        } else {
            setRegion(null);
            setRegionType(null);
        } // disable filtering

        if (settings.getLocationNames() != null && !settings.getLocationNames().isEmpty()) {
            setLocationNames(settings.getLocationNames());
        } else
            setLocationNames(null);// disable filtering
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
