package com.corosus.monsters;

import modconfig.ConfigMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import com.corosus.monsters.config.ConfigHWMonsters;

@Mod(modid = "hw_monsters", name="HW_Monsters", version="v0.1", acceptableRemoteVersions="*", dependencies="required-after:coroutil")
public class Monsters {
	
	@Mod.Instance( value = "hw_monsters" )
	public static Monsters instance;
	public static String modID = "hw_monsters";
    
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		ConfigMod.addConfigFile(event, "HWMonstersMisc", new ConfigHWMonsters());
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
