package com.corosus.monsters.client.entity;

import com.corosus.monsters.Monsters;
import com.corosus.monsters.entity.EntityZombiePlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerZombication implements LayerRenderer<EntityZombiePlayer>
{
    private static final ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation(Monsters.modID, "textures/entity/zombification.png");
    private final RenderZombiePlayer endermanRenderer;

    public LayerZombication(RenderZombiePlayer endermanRendererIn)
    {
        this.endermanRenderer = endermanRendererIn;
    }


    @Override
    public void doRenderLayer(EntityZombiePlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
        /*GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
        int i = 61680;
        int j = 61680;
        int k = 0;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);*/
        this.endermanRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        /*Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        this.endermanRenderer.setLightmap(entitylivingbaseIn);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();*/
    }

    @Override
    public boolean shouldCombineTextures()
    {
        //for hurt damage overlay etc, see dog collar
        return true;
    }
}