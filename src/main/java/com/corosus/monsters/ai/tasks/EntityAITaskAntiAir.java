package com.corosus.monsters.ai.tasks;

import io.netty.util.HashedWheelTimer;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import CoroPets.ai.ITaskInitializer;
import CoroUtil.forge.CoroAI;
import CoroUtil.packet.PacketHelper;
import CoroUtil.world.player.DynamicDifficulty;

import com.corosus.monsters.config.ConfigHWMonsters;

public class EntityAITaskAntiAir extends EntityAIBase implements ITaskInitializer
{
    private EntityCreature entity = null;
    private EntityPlayer targetLastTracked = null;
    
    private int leapDelayCur = 0;
    private int leapDelayRate = 40;
    
    private boolean autoAttackTest = true;
    private boolean tryingToGrab = false;
    private boolean grabLock = false;
    
    private String dataPlayerLastPullDownTick = "HW_M_lastPullDownTick";

    public EntityAITaskAntiAir()
    {
        //this.setMutexBits(3);
    }
    
    @Override
    public void setEntity(EntityCreature creature) {
    	this.entity = creature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	
    	if (!ConfigHWMonsters.antiAir) return false;
    	
    	if (entity.getAttackTarget() != null || autoAttackTest) {
    		targetLastTracked = getFlyingPlayerNear();
    		return targetLastTracked != null;
    		/*if (entity.worldObj.getTotalWorldTime() % 60 == 0) {
    			return true;
    		}*/
    		
    	}
    	
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	
    	if (!ConfigHWMonsters.antiAir) return false;
    	
    	if (entity.getAttackTarget() != null || autoAttackTest) {
	    	targetLastTracked = getFlyingPlayerNear();
			return targetLastTracked != null || tryingToGrab || grabLock;
    	} else {
    		return false;
    	}
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	//System.out.println("start!");
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
    	if (entity.riddenByEntity instanceof EntityPlayer) {
			entity.riddenByEntity.mountEntity(null);
		}
    	//System.out.println("reset!");
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	
    	entity.fallDistance = 0;
    	
    	if (entity.getAttackTarget() != null || autoAttackTest) {
	    	targetLastTracked = getFlyingPlayerNear();
	    	
	    	if (targetLastTracked != null) {
	    		
	    		double dist = entity.getDistanceToEntity(targetLastTracked);
	    		
	    		//System.out.println(targetLastTracked.motionY);
	    		
	    		
	    		long time = targetLastTracked.getEntityData().getLong(DynamicDifficulty.dataPlayerDetectInAirTime);
	    		boolean inAirLongEnough = time > leapDelayRate;
	    		
	    		if (ConfigHWMonsters.antiAirType == 0) {
	    		
		    		if (entity.onGround || entity.isInWater() || entity.isInsideOfMaterial(Material.lava)) {
		    			
		    			
		    			
		    			
		    	    	if (leapDelayCur == 0 && inAirLongEnough) {
			    			
			    			double vecX = targetLastTracked.posX - entity.posX;
			    			double vecY = targetLastTracked.posY - entity.posY;
			    			double vecZ = targetLastTracked.posZ - entity.posZ;
			    			
			    			if (dist != 0) {
			    				vecX /= dist;
			    				vecY /= dist;
			    				vecZ /= dist;
			    				
			    				double speed = ConfigHWMonsters.antiAirLeapSpeed * dist;
			    				double xzAmp = 1.3D;
			    				
			    				entity.motionX = vecX * speed * xzAmp;
			    				entity.motionY = (vecY * speed) + 0.1D;
			    				entity.motionZ = vecZ * speed * xzAmp;
			    				
			    				//entity.onGround = false;
			    				
			    				leapDelayCur = leapDelayRate;
			    				
			    				tryingToGrab = true;
			    			}
		    	    	}
		    		} else {
		    			
		    			if (tryingToGrab) {
			    			
			    			
			    			if (dist < 2 || grabLock) {
			    				if (targetLastTracked.ridingEntity == null) { 
				    				targetLastTracked.mountEntity(entity);
				    				grabLock = true;
				    				if (autoAttackTest && targetLastTracked.capabilities.isFlying) {
				    					targetLastTracked.capabilities.isFlying = false;
				    				}
			    				}
			    				tryingToGrab = false;
			    			}
		    			}
		    			
		    		}
	    		} else if (ConfigHWMonsters.antiAirType == 1) {
	    			if (inAirLongEnough) {
			    		targetLastTracked.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 100, 2, false));
			    		targetLastTracked.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 100, 2, false));
			    		targetLastTracked.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 100, 2, false));
			    		
			    		if (targetLastTracked instanceof EntityPlayerMP) {
			    			EntityPlayerMP entMP = (EntityPlayerMP) targetLastTracked;
			    			long lastPullTime = targetLastTracked.getEntityData().getLong(dataPlayerLastPullDownTick);
			    			if (entMP.worldObj.getTotalWorldTime() != lastPullTime) {
			    				targetLastTracked.getEntityData().setLong(dataPlayerLastPullDownTick, entMP.worldObj.getTotalWorldTime());
				    			//entMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetLastTracked.getEntityId(), 0, -0.4D, 0));
				    			CoroAI.eventChannel.sendTo(PacketHelper.getPacketForRelativeMotion(entMP, 0, -0.4D, 0), entMP);
			    			}
			    		}
	    			}
	    		}
	    	}
    	}
    	
    	if (ConfigHWMonsters.antiAirType == 0) {
	    	if (entity.onGround || entity.isInWater() || entity.isInsideOfMaterial(Material.lava)) {
	    		if (leapDelayCur > 0) {
		    		leapDelayCur--;
	    		}
	    		grabLock = false;
		    	if (entity.riddenByEntity instanceof EntityPlayer) {
					entity.riddenByEntity.mountEntity(null);
				}
	    	}
    	}
    }
    
    public EntityPlayer getFlyingPlayerNear() {
    	
    	int findRange = ConfigHWMonsters.antiAirTryDist;
    	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.posX, entity.posY, entity.posZ);
		aabb = aabb.expand(findRange, findRange, findRange);
		List list = entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
		boolean found = false;
		double closest = 99999;
		EntityPlayer closestPlayer = null;
        for(int j = 0; j < list.size(); j++)
        {
        	EntityPlayer ent = (EntityPlayer)list.get(j);
        	
        	if (isPlayerFlying(ent)) {
        		if (ent.canEntityBeSeen(entity) && ent.ridingEntity == null) {
	        		double dist = ent.getDistanceToEntity(entity);
	        		if (dist < closest) {
	        			closest = dist;
	        			closestPlayer = ent;
	        		}
    			}
        	}
        }
    	
    	return closestPlayer;
    }
    
    public boolean isPlayerFlying(EntityPlayer player) {
    	return player.getEntityData().getLong(DynamicDifficulty.dataPlayerDetectInAirTime) > 0;
    }
}
