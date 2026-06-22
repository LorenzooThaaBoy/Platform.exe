package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ControllerENEMY {
    private static final int MAX_ENEMIES = 7;
    private static final float SPAWN_INTERVAL = 2.2f;

    private final Array<ModelENEMY> enemies = new Array<>();
    private float spawnTimer;
    private int nextSpawnPoint;

    public Array<ModelENEMY> getEnemies() {
        return enemies;
    }

    public void update(ModelMAP map, ModelPLAYER player, float delta) {
        spawnTimer -= delta;
        if (spawnTimer <= 0f && enemies.size < MAX_ENEMIES) {
            spawnEnemy(map);
            spawnTimer = SPAWN_INTERVAL;
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            ModelENEMY enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                enemies.removeIndex(i);
                continue;
            }

            enemy.updateTowards(player.getBounds(), delta);

            if (player.isAttacking() && player.getAttackBounds().overlaps(enemy.getBounds())) {
                enemy.kill();
                enemies.removeIndex(i);
                continue;
            }

            if (enemy.getBounds().overlaps(player.getBounds())) {
                player.takeDamage();
            }
        }
    }

    public void reset() {
        enemies.clear();
        spawnTimer = 0f;
        nextSpawnPoint = 0;
    }

    private void spawnEnemy(ModelMAP map) {
        if (map.getEnemySpawnPoints().size == 0) return;

        Vector2 spawnPoint = map.getEnemySpawnPoints().get(nextSpawnPoint);
        enemies.add(new ModelENEMY(spawnPoint.x, spawnPoint.y));
        nextSpawnPoint = (nextSpawnPoint + 1) % map.getEnemySpawnPoints().size;
    }
}
