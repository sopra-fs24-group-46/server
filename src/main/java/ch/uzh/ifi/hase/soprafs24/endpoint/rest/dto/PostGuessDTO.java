package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class PostGuessDTO {
    private String playerId;
    private double x;
    private double y;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
