package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ViewMain extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    ModelMAP map;
    ModelPLAYER player;
    ModelSHOP shop; 

    ControllerPLAYER controllerPlayer;
    ControllerENEMY controllerEnemy;
    ControllerSHOP controllerShop; 

    ViewMAP viewMap;
    ViewPLAYER viewPlayer; 
    ViewENEMY viewEnemy;
    ViewSHOP viewShop;

    enum GameState{
        PLAYING, SHOP, GAME_OVER
    }

    GameState gameState = GameState.PLAYING;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        batch = new SpriteBatch();

        map = new ModelMAP();

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
