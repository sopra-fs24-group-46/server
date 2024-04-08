package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.UUID;

public class GuestPlayer implements Player {

    private static long serialVersionUID = 1L;

    private Long id;
    private String token;
    private String nickname;

    public GuestPlayer() {
        this.id = serialVersionUID;
        serialVersionUID += 1;
        this.token = UUID.randomUUID().toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getDisplayName() {
        return nickname;
    }

    public void setDisplayName(String displayName) {
        this.nickname = displayName;
    }

    // does the same as setDisplayName. Here for more intuitive use of GuestPlayer
    public void setNickname(String displayName) {
        this.nickname = displayName;
    }
}
