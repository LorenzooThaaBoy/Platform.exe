package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ModelMAP {
    public static final float WORLD_WIDTH = 640f;
    public static final float WORLD_HEIGHT = 480f;

    private final Array<Rectangle> platforms = new Array<>();
    private final Array<Vector2> enemySpawnPoints = new Array<>();

    public ModelMAP() {
        platforms.add(new Rectangle(0f, 0f, WORLD_WIDTH, 36f));
        platforms.add(new Rectangle(80f, 120f, 150f, 22f));
        platforms.add(new Rectangle(280f, 210f, 140f, 22f));
        platforms.add(new Rectangle(470f, 135f, 120f, 22f));

        enemySpawnPoints.add(new Vector2(-40f, 310f));
        enemySpawnPoints.add(new Vector2(WORLD_WIDTH + 40f, 330f));
        enemySpawnPoints.add(new Vector2(WORLD_WIDTH + 40f, 210f));
    }

    public Array<Rectangle> getPlatforms() {
        return platforms;
    }

    public Array<Vector2> getEnemySpawnPoints() {
        return enemySpawnPoints;
    }
}
