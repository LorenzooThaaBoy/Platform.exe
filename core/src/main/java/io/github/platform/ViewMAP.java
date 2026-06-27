package io.github.platform;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ViewMAP {
    private static final float PLATFORM_TEXTURE_WIDTH_SCALE = 1.3f;
    private static final float PLATFORM_TEXTURE_HEIGHT_SCALE = 5.5f;
    private static final float PLATFORM_TEXTURE_X_OFFSET = -25f;
    private static final float PLATFORM_TEXTURE_Y_OFFSET = -44f;

    private final Texture backgroundTexture;
    private final Texture platformTexture;

    public ViewMAP() {
        backgroundTexture = new Texture("Background.png");
        platformTexture = new Texture("PlatformTexture.png");
    }
    
    public void render(SpriteBatch batch, ModelMAP map) {
        batch.draw(backgroundTexture, 0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);

        for (Rectangle platform : map.getPlatforms()) {
            float textureWidth = platform.width * PLATFORM_TEXTURE_WIDTH_SCALE;
            float textureHeight = platform.height * PLATFORM_TEXTURE_HEIGHT_SCALE;
            float textureX = platform.x + PLATFORM_TEXTURE_X_OFFSET;
            float textureY = platform.y + PLATFORM_TEXTURE_Y_OFFSET;

            batch.draw(platformTexture, textureX, textureY, textureWidth, textureHeight);
        }
    }
    public void dispose() {
        backgroundTexture.dispose();
        platformTexture.dispose();
    }
}
