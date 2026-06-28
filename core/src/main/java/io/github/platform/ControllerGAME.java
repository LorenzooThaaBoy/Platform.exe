package io.github.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;

//main game state controller
public class ControllerGAME {
    //menu buttons in world coords
    private static final Rectangle RESTART_BUTTON = new Rectangle(245f, 180f, 150f, 54f);
    private static final Rectangle CONTINUE_BUTTON = new Rectangle(300f, 235f, 200f, 54f);
    private static final Rectangle LEAVE_GAME_BUTTON = new Rectangle(300f, 165f, 200f, 54f);

    public Rectangle getRestartButton() {
        return RESTART_BUTTON;
    }

    public Rectangle getContinueButton() {
        return CONTINUE_BUTTON;
    }

    public Rectangle getLeaveGameButton() {
        return LEAVE_GAME_BUTTON;
    }

    public void update(
        ModelGAME game,
        ModelMAP map,
        ModelWAVE wave,
        ModelPLAYER player,
        ModelSHOP shop,
        ControllerPLAYER controllerPlayer,
        ControllerENEMY controllerEnemy,
        ControllerSHOP controllerShop,
        StretchViewport viewport,
        float delta
    ) {
        //state machine stuff
        if (game.isPlaying()) {
            updatePlaying(game, map, wave, player, shop, controllerPlayer, controllerEnemy, delta);
        } else if (game.isShop()) {
            updateShop(game, wave, player, shop, controllerShop);
        } else if (game.isPaused()) {
            updatePauseInput(game, viewport);
        } else if (game.isGameOver() && restartRequested(viewport)) {
            restart(game, wave, player);
        }
    }

    private void updatePlaying(
        ModelGAME game,
        ModelMAP map,
        ModelWAVE wave,
        ModelPLAYER player,
        ModelSHOP shop,
        ControllerPLAYER controllerPlayer,
        ControllerENEMY controllerEnemy,
        float delta
    ) {
        game.updateTransition(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //pause
            game.setState(ModelGAME.State.PAUSED);
            return;
        }

        controllerPlayer.update(player, map, delta);
        controllerEnemy.update(wave, map, player, delta);

        if (player.getLives() <= 0) {
            game.setState(ModelGAME.State.GAME_OVER);
        } else if (wave.isWaveComplete()) {
            //shop every 3 waves
            if (wave.shouldOpenShop()) {
                openShop(game, shop);
            } else {
                wave.startNextWave();
                game.startTransition(0.35f);
            }
        }
    }

    private void updateShop(
        ModelGAME game,
        ModelWAVE wave,
        ModelPLAYER player,
        ModelSHOP shop,
        ControllerSHOP controllerShop
    ) {
        if (!controllerShop.update(shop, player)) return;

        wave.startNextWave();
        game.setState(ModelGAME.State.PLAYING);
        game.startTransition(0.45f);
    }

    private void updatePauseInput(ModelGAME game, StretchViewport viewport) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //unpause
            game.setState(ModelGAME.State.PLAYING);
            return;
        }

        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        Vector2 click = getWorldClick(viewport);
        if (CONTINUE_BUTTON.contains(click)) {
            game.setState(ModelGAME.State.PLAYING);
        } else if (LEAVE_GAME_BUTTON.contains(click)) {
            Gdx.app.exit();
        }
    }

    private boolean restartRequested(StretchViewport viewport) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) return true;
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
            && RESTART_BUTTON.contains(getWorldClick(viewport));
    }

    private void restart(ModelGAME game, ModelWAVE wave, ModelPLAYER player) {
        player.reset();
        wave.reset();
        game.startTransition(0.35f);
        game.setState(ModelGAME.State.PLAYING);
    }

    private void openShop(ModelGAME game, ModelSHOP shop) {
        shop.resetForShop();
        game.setState(ModelGAME.State.SHOP);
        game.startTransition(0.45f);
    }

    private Vector2 getWorldClick(StretchViewport viewport) {
        //screen mouse -> game world
        return viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
