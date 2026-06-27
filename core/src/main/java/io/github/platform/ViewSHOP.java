package io.github.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//TODO: item sprites later
public class ViewSHOP {
    //shop card layout
    private static final float CARD_WIDTH = 150f;
    private static final float CARD_HEIGHT = 170f;
    private static final float CARD_Y = 145f;
    private static final float FIRST_CARD_X = 125f;
    private static final float CARD_GAP = 35f;

    public void render(ShapeRenderer shapes, ModelSHOP shop) {
        //shop background
        shapes.setColor(new Color(0.12f, 0.1f, 0.18f, 1f));
        shapes.rect(0f, 0f, ModelMAP.WORLD_WIDTH, ModelMAP.WORLD_HEIGHT);

        //3 item cards
        for (int i = 0; i < shop.getItems().length; i++) {
            float x = FIRST_CARD_X + i * (CARD_WIDTH + CARD_GAP);
            boolean selected = i == shop.getSelectedIndex();

            shapes.setColor(selected ? new Color(0.95f, 0.78f, 0.25f, 1f) : new Color(0.28f, 0.25f, 0.35f, 1f));
            shapes.rect(x - 5f, CARD_Y - 5f, CARD_WIDTH + 10f, CARD_HEIGHT + 10f);

            shapes.setColor(shop.isItemSelected() && selected ? new Color(0.25f, 0.62f, 1f, 1f) : new Color(0.18f, 0.16f, 0.24f, 1f));
            shapes.rect(x, CARD_Y, CARD_WIDTH, CARD_HEIGHT);

            shapes.setColor(new Color(0.7f, 0.7f, 0.78f, 1f));
            shapes.rect(x + 45f, CARD_Y + 85f, 60f, 55f);
        }
    }

    public void renderText(SpriteBatch batch, BitmapFont font, ModelSHOP shop, ModelPLAYER player, int nextWave) {
        //text layer after shapes
        font.draw(batch, "SHOP", 365f, 390f);
        font.draw(batch, "Wave " + nextWave + " starts after you leave", 280f, 365f);

        for (int i = 0; i < shop.getItems().length; i++) {
            float x = FIRST_CARD_X + i * (CARD_WIDTH + CARD_GAP);
            font.draw(batch, shop.getItemName(i), x + 32f, CARD_Y + 58f);
            font.draw(batch, shop.getItemDescription(i, player), x + 18f, CARD_Y + 36f);
        }

        if (shop.isItemSelected()) {
            font.draw(batch, shop.getSelectedItem() + " selected", 325f, 105f);
            font.draw(batch, "Press Space to continue", 310f, 82f);
        } else {
            font.draw(batch, "A/D or Left/Right to choose, Space to buy", 245f, 95f);
        }
    }
}
