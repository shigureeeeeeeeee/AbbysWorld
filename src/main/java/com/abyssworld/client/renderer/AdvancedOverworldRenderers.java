package com.abyssworld.client.renderer;

import com.abyssworld.AbyssWorld;
import com.abyssworld.client.model.AbyssModelLayers;
import com.abyssworld.client.model.AdvancedOverworldModels;
import com.abyssworld.entity.BoundaryWatcherEntity;
import com.abyssworld.entity.CrystalParasiteEntity;
import com.abyssworld.entity.FallenResearcherEntity;
import com.abyssworld.entity.ManaLeechEntity;
import com.abyssworld.entity.ShadowWalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Renderers for the production-quality Overworld abyss rigs. */
public final class AdvancedOverworldRenderers {
    private AdvancedOverworldRenderers() {
    }

    private abstract static class GlowingRenderer<T extends Mob, M extends EntityModel<T>>
            extends MobRenderer<T, M> {
        private final ResourceLocation texture;
        private final float scale;

        protected GlowingRenderer(EntityRendererProvider.Context context, M model, String textureName,
                                  float shadowRadius, float scale) {
            super(context, model, shadowRadius);
            texture = texture(textureName);
            this.scale = scale;
            addLayer(new FullBrightLayer<>(this, texture(textureName + "_glow")));
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

    public static final class ShadowWalkerRenderer
            extends GlowingRenderer<ShadowWalkerEntity, AdvancedOverworldModels.ShadowWalkerModel> {
        public ShadowWalkerRenderer(EntityRendererProvider.Context context) {
            super(context, new AdvancedOverworldModels.ShadowWalkerModel(
                    context.bakeLayer(AbyssModelLayers.SHADOW_WALKER)), "shadow_walker", 0.55F, 0.94F);
        }
    }

    public static final class ManaLeechRenderer
            extends GlowingRenderer<ManaLeechEntity, AdvancedOverworldModels.ManaLeechModel> {
        public ManaLeechRenderer(EntityRendererProvider.Context context) {
            super(context, new AdvancedOverworldModels.ManaLeechModel(
                    context.bakeLayer(AbyssModelLayers.MANA_LEECH)), "mana_leech", 0.38F, 0.76F);
        }
    }

    public static final class CrystalParasiteRenderer
            extends GlowingRenderer<CrystalParasiteEntity, AdvancedOverworldModels.CrystalParasiteModel> {
        public CrystalParasiteRenderer(EntityRendererProvider.Context context) {
            super(context, new AdvancedOverworldModels.CrystalParasiteModel(
                    context.bakeLayer(AbyssModelLayers.CRYSTAL_PARASITE)), "crystal_parasite", 0.75F, 0.9F);
        }
    }

    public static final class FallenResearcherRenderer
            extends GlowingRenderer<FallenResearcherEntity, AdvancedOverworldModels.FallenResearcherModel> {
        public FallenResearcherRenderer(EntityRendererProvider.Context context) {
            super(context, new AdvancedOverworldModels.FallenResearcherModel(
                    context.bakeLayer(AbyssModelLayers.FALLEN_RESEARCHER)), "fallen_researcher", 0.52F, 0.92F);
        }
    }

    public static final class BoundaryWatcherRenderer
            extends GlowingRenderer<BoundaryWatcherEntity, AdvancedOverworldModels.BoundaryWatcherModel> {
        public BoundaryWatcherRenderer(EntityRendererProvider.Context context) {
            super(context, new AdvancedOverworldModels.BoundaryWatcherModel(
                    context.bakeLayer(AbyssModelLayers.BOUNDARY_WATCHER)), "boundary_watcher", 1.0F, 1.02F);
        }
    }

    private static final class FullBrightLayer<T extends Mob, M extends EntityModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation glowTexture;

        private FullBrightLayer(RenderLayerParent<T, M> parent, ResourceLocation glowTexture) {
            super(parent);
            this.glowTexture = glowTexture;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                           float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                           float netHeadYaw, float headPitch) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(glowTexture));
            getParentModel().renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(AbyssWorld.MODID, "textures/entity/" + name + ".png");
    }
}
