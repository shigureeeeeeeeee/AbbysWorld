package com.abyssworld.client.model;

import com.abyssworld.entity.BoundaryWatcherEntity;
import com.abyssworld.entity.CrystalParasiteEntity;
import com.abyssworld.entity.FallenResearcherEntity;
import com.abyssworld.entity.ManaLeechEntity;
import com.abyssworld.entity.ShadowWalkerEntity;
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
import net.minecraft.world.entity.monster.Monster;

/** Dedicated production rigs for the five non-hound Overworld abyss mobs. */
public final class AdvancedOverworldModels {
    private AdvancedOverworldModels() {
    }

    public abstract static class AdvancedModel<T extends Monster> extends EntityModel<T> {
        protected final ModelPart rig;

        protected AdvancedModel(ModelPart root, String rigName) {
            rig = root.getChild(rigName);
        }

        protected void reset() {
            rig.getAllParts().forEach(ModelPart::resetPose);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                   int packedOverlay, float red, float green, float blue, float alpha) {
            rig.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public static final class ShadowWalkerModel extends AdvancedModel<ShadowWalkerEntity> {
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart rightArm;
        private final ModelPart rightForearm;
        private final ModelPart leftArm;
        private final ModelPart leftForearm;
        private final ModelPart rightAux;
        private final ModelPart leftAux;
        private final ModelPart rightShard;
        private final ModelPart leftShard;
        private final ModelPart frontMantle;
        private final ModelPart backMantle;

        public ShadowWalkerModel(ModelPart root) {
            super(root, "walker");
            body = rig.getChild("body");
            head = rig.getChild("head");
            rightArm = rig.getChild("right_arm");
            rightForearm = rightArm.getChild("forearm");
            leftArm = rig.getChild("left_arm");
            leftForearm = leftArm.getChild("forearm");
            rightAux = rig.getChild("right_aux_arm");
            leftAux = rig.getChild("left_aux_arm");
            rightShard = rig.getChild("right_leg_shard");
            leftShard = rig.getChild("left_leg_shard");
            frontMantle = rig.getChild("front_mantle");
            backMantle = rig.getChild("back_mantle");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition rig = mesh.getRoot().addOrReplaceChild("walker", CubeListBuilder.create(), PartPose.ZERO);
            PartDefinition body = rig.addOrReplaceChild("body",
                    CubeListBuilder.create()
                            .texOffs(0, 30).addBox(-4.0F, -7.0F, -2.5F, 8.0F, 16.0F, 5.0F)
                            .texOffs(28, 30).addBox(-5.0F, -6.0F, -3.0F, 10.0F, 2.0F, 6.0F)
                            .texOffs(28, 40).addBox(-4.5F, -1.0F, -3.0F, 9.0F, 1.5F, 6.0F)
                            .texOffs(28, 48).addBox(-4.0F, 3.0F, -3.0F, 8.0F, 1.5F, 6.0F),
                    PartPose.offset(0.0F, 7.0F, 0.0F));
            body.addOrReplaceChild("rift_ribs",
                    CubeListBuilder.create().texOffs(224, 8).addBox(-1.0F, -5.5F, -0.6F, 2.0F, 11.0F, 1.0F),
                    PartPose.offset(0.0F, 0.0F, -2.5F));

            PartDefinition head = rig.addOrReplaceChild("head",
                    CubeListBuilder.create()
                            .texOffs(0, 0).addBox(-4.0F, -9.0F, -3.5F, 8.0F, 10.0F, 7.0F)
                            .texOffs(32, 0).addBox(-6.0F, -14.0F, -2.5F, 4.0F, 10.0F, 5.0F)
                            .texOffs(50, 0).addBox(2.0F, -14.0F, -2.5F, 4.0F, 10.0F, 5.0F),
                    PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("void_slit",
                    CubeListBuilder.create().texOffs(224, 0).addBox(-1.0F, -3.0F, -0.5F, 2.0F, 6.0F, 1.0F,
                            new CubeDeformation(0.08F)),
                    PartPose.offset(0.0F, -5.0F, -3.35F));

            addWalkerArm(rig, "right_arm", -5.0F, false);
            addWalkerArm(rig, "left_arm", 5.0F, true);
            rig.addOrReplaceChild("right_aux_arm",
                    CubeListBuilder.create().texOffs(86, 32).addBox(-2.0F, -1.0F, -1.5F, 2.0F, 12.0F, 3.0F),
                    PartPose.offsetAndRotation(-3.8F, 9.0F, 1.5F, -0.35F, 0.0F, 0.42F));
            rig.addOrReplaceChild("left_aux_arm",
                    CubeListBuilder.create().texOffs(86, 32).mirror().addBox(0.0F, -1.0F, -1.5F, 2.0F, 12.0F, 3.0F),
                    PartPose.offsetAndRotation(3.8F, 9.0F, 1.5F, -0.35F, 0.0F, -0.42F));

            rig.addOrReplaceChild("front_mantle",
                    CubeListBuilder.create().texOffs(0, 58).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 2.0F),
                    PartPose.offsetAndRotation(0.0F, 14.0F, -2.0F, 0.08F, 0.0F, 0.0F));
            rig.addOrReplaceChild("back_mantle",
                    CubeListBuilder.create().texOffs(28, 58).addBox(-6.0F, 0.0F, -1.0F, 12.0F, 18.0F, 2.0F),
                    PartPose.offsetAndRotation(0.0F, 13.0F, 2.5F, -0.08F, 0.0F, 0.0F));
            rig.addOrReplaceChild("right_leg_shard",
                    CubeListBuilder.create().texOffs(60, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(-2.5F, 17.0F, 0.0F, 0.08F, 0.0F, 0.08F));
            rig.addOrReplaceChild("left_leg_shard",
                    CubeListBuilder.create().texOffs(60, 58).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(2.5F, 17.0F, 0.0F, -0.08F, 0.0F, -0.08F));
            return LayerDefinition.create(mesh, 256, 128);
        }

        private static void addWalkerArm(PartDefinition rig, String name, float x, boolean mirror) {
            CubeListBuilder upper = CubeListBuilder.create().texOffs(60, 20);
            CubeListBuilder lower = CubeListBuilder.create().texOffs(76, 20);
            if (mirror) {
                upper.mirror();
                lower.mirror();
            }
            PartDefinition arm = rig.addOrReplaceChild(name,
                    upper.addBox(-1.5F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(x, 4.0F, 0.0F, 0.05F, 0.0F, mirror ? -0.16F : 0.16F));
            arm.addOrReplaceChild("forearm",
                    lower.addBox(-1.5F, 0.0F, -2.0F, 3.0F, 15.0F, 4.0F)
                            .texOffs(96, 20).addBox(-1.0F, 11.0F, -6.0F, 2.0F, 8.0F, 5.0F),
                    PartPose.offset(0.0F, 11.0F, 0.0F));
        }

        @Override
        public void setupAnim(ShadowWalkerEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            reset();
            float floatWave = Mth.sin(ageInTicks * 0.09F);
            float walk = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
            float attack = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
            rig.y = floatWave * 0.8F;
            body.yRot = Mth.sin(ageInTicks * 0.045F) * 0.05F;
            head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.7F;
            head.xRot = headPitch * Mth.DEG_TO_RAD * 0.55F;
            rightArm.xRot = 0.05F + Mth.cos(limbSwing * 0.55F) * walk * 0.3F - attack * 1.25F;
            leftArm.xRot = 0.05F + Mth.cos(limbSwing * 0.55F + Mth.PI) * walk * 0.3F - attack * 1.05F;
            rightArm.zRot = 0.16F + attack * 0.7F;
            leftArm.zRot = -0.16F - attack * 0.7F;
            rightForearm.xRot = -0.1F - attack * 0.55F;
            leftForearm.xRot = -0.1F - attack * 0.55F;
            rightAux.xRot = -0.35F + Mth.sin(ageInTicks * 0.13F) * 0.18F - attack * 0.6F;
            leftAux.xRot = -0.35F + Mth.sin(ageInTicks * 0.13F + Mth.PI) * 0.18F - attack * 0.6F;
            rightShard.y = 17.0F + Mth.sin(ageInTicks * 0.12F) * 0.7F;
            leftShard.y = 17.0F + Mth.sin(ageInTicks * 0.12F + Mth.PI) * 0.7F;
            rightShard.xRot = 0.08F + Mth.cos(limbSwing * 0.5F) * walk * 0.24F;
            leftShard.xRot = -0.08F + Mth.cos(limbSwing * 0.5F + Mth.PI) * walk * 0.24F;
            frontMantle.xRot = 0.08F + Mth.sin(ageInTicks * 0.07F) * 0.05F;
            backMantle.xRot = -0.08F - Mth.sin(ageInTicks * 0.07F) * 0.06F;
        }
    }

    public static final class ManaLeechModel extends AdvancedModel<ManaLeechEntity> {
        private final ModelPart body;
        private final ModelPart mouth;
        private final ModelPart[] petals = new ModelPart[4];
        private final ModelPart[] legs = new ModelPart[4];
        private final ModelPart[] tendrils = new ModelPart[3];

        public ManaLeechModel(ModelPart root) {
            super(root, "leech");
            body = rig.getChild("body");
            mouth = rig.getChild("mouth");
            for (int i = 0; i < 4; i++) {
                petals[i] = mouth.getChild("petal_" + i);
                legs[i] = rig.getChild("leg_" + i);
            }
            for (int i = 0; i < 3; i++) tendrils[i] = rig.getChild("tendril_" + i);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition rig = mesh.getRoot().addOrReplaceChild("leech", CubeListBuilder.create(), PartPose.ZERO);
            PartDefinition body = rig.addOrReplaceChild("body",
                    CubeListBuilder.create()
                            .texOffs(0, 28).addBox(-4.5F, -4.0F, -6.0F, 9.0F, 8.0F, 14.0F)
                            .texOffs(48, 28).addBox(-5.0F, -4.5F, -2.0F, 10.0F, 9.0F, 4.0F,
                                    new CubeDeformation(0.15F))
                            .texOffs(80, 28).addBox(-4.0F, -3.5F, 7.0F, 8.0F, 7.0F, 7.0F),
                    PartPose.offset(0.0F, 12.0F, 1.0F));
            body.addOrReplaceChild("mana_sac",
                    CubeListBuilder.create().texOffs(224, 8).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F),
                    PartPose.offset(0.0F, 0.0F, -6.0F));
            PartDefinition mouth = rig.addOrReplaceChild("mouth",
                    CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -4.5F, -2.0F, 9.0F, 9.0F, 4.0F),
                    PartPose.offset(0.0F, 12.0F, -6.0F));
            for (int i = 0; i < 4; i++) {
                float angle = i * Mth.HALF_PI;
                mouth.addOrReplaceChild("petal_" + i,
                        CubeListBuilder.create().texOffs(30 + i * 14, 0)
                                .addBox(-2.0F, -1.5F, -6.0F, 4.0F, 3.0F, 7.0F)
                                .texOffs(224, 0).addBox(-0.7F, -0.7F, -6.5F, 1.4F, 1.4F, 1.0F),
                        PartPose.offsetAndRotation(Mth.cos(angle) * 2.7F, Mth.sin(angle) * 2.7F,
                                -1.0F, 0.0F, 0.0F, angle));
            }
            for (int i = 0; i < 4; i++) {
                boolean left = i % 2 == 1;
                float x = left ? 4.0F : -4.0F;
                float z = i < 2 ? -1.0F : 6.0F;
                rig.addOrReplaceChild("leg_" + i,
                        CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F)
                                .texOffs(10, 58).addBox(-1.0F, 8.0F, -4.0F, 2.0F, 2.0F, 5.0F),
                        PartPose.offsetAndRotation(x, 13.0F, z, 0.15F, 0.0F, left ? -0.55F : 0.55F));
            }
            for (int i = 0; i < 3; i++) {
                rig.addOrReplaceChild("tendril_" + i,
                        CubeListBuilder.create().texOffs(26, 58).addBox(-0.75F, 0.0F, -0.75F, 1.5F, 15.0F, 1.5F)
                                .texOffs(34, 58).addBox(-1.0F, 13.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                        PartPose.offsetAndRotation((i - 1) * 2.4F, 10.0F, 12.0F,
                                -1.15F + i * 0.12F, 0.0F, (i - 1) * 0.3F));
            }
            return LayerDefinition.create(mesh, 256, 128);
        }

        @Override
        public void setupAnim(ManaLeechEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            reset();
            float pulse = Mth.sin(ageInTicks * 0.22F);
            float attack = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
            rig.y = -1.5F + pulse * 0.7F;
            body.zScale = 1.0F + pulse * 0.035F;
            mouth.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.45F;
            mouth.xRot = headPitch * Mth.DEG_TO_RAD * 0.35F;
            for (int i = 0; i < 4; i++) {
                float open = 0.12F + attack * 0.7F + Mth.sin(ageInTicks * 0.13F + i) * 0.04F;
                petals[i].xRot = i == 0 ? -open : i == 2 ? open : 0.0F;
                petals[i].yRot = i == 1 ? open : i == 3 ? -open : 0.0F;
                legs[i].xRot += Mth.sin(ageInTicks * 0.19F + i * 1.45F) * 0.34F - attack * 0.35F;
            }
            for (int i = 0; i < 3; i++) {
                tendrils[i].xRot = -1.15F + i * 0.12F + Mth.sin(ageInTicks * 0.14F + i) * 0.16F;
                tendrils[i].zRot = (i - 1) * 0.3F + Mth.cos(ageInTicks * 0.11F + i) * 0.19F;
            }
            if (attack > 0.0F) rig.z -= attack * 2.0F;
        }
    }

    public static final class CrystalParasiteModel extends AdvancedModel<CrystalParasiteEntity> {
        private final ModelPart base;
        private final ModelPart turret;
        private final ModelPart leftShield;
        private final ModelPart rightShield;
        private final ModelPart[] legs = new ModelPart[6];
        private final ModelPart[] fins = new ModelPart[4];

        public CrystalParasiteModel(ModelPart root) {
            super(root, "parasite");
            base = rig.getChild("base");
            turret = rig.getChild("turret");
            leftShield = rig.getChild("left_shield");
            rightShield = rig.getChild("right_shield");
            for (int i = 0; i < 6; i++) legs[i] = rig.getChild("leg_" + i);
            for (int i = 0; i < 4; i++) fins[i] = turret.getChild("fin_" + i);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition rig = mesh.getRoot().addOrReplaceChild("parasite", CubeListBuilder.create(), PartPose.ZERO);
            rig.addOrReplaceChild("base",
                    CubeListBuilder.create()
                            .texOffs(0, 32).addBox(-7.0F, -3.0F, -6.0F, 14.0F, 7.0F, 12.0F)
                            .texOffs(54, 32).addBox(-6.0F, -5.0F, -5.0F, 12.0F, 3.0F, 10.0F),
                    PartPose.offset(0.0F, 16.0F, 0.0F));
            PartDefinition turret = rig.addOrReplaceChild("turret",
                    CubeListBuilder.create()
                            .texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 10.0F, 8.0F)
                            .texOffs(34, 0).addBox(-3.0F, -15.0F, -3.0F, 6.0F, 7.0F, 6.0F),
                    PartPose.offset(0.0F, 12.0F, 0.0F));
            turret.addOrReplaceChild("aperture",
                    CubeListBuilder.create().texOffs(224, 0).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                    PartPose.offset(0.0F, -5.0F, -4.0F));
            for (int i = 0; i < 4; i++) {
                turret.addOrReplaceChild("fin_" + i,
                        CubeListBuilder.create().texOffs(60, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                        PartPose.offset((i % 2 == 0 ? 1 : -1) * 7.0F,
                                -7.0F + (i / 2) * 5.0F, (i < 2 ? 1 : -1) * 3.0F));
            }
            rig.addOrReplaceChild("left_shield",
                    CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, -6.0F, -4.0F, 3.0F, 13.0F, 9.0F)
                            .texOffs(26, 58).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 1.0F),
                    PartPose.offsetAndRotation(7.0F, 16.0F, -2.0F, 0.0F, -0.18F, -0.08F));
            rig.addOrReplaceChild("right_shield",
                    CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-2.0F, -6.0F, -4.0F, 3.0F, 13.0F, 9.0F)
                            .texOffs(26, 58).addBox(-0.5F, -2.0F, -4.5F, 1.0F, 4.0F, 1.0F),
                    PartPose.offsetAndRotation(-7.0F, 16.0F, -2.0F, 0.0F, 0.18F, 0.08F));
            for (int i = 0; i < 6; i++) {
                boolean left = i % 2 == 1;
                float z = -4.5F + (i / 2) * 4.5F;
                rig.addOrReplaceChild("leg_" + i,
                        CubeListBuilder.create().texOffs(42, 58).addBox(-1.5F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F)
                                .texOffs(42, 66).addBox(5.0F, -1.0F, -1.0F, 3.0F, 8.0F, 2.0F),
                        PartPose.offsetAndRotation(left ? 5.0F : -5.0F, 17.0F, z,
                                0.0F, left ? -0.35F : Mth.PI + 0.35F, left ? 0.35F : -0.35F));
            }
            return LayerDefinition.create(mesh, 256, 128);
        }

        @Override
        public void setupAnim(CrystalParasiteEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            reset();
            float walk = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
            float charge = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
            base.y = 16.0F + Mth.sin(ageInTicks * 0.08F) * 0.12F;
            turret.yRot = netHeadYaw * Mth.DEG_TO_RAD;
            turret.xRot = headPitch * Mth.DEG_TO_RAD * 0.35F - charge * 0.1F;
            for (int i = 0; i < 6; i++) {
                legs[i].zRot += Mth.sin(limbSwing * 0.7F + i * 1.3F) * walk * 0.22F;
            }
            for (int i = 0; i < 4; i++) {
                float orbit = ageInTicks * 0.04F + i * Mth.HALF_PI;
                fins[i].x = Mth.cos(orbit) * (7.0F + charge * 2.0F);
                fins[i].z = Mth.sin(orbit) * (7.0F + charge * 2.0F);
                fins[i].y = -6.0F + Mth.sin(orbit * 1.7F) * 2.0F;
                fins[i].yRot = -orbit;
            }
            leftShield.yRot = -0.18F - charge * 0.55F;
            rightShield.yRot = 0.18F + charge * 0.55F;
            turret.yScale = 1.0F + charge * 0.09F;
        }
    }

    public static final class FallenResearcherModel extends AdvancedModel<FallenResearcherEntity> {
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart normalArm;
        private final ModelPart book;
        private final ModelPart mutatedArm;
        private final ModelPart[] fingers = new ModelPart[3];
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;
        private final ModelPart rightCoat;
        private final ModelPart leftCoat;
        private final ModelPart rightTendril;
        private final ModelPart leftTendril;

        public FallenResearcherModel(ModelPart root) {
            super(root, "researcher");
            body = rig.getChild("body");
            head = rig.getChild("head");
            normalArm = rig.getChild("normal_arm");
            book = normalArm.getChild("codex");
            mutatedArm = rig.getChild("mutated_arm");
            for (int i = 0; i < 3; i++) fingers[i] = mutatedArm.getChild("finger_" + i);
            rightLeg = rig.getChild("right_leg");
            leftLeg = rig.getChild("left_leg");
            rightCoat = rig.getChild("right_coat");
            leftCoat = rig.getChild("left_coat");
            rightTendril = rig.getChild("right_tendril");
            leftTendril = rig.getChild("left_tendril");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition rig = mesh.getRoot().addOrReplaceChild("researcher", CubeListBuilder.create(), PartPose.ZERO);
            rig.addOrReplaceChild("body",
                    CubeListBuilder.create()
                            .texOffs(0, 32).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 14.0F, 6.0F)
                            .texOffs(34, 32).addBox(-6.5F, -2.0F, -3.5F, 5.0F, 5.0F, 7.0F)
                            .texOffs(56, 32).addBox(-4.0F, 3.0F, -3.5F, 8.0F, 8.0F, 1.0F),
                    PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.18F, 0.0F, 0.0F));
            PartDefinition head = rig.addOrReplaceChild("head",
                    CubeListBuilder.create()
                            .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 9.0F, 8.0F)
                            .texOffs(34, 0).addBox(-3.0F, -3.0F, -8.0F, 6.0F, 5.0F, 5.0F)
                            .texOffs(58, 0).addBox(-4.5F, -8.5F, -4.5F, 9.0F, 2.0F, 9.0F),
                    PartPose.offsetAndRotation(0.0F, 5.0F, -1.0F, 0.14F, 0.0F, 0.0F));
            head.addOrReplaceChild("lenses",
                    CubeListBuilder.create().texOffs(224, 0)
                            .addBox(-2.8F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F)
                            .addBox(0.8F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F)
                            .addBox(-1.0F, 1.2F, -0.5F, 2.0F, 2.0F, 1.0F),
                    PartPose.offset(0.0F, -3.0F, -7.8F));
            PartDefinition normalArm = rig.addOrReplaceChild("normal_arm",
                    CubeListBuilder.create().texOffs(0, 58).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                    PartPose.offsetAndRotation(-5.0F, 7.0F, 0.0F, -0.35F, 0.0F, 0.12F));
            normalArm.addOrReplaceChild("codex",
                    CubeListBuilder.create().texOffs(18, 58).addBox(-4.0F, -5.0F, -1.0F, 8.0F, 10.0F, 2.0F)
                            .texOffs(224, 10).addBox(-2.0F, -2.0F, -1.2F, 4.0F, 4.0F, 1.0F),
                    PartPose.offsetAndRotation(-1.0F, 12.0F, -3.0F, 0.25F, 0.0F, -0.1F));
            PartDefinition mutated = rig.addOrReplaceChild("mutated_arm",
                    CubeListBuilder.create().texOffs(42, 58).addBox(-1.0F, -3.0F, -3.0F, 6.0F, 15.0F, 6.0F)
                            .texOffs(68, 58).addBox(1.0F, 8.0F, -4.0F, 7.0F, 7.0F, 8.0F),
                    PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, -0.28F, 0.0F, -0.16F));
            for (int i = 0; i < 3; i++) {
                mutated.addOrReplaceChild("finger_" + i,
                        CubeListBuilder.create().texOffs(100, 58).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F)
                                .texOffs(224, 20).addBox(-1.5F, 9.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                        PartPose.offsetAndRotation(4.0F + i * 1.6F, 12.0F, (i - 1) * 2.2F,
                                -0.5F + i * 0.16F, 0.0F, -0.45F + i * 0.45F));
            }
            rig.addOrReplaceChild("right_leg",
                    CubeListBuilder.create().texOffs(0, 82).addBox(-3.0F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F),
                    PartPose.offset(-2.5F, 12.0F, 1.0F));
            rig.addOrReplaceChild("left_leg",
                    CubeListBuilder.create().texOffs(0, 82).mirror().addBox(-2.0F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F),
                    PartPose.offset(2.5F, 12.0F, 1.0F));
            rig.addOrReplaceChild("right_coat",
                    CubeListBuilder.create().texOffs(22, 82).addBox(-5.0F, 0.0F, -1.0F, 5.0F, 12.0F, 2.0F),
                    PartPose.offsetAndRotation(0.0F, 11.0F, 2.8F, -0.08F, 0.0F, 0.08F));
            rig.addOrReplaceChild("left_coat",
                    CubeListBuilder.create().texOffs(22, 82).mirror().addBox(0.0F, 0.0F, -1.0F, 5.0F, 12.0F, 2.0F),
                    PartPose.offsetAndRotation(0.0F, 11.0F, 2.8F, -0.08F, 0.0F, -0.08F));
            rig.addOrReplaceChild("right_tendril",
                    CubeListBuilder.create().texOffs(40, 82).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F),
                    PartPose.offsetAndRotation(-3.0F, 5.0F, 2.5F, -1.05F, 0.0F, 0.38F));
            rig.addOrReplaceChild("left_tendril",
                    CubeListBuilder.create().texOffs(40, 82).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F),
                    PartPose.offsetAndRotation(3.0F, 5.0F, 2.5F, -1.05F, 0.0F, -0.38F));
            return LayerDefinition.create(mesh, 256, 128);
        }

        @Override
        public void setupAnim(FallenResearcherEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            reset();
            float walk = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
            float cast = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
            body.xRot = 0.18F + Mth.sin(ageInTicks * 0.08F) * 0.025F;
            head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.75F;
            head.xRot = 0.14F + headPitch * Mth.DEG_TO_RAD * 0.55F;
            rightLeg.xRot = Mth.cos(limbSwing * 0.66F) * walk * 0.75F;
            leftLeg.xRot = Mth.cos(limbSwing * 0.66F + Mth.PI) * walk * 0.75F;
            normalArm.xRot = -0.35F + Mth.sin(ageInTicks * 0.1F) * 0.08F - cast * 0.35F;
            mutatedArm.xRot = -0.28F - cast * 1.15F;
            mutatedArm.yRot = -cast * 0.35F;
            book.y = 12.0F + Mth.sin(ageInTicks * 0.16F) * 0.6F - cast * 2.0F;
            book.yRot = Mth.sin(ageInTicks * 0.08F) * 0.14F;
            for (int i = 0; i < 3; i++) {
                fingers[i].zRot = -0.45F + i * 0.45F + (i - 1) * cast * 0.38F;
                fingers[i].xRot = -0.5F + i * 0.16F - cast * 0.25F;
            }
            rightCoat.xRot = -0.08F + Mth.sin(limbSwing * 0.66F) * walk * 0.18F;
            leftCoat.xRot = -0.08F + Mth.sin(limbSwing * 0.66F + Mth.PI) * walk * 0.18F;
            rightTendril.xRot = -1.05F + Mth.sin(ageInTicks * 0.12F) * 0.18F;
            leftTendril.xRot = -1.05F + Mth.sin(ageInTicks * 0.12F + Mth.PI) * 0.18F;
        }
    }

    public static final class BoundaryWatcherModel extends AdvancedModel<BoundaryWatcherEntity> {
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart leftGate;
        private final ModelPart rightGate;
        private final ModelPart rightArm;
        private final ModelPart leftArm;
        private final ModelPart rightHand;
        private final ModelPart leftHand;
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;
        private final ModelPart[] halo = new ModelPart[6];

        public BoundaryWatcherModel(ModelPart root) {
            super(root, "watcher");
            body = rig.getChild("body");
            head = rig.getChild("head");
            leftGate = head.getChild("left_gate");
            rightGate = head.getChild("right_gate");
            rightArm = rig.getChild("right_arm");
            leftArm = rig.getChild("left_arm");
            rightHand = rightArm.getChild("hand");
            leftHand = leftArm.getChild("hand");
            rightLeg = rig.getChild("right_leg");
            leftLeg = rig.getChild("left_leg");
            for (int i = 0; i < 6; i++) halo[i] = rig.getChild("halo_" + i);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition rig = mesh.getRoot().addOrReplaceChild("watcher", CubeListBuilder.create(), PartPose.ZERO);
            PartDefinition body = rig.addOrReplaceChild("body",
                    CubeListBuilder.create()
                            .texOffs(0, 34).addBox(-7.0F, -3.0F, -4.0F, 14.0F, 18.0F, 8.0F)
                            .texOffs(46, 34).addBox(-10.0F, -4.0F, -4.5F, 6.0F, 7.0F, 9.0F)
                            .texOffs(78, 34).addBox(4.0F, -4.0F, -4.5F, 6.0F, 7.0F, 9.0F),
                    PartPose.offset(0.0F, 2.0F, 0.0F));
            body.addOrReplaceChild("boundary_core",
                    CubeListBuilder.create().texOffs(224, 8).addBox(-3.0F, -4.0F, -0.5F, 6.0F, 8.0F, 1.0F),
                    PartPose.offset(0.0F, 5.0F, -4.1F));
            PartDefinition head = rig.addOrReplaceChild("head",
                    CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 11.0F, 8.0F),
                    PartPose.offset(0.0F, -6.0F, 0.0F));
            head.addOrReplaceChild("rift_face",
                    CubeListBuilder.create().texOffs(224, 0).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 8.0F, 1.0F),
                    PartPose.offset(0.0F, -4.5F, -4.0F));
            head.addOrReplaceChild("left_gate",
                    CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -11.0F, -4.5F, 5.0F, 13.0F, 9.0F),
                    PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.08F));
            head.addOrReplaceChild("right_gate",
                    CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-4.0F, -11.0F, -4.5F, 5.0F, 13.0F, 9.0F),
                    PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.08F));
            addWatcherArm(rig, "right_arm", -9.0F, false);
            addWatcherArm(rig, "left_arm", 9.0F, true);
            rig.addOrReplaceChild("right_leg",
                    CubeListBuilder.create().texOffs(0, 66).addBox(-5.0F, 0.0F, -4.0F, 7.0F, 19.0F, 8.0F)
                            .texOffs(32, 66).addBox(-5.5F, 16.0F, -7.0F, 8.0F, 5.0F, 11.0F),
                    PartPose.offset(-4.0F, 14.0F, 0.0F));
            rig.addOrReplaceChild("left_leg",
                    CubeListBuilder.create().texOffs(0, 66).mirror().addBox(-2.0F, 0.0F, -4.0F, 7.0F, 19.0F, 8.0F)
                            .texOffs(32, 66).mirror().addBox(-2.5F, 16.0F, -7.0F, 8.0F, 5.0F, 11.0F),
                    PartPose.offset(4.0F, 14.0F, 0.0F));
            for (int i = 0; i < 6; i++) {
                float angle = (i - 2.5F) * 0.72F;
                rig.addOrReplaceChild("halo_" + i,
                        CubeListBuilder.create().texOffs(72, 66).addBox(-3.0F, -7.0F, -1.5F, 6.0F, 14.0F, 3.0F)
                                .texOffs(224, 20).addBox(-1.0F, -2.0F, -1.7F, 2.0F, 4.0F, 1.0F),
                        PartPose.offsetAndRotation(Mth.sin(angle) * 14.0F, -4.0F - Mth.cos(angle) * 12.0F,
                                5.0F, 0.0F, 0.0F, -angle));
            }
            return LayerDefinition.create(mesh, 256, 128);
        }

        private static void addWatcherArm(PartDefinition rig, String name, float x, boolean mirror) {
            CubeListBuilder armBuilder = CubeListBuilder.create().texOffs(104, 34);
            CubeListBuilder handBuilder = CubeListBuilder.create().texOffs(104, 64);
            if (mirror) {
                armBuilder.mirror();
                handBuilder.mirror();
            }
            PartDefinition arm = rig.addOrReplaceChild(name,
                    armBuilder.addBox(-3.5F, -3.0F, -3.5F, 7.0F, 20.0F, 7.0F),
                    PartPose.offsetAndRotation(x, 2.0F, 0.0F, 0.0F, 0.0F, mirror ? -0.08F : 0.08F));
            PartDefinition hand = arm.addOrReplaceChild("hand",
                    handBuilder.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 6.0F, 8.0F),
                    PartPose.offset(0.0F, 16.0F, 0.0F));
            for (int i = 0; i < 4; i++) {
                hand.addOrReplaceChild("prong_" + i,
                        CubeListBuilder.create().texOffs(104, 80).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F),
                        PartPose.offsetAndRotation((i % 2 == 0 ? -1 : 1) * 2.5F, 5.0F,
                                (i < 2 ? -1 : 1) * 2.5F, (i < 2 ? -0.2F : 0.2F), 0.0F,
                                (i % 2 == 0 ? 0.18F : -0.18F)));
            }
        }

        @Override
        public void setupAnim(BoundaryWatcherEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            reset();
            int phase = entity.getPhase();
            float phaseOpen = (phase - 1) * 0.45F;
            float walk = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
            float attack = attackTime > 0.0F ? Mth.sin(Mth.sqrt(attackTime) * Mth.PI) : 0.0F;
            body.y = 2.0F + Mth.sin(ageInTicks * 0.065F) * 0.18F;
            head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
            head.xRot = headPitch * Mth.DEG_TO_RAD * 0.35F;
            leftGate.zRot = -0.08F - phaseOpen * 0.42F;
            rightGate.zRot = 0.08F + phaseOpen * 0.42F;
            rightLeg.xRot = Mth.cos(limbSwing * 0.55F) * walk * 0.55F;
            leftLeg.xRot = Mth.cos(limbSwing * 0.55F + Mth.PI) * walk * 0.55F;
            rightArm.xRot = Mth.cos(limbSwing * 0.45F + Mth.PI) * walk * 0.25F - attack * 1.15F;
            leftArm.xRot = Mth.cos(limbSwing * 0.45F) * walk * 0.25F - attack * 0.75F;
            rightArm.zRot = 0.08F + phaseOpen * 0.22F + attack * 0.35F;
            leftArm.zRot = -0.08F - phaseOpen * 0.22F - attack * 0.35F;
            rightHand.yRot = attack * 0.45F;
            leftHand.yRot = -attack * 0.45F;
            for (int i = 0; i < 6; i++) {
                float angle = (i - 2.5F) * (0.72F + phaseOpen * 0.18F);
                float radius = 14.0F + phaseOpen * 7.0F;
                halo[i].x = Mth.sin(angle) * radius;
                halo[i].y = -4.0F - Mth.cos(angle) * (12.0F + phaseOpen * 5.0F)
                        + Mth.sin(ageInTicks * 0.08F + i) * (0.5F + phaseOpen);
                halo[i].z = 5.0F + phaseOpen * 2.0F;
                halo[i].zRot = -angle + Mth.sin(ageInTicks * 0.05F + i) * 0.04F;
                halo[i].yRot = ageInTicks * 0.01F * phase;
            }
        }
    }
}
