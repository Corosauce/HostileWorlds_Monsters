package com.corosus.monsters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class EventHandlerForge {
	
	@SubscribeEvent
	public void tickServer(ServerTickEvent event) {
		
		if (event.phase == Phase.START) {
			
			World world = DimensionManager.getWorld(0);
			if (world != null) {
				if (world.getTotalWorldTime() % 20 == 0) {
					for (Object player : world.playerEntities) {
						tickPlayer((EntityPlayer)player);
					}
				}
			}
		}
	}
	
	/**
	 * Ticked every 20 ticks
	 * 
	 * @param player
	 */
	public void tickPlayer(EntityPlayer player) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
