package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ViewMain extends ApplicationAdapter {
    private static final Rectangle RESTART_BUTTON = new Rectangle(245f, 180f, 150f, 54f); //after death
    private static final Rectangle DEBUG_SHOP_BUTTON = new Rectangle(655f, 430f, 90f, 34f);

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture heartTexture;
    private float transitionTimer;

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
        heartTexture = new Texture("Hrat.png");

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

        updateInput(delta);
        renderGame(delta);
    }

    private void updateInput(float delta) {
        transitionTimer = Math.max(0f, transitionTimer - delta);

        if (gameState == GameState.PLAYING) {
            if (debugShopRequested()) {
                openShop();
                return;
            }

            controllerPlayer.update(player, map, delta);
            controllerEnemy.update(map, player, delta);

            if (player.getLives() <= 0) {
                gameState = GameState.GAME_OVER;
            } else if (controllerEnemy.isWaveComplete()) {
                if (controllerEnemy.shouldOpenShop()) {
                    openShop();
                } else {
                    controllerEnemy.startNextWave();
                    transitionTimer = 0.35f;
                }
            }
        } else if (gameState == GameState.SHOP) {
            if (controllerShop.update(shop)) {
                controllerEnemy.startNextWave();
                gameState = GameState.PLAYING;
                transitionTimer = 0.45f;
            }
        } else if (gameState == GameState.GAME_OVER && restartRequested()) {
            restart();
        }
    }

    private void renderGame(float delta) {
        if (gameState == GameState.SHOP) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            viewShop.render(shapes, shop);
            renderTransitionOverlay();
            shapes.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            batch.begin();
            viewMap.render(batch, map);
            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            //viewMap.renderHitboxGuides(shapes, map); 
            viewEnemy.render(shapes, controllerEnemy);
            shapes.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
            viewEnemy.renderSprites(batch, controllerEnemy);
            viewPlayer.render(batch, player, delta);
            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            viewPlayer.renderHitboxes(shapes, player);
            if (gameState == GameState.PLAYING) {
                renderDebugShopButton();
            }
            if (gameState == GameState.GAME_OVER) {
                renderGameOverOverlay();
            }
            renderTransitionOverlay();
            shapes.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        batch.begin();
        renderLives();
        renderText();
        if (gameState == GameState.SHOP) {
            viewShop.renderText(batch, font, shop, controllerEnemy.getWave() + 1);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        viewMap.dispose();
        viewPlayer.dispose();
        viewEnemy.dispose();
        shapes.dispose();
        batch.dispose();
        font.dispose();
        heartTexture.dispose();
    }

    private void restart() {
        player.reset();
        controllerEnemy.reset();
        transitionTimer = 0.35f;
        gameState = GameState.PLAYING;
    }

    private void openShop() {
        shop.resetForShop();
        gameState = GameState.SHOP;
        transitionTimer = 0.45f;
    }

    private void renderLives() {
        for (int i = 0; i < player.getLives(); i++) {
            batch.draw(heartTexture, 16f + i * 24f, ModelMAP.WORLD_HEIGHT - 30f, 20f, 20f);
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

        return RESTART_BUTTON.contains(getWorldClick());
    }

    private boolean debugShopRequested() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) return true;
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return false;

        return DEBUG_SHOP_BUTTON.contains(getWorldClick());
    }

    private Vector2 getWorldClick() {
        return viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }

    private void renderText() {
        font.draw(batch, "Lives: " + player.getLives(), 16f, ModelMAP.WORLD_HEIGHT - 38f);
        font.draw(batch, "Wave: " + controllerEnemy.getWave() + "  Enemies: " + controllerEnemy.getSpawnedThisWave() + "/" + controllerEnemy.getEnemiesThisWave(), 16f, ModelMAP.WORLD_HEIGHT - 58f);
        font.draw(batch, "Enemy hits: " + controllerEnemy.getEnemyHitPoints() + "  Health scale: +" + controllerEnemy.getEnemyHealthIncreases() + "  Speed: " + (int)controllerEnemy.getEnemySpeed(), 16f, ModelMAP.WORLD_HEIGHT - 78f);

        if (gameState == GameState.PLAYING) {
            font.draw(batch, "SHOP", DEBUG_SHOP_BUTTON.x + 27f, DEBUG_SHOP_BUTTON.y + 23f);
            font.draw(batch, "T", DEBUG_SHOP_BUTTON.x + 41f, DEBUG_SHOP_BUTTON.y - 4f);
        }

        if (gameState == GameState.GAME_OVER) {
            font.draw(batch, "GAME OVER", 285f, 260f);
            font.draw(batch, "RESTART", RESTART_BUTTON.x + 45f, RESTART_BUTTON.y + 33f);
            font.draw(batch, "Press R or click the button", 235f, 160f);
        }
    }

    private void renderTransitionOverlay() {
        if (transitionTimer <= 0f) return;

        float alpha = Math.min(0.5f, transitionTimer / 0.45f * 0.5f);
        shapes.setColor(0f, 0f, 0f, alpha);
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);
    }

    private void renderDebugShopButton() {
        shapes.setColor(0.12f, 0.1f, 0.18f, 0.75f);
        shapes.rect(DEBUG_SHOP_BUTTON.x, DEBUG_SHOP_BUTTON.y, DEBUG_SHOP_BUTTON.width, DEBUG_SHOP_BUTTON.height);
        shapes.setColor(0.95f, 0.78f, 0.25f, 1f);
        shapes.rect(DEBUG_SHOP_BUTTON.x, DEBUG_SHOP_BUTTON.y, DEBUG_SHOP_BUTTON.width, 3f);
    }
}
