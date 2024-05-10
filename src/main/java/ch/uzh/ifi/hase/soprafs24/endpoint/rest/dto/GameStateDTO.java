package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;

public class GameStateDTO {
    private GameState gameState;
    private int currentRound;
    private long timeTillNextPhaseInMillis;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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
