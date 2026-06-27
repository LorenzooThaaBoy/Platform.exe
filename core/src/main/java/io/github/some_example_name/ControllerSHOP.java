package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ControllerSHOP {
    public boolean update(ModelSHOP shop, ModelPLAYER player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            shop.selectPrevious();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            shop.selectNext();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (shop.isItemSelected()) {
                return true;
            }

            shop.applySelectedItem(player);
            shop.selectItem();
        }

        return false;
    }
}
