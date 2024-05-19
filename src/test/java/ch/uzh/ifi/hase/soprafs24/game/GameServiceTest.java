package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserRepository;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class GameServiceTest{

    @Mock
    private final UserService userService = Mockito.mock(UserService.class);

    @InjectMocks
    private GameService gameService;

    private User host;
    private String gameId;
    private String playerId;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        gameService = new GameService(userService);

        // given
        host = new User();
        host.setId(1L);
        host.setUsername("HostPlayer");


        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        Mockito.when(userService.verifyUserCredentials(Mockito.any())).thenReturn(host);

        var response = gameService.createGame(new CredentialsDTO());
        gameId = response.getGameId();
        playerId = response.getPlayerId();
    }

    @Test
    public void createGame_validInputs_success() {
        // Arrange
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        CreateGameResponseDTO createGameResponseDTO = gameService.createGame(credentialsDTO);

        // Assert
        assertNotNull(createGameResponseDTO);
        assertNotNull(createGameResponseDTO.getGameId());
    }

    @Test
    public void deleteGame_validInputs_success() {
        // Arrange
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        gameService.deleteGame(gameId, credentialsDTO);

        // Assert
        assertEquals(0, gameService.getAllGameIds().size());
    }

    @Test
    public void updateSettings_validInputs_success() {
        // Arrange
        Settings settings = new Settings();
        settings.setGuessingTime(22);
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        gameService.updateSettings(gameId, settings, credentialsDTO);

        // Assert
        var settingsDTO = gameService.getSettingsView(gameId);
        assertEquals(22, settingsDTO.getGuessingTime());
    }

    @Test
    public void getSettingsView_validInputs_success() {
        // Arrange

        // Act
        SettingView settingView = gameService.getSettingsView(gameId);

        // Assert
        assertNotNull(settingView);
    }

    @Test
    public void getGameState_validInputs_success() {
        // Arrange

        // Act
        GameStateDTO gameStateDTO = gameService.getGameState(gameId);

        // Assert
        assertNotNull(gameStateDTO);
    }

    @Test
    public void joinGame_validInputs_success() {
        // Arrange
        String displayName = "John Doe";

        // Act
        String playerId = gameService.joinGame(gameId, displayName);

        // Assert
        assertNotNull(playerId);
    }

    @Test
    public void joinGame_duplicateName_fail() {
        // Arrange
        String displayName = "John Doe";

        // Act
        gameService.joinGame(gameId, displayName);

        // Assert
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(gameId, displayName));
    }

    @Test
    public void leaveGame_validInputs_success() {
        // Arrange
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        gameService.leaveGame(gameId, playerId);

        // Assert
        assertEquals(0, gameService.getGameView(gameId).getPlayers().size());
    }

    @Test
    public void openLobby_validInputs_success() {
        // Arrange
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        Boolean openLobby = gameService.openLobby(gameId, credentialsDTO);

        // Assert
        assertTrue(openLobby);
    }

    @Test
    public void startGame_validInputs_success() {
        // Arrange
        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setId(1L);
        credentialsDTO.setToken("1");

        // Act
        gameService.startGame(gameId, credentialsDTO);

        // Assert
        assertEquals(GameState.PLAYING, gameService.getGameState(gameId).getGameState());
    }

    @Test
    public void submitAnswer_validInputs_success() throws InterruptedException {
        // Arrange
        GeoLocation location = new GeoLocation( 46.0, 8.0 );
        Answer answer = new Answer(location);

        // Act
        gameService.startGame(gameId, null);
        assertThrows(ResponseStatusException.class, () -> gameService.submitAnswer(gameId, playerId, answer));
        waitFor(RoundState.GUESSING);
        gameService.submitAnswer(gameId, playerId, answer);

        // Assert
        assertEquals(location, gameService.getGameView(gameId).getAnswers().get(playerId).getLocation());
    }

    @Test
    public void getGameView_validInputs_success() {
        // Arrange

        // Act
        var gameView = gameService.getGameView(gameId);

        // Assert
        assertNotNull(gameView);
    }

    @Test
    public void usePowerUp_validInputs_success() {
        // Arrange
        PowerUp powerUp = PowerUp.X2;

        // Act
        Boolean usePowerUp = gameService.usePowerUp(gameId, playerId, powerUp);

        // Assert
        assertTrue(usePowerUp);
    }

    @Test
    public void getAllGameIds_validInputs_success() {
        // Arrange

        // Act
        List<String> allGameIds = gameService.getAllGameIds();

        // Assert
        assertNotNull(allGameIds);
    }

    private void waitFor(GameState state) throws InterruptedException {
        System.out.println("waiting for " + state.toString());
        waitFor(() -> gameService.getGameState(gameId).getGameState() == state);
    }

    private void waitFor(RoundState state) throws InterruptedException {
        System.out.println("waiting for " + state.toString());
        waitFor(() -> gameService.getGameState(gameId).getRoundState() == state);
    }

    private void waitFor(Supplier<Boolean> matches) throws InterruptedException {
        for (int i = 0; i < 30; i++) {// wait for the state
            if (matches.get()) {
                System.out.println("found state after " + i / 2 + " seconds");
                break;
            }
            Thread.sleep(500);
        }
    }
}
