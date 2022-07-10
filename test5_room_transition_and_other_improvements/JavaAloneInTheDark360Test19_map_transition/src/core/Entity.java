package core;

import java.awt.Graphics2D;
import renderer3d.Renderer;

/**
 *
 * @author Leo
 * @param <S>
 */
public class Entity<S extends Scene> {
    
    private S scene;

    public Entity(S scene) {
        this.scene = scene;
    }

    public S getScene() {
        return scene;
    }
    
    public void start() {
    }

    public void update() {
    }

    public void draw(Graphics2D g, Renderer renderer) {
    }
    
}
