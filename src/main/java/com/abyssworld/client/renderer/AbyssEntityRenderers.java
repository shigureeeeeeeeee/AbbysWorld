package com.abyssworld.client.renderer;

import com.abyssworld.client.model.AbyssMonsterModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;

public final class AbyssEntityRenderers {
    private AbyssEntityRenderers() {
    }

    public static class AbyssMonster<T extends Monster> extends MobRenderer<T, AbyssMonsterModel<T>> {
        private final ResourceLocation texture;
        private final float scale;

        public AbyssMonster(EntityRendererProvider.Context context, ResourceLocation texture,
                            ModelLayerLocation modelLayer, float scale) {
            super(context, new AbyssMonsterModel<>(context.bakeLayer(modelLayer)), 0.5F * scale);
            this.texture = texture;
            this.scale = scale;
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return texture;
        }

        @Override
        protected void scale(T entity, PoseStack poseStack, float partialTick) {
            poseStack.scale(scale, scale, scale);
        }
    }
}
