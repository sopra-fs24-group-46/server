//This is an immutable wrapper for the Settings Class
package ch.uzh.ifi.hase.soprafs24.game.View;

public interface SettingView {
    public Integer getMaxPlayers();
    public Integer getRounds();
    public Integer getGuessingTimePerRound();
}
