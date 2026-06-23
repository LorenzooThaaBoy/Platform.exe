package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color; //TODO: Player Sprites (for spine engine) 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ViewPLAYER {
    public void render(ShapeRenderer shapes, ModelPLAYER player) {
        if (player.isHurt()) { //AUA https://youtu.be/unguG0f3xyk?si=wVCINfs0U8mpaCos
            shapes.setColor(new Color(1f, 0.35f, 0.35f, 1f));
        } else {
            shapes.setColor(new Color(0.25f, 0.62f, 1f, 1f));
        }

        shapes.rect(
            player.getBounds().x,
            player.getBounds().y,
            player.getBounds().width,
            player.getBounds().height
        );

        if (player.isAttacking()) {
            shapes.setColor(new Color(1f, 0.86f, 0.35f, 0.65f));
            shapes.rect(
                player.getAttackBounds().x,
                player.getAttackBounds().y,
                player.getAttackBounds().width,
                player.getAttackBounds().height
            );
        }
    }
}
