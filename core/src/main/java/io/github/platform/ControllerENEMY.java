package io.github.platform;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//enemy wave controller
public class ControllerENEMY {
    private static final int SCATTER_PROJECTILE_COUNT = 14;
    private static final float SCATTER_PROJECTILE_SPEED = 260f;
    private static final float SCATTER_PROJECTILE_DAMAGE = 0.75f;
    private static final float LIGHTNING_EFFECT_DURATION = 0.18f;

    public void update(ModelWAVE wave, ModelMAP map, ModelPLAYER player, float delta) {
        if (wave.isWaveComplete()) return;

        //spawn timer + lightning visual timer
        wave.updateLightningEffectTimer(delta);
        wave.updateSpawnTimer(delta);
        if (wave.getSpawnTimer() <= 0f
            && wave.getSpawnedThisWave() < wave.getEnemiesThisWave()
            && wave.getEnemies().size < wave.getMaxEnemiesAlive()) {
            spawnEnemy(wave, map);
            wave.setSpawnTimer(wave.getSpawnInterval());
        }

        updateProjectiles(wave, delta, player);
        updateLightning(wave, player);
        updateLaserBeam(wave, map, player);

        //move enemies and check all player damage sources
        for (int i = wave.getEnemies().size - 1; i >= 0; i--) {
            ModelENEMY enemy = wave.getEnemies().get(i);
            if (!enemy.isAlive()) {
                wave.getEnemies().removeIndex(i);
                continue;
            }

            enemy.updateTowards(player.getBounds(), delta); 

            if (player.isAttacking() && player.getAttackBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(wave, enemy, player.getSwordDamage(), player.getAttackId(), player)) {
                    wave.getEnemies().removeIndex(i);
                    continue;
                }
            }

            if (player.getPrimaryItem() == ModelPLAYER.PrimaryItem.MAGIC_WAND
                && player.getMagicOrbBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(wave, enemy, player.getMagicOrbDamage(), player.getMagicOrbAttackId(), player)) {
                    wave.getEnemies().removeIndex(i);
                    continue;
                }
            }

            if (player.isDashing() && player.getDashBounds().overlaps(enemy.getBounds())) {
                if (damageEnemy(wave, enemy, player.getDashDamage(), player.getDashAttackId(), player)) {
                    wave.getEnemies().removeIndex(i);
                    continue;
                }
            }

            if (player.isLaserBeamActive() && isLaserHittingEnemy(player, enemy)) {
                if (damageEnemy(wave, enemy, player.getLaserDamage(), player.getLaserAttackId(), player)) {
                    wave.getEnemies().removeIndex(i);
                    continue;
                }
            }

            if (!player.isDashing() && enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage();
            }
        }

        wave.setWaveComplete(wave.getSpawnedThisWave() >= wave.getEnemiesThisWave() && wave.getEnemies().size == 0);
    }

    private void updateProjectiles(ModelWAVE wave, float delta, ModelPLAYER player) {
        //scatter balls
        for (int i = wave.getProjectiles().size - 1; i >= 0; i--) {
            ModelPROJECTILE projectile = wave.getProjectiles().get(i);
            projectile.update(delta);
            if (!projectile.isAlive()) {
                wave.getProjectiles().removeIndex(i);
                continue;
            }

            for (int j = wave.getEnemies().size - 1; j >= 0; j--) {
                ModelENEMY enemy = wave.getEnemies().get(j);
                if (!projectile.getBounds().overlaps(enemy.getBounds())) continue;

                if (damageEnemy(wave, enemy, SCATTER_PROJECTILE_DAMAGE, projectile.getAttackId(), player)) {
                    wave.getEnemies().removeIndex(j);
                }
            }
        }
    }

    private void updateLightning(ModelWAVE wave, ModelPLAYER player) {
        //zap random enemy
        if (!player.isLightningRequested() || wave.getEnemies().size == 0) return;

        int enemyIndex = MathUtils.random(wave.getEnemies().size - 1);
        ModelENEMY enemy = wave.getEnemies().get(enemyIndex);
        Rectangle bounds = enemy.getBounds();
        wave.startLightningEffect(
            bounds.x + bounds.width / 2f,
            bounds.y + bounds.height / 2f,
            LIGHTNING_EFFECT_DURATION
        );

        if (damageEnemy(wave, enemy, player.getLightningDamage(), player.getLightningAttackId(), player)) {
            wave.getEnemies().removeIndex(enemyIndex);
        }
    }

    private void updateLaserBeam(ModelWAVE wave, ModelMAP map, ModelPLAYER player) {
        if (!player.isLaserBeamActive()) return;
        if (player.isLaserBeamLocked()) return;

        Rectangle maxBeamBounds = player.getLaserMaxBeamBounds();
        float hitX = player.getLaserDirection() > 0 ? ModelMAP.WORLD_WIDTH : 0f;
        boolean enemyHit = false;

        for (Rectangle platform : map.getPlatforms()) {
            if (!verticalRangesOverlap(maxBeamBounds, platform)) continue;

            if (player.getLaserDirection() > 0) {
                if (platform.x >= maxBeamBounds.x && platform.x < hitX) {
                    hitX = platform.x;
                    enemyHit = false;
                }
            } else {
                float platformRight = platform.x + platform.width;
                if (platformRight <= maxBeamBounds.x + maxBeamBounds.width && platformRight > hitX) {
                    hitX = platformRight;
                    enemyHit = false;
                }
            }
        }

        for (ModelENEMY enemy : wave.getEnemies()) {
            Rectangle enemyBounds = enemy.getBounds();
            if (!verticalRangesOverlap(maxBeamBounds, enemyBounds)) continue;

            if (player.getLaserDirection() > 0) {
                if (enemyBounds.x >= maxBeamBounds.x && enemyBounds.x < hitX) {
                    hitX = enemyBounds.x;
                    enemyHit = true;
                }
            } else {
                float enemyRight = enemyBounds.x + enemyBounds.width;
                if (enemyRight <= maxBeamBounds.x + maxBeamBounds.width && enemyRight > hitX) {
                    hitX = enemyRight;
                    enemyHit = true;
                }
            }
        }

        player.lockLaserBeamEnd(hitX, enemyHit);
    }

    private boolean isLaserHittingEnemy(ModelPLAYER player, ModelENEMY enemy) {
        Rectangle beamBounds = player.getLaserBeamBounds();
        Rectangle enemyBounds = enemy.getBounds();
        if (beamBounds.overlaps(enemyBounds)) return true;
        if (!verticalRangesOverlap(beamBounds, enemyBounds)) return false;

        float hitX = player.getLaserDirection() > 0 ? beamBounds.x + beamBounds.width : beamBounds.x;
        if (player.getLaserDirection() > 0) {
            return Math.abs(enemyBounds.x - hitX) <= 1f;
        }
        return Math.abs(enemyBounds.x + enemyBounds.width - hitX) <= 1f;
    }

    private boolean verticalRangesOverlap(Rectangle a, Rectangle b) {
        return a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private boolean damageEnemy(ModelWAVE wave, ModelENEMY enemy, float damage, int attackId, ModelPLAYER player) {
        enemy.takeDamage(damage, attackId);
        if (!enemy.isAlive()) {
            spawnScatter(wave, enemy, player);
            return true;
        }
        return false;
    }

    private void spawnScatter(ModelWAVE wave, ModelENEMY enemy, ModelPLAYER player) {
        //death burst passive
        if (!player.hasScatter()) return;

        Rectangle bounds = enemy.getBounds();
        float centerX = bounds.x + bounds.width / 2f;
        float centerY = bounds.y + bounds.height / 2f;
        for (int i = 0; i < SCATTER_PROJECTILE_COUNT; i++) {
            float angle = MathUtils.PI2 * i / SCATTER_PROJECTILE_COUNT;
            wave.getProjectiles().add(new ModelPROJECTILE(
                centerX,
                centerY,
                MathUtils.cos(angle) * SCATTER_PROJECTILE_SPEED,
                MathUtils.sin(angle) * SCATTER_PROJECTILE_SPEED,
                wave.nextEffectAttackId()
            ));
        }
    }

    private void spawnEnemy(ModelWAVE wave, ModelMAP map) {
        //cycles spawn points
        if (map.getEnemySpawnPoints().size == 0) return; 

        Vector2 spawnPoint = map.getEnemySpawnPoints().get(wave.getNextSpawnPoint());
        wave.getEnemies().add(new ModelENEMY(spawnPoint.x, spawnPoint.y, wave.getEnemyHitPoints(), wave.getEnemySpeed()));
        wave.enemySpawned();
        wave.advanceSpawnPoint(map.getEnemySpawnPoints().size);
    }
}
