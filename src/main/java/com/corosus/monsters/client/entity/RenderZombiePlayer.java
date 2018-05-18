package com.corosus.monsters.client.entity;

import com.corosus.monsters.client.model.ModelZombiePlayer;
import com.corosus.monsters.entity.EntityZombiePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RenderZombiePlayer extends RenderBiped<EntityZombiePlayer> {

    public static ConcurrentHashMap<UUID, NetworkPlayerInfo> lookupUUIDToPlayerInfo = new ConcurrentHashMap<>();

    public ModelZombiePlayer modelPlayerThin = new ModelZombiePlayer(0.0F, true);

    public RenderZombiePlayer(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelZombiePlayer(0.0F, false), 0.5F);

        this.getMainModel().isChild = false;
        modelPlayerThin.isChild = false;

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

        /**
         * First we try to just grab from loaded player infos, but there are scenarios where they could be missing:
         * - zombie has been alive for a while, player its based on isnt connected after games reloaded etc
         * - new player flies into area where player its based on isnt at
         */

        //TODO: TEMP, relocate, cache, lookup players properly

        NetworkPlayerInfo playerInfo = getPlayerInfo(entity);
        if (playerInfo != null) {
            //this specifically needs to be threaded, rather, called on the thread first, so this doesnt block the client thread for network use
            return playerInfo.getLocationSkin();
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

            if (isSlim(entitylivingbaseIn)) {
                modelPlayerThin.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            } else {
                this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            }

            if (flag1)
            {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }

    public static NetworkPlayerInfo getPlayerInfo(EntityZombiePlayer entity) {
        GameProfile profile = entity.getGameProfile();

        //temp
        //profile = new GameProfile(UUIDTypeAdapter.fromString("ef29f2d6-14e1-4eda-9c53-d4eac41c0062"), "PhoenixfireLune");
        //profile = new GameProfile(UUIDTypeAdapter.fromString("e0bc7f7a-0d68-4e85-bbc4-8bd17e52e9e5"), "Corosus");

        if (profile == null) return null;

        NetworkPlayerInfo playerInfo = lookupUUIDToPlayerInfo.get(profile.getId());

        if (playerInfo == null) {

            //try built in cache that is populated if that player is connected
            playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(profile.getId());
            if (playerInfo == null) {
                //fallback to actually sending a request for the data
                //TODO: !!!!!!!!!!!!!!! thread this, it does network requests
                GameProfile fullProfile = Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);
                playerInfo = new NetworkPlayerInfo(fullProfile);
                //TODO: add new entry to NetHandlerPlayClient.playerInfoMap ?, or just cache it for our own purposes, might be cleaner for us

            }

            if (playerInfo != null) {
                lookupUUIDToPlayerInfo.put(profile.getId(), playerInfo);
            }
        }

        return playerInfo;
    }

    public boolean isSlim(EntityZombiePlayer entity) {
        NetworkPlayerInfo playerInfo = getPlayerInfo(entity);
        return playerInfo != null && playerInfo.getSkinType().equals("slim");
    }
}
