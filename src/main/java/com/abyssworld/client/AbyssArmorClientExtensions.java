package com.abyssworld.client;

import com.abyssworld.client.model.AbyssArmorModel;
import com.abyssworld.client.model.AbyssModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class AbyssArmorClientExtensions {
    private AbyssArmorClientExtensions() {
    }

    public static IClientItemExtensions crystalline() {
        return new ArmorExtension(AbyssModelLayers.CRYSTALLINE_ABYSS_ARMOR);
    }

    public static IClientItemExtensions singularity() {
        return new ArmorExtension(AbyssModelLayers.SINGULARITY_ABYSS_ARMOR);
    }

    private static final class ArmorExtension implements IClientItemExtensions {
        private final ModelLayerLocation layer;
        private AbyssArmorModel model;

        private ArmorExtension(ModelLayerLocation layer) {
            this.layer = layer;
        }

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                       EquipmentSlot slot, HumanoidModel<?> original) {
            if (model == null) {
                model = new AbyssArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(layer));
            }
            model.copyFrom(original, slot);
            return model;
        }
    }
}
