package core;

import core.state.StateManager;
import java.awt.AlphaComposite;
import static java.awt.AlphaComposite.SRC_OVER;
import java.awt.Color;
import java.awt.Graphics2D;
import scene.RoomTransition;
import scene.Stage;

/**
 * SceneManager class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class SceneManager extends StateManager<Scene> {
    
    private static final AlphaComposite[] ALPHAS = new AlphaComposite[61];
    private int fadeValue;
    private int fadeStatus;
    private int waitBetweenFade;
    
    static {
        // cache all alpha composite's
        for (int i = 0; i < 41; i++) {
            float a = i / 40f;
            ALPHAS[i] = AlphaComposite.getInstance(SRC_OVER, a);
        }        
    }
    
    public SceneManager() {
        createAllScenes();
    }
    
    private void createAllScenes() {
        addState(new Stage(this));
        addState(new RoomTransition(this));
        switchTo("stage");
        //switchTo("room_transition");
    }

    @Override
    public void update() {
        if (fadeStatus == 1) {
            fadeValue--;
            if (fadeValue < 0) {
                fadeValue = 0;
                fadeStatus = 3;
                if (currentState != null) {
                    currentState.onExit();
                }
                currentState = nextState;
                currentState.onEnter();
                currentState.update();
                nextState = null;
            }
        }
        else if (fadeStatus >= 3) {
            fadeStatus++;
            if (fadeStatus > waitBetweenFade) {
                fadeStatus = 2;
            }
        }
        else if (fadeStatus == 2) {
            fadeValue++;
            if (fadeValue > 40) {
                fadeValue = 40;
                fadeStatus = 0;
                currentState.onEnter();
            }
        }
        else {
            if (currentState != null) {
                currentState.update();
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        
        if (currentState != null) {
            if (fadeStatus != 0) {
                g.setComposite(ALPHAS[fadeValue]);
            }
            currentState.draw(g);
        }
    }

    @Override
    public void switchTo(String nextSceneId) {
        switchTo(nextSceneId, 40);
    }
    
    public void switchTo(String nextSceneId, int waitBetweenFade) {
        nextState = states.get(nextSceneId);
        fadeStatus = 1;
        fadeValue = 40;
        this.waitBetweenFade = waitBetweenFade;
    }
    
}
