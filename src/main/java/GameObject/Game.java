package GameObject;

public class Game {
    private final int ID;
    private boolean ready = false;

    public Game(int id) {
        ID = id;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
