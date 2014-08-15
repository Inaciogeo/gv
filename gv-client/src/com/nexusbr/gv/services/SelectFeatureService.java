package com.nexusbr.gv.services;


public class SelectFeatureService {
	private static String featureId;

	public static String getFeatureId() {
		return featureId;
	}

	public static void setFeatureId(String featureId) {
		SelectFeatureService.featureId = featureId;
	}
	
}
