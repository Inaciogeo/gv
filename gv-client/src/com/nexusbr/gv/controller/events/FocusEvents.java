package com.nexusbr.gv.controller.events;

import java.awt.Color;
import java.awt.event.FocusListener;

public class FocusEvents implements FocusListener{	

	@Override
	public void focusGained(java.awt.event.FocusEvent e) {
		e.getComponent().setBackground(new Color(255, 255, 240));
	}


	@Override
	public void focusLost(java.awt.event.FocusEvent e) {
		e.getComponent().setBackground(Color.WHITE);
	}
	
}
