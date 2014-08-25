package com.nexusbr.gv.services;

public class SelectFeatureService {
	private static String featureId;
	private static String osX;
	private static String osY;
	private static String osIP;
	
	public static String getFeatureId() {
		return featureId;
	}

	public static void setFeatureId(String featureId) {
		SelectFeatureService.featureId = featureId;
	}

	public static String getOsX() {
		return osX;
	}

	public static void setOsX(String osX) {
		SelectFeatureService.osX = osX;
	}

	public static String getOsY() {
		return osY;
	}

	public static void setOsY(String osY) {
		SelectFeatureService.osY = osY;
	}

	public static String getOsIP() {
		return osIP;
	}

	public static void setOsIP(String osIP) {
		SelectFeatureService.osIP = osIP;
	}
	
}
