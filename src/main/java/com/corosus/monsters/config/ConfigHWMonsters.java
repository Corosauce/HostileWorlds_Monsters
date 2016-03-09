package com.corosus.monsters.config;

import java.io.File;

import modconfig.ConfigComment;
import modconfig.IConfigCategory;

public class ConfigHWMonsters implements IConfigCategory {

	@ConfigComment("Flying is overpowered!")
	public static boolean antiAir = true;
	
	public static double antiAirLeapSpeed = 0.15D;
	
	public static int antiAirLeapRate = 40;
	
	public static int antiAirTryDist = 20;
	
	@ConfigComment("Additional scaling for dynamic difficulty based health boost")
	public static double scaleHealth = 1D;
	
	
	
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
