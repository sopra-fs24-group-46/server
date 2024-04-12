package ch.uzh.ifi.hase.soprafs24.game;

import java.util.concurrent.*;

public class schedulerTemplate {

    private ExecutorService gameExecutor = Executors.newCachedThreadPool();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5); // Adjust the number of threads as
                                                                                      // needed
    private ConcurrentHashMap<String, GameState> gameStates = new ConcurrentHashMap<>();

    // Method to start a new game
    public void startGame(String gameId) {
        gameStates.put(gameId, new GameState());
        scheduleNextStateTransition(gameId);
    }

    // Method to stop a game
    public void stopGame(String gameId) {
        gameStates.remove(gameId);
    }

    // Method to transition to the next state for a specific game
    private void transitionToNextState(String gameId) {
        GameState gameState = gameStates.get(gameId);
        if (gameState != null) {
            gameState.updateState();
            scheduleNextStateTransition(gameId);
        }
    }

    // Method to schedule the next state transition for a specific game
    private void scheduleNextStateTransition(String gameId) {
        GameState gameState = gameStates.get(gameId);
        if (gameState != null) {
            long delay = gameState.getNextStateDelay();
            scheduler.schedule(() -> transitionToNextState(gameId), delay, TimeUnit.MILLISECONDS);
        }
    }

    // Inner class representing game state
    private class GameState {
        private String[] states = { "State1", "State2", "State3" }; // Define your game states here
        private int[] delays = { 5000, 10000, 15000 }; // Define your state delays in milliseconds here
        private int currentStateIndex = 0;

        // Method to update the game state
        public void updateState() {
            System.out.println("Game State: " + states[currentStateIndex]);
            currentStateIndex = (currentStateIndex + 1) % states.length;
        }

        // Method to get the delay for the next state transition
        public long getNextStateDelay() {
            return delays[currentStateIndex];
        }
    }
}
