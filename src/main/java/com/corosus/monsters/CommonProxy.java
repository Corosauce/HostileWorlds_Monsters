package com.corosus.monsters;

import com.corosus.monsters.block.BlockTotem;
import com.corosus.monsters.block.TileEntityTotem;
import com.corosus.monsters.entity.EntityZombiePlayer;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Monsters.modID)
public class CommonProxy implements IGuiHandler
{

    public static final String totem_name = "totem";

    @GameRegistry.ObjectHolder(Monsters.modID + ":" + totem_name)
    public static Block blockTotem;

    public CommonProxy()
    {
    }

    public void init(Monsters pMod)
    {
        addMapping(EntityZombiePlayer.class, "zombie_player", 0, 64, 1, true);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        return null;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Monsters.proxy.addBlock(event, new BlockTotem(), TileEntityTotem.class, totem_name);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Monsters.proxy.addItemBlock(event, new ItemBlock(blockTotem).setRegistryName(blockTotem.getRegistryName()));
    }

    public String getNameUnlocalized(String name) {
        return Monsters.modID + "." + name;
    }

    public String getNameDomained(String name) {
        return Monsters.modID + ":" + name;
    }

    public void addBlock(RegistryEvent.Register<Block> event, Block block, Class tEnt, String unlocalizedName) {
        addBlock(event, block, tEnt, unlocalizedName, true);
    }

    public void addBlock(RegistryEvent.Register<Block> event, Block block, Class tEnt, String unlocalizedName, boolean creativeTab) {
        addBlock(event, block, unlocalizedName, creativeTab);
        GameRegistry.registerTileEntity(tEnt, getNameDomained(unlocalizedName));
    }

    public void addBlock(RegistryEvent.Register<Block> event, Block parBlock, String unlocalizedName) {
        addBlock(event, parBlock, unlocalizedName, true);
    }

    public void addBlock(RegistryEvent.Register<Block> event, Block parBlock, String unlocalizedName, boolean creativeTab) {
        parBlock.setUnlocalizedName(getNameUnlocalized(unlocalizedName));
        parBlock.setRegistryName(getNameDomained(unlocalizedName));

        parBlock.setCreativeTab(CreativeTabs.MISC);

        if (event != null) {
            event.getRegistry().register(parBlock);
        }
    }

    public void addItemBlock(RegistryEvent.Register<Item> event, Item item) {
        event.getRegistry().register(item);
    }

    public void addItem(RegistryEvent.Register<Item> event, Item item, String name) {
        item.setUnlocalizedName(getNameUnlocalized(name));
        item.setRegistryName(getNameDomained(name));

        item.setCreativeTab(CreativeTabs.MISC);

        if (event != null) {
            event.getRegistry().register(item);
        }
    }

    public void addMapping(Class par0Class, String par1Str, int entityId, int distSync, int tickRateSync, boolean syncMotion) {
        EntityRegistry.registerModEntity(new ResourceLocation(Monsters.modID, par1Str), par0Class, par1Str, entityId, Monsters.instance, distSync, tickRateSync, syncMotion);
    }
}
