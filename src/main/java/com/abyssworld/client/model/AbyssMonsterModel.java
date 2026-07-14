package com.abyssworld.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;

public class AbyssMonsterModel<T extends Monster> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart leftTendril;
    private final ModelPart rightTendril;

    public AbyssMonsterModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.leftTendril = root.getChild("left_tendril");
        this.rightTendril = root.getChild("right_tendril");
    }

    public static LayerDefinition rootboundThrallLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(7.0F, 7.0F, 7.0F, 8.0F, 11.0F, 5.0F, 3.0F, 12.0F, 3.0F)
                .head(1.0F, 0.0F).body(1.0F, 0.0F).arms(4.8F, 2.0F, 0.0F).legs(1.8F, 12.0F, 0.0F).tendrils(11.0F, 4.0F));
        parts.head.addOrReplaceChild("root_crown",
                CubeListBuilder.create().texOffs(32, 0).addBox(-5.0F, -10.0F, -1.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(36, 4).addBox(-6.5F, -11.0F, -0.5F, 2.0F, 5.0F, 1.0F)
                        .texOffs(44, 4).addBox(4.5F, -11.0F, -0.5F, 2.0F, 5.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("root_plates",
                CubeListBuilder.create().texOffs(44, 18).addBox(-5.5F, 1.0F, -3.2F, 11.0F, 4.0F, 2.0F)
                        .texOffs(48, 24).addBox(-4.0F, 7.0F, 2.2F, 8.0F, 5.0F, 2.0F),
                PartPose.ZERO);
        armClaw(parts.rightArm, "right_root_claws", -2.6F);
        armClaw(parts.leftArm, "left_root_claws", 0.8F);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition forestStalkerLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(6.0F, 6.0F, 9.0F, 7.0F, 12.0F, 4.0F, 2.5F, 15.0F, 2.5F)
                .head(2.0F, -2.0F).body(3.0F, -1.0F).arms(5.2F, 1.5F, -1.2F).legs(2.0F, 12.0F, 0.0F).tendrils(9.0F, 3.5F));
        parts.head.addOrReplaceChild("snout",
                CubeListBuilder.create().texOffs(28, 0).addBox(-2.0F, -5.5F, -7.5F, 4.0F, 3.0F, 4.0F)
                        .texOffs(44, 0).addBox(-3.5F, -9.5F, -3.0F, 2.0F, 6.0F, 1.0F)
                        .texOffs(50, 0).addBox(1.5F, -9.5F, -3.0F, 2.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("high_shoulder",
                CubeListBuilder.create().texOffs(40, 18).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 5.0F, 7.0F)
                        .texOffs(48, 28).addBox(-1.0F, 0.0F, 3.5F, 2.0F, 11.0F, 2.0F),
                PartPose.ZERO);
        armClaw(parts.rightArm, "right_stalker_claws", -2.2F);
        armClaw(parts.leftArm, "left_stalker_claws", 0.4F);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition rottenForestGuardianLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(10.0F, 9.0F, 10.0F, 12.0F, 14.0F, 7.0F, 5.0F, 15.0F, 5.0F)
                .head(-1.0F, 0.0F).body(0.0F, 0.0F).arms(7.2F, 1.0F, 0.0F).legs(3.0F, 13.0F, 0.0F).tendrils(12.0F, 5.0F));
        parts.head.addOrReplaceChild("branch_antlers",
                CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -12.0F, -1.0F, 16.0F, 2.0F, 2.0F)
                        .texOffs(36, 32).addBox(-9.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(44, 32).addBox(7.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(52, 32).addBox(-3.0F, -17.0F, -0.5F, 2.0F, 7.0F, 1.0F)
                        .texOffs(58, 32).addBox(1.0F, -17.0F, -0.5F, 2.0F, 7.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("bark_shell",
                CubeListBuilder.create().texOffs(0, 42).addBox(-7.0F, -1.0F, -4.2F, 14.0F, 8.0F, 3.0F)
                        .texOffs(34, 42).addBox(-6.0F, 5.0F, 3.0F, 12.0F, 9.0F, 3.0F),
                PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_branch_fist",
                CubeListBuilder.create().texOffs(0, 54).addBox(-5.5F, 10.0F, -3.0F, 5.0F, 5.0F, 6.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_branch_fist",
                CubeListBuilder.create().texOffs(22, 54).addBox(0.5F, 10.0F, -3.0F, 5.0F, 5.0F, 6.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition cinderImpLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(6.0F, 6.0F, 6.0F, 6.0F, 8.0F, 4.0F, 2.0F, 9.0F, 2.0F)
                .head(3.0F, 0.0F).body(4.0F, 0.0F).arms(4.0F, 5.0F, 0.0F).legs(1.5F, 14.0F, 0.0F).tendrils(7.0F, 3.0F));
        parts.head.addOrReplaceChild("imp_horns",
                CubeListBuilder.create().texOffs(28, 0).addBox(-5.0F, -9.0F, -1.0F, 3.0F, 3.0F, 2.0F)
                        .texOffs(38, 0).addBox(2.0F, -9.0F, -1.0F, 3.0F, 3.0F, 2.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("ember_wings",
                CubeListBuilder.create().texOffs(44, 8).addBox(-8.0F, 1.0F, 2.3F, 6.0F, 8.0F, 1.0F)
                        .texOffs(44, 17).addBox(2.0F, 1.0F, 2.3F, 6.0F, 8.0F, 1.0F)
                        .texOffs(36, 26).addBox(-1.0F, 7.0F, 2.5F, 2.0F, 9.0F, 2.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition ashRevenantLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(7.0F, 8.0F, 7.0F, 7.0F, 13.0F, 5.0F, 3.0F, 14.0F, 3.0F)
                .head(0.0F, 0.0F).body(0.0F, 0.0F).arms(5.8F, 1.0F, 0.0F).legs(2.0F, 12.0F, 0.0F).tendrils(10.0F, 4.0F));
        parts.head.addOrReplaceChild("ash_face_crest",
                CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 4.0F, 2.0F)
                        .texOffs(46, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("burnt_mail",
                CubeListBuilder.create().texOffs(0, 24).addBox(-5.5F, 0.0F, -3.0F, 11.0F, 12.0F, 2.0F)
                        .texOffs(28, 24).addBox(-6.5F, 0.0F, -3.2F, 3.0F, 4.0F, 6.0F)
                        .texOffs(42, 24).addBox(3.5F, 0.0F, -3.2F, 3.0F, 4.0F, 6.0F),
                PartPose.ZERO);
        bladeArm(parts.rightArm, "right_ash_blade", -2.0F);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition ashKingLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(9.0F, 9.0F, 9.0F, 10.0F, 12.0F, 6.0F, 4.0F, 13.0F, 4.0F)
                .head(-1.0F, 0.0F).body(0.0F, 0.0F).arms(6.5F, 1.0F, 0.0F).legs(2.2F, 12.0F, 0.0F).tendrils(13.0F, 5.0F));
        parts.head.addOrReplaceChild("flame_crown",
                CubeListBuilder.create().texOffs(28, 0).addBox(-6.0F, -11.0F, -1.0F, 12.0F, 2.0F, 2.0F)
                        .texOffs(0, 24).addBox(-5.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(8, 24).addBox(-1.0F, -17.0F, -0.5F, 2.0F, 8.0F, 1.0F)
                        .texOffs(16, 24).addBox(3.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("royal_mantle",
                CubeListBuilder.create().texOffs(24, 20).addBox(-7.0F, -1.0F, 2.0F, 14.0F, 14.0F, 2.0F)
                        .texOffs(46, 8).addBox(-2.0F, 3.0F, -3.8F, 4.0F, 6.0F, 2.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_flame_fan",
                CubeListBuilder.create().texOffs(50, 18).addBox(1.0F, 2.0F, -2.5F, 2.0F, 10.0F, 5.0F),
                PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_flame_fan",
                CubeListBuilder.create().texOffs(50, 18).addBox(-3.0F, 2.0F, -2.5F, 2.0F, 10.0F, 5.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition glacialWraithLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(6.0F, 7.0F, 6.0F, 6.0F, 14.0F, 3.0F, 2.0F, 10.0F, 2.0F)
                .head(0.0F, 0.0F).body(1.0F, 0.0F).arms(4.8F, 2.0F, 0.0F).legs(1.1F, 14.0F, 0.0F).tendrils(15.0F, 3.5F));
        parts.head.addOrReplaceChild("ice_veil",
                CubeListBuilder.create().texOffs(28, 0).addBox(-5.0F, -9.0F, -3.0F, 10.0F, 3.0F, 1.0F)
                        .texOffs(44, 4).addBox(-1.0F, -13.0F, -0.5F, 2.0F, 5.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("torn_robe",
                CubeListBuilder.create().texOffs(28, 18).addBox(-5.0F, 7.0F, -2.0F, 10.0F, 10.0F, 2.0F)
                        .texOffs(52, 14).addBox(-1.0F, 1.0F, 2.0F, 2.0F, 15.0F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition frostMarauderLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(7.0F, 7.0F, 7.0F, 8.0F, 12.0F, 5.0F, 3.0F, 13.0F, 3.0F)
                .head(0.0F, 0.0F).body(0.5F, 0.0F).arms(5.5F, 1.5F, 0.0F).legs(2.0F, 12.0F, 0.0F).tendrils(10.0F, 3.5F));
        parts.head.addOrReplaceChild("ice_helm",
                CubeListBuilder.create().texOffs(32, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 3.0F, 2.0F)
                        .texOffs(48, 5).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("ice_pauldron",
                CubeListBuilder.create().texOffs(28, 18).addBox(-7.0F, 0.0F, -3.0F, 4.0F, 5.0F, 6.0F)
                        .texOffs(46, 18).addBox(3.0F, 0.0F, -3.0F, 4.0F, 5.0F, 6.0F)
                        .texOffs(52, 0).addBox(-1.0F, -3.0F, 2.5F, 2.0F, 8.0F, 1.0F),
                PartPose.ZERO);
        lanceArm(parts.rightArm, "right_ice_lance", -2.0F);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition frostboundWardenLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(9.0F, 8.0F, 9.0F, 11.0F, 13.0F, 7.0F, 4.0F, 13.0F, 4.0F)
                .head(-0.5F, 0.0F).body(0.0F, 0.0F).arms(6.8F, 1.0F, 0.0F).legs(2.4F, 12.0F, 0.0F).tendrils(9.0F, 4.0F));
        parts.head.addOrReplaceChild("warden_crown",
                CubeListBuilder.create().texOffs(28, 0).addBox(-6.0F, -10.0F, -1.0F, 12.0F, 2.0F, 2.0F)
                        .texOffs(0, 24).addBox(-6.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(8, 24).addBox(4.0F, -15.0F, -0.5F, 2.0F, 6.0F, 1.0F)
                        .texOffs(16, 24).addBox(-1.0F, -17.0F, -0.5F, 2.0F, 8.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("frozen_carapace",
                CubeListBuilder.create().texOffs(24, 18).addBox(-6.0F, -1.0F, -3.8F, 12.0F, 12.0F, 2.0F)
                        .texOffs(48, 14).addBox(-5.0F, 8.0F, 2.5F, 10.0F, 6.0F, 2.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_ice_shield",
                CubeListBuilder.create().texOffs(48, 22).addBox(2.0F, 1.0F, -4.0F, 2.0F, 11.0F, 8.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition marrowCrawlerLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(8.0F, 5.0F, 11.0F, 8.0F, 7.0F, 13.0F, 2.5F, 8.0F, 2.5F)
                .head(8.0F, -4.0F).body(9.0F, 0.0F).arms(5.5F, 9.0F, -2.5F).legs(3.0F, 16.0F, 2.5F).tendrils(8.0F, 6.0F));
        parts.head.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -1.0F, -8.0F, 6.0F, 3.0F, 5.0F)
                        .texOffs(54, 0).addBox(-4.5F, -4.0F, -5.0F, 1.0F, 5.0F, 1.0F)
                        .texOffs(58, 0).addBox(3.5F, -4.0F, -5.0F, 1.0F, 5.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("spine_row",
                CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -4.0F, -5.0F, 2.0F, 6.0F, 1.0F)
                        .texOffs(8, 32).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 7.0F, 1.0F)
                        .texOffs(16, 32).addBox(-1.0F, -4.0F, 3.0F, 2.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition fleshHunterLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(9.0F, 6.0F, 12.0F, 13.0F, 9.0F, 16.0F, 4.0F, 9.0F, 4.0F)
                .head(6.0F, -6.0F).body(8.0F, 0.0F).arms(6.5F, 9.0F, -4.0F).legs(4.0F, 15.0F, 4.5F).tendrils(10.0F, 7.0F));
        parts.head.addOrReplaceChild("split_maw",
                CubeListBuilder.create().texOffs(36, 0).addBox(-4.0F, -1.0F, -9.0F, 8.0F, 3.0F, 6.0F)
                        .texOffs(64, 0).addBox(-5.0F, -5.0F, -5.0F, 2.0F, 6.0F, 2.0F)
                        .texOffs(72, 0).addBox(3.0F, -5.0F, -5.0F, 2.0F, 6.0F, 2.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("sinew_ridge",
                CubeListBuilder.create().texOffs(0, 34).addBox(-2.0F, -5.0F, -7.0F, 4.0F, 7.0F, 2.0F)
                        .texOffs(14, 34).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 8.0F, 2.0F)
                        .texOffs(28, 34).addBox(-2.0F, -5.0F, 5.0F, 4.0F, 7.0F, 2.0F),
                PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_talon",
                CubeListBuilder.create().texOffs(82, 8).addBox(-4.0F, 6.0F, -5.0F, 3.0F, 4.0F, 7.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_talon",
                CubeListBuilder.create().texOffs(102, 8).addBox(1.0F, 6.0F, -5.0F, 3.0F, 4.0F, 7.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static LayerDefinition fleshColossusLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(12.0F, 8.0F, 12.0F, 18.0F, 15.0F, 12.0F, 6.0F, 14.0F, 6.0F)
                .head(2.0F, -2.0F).body(2.0F, 0.0F).arms(9.0F, 3.0F, -1.0F).legs(4.0F, 13.0F, 1.5F).tendrils(13.0F, 7.0F));
        parts.head.addOrReplaceChild("bone_mask",
                CubeListBuilder.create().texOffs(40, 0).addBox(-6.0F, -9.0F, -7.0F, 12.0F, 4.0F, 3.0F)
                        .texOffs(76, 0).addBox(-8.0F, -5.0F, -2.0F, 3.0F, 5.0F, 3.0F)
                        .texOffs(88, 0).addBox(5.0F, -5.0F, -2.0F, 3.0F, 5.0F, 3.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("rib_cage",
                CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, -1.0F, -7.0F, 16.0F, 5.0F, 3.0F)
                        .texOffs(40, 36).addBox(-7.0F, 4.0F, -7.2F, 14.0F, 4.0F, 3.0F)
                        .texOffs(76, 32).addBox(-5.0F, 7.0F, 4.0F, 10.0F, 8.0F, 4.0F),
                PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_mass_fist",
                CubeListBuilder.create().texOffs(0, 52).addBox(-7.0F, 10.0F, -4.0F, 7.0F, 5.0F, 8.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_mass_fist",
                CubeListBuilder.create().texOffs(34, 52).addBox(0.0F, 10.0F, -4.0F, 7.0F, 5.0F, 8.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static LayerDefinition voidShadeLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(5.0F, 7.0F, 5.0F, 5.0F, 15.0F, 3.0F, 2.0F, 16.0F, 2.0F)
                .head(-1.0F, 0.0F).body(0.0F, 0.0F).arms(4.5F, 1.0F, 0.0F).legs(1.0F, 13.0F, 0.0F).tendrils(17.0F, 3.0F));
        parts.head.addOrReplaceChild("void_face",
                CubeListBuilder.create().texOffs(28, 0).addBox(-4.0F, -9.0F, -4.5F, 8.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("shadow_ribbon",
                CubeListBuilder.create().texOffs(24, 18).addBox(-7.0F, 2.0F, 2.0F, 14.0F, 13.0F, 1.0F)
                        .texOffs(54, 6).addBox(-1.0F, 0.0F, 2.8F, 2.0F, 17.0F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition voidReaperLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(6.0F, 9.0F, 6.0F, 6.0F, 17.0F, 4.0F, 2.5F, 17.0F, 2.5F)
                .head(-2.0F, 0.0F).body(-1.0F, 0.0F).arms(5.3F, 0.0F, 0.0F).legs(1.4F, 12.0F, 0.0F).tendrils(18.0F, 4.0F));
        parts.head.addOrReplaceChild("reaper_hood",
                CubeListBuilder.create().texOffs(28, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 4.0F, 2.0F)
                        .texOffs(48, 0).addBox(-2.0F, -14.0F, -0.5F, 4.0F, 5.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("void_ribs",
                CubeListBuilder.create().texOffs(22, 18).addBox(-5.0F, 2.0F, -3.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(22, 23).addBox(-4.0F, 6.0F, -3.0F, 8.0F, 2.0F, 2.0F)
                        .texOffs(46, 16).addBox(-8.0F, 1.0F, 2.2F, 16.0F, 15.0F, 1.0F),
                PartPose.ZERO);
        bladeArm(parts.leftArm, "left_void_scythe", 0.5F);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition voidArchonLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(7.0F, 10.0F, 7.0F, 7.0F, 18.0F, 4.0F, 3.0F, 18.0F, 3.0F)
                .head(-3.0F, 0.0F).body(-1.0F, 0.0F).arms(6.0F, -1.0F, 0.0F).legs(1.5F, 12.0F, 0.0F).tendrils(19.0F, 4.5F));
        parts.head.addOrReplaceChild("archon_halo",
                CubeListBuilder.create().texOffs(28, 0).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 2.0F, 1.0F)
                        .texOffs(0, 24).addBox(-1.0F, -18.0F, -0.5F, 2.0F, 7.0F, 1.0F)
                        .texOffs(8, 24).addBox(-10.0F, -9.0F, -0.5F, 5.0F, 2.0F, 1.0F)
                        .texOffs(22, 24).addBox(5.0F, -9.0F, -0.5F, 5.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("space_mantle",
                CubeListBuilder.create().texOffs(22, 16).addBox(-9.0F, 1.0F, 2.2F, 18.0F, 16.0F, 1.0F)
                        .texOffs(46, 8).addBox(-2.0F, 3.0F, -3.2F, 4.0F, 10.0F, 1.0F),
                PartPose.ZERO);
        parts.leftArm.addOrReplaceChild("left_orbit",
                CubeListBuilder.create().texOffs(56, 20).addBox(2.0F, 2.0F, -3.0F, 2.0F, 9.0F, 6.0F),
                PartPose.ZERO);
        parts.rightArm.addOrReplaceChild("right_orbit",
                CubeListBuilder.create().texOffs(56, 20).addBox(-4.0F, 2.0F, -3.0F, 2.0F, 9.0F, 6.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition abyssSovereignLayer() {
        MeshDefinition mesh = new MeshDefinition();
        Parts parts = core(mesh, Shape.humanoid(10.0F, 11.0F, 10.0F, 11.0F, 18.0F, 7.0F, 4.0F, 18.0F, 4.0F)
                .head(-5.0F, 0.0F).body(-2.0F, 0.0F).arms(7.5F, -1.0F, 0.0F).legs(2.0F, 12.0F, 0.0F).tendrils(20.0F, 5.5F));
        parts.head.addOrReplaceChild("sovereign_crown",
                CubeListBuilder.create().texOffs(28, 0).addBox(-8.0F, -13.0F, -1.0F, 16.0F, 3.0F, 2.0F)
                        .texOffs(0, 22).addBox(-7.0F, -20.0F, -0.5F, 2.0F, 8.0F, 1.0F)
                        .texOffs(8, 22).addBox(-1.0F, -23.0F, -0.5F, 2.0F, 11.0F, 1.0F)
                        .texOffs(16, 22).addBox(5.0F, -20.0F, -0.5F, 2.0F, 8.0F, 1.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("abyss_mantle",
                CubeListBuilder.create().texOffs(22, 16).addBox(-10.0F, -1.0F, 2.5F, 20.0F, 18.0F, 2.0F)
                        .texOffs(44, 20).addBox(-3.0F, 1.0F, -4.0F, 6.0F, 12.0F, 2.0F),
                PartPose.ZERO);
        parts.body.addOrReplaceChild("broken_wings",
                CubeListBuilder.create().texOffs(0, 16).addBox(-16.0F, 0.0F, 3.0F, 8.0F, 18.0F, 1.0F)
                        .texOffs(0, 16).addBox(8.0F, 0.0F, 3.0F, 8.0F, 18.0F, 1.0F),
                PartPose.ZERO);
        bladeArm(parts.rightArm, "right_abyss_blade", -2.5F);
        bladeArm(parts.leftArm, "left_abyss_blade", 0.5F);
        return LayerDefinition.create(mesh, 64, 32);
    }

    private static Parts core(MeshDefinition mesh, Shape shape) {
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-shape.headWidth / 2.0F, -shape.headHeight,
                        -shape.headDepth / 2.0F, shape.headWidth, shape.headHeight, shape.headDepth),
                PartPose.offset(0.0F, shape.headY, shape.headZ));
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16).addBox(-shape.bodyWidth / 2.0F, 0.0F,
                        -shape.bodyDepth / 2.0F, shape.bodyWidth, shape.bodyHeight, shape.bodyDepth),
                PartPose.offset(0.0F, shape.bodyY, shape.bodyZ));
        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 16).addBox(-shape.armWidth, -2.0F,
                        -shape.armDepth / 2.0F, shape.armWidth, shape.armHeight, shape.armDepth),
                PartPose.offset(-shape.armX, shape.armY, shape.armZ));
        PartDefinition leftArm = root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 16).mirror().addBox(0.0F, -2.0F,
                        -shape.armDepth / 2.0F, shape.armWidth, shape.armHeight, shape.armDepth),
                PartPose.offset(shape.armX, shape.armY, shape.armZ));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16).addBox(-shape.legWidth, 0.0F,
                        -shape.legDepth / 2.0F, shape.legWidth, shape.legHeight, shape.legDepth),
                PartPose.offset(-shape.legX, shape.legY, shape.legZ));
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(0.0F, 0.0F,
                        -shape.legDepth / 2.0F, shape.legWidth, shape.legHeight, shape.legDepth),
                PartPose.offset(shape.legX, shape.legY, shape.legZ));
        PartDefinition leftTendril = root.addOrReplaceChild("left_tendril",
                CubeListBuilder.create().texOffs(56, 16).addBox(-0.5F, 0.0F, -0.5F, 1.0F,
                        shape.tendrilLength, 1.0F),
                PartPose.offset(shape.tendrilX, 11.0F, 2.8F));
        PartDefinition rightTendril = root.addOrReplaceChild("right_tendril",
                CubeListBuilder.create().texOffs(56, 16).addBox(-0.5F, 0.0F, -0.5F, 1.0F,
                        shape.tendrilLength, 1.0F),
                PartPose.offset(-shape.tendrilX, 11.0F, 2.8F));
        return new Parts(root, head, body, rightArm, leftArm, rightLeg, leftLeg, leftTendril, rightTendril);
    }

    private static void armClaw(PartDefinition arm, String name, float x) {
        arm.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(52, 24).addBox(x, 10.0F, -2.5F, 2.0F, 5.0F, 5.0F),
                PartPose.ZERO);
    }

    private static void bladeArm(PartDefinition arm, String name, float x) {
        arm.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(52, 16).addBox(x, 7.0F, -0.5F, 2.0F, 13.0F, 1.0F),
                PartPose.ZERO);
    }

    private static void lanceArm(PartDefinition arm, String name, float x) {
        arm.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(52, 16).addBox(x, 4.0F, -0.5F, 2.0F, 17.0F, 1.0F),
                PartPose.ZERO);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        body.yRot = Mth.sin(ageInTicks * 0.06F) * 0.03F;

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.1F * limbSwingAmount;
        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.5F * limbSwingAmount - 0.2F;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.5F * limbSwingAmount - 0.2F;

        if (attackTime > 0.0F) {
            float swing = Mth.sin(Mth.sqrt(attackTime) * Mth.PI);
            rightArm.xRot = -1.7F + swing * 0.5F;
            leftArm.xRot = -1.4F + swing * 0.35F;
        }

        leftTendril.xRot = 0.22F + Mth.sin(ageInTicks * 0.13F) * 0.12F;
        leftTendril.zRot = 0.18F + Mth.cos(ageInTicks * 0.09F) * 0.1F;
        rightTendril.xRot = 0.22F + Mth.cos(ageInTicks * 0.12F) * 0.12F;
        rightTendril.zRot = -0.18F + Mth.sin(ageInTicks * 0.1F) * 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private record Parts(PartDefinition root, PartDefinition head, PartDefinition body,
                         PartDefinition rightArm, PartDefinition leftArm,
                         PartDefinition rightLeg, PartDefinition leftLeg,
                         PartDefinition leftTendril, PartDefinition rightTendril) {
    }

    private static final class Shape {
        private final float headWidth;
        private final float headHeight;
        private final float headDepth;
        private final float bodyWidth;
        private final float bodyHeight;
        private final float bodyDepth;
        private final float armWidth;
        private final float armHeight;
        private final float armDepth;
        private float headY;
        private float headZ;
        private float bodyY;
        private float bodyZ;
        private float armX;
        private float armY;
        private float armZ;
        private float legWidth = 2.0F;
        private float legHeight = 12.0F;
        private float legDepth = 2.0F;
        private float legX = 1.8F;
        private float legY = 12.0F;
        private float legZ;
        private float tendrilLength = 10.0F;
        private float tendrilX = 4.0F;

        private Shape(float headWidth, float headHeight, float headDepth,
                      float bodyWidth, float bodyHeight, float bodyDepth,
                      float armWidth, float armHeight, float armDepth) {
            this.headWidth = headWidth;
            this.headHeight = headHeight;
            this.headDepth = headDepth;
            this.bodyWidth = bodyWidth;
            this.bodyHeight = bodyHeight;
            this.bodyDepth = bodyDepth;
            this.armWidth = armWidth;
            this.armHeight = armHeight;
            this.armDepth = armDepth;
        }

        private static Shape humanoid(float headWidth, float headHeight, float headDepth,
                                      float bodyWidth, float bodyHeight, float bodyDepth,
                                      float armWidth, float armHeight, float armDepth) {
            return new Shape(headWidth, headHeight, headDepth, bodyWidth, bodyHeight, bodyDepth,
                    armWidth, armHeight, armDepth);
        }

        private Shape head(float y, float z) {
            this.headY = y;
            this.headZ = z;
            return this;
        }

        private Shape body(float y, float z) {
            this.bodyY = y;
            this.bodyZ = z;
            return this;
        }

        private Shape arms(float x, float y, float z) {
            this.armX = x;
            this.armY = y;
            this.armZ = z;
            return this;
        }

        private Shape legs(float x, float y, float z) {
            this.legX = x;
            this.legY = y;
            this.legZ = z;
            return this;
        }

        private Shape tendrils(float length, float x) {
            this.tendrilLength = length;
            this.tendrilX = x;
            return this;
        }
    }
}
