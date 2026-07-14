package com.abyssworld.client.renderer;

import com.abyssworld.AbyssWorld;
import com.abyssworld.client.model.AbyssHoundModel;
import com.abyssworld.client.model.AbyssModelLayers;
import com.abyssworld.entity.AbyssHoundEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class AbyssHoundRenderer extends MobRenderer<AbyssHoundEntity, AbyssHoundModel> {
    private static final ResourceLocation TEXTURE = texture("abyss_hound");
    private static final ResourceLocation GLOW_TEXTURE = texture("abyss_hound_glow");

    public AbyssHoundRenderer(EntityRendererProvider.Context context) {
        super(context, new AbyssHoundModel(context.bakeLayer(AbyssModelLayers.ABYSS_HOUND)), 0.72F);
        addLayer(new GlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AbyssHoundEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(AbyssHoundEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.92F, 0.92F, 0.92F);
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "textures/entity/" + name + ".png");
    }

    private static final class GlowLayer extends RenderLayer<AbyssHoundEntity, AbyssHoundModel> {
        private GlowLayer(RenderLayerParent<AbyssHoundEntity, AbyssHoundModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           AbyssHoundEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE));
            getParentModel().renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
