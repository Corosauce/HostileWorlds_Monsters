package com.corosus.monsters;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import CoroUtil.util.BlockCoord;
import CoroUtil.util.CoroUtilEntity;
import CoroUtil.world.player.DynamicDifficulty;

import com.corosus.monsters.ai.tasks.EntityAITaskAntiAir;
import com.corosus.monsters.ai.tasks.EntityAITaskEnhancedCombat;
import com.corosus.monsters.config.ConfigHWMonsters;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class EventHandlerForge {
	
	public Class[] tasksToInject = new Class[] { EntityAITaskEnhancedCombat.class, EntityAITaskAntiAir.class };
	public int[] taskPriorities = { 2, 3 };
	
	public static double speedCap = 0.4D;
	
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
		if (event.entity.worldObj.isRemote) return;
		if (event.entity instanceof IMob) {
			if (event.entity instanceof EntityCreature) {
				World world = event.world;
				EntityCreature ent = (EntityCreature) event.entity;
				
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
					
					if (ent instanceof EntityZombie) {
						/*if (ConfigHWMonsters.antiAir) {
							BehaviorModifier.addTaskIfMissing(ent, TaskAntiAir.class, tasksToInject, taskPriorities[0]);
						}*/
						
						//note, there are 2 instances of attack on collide, we are targetting the first one that is for player
						BehaviorModifier.replaceTaskIfMissing(ent, EntityAIAttackOnCollide.class, tasksToInject, taskPriorities);
					}
					
					if (!ent.getEntityData().getBoolean(BehaviorModifier.dataEntityEnhanced)) {
						ent.getEntityData().setBoolean(BehaviorModifier.dataEntityEnhanced, true);
						//BehaviorModifier.addTaskIfMissing(ent, TaskDigTowardsTarget.class, tasksToInject, taskPriorities[0]);
						
						float difficulty = DynamicDifficulty.getDifficultyScaleAverage(world, player, new BlockCoord(ent));
						
						/**
						 * The mathematical behavior is as follows: 
						 * Operation 0: Increment X by Amount, 
						 * Operation 1: Increment Y by X * Amount, 
						 * Operation 2: Y = Y * (1 + Amount) (equivalent to Increment Y by Y * Amount). 
						 * The game first sets X = Base, then executes all Operation 0 modifiers, then sets Y = X, 
						 * then executes all Operation 1 modifiers, and finally executes all Operation 2 modifiers.
						 */
						
						float maxHealthClean = Math.round(ent.getMaxHealth() * 1000F) / 1000F;
						//System.out.println("health max before: " + maxHealthClean);
						
						double healthBoostMultiply = (double)(/*1F + */difficulty * ConfigHWMonsters.scaleHealth);
						ent.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("health multiplier boost", healthBoostMultiply, 2));
						
						//chance to ignore knockback based on difficulty
						ent.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(difficulty * ConfigHWMonsters.scaleKnockbackResistance);
						
						String debug = "";
						
						double curSpeed = ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
						//avoid retardedly fast speeds
						if (curSpeed < speedCap) {
							double speedBoost = (Math.min(ConfigHWMonsters.scaleSpeedCap, difficulty * ConfigHWMonsters.scaleSpeed));
							debug += "speed % " + speedBoost;
							ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(new AttributeModifier("speed multiplier boost", speedBoost, 2));
						}
						
						debug += ", new speed: " + ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
						//System.out.println("mobs final speed: " + ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
						//System.out.println("difficulty: " + difficulty);
						//System.out.println("hb %: " + healthBoostMultiply);
						maxHealthClean = Math.round(ent.getMaxHealth() * 1000F) / 1000F;
						//System.out.println("health max: " + maxHealthClean);
						
						debug += ", health boost: " + healthBoostMultiply;
						
						ent.setHealth(ent.getMaxHealth());
						
						debug += ", new health: " + maxHealthClean;
						
						//System.out.println(debug);
					}
					
				}
				
				
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingHurtEvent event) {
		if (event.entity.worldObj.isRemote) return;
		if (event.entity instanceof IMob) {
			if (event.entity instanceof EntityLiving) {
				World world = event.entity.worldObj;
				EntityLiving ent = (EntityLiving) event.entity;
				
				if (event.source instanceof EntityDamageSource || event.source instanceof EntityDamageSourceIndirect) {
					//test
					//System.out.println("boop!");
					//ent.motionY += 0.4D;
				}
			}
			
			
		}
	}
}
