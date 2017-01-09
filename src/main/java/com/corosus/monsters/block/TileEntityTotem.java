package com.corosus.monsters.block;

import CoroUtil.util.CoroUtilCrossMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TileEntityTotem extends TileEntity implements ITickable
{

	@Override
    public void update()
    {
    	if (!worldObj.isRemote) {
    		
    		if (worldObj.getTotalWorldTime() % 40 == 0) {
                BlockPos pos = getPos();
                EntityPlayer player = worldObj.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 30, false);
                if (player != null) {
                    List<EntityLiving> listEnts = getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()).expand(16, 8, 16));
                    if (listEnts.size() < 2) {
                        EntityZombie ent = new EntityZombie(getWorld());
                        ent.setPosition(this.getPos().getX() + 0.5D, this.getPos().getY() + 1.5D, this.getPos().getZ() + 0.5D);
                        getWorld().spawnEntityInWorld(ent);
                        ent.onInitialSpawn(worldObj.getDifficultyForLocation(getPos()), null);
                        String listMods = "";
                        for (String mod : CoroUtilCrossMod.listModifiers) {
                            listMods += mod + " ";
                        }
                        //CoroUtilCrossMod.infernalMobs_AddModifiers((EntityLivingBase) ent, listMods);
                    } else {
                        System.out.println("ents around: " + listEnts.size());
                    }
                }
    		}
    	}
    }
    
    /*@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
    	return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 3, getPos().getZ() + 1);
    }*/

    public NBTTagCompound writeToNBT(NBTTagCompound var1)
    {
        return super.writeToNBT(var1);
    }

    public void readFromNBT(NBTTagCompound var1)
    {
        super.readFromNBT(var1);

    }
}
