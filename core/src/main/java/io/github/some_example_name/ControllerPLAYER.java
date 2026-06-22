package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ControllerPLAYER {
    public void update(ModelPLAYER player, ModelMAP map, float delta) {
        player.updateTimers(delta);

        float movement = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement += 1f;
        }

        player.setFacingFromMovement(movement);
        player.getVelocity().x = movement * ModelPLAYER.MOVE_SPEED;

        if ((Gdx.input.isKeyJustPressed(Input.Keys.W)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && player.isGrounded()) {
            player.getVelocity().y = ModelPLAYER.JUMP_SPEED;
            player.setGrounded(false);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)
            || Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            player.startAttack();
        }

        moveAndCollide(player, map, delta);
    }

    private void moveAndCollide(ModelPLAYER player, ModelMAP map, float delta) {
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

            if (player.getVelocity().y <= 0f && previousBottom >= platformTop - 3f) {
                bounds.y = platformTop;
                player.getVelocity().y = 0f;
                player.setGrounded(true);
            } else if (player.getVelocity().y > 0f && previousTop <= platform.y + 3f) {
                bounds.y = platform.y - bounds.height;
                player.getVelocity().y = 0f;
            }
        }

        if (bounds.y < 0f) {
            bounds.y = 0f;
            player.getVelocity().y = 0f;
            player.setGrounded(true);
        }
    }
}
