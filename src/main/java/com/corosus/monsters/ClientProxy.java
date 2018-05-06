package com.corosus.monsters;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
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
    public void addItem(RegistryEvent.Register<Item> event, Item item, String name) {
        super.addItem(event, item, name);

        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Monsters.modID + ":" + name, "inventory"));
    }

    @Override
    public void addBlock(RegistryEvent.Register<Block> event, Block parBlock, String unlocalizedName, boolean creativeTab) {
        super.addBlock(event, parBlock, unlocalizedName, creativeTab);

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(parBlock), 0, new ModelResourceLocation(Monsters.modID + ":" + unlocalizedName, "inventory"));
    }

    @Override
    public void addItemBlock(RegistryEvent.Register<Item> event, Item item) {
        super.addItemBlock(event, item);

        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
