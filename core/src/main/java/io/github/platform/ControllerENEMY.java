package io.github.platform;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//enemy wave controller
public class ControllerENEMY {
    //wave scaling stuff
    private static final int BASE_ENEMIES_PER_WAVE = 2;
    private static final int BASE_MAX_ENEMIES = 7;
    private static final int BASE_ENEMY_SWORD_HITS = 1;
    private static final float BASE_ENEMY_SPEED = 95f;
    private static final float BASE_SPAWN_INTERVAL = 2.2f;
    private static final int SCATTER_PROJECTILE_COUNT = 14;
    private static final float SCATTER_PROJECTILE_SPEED = 260f;
    private static final float SCATTER_PROJECTILE_DAMAGE = 0.75f;
    private static final float LIGHTNING_EFFECT_DURATION = 0.18f;

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

    //used by view
    public Array<ModelENEMY> getEnemies() {
        return enemies;
    }

    public Array<ModelPROJECTILE> getProjectiles() {
        return projectiles;
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

    public boolean isLightningEffectActive() {
        return lightningEffectTimer > 0f;
    }

    public float getLightningEffectX() {
        return lightningEffectX;
    }

    public float getLightningEffectY() {
        return lightningEffectY;
    }

    public void update(ModelMAP map, ModelPLAYER player, ControllerPLAYER controllerPlayer, float delta) {
        if (waveComplete) return;

        //spawn timer + lightning visual timer
        lightningEffectTimer = Math.max(0f, lightningEffectTimer - delta);
        spawnTimer -= delta; 
        if (spawnTimer <= 0f && spawnedThisWave < getEnemiesThisWave() && enemies.size < getMaxEnemiesAlive()) {
            spawnEnemy(map);
            spawnTimer = getSpawnInterval();
        }

        updateProjectiles(delta, player);
        updateLightning(player);

        //move enemies and check all player damage sources
        for (int i = enemies.size - 1; i >= 0; i--) {
            ModelENEMY enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                enemies.removeIndex(i);
                continue;
            }

            enemy.updateTowards(player.getBounds(), delta); 

            if (player.isAttacking() && player.getAttackBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(enemy, player.getSwordDamage(), player.getAttackId(), player)) {
                    enemies.removeIndex(i);
                    continue;
                }
            }

            if (player.getPrimaryItem() == ModelPLAYER.PrimaryItem.MAGIC_WAND
                && controllerPlayer.getMagicOrbBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(enemy, controllerPlayer.getMagicOrbDamage(player), controllerPlayer.getMagicOrbAttackId(), player)) {
                    enemies.removeIndex(i);
                    continue;
                }
            }

            if (player.isDashing() && player.getDashBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(enemy, player.getDashDamage(), player.getDashAttackId(), player)) {
                    enemies.removeIndex(i);
                    continue;
                }
            }

            Rectangle brimstoneBeamBounds = player.getBrimstoneBeamBounds();
            if (player.isBrimstoneBeamActive() && brimstoneBeamBounds.overlaps(enemy.getBounds())) {
                if (damageEnemy(enemy, player.getBrimstoneDamage(), player.getBrimstoneAttackId(), player)) {
                    enemies.removeIndex(i);
                    continue;
                }
            }

            if (!player.isDashing() && enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage();
            }
        }

        waveComplete = spawnedThisWave >= getEnemiesThisWave() && enemies.size == 0;
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

    private void updateProjectiles(float delta, ModelPLAYER player) {
        //scatter balls
        for (int i = projectiles.size - 1; i >= 0; i--) {
            ModelPROJECTILE projectile = projectiles.get(i);
            projectile.update(delta);
            if (!projectile.isAlive()) {
                projectiles.removeIndex(i);
                continue;
            }

            for (int j = enemies.size - 1; j >= 0; j--) {
                ModelENEMY enemy = enemies.get(j);
                if (!projectile.getBounds().overlaps(enemy.getBounds())) continue;

                if (damageEnemy(enemy, SCATTER_PROJECTILE_DAMAGE, projectile.getAttackId(), player)) {
                    enemies.removeIndex(j);
                }
            }
        }
    }

    private void updateLightning(ModelPLAYER player) {
        //zap random enemy
        if (!player.isLightningRequested() || enemies.size == 0) return;

        int enemyIndex = MathUtils.random(enemies.size - 1);
        ModelENEMY enemy = enemies.get(enemyIndex);
        Rectangle bounds = enemy.getBounds();
        lightningEffectX = bounds.x + bounds.width / 2f;
        lightningEffectY = bounds.y + bounds.height / 2f;
        lightningEffectTimer = LIGHTNING_EFFECT_DURATION;

        if (damageEnemy(enemy, player.getLightningDamage(), player.getLightningAttackId(), player)) {
            enemies.removeIndex(enemyIndex);
        }
    }

    private boolean damageEnemy(ModelENEMY enemy, float damage, int attackId, ModelPLAYER player) {
        enemy.takeDamage(damage, attackId);
        if (!enemy.isAlive()) {
            spawnScatter(enemy, player);
            return true;
        }
        return false;
    }

    private void spawnScatter(ModelENEMY enemy, ModelPLAYER player) {
        //death burst passive
        if (!player.hasScatter()) return;

        Rectangle bounds = enemy.getBounds();
        float centerX = bounds.x + bounds.width / 2f;
        float centerY = bounds.y + bounds.height / 2f;
        for (int i = 0; i < SCATTER_PROJECTILE_COUNT; i++) {
            float angle = MathUtils.PI2 * i / SCATTER_PROJECTILE_COUNT;
            projectiles.add(new ModelPROJECTILE(
                centerX,
                centerY,
                MathUtils.cos(angle) * SCATTER_PROJECTILE_SPEED,
                MathUtils.sin(angle) * SCATTER_PROJECTILE_SPEED,
                ++effectAttackId
            ));
        }
    }

    private void spawnEnemy(ModelMAP map) {
        //cycles spawn points
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
