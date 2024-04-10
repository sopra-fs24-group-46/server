package ch.uzh.ifi.hase.soprafs24.game.entity;

import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.user.User;

public class Settings implements SettingView {

    private Long hostUserId;
    private Integer maxPlayers;
    private Integer rounds;
    private Integer guessingTimePerRound;
    // M2
    // Difficulty
    // Region

    public Settings(int maxPlayers, int rounds, int guessingTimePerRound) {
        this.maxPlayers = maxPlayers;
        this.rounds = rounds;
        this.guessingTimePerRound = guessingTimePerRound;
    }

    public Settings() {
        // TODO Auto-generated constructor stub
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostPlayer(User hostPlayer) {
        this.hostUserId = hostPlayer.getId();
    }

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
