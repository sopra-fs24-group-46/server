package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;

public class GameStateDTO {
    private GameState gameState;
    private RoundState roundState;
    private int currentRound;
    private long timeTillNextPhaseInMillis;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public long getTimeTillNextPhaseInMillis() {
        return timeTillNextPhaseInMillis;
    }

    public void setTimeTillNextPhaseInMillis(long timeTillNextPhase) {
        this.timeTillNextPhaseInMillis = timeTillNextPhase;
    }
}
