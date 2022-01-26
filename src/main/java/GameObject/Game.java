package GameObject;

import Client.Players.Bullet;
import Client.Players.LocalPlayer;

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
    private ArrayList<Bullet> players1BulletsPositions = new ArrayList<>();
    private int player1HP;

    private Point player2Position = new Point(400, 100);
    private ArrayList<Bullet> players2BulletsPositions = new ArrayList<>();
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
        this.players1BulletsPositions = gameFromClient.players1BulletsPositions;

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

    public Point getPlayer1Position() {
        return player1Position;
    }

    public Point getPlayer2Position() {
        return player2Position;
    }
    
    public ArrayList<Bullet> getPlayers1BulletsPositions() {
        return players1BulletsPositions;
    }
    
    public ArrayList<Bullet> getPlayers2BulletsPositions() {
        return players2BulletsPositions;
    }

    public void updatePlayer1(LocalPlayer player) {
        player1Position = player.getPos();
        player1HP = player.getHp();
        players1BulletsPositions = new ArrayList<>(player.bullets);
    }

    public void updatePlayer2(LocalPlayer player) {
        player2Position = player.getPos();
        player2HP = player.getHp();
        players2BulletsPositions = new ArrayList<>(player.bullets);
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getID() {
        return ID;
    }
}
