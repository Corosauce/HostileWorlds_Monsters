package com.corosus.monsters.ai.tasks;

import java.util.List;

import com.corosus.monsters.config.ConfigHWMonsters;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import CoroPets.ai.ITaskInitializer;

public class TaskAntiAir extends EntityAIBase implements ITaskInitializer
{
    private EntityCreature entity = null;
    private EntityPlayer targetLastTracked = null;
    
    private int leapDelayCur = 0;
    private int leapDelayRate = 40;
    
    private boolean autoAttackTest = true;
    private boolean tryingToGrab = false;
    private boolean grabLock = false;
    
    private String detectOnGroundTime = "HW_M_detectOnGroundTime";

    public TaskAntiAir()
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
	    		
	    		long time = targetLastTracked.getEntityData().getLong(detectOnGroundTime);
	    		boolean inAirLongEnough = time + (long)ConfigHWMonsters.antiAirLeapRate < entity.worldObj.getTotalWorldTime();
	    		
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
	    			
	    			//if (tryingToGrab) {
		    			
		    			
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
	    			//}
	    			
	    		}
	    	}
    	}
    	
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
        	
        	//TODO: better generic flying detection
        	if ((!ent.capabilities.isCreativeMode || autoAttackTest)) {
        		if ((ent.capabilities.isFlying || (!ent.onGround && !ent.isInWater() && !ent.isInsideOfMaterial(Material.lava)))) {
        			if (ent.canEntityBeSeen(entity) && ent.ridingEntity == null) {
		        		double dist = ent.getDistanceToEntity(entity);
		        		if (dist < closest) {
		        			closest = dist;
		        			closestPlayer = ent;
		        		}
        			}
        		} else {
        			ent.getEntityData().setLong(detectOnGroundTime, ent.worldObj.getTotalWorldTime());
        		}
        	}
        }
    	
    	return closestPlayer;
    }
}
