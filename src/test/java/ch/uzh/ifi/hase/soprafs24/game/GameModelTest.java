package ch.uzh.ifi.hase.soprafs24.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Score;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;

public class GameModelTest {
    @Test
    void testInitRound() {
        GameModel gameModel = new GameModel();
        gameModel.initRound();
        assertEquals(RoundState.QUESTION, gameModel.getRoundState());
        assertNull(gameModel.getPowerUps().get("player1"));
        assertNull(gameModel.getAnswers().get("player1"));
        assertNull(gameModel.getCurrentScores().get("player1"));
    }

    @Test
    void testPushHistory() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");
        String player2 = gameModel.addPlayer("player2");

        gameModel.getCurrentScores().put(player1, new Score(10, 0.8));
        gameModel.getCumulativeScores().put(player2, new Score(8, 0.7));

        gameModel.pushHistory();

        
        assertEquals(10, gameModel.getCumulativeScores().get(player1).getScore());
        assertEquals(0.8, gameModel.getCumulativeScores().get(player1).getDistance(), 0.1);
        assertEquals(8, gameModel.getCumulativeScores().get(player2).getScore());
        assertEquals(0.7, gameModel.getCumulativeScores().get(player2).getDistance(), 0.1);

        gameModel.getCurrentScores().put(player1, new Score(20, 0.8));
        gameModel.pushHistory();


        assertEquals(30, gameModel.getCumulativeScores().get(player1).getScore());
        assertEquals(1.6, gameModel.getCumulativeScores().get(player1).getDistance());
        assertEquals(20, gameModel.getHistories().get(player1).getRounds().get(1).getScore().getScore());
        assertEquals(10, gameModel.getHistories().get(player1).getRounds().get(0).getScore().getScore());
    }

    @Test
    void testAddPlayer() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");
        assertEquals(1, gameModel.getPlayers().size());
        assertEquals("player1", gameModel.getPlayers().get(0).getDisplayName());
        assertEquals(player1, gameModel.getPlayers().get(0).getId());
        assertEquals(1, gameModel.getPlayers().stream().filter(player -> player.getId().equals(player1)).count());
    }

    @Test
    void testRemovePlayer() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");
        String player2 = gameModel.addPlayer("player2");

        gameModel.removePlayer(player1);
        assertEquals(1, gameModel.getPlayers().size());
        assertEquals(0, gameModel.getPlayers().stream().filter(player -> player.getId().equals(player1)).count());
        assertEquals(1, gameModel.getPlayers().stream().filter(player -> player.getId().equals(player2)).count());
    }

    @Test
    void testUsePowerUp() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");

        gameModel.usePowerUp(player1, PowerUp.X2);

        assertEquals(PowerUp.X2, gameModel.getPowerUps().get(player1));
        assertEquals(1, gameModel.getUsedPowerUps().get(player1).size());
        assertEquals(1, gameModel.getUsedPowerUps().get(player1).stream().filter(powerUp -> powerUp.equals(PowerUp.X2)).count());
    }

    @Test
    void testUsePowerUpTwoInOneRound_Fail() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");

        gameModel.usePowerUp(player1, PowerUp.X2);

        assertThrows(ResponseStatusException.class, () -> gameModel.usePowerUp(player1, PowerUp.JOKER));
    }

    @Test
    void testUsePowerUpTwice_Fail() {
        GameModel gameModel = new GameModel();
        String player1 = gameModel.addPlayer("player1");

        gameModel.usePowerUp(player1, PowerUp.X2);

        gameModel.initRound();
        gameModel.usePowerUp(player1, PowerUp.JOKER);
        assertEquals(PowerUp.JOKER, gameModel.getPowerUps().get(player1));
        assertThrows(ResponseStatusException.class, () -> gameModel.usePowerUp(player1, PowerUp.X2));
    }
}
