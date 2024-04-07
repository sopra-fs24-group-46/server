//Endpoints for Game
package ch.uzh.ifi.hase.soprafs24.endpoint.controller;


import ch.uzh.ifi.hase.soprafs24.endpoint.controller.UserController;
import ch.uzh.ifi.hase.soprafs24.game.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // @PutMapping("/start")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void startGame(@PathVariable Long id) {
    //     gameService.startGame(id);
    // }

    // @PutMapping("/end")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void endGame(@PathVariable Long id) {
    //     gameService.endGame(id);
    // }
}

