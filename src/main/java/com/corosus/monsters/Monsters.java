package com.corosus.monsters;

import CoroUtil.forge.CoroUtil;
import com.corosus.monsters.world.TotemGen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import CoroUtil.config.ConfigHWMonsters;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "hw_monsters", name="hw_monsters", version="v0.1", acceptableRemoteVersions="*", dependencies="required-after:coroutil")
public class Monsters {
	
	@Mod.Instance( value = "hw_monsters" )
	public static Monsters instance;
	public static final String modID = CoroUtil.modID_HWMonsters;

    @SidedProxy(clientSide = "com.corosus.monsters.ClientProxy", serverSide = "com.corosus.monsters.CommonProxy")
    public static CommonProxy proxy;
    
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

    }
    
	@Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
		//FMLCommonHandler.instance().bus().register(new EventHandlerForge());

        proxy.init(this);

        if (ConfigHWMonsters.genTotems) {
            GameRegistry.registerWorldGenerator(new TotemGen(), 1000);
        }
    }
    
    @Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
    	//event.registerServerCommand(new CommandInvasion());
    }
    
    @Mod.EventHandler
    public void serverStart(FMLServerStartedEvent event) {
    	
    }
    
    @Mod.EventHandler
    public void serverStop(FMLServerStoppedEvent event) {
    	
    }

}
