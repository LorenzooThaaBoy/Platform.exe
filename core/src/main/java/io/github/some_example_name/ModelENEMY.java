package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ModelENEMY {
    public static final float SIZE = 28f;

    private final Rectangle bounds;
    private final Vector2 velocity = new Vector2();
    private boolean alive = true;
    private float speed = 95f;

    public ModelENEMY(float x, float y) {
        bounds = new Rectangle(x, y, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public void updateTowards(Rectangle target, float delta) {
        float targetCenterX = target.x + target.width / 2f;
        float targetCenterY = target.y + target.height / 2f;
        float enemyCenterX = bounds.x + bounds.width / 2f;
        float enemyCenterY = bounds.y + bounds.height / 2f;

        velocity.set(targetCenterX - enemyCenterX, targetCenterY - enemyCenterY);
        if (velocity.len2() > 0.01f) {
            velocity.nor().scl(speed);
        }

        bounds.x += velocity.x * delta;
        bounds.y += velocity.y * delta;
    }
}
