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

    private Point player1Position;
    private ArrayList<Point> players1BulletsPositions;
    private int player1HP;

    private Point player2Position;
    private ArrayList<Point> players2BulletsPositions;
    private int player2HP;

    public Game(int id) {
        ID = id;
    }

    public boolean isReady() {
        return ready;
    }


    public Game determineAndUpdate(Game gameFromClient) {
        if (!this.ready && gameFromClient.isReady()) {
            this.ready = true;
        }
        return this;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getID() {
        return ID;
    }
}
