package ch.uzh.ifi.hase.soprafs24.game;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.concurrent.*;

class GameEngineUnitTest {

    @Test
    void testScheduleRounds() {
        // don't know how to mock schedule. Integration test is done
    }

    @Test
    void testScheduleRound() {
        // don't know how to mock schedule. Integration test is done
    }

    @Test
    void testInitGame() {

        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        assertEquals(GameState.LOBBY, gameModel.getGameState());
        assertThrows(ResponseStatusException.class, () -> GameEngine.initGame(gameModel, settings));
        assertEquals(0, gameModel.getCurrentRound());
    }

    @Test
    void testLoadGame() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        assertThrows(ResponseStatusException.class, () -> GameEngine.loadGame(gameModel, settings));

        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings); // loading game only works if game in lobby state since init is not
                                                  // done yet
        assertEquals(GameState.PLAYING, gameModel.getGameState());
        assertEquals(1, gameModel.getCurrentRound());
    }

    @Test
    void testNextRoundStateQuestion() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings);

        GameEngine.nextRoundState(gameModel, RoundState.QUESTION);
        assertEquals(RoundState.QUESTION, gameModel.getRoundState());
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL));
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.LEADERBOARD));
    }

    @Test
    void testNextRoundStateGuessing() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings);
        GameEngine.nextRoundState(gameModel, RoundState.QUESTION);

        GameEngine.nextRoundState(gameModel, RoundState.GUESSING);
        assertEquals(RoundState.GUESSING, gameModel.getRoundState());
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.GUESSING));
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.LEADERBOARD));
    }

    @Test
    void testNextRoundStateMapReveal() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings);
        GameEngine.nextRoundState(gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(gameModel, RoundState.GUESSING);

        GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL);
        assertEquals(RoundState.MAP_REVEAL, gameModel.getRoundState());
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL));
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.GUESSING));
    }

    @Test
    void testNextRoundStateLeaderboard() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings);
        GameEngine.nextRoundState(gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(gameModel, RoundState.GUESSING);
        GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL);

        GameEngine.nextRoundState(gameModel, RoundState.LEADERBOARD);
        assertEquals(RoundState.LEADERBOARD, gameModel.getRoundState());
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.GUESSING));
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL));
        assertThrows(ResponseStatusException.class, () -> GameEngine.nextRoundState(gameModel, RoundState.LEADERBOARD));
    }

    @Test
    void testEndGame() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);

        assertThrows(ResponseStatusException.class, () -> GameEngine.endGame(gameModel, settings));
        GameEngine.loadGame(gameModel, settings);

        GameEngine.endGame(gameModel, settings);
        assertEquals(GameState.ENDED, gameModel.getGameState());
    }

    @Test
    void testAddAnswer() {
        GameModel gameModel = new GameModel();
        Settings settings = new Settings();
        GameEngine.initGame(gameModel, settings);
        GameEngine.loadGame(gameModel, settings);

        GameEngine.nextRoundState(gameModel, RoundState.QUESTION);
        assertThrows(ResponseStatusException.class,
                () -> GameEngine.addAnswer(gameModel, new Answer(new GeoLocation(10.0, 10.0)), "1"));

        GameEngine.nextRoundState(gameModel, RoundState.GUESSING);
        GameEngine.addAnswer(gameModel, new Answer(new GeoLocation(10.0, 10.0)), "1");

        GameEngine.nextRoundState(gameModel, RoundState.MAP_REVEAL);
        assertThrows(ResponseStatusException.class,
                () -> GameEngine.addAnswer(gameModel, new Answer(new GeoLocation(10.0, 10.0)), "1"));
    }
}
