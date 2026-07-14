package com.abyssworld.client.model;

import com.abyssworld.entity.AbyssHoundEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Dedicated quadruped model for the Abyss Hound.
 *
 * <p>The old prototype reused the humanoid Flesh Hunter rig. This rig keeps the jaw, four
 * two-joint legs, shoulder armour, dorsal spines and three tail segments independent so attacks
 * and locomotion have a readable silhouette.</p>
 */
public final class AbyssHoundModel extends EntityModel<AbyssHoundEntity> {
    private final ModelPart hound;
    private final ModelPart torso;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart lowerJaw;
    private final ModelPart frontRight;
    private final ModelPart frontRightLower;
    private final ModelPart frontLeft;
    private final ModelPart frontLeftLower;
    private final ModelPart hindRight;
    private final ModelPart hindRightLower;
    private final ModelPart hindLeft;
    private final ModelPart hindLeftLower;
    private final ModelPart tailBase;
    private final ModelPart tailMiddle;
    private final ModelPart tailTip;

    public AbyssHoundModel(ModelPart root) {
        hound = root.getChild("hound");
        torso = hound.getChild("torso");
        chest = hound.getChild("chest");
        head = hound.getChild("head");
        lowerJaw = head.getChild("lower_jaw");
        frontRight = hound.getChild("front_right_leg");
        frontRightLower = frontRight.getChild("lower_leg");
        frontLeft = hound.getChild("front_left_leg");
        frontLeftLower = frontLeft.getChild("lower_leg");
        hindRight = hound.getChild("hind_right_leg");
        hindRightLower = hindRight.getChild("lower_leg");
        hindLeft = hound.getChild("hind_left_leg");
        hindLeftLower = hindLeft.getChild("lower_leg");
        tailBase = hound.getChild("tail_base");
        tailMiddle = tailBase.getChild("tail_middle");
        tailTip = tailMiddle.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition hound = root.addOrReplaceChild("hound", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition torso = hound.addOrReplaceChild("torso",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-5.0F, -4.0F, -7.5F, 10.0F, 9.0F, 15.0F)
                        .texOffs(0, 57).addBox(-5.5F, -4.5F, -5.5F, 11.0F, 2.0F, 5.0F,
                                new CubeDeformation(0.15F))
                        .texOffs(32, 57).addBox(-5.3F, -4.3F, 1.0F, 10.6F, 2.0F, 5.0F,
                                new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 12.0F, 2.0F));

        torso.addOrReplaceChild("front_spine",
                CubeListBuilder.create().texOffs(66, 104).addBox(-1.5F, -6.0F, -1.0F, 3.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, -4.5F, -0.12F, 0.0F, 0.0F));
        torso.addOrReplaceChild("middle_spine",
                CubeListBuilder.create().texOffs(78, 104).addBox(-1.3F, -5.5F, -1.0F, 2.6F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.05F, 0.0F, 0.0F));
        torso.addOrReplaceChild("rear_spine",
                CubeListBuilder.create().texOffs(88, 104).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -3.2F, 4.5F, 0.22F, 0.0F, 0.0F));

        PartDefinition chest = hound.addOrReplaceChild("chest",
                CubeListBuilder.create()
                        .texOffs(52, 32).addBox(-6.0F, -5.0F, -4.5F, 12.0F, 10.0F, 9.0F)
                        .texOffs(52, 52).addBox(-7.0F, -5.3F, -3.8F, 4.0F, 4.0F, 8.0F,
                                new CubeDeformation(0.2F))
                        .texOffs(78, 52).addBox(3.0F, -5.3F, -3.8F, 4.0F, 4.0F, 8.0F,
                                new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(0.0F, 11.5F, -4.0F, -0.08F, 0.0F, 0.0F));
        chest.addOrReplaceChild("abyss_fissure",
                CubeListBuilder.create().texOffs(224, 8).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 4.0F, 1.0F,
                        new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, 0.0F, -4.45F));

        PartDefinition head = hound.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -4.0F, -5.5F, 9.0F, 7.0F, 8.0F)
                        .texOffs(36, 0).addBox(-3.5F, -1.5F, -10.0F, 7.0F, 4.0F, 5.0F)
                        .texOffs(36, 13).addBox(-4.1F, -3.2F, -7.4F, 8.2F, 2.0F, 5.0F,
                                new CubeDeformation(0.12F)),
                PartPose.offsetAndRotation(0.0F, 10.5F, -7.0F, 0.04F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(224, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F,
                        new CubeDeformation(0.05F)),
                PartPose.offset(2.7F, -1.0F, -5.6F));
        head.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(224, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F,
                        new CubeDeformation(0.05F)),
                PartPose.offset(-2.7F, -1.0F, -5.6F));
        head.addOrReplaceChild("left_horn",
                CubeListBuilder.create().texOffs(96, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(3.2F, -3.2F, -1.5F, 0.34F, 0.18F, -0.12F));
        head.addOrReplaceChild("right_horn",
                CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(-3.2F, -3.2F, -1.5F, 0.34F, -0.18F, 0.12F));
        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(96, 14).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(3.5F, -3.0F, 0.5F, 0.16F, 0.0F, 0.2F));
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(96, 14).mirror().addBox(-1.0F, -4.0F, -0.5F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(-3.5F, -3.0F, 0.5F, 0.16F, 0.0F, -0.2F));
        head.addOrReplaceChild("lower_jaw",
                CubeListBuilder.create()
                        .texOffs(68, 0).addBox(-3.5F, -0.5F, -5.5F, 7.0F, 2.0F, 6.0F)
                        .texOffs(68, 10).addBox(-3.0F, -1.5F, -5.0F, 1.0F, 2.0F, 1.0F)
                        .texOffs(76, 10).addBox(2.0F, -1.5F, -5.0F, 1.0F, 2.0F, 1.0F),
                PartPose.offset(0.0F, 2.3F, -4.8F));

        addFrontLeg(hound, "front_right_leg", -5.0F, false);
        addFrontLeg(hound, "front_left_leg", 5.0F, true);
        addHindLeg(hound, "hind_right_leg", -4.3F, false);
        addHindLeg(hound, "hind_left_leg", 4.3F, true);

        PartDefinition tailBase = hound.addOrReplaceChild("tail_base",
                CubeListBuilder.create().texOffs(0, 104).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 11.0F, 9.0F, -0.2F, 0.0F, 0.0F));
        tailBase.addOrReplaceChild("tail_fissure",
                CubeListBuilder.create().texOffs(224, 20).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F,
                        new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, -1.7F, 2.0F));
        PartDefinition tailMiddle = tailBase.addOrReplaceChild("tail_middle",
                CubeListBuilder.create().texOffs(24, 104).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, -0.12F, 0.0F, 0.0F));
        tailMiddle.addOrReplaceChild("tail_tip",
                CubeListBuilder.create()
                        .texOffs(46, 104).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 7.0F)
                        .texOffs(98, 104).addBox(-1.5F, -1.5F, 5.5F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, -0.08F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 256, 128);
    }

    private static void addFrontLeg(PartDefinition hound, String name, float x, boolean mirror) {
        CubeListBuilder upper = CubeListBuilder.create().texOffs(0, 86);
        CubeListBuilder lower = CubeListBuilder.create().texOffs(20, 86);
        CubeListBuilder paw = CubeListBuilder.create().texOffs(34, 86);
        if (mirror) {
            upper.mirror();
            lower.mirror();
            paw.mirror();
        }
        PartDefinition leg = hound.addOrReplaceChild(name,
                upper.addBox(-2.0F, -1.0F, -2.5F, 4.0F, 7.0F, 5.0F),
                PartPose.offsetAndRotation(x, 14.0F, -5.0F, 0.12F, 0.0F, 0.0F));
        PartDefinition shin = leg.addOrReplaceChild("lower_leg",
                lower.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, -0.12F, 0.0F, 0.0F));
        shin.addOrReplaceChild("paw",
                paw.addBox(-2.5F, -1.0F, -5.0F, 5.0F, 3.0F, 7.0F),
                PartPose.offset(0.0F, 6.0F, -0.5F));
    }

    private static void addHindLeg(PartDefinition hound, String name, float x, boolean mirror) {
        CubeListBuilder upper = CubeListBuilder.create().texOffs(60, 86);
        CubeListBuilder lower = CubeListBuilder.create().texOffs(84, 86);
        CubeListBuilder paw = CubeListBuilder.create().texOffs(98, 86);
        if (mirror) {
            upper.mirror();
            lower.mirror();
            paw.mirror();
        }
        PartDefinition leg = hound.addOrReplaceChild(name,
                upper.addBox(-2.5F, -2.0F, -3.0F, 5.0F, 7.0F, 6.0F),
                PartPose.offsetAndRotation(x, 14.0F, 7.0F, -0.42F, 0.0F, 0.0F));
        PartDefinition hock = leg.addOrReplaceChild("lower_leg",
                lower.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 1.5F, 0.72F, 0.0F, 0.0F));
        hock.addOrReplaceChild("paw",
                paw.addBox(-2.5F, -1.0F, -5.0F, 5.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 6.0F, -0.5F, -0.3F, 0.0F, 0.0F));
    }

    @Override
    public void setupAnim(AbyssHoundEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        hound.getAllParts().forEach(ModelPart::resetPose);

        float stride = Mth.clamp(limbSwingAmount * 1.35F, 0.0F, 1.0F);
        float cycle = limbSwing * 0.92F;
        float rightStep = Mth.cos(cycle) * stride;
        float leftStep = Mth.cos(cycle + Mth.PI) * stride;
        float breath = Mth.sin(ageInTicks * 0.11F);
        float tailWave = Mth.sin(ageInTicks * 0.16F);
        float alert = entity.isAggressive() ? 1.0F : 0.0F;

        torso.y += breath * 0.12F;
        torso.xRot = 0.03F + Mth.abs(Mth.cos(cycle)) * stride * 0.05F;
        chest.y += breath * 0.16F + alert * 0.3F;
        chest.xRot = -0.08F - alert * 0.08F;

        head.yRot = Mth.clamp(netHeadYaw * Mth.DEG_TO_RAD, -0.9F, 0.9F);
        head.xRot = 0.04F + headPitch * Mth.DEG_TO_RAD * 0.65F - alert * 0.13F;
        head.zRot = Mth.sin(ageInTicks * 0.055F) * 0.018F;

        frontRight.xRot = 0.12F + rightStep * 0.95F;
        frontLeft.xRot = 0.12F + leftStep * 0.95F;
        hindRight.xRot = -0.42F + leftStep * 0.82F;
        hindLeft.xRot = -0.42F + rightStep * 0.82F;
        frontRightLower.xRot = -0.12F + Math.max(0.0F, -rightStep) * 0.7F;
        frontLeftLower.xRot = -0.12F + Math.max(0.0F, -leftStep) * 0.7F;
        hindRightLower.xRot = 0.72F + Math.max(0.0F, rightStep) * 0.65F;
        hindLeftLower.xRot = 0.72F + Math.max(0.0F, leftStep) * 0.65F;

        tailBase.xRot = -0.2F + stride * 0.18F;
        tailBase.yRot = tailWave * 0.28F + rightStep * 0.1F;
        tailMiddle.xRot = -0.12F + Mth.cos(ageInTicks * 0.13F) * 0.08F;
        tailMiddle.yRot = Mth.sin(ageInTicks * 0.16F - 0.8F) * 0.34F;
        tailTip.xRot = -0.08F + Mth.sin(ageInTicks * 0.14F) * 0.1F;
        tailTip.yRot = Mth.sin(ageInTicks * 0.16F - 1.6F) * 0.42F;

        float bite = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
        lowerJaw.xRot = 0.06F + alert * (0.05F + (breath + 1.0F) * 0.025F) + bite * 0.72F;
        if (bite > 0.0F) {
            torso.xRot -= bite * 0.22F;
            chest.xRot -= bite * 0.3F;
            chest.z -= bite * 1.2F;
            head.xRot -= bite * 0.32F;
            head.z -= bite * 2.3F;
            frontRight.xRot = -0.72F + bite * 0.28F;
            frontLeft.xRot = -0.58F + bite * 0.18F;
        }

        if (!entity.onGround()) {
            float vertical = (float) Mth.clamp(entity.getDeltaMovement().y * 3.0D, -1.0D, 1.0D);
            torso.xRot = -0.2F - vertical * 0.18F;
            chest.xRot = -0.28F - vertical * 0.12F;
            frontRight.xRot = -0.82F;
            frontLeft.xRot = -0.82F;
            frontRightLower.xRot = 0.4F;
            frontLeftLower.xRot = 0.4F;
            hindRight.xRot = 0.25F;
            hindLeft.xRot = 0.25F;
            hindRightLower.xRot = 1.05F;
            hindLeftLower.xRot = 1.05F;
            tailBase.xRot = 0.12F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        hound.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
