package com.corosus.monsters;

import CoroUtil.difficulty.UtilEntityBuffs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import CoroUtil.util.BlockCoord;
import CoroUtil.util.CoroUtilEntity;
import CoroUtil.difficulty.DynamicDifficulty;

import CoroUtil.config.ConfigHWMonsters;

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
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote) return;
		if (event.getEntity() instanceof IMob) {
			if (event.getEntity() instanceof EntityCreature) {
				World world = event.getWorld();
				EntityCreature ent = (EntityCreature) event.getEntity();
				
				//NO ENHANCED CHILDREN!
				if (ent.isChild()) return;
				
				EntityPlayer player = DynamicDifficulty.getBestPlayerForArea(world, new BlockCoord(ent));
				
				if (player != null) {
					if (ConfigHWMonsters.useBlacklistAsWhitelist) {
						if (!ConfigHWMonsters.blackListPlayers.contains(CoroUtilEntity.getName(player))) {
							return;
						}
					} else {
						if (ConfigHWMonsters.blackListPlayers.contains(CoroUtilEntity.getName(player))) {
							return;
						}
					}

					//UtilEntityBuffs.buffGeneric(world, ent, player);
					UtilEntityBuffs.buff_RollDice(world, ent, player);
					
				}
				
				
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingHurtEvent event) {
		if (event.getEntity().world.isRemote) return;
		if (event.getEntity() instanceof IMob) {
			if (event.getEntity() instanceof EntityLiving) {
				World world = event.getEntity().world;
				EntityLiving ent = (EntityLiving) event.getEntity();
				
				if (event.getSource() instanceof EntityDamageSource || event.getSource() instanceof EntityDamageSourceIndirect) {
					//test
					//System.out.println("boop!");
					//ent.motionY += 0.4D;
				}
			}
			
			
		}
	}
}
