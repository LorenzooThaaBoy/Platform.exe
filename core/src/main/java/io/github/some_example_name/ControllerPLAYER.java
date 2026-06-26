package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ControllerPLAYER {
    private static final float MAGIC_ORB_SIZE = 18f;
    private static final float MAGIC_ORB_BASE_SPEED = 185f;
    private static final float MAGIC_ORB_BASE_DPS = 2f;
    private static final float MAGIC_ORB_DAMAGE_INTERVAL = 0.25f;

    private final Rectangle magicOrbBounds = new Rectangle();
    private float magicOrbDamageTimer;
    private int magicOrbAttackId = -100000;

    public Rectangle getMagicOrbBounds() {
        return magicOrbBounds;
    }

    public float getMagicOrbDamage(ModelPLAYER player) {
        return MAGIC_ORB_BASE_DPS * MAGIC_ORB_DAMAGE_INTERVAL * player.getSwordDamage();
    }

    public int getMagicOrbAttackId() {
        return magicOrbAttackId;
    }

    public void update(ModelPLAYER player, ModelMAP map, float delta) {
        player.updateTimers(delta);

        float movement = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { //left
            movement -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {//right
            movement += 1f;
        }

        player.setFacingFromMovement(movement);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            player.startDash();
        }
        player.updateSecondaryItemInput(delta, Gdx.input.isKeyJustPressed(Input.Keys.C), Gdx.input.isKeyPressed(Input.Keys.C));

        player.getVelocity().x = player.isDashing() ? player.getFacing() * player.getDashSpeed() : movement * player.getMoveSpeed();

        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) //foward
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && player.isGrounded()) { //jump
            player.getVelocity().y = ModelPLAYER.JUMP_SPEED;
            player.setGrounded(false);
        }

        float magicOrbDirectionX = 0f;
        float magicOrbDirectionY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) magicOrbDirectionX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) magicOrbDirectionX += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) magicOrbDirectionY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) magicOrbDirectionY += 1f;
        updateMagicOrb(player, magicOrbDirectionX, magicOrbDirectionY, delta);

        if (player.getPrimaryItem() == ModelPLAYER.PrimaryItem.MAGIC_HAT) {
            moveAndCollide(player, map, delta);
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.startAttack(0, 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            player.startAttack(-1, 0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            player.startAttack(1, 0);
        }

        moveAndCollide(player, map, delta);
    }

    private void moveAndCollide(ModelPLAYER player, ModelMAP map, float delta) { //colllison logic 
        Rectangle bounds = player.getBounds();

        bounds.x += player.getVelocity().x * delta;
        bounds.x = MathUtils.clamp(bounds.x, 0f, ModelMAP.WORLD_WIDTH - bounds.width);

        float previousY = bounds.y;
        player.getVelocity().y += ModelPLAYER.GRAVITY * delta;
        bounds.y += player.getVelocity().y * delta;
        player.setGrounded(false);

        for (Rectangle platform : map.getPlatforms()) {
            if (!bounds.overlaps(platform)) continue;

            float previousBottom = previousY;
            float previousTop = previousY + bounds.height;
            float platformTop = platform.y + platform.height;

            if (player.getVelocity().y <= 0f && previousBottom >= platformTop - 3f) { //platform collision
                bounds.y = platformTop;
                player.getVelocity().y = 0f;
                player.setGrounded(true);
            } else if (player.getVelocity().y > 0f && previousTop <= platform.y + 3f) {
                bounds.y = platform.y - bounds.height;
                player.getVelocity().y = 0f;
            }
        }

        if (bounds.y < ModelMAP.GROUND_Y) {
            bounds.y = ModelMAP.GROUND_Y;
            player.getVelocity().y = 0f;
            player.setGrounded(true);
        }
    }

    private void updateMagicOrb(ModelPLAYER player, float directionX, float directionY, float delta) {
        if (player.getPrimaryItem() != ModelPLAYER.PrimaryItem.MAGIC_HAT) {
            magicOrbBounds.set(0f, 0f, 0f, 0f);
            return;
        }

        if (magicOrbBounds.width <= 0f) {
            resetMagicOrbPosition(player);
        }

        magicOrbDamageTimer -= delta;
        if (magicOrbDamageTimer <= 0f) {
            magicOrbAttackId--;
            magicOrbDamageTimer = MAGIC_ORB_DAMAGE_INTERVAL;
        }

        float lengthSquared = directionX * directionX + directionY * directionY;
        if (lengthSquared > 0f) {
            float length = (float)Math.sqrt(lengthSquared);
            float speed = MAGIC_ORB_BASE_SPEED * player.getMoveSpeed() / ModelPLAYER.MOVE_SPEED;
            magicOrbBounds.x += directionX / length * speed * delta;
            magicOrbBounds.y += directionY / length * speed * delta;
        }

        magicOrbBounds.x = MathUtils.clamp(magicOrbBounds.x, 0f, ModelMAP.WORLD_WIDTH - magicOrbBounds.width);
        magicOrbBounds.y = MathUtils.clamp(magicOrbBounds.y, ModelMAP.GROUND_Y, ModelMAP.WORLD_HEIGHT - magicOrbBounds.height);
    }

    private void resetMagicOrbPosition(ModelPLAYER player) {
        Rectangle playerBounds = player.getBounds();
        magicOrbBounds.set(
            playerBounds.x + playerBounds.width / 2f - MAGIC_ORB_SIZE / 2f,
            playerBounds.y + playerBounds.height / 2f - MAGIC_ORB_SIZE / 2f,
            MAGIC_ORB_SIZE,
            MAGIC_ORB_SIZE
        );
    }
}
