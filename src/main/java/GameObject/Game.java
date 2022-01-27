package GameObject;

import Client.Players.Bullet;
import Client.Players.LocalPlayer;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int ID;
    private boolean ready = false;
    private int playerId;

    private Point player1Position = new Point(400, 600);
    private LinkedList<Bullet> players1BulletsPositions = new LinkedList<>();
    private int player1HP = 200;

    private Point player2Position = new Point(-400, -100);
    private LinkedList<Bullet> players2BulletsPositions = new LinkedList<>();
    private int player2HP = 200;

    // game results
    public static final String PLAYER_1 = "p1";
    public static final String PLAYER_2 = "p2";
    public static final String DRAW = "dr";
    public static final String NONE = "none";

    private String winner = NONE;

    public Game(int id) {
        ID = id;
    }

    public Game determineAndUpdate1(Game gameFromClient) {
        if (!this.ready && gameFromClient.isReady()) {
            this.ready = true;
        }
        this.player1Position = gameFromClient.player1Position;
        this.player2HP = gameFromClient.player2HP;
        this.players1BulletsPositions = gameFromClient.players1BulletsPositions;

        if (this.winner.equals(NONE)) {
            this.winner = gameFromClient.getWinner();
        }

        return this;
    }

    public Game determineAndUpdate2(Game gameFromClient) {
        if (!this.ready && gameFromClient.isReady()) {
            this.ready = true;
        }
        this.player1HP = gameFromClient.player1HP;
        this.player2Position = gameFromClient.player2Position;
        this.players2BulletsPositions = gameFromClient.players2BulletsPositions;

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

    public int getPlayer1HP() {
        return player1HP;
    }

    public int getPlayer2HP() {
        return player2HP;
    }

    public Point getPlayer1Position() {
        return player1Position;
    }

    public Point getPlayer2Position() {
        return player2Position;
    }
    
    public LinkedList<Bullet> getPlayers1BulletsPositions() {
        return players1BulletsPositions;
    }
    
    public LinkedList<Bullet> getPlayers2BulletsPositions() {
        return players2BulletsPositions;
    }

    public void updatePlayer1(LocalPlayer player, int opponentHp) {
        player1Position = player.getPos();
        player2HP = opponentHp;
        players1BulletsPositions = new LinkedList<>(player.bullets);
    }

    public void updatePlayer2(LocalPlayer player, int opponentHp) {
        player2Position = player.getPos();
        player1HP = opponentHp;
        players2BulletsPositions = new LinkedList<>(player.bullets);
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getID() {
        return ID;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
