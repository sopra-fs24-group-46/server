package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;

public class PowerUpDTO {
    private String playerId;
    private PowerUp powerUp;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void setPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }
}
