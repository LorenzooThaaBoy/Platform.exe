package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ViewMAP {
    public void render(ShapeRenderer shapes, ModelMAP map) {
        shapes.setColor(new Color(0.13f, 0.18f, 0.22f, 1f));
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);

        shapes.setColor(new Color(0.35f, 0.45f, 0.38f, 1f));
        for (Rectangle platform : map.getPlatforms()) {
            shapes.rect(platform.x, platform.y, platform.width, platform.height);
        }
    }
}
