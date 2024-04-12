//This is an immutable wrapper for the Settings Class
package ch.uzh.ifi.hase.soprafs24.game.View;

import ch.uzh.ifi.hase.soprafs24.user.User;

public interface SettingView {
    public Long getHostUserId();

    public Integer getMaxPlayers();

    public Integer getRounds();

    public Integer getGuessingTimePerRound();
}
