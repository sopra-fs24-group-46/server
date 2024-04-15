//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
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

    public CreateGameResponseDTO createGame(User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = new Game(userCredentials);
        gameRepository.put(game.getId(), game);

        CreateGameResponseDTO response = new CreateGameResponseDTO();
        response.setGameId(game.getId());
        response.setPlayerId(game.getHostPlayerId());
        return response;
    }

    public void deleteGame(String gameId, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        gameRepository.remove(game.getId());
    }

    public void updateSettings(String gameId, Settings settings, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        game.updateSettings(settings);
    }

    public SettingView getSettingsView(String gameId) {
        Game game = findGameByPublicId(gameId);
        return game.getSettings();
    }

    public String joinGame(String gameId, String displayName) {
        Game game = findGameByPublicId(gameId);
        String playerId = game.joinGame(displayName);
        return playerId;
    }

    public void leaveGame(String gameId, String playerId) {
        findGameByPublicId(gameId).leaveGame(playerId);
    }

    public Boolean openLobby(String gameId, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        return game.openLobby();
    }

    // at this time Lobby info is also a GameModelView
    public String getLobbyView(String gameId) {
        return getGameView(gameId);
    }

    public void startGame(String gameId, User userCredentials) {

        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        game.startGame();
    }

    public void submitAnswer(String gameId, String playerId, Answer answer) {
        Game game = findGameByPublicId(gameId);
        game.guess(playerId, answer);
    }

    // for an example JSON string check:
    // src\main\resources\GameModelView_gameEnded.json
    public String getGameView(String gameId) {
        Game game = findGameByPublicId(gameId);
        GameModelView gameModelView = game.getGameModelView();
        ObjectMapper mapper = new ObjectMapper();
        String gameModelViewJson = null;

        try {// this handles the json conversion (DTO -> JSON) since i don't trust the DTO
             // mappers to handle multilayer objects
            gameModelViewJson = mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not convert GameModelView to JSON string", e);
        }

        return gameModelViewJson;
    }

    // Private functions-------------------------------------------------
    private Game findGameByPublicId(String gameId) {
        Game game = gameRepository.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with publicId: " + gameId + " not found");
        }
        return game;
    }

    public List<String> getAllGameIds() {
        List<String> gameIds = new ArrayList<>();
        for (Game game : gameRepository.values()) {
            gameIds.add(game.getId());
        }
        return gameIds;
    }

}