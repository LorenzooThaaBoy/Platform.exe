package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ViewMain extends ApplicationAdapter {
    private ShapeRenderer shapes;

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
        shapes = new ShapeRenderer();

        map = new ModelMAP();
        player = new ModelPLAYER();
        shop = new ModelSHOP();

        controllerPlayer = new ControllerPLAYER();
        controllerEnemy = new ControllerENEMY();
        controllerShop = new ControllerSHOP();

        viewMap = new ViewMAP();
        viewPlayer = new ViewPLAYER();
        viewEnemy = new ViewENEMY();
        viewShop = new ViewSHOP();

    }

    @Override
    public void render() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (gameState == GameState.PLAYING) {
            controllerPlayer.update(player, map, delta);
            controllerEnemy.update(map, player, delta);

            if (player.getLives() <= 0) {
                gameState = GameState.GAME_OVER;
            }
        } else if (gameState == GameState.GAME_OVER && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restart();
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        if (gameState == GameState.SHOP) {
            viewShop.render(shapes);
        } else {
            viewMap.render(shapes, map);
            viewEnemy.render(shapes, controllerEnemy);
            viewPlayer.render(shapes, player);
            renderLives();
            if (gameState == GameState.GAME_OVER) {
                renderGameOverOverlay();
            }
        }
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }

    private void restart() {
        player.reset();
        controllerEnemy.reset();
        gameState = GameState.PLAYING;
    }

    private void renderLives() {
        shapes.setColor(0.9f, 0.18f, 0.25f, 1f);
        for (int i = 0; i < player.getLives(); i++) {
            shapes.rect(16f + i * 22f, ModelMAP.WORLD_HEIGHT - 28f, 16f, 16f);
        }
    }

    private void renderGameOverOverlay() {
        shapes.setColor(0f, 0f, 0f, 0.55f);
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);
        shapes.setColor(0.9f, 0.2f, 0.25f, 1f);
        shapes.rect(220f, 210f, 200f, 60f);
    }
}
