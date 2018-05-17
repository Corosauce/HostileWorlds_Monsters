package com.corosus.monsters.entity;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityZombiePlayer extends EntityZombie {

    //client stuff
    @SideOnly(Side.CLIENT)
    public NetworkPlayerInfo playerInfo;

    public EntityZombiePlayer(World worldIn) {
        super(worldIn);
    }
}
