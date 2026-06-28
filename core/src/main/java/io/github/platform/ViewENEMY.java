package io.github.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ViewENEMY { 
    private static final float SPRITE_SCALE = 3f;
    private static final float LIGHTNING_FALL_WIDTH = 48f;
    private static final float LIGHTNING_FALL_HEIGHT = 190f;
    private static final float LIGHTNING_IMPACT_WIDTH = 96f;
    private static final float LIGHTNING_IMPACT_HEIGHT = 116f;

    private final Texture enemyTexture;
    private final Texture lightningTexture;
    private final TextureRegion lightningFallRegion;
    private final TextureRegion lightningImpactRegion;

    public ViewENEMY() {
        enemyTexture = new Texture("Enemy_static.png");
        lightningTexture = new Texture("lightning_sprite_.png");
        lightningFallRegion = new TextureRegion(lightningTexture, 0, 0, 172, 343);
        lightningImpactRegion = new TextureRegion(lightningTexture, 172, 0, 172, 343);
    }

    public void render(ShapeRenderer shapes, ModelWAVE wave) {
        shapes.setColor(new Color(1f, 0.86f, 0.22f, 1f));
        for (ModelPROJECTILE projectile : wave.getProjectiles()) {
            shapes.circle(
                projectile.getBounds().x + projectile.getBounds().width / 2f,
                projectile.getBounds().y + projectile.getBounds().height / 2f,
                projectile.getBounds().width / 2f
            );
        }

    }

    public void renderSprites(SpriteBatch batch, ModelWAVE wave) {
        for (ModelENEMY enemy : wave.getEnemies()) {
            float spriteWidth = enemy.getBounds().width * SPRITE_SCALE;
            float spriteHeight = enemy.getBounds().height * SPRITE_SCALE;
            float spriteX = enemy.getBounds().x + enemy.getBounds().width / 2f - spriteWidth / 2f;
            float spriteY = enemy.getBounds().y + enemy.getBounds().height / 2f - spriteHeight / 2f;
            if (enemy.getFacing() < 0) {
                batch.draw(enemyTexture, spriteX + spriteWidth, spriteY, -spriteWidth, spriteHeight);
            } else {
                batch.draw(enemyTexture, spriteX, spriteY, spriteWidth, spriteHeight);
            }
        }

        if (wave.isLightningEffectActive()) {
            float effectX = wave.getLightningEffectX();
            float effectY = wave.getLightningEffectY();
            batch.draw(
                lightningFallRegion,
                effectX - LIGHTNING_FALL_WIDTH / 2f,
                effectY + 8f,
                LIGHTNING_FALL_WIDTH,
                LIGHTNING_FALL_HEIGHT
            );
            batch.draw(
                lightningImpactRegion,
                effectX - LIGHTNING_IMPACT_WIDTH / 2f,
                effectY - LIGHTNING_IMPACT_HEIGHT / 2f,
                LIGHTNING_IMPACT_WIDTH,
                LIGHTNING_IMPACT_HEIGHT
            );
        }
    }

    public void dispose() {
        enemyTexture.dispose();
        lightningTexture.dispose();
    }
}
