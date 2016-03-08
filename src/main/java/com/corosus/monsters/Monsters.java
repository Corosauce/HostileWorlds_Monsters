package com.corosus.monsters;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = "hw_monsters", name="HW_Monsters", version="v0.1", acceptableRemoteVersions="*")
public class Monsters {
	
	@Mod.Instance( value = "hw_monsters" )
	public static Monsters instance;
	public static String modID = "hw_monsters";
    
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		//ConfigMod.addConfigFile(event, "invasionConfig", new ConfigInvasion());
    }
    
	@Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
		FMLCommonHandler.instance().bus().register(new EventHandlerForge());
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
