package GameDataObject;

import Client.Players.Bullet;
import Client.Players.LocalPlayer;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;

// GameDataObject is an object which is sent between server and client.
// It contains info like:
//     - players positions
//     - players hp
//     - bullets lists
//     - current winner (or no winner or draw)
public class GameDataObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final long ID;
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

    public GameDataObject(long id) {
        ID = id;
    }

    // method which runs on server
    // updates server's GameDataObject with info from player 1
    public GameDataObject determineAndUpdate1(GameDataObject gameDataObjectFromClient) {
        if (!this.ready && gameDataObjectFromClient.isReady()) {
            this.ready = true;
        }
        this.player1Position = gameDataObjectFromClient.player1Position;
        this.player2HP = gameDataObjectFromClient.player2HP;
        this.players1BulletsPositions = gameDataObjectFromClient.players1BulletsPositions;

        if (this.winner.equals(NONE)) {
            this.winner = gameDataObjectFromClient.getWinner();
        }
        return this;
    }

    // method which runs on server
    // updates server's GameDataObject with info from player 2
    public GameDataObject determineAndUpdate2(GameDataObject gameDataObjectFromClient) {
        if (!this.ready && gameDataObjectFromClient.isReady()) {
            this.ready = true;
        }
        this.player1HP = gameDataObjectFromClient.player1HP;
        this.player2Position = gameDataObjectFromClient.player2Position;
        this.players2BulletsPositions = gameDataObjectFromClient.players2BulletsPositions;

        if (this.winner.equals(NONE)) {
            this.winner = gameDataObjectFromClient.getWinner();
        }
        return this;
    }

    // method which runs on client side
    // update client's GameDataObject with player 1 local info
    public void updatePlayer1(LocalPlayer player, int opponentHp) {
        player1Position = player.getPos();
        player2HP = opponentHp;
        players1BulletsPositions = new LinkedList<>(player.bullets);
    }

    // method which runs on client side
    // update client's GameDataObject with player 2 local info
    public void updatePlayer2(LocalPlayer player, int opponentHp) {
        player2Position = player.getPos();
        player1HP = opponentHp;
        players2BulletsPositions = new LinkedList<>(player.bullets);
    }

    // getters and setters
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


    public void setReady(boolean ready) {
        this.ready = ready;
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

    public long getID() {
        return ID;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
