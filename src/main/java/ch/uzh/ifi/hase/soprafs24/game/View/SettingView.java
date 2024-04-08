//This is an immutable wrapper for the Settings Class
package ch.uzh.ifi.hase.soprafs24.game.View;

import ch.uzh.ifi.hase.soprafs24.game.entity.Player;

public interface SettingView {
    public Player getHostPlayer();

    public Integer getMaxPlayers();

    public Integer getRounds();

    public Integer getGuessingTimePerRound();
}
