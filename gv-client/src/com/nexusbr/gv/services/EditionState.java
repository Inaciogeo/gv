package com.nexusbr.gv.services;

public class EditionState {
	public static boolean edited;
	public static boolean save;

	public EditionState() {
		EditionState.setEdited(false);
	}

	public static boolean isEdited() {
		return edited;
	}

	public static void setEdited(boolean edited) {
		EditionState.edited = edited;
	}

	public static boolean isSave() {
		return save;
	}

	public static void setSave(boolean save) {
		EditionState.save = save;
	}		
	
}
