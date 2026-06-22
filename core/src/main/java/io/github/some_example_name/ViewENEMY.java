package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ViewENEMY { //TODO: add sprite sheet for animation 
    public void render(ShapeRenderer shapes, ControllerENEMY controllerEnemy) {
        shapes.setColor(new Color(0.88f, 0.18f, 0.42f, 1f));
        for (ModelENEMY enemy : controllerEnemy.getEnemies()) {
            shapes.circle(
                enemy.getBounds().x + enemy.getBounds().width / 2f,
                enemy.getBounds().y + enemy.getBounds().height / 2f,
                enemy.getBounds().width / 2f
            );
        }
    }
}
