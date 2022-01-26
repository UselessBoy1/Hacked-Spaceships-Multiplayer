package GameObject;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int ID;
    private boolean ready = false;
    private int playerId;

    private Point player1Position = new Point(400, 600);
    private ArrayList<Point> players1BulletsPositions;
    private int player1HP;

    private Point player2Position = new Point(400, 100);
    private ArrayList<Point> players2BulletsPositions;
    private int player2HP;

    public Game(int id) {
        ID = id;
    }

    public Game determineAndUpdate1(Game gameFromClient) {
        if (!this.ready && gameFromClient.isReady()) {
            this.ready = true;
        }
        this.player1Position = gameFromClient.player1Position;
        this.player2HP = gameFromClient.player2HP;

        return this;
    }

    public Game determineAndUpdate2(Game gameFromClient) {
        if (!this.ready && gameFromClient.isReady()) {
            this.ready = true;
        }
        this.player1HP = gameFromClient.player1HP;
        this.player2Position = gameFromClient.player2Position;

        return this;
    }
    public boolean isReady() {
        return ready;
    }

    public void setPlayerId(int id) {
        playerId = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Point getPlayer1Position() {
        return player1Position;
    }

    public Point getPlayer2Position() {
        return player2Position;
    }

    public void updatePlayer1(Point pos, int hp) {
        player1Position = pos;
        player1HP = hp;
    }

    public void updatePlayer2(Point pos, int hp) {
        player2Position = pos;
        player2HP = hp;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getID() {
        return ID;
    }
}
