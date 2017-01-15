package com.corosus.monsters.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTotem extends BlockContainer
{
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0F, 0, 0.0F, 1.0F, 1.0F, 1.0F);
	
    public BlockTotem()
    {
        super(Material.CLAY);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        return new TileEntityTotem();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

}
