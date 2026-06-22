package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ViewMain extends ApplicationAdapter {
    private static final Rectangle RESTART_BUTTON = new Rectangle(245f, 180f, 150f, 54f);

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont font;

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
        camera = new OrthographicCamera();
        viewport = new StretchViewport(ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT, camera);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

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
        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if (gameState == GameState.PLAYING) {
            controllerPlayer.update(player, map, delta);
            controllerEnemy.update(map, player, delta);

            if (player.getLives() <= 0) {
                gameState = GameState.GAME_OVER;
            }
        } else if (gameState == GameState.GAME_OVER && restartRequested()) {
            restart();
        }

        if (gameState == GameState.SHOP) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            viewShop.render(shapes);
            shapes.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            batch.begin();
            viewMap.render(batch, map);
            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            //viewMap.renderHitboxGuides(shapes, map); //TODO: if build failed error not fixed take care of 
            viewEnemy.render(shapes, controllerEnemy);
            viewPlayer.render(shapes, player);
            renderLives();
            if (gameState == GameState.GAME_OVER) {
                renderGameOverOverlay();
            }
            shapes.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        batch.begin();
        renderText();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        viewMap.dispose();
        shapes.dispose();
        batch.dispose();
        font.dispose();
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
        shapes.rect(RESTART_BUTTON.x, RESTART_BUTTON.y, RESTART_BUTTON.width, RESTART_BUTTON.height);
    }

    private boolean restartRequested() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) return true;
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return false;

        Vector2 click = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return RESTART_BUTTON.contains(click);
    }

    private void renderText() {
        font.draw(batch, "Lives: " + player.getLives(), 16f, ModelMAP.WORLD_HEIGHT - 38f);
        font.draw(batch, "Move: A/D  Jump: W/Space  Attack: Right Mouse", 16f, ModelMAP.WORLD_HEIGHT - 58f);

        if (gameState == GameState.GAME_OVER) {
            font.draw(batch, "GAME OVER", 285f, 260f);
            font.draw(batch, "RESTART", RESTART_BUTTON.x + 45f, RESTART_BUTTON.y + 33f);
            font.draw(batch, "Press R or click the button", 235f, 160f);
        }
    }
}
