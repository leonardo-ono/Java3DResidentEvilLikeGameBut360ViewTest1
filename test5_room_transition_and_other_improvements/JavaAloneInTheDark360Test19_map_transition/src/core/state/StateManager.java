package core.state;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * StateManager class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 * @param <S> state type
 */
public class StateManager<S extends State> {
    
    protected final Map<String, S> states = new HashMap<>();
    protected S currentState;
    protected S nextState;
    
    public StateManager() {
    }

    public void addState(S state) {
        states.put(state.getName(), state);
    }
    
    public void switchTo(String name) {
        nextState = states.get(name);
    }
    
    public void update() {
        if (nextState != null) {
            if (currentState != null) {
                currentState.onExit();
            }
            currentState = nextState;
            nextState = null;
            currentState.onEnter();
        }
        if (currentState != null) {
            currentState.update();
        }
    }
    
    public void draw(Graphics2D g) {
        if (currentState != null) {
            currentState.draw(g);
        }
    }
    
}
