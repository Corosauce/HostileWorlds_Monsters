package com.corosus.monsters;

import CoroUtil.util.BlockCoord;
import CoroUtil.util.CoroUtilCrossMod;
import CoroUtil.world.player.DynamicDifficulty;
import com.corosus.monsters.ai.tasks.EntityAITaskAntiAir;
import com.corosus.monsters.ai.tasks.EntityAITaskEnhancedCombat;
import com.corosus.monsters.config.ConfigHWMonsters;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Corosus on 1/8/2017.
 */
public class UtilEntityBuffs {



    public static String dataEntityBuffed_AI_CoroAI = "CoroAI_HW_Buffed_AI_CoroAI";

    //deprecating?
    public static String dataEntityBuffed_Tried = "CoroAI_HW_Buffed_AI_Tried";

    public static String dataEntityBuffed_AI_Infernal = "CoroAI_HW_Buffed_AI_Infernal";

    public static String dataEntityBuffed_Health = "CoroAI_HW_Buffed_Health";
    public static String dataEntityBuffed_Damage = "CoroAI_HW_Buffed_Damage";
    public static String dataEntityBuffed_Inventory = "CoroAI_HW_Buffed_Inventory";
    public static String dataEntityBuffed_Speed = "CoroAI_HW_Buffed_Speed";

    public static Class[] tasksToInject = new Class[] { EntityAITaskEnhancedCombat.class, EntityAITaskAntiAir.class };
    public static int[] taskPriorities = { 2, 3 };

    public static double speedCap = 0.4D;
    
    public static void buffGeneric(World world, EntityCreature ent, EntityPlayer playerClosest) {
        if (ent instanceof EntityZombie) {
						/*if (ConfigHWMonsters.antiAir) {
							BehaviorModifier.addTaskIfMissing(ent, TaskAntiAir.class, tasksToInject, taskPriorities[0]);
						}*/

            //note, there are 2 instances of attack on collide, we are targetting the first one that is for player
            //TODO: 1.10.2 verify going from EntityAIAttackOnCollide to EntityAIZombieAttack doesnt break things
            BehaviorModifier.replaceTaskIfMissing(ent, EntityAIZombieAttack.class, tasksToInject, taskPriorities);
        }

        if (!ent.getEntityData().getBoolean(dataEntityBuffed_AI_CoroAI)) {
            ent.getEntityData().setBoolean(dataEntityBuffed_AI_CoroAI, true);
            //BehaviorModifier.addTaskIfMissing(ent, TaskDigTowardsTarget.class, tasksToInject, taskPriorities[0]);

            float difficulty = DynamicDifficulty.getDifficultyScaleAverage(world, playerClosest, new BlockCoord(ent));

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

    /**
     * Randomly decide how many aspects to buff, and by how much based on difficulty and config
     *
     * @param world
     * @param ent
     * @param playerClosest
     */
    public static void buff_RollDice(World world, EntityCreature ent, EntityPlayer playerClosest) {

        /**
         * number of buffs to add depends on difficulty
         * try to add random buff, if already added, reroll to try another
         */

        //temp
        int amountOfBuffs = 2;
        int remainingBuffs = amountOfBuffs;
        Random rand = new Random();

        float difficulty = DynamicDifficulty.getDifficultyScaleAverage(world, playerClosest, new BlockCoord(ent));

        //until i go more object oriented, a buff can return false to say it didnt properly apply, and still set the tag to prevent retrying
        //- this is so it wont keep retrying that buff but wont decrement remainingBuffs

        while (remainingBuffs > 0) {
            int randVal = rand.nextInt(6);
            if (randVal == 0) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_Health)) {
                    if (buffHealth(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            } else if (randVal == 1) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_Damage)) {
                    if (buffDamage(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            } else if (randVal == 2) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_Inventory)) {
                    if (buffInventory(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            } else if (randVal == 3) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_Speed)) {
                    if (buffSpeed(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            } else if (randVal == 4) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_AI_CoroAI)) {
                    if (buffAI_CoroAI(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            } else if (randVal == 5) {
                if (!ent.getEntityData().getBoolean(dataEntityBuffed_AI_Infernal)) {
                    if (buffAI_Infernal(world, ent, playerClosest, difficulty)) {
                        remainingBuffs--;
                    }
                }
            }

            //endless loop protection - check if it has all buffs
            //keep in mind AI buff can fail and use only the tried tag
            if (ent.getEntityData().getBoolean(dataEntityBuffed_Health) &&
                    ent.getEntityData().getBoolean(dataEntityBuffed_Damage) &&
                    ent.getEntityData().getBoolean(dataEntityBuffed_Inventory) &&
                    ent.getEntityData().getBoolean(dataEntityBuffed_Speed) &&
                    ent.getEntityData().getBoolean(dataEntityBuffed_AI_CoroAI) &&
                    ent.getEntityData().getBoolean(dataEntityBuffed_AI_Infernal)) {
                break;
            }
        }


    }

    public static boolean buffHealth(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {

        double healthBoostMultiply = (/*1F + */difficulty * ConfigHWMonsters.scaleHealth);
        ent.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("health multiplier boost", healthBoostMultiply, 2));

        //group with health buff for now...
        ent.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(difficulty * ConfigHWMonsters.scaleKnockbackResistance);

        ent.getEntityData().setBoolean(dataEntityBuffed_Health, true);

        return true;
    }

    public static boolean buffDamage(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {


        ent.getEntityData().setBoolean(dataEntityBuffed_Damage, true);

        //TODO: decide if we will use this

        return false;
    }

    /**
     * Inventory using bipeds only!
     *
     * @param world
     * @param ent
     * @param playerClosest
     */
    public static boolean buffInventory(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {


        ent.getEntityData().setBoolean(dataEntityBuffed_Inventory, true);
        return true;
    }

    public static boolean buffSpeed(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {

        double curSpeed = ent.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        //avoid retardedly fast speeds
        if (curSpeed < speedCap) {
            double speedBoost = (Math.min(ConfigHWMonsters.scaleSpeedCap, difficulty * ConfigHWMonsters.scaleSpeed));
            //debug += "speed % " + speedBoost;
            ent.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("speed multiplier boost", speedBoost, 2));
        }

        ent.getEntityData().setBoolean(dataEntityBuffed_Speed, true);
        return true;
    }

    public static boolean buffAI_CoroAI(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {

        //TODO: make it so the tasks themselves dont have rand chance, have it be on if they are added so this method called means they 100% are either used or not used

        ent.getEntityData().setBoolean(dataEntityBuffed_AI_CoroAI, true);

        if (ent instanceof EntityZombie) {
						/*if (ConfigHWMonsters.antiAir) {
							BehaviorModifier.addTaskIfMissing(ent, TaskAntiAir.class, tasksToInject, taskPriorities[0]);
						}*/

            //note, there are 2 instances of attack on collide, we are targetting the first one that is for player
            //TODO: 1.10.2 verify going from EntityAIAttackOnCollide to EntityAIZombieAttack doesnt break things
            BehaviorModifier.replaceTaskIfMissing(ent, EntityAIZombieAttack.class, tasksToInject, taskPriorities);
        } else {
            return false;
        }


        return true;
    }

    public static boolean buffAI_Infernal(World world, EntityCreature ent, EntityPlayer playerClosest, float difficulty) {

        ent.getEntityData().setBoolean(dataEntityBuffed_AI_Infernal, true);

        if (!CoroUtilCrossMod.hasInfernalMobs()) return false;

        //how many modifiers do we add? based on difficulty i guess, x2!
        return CoroUtilCrossMod.infernalMobs_AddRandomModifiers(ent, (int)(difficulty * 2D));
    }
    
}
