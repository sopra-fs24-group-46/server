package ch.uzh.ifi.hase.soprafs24.game.entity;

public interface Player {

    Long getId(); //used to identify the player

    String getToken(); //used to verify/authenticate the player

    String getDisplayName(); //used to display in frontend
}
