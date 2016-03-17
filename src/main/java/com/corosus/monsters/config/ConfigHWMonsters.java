package com.corosus.monsters.config;

import java.io.File;

import modconfig.ConfigComment;
import modconfig.IConfigCategory;

public class ConfigHWMonsters implements IConfigCategory {

	@ConfigComment("Flying is overpowered!")
	public static boolean antiAir = true;
	
	@ConfigComment("0 = leap and pull down, 1 = super evil effects and force pull down")
	public static int antiAirType = 0;
	
	public static double antiAirV2PullDownRate = 0.2D;
	
	public static double antiAirLeapSpeed = 0.15D;
	
	public static int antiAirLeapRate = 40;
	
	public static int antiAirTryDist = 20;
	
	@ConfigComment("Additional scaling for dynamic difficulty based health boost")
	public static double scaleHealth = 1D;
	
	//public static double scaleSpeed = 1D;

	@ConfigComment("Maximum speed buff allowed for max difficulty, scales based on current difficulty")
	public static double scaleSpeed = 1.3;
	
	public static double scaleSpeedCap = 0.5;
	
	@ConfigComment("0 to disable")
	public static double scaleKnockbackResistance = 1D;
	
	@ConfigComment("0 to disable")
	public static double scaleLeapAttackUseChance = 1D;
	
	@ConfigComment("0 to disable")
	public static double scaleLungeUseChance = 1D;
	
	public static double lungeDist = 7D;
	public static double lungeSpeed = 0.3D;
	public static double speedTowardsTargetLunge = 1.3D;
	public static long counterAttackDetectThreshold = 15;
	public static long counterAttackReuseDelay = 30;
	public static double counterAttackLeapSpeed = 0.8D;
	
	public static String blackListPlayers = "";
	

	
	
	
	
	@Override
	public String getConfigFileName() {
		return "HW_Monsters" + File.separator + "Misc";
	}

	@Override
	public String getCategory() {
		return "HW-M-Misc";
	}

	@Override
	public void hookUpdatedValues() {
		
	}

}
