package ch.uzh.ifi.hase.soprafs24.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

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
}
