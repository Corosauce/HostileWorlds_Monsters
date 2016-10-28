package com.corosus.monsters;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIZombieAttack;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import CoroUtil.util.BlockCoord;
import CoroUtil.util.CoroUtilEntity;
import CoroUtil.world.player.DynamicDifficulty;

import com.corosus.monsters.ai.tasks.EntityAITaskAntiAir;
import com.corosus.monsters.ai.tasks.EntityAITaskEnhancedCombat;
import com.corosus.monsters.config.ConfigHWMonsters;

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
		if (event.getEntity().worldObj.isRemote) return;
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
					
					if (ent instanceof EntityZombie) {
						/*if (ConfigHWMonsters.antiAir) {
							BehaviorModifier.addTaskIfMissing(ent, TaskAntiAir.class, tasksToInject, taskPriorities[0]);
						}*/
						
						//note, there are 2 instances of attack on collide, we are targetting the first one that is for player
						//TODO: 1.10.2 verify going from EntityAIAttackOnCollide to EntityAIZombieAttack doesnt break things
						BehaviorModifier.replaceTaskIfMissing(ent, EntityAIZombieAttack.class, tasksToInject, taskPriorities);
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
						ent.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("health multiplier boost", healthBoostMultiply, 2));
						
						//chance to ignore knockback based on difficulty
						ent.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(difficulty * ConfigHWMonsters.scaleKnockbackResistance);
						
						String debug = "";
						
						double curSpeed = ent.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
						//avoid retardedly fast speeds
						if (curSpeed < speedCap) {
							double speedBoost = (Math.min(ConfigHWMonsters.scaleSpeedCap, difficulty * ConfigHWMonsters.scaleSpeed));
							debug += "speed % " + speedBoost;
							ent.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("speed multiplier boost", speedBoost, 2));
						}
						
						debug += ", new speed: " + ent.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
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
		if (event.getEntity().worldObj.isRemote) return;
		if (event.getEntity() instanceof IMob) {
			if (event.getEntity() instanceof EntityLiving) {
				World world = event.getEntity().worldObj;
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
