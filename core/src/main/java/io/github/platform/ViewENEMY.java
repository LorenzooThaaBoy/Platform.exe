package io.github.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ViewENEMY { 
    private static final float SPRITE_SCALE = 3f;

    private final Texture enemyTexture;

    public ViewENEMY() {
        enemyTexture = new Texture("Enemy_static.png");
    }

    public void render(ShapeRenderer shapes, ControllerENEMY controllerEnemy) {
        shapes.setColor(new Color(0.88f, 0.18f, 0.42f, 1f));
        for (ModelENEMY enemy : controllerEnemy.getEnemies()) {
            shapes.circle(
                enemy.getBounds().x + enemy.getBounds().width / 2f,
                enemy.getBounds().y + enemy.getBounds().height / 2f,
                enemy.getBounds().width / 2f
            );
        }

        shapes.setColor(new Color(1f, 0.86f, 0.22f, 1f));
        for (ModelPROJECTILE projectile : controllerEnemy.getProjectiles()) {
            shapes.circle(
                projectile.getBounds().x + projectile.getBounds().width / 2f,
                projectile.getBounds().y + projectile.getBounds().height / 2f,
                projectile.getBounds().width / 2f
            );
        }

        if (controllerEnemy.isLightningEffectActive()) {
            shapes.setColor(new Color(0.45f, 0.9f, 1f, 0.85f));
            shapes.rect(controllerEnemy.getLightningEffectX() - 4f, controllerEnemy.getLightningEffectY(), 8f, ModelMAP.WORLD_HEIGHT - controllerEnemy.getLightningEffectY());
            shapes.circle(controllerEnemy.getLightningEffectX(), controllerEnemy.getLightningEffectY(), 20f);
        }
    }

    public void renderSprites(SpriteBatch batch, ControllerENEMY controllerEnemy) {
        for (ModelENEMY enemy : controllerEnemy.getEnemies()) {
            float spriteWidth = enemy.getBounds().width * SPRITE_SCALE;
            float spriteHeight = enemy.getBounds().height * SPRITE_SCALE;
            batch.draw(
                enemyTexture,
                enemy.getBounds().x + enemy.getBounds().width / 2f - spriteWidth / 2f,
                enemy.getBounds().y + enemy.getBounds().height / 2f - spriteHeight / 2f,
                spriteWidth,
                spriteHeight
            );
        }
    }

    public void dispose() {
        enemyTexture.dispose();
    }
}
