package ch.uzh.ifi.hase.soprafs24.game.entity;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int rounds;

    @Column(nullable = false)
    private int guessingTimePerRound;

    // Getters and setters for the properties

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getGuessingTimePerRound() {
        return guessingTimePerRound;
    }

    public void setGuessingTimePerRound(int guessingTimePerRound) {
        this.guessingTimePerRound = guessingTimePerRound;
    }
}
