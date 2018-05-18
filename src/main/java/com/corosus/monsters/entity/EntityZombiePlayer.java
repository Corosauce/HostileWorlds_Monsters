package com.corosus.monsters.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;

public class EntityZombiePlayer extends EntityZombie implements IEntityAdditionalSpawnData {

    public GameProfile gameProfile;

    public EntityZombiePlayer(World worldIn) {
        super(worldIn);
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.enablePersistence();

        //temp
        //GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString("a6484c2f-cd05-460f-81d1-36e92d8f8f9e"), "Cojomax99");
        GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString("ef29f2d6-14e1-4eda-9c53-d4eac41c0062"), "PhoenixfireLune");
        //profile = new GameProfile(UUIDTypeAdapter.fromString("e0bc7f7a-0d68-4e85-bbc4-8bd17e52e9e5"), "Corosus");
        setGameProfile(profile);

        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        String playerName = compound.getString("playerName");
        String playerUUID = compound.getString("playerUUID");
        gameProfile = new GameProfile(UUIDTypeAdapter.fromString(playerUUID), playerName);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (compound != null) {
            compound.setString("playerName", gameProfile.getName());
            compound.setString("playerUUID", gameProfile.getId().toString());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (gameProfile != null) {
            ByteBufUtils.writeUTF8String(buffer, gameProfile.getName());
            ByteBufUtils.writeUTF8String(buffer, gameProfile.getId().toString());
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            String playerName = ByteBufUtils.readUTF8String(additionalData);
            String playerUUID = ByteBufUtils.readUTF8String(additionalData);
            gameProfile = new GameProfile(UUIDTypeAdapter.fromString(playerUUID), playerName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
