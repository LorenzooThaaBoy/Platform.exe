package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ModelPLAYER {
    public static final float WIDTH = 32f;
    public static final float HEIGHT = 46f;
    public static final float MOVE_SPEED = 210f;
    public static final float JUMP_SPEED = 420f;
    public static final float GRAVITY = -980f;
    private static final float ATTACK_DURATION = 0.18f;
    private static final float HURT_DURATION = 0.75f;

    private final Rectangle bounds = new Rectangle(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
    private final Vector2 velocity = new Vector2();
    private int lives = 3;
    private int facing = 1;
    private boolean grounded;
    private float attackTimer;
    private float hurtTimer;

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getLives() {
        return lives;
    }

    public int getFacing() {
        return facing;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isAttacking() {
        return attackTimer > 0f;
    }

    public boolean isHurt() {
        return hurtTimer > 0f;
    }

    public void setFacingFromMovement(float movement) {
        if (movement > 0f) facing = 1;
        if (movement < 0f) facing = -1;
    }

    public void startAttack() {
        if (attackTimer <= 0f) attackTimer = ATTACK_DURATION;
    }

    public void takeDamage() {
        if (hurtTimer > 0f || lives <= 0) return;
        lives--;
        hurtTimer = HURT_DURATION;
    }

    public void updateTimers(float delta) {
        attackTimer = Math.max(0f, attackTimer - delta);
        hurtTimer = Math.max(0f, hurtTimer - delta);
    }

    public Rectangle getAttackBounds() {
        float attackWidth = 38f;
        float attackHeight = 28f;
        float attackX = facing > 0 ? bounds.x + bounds.width : bounds.x - attackWidth;
        float attackY = bounds.y + 10f;
        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    public void reset() {
        bounds.set(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
        velocity.setZero();
        lives = 3;
        facing = 1;
        grounded = false;
        attackTimer = 0f;
        hurtTimer = 0f;
    }

    //Item müssen auch noch implementiert werden: 
    // DMG up
    // Range up
    // HP up
    // Speed up
    // Dash!
    //...
}
