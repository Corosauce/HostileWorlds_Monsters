package com.corosus.monsters.client.entity;

import com.corosus.monsters.client.model.ModelZombiePlayer;
import com.corosus.monsters.entity.EntityZombiePlayer;
import com.corosus.monsters.util.UtilProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class RenderZombiePlayer extends RenderBiped<EntityZombiePlayer> {

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

    /**
     * Not using anymore, unless no texture found
     *
     * @param entity
     * @return
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityZombiePlayer entity) {

        /**
         * First we try to just grab from loaded player infos, but there are scenarios where they could be missing:
         * - zombie has been alive for a while, player its based on isnt connected after games reloaded etc
         * - new player flies into area where player its based on isnt at
         * - so we do proper full network requests as fallback
         */

        /*NetworkPlayerInfo playerInfo = getPlayerInfo(entity);
        if (playerInfo != null) {
            //called in thread first to make sure all networking request arent needed
            return playerInfo.getLocationSkin();
        } else {
            return super.getEntityTexture(entity);
        }*/

        //return test(entity);
        return super.getEntityTexture(entity);
    }

    @Override
    protected void renderModel(EntityZombiePlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
    {
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (flag || flag1)
        {

            UtilProfile.CachedPlayerData cache = getCachedPlayerData(entitylivingbaseIn);

            /*if (!this.bindEntityTexture(entitylivingbaseIn))
            {
                return;
            }*/

            if (cache != null) {
                this.bindTexture(cache.getTexture());
            } else {
                this.bindTexture(getEntityTexture(entitylivingbaseIn));
            }

            if (flag1)
            {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            if (cache != null && cache.isSlim()) {
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

    @Override
    protected void applyRotations(EntityZombiePlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        //GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);

        if (entityLiving.risingTime < entityLiving.risingTimeMax)
        {
            float f = 1F - ((entityLiving.risingTime + partialTicks) / (float)entityLiving.risingTimeMax);
            /*float f = ((float)entityLiving.risingTime + partialTicks - 1.0F) / (float)entityLiving.risingTimeMax*//* * 1.6F*//*;
            f = MathHelper.sqrt(f);*/

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            //GlStateManager.translate(0, f * 2F, 0);
            //GlStateManager.translate(0, -f * 3F, 0);
            GlStateManager.rotate(-0F + (f * 90F)/* * this.getDeathMaxRotation(entityLiving)*/, 1.0F, 0.0F, 0.0F);
            //GlStateManager.rotate(-0F + (f * 90F)/* * this.getDeathMaxRotation(entityLiving)*/, 1.0F, 0.0F, 0.0F);
        }

        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    }

    /*public static NetworkPlayerInfo getPlayerInfo(EntityZombiePlayer entity) {
        GameProfile profile = entity.getGameProfile();

        if (profile == null) return null;

        if (profile.getId() == null && UtilProfile.getInstance().lookupNameToUUID.contains(profile.getName())) {
            //fix partial profile
            System.out.println("detected proper profile to fix for " + profile.getName());
            profile = UtilProfile.getInstance().lookupNameToUUID.get(profile.getName());
            entity.setGameProfile(profile);
        }

        if (profile.getId() != null) {
            UtilProfile.getInstance().lookupUUIDToPlayerInfo.get(profile.getId());
        }

        NetworkPlayerInfo playerInfo = null;

        if (playerInfo == null) {
            //try built in cache that is populated if that player is connected already
            if (profile.getId() != null) {
                playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(profile.getId());
            }
            if (playerInfo == null) {
                *//**
                 * send off a request and let is return a null profile for now,
                 * once thread is done and this is called again it shouldnt be null
                 *//*
                //UtilProfile.getInstance().addProfileToLookupQueue(profile);

            }
        }

        return playerInfo;
    }*/

    /*public boolean isSlim(EntityZombiePlayer entity) {
        NetworkPlayerInfo playerInfo = getPlayerInfo(entity);
        return playerInfo != null && playerInfo.getSkinType().equals("slim");
    }*/

    /*private static ResourceLocation getPlayerTexture(GameProfile profile, MinecraftProfileTexture.Type type, SkinManager.SkinAvailableCallback callBack) {
        if (profile != null && profile.getName() != null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
            if (map.containsKey(type))
                return minecraft.getSkinManager().loadSkin(map.get(type), type, callBack);
        }
        return null;
    }*/

    public static UtilProfile.CachedPlayerData getCachedPlayerData(EntityZombiePlayer entity) {
        if (entity.getGameProfile() == null) {
            return null;
        }
        String name = entity.getGameProfile().getName();
        UtilProfile.CachedPlayerData data = UtilProfile.getInstance().lookupNameToCachedData.get(name);
        //cant use .contains here because Strings
        if (data != null) {
            if (data.getTexture() == null) {
                if (data.getTemp() != null) {
                    //actually load in the texture and data if its waiting to be loaded (must done done from gl context thread)
                    data.setTexture(Minecraft.getMinecraft().getSkinManager().loadSkin(data.getTemp(), MinecraftProfileTexture.Type.SKIN, null));
                    String model = data.getTemp().getMetadata("model");
                    if (model != null) {
                        data.setSlim(model.equals("slim"));
                    }
                    RenderZombiePlayer.dbg(String.format("full data received for %s, is slim = " + data.isSlim(), name));
                } else {
                    //means thread lookup failed, could be bad username, no internet, etc
                    return null;
                }
            } else {
                //thread safe fallback?
                return data;
            }
            return data;
        } else {
            UtilProfile.getInstance().tryToAddProfileToLookupQueue(entity.getGameProfile());
        }

        return null;
    }

    /*public ResourceLocation test(EntityZombiePlayer entity) {
        GameProfile profile = entity.getGameProfile();

        if (profile == null) {
            return super.getEntityTexture(entity);
        }

        if (profile.getId() == null && UtilProfile.getInstance().lookupNameToUUID.contains(profile.getName())) {
            //fix partial profile
            dbg("detected cached proper profile to fix for " + profile.getName());
            profile = UtilProfile.getInstance().lookupNameToUUID.get(profile.getName());
            entity.setGameProfile(profile);
        }

        if (profile.getId() == null) {
            dbg("detected cached proper profile to fix for " + profile.getName());
            profile = TileEntitySkull.updateGameprofile(profile);
        }

        dbg("getting full texture data from cache or online");
        ResourceLocation loc = getPlayerTexture(profile, MinecraftProfileTexture.Type.SKIN, null);

        if (loc != null) {
            return loc;
        } else {
            return super.getEntityTexture(entity);
        }
    }*/


    public static void dbg(String str) {
        System.out.println(str);
    }
}
