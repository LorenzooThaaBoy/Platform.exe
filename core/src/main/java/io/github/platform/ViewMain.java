package io.github.platform;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
//main view, owns render loop + drawing
public class ViewMain extends ApplicationAdapter {
    //hud slots
    private static final Rectangle PRIMARY_ITEM_SLOT = new Rectangle(16f, 16f, 42f, 42f);
    private static final Rectangle SECONDARY_ITEM_SLOT = new Rectangle(66f, 16f, 42f, 42f);

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture heartTexture;

    private ModelGAME game;
    private ModelMAP map;
    private ModelPLAYER player;
    private ModelSHOP shop;

    private ControllerGAME controllerGame;
    private ControllerPLAYER controllerPlayer;
    private ControllerENEMY controllerEnemy;
    private ControllerSHOP controllerShop;

    private ViewMAP viewMap;
    private ViewPLAYER viewPlayer;
    private ViewENEMY viewEnemy;
    private ViewSHOP viewShop;

    @Override
    public void create() {
        //libgdx setup
        camera = new OrthographicCamera();
        viewport = new StretchViewport(ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT, camera);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        heartTexture = new Texture("Hrat.png");

        game = new ModelGAME();
        map = new ModelMAP();
        player = new ModelPLAYER();
        shop = new ModelSHOP();

        controllerGame = new ControllerGAME();
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
        //cap delta so physics doesnt explode
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        controllerGame.update(
            game,
            map,
            player,
            shop,
            controllerPlayer,
            controllerEnemy,
            controllerShop,
            viewport,
            delta
        );
        renderGame(delta);
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

    private void renderGame(float delta) {
        //shop has its own screen
        if (game.isShop()) {
            renderShop();
        } else {
            renderWorld(delta);
        }

        batch.begin();
        renderLives();
        renderText();
        if (game.isShop()) {
            viewShop.renderText(batch, font, shop, player, controllerEnemy.getWave() + 1);
        }
        batch.end();
    }

    private void renderShop() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        viewShop.render(shapes, shop);
        renderTransitionOverlay();
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderWorld(float delta) {
        //batch for textures
        batch.begin();
        viewMap.render(batch, map);
        batch.end();

        //shapes for effects / hitboxes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        viewEnemy.render(shapes, controllerEnemy);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        viewEnemy.renderSprites(batch, controllerEnemy);
        viewPlayer.render(batch, player, game.isPaused() ? 0f : delta);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        viewPlayer.renderHitboxes(shapes, player);
        renderMagicOrb();
        if (shouldRenderItemSlots()) {
            renderItemSlots();
        }
        if (game.isGameOver()) {
            renderGameOverOverlay();
        }
        if (game.isPaused()) {
            renderPauseOverlay();
        }
        renderTransitionOverlay();
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderLives() {
        for (int i = 0; i < player.getLives(); i++) {
            batch.draw(heartTexture, 16f + i * 24f, ModelMAP.WORLD_HEIGHT - 30f, 20f, 20f);
        }
    }

    private void renderGameOverOverlay() {
        Rectangle restartButton = controllerGame.getRestartButton();
        shapes.setColor(0f, 0f, 0f, 0.55f);
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);
        shapes.setColor(0.9f, 0.2f, 0.25f, 1f);
        shapes.rect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
    }

    private void renderPauseOverlay() {
        Rectangle continueButton = controllerGame.getContinueButton();
        Rectangle leaveGameButton = controllerGame.getLeaveGameButton();

        shapes.setColor(0f, 0f, 0f, 0.6f);
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);
        shapes.setColor(0.25f, 0.62f, 1f, 1f);
        shapes.rect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        shapes.setColor(0.9f, 0.2f, 0.25f, 1f);
        shapes.rect(leaveGameButton.x, leaveGameButton.y, leaveGameButton.width, leaveGameButton.height);
    }

    private void renderText() {
        //hud text
        font.draw(batch, "Lives: " + player.getLives() + "/" + player.getMaxLives(), 16f, ModelMAP.WORLD_HEIGHT - 38f);
        font.draw(batch, "Wave: " + controllerEnemy.getWave() + "  Enemies: " + controllerEnemy.getSpawnedThisWave() + "/" + controllerEnemy.getEnemiesThisWave(), 16f, ModelMAP.WORLD_HEIGHT - 58f);
        font.draw(batch, "Enemy hits: " + controllerEnemy.getEnemyHitPoints() + "  Health scale: +" + controllerEnemy.getEnemyHealthIncreases() + "  Speed: " + (int)controllerEnemy.getEnemySpeed(), 16f, ModelMAP.WORLD_HEIGHT - 78f);

        if (game.isGameOver()) {
            Rectangle restartButton = controllerGame.getRestartButton();
            font.draw(batch, "GAME OVER", 285f, 260f);
            font.draw(batch, "RESTART", restartButton.x + 45f, restartButton.y + 33f);
            font.draw(batch, "Press R or click the button", 235f, 160f);
        }

        if (game.isPaused()) {
            Rectangle continueButton = controllerGame.getContinueButton();
            Rectangle leaveGameButton = controllerGame.getLeaveGameButton();
            font.draw(batch, "PAUSED", 365f, 330f);
            font.draw(batch, "CONTINUE", continueButton.x + 63f, continueButton.y + 33f);
            font.draw(batch, "LEAVE GAME", leaveGameButton.x + 56f, leaveGameButton.y + 33f);
        }

        if (shouldRenderItemSlots()) {
            renderItemSlotText();
        }
    }

    private void renderTransitionOverlay() {
        if (game.getTransitionTimer() <= 0f) return;

        float alpha = Math.min(0.5f, game.getTransitionTimer() / 0.45f * 0.5f);
        shapes.setColor(0f, 0f, 0f, alpha);
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);
    }

    private void renderItemSlots() {
        //item boxes bottom left
        renderItemSlot(PRIMARY_ITEM_SLOT, new Color(0.22f, 0.22f, 0.3f, 0.85f));
        renderItemSlot(SECONDARY_ITEM_SLOT, new Color(0.18f, 0.28f, 0.34f, 0.85f));

        if (player.isBrimstoneCharging()) {
            shapes.setColor(1f, 0.28f, 0.05f, 1f);
            shapes.rect(
                SECONDARY_ITEM_SLOT.x,
                SECONDARY_ITEM_SLOT.y - 5f,
                SECONDARY_ITEM_SLOT.width * player.getBrimstoneChargeProgress(),
                3f
            );
        }
    }

    private void renderMagicOrb() {
        if (player.getPrimaryItem() != ModelPLAYER.PrimaryItem.MAGIC_HAT) return;

        Rectangle magicOrbBounds = controllerPlayer.getMagicOrbBounds();
        if (magicOrbBounds.width <= 0f) return;

        shapes.setColor(new Color(0.25f, 0.55f, 1f, 0.8f));
        shapes.circle(
            magicOrbBounds.x + magicOrbBounds.width / 2f,
            magicOrbBounds.y + magicOrbBounds.height / 2f,
            magicOrbBounds.width / 2f
        );
    }

    private boolean shouldRenderItemSlots() {
        return game.isPlaying() || game.isPaused();
    }

    private void renderItemSlot(Rectangle slot, Color fillColor) {
        shapes.setColor(0.05f, 0.05f, 0.07f, 0.9f);
        shapes.rect(slot.x - 3f, slot.y - 3f, slot.width + 6f, slot.height + 6f);
        shapes.setColor(fillColor);
        shapes.rect(slot.x, slot.y, slot.width, slot.height);
    }

    private void renderItemSlotText() {
        font.draw(batch, getPrimaryItemLabel(), PRIMARY_ITEM_SLOT.x + 8f, PRIMARY_ITEM_SLOT.y + 25f);
        font.draw(batch, getSecondaryItemLabel(), SECONDARY_ITEM_SLOT.x + 8f, SECONDARY_ITEM_SLOT.y + 25f);
    }

    private String getPrimaryItemLabel() {
        return player.getPrimaryItem() == ModelPLAYER.PrimaryItem.MAGIC_HAT ? "H" : "-";
    }

    private String getSecondaryItemLabel() {
        switch (player.getSecondaryItem()) {
            case LIGHTNING:
                return "L";
            case BRIMSTONE:
                return "B";
            default:
                return "-";
        }
    }
}
