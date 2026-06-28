package io.github.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ViewPLAYER {
    /*
     * Spritesheet tuning:
     * - The bordered guide sheet marks 6 columns x 4 rows.
     * - Change FRAME_BORDER_X/Y if a new export moves the frame borders.
     * - Change *_FRAME_COUNT and *_DURATION to use fewer/more poses or make animations faster.
     * - Change DRAW_* and BODY_* when the art size should match the player hitbox differently.
     * - *_FRAME_OFFSET_* is for tiny per-frame fixes if one pose still slides.
     */
    private static final String CHARACTER_SHEET_PATH = "character_sprite_sheet.png";
    private static final int[] FRAME_BORDER_X = {32, 418, 797, 1175, 1555, 1933, 2318};
    private static final int[] FRAME_BORDER_Y = {32, 405, 772, 1163, 1530};
    private static final int FRAME_BORDER_INSET = 5;

    private static final int IDLE_ROW = 0;
    private static final int WALK_ROW = 1;
    private static final int HURT_ROW = 2;
    private static final int ATTACK_ROW = 3;

    private static final int IDLE_FRAME_COUNT = 5;
    private static final int WALK_FRAME_COUNT = 6;
    private static final int HURT_FRAME_COUNT = 4;
    private static final int ATTACK_FRAME_COUNT = 6;

    private static final float IDLE_DURATION = 0.70f;
    private static final float WALK_DURATION = 0.55f;
    private static final float HURT_DURATION = 0.75f;
    private static final float ATTACK_DURATION = 0.58f;

    private static final boolean ANIMATE_IDLE_WHEN_STANDING = true;
    private static final boolean APPLY_FRAME_OFFSETS = true;
    private static final float BODY_ANCHOR_X = 37f;
    private static final float[] IDLE_ANCHOR_X = {164.5f, 153f, 144.5f, 141f, 134f};
    private static final float[] WALK_ANCHOR_X = {151f, 155f, 137.5f, 129.5f, 145f, 121f};
    private static final float[] HURT_ANCHOR_X = {209f, 197f, 194.5f, 195f};
    private static final float[] ATTACK_ANCHOR_X = {83.5f, 80f, 80.5f, 158.5f, 142.5f, 110f};
    private static final float[] IDLE_FRAME_OFFSET_X = {0f, 0f, 0f, 0f, 0f}; // for each frame a seperate offset
    private static final float[] WALK_FRAME_OFFSET_X = {.3f, 1f, 0f, 0f, 0f, 0f};
    private static final float[] HURT_FRAME_OFFSET_X = {0f, 0f, 0f, 0f};
    private static final float[] ATTACK_FRAME_OFFSET_X = {0f, 0f, 0f, 0f, 0f, 0f};
    private static final float[] IDLE_FRAME_OFFSET_Y = {0f, -.5f, -.5f, -.5f, -.5f};
    private static final float[] WALK_FRAME_OFFSET_Y = {0f, 0f, 0f, 0f, 0f, 0f};
    private static final float[] HURT_FRAME_OFFSET_Y = {3f, 3.5f, 3.5f, 3.5f};
    private static final float[] ATTACK_FRAME_OFFSET_Y = {0f, 0f, -.5f, -0f, -0f, 0f};

    private static final float DRAW_WIDTH = 84f;
    private static final float DRAW_HEIGHT = 84f;
    private static final float BODY_OFFSET_X = DRAW_WIDTH / 2f - BODY_ANCHOR_X;
    private static final float BODY_OFFSET_Y = -7f;
    private static final float HURT_DRAW_OFFSET_Y = -3f;
    private static final float ATTACK_DRAW_OFFSET_X = 0f;
    private static final float MAGIC_ORB_RENDER_SIZE_MULTIPLIER = 1.5f;
    private static final float LASER_DRAW_HEIGHT = 44f;
    private static final float LASER_IMPACT_WIDTH = 92f;
    private static final float LASER_IMPACT_HEIGHT = 48f;

    private final Texture characterSheet;
    private final Texture magicOrbTexture;
    private final Texture laserTexture;
    private final TextureRegion laserBeamRegion;
    private final TextureRegion laserImpactRegion;
    private final SpriteAnimation idleAnimation;
    private final SpriteAnimation walkAnimation;
    private final SpriteAnimation hurtAnimation;
    private final SpriteAnimation attackAnimation;

    private float animationTime;
    private float hurtAnimationTime;
    private float attackAnimationTime;
    private int lastAttackId;
    private boolean attackVisualActive;

    public ViewPLAYER() {
        characterSheet = new Texture(CHARACTER_SHEET_PATH);
        characterSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        magicOrbTexture = new Texture("MagicOrbSprite.png");
        laserTexture = new Texture("texture_laser.png");
        laserBeamRegion = new TextureRegion(laserTexture, 154, 677, 70, 626);
        laserImpactRegion = new TextureRegion(laserTexture, 477, 519, 138, 71);

        idleAnimation = createAnimation(IDLE_ROW, IDLE_FRAME_COUNT, IDLE_DURATION, true, IDLE_ANCHOR_X, IDLE_FRAME_OFFSET_X, IDLE_FRAME_OFFSET_Y);
        walkAnimation = createAnimation(WALK_ROW, WALK_FRAME_COUNT, WALK_DURATION, true, WALK_ANCHOR_X, WALK_FRAME_OFFSET_X, WALK_FRAME_OFFSET_Y);
        hurtAnimation = createAnimation(HURT_ROW, HURT_FRAME_COUNT, HURT_DURATION, false, HURT_ANCHOR_X, HURT_FRAME_OFFSET_X, HURT_FRAME_OFFSET_Y);
        attackAnimation = createAnimation(ATTACK_ROW, ATTACK_FRAME_COUNT, ATTACK_DURATION, false, ATTACK_ANCHOR_X, ATTACK_FRAME_OFFSET_X, ATTACK_FRAME_OFFSET_Y);
    }

    public void render(SpriteBatch batch, ModelPLAYER player, float delta) {
        animationTime += delta;
        updateOneShotTimers(player, delta);

        SpriteAnimation animation = selectAnimation(player);
        float stateTime = getStateTime(player, animation);
        SpriteFrame frame = animation.getKeyFrame(stateTime);

        float drawX = player.getBounds().x + player.getBounds().width / 2f - DRAW_WIDTH / 2f
            + BODY_OFFSET_X + frame.getOffsetX(player.getFacing());
        float drawY = player.getBounds().y + BODY_OFFSET_Y + frame.offsetY;

        if (animation == hurtAnimation) {
            drawY += HURT_DRAW_OFFSET_Y;
        } else if (animation == attackAnimation) {
            drawX += player.getFacing() * ATTACK_DRAW_OFFSET_X;
        }

        if (player.isHurt()) {
            batch.setColor(1f, 0.55f, 0.55f, 1f);
        }

        if (player.getFacing() < 0) {
            batch.draw(frame.region, drawX + frame.drawWidth, drawY, -frame.drawWidth, frame.drawHeight);
        } else {
            batch.draw(frame.region, drawX, drawY, frame.drawWidth, frame.drawHeight);
        }

        if (player.isHurt()) {
            batch.setColor(Color.WHITE);
        }
    }

    public void renderMagicOrb(SpriteBatch batch, ModelPLAYER player, Rectangle magicOrbBounds) {
        if (player.getPrimaryItem() != ModelPLAYER.PrimaryItem.MAGIC_WAND) return;
        if (magicOrbBounds.width <= 0f) return;

        float width = magicOrbBounds.width * MAGIC_ORB_RENDER_SIZE_MULTIPLIER;
        float height = magicOrbBounds.height * MAGIC_ORB_RENDER_SIZE_MULTIPLIER;
        float x = magicOrbBounds.x + magicOrbBounds.width / 2f - width / 2f;
        float y = magicOrbBounds.y + magicOrbBounds.height / 2f - height / 2f;
        batch.draw(magicOrbTexture, x, y, width, height);
    }

    public void renderLaser(SpriteBatch batch, ModelPLAYER player) {
        if (!player.isLaserBeamActive()) return;

        Rectangle beamBounds = player.getLaserBeamBounds();
        float beamLength = beamBounds.width;
        float centerY = beamBounds.y + beamBounds.height / 2f;
        float impactX;

        if (player.getLaserDirection() > 0) {
            impactX = beamBounds.x + beamLength;
            if (beamLength > 0f) {
                batch.draw(
                    laserBeamRegion,
                    beamBounds.x,
                    centerY + LASER_DRAW_HEIGHT / 2f,
                    0f,
                    0f,
                    LASER_DRAW_HEIGHT,
                    beamLength,
                    1f,
                    1f,
                    -90f
                );
            }
            drawLaserImpact(batch, player, impactX, centerY);
        } else {
            impactX = beamBounds.x;
            if (beamLength > 0f) {
                batch.draw(
                    laserBeamRegion,
                    beamBounds.x + beamLength,
                    centerY - LASER_DRAW_HEIGHT / 2f,
                    0f,
                    0f,
                    LASER_DRAW_HEIGHT,
                    beamLength,
                    1f,
                    1f,
                    90f
                );
            }
            drawLaserImpact(batch, player, impactX, centerY);
        }
    }

    private void drawLaserImpact(SpriteBatch batch, ModelPLAYER player, float impactX, float centerY) {
        if (player.isLaserImpactEnemyHit()) {
            float width = LASER_IMPACT_HEIGHT;
            float height = LASER_IMPACT_WIDTH;
            float direction = player.getLaserDirection();
            float drawX = impactX - width / 2f + direction * width * 0.25f;
            float drawY = centerY - height / 2f;
            batch.draw(
                laserImpactRegion,
                drawX,
                drawY,
                width / 2f,
                height / 2f,
                width,
                height,
                1f,
                1f,
                direction > 0f ? -90f : 90f
            );
            return;
        }

        if (player.getLaserDirection() > 0) {
            batch.draw(
                laserImpactRegion,
                impactX - LASER_IMPACT_WIDTH * 0.35f,
                centerY - LASER_IMPACT_HEIGHT / 2f,
                LASER_IMPACT_WIDTH,
                LASER_IMPACT_HEIGHT
            );
        } else {
            batch.draw(
                laserImpactRegion,
                impactX + LASER_IMPACT_WIDTH * 0.35f,
                centerY - LASER_IMPACT_HEIGHT / 2f,
                -LASER_IMPACT_WIDTH,
                LASER_IMPACT_HEIGHT
            );
        }
    }

    public void renderHitboxes(ShapeRenderer shapes, ModelPLAYER player) {
        if (player.isHurt()) {
            shapes.setColor(new Color(1f, 0.35f, 0.35f, 0.35f));
        } else {
            shapes.setColor(new Color(0.25f, 0.62f, 1f, 0.25f));
        }
        shapes.rect(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);

        if (player.isAttacking()) {
            shapes.setColor(new Color(1f, 0.86f, 0.35f, 0.55f));
            shapes.rect(
                player.getAttackBounds().x,
                player.getAttackBounds().y,
                player.getAttackBounds().width,
                player.getAttackBounds().height
            );
        }

        if (player.isDashing()) {
            shapes.setColor(new Color(0.45f, 0.9f, 1f, 0.45f));
            shapes.rect(
                player.getDashBounds().x,
                player.getDashBounds().y,
                player.getDashBounds().width,
                player.getDashBounds().height
            );
        }

        if (player.isLaserBeamActive()) {
            Rectangle laserBeamBounds = player.getLaserBeamBounds();
            shapes.setColor(new Color(1f, 0.18f, 0.08f, 0.65f));
            shapes.rect(
                laserBeamBounds.x,
                laserBeamBounds.y,
                laserBeamBounds.width,
                laserBeamBounds.height
            );
        }
    }

    public void dispose() {
        characterSheet.dispose();
        magicOrbTexture.dispose();
        laserTexture.dispose();
    }

    private void updateOneShotTimers(ModelPLAYER player, float delta) {
        if (player.isHurt()) {
            hurtAnimationTime += delta;
        } else {
            hurtAnimationTime = 0f;
        }

        if (player.getAttackId() != lastAttackId) {
            lastAttackId = player.getAttackId();
            attackAnimationTime = 0f;
            attackVisualActive = true;
        }

        if (attackVisualActive) {
            attackAnimationTime += delta;
            if (attackAnimationTime >= ATTACK_DURATION) {
                attackVisualActive = false;
            }
        }
    }

    private SpriteAnimation selectAnimation(ModelPLAYER player) {
        if (player.isHurt()) return hurtAnimation;
        if (attackVisualActive) return attackAnimation;
        if (player.isGrounded() && Math.abs(player.getVelocity().x) > 1f) return walkAnimation;
        return idleAnimation;
    }

    private float getStateTime(ModelPLAYER player, SpriteAnimation animation) {
        if (animation == hurtAnimation) return hurtAnimationTime;
        if (animation == attackAnimation) return attackAnimationTime;
        if (animation == walkAnimation) {
            return animationTime * Math.max(0.65f, player.getMoveSpeed() / ModelPLAYER.MOVE_SPEED);
        }
        if (!ANIMATE_IDLE_WHEN_STANDING) {
            return 0f;
        }
        return animationTime;
    }

    private SpriteAnimation createAnimation(
        int row,
        int frameCount,
        float duration,
        boolean looping,
        float[] frameAnchorX,
        float[] frameOffsetX,
        float[] frameOffsetY
    ) {
        SpriteFrame[] frames = new SpriteFrame[frameCount];
        for (int column = 0; column < frameCount; column++) {
            int x = FRAME_BORDER_X[column] + FRAME_BORDER_INSET;
            int y = FRAME_BORDER_Y[row] + FRAME_BORDER_INSET;
            int width = FRAME_BORDER_X[column + 1] - FRAME_BORDER_X[column] - FRAME_BORDER_INSET * 2;
            int height = FRAME_BORDER_Y[row + 1] - FRAME_BORDER_Y[row] - FRAME_BORDER_INSET * 2;
            TextureRegion region = new TextureRegion(
                characterSheet,
                x,
                y,
                width,
                height
            );
            float offsetX = APPLY_FRAME_OFFSETS ? frameOffsetX[column] : 0f;
            float offsetY = APPLY_FRAME_OFFSETS ? frameOffsetY[column] : 0f;
            float anchorX = frameAnchorX[column] * DRAW_WIDTH / width;
            float rightOffsetX = BODY_ANCHOR_X - anchorX + offsetX;
            float leftOffsetX = BODY_ANCHOR_X - (DRAW_WIDTH - anchorX) - offsetX;
            frames[column] = new SpriteFrame(region, DRAW_WIDTH, DRAW_HEIGHT, rightOffsetX, leftOffsetX, offsetY);
        }

        return new SpriteAnimation(frames, duration / frameCount, looping);
    }

    private static class SpriteAnimation {
        private final SpriteFrame[] frames;
        private final float frameDuration;
        private final boolean looping;

        private SpriteAnimation(SpriteFrame[] frames, float frameDuration, boolean looping) {
            this.frames = frames;
            this.frameDuration = frameDuration;
            this.looping = looping;
        }

        private SpriteFrame getKeyFrame(float stateTime) {
            int frameIndex = (int)(stateTime / frameDuration);
            if (looping) {
                frameIndex = frameIndex % frames.length;
            } else {
                frameIndex = Math.min(frameIndex, frames.length - 1);
            }
            return frames[frameIndex];
        }
    }

    private static class SpriteFrame {
        private final TextureRegion region;
        private final float drawWidth;
        private final float drawHeight;
        private final float rightOffsetX;
        private final float leftOffsetX;
        private final float offsetY;

        private SpriteFrame(
            TextureRegion region,
            float drawWidth,
            float drawHeight,
            float rightOffsetX,
            float leftOffsetX,
            float offsetY
        ) {
            this.region = region;
            this.drawWidth = drawWidth;
            this.drawHeight = drawHeight;
            this.rightOffsetX = rightOffsetX;
            this.leftOffsetX = leftOffsetX;
            this.offsetY = offsetY;
        }

        private float getOffsetX(int facing) {
            return facing < 0 ? leftOffsetX : rightOffsetX;
        }
    }

}
