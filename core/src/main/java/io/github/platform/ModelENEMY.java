package io.github.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ModelENEMY {
    public static final float SIZE = 28f;

    private final Rectangle bounds;
    private final Vector2 velocity = new Vector2();
    private boolean alive = true;
    private final float speed;
    private float hitPoints;
    private int lastHitAttackId = -1;

    public ModelENEMY(float x, float y, int hitPoints, float speed) {
        bounds = new Rectangle(x, y, SIZE, SIZE);
        this.hitPoints = hitPoints;
        this.speed = speed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isAlive() {
        return alive;
    }

    public void takeDamage(float damage, int attackId) {
        if (lastHitAttackId == attackId) return;

        lastHitAttackId = attackId;
        hitPoints -= damage;
        if (hitPoints <= 0) {
            alive = false;
        }
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
