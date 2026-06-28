package io.github.platform;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//keep this mvc, model = player data + player ability state
public class ModelPLAYER {
    //equipped item types
    public enum PrimaryItem {
        NONE, MAGIC_WAND
    }

    public enum SecondaryItem {
        NONE,
        LIGHTNING,
        BRIMSTONE
    }

    //player stats
    public static final float WIDTH = 32f;
    public static final float HEIGHT = 46f;
    public static final float MOVE_SPEED = 210f;
    public static final float JUMP_SPEED = 420f;
    public static final float GRAVITY = -980f;
    private static final float ATTACK_DURATION = 0.18f;
    private static final float HURT_DURATION = 0.75f;
    private static final float DASH_DURATION = 0.16f;
    private static final float DASH_COOLDOWN = 0.55f;
    private static final float BASE_ATTACK_DAMAGE = 1f;
    private static final float BASE_ATTACK_REACH = 38f;
    private static final float BASE_ATTACK_WIDTH = 44f;
    private static final float BASE_ATTACK_HEIGHT = 30f;
    private static final float BASE_UP_ATTACK_WIDTH = 34f;
    private static final float BASE_UP_ATTACK_HEIGHT = 48f;
    private static final float[] DAMAGE_MULTIPLIERS = {1f, 1.2f, 1.7f, 2.3f, 3f};
    private static final float[] RANGE_BONUSES = {0f, 14f, 28f, 44f, 62f};
    private static final float[] SPEED_MULTIPLIERS = {1f, 1.15f, 1.32f, 1.5f, 1.7f};
    private static final float[] DASH_SPEEDS = {0f, 520f, 650f, 780f, 930f};
    private static final float[] DASH_DAMAGE = {0f, 1f, 1.4f, 2f, 2.8f};
    private static final float[] DASH_REACH = {0f, 36f, 48f, 62f, 78f};
    private static final int BASE_MAX_LIVES = 3;
    private static final float LIGHTNING_DAMAGE = 2.25f;
    private static final float BRIMSTONE_CHARGE_DURATION = 1.2f;
    private static final float BRIMSTONE_BEAM_DURATION = 0.22f;
    private static final float SECONDARY_ABILITY_COOLDOWN = 5f;
    private static final float BRIMSTONE_BEAM_HEIGHT = 18f;
    private static final float BRIMSTONE_DAMAGE = 4f;

    //position + current state
    private final Rectangle bounds = new Rectangle(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
    private final Vector2 velocity = new Vector2();
    private int lives = BASE_MAX_LIVES;
    private int maxLives = BASE_MAX_LIVES;
    private int facing = 1;
    private boolean grounded;
    private float attackTimer;
    private float hurtTimer;
    private float dashTimer;
    private float dashCooldownTimer;
    private int attackId;
    private int dashAttackId;
    private int dashDirection = 1;
    private int attackDirectionX = 1;
    private int attackDirectionY;
    private int damageLevel;
    private int rangeLevel;
    private int speedLevel;
    private int dashLevel;
    private PrimaryItem primaryItem = PrimaryItem.NONE;
    private SecondaryItem secondaryItem = SecondaryItem.NONE;
    private boolean scatterUnlocked;
    private boolean lightningRequested;
    private int lightningAttackId;
    private float brimstoneChargeTimer;
    private float brimstoneBeamTimer;
    private float brimstoneCooldownTimer;
    private int brimstoneDirection = 1;
    private int brimstoneAttackId;

    //getters for controller/view stuff
    public Rectangle getBounds() { 
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getLives() {
        return lives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getFacing() {
        return facing;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isAttacking() {
        return attackTimer > 0f;
    }

    public boolean isHurt() {
        return hurtTimer > 0f;
    }

    public boolean isDashing() {
        return dashTimer > 0f;
    }

    public PrimaryItem getPrimaryItem() {
        return primaryItem;
    }

    public SecondaryItem getSecondaryItem() {
        return secondaryItem;
    }

    public boolean hasScatter() {
        return scatterUnlocked;
    }

    public boolean isLightningRequested() {
        return lightningRequested;
    }

    public float getLightningDamage() {
        return LIGHTNING_DAMAGE * DAMAGE_MULTIPLIERS[damageLevel];
    }

    public int getLightningAttackId() {
        return lightningAttackId;
    }

    public boolean isBrimstoneCharging() {
        return secondaryItem == SecondaryItem.BRIMSTONE && brimstoneChargeTimer > 0f && brimstoneBeamTimer <= 0f;
    }

    public float getBrimstoneChargeProgress() {
        return Math.min(1f, brimstoneChargeTimer / BRIMSTONE_CHARGE_DURATION);
    }

    public boolean isBrimstoneBeamActive() {
        return brimstoneBeamTimer > 0f;
    }

    public float getBrimstoneDamage() {
        return BRIMSTONE_DAMAGE * DAMAGE_MULTIPLIERS[damageLevel];
    }

    public int getBrimstoneAttackId() {
        return brimstoneAttackId;
    }

    public void setFacingFromMovement(float movement) {
        if (movement > 0f) facing = 1;
        if (movement < 0f) facing = -1;
    }

    public void startAttack(int directionX, int directionY) {
        if (attackTimer <= 0f) {
            attackDirectionX = directionX;
            attackDirectionY = directionY;
            if (directionX != 0) facing = directionX;
            attackTimer = ATTACK_DURATION;
            attackId++;
        }
    }

    public int getAttackId() {
        return attackId;
    }

    public float getSwordDamage() {
        return BASE_ATTACK_DAMAGE * DAMAGE_MULTIPLIERS[damageLevel];
    }

    public float getMoveSpeed() {
        return MOVE_SPEED * SPEED_MULTIPLIERS[speedLevel];
    }

    public float getDashSpeed() {
        return DASH_SPEEDS[dashLevel];
    }

    public float getDashDamage() {
        return DASH_DAMAGE[dashLevel];
    }

    public int getDashAttackId() {
        return dashAttackId;
    }

    public int getDamageLevel() {
        return damageLevel;
    }

    public int getRangeLevel() {
        return rangeLevel;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public int getDashLevel() {
        return dashLevel;
    }

    public float getNextDamageMultiplier() {
        return DAMAGE_MULTIPLIERS[Math.min(damageLevel + 1, DAMAGE_MULTIPLIERS.length - 1)];
    }

    public float getNextRangeBonus() {
        return RANGE_BONUSES[Math.min(rangeLevel + 1, RANGE_BONUSES.length - 1)];
    }

    public float getNextSpeedMultiplier() {
        return SPEED_MULTIPLIERS[Math.min(speedLevel + 1, SPEED_MULTIPLIERS.length - 1)];
    }

    public float getNextDashSpeed() {
        return DASH_SPEEDS[Math.min(dashLevel + 1, DASH_SPEEDS.length - 1)];
    }

    public float getNextDashDamage() {
        return DASH_DAMAGE[Math.min(dashLevel + 1, DASH_DAMAGE.length - 1)];
    }

    public boolean canUpgradeDamage() {
        return damageLevel < DAMAGE_MULTIPLIERS.length - 1;
    }

    public boolean canUpgradeRange() {
        return rangeLevel < RANGE_BONUSES.length - 1;
    }

    public boolean canUpgradeSpeed() {
        return speedLevel < SPEED_MULTIPLIERS.length - 1;
    }

    public boolean canUpgradeDash() {
        return dashLevel < DASH_SPEEDS.length - 1;
    }

    public int getAttackDirectionX() {
        return attackDirectionX;
    }

    public int getAttackDirectionY() {
        return attackDirectionY;
    }

    public void takeDamage() {
        if (hurtTimer > 0f || lives <= 0) return;
        lives--;
        hurtTimer = HURT_DURATION;
    }

    public void updateTimers(float delta) {
        //count down action windows
        attackTimer = Math.max(0f, attackTimer - delta);
        hurtTimer = Math.max(0f, hurtTimer - delta);
        dashTimer = Math.max(0f, dashTimer - delta);
        dashCooldownTimer = Math.max(0f, dashCooldownTimer - delta);
        brimstoneBeamTimer = Math.max(0f, brimstoneBeamTimer - delta);
        brimstoneCooldownTimer = Math.max(0f, brimstoneCooldownTimer - delta);
    }

    public void updateSecondaryItemInput(float delta, boolean justPressed, boolean pressed) {
        //lightning = tap C
        lightningRequested = false;

        if (secondaryItem == SecondaryItem.LIGHTNING) {
            if (justPressed && brimstoneCooldownTimer <= 0f) {
                lightningRequested = true;
                lightningAttackId = ++attackId;
                brimstoneCooldownTimer = SECONDARY_ABILITY_COOLDOWN;
            }
            return;
        }

        //brimstone = hold C then release
        if (secondaryItem != SecondaryItem.BRIMSTONE) {
            brimstoneChargeTimer = 0f;
            return;
        }

        if (brimstoneBeamTimer > 0f) {
            brimstoneChargeTimer = 0f;
            return;
        }

        if (!pressed) {
            if (brimstoneChargeTimer >= BRIMSTONE_CHARGE_DURATION) {
                startBrimstoneBeam();
            } else {
                brimstoneChargeTimer = 0f;
            }
            return;
        }

        if (brimstoneCooldownTimer > 0f) {
            brimstoneChargeTimer = 0f;
            return;
        }

        if (brimstoneChargeTimer <= 0f) {
            brimstoneDirection = facing;
        }

        brimstoneChargeTimer = Math.min(BRIMSTONE_CHARGE_DURATION, brimstoneChargeTimer + delta);
    }

    public void startDash() {
        //dash only after upgrade
        if (dashLevel <= 0 || dashTimer > 0f || dashCooldownTimer > 0f) return;

        dashDirection = facing;
        dashTimer = DASH_DURATION;
        dashCooldownTimer = DASH_COOLDOWN;
        dashAttackId = ++attackId;
        hurtTimer = Math.max(hurtTimer, DASH_DURATION);
    }

    public Rectangle getAttackBounds() {
        //sword hitbox, side or up
        float rangeBonus = RANGE_BONUSES[rangeLevel];
        float attackWidth = attackDirectionY > 0 ? BASE_UP_ATTACK_WIDTH + rangeBonus * 0.4f : BASE_ATTACK_WIDTH + rangeBonus;
        float attackHeight = attackDirectionY > 0 ? BASE_UP_ATTACK_HEIGHT + rangeBonus : BASE_ATTACK_HEIGHT;
        float sideReach = BASE_ATTACK_REACH + rangeBonus;
        float attackX = attackDirectionY > 0
            ? bounds.x + bounds.width / 2f - attackWidth / 2f
            : attackDirectionX > 0 ? bounds.x + bounds.width + sideReach - attackWidth : bounds.x - sideReach;
        float attackY = attackDirectionY > 0 ? bounds.y + bounds.height : bounds.y + 9f;
        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    public Rectangle getDashBounds() {
        //dash damage box in front
        float reach = DASH_REACH[dashLevel];
        float dashX = dashDirection > 0 ? bounds.x + bounds.width : bounds.x - reach;
        return new Rectangle(dashX, bounds.y + 4f, reach, bounds.height - 8f);
    }

    public Rectangle getBrimstoneBeamBounds() {
        //beam across screen
        float beamY = bounds.y + bounds.height / 2f - BRIMSTONE_BEAM_HEIGHT / 2f;
        if (brimstoneDirection > 0) {
            float beamX = bounds.x + bounds.width;
            return new Rectangle(beamX, beamY, ModelMAP.WORLD_WIDTH - beamX, BRIMSTONE_BEAM_HEIGHT);
        }

        return new Rectangle(0f, beamY, bounds.x, BRIMSTONE_BEAM_HEIGHT);
    }

    public void upgradeDamage() {
        if (canUpgradeDamage()) damageLevel++;
    }

    public void upgradeRange() {
        if (canUpgradeRange()) rangeLevel++;
    }

    public void upgradeHealth() {
        maxLives++;
        lives = maxLives;
    }

    public void upgradeSpeed() {
        if (canUpgradeSpeed()) speedLevel++;
    }

    public void upgradeDash() {
        if (canUpgradeDash()) dashLevel++;
    }

    public void equipPrimaryItem(PrimaryItem primaryItem) {
        this.primaryItem = primaryItem;
        attackTimer = 0f;
    }

    public void equipSecondaryItem(SecondaryItem secondaryItem) {
        this.secondaryItem = secondaryItem;
        lightningRequested = false;
        brimstoneChargeTimer = 0f;
        brimstoneBeamTimer = 0f;
        brimstoneCooldownTimer = 0f;
    }

    public void unlockScatter() {
        scatterUnlocked = true;
    }

    public void reset() {
        //new run reset
        bounds.set(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
        velocity.setZero();
        lives = BASE_MAX_LIVES;
        maxLives = BASE_MAX_LIVES;
        facing = 1;
        grounded = false;
        attackTimer = 0f;
        hurtTimer = 0f;
        dashTimer = 0f;
        dashCooldownTimer = 0f;
        attackId = 0;
        dashAttackId = 0;
        dashDirection = 1;
        attackDirectionX = 1;
        attackDirectionY = 0;
        damageLevel = 0;
        rangeLevel = 0;
        speedLevel = 0;
        dashLevel = 0;
        primaryItem = PrimaryItem.NONE;
        secondaryItem = SecondaryItem.NONE;
        scatterUnlocked = false;
        lightningRequested = false;
        lightningAttackId = 0;
        brimstoneChargeTimer = 0f;
        brimstoneBeamTimer = 0f;
        brimstoneCooldownTimer = 0f;
        brimstoneDirection = 1;
        brimstoneAttackId = 0;
    }

    private void startBrimstoneBeam() {
        brimstoneChargeTimer = 0f;
        brimstoneBeamTimer = BRIMSTONE_BEAM_DURATION;
        brimstoneCooldownTimer = SECONDARY_ABILITY_COOLDOWN;
        brimstoneAttackId = ++attackId;
    }

}
