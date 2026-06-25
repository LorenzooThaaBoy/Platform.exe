package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ModelPLAYER {
    public static final float WIDTH = 32f;
    public static final float HEIGHT = 46f;
    public static final float MOVE_SPEED = 210f;
    public static final float JUMP_SPEED = 420f;
    public static final float GRAVITY = -980f;
    private static final float ATTACK_DURATION = 0.18f;
    private static final float HURT_DURATION = 0.75f;

    private final Rectangle bounds = new Rectangle(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
    private final Vector2 velocity = new Vector2();
    private int lives = 3;
    private int facing = 1;
    private boolean grounded;
    private float attackTimer;
    private float hurtTimer;
    private int attackId;

    public Rectangle getBounds() { 
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getLives() {
        return lives;
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

    public void setFacingFromMovement(float movement) {
        if (movement > 0f) facing = 1;
        if (movement < 0f) facing = -1;
    }

    public void startAttack() {
        if (attackTimer <= 0f) {
            attackTimer = ATTACK_DURATION;
            attackId++;
        }
    }

    public int getAttackId() {
        return attackId;
    }

    public int getSwordDamage() {
        return 1;
    }

    public void takeDamage() {
        if (hurtTimer > 0f || lives <= 0) return;
        lives--;
        hurtTimer = HURT_DURATION;
    }

    public void updateTimers(float delta) {
        attackTimer = Math.max(0f, attackTimer - delta);
        hurtTimer = Math.max(0f, hurtTimer - delta);
    }

    public Rectangle getAttackBounds() {
        float attackWidth = 38f;
        float attackHeight = 28f;
        float attackX = facing > 0 ? bounds.x + bounds.width : bounds.x - attackWidth;
        float attackY = bounds.y + 10f;
        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    public void reset() {
        bounds.set(80f, ModelMAP.GROUND_Y, WIDTH, HEIGHT);
        velocity.setZero();
        lives = 3;
        facing = 1;
        grounded = false;
        attackTimer = 0f;
        hurtTimer = 0f;
        attackId = 0;
    }

    //Item müssen auch noch implementiert werden: 
    // 
    //TODO: DMG up, Range up, HP up, Speed up, Dash!
    //...
    //TODO: komliziertere Items:
//Blitz: bei Gegner Treffer schießt ein Blitz auf zufälligen Gegner der dan dmg bekommt
//Scatter: wenn Angriff Gegner trifft, werden bei dabei viele Projektile rausgeschossen(pumpgun mäßig), welche anderen Gegner schaden kann
//Bumerang: man schlägt nicht nur sondern wirft einen Bumerang stattdessen der zurückprallt (oder falls das nicht geht durch Gegner durchgeht) und wieder gefangen wird; somit muss der Bumerang erst gefangen werden bevor er wieder geworfen werden kann
//Feuerstrahl (brimstone) jeder Angriff dauert länger, aber ein Laser (viel dmg) wird geschossen, der alles durchdringt 
//"Ewiges Katana": desto länger man nicht angegriffen hat, desto mehr dmg macht man und desto mehr Range hat der Angriff
//Zauberhut: man steuert mit den pfeiltasten nun eine magiekugel die Schaden macht  und man kann nicht mehr schlagen, magiekugel hat eine dmg Aura, somit muss man sie zu den Gegnern hinbewegen um diese zu dmgen
//Garlic: kleine dmg aura welche Gegner in zeitintervallen dmg machen (ca alle 5 sec)

//Wie man Wellen schwerer macht: Gegner mehr hp geben (insgesamt hp geben), und das skalieren lassen
//Mehrmals pro Welle Gegner spawnen lassen 
}
