package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ModelPROJECTILE {
    public static final float SIZE = 8f;

    private final Rectangle bounds;
    private final Vector2 velocity = new Vector2();
    private final int attackId;
    private float lifeTimer;

    public ModelPROJECTILE(float centerX, float centerY, float velocityX, float velocityY, int attackId) {
        bounds = new Rectangle(centerX - SIZE / 2f, centerY - SIZE / 2f, SIZE, SIZE);
        velocity.set(velocityX, velocityY);
        this.attackId = attackId;
        lifeTimer = 1.1f;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getAttackId() {
        return attackId;
    }

    public boolean isAlive() {
        return lifeTimer > 0f
            && bounds.x + bounds.width >= 0f
            && bounds.x <= ModelMAP.WORLD_WIDTH
            && bounds.y + bounds.height >= 0f
            && bounds.y <= ModelMAP.WORLD_HEIGHT;
    }

    public void update(float delta) {
        lifeTimer -= delta;
        bounds.x += velocity.x * delta;
        bounds.y += velocity.y * delta;
    }
}
