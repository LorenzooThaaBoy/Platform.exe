package io.github.platform;

import com.badlogic.gdx.utils.Array;

//enemy wave data + scaling
public class ModelWAVE {
    private static final int BASE_ENEMIES_PER_WAVE = 2;
    private static final int BASE_MAX_ENEMIES = 7;
    private static final int BASE_ENEMY_SWORD_HITS = 1;
    private static final float BASE_ENEMY_SPEED = 95f;
    private static final float BASE_SPAWN_INTERVAL = 2.2f;

    private final Array<ModelENEMY> enemies = new Array<>();
    private final Array<ModelPROJECTILE> projectiles = new Array<>();
    private float spawnTimer;
    private float lightningEffectTimer;
    private float lightningEffectX;
    private float lightningEffectY;
    private int nextSpawnPoint;
    private int effectAttackId;
    private int wave = 1;
    private int spawnedThisWave;
    private boolean waveComplete;

    public Array<ModelENEMY> getEnemies() {
        return enemies;
    }

    public Array<ModelPROJECTILE> getProjectiles() {
        return projectiles;
    }

    public float getSpawnTimer() {
        return spawnTimer;
    }

    public void setSpawnTimer(float spawnTimer) {
        this.spawnTimer = spawnTimer;
    }

    public void updateSpawnTimer(float delta) {
        spawnTimer -= delta;
    }

    public boolean isLightningEffectActive() {
        return lightningEffectTimer > 0f;
    }

    public void updateLightningEffectTimer(float delta) {
        lightningEffectTimer = Math.max(0f, lightningEffectTimer - delta);
    }

    public void startLightningEffect(float x, float y, float duration) {
        lightningEffectX = x;
        lightningEffectY = y;
        lightningEffectTimer = duration;
    }

    public float getLightningEffectX() {
        return lightningEffectX;
    }

    public float getLightningEffectY() {
        return lightningEffectY;
    }

    public int getNextSpawnPoint() {
        return nextSpawnPoint;
    }

    public void advanceSpawnPoint(int spawnPointCount) {
        nextSpawnPoint = (nextSpawnPoint + 1) % spawnPointCount;
    }

    public int nextEffectAttackId() {
        effectAttackId++;
        return effectAttackId;
    }

    public int getWave() {
        return wave;
    }

    public int getSpawnedThisWave() {
        return spawnedThisWave;
    }

    public void enemySpawned() {
        spawnedThisWave++;
    }

    public int getEnemiesThisWave() {
        return BASE_ENEMIES_PER_WAVE + getEnemyCountIncreases() * 2;
    }

    public int getEnemyHitPoints() {
        return BASE_ENEMY_SWORD_HITS + getEnemyHealthIncreases() / 3;
    }

    public int getEnemyHealthIncreases() {
        return (wave + 1) / 3;
    }

    public float getEnemySpeed() {
        return BASE_ENEMY_SPEED + getEnemySpeedIncreases() * 14f;
    }

    public int getMaxEnemiesAlive() {
        return BASE_MAX_ENEMIES + getEnemyCountIncreases();
    }

    public float getSpawnInterval() {
        return Math.max(0.75f, BASE_SPAWN_INTERVAL - getEnemyCountIncreases() * 0.12f);
    }

    public boolean isWaveComplete() {
        return waveComplete;
    }

    public void setWaveComplete(boolean waveComplete) {
        this.waveComplete = waveComplete;
    }

    public boolean shouldOpenShop() {
        return wave > 0 && wave % 3 == 0;
    }

    public void reset() {
        enemies.clear();
        projectiles.clear();
        spawnTimer = 0f;
        lightningEffectTimer = 0f;
        nextSpawnPoint = 0;
        effectAttackId = 0;
        wave = 1;
        spawnedThisWave = 0;
        waveComplete = false;
    }

    public void startNextWave() {
        wave++;
        enemies.clear();
        projectiles.clear();
        spawnTimer = 0f;
        lightningEffectTimer = 0f;
        spawnedThisWave = 0;
        waveComplete = false;
    }

    private int getEnemyCountIncreases() {
        return (wave + 2) / 3;
    }

    private int getEnemySpeedIncreases() {
        return wave / 3;
    }
}
