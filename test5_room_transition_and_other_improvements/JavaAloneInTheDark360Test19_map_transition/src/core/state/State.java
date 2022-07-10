package core.state;

import java.awt.Graphics2D;

/**
 * State class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class State {
    
    private final String name;
    private final StateManager manager;

    public State(String name, StateManager manager) {
        this.name = name;
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public StateManager getManager() {
        return manager;
    }

    public void onEnter() {
    }

    public void onExit() {
    }

    public void update() {
    }
    
    public void draw(Graphics2D g) {
    }
    
}
