package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ControllerSHOP { // do i need to epicly comment this class? yes. yes i do. this class handles the shop logic and player input for the shop. it allows the player to navigate through the available items, select an item, and apply its effects to the player character. it also manages the state of the shop, including which item is currently selected and whether an item has been chosen. this class interacts with the ModelSHOP and ModelPLAYER classes to facilitate these actions.
    public boolean update(ModelSHOP shop, ModelPLAYER player) { // hahahahaha lmaoai comments are hilarious 
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
