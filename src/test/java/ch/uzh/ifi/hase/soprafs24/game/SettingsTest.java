package ch.uzh.ifi.hase.soprafs24.game;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.RegionType;

//not testing default values
class SettingsTest {
    private Settings testSettings;

    @BeforeEach
    void setup() {
        testSettings = new Settings();
    }

    @Test
    void testSettingsCreation() {
        Assertions.assertNotNull(testSettings, "Settings object should not be null");
    }

    int maxPlayers = 10;
    int rounds = 5;
    int questionTime = 20;
    int guessingTime = 30;
    int mapRevealTime = 5;
    int leaderBoardTime = 10;

    @Test
    void testRoundTime() {
        testSettings.setGuessingTime(2);
        testSettings.setLeaderBoardTime(2);
        testSettings.setMapRevealTime(2);
        testSettings.setQuestionTime(2);
        Assertions.assertEquals(8, testSettings.getRoundTime(), "RoundTime should be 8");
    }

    @Test
    void testMaxPlayersSetterAndGetter() {
        testSettings.setMaxPlayers(maxPlayers);
        Assertions.assertEquals(maxPlayers, testSettings.getMaxPlayers(), "MaxPlayers should be " + maxPlayers);
    }

    @Test
    void testRoundsSetterAndGetter() {
        testSettings.setRounds(rounds);
        Assertions.assertEquals(rounds, testSettings.getRounds(), "Rounds should be " + rounds);
    }

    @Test
    void testQuestionTimeSetterAndGetter() {
        testSettings.setQuestionTime(questionTime);
        Assertions.assertEquals(questionTime, testSettings.getQuestionTime(), "QuestionTime should be " + questionTime);
    }

    @Test
    void testGuessingTimeSetterAndGetter() {
        testSettings.setGuessingTime(guessingTime);
        Assertions.assertEquals(guessingTime, testSettings.getGuessingTime(), "GuessingTime should be " + guessingTime);
    }

    @Test
    void testMapRevealTimeSetterAndGetter() {
        testSettings.setMapRevealTime(mapRevealTime);
        Assertions.assertEquals(mapRevealTime, testSettings.getMapRevealTime(),
                "MapRevealTime should be " + mapRevealTime);
    }

    @Test
    void testLeaderBoardTimeSetterAndGetter() {
        testSettings.setLeaderBoardTime(leaderBoardTime);
        Assertions.assertEquals(leaderBoardTime, testSettings.getLeaderBoardTime(),
                "LeaderBoardTime should be " + leaderBoardTime);
    }

    @Test
    void testUpdateAllFields() {
        Settings settings = new Settings();
        settings.setMaxPlayers(10);
        settings.setRounds(5);
        settings.setGuessingTime(30);
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL));
        settings.setRegionAsPolygon(new double[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}});
        settings.setRegion("Switzerland");
        settings.setRegionType(RegionType.CANTON);
        settings.setLocationNames(List.of("Bern", "Zurich"));

        Settings testSettings = new Settings();
        testSettings.setLocationTypes(null);
        testSettings.update(settings);

        Assertions.assertEquals(10, testSettings.getMaxPlayers());
        Assertions.assertEquals(5, testSettings.getRounds());
        Assertions.assertEquals(30, testSettings.getGuessingTime());
        Assertions.assertArrayEquals(new LocationTypes[]{LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL}, testSettings.getLocationTypes().toArray());
        Assertions.assertArrayEquals(new double[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}}, testSettings.getRegionAsPolygon());
        Assertions.assertEquals("Switzerland", testSettings.getRegion());
        Assertions.assertEquals(RegionType.CANTON, testSettings.getRegionType());
        Assertions.assertArrayEquals(new String[]{"Bern", "Zurich"}, testSettings.getLocationNames().toArray());
    }

    @Test
    void testUpdateWithEmptySettings() {
        Settings settings = new Settings();
        settings.setMaxPlayers(10);
        settings.setRounds(5);
        settings.setGuessingTime(30);
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL));
        settings.setRegionAsPolygon(new double[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}});
        settings.setRegion("Switzerland");
        settings.setRegionType(RegionType.CANTON);
        settings.setLocationNames(List.of("Bern", "Zurich"));

        Settings testSettings = new Settings();
        settings.update(testSettings);


        //stays around
        Assertions.assertEquals(10, settings.getMaxPlayers());
        Assertions.assertEquals(5, settings.getRounds());
        Assertions.assertEquals(30, settings.getGuessingTime());
        
        //data is still loaded
        Assertions.assertArrayEquals(
                new LocationTypes[] { LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL },
                settings.getLocationTypes().toArray());
        
        //filters get disabled
        Assertions.assertArrayEquals(null, settings.getRegionAsPolygon());
        Assertions.assertEquals(null, settings.getRegion());
        Assertions.assertEquals(null, settings.getRegionType());
        Assertions.assertNull(settings.getLocationNames());
    }

    @Test
    void testUpdateRegionTypeToNull_ShouldFail() {
        Settings settings = new Settings();
        settings.setRegion("Switzerland");
        settings.setRegionType(RegionType.CANTON);

        Settings testSettings = new Settings();
        testSettings.update(settings);

        Assertions.assertEquals(settings.getRegion(), testSettings.getRegion());
        Assertions.assertEquals(settings.getRegionType(), testSettings.getRegionType());

        //fails because region type is null and region is not null. this is never wanted
        settings.setRegionType(null);
        assertThrows(ResponseStatusException.class, () -> testSettings.update(settings));
        
        //now region filtering is off. 
        settings.setRegion(null);
        testSettings.update(settings);
    }
}
