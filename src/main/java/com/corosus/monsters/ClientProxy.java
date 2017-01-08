package com.corosus.monsters;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    
    public ClientProxy()
    {
        
    }

    @Override
    public void init(Monsters pMod)
    {
        super.init(pMod);
        
        

        
        
    }

    @Override
    public void addBlock(Block parBlock, String unlocalizedName, boolean creativeTab) {
        super.addBlock(parBlock, unlocalizedName, creativeTab);

        registerItem(Item.getItemFromBlock(parBlock), 0, new ModelResourceLocation(Monsters.modID + ":" + unlocalizedName, "inventory"));
    }

    public void registerItem(Item item, int meta, ModelResourceLocation location) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
    }
}
