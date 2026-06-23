package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color; //TODO: Make a shop sprite + mechanic  
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ViewSHOP {
    public void render(ShapeRenderer shapes) {
        shapes.setColor(new Color(0.12f, 0.1f, 0.18f, 1f));
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);

        shapes.setColor(new Color(0.8f, 0.65f, 0.25f, 1f));
        shapes.rect(180f, 150f, 280f, 160f);
    }
}
