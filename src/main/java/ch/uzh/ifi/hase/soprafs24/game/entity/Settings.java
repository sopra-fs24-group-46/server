package ch.uzh.ifi.hase.soprafs24.game.entity;

import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;

public class Settings implements SettingView{

    private Integer maxPlayers;
    private Integer rounds;
    private Integer guessingTimePerRound;
    //M2
    //Difficulty
    //Region

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public Integer getGuessingTimePerRound() {
        return guessingTimePerRound;
    }

    public void setGuessingTimePerRound(Integer guessingTimePerRound) {
        this.guessingTimePerRound = guessingTimePerRound;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void update(Settings settings) {
        if (settings.getMaxPlayers() != null) {
            setMaxPlayers(settings.getMaxPlayers());
        }
        if (settings.getRounds() != null) {
            setRounds(settings.getRounds());
        }
        if (settings.getGuessingTimePerRound() != null) {
            setGuessingTimePerRound(settings.getGuessingTimePerRound());
        }
    }
}
