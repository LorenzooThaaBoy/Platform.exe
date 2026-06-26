package io.github.some_example_name; //TODO: Make a git release version + see how to make package/.jdk 

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ControllerENEMY {
    private static final int BASE_ENEMIES_PER_WAVE = 0;
    private static final int BASE_MAX_ENEMIES = 0;
    private static final int BASE_ENEMY_SWORD_HITS = 1;
    private static final float BASE_ENEMY_SPEED = 95f;
    private static final float BASE_SPAWN_INTERVAL = 2.2f;

    private final Array<ModelENEMY> enemies = new Array<>();
    private float spawnTimer;
    private int nextSpawnPoint;
    private int wave = 1;
    private int spawnedThisWave;
    private boolean waveComplete;

    public Array<ModelENEMY> getEnemies() {
        return enemies;
    }

    public int getWave() {
        return wave;
    }

    public int getSpawnedThisWave() {
        return spawnedThisWave;
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

    public boolean isWaveComplete() {
        return waveComplete;
    }

    public boolean shouldOpenShop() {
        return wave > 0 && wave % 3 == 0;
    }

    public void update(ModelMAP map, ModelPLAYER player, float delta) {
        if (waveComplete) return;

        spawnTimer -= delta;
        if (spawnTimer <= 0f && spawnedThisWave < getEnemiesThisWave() && enemies.size < getMaxEnemiesAlive()) {
            spawnEnemy(map);
            spawnTimer = getSpawnInterval();
        }

        for (int i = enemies.size - 1; i >= 0; i--) { //enemy death
            ModelENEMY enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                enemies.removeIndex(i);
                continue;
            }

            enemy.updateTowards(player.getBounds(), delta); 

            if (player.isAttacking() && player.getAttackBounds().overlaps(enemy.getBounds())) { // enemy death logic
                enemy.takeDamage(player.getSwordDamage(), player.getAttackId());
                if (!enemy.isAlive()) {
                    enemies.removeIndex(i);
                    continue;
                }
            }

            if (enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage();
            }
        }

        waveComplete = spawnedThisWave >= getEnemiesThisWave() && enemies.size == 0;
    }

    public void reset() {
        enemies.clear();
        spawnTimer = 0f;
        nextSpawnPoint = 0;
        wave = 1;
        spawnedThisWave = 0;
        waveComplete = false;
    }

    public void startNextWave() {
        wave++;
        enemies.clear();
        spawnTimer = 0f;
        spawnedThisWave = 0;
        waveComplete = false;
    }

    private void spawnEnemy(ModelMAP map) {
        if (map.getEnemySpawnPoints().size == 0) return; 

        Vector2 spawnPoint = map.getEnemySpawnPoints().get(nextSpawnPoint);
        enemies.add(new ModelENEMY(spawnPoint.x, spawnPoint.y, getEnemyHitPoints(), getEnemySpeed()));
        spawnedThisWave++;
        nextSpawnPoint = (nextSpawnPoint + 1) % map.getEnemySpawnPoints().size;
    }

    private int getEnemyCountIncreases() {
        return (wave + 2) / 3;
    }

    private int getEnemySpeedIncreases() {
        return wave / 3;
    }

    private int getMaxEnemiesAlive() {
        return BASE_MAX_ENEMIES + getEnemyCountIncreases();
    }

    private float getSpawnInterval() {
        return Math.max(0.75f, BASE_SPAWN_INTERVAL - getEnemyCountIncreases() * 0.12f);
    }
}
