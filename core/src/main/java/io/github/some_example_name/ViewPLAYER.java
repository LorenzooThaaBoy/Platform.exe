package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.SlotData;
import com.esotericsoftware.spine.attachments.RegionAttachment;

public class ViewPLAYER {
    private final Texture blockTexture;
    private final TextureRegion blockRegion;
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
        blockTexture = createWhitePixelTexture();
        blockRegion = new TextureRegion(blockTexture);
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
    }

    public void dispose() {
        blockTexture.dispose();
    }

    private void poseSkeleton(ModelPLAYER player) { // spine animation skeleton factors
        skeleton.setBonesToSetupPose();

        float centerX = player.getBounds().x + player.getBounds().width / 2f;
        float floorY = player.getBounds().y + 2f;
        skeleton.setPosition(centerX, floorY);
        skeleton.setScale(player.getFacing(), 1f);
        skeleton.setColor(player.isHurt() ? 1f : 0.95f, player.isHurt() ? 0.5f : 1f, player.isHurt() ? 0.5f : 1f, 1f);

        float movementSpeed = Math.abs(player.getVelocity().x);
        boolean walking = player.isGrounded() && movementSpeed > 1f;
        boolean risingOrFalling = !player.isGrounded();

        float walkCycle = MathUtils.sin(animationTime * 10f);
        float walkAmount = walking ? 1f : 0f;
        float idleBob = MathUtils.sin(animationTime * 3f) * 1.2f;

        root.setY(walking ? Math.abs(walkCycle) * 1.2f : idleBob);
        torso.setRotation(walking ? walkCycle * 3f : MathUtils.sin(animationTime * 2f) * 1.5f);
        head.setRotation(-torso.getRotation() * 0.5f);

        backUpperLeg.setRotation(-walkCycle * 18f * walkAmount);
        backLowerLeg.setRotation(Math.max(0f, walkCycle) * 22f * walkAmount);
        frontUpperLeg.setRotation(walkCycle * 18f * walkAmount);
        frontLowerLeg.setRotation(Math.max(0f, -walkCycle) * 22f * walkAmount);

        backUpperArm.setRotation(walkCycle * 14f * walkAmount - 10f);
        backForearm.setRotation(-10f);
        frontUpperArm.setRotation(-walkCycle * 14f * walkAmount - 8f);
        frontForearm.setRotation(-12f);
        sword.setRotation(0f);

        if (risingOrFalling) {
            torso.setRotation(player.getVelocity().y > 0f ? -6f : 6f);
            backUpperLeg.setRotation(-12f);
            backLowerLeg.setRotation(20f);
            frontUpperLeg.setRotation(14f);
            frontLowerLeg.setRotation(10f);
        }

        if (player.isAttacking()) {
            float attackPhase = MathUtils.clamp(attackAnimationTime / 0.18f, 0f, 1f);
            float swing = MathUtils.sin(attackPhase * MathUtils.PI);
            frontUpperArm.setRotation(-55f + swing * 90f);
            frontForearm.setRotation(-35f + swing * 60f);
            sword.setRotation(-25f + swing * 85f);
            torso.setRotation(torso.getRotation() - 4f + swing * 8f);
        }

        skeleton.updateWorldTransform(Skeleton.Physics.update);
    }

    private SkeletonData createPrototypeSkeletonData() {
        SkeletonData data = new SkeletonData();
        data.setName("prototype-block-player");

        BoneData rootData = addBone(data, 0, "root", null, 0f, 0f);
        BoneData torsoData = addBone(data, 1, "torso", rootData, 0f, 24f);
        BoneData headData = addBone(data, 2, "head", torsoData, 0f, 24f);

        BoneData backUpperArmData = addBone(data, 3, "back-upper-arm", torsoData, -11f, 17f);
        BoneData backForearmData = addBone(data, 4, "back-forearm", backUpperArmData, 15f, 0f);
        BoneData frontUpperArmData = addBone(data, 5, "front-upper-arm", torsoData, 11f, 17f);
        BoneData frontForearmData = addBone(data, 6, "front-forearm", frontUpperArmData, 15f, 0f);
        BoneData swordData = addBone(data, 7, "sword", frontForearmData, 15f, 0f);

        BoneData backUpperLegData = addBone(data, 8, "back-upper-leg", rootData, -6f, 22f);
        BoneData backLowerLegData = addBone(data, 9, "back-lower-leg", backUpperLegData, 0f, -19f);
        BoneData frontUpperLegData = addBone(data, 10, "front-upper-leg", rootData, 6f, 22f);
        BoneData frontLowerLegData = addBone(data, 11, "front-lower-leg", frontUpperLegData, 0f, -19f);

        Skin skin = new Skin("default");
        addBlockSlot(data, skin, 0, "back-upper-arm", backUpperArmData, 7f, 0f, 15f, 5f, 0.14f, 0.37f, 0.78f, 1f);
        addBlockSlot(data, skin, 1, "back-forearm", backForearmData, 7f, 0f, 15f, 5f, 0.12f, 0.32f, 0.68f, 1f);
        addBlockSlot(data, skin, 2, "back-upper-leg", backUpperLegData, 0f, -9f, 7f, 20f, 0.11f, 0.28f, 0.58f, 1f);
        addBlockSlot(data, skin, 3, "back-lower-leg", backLowerLegData, 0f, -9f, 6f, 19f, 0.09f, 0.23f, 0.47f, 1f);
        addBlockSlot(data, skin, 4, "torso", torsoData, 0f, 0f, 22f, 34f, 0.24f, 0.58f, 1f, 1f);
        addBlockSlot(data, skin, 5, "head", headData, 0f, 8f, 16f, 16f, 0.95f, 0.74f, 0.52f, 1f);
        addBlockSlot(data, skin, 6, "front-upper-leg", frontUpperLegData, 0f, -9f, 7f, 20f, 0.14f, 0.35f, 0.72f, 1f);
        addBlockSlot(data, skin, 7, "front-lower-leg", frontLowerLegData, 0f, -9f, 6f, 19f, 0.12f, 0.30f, 0.62f, 1f);
        addBlockSlot(data, skin, 8, "front-upper-arm", frontUpperArmData, 7f, 0f, 15f, 5f, 0.22f, 0.52f, 0.95f, 1f);
        addBlockSlot(data, skin, 9, "front-forearm", frontForearmData, 7f, 0f, 15f, 5f, 0.18f, 0.44f, 0.84f, 1f);
        addBlockSlot(data, skin, 10, "sword", swordData, 17f, 0f, 34f, 4f, 0.95f, 0.88f, 0.52f, 1f);

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

    private void addBlockSlot(
        SkeletonData data,
        Skin skin,
        int index,
        String name,
        BoneData bone,
        float x,
        float y,
        float width,
        float height,
        float r,
        float g,
        float b,
        float a
    ) {
        SlotData slot = new SlotData(index, name, bone);
        slot.setAttachmentName(name);
        data.getSlots().add(slot);

        RegionAttachment attachment = new RegionAttachment(name);
        attachment.setRegion(blockRegion);
        attachment.setX(x);
        attachment.setY(y);
        attachment.setWidth(width);
        attachment.setHeight(height);
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
