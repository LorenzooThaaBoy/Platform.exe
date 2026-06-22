package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ModelMAP { //Map parameters
    public static final float WORLD_WIDTH = 640f;
    public static final float WORLD_HEIGHT = 480f;
    public static final float GROUND_Y = 60f;

    private final Array<Rectangle> platforms = new Array<>();
    private final Array<Vector2> enemySpawnPoints = new Array<>();

    public ModelMAP() { //TODO: make things match better + total 5 platforms, more height 
        platforms.add(new Rectangle(80f, 114f, 150f, 22f));
        platforms.add(new Rectangle(280f, 180f, 160f, 22));
        platforms.add(new Rectangle(490f, 114f, 130f, 22));

        enemySpawnPoints.add(new Vector2(-40f, 310f)); //TODO: Add more for further progression 
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
