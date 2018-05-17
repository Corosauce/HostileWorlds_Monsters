package com.corosus.monsters.client.entity;

import com.corosus.monsters.entity.EntityZombiePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class RenderZombiePlayer extends RenderBiped<EntityZombiePlayer> {

    public RenderZombiePlayer(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelZombie(), 0.5F);

        this.addLayer(new LayerZombication(this));

        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        };
        this.addLayer(layerbipedarmor);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombiePlayer entity) {

        //TODO: TEMP, relocate, cache, lookup players properly
        if (entity.playerInfo == null) {
            //GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString("a6484c2f-cd05-460f-81d1-36e92d8f8f9e"), "Cojomax99");
            GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString("ef29f2d6-14e1-4eda-9c53-d4eac41c0062"), "PhoenixfireLune");
            entity.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(profile.getName());
            if (entity.playerInfo == null) {
                //TODO: i am misusing this, needs properties map from GameProfile with 'textures' entry etc, explore other options
                entity.playerInfo = new NetworkPlayerInfo(profile);
                //TODO: add new entry to NetHandlerPlayClient.playerInfoMap ?, or just cache it for our own purposes, might be cleaner for us

            }
        }

        if (entity.playerInfo != null) {
            //this specifically needs to be threaded, rather, called on the thread first, so this doesnt block the client thread for network use
            return entity.playerInfo.getLocationSkin();
        } else {
            return super.getEntityTexture(entity);
        }

        /*Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

        if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
        {
            return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        } else {
            return super.getEntityTexture(entity);
        }*/


    }

    @Override
    protected void renderModel(EntityZombiePlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
    {
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (flag || flag1)
        {
            if (!this.bindEntityTexture(entitylivingbaseIn))
            {
                return;
            }

            if (flag1)
            {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

            if (flag1)
            {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }
}
