package com.corosus.monsters.util;

import com.corosus.monsters.client.entity.RenderZombiePlayer;
import com.corosus.monsters.entity.EntityZombiePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UtilProfile implements Runnable {

    private static UtilProfile instance;

    public static UtilProfile getInstance() {
        if (instance == null) {
            instance = new UtilProfile();
            (new Thread(instance, "Player Profile Data Request Thread")).start();
        }
        return instance;
    }

    /*public ConcurrentHashMap<UUID, NetworkPlayerInfo> lookupUUIDToPlayerInfo = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, GameProfile> lookupNameToUUID = new ConcurrentHashMap<>();*/
    public ConcurrentLinkedQueue<GameProfile> listProfileRequests = new ConcurrentLinkedQueue<>();

    public ConcurrentHashMap<UUID, CachedPlayerData> lookupUUIDToCachedData = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, CachedPlayerData> lookupNameToCachedData = new ConcurrentHashMap<>();

    public class CachedPlayerData {
        //full profile
        private GameProfile profile;
        private ResourceLocation texture;
        private boolean isSlim = false;

        private MinecraftProfileTexture temp;

        public CachedPlayerData(GameProfile profile) {
            this.profile = profile;
        }

        public GameProfile getProfile() {
            return profile;
        }

        public void setProfile(GameProfile profile) {
            this.profile = profile;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public void setTexture(ResourceLocation texture) {
            this.texture = texture;
        }

        public boolean isSlim() {
            return isSlim;
        }

        public void setSlim(boolean slim) {
            isSlim = slim;
        }

        public MinecraftProfileTexture getTemp() {
            return temp;
        }

        public void setTemp(MinecraftProfileTexture temp) {
            this.temp = temp;
        }
    }

    /*public class ProfileCallback implements SkinManager.SkinAvailableCallback {

        public GameProfile profile;
        public EntityZombiePlayer entity;

        public ProfileCallback(EntityZombiePlayer entity, GameProfile profile) {
            this.entity = entity;
            this.profile = profile;
        }

        @Override
        public void skinAvailable(MinecraftProfileTexture.Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
            String skinType = "default";
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                skinType = map.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
            }


        }
    }*/

    @Override
    public void run() {
        while (true) {
            Iterator<GameProfile> it = listProfileRequests.iterator();
            while (it.hasNext()) {
                GameProfile profile = it.next();
                setupProfileData(profile);
                it.remove();
            }
            try {
                Thread.sleep(200);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

            }
        }
    }

    public void tryToAddProfileToLookupQueue(GameProfile profile) {
        if (!listProfileRequests.contains(profile)) {
            RenderZombiePlayer.dbg("requesting data for: " + profile.getName());
            listProfileRequests.add(profile);
        }
    }

    /**
     * Does the networking required to get the players proper skin info and texture
     *
     * @param profile
     */
    /*public void setupProfileDataOld(GameProfile profile) {
        if (profile.getId() == null) {
            profile = TileEntitySkull.updateGameprofile(profile);
            System.out.println("got updated profile for " + profile.getName() + ", uuid: " + profile.getId());
        }
        GameProfile fullProfile = Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);
        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(fullProfile);
        System.out.println("got full profile for " + profile.getName() + ", uuid: " + profile.getId() + ", properties size: " + playerInfo.getGameProfile().getProperties().size());
        //this makes sure the rest of the network request is done
        playerInfo.getLocationSkin();
        lookupUUIDToPlayerInfo.put(profile.getId(), playerInfo);
        lookupNameToUUID.put(profile.getName(), profile);
    }*/

    public void setupProfileData(GameProfile profile) {
        try {
            /*if (profile.getId() == null) {
                //gets profile uuid from cache if available
                profile = TileEntitySkull.updateGameprofile(profile);
                RenderZombiePlayer.dbg("got updated profile for " + profile.getName() + ", uuid: " + profile.getId());
            }*/

            //TileEntitySkull.updateGameprofile can change name to correct casing, this would break lookup
            String originalLookupName = profile.getName();

            //this does more than just get uuid, needs to run every time
            profile = TileEntitySkull.updateGameprofile(profile);
            RenderZombiePlayer.dbg("got updated profile for " + originalLookupName + " (" + profile.getName() + ")" + ", uuid: " + profile.getId());

            //make sure network or cache got what it needed
            if (profile != null) {

                CachedPlayerData data = new CachedPlayerData(profile);

                Minecraft minecraft = Minecraft.getMinecraft();
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
                MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.SKIN;
                if (map.containsKey(type)){
                    RenderZombiePlayer.dbg("set temp data to load from gl context");
                    data.setTemp(map.get(type));

                    lookupNameToCachedData.put(originalLookupName, data);
                    lookupUUIDToCachedData.put(profile.getId(), data);
                } else {
                    //happens if a bad name is used, eg one with spaces
                    RenderZombiePlayer.dbg("error getting profile texture map data for name " + originalLookupName);
                    lookupNameToCachedData.put(originalLookupName, data);
                    lookupUUIDToCachedData.put(profile.getId(), data);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
