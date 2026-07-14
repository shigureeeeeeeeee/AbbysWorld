package com.abyssworld.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class AbyssArmorModel extends HumanoidModel<LivingEntity> {
    private final ModelPart chestDetails;
    private final ModelPart waistDetails;
    private final ModelPart rightThighDetails;
    private final ModelPart leftThighDetails;
    private final ModelPart rightBootDetails;
    private final ModelPart leftBootDetails;

    public AbyssArmorModel(ModelPart root) {
        super(root);
        chestDetails = body.getChild("chest_details");
        waistDetails = body.getChild("waist_details");
        rightThighDetails = rightLeg.getChild("thigh_details");
        leftThighDetails = leftLeg.getChild("thigh_details");
        rightBootDetails = rightLeg.getChild("boot_details");
        leftBootDetails = leftLeg.getChild("boot_details");
    }

    public void copyFrom(HumanoidModel<?> source, EquipmentSlot slot) {
        attackTime = source.attackTime;
        riding = source.riding;
        young = source.young;
        crouching = source.crouching;
        swimAmount = source.swimAmount;
        leftArmPose = source.leftArmPose;
        rightArmPose = source.rightArmPose;
        head.copyFrom(source.head);
        hat.copyFrom(source.hat);
        body.copyFrom(source.body);
        rightArm.copyFrom(source.rightArm);
        leftArm.copyFrom(source.leftArm);
        rightLeg.copyFrom(source.rightLeg);
        leftLeg.copyFrom(source.leftLeg);

        head.visible = slot == EquipmentSlot.HEAD;
        hat.visible = false;
        body.visible = slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS;
        rightArm.visible = slot == EquipmentSlot.CHEST;
        leftArm.visible = slot == EquipmentSlot.CHEST;
        rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        chestDetails.visible = slot == EquipmentSlot.CHEST;
        waistDetails.visible = slot == EquipmentSlot.LEGS;
        rightThighDetails.visible = slot == EquipmentSlot.LEGS;
        leftThighDetails.visible = slot == EquipmentSlot.LEGS;
        rightBootDetails.visible = slot == EquipmentSlot.FEET;
        leftBootDetails.visible = slot == EquipmentSlot.FEET;
    }

    public static LayerDefinition crystallineLayer() {
        return createLayer(false);
    }

    public static LayerDefinition singularityLayer() {
        return createLayer(true);
    }

    private static LayerDefinition createLayer(boolean singularity) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        float shell = singularity ? 1.0F : 0.72F;

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(shell)),
                PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                                new CubeDeformation(shell)),
                PartPose.ZERO);
        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                new CubeDeformation(shell)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(32, 48).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                new CubeDeformation(shell)),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                new CubeDeformation(shell)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(16, 48).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                new CubeDeformation(shell)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        addHelmetDetails(head, singularity);
        addBodyDetails(body, singularity);
        addArmDetails(rightArm, true, singularity);
        addArmDetails(leftArm, false, singularity);
        addLegDetails(rightLeg, singularity);
        addLegDetails(leftLeg, singularity);
        return LayerDefinition.create(mesh, 128, 64);
    }

    private static void addHelmetDetails(PartDefinition head, boolean singularity) {
        float spread = singularity ? 5.2F : 4.6F;
        float angle = singularity ? 0.48F : 0.34F;
        head.addOrReplaceChild("brow",
                CubeListBuilder.create().texOffs(64, 0)
                        .addBox(-5.0F, -8.2F, -5.15F, 10.0F, 2.0F, 1.0F), PartPose.ZERO);
        head.addOrReplaceChild("crest",
                CubeListBuilder.create().texOffs(88, 0)
                        .addBox(-1.5F, -12.0F, -5.1F, 3.0F, 8.0F, 2.0F), PartPose.ZERO);
        head.addOrReplaceChild("left_crown",
                CubeListBuilder.create().texOffs(104, 0)
                        .addBox(-1.0F, -6.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(-spread, -6.0F, 0.0F, 0.0F, 0.0F, -angle));
        head.addOrReplaceChild("right_crown",
                CubeListBuilder.create().texOffs(112, 0)
                        .addBox(-1.0F, -6.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(spread, -6.0F, 0.0F, 0.0F, 0.0F, angle));
        if (singularity) {
            head.addOrReplaceChild("void_visor",
                    CubeListBuilder.create().texOffs(96, 8)
                            .addBox(-2.0F, -6.5F, -5.45F, 4.0F, 4.0F, 1.0F),
                    PartPose.rotation(0.0F, 0.0F, 0.785F));
        }
    }

    private static void addBodyDetails(PartDefinition body, boolean singularity) {
        PartDefinition chest = body.addOrReplaceChild("chest_details", CubeListBuilder.create(), PartPose.ZERO);
        chest.addOrReplaceChild("breastplate",
                CubeListBuilder.create().texOffs(64, 16)
                        .addBox(-5.0F, -0.5F, -3.35F, 10.0F, singularity ? 10.0F : 8.0F, 2.0F),
                PartPose.ZERO);
        chest.addOrReplaceChild("core",
                CubeListBuilder.create().texOffs(104, 16)
                        .addBox(-2.0F, 1.5F, -4.25F, 4.0F, 5.0F, 1.0F),
                PartPose.rotation(0.0F, 0.0F, singularity ? 0.785F : 0.0F));
        chest.addOrReplaceChild("spine",
                CubeListBuilder.create().texOffs(116, 16)
                        .addBox(-1.0F, 0.0F, 2.8F, 2.0F, 11.0F, 2.0F), PartPose.ZERO);

        PartDefinition waist = body.addOrReplaceChild("waist_details", CubeListBuilder.create(), PartPose.ZERO);
        waist.addOrReplaceChild("belt",
                CubeListBuilder.create().texOffs(64, 32)
                        .addBox(-4.8F, 8.0F, -2.8F, 9.6F, 4.0F, 5.6F), PartPose.ZERO);
        waist.addOrReplaceChild("front_guard",
                CubeListBuilder.create().texOffs(96, 32)
                        .addBox(-2.0F, 10.0F, -3.5F, 4.0F, 5.0F, 1.0F), PartPose.ZERO);
    }

    private static void addArmDetails(PartDefinition arm, boolean right, boolean singularity) {
        float outerX = right ? -4.0F : -2.0F;
        arm.addOrReplaceChild("pauldron",
                CubeListBuilder.create().texOffs(right ? 64 : 88, 44)
                        .addBox(outerX, -3.2F, -3.0F, 6.0F, singularity ? 5.0F : 4.0F, 6.0F),
                PartPose.rotation(0.0F, 0.0F, right ? 0.08F : -0.08F));
        arm.addOrReplaceChild("bracer",
                CubeListBuilder.create().texOffs(right ? 64 : 84, 56)
                        .addBox(right ? -3.2F : -1.8F, 5.0F, -2.6F, 5.0F, 6.0F, 5.2F),
                PartPose.ZERO);
    }

    private static void addLegDetails(PartDefinition leg, boolean singularity) {
        PartDefinition thigh = leg.addOrReplaceChild("thigh_details", CubeListBuilder.create(), PartPose.ZERO);
        thigh.addOrReplaceChild("knee",
                CubeListBuilder.create().texOffs(108, 40)
                        .addBox(-2.5F, 4.0F, -3.0F, 5.0F, singularity ? 5.0F : 4.0F, 2.0F),
                PartPose.ZERO);
        PartDefinition boot = leg.addOrReplaceChild("boot_details", CubeListBuilder.create(), PartPose.ZERO);
        boot.addOrReplaceChild("greave",
                CubeListBuilder.create().texOffs(104, 50)
                        .addBox(-2.6F, 5.0F, -2.8F, 5.2F, 7.0F, 5.6F), PartPose.ZERO);
        boot.addOrReplaceChild("toe",
                CubeListBuilder.create().texOffs(96, 56)
                        .addBox(-2.6F, 9.0F, -4.2F, 5.2F, 3.0F, 3.0F), PartPose.ZERO);
    }
}
