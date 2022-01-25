package Client;

import Client.Handlers.KeyHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    public void goodWhenClientConnectsToServer() {
        Level level = new Level(new KeyHandler());
        level.update();

        assertEquals("waiting", level.getState());
    }

}