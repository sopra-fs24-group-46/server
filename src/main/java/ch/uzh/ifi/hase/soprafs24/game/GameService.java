//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

@Service
@Transactional
public class GameService {

    private final Map<String, Game> gameRepository;
    private final UserService userService;

    @Autowired
    public GameService(UserService userService) {
        this.gameRepository = new HashMap<>();
        this.userService = userService;
    }

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Runnable clearClosedGamesRunnable = this::clearClosedGames;

    {
        scheduler.scheduleAtFixedRate(clearClosedGamesRunnable, 0, 1, TimeUnit.MINUTES);
    }
   
    public CreateGameResponseDTO createGame(CredentialsDTO credentials) {
        User user = userService.verifyUserCredentials(credentials);
        Game game = new Game(user);
        gameRepository.put(game.getId(), game);

        CreateGameResponseDTO response = new CreateGameResponseDTO();
        response.setGameId(game.getId());
        response.setPlayerId(game.getHostPlayerId());
        return response;
    }

    public void deleteGame(String gameId, CredentialsDTO credentials) {
        var user = userService.verifyUserCredentials(credentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(user);
        gameRepository.remove(game.getId());
    }

    public void updateSettings(String gameId, Settings settings, CredentialsDTO credentials) {
        var user = userService.verifyUserCredentials(credentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(user);
        game.updateSettings(settings);
    }

    public SettingView getSettingsView(String gameId) {
        Game game = findGameByPublicId(gameId);
        return game.getSettings();
    }

    public GameStateDTO getGameState(String gameId) {
        Game game = findGameByPublicId(gameId);
        return game.getGameState();
    }

    public String joinGame(String gameId, String displayName) {
        Game game = findGameByPublicId(gameId);
        String playerId = game.joinGame(displayName);
        return playerId;
    }

    public void leaveGame(String gameId, String playerId) {
        findGameByPublicId(gameId).leaveGame(playerId);
    }

    public Boolean openLobby(String gameId, CredentialsDTO credentials) {
        var user = userService.verifyUserCredentials(credentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(user);
        return game.openLobby();
    }

    public void startGame(String gameId, CredentialsDTO credentials) {

        var user = userService.verifyUserCredentials(credentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(user);
        game.startGame();
    }

    public void submitAnswer(String gameId, String playerId, Answer answer) {
        Game game = findGameByPublicId(gameId);
        game.guess(playerId, answer);
    }

    // for an example JSON string check:
    // src\main\resources\GameModelView_gameEnded.json
    public GameModelView getGameView(String gameId) {
        Game game = findGameByPublicId(gameId);
        return game.getGameModelView();
    }

    public Boolean usePowerUp(String gameId, String playerId, PowerUp powerUp) {
        return findGameByPublicId(gameId).usePowerUp(playerId, powerUp);
    }

    public List<String> getAllGameIds() {
        List<String> gameIds = new ArrayList<>();
        for (Game game : gameRepository.values()) {
            gameIds.add(game.getId());
        }
        return gameIds;
    }

    public static String toJsonString(GameModelView gameModelView) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not convert GameModelView to JSON string", e);
        }
    }

    // Private functions-------------------------------------------------

    private Game findGameByPublicId(String gameId) {
        Game game = gameRepository.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with publicId: " + gameId + " not found");
        }
        return game;
    }

    private void clearClosedGames() {
        gameRepository.entrySet().removeIf(entry -> entry.getValue().getGameState().getGameState() == GameState.CLOSED);
    }
}