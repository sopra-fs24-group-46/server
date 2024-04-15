//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

@Service
@Transactional
public class GameService {

    private final UserService userService;
    private final GameRepository gameRepository;
    private final SettingsRepository settingsRepository;
    private final GameModelRepository gameModelRepository; // new line of code
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final HistoryRepository historyRepository;
    private final RoundRepository roundRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository,
            SettingsRepository settingsRepository,
            QuestionRepository questionRepository, AnswerRepository answerRepository,
            HistoryRepository historyRepository, GameModelRepository gameModelRepository,
            RoundRepository roundRepository, UserService userService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.settingsRepository = settingsRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.historyRepository = historyRepository;
        this.gameModelRepository = gameModelRepository;
        this.roundRepository = roundRepository;
        this.userService = userService;
    }

    public CreateGameResponseDTO createGame(User userCredentials) {
        var user = userService.verifyUserCredentials(userCredentials);
        Game game = new Game(user);
        saveGameModel(game.gameModel);
        settingsRepository.save(game.settings);
        settingsRepository.flush();
        gameRepository.save(game);
        gameRepository.flush();

        CreateGameResponseDTO response = new CreateGameResponseDTO();
        response.setGameId(game.getId());
        response.setPlayerId(game.getHostPlayerId());
        return response;
    }

    private void saveGameModel(GameModel gameModel) {
        playerRepository.saveAll(gameModel.getPlayers());
        questionRepository.saveAll(gameModel.getQuestions());
        answerRepository.saveAll(gameModel.getAnswers().values());
        roundRepository.saveAll(gameModel.getHistories().values().stream().map(h -> {
            return h.getRounds();
        }).reduce(new ArrayList<>(), (a, b) -> {
            a.addAll(b);
            return a;
        }));

        historyRepository.saveAll(gameModel.getHistories().values());
        gameModelRepository.save(gameModel);

        playerRepository.flush();
        questionRepository.flush();
        answerRepository.flush();
        roundRepository.flush();
        historyRepository.flush();
        gameModelRepository.flush();
    }

    public void deleteGame(String gameId, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        gameRepository.deleteById(game.getId());
    }

    public void updateSettings(String gameId, Settings settings, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        game.updateSettings(settings);
        settingsRepository.save(game.settings);
        settingsRepository.flush();
    }

    public SettingView getSettingsView(String gameId) {
        Game game = findGameByPublicId(gameId);
        return game.getSettings();
    }

    public String joinGame(String gameId, String displayName) {
        Game game = findGameByPublicId(gameId);
        String playerId = game.joinGame(displayName);
        saveGameModel(game.gameModel);
        return playerId;
    }

    public void leaveGame(String gameId, String playerId) {
        findGameByPublicId(gameId).leaveGame(playerId);
    }

    public Boolean openLobby(String gameId, User userCredentials) {
        userService.verifyUserCredentials(userCredentials);
        Game game = findGameByPublicId(gameId);
        game.verifyHost(userCredentials);
        var success = game.openLobby();
        saveGameModel(game.gameModel);
        return success;
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
        saveGameModel(game.gameModel);
    }

    public void submitAnswer(String gameId, String playerId, Answer answer) {
        Game game = findGameByPublicId(gameId);
        game.guess(playerId, answer);
        saveGameModel(game.gameModel);
    }

    // for an example JSON string check:
    // src\main\resources\GameModelView_gameEnded.json
    public String getGameView(String gameId) {
        Game game = findGameByPublicId(gameId);
        GameModelView gameModelView = game.getGameModelView();
        String gameModelViewJson = toJsonString(gameModelView);
        return gameModelViewJson;
    }

    // Private functions-------------------------------------------------
    private static String toJsonString(GameModelView gameModelView) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not convert GameModelView to JSON string", e);
        }
    }

    private Game findGameByPublicId(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Game with publicId: " + gameId + " not found"));
    }

    public List<String> getAllGameIds() {
        List<String> gameIds = new ArrayList<>();
        for (Game game : gameRepository.findAll()) {
            gameIds.add(game.getId());
        }
        return gameIds;
    }

}