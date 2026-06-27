package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.SlotData;
import com.esotericsoftware.spine.attachments.RegionAttachment;

//TODO: tune skeleton sprite positions if sprites change
public class ViewPLAYER {
    //body part sizes and rotations
    private static final int SPRITE_FACING_OFFSET = -1;
    private static final float TORSO_LENGTH = 34f;
    private static final float HEAD_LENGTH = 16f;
    private static final float SHOULDER_LENGTH = 15f;
    private static final float SHOULDER_SCALE_X = 1.5f;
    private static final float SHOULDER_SCALE_Y = 1.5f;
    private static final float UPPER_ARM_LENGTH = 20f;
    private static final float LOWER_ARM_LENGTH = 25f;
    private static final float ARM_FORWARD = -1f;
    private static final float HAND_JOINT_DISTANCE = 16f;
    private static final float UPPER_LEG_LENGTH = 20f;
    private static final float LOWER_LEG_LENGTH = 19f;
    private static final float SWORD_LENGTH = 34f;
    private static final float TORSO_ROTATION = 0f;
    private static final float HEAD_ROTATION = 0f;
    private static final float SHOULDER_ROTATION = -90f;
    private static final float LOWER_ARM_ROTATION = 0f;
    private static final float UPPER_LEG_ROTATION = 0f;
    private static final float LOWER_LEG_ROTATION = 0f;
    private static final float SWORD_ROTATION = 45f;
    private static final float ATTACK_ANIMATION_DURATION = 0.32f;
    private static final float FRONT_UPPER_ARM_READY_ROTATION = 40f;
    private static final float FRONT_FOREARM_READY_ROTATION = 0f;
    private static final float SWORD_READY_ROTATION = 38f;
    private static final float UPPER_ARM_BAR_THICKNESS = 3f;

    private final Texture blockTexture;
    private final TextureRegion blockRegion;
    private final Texture headTexture;
    private final Texture torsoTexture;
    private final Texture shoulderTexture;
    private final Texture lowerArmTexture;
    private final Texture upperLegTexture;
    private final Texture lowerLegTexture;
    private final Texture swordTexture;
    private final TextureRegion headRegion;
    private final TextureRegion torsoRegion;
    private final TextureRegion shoulderRegion;
    private final TextureRegion lowerArmRegion;
    private final TextureRegion upperLegRegion;
    private final TextureRegion lowerLegRegion;
    private final TextureRegion swordRegion;
    private final SkeletonRenderer skeletonRenderer;
    private final Skeleton skeleton;

    private final Bone root;
    private final Bone torso;
    private final Bone head;
    private final Bone backUpperArm;
    private final Bone backForearm;
    private final Bone frontUpperArm;
    private final Bone frontForearm;
    private final Bone sword;
    private final Bone backUpperLeg;
    private final Bone backLowerLeg;
    private final Bone frontUpperLeg;
    private final Bone frontLowerLeg;

    private float animationTime;
    private float attackAnimationTime;

    public ViewPLAYER() {
        //load player sprite parts
        blockTexture = createWhitePixelTexture();
        blockRegion = new TextureRegion(blockTexture);
        headTexture = new Texture("Head.PNG");
        torsoTexture = new Texture("Torso.PNG");
        shoulderTexture = new Texture("Leftshoulder.PNG");
        lowerArmTexture = new Texture("Lower Arm.PNG");
        upperLegTexture = new Texture("UpperLeg.PNG");
        lowerLegTexture = new Texture("LowerLeg.PNG");
        swordTexture = new Texture("Sword.PNG");
        headRegion = new TextureRegion(headTexture);
        torsoRegion = new TextureRegion(torsoTexture);
        shoulderRegion = new TextureRegion(shoulderTexture);
        lowerArmRegion = new TextureRegion(lowerArmTexture);
        upperLegRegion = new TextureRegion(upperLegTexture);
        lowerLegRegion = new TextureRegion(lowerLegTexture);
        swordRegion = new TextureRegion(swordTexture);
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(false);

        skeleton = new Skeleton(createPrototypeSkeletonData());
        skeleton.setSkin("default");
        skeleton.setSlotsToSetupPose();

        root = skeleton.findBone("root");
        torso = skeleton.findBone("torso");
        head = skeleton.findBone("head");
        backUpperArm = skeleton.findBone("back-upper-arm");
        backForearm = skeleton.findBone("back-forearm");
        frontUpperArm = skeleton.findBone("front-upper-arm");
        frontForearm = skeleton.findBone("front-forearm");
        sword = skeleton.findBone("sword");
        backUpperLeg = skeleton.findBone("back-upper-leg");
        backLowerLeg = skeleton.findBone("back-lower-leg");
        frontUpperLeg = skeleton.findBone("front-upper-leg");
        frontLowerLeg = skeleton.findBone("front-lower-leg");
    }

    public void render(SpriteBatch batch, ModelPLAYER player, float delta) {
        //animation timers only
        animationTime += delta;
        if (player.isAttacking()) {
            attackAnimationTime += delta;
        } else {
            attackAnimationTime = 0f;
        }

        poseSkeleton(player);
        skeletonRenderer.draw(batch, skeleton);
    }

    public void renderHitboxes(ShapeRenderer shapes, ModelPLAYER player) {
        //player + attack boxes
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

        if (player.isBrimstoneBeamActive()) {
            Rectangle brimstoneBeamBounds = player.getBrimstoneBeamBounds();
            shapes.setColor(new Color(1f, 0.28f, 0.05f, 0.65f));
            shapes.rect(
                brimstoneBeamBounds.x,
                brimstoneBeamBounds.y,
                brimstoneBeamBounds.width,
                brimstoneBeamBounds.height
            );
        }
    }

    public void dispose() {
        blockTexture.dispose();
        headTexture.dispose();
        torsoTexture.dispose();
        shoulderTexture.dispose();
        lowerArmTexture.dispose();
        upperLegTexture.dispose();
        lowerLegTexture.dispose();
        swordTexture.dispose();
    }

    private void poseSkeleton(ModelPLAYER player) {
        //reset pose then move bones
        skeleton.setBonesToSetupPose();

        float centerX = player.getBounds().x + player.getBounds().width / 2f;
        float floorY = player.getBounds().y + 2f;
        skeleton.setPosition(centerX, floorY);
        skeleton.setScale(player.getFacing() * SPRITE_FACING_OFFSET, 1f);
        skeleton.setColor(player.isHurt() ? 1f : 0.95f, player.isHurt() ? 0.5f : 1f, player.isHurt() ? 0.5f : 1f, 1f);

        float movementSpeed = Math.abs(player.getVelocity().x);
        boolean walking = player.isGrounded() && movementSpeed > 1f;
        boolean risingOrFalling = !player.isGrounded();

        float walkCycle = MathUtils.sin(animationTime * 10f);
        float walkAmount = walking ? 1f : 0f;
        float idleBob = MathUtils.sin(animationTime * 3f) * 1.2f;

        //idle/walk animation
        root.setY(walking ? Math.abs(walkCycle) * 1.2f : idleBob);
        torso.setRotation(walking ? walkCycle * 3f : MathUtils.sin(animationTime * 2f) * 1.5f);
        head.setRotation(-torso.getRotation() * 0.5f);

        backUpperLeg.setRotation(-walkCycle * 18f * walkAmount);
        backLowerLeg.setRotation(Math.max(0f, walkCycle) * 22f * walkAmount);
        frontUpperLeg.setRotation(walkCycle * 18f * walkAmount);
        frontLowerLeg.setRotation(Math.max(0f, -walkCycle) * 22f * walkAmount);

        backUpperArm.setRotation(walkCycle * 14f * walkAmount + 8f);
        backForearm.setRotation(10f);
        frontUpperArm.setRotation(FRONT_UPPER_ARM_READY_ROTATION - walkCycle * 6f * walkAmount);
        frontForearm.setRotation(FRONT_FOREARM_READY_ROTATION);
        sword.setRotation(SWORD_READY_ROTATION);

        if (risingOrFalling) {
            //jump/fall pose
            torso.setRotation(player.getVelocity().y > 0f ? -6f : 6f);
            backUpperLeg.setRotation(-12f);
            backLowerLeg.setRotation(20f);
            frontUpperLeg.setRotation(14f);
            frontLowerLeg.setRotation(10f);
        }

        if (player.isAttacking()) {
            //sword swing pose
            float attackPhase = MathUtils.clamp(attackAnimationTime / ATTACK_ANIMATION_DURATION, 0f, 1f);
            float swing = MathUtils.sin(attackPhase * MathUtils.PI * 0.5f);
            if (player.getAttackDirectionY() > 0) {
                frontUpperArm.setRotation(-45f + swing * 5f);
                frontForearm.setRotation(-125f - swing * 30f);
                sword.setRotation(-45f);
                torso.setRotation(torso.getRotation() - 5f);
            } else {
                frontUpperArm.setRotation(FRONT_UPPER_ARM_READY_ROTATION + swing * 46f);
                frontForearm.setRotation(FRONT_FOREARM_READY_ROTATION + swing * 50f);
                sword.setRotation(SWORD_READY_ROTATION + swing * 45f);
                torso.setRotation(torso.getRotation() - 4f + swing * 8f);
            }
        }

        skeleton.updateWorldTransform(Skeleton.Physics.update);
    }

    private SkeletonData createPrototypeSkeletonData() {
        //spine-ish skeleton made in code
        SkeletonData data = new SkeletonData();  
        data.setName("prototype-block-player");

        BoneData rootData = addBone(data, 0, "root", null, 0f, 0f);
        BoneData torsoData = addBone(data, 1, "torso", rootData, 0f, 24f);
        BoneData headData = addBone(data, 2, "head", torsoData, 0f, 24f);

        BoneData backUpperArmData = addBone(data, 3, "back-upper-arm", torsoData, 11f, 17f);
        BoneData backForearmData = addBone(data, 4, "back-forearm", backUpperArmData, ARM_FORWARD * UPPER_ARM_LENGTH, 0f);
        BoneData frontUpperArmData = addBone(data, 5, "front-upper-arm", torsoData, 0f, 17f);
        BoneData frontForearmData = addBone(data, 6, "front-forearm", frontUpperArmData, ARM_FORWARD * UPPER_ARM_LENGTH, 0f);
        BoneData swordData = addBone(data, 7, "sword", frontForearmData, ARM_FORWARD * HAND_JOINT_DISTANCE, 0f);

        BoneData backUpperLegData = addBone(data, 8, "back-upper-leg", rootData, -6f, 22f);
        BoneData backLowerLegData = addBone(data, 9, "back-lower-leg", backUpperLegData, 0f, -19f);
        BoneData frontUpperLegData = addBone(data, 10, "front-upper-leg", rootData, 6f, 22f);
        BoneData frontLowerLegData = addBone(data, 11, "front-lower-leg", frontUpperLegData, 0f, -19f);

        //attachments = actual sprite pieces
        Skin skin = new Skin("default");
        addUpperArmBar(data, skin, 0, "back-upper-arm", backUpperArmData, UPPER_ARM_LENGTH);
        addFittedSlot(data, skin, 1, "back-forearm", backForearmData, lowerArmRegion, -4f, -1f, LOWER_ARM_LENGTH, LOWER_ARM_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 2, "back-shoulder", backUpperArmData, shoulderRegion, -12f, 2f, SHOULDER_LENGTH, SHOULDER_ROTATION, SHOULDER_SCALE_X, SHOULDER_SCALE_Y, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 3, "back-upper-leg", backUpperLegData, upperLegRegion, 0f, -8f, UPPER_LEG_LENGTH, UPPER_LEG_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 4, "back-lower-leg", backLowerLegData, lowerLegRegion, 0f, -1f, LOWER_LEG_LENGTH, LOWER_LEG_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 5, "torso", torsoData, torsoRegion, -2f, 10f, TORSO_LENGTH, TORSO_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 6, "head", headData, headRegion, -4f, 4f, HEAD_LENGTH, HEAD_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 7, "front-upper-leg", frontUpperLegData, upperLegRegion, -5f, -8f, UPPER_LEG_LENGTH, UPPER_LEG_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 8, "front-lower-leg", frontLowerLegData, lowerLegRegion, -4f,-1f, LOWER_LEG_LENGTH, LOWER_LEG_ROTATION, 1f, 1f, 1f, 1f);
        addUpperArmBar(data, skin, 9, "front-upper-arm", frontUpperArmData, UPPER_ARM_LENGTH);
        addFittedSlot(data, skin, 10, "front-forearm", frontForearmData, lowerArmRegion, -4f, 4f, LOWER_ARM_LENGTH, LOWER_ARM_ROTATION, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 11, "front-shoulder", frontUpperArmData, shoulderRegion, -1f, 0f, SHOULDER_LENGTH, SHOULDER_ROTATION, SHOULDER_SCALE_X, SHOULDER_SCALE_Y, 1f, 1f, 1f, 1f);
        addFittedSlot(data, skin, 12, "sword", swordData, swordRegion, 18f, -2f, SWORD_LENGTH+5, SWORD_ROTATION+225, 1f, 1f, 1f, 1f);

        data.getSkins().add(skin);
        data.setDefaultSkin(skin);
        return data;
    }

    private BoneData addBone(SkeletonData data, int index, String name, BoneData parent, float x, float y) {
        BoneData bone = new BoneData(index, name, parent);
        bone.setPosition(x, y);
        data.getBones().add(bone);
        return bone;
    }

    private void addUpperArmBar(SkeletonData data, Skin skin, int index, String name, BoneData bone, float length) {
        addBlockSlot(
            data,
            skin,
            index,
            name,
            bone,
            blockRegion,
            -length / 2f,
            0f,
            length,
            UPPER_ARM_BAR_THICKNESS,
            0f,
            1f,
            1f,
            1f,
            0f,
            0f,
            0.45f
        );
    }

    private void addFittedSlot(
        SkeletonData data,
        Skin skin,
        int index,
        String name,
        BoneData bone,
        TextureRegion region,
        float x,
        float y,
        float targetLength,
        float rotation,
        float r,
        float g,
        float b,
        float a
    ) {
        addFittedSlot(data, skin, index, name, bone, region, x, y, targetLength, rotation, 1f, 1f, r, g, b, a);
    }

    private void addFittedSlot(
        SkeletonData data,
        Skin skin,
        int index,
        String name,
        BoneData bone,
        TextureRegion region,
        float x,
        float y,
        float targetLength,
        float rotation,
        float scaleX,
        float scaleY,
        float r,
        float g,
        float b,
        float a
    ) {
        //keeps sprite ratio, targetLength = height
        float aspectRatio = (float)region.getRegionWidth() / region.getRegionHeight();
        addBlockSlot(data, skin, index, name, bone, region, x, y, targetLength * aspectRatio, targetLength, rotation, scaleX, scaleY, r, g, b, a);
    }

    private void addBlockSlot(
        SkeletonData data,
        Skin skin,
        int index,
        String name,
        BoneData bone,
        TextureRegion region,
        float x,
        float y,
        float width,
        float height,
        float rotation,
        float scaleX,
        float scaleY,
        float r,
        float g,
        float b,
        float a
    ) {
        SlotData slot = new SlotData(index, name, bone);
        slot.setAttachmentName(name);
        data.getSlots().add(slot);

        RegionAttachment attachment = new RegionAttachment(name);
        attachment.setRegion(region);
        attachment.setX(x);
        attachment.setY(y);
        attachment.setWidth(width);
        attachment.setHeight(height);
        attachment.setRotation(rotation);
        attachment.setScaleX(scaleX);
        attachment.setScaleY(scaleY);
        attachment.getColor().set(r, g, b, a);
        attachment.updateRegion();
        skin.setAttachment(index, name, attachment);
    }

    private Texture createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
