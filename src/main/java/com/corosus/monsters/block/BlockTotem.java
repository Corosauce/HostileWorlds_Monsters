package com.corosus.monsters.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTotem extends BlockContainer
{
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.4F, 0, 0.4F, 0.6F, 0.3F, 0.6F);
	
    public BlockTotem()
    {
        super(Material.CLAY);
    }
    
    /*@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
    		BlockPos pos) {
    	return AABB;
    }*/

    public int tickRate()
    {
        return 90;
    }

    public void updateTick(World var1, int var2, int var3, int var4, Random var5) {}

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        return new TileEntityTotem();
    }

}
