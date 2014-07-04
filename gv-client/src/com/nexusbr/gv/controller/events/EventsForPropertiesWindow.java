package com.nexusbr.gv.controller.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.nexusbr.gv.view.components.PropertiesWindow;

public class EventsForPropertiesWindow implements ItemListener, ActionListener, PropertyChangeListener {
	
	private PropertiesWindow propertiesWindow;
	
	public EventsForPropertiesWindow(PropertiesWindow properties){
		this.propertiesWindow = properties;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		/*
		if(e.getSource() == propertiesWindow.getCmbLayer()){
			if(propertiesWindow.getCmbLayer().getSelectedItem().toString().isEmpty())
				return;
			Table tbl = new TablesBO().getTableFromLayerName(propertiesWindow.getCmbLayer().getSelectedItem().toString());
			DefaultTableModel model = (DefaultTableModel)propertiesWindow.getTableAttributes().getModel();
			new ModelBO().populateTable(tbl, model, propertiesWindow.getRowEditor());		
		}*/
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		/*if(e.getSource() == propertiesWindow.getBtnSave()){			
			AttributesTableModel model = (AttributesTableModel) propertiesWindow.getTableAttributes().getModel();
			String layerName = propertiesWindow.getCmbLayer().getSelectedItem().toString();
			HashMap<String, String> dynAttr = new HashMap<String, String>();
			for (int i = 0; i < model.getRowCount(); i++) {
				dynAttr.put(model.getValueAt(i, 0).toString(), model.getValueAt(i, 1).toString());				
			}
			propertiesWindow.getFeature().setAttribute("layerName", layerName);
			propertiesWindow.getFeature().setAttribute("dynamicAttributes", dynAttr);
		}*/
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		/*if(e.getSource() == propertiesWindow.getCmbLayer().getSelectedItem().toString()){			
			if(propertiesWindow.getCmbLayer().getSelectedItem().toString().isEmpty())
				return;
			
			String layerName = propertiesWindow.getCmbLayer().getSelectedItem().toString();		
			Table tbl = new TablesBO().getTableFromLayerName(layerName);					
			
			DefaultTableModel model = (DefaultTableModel)propertiesWindow.getTableAttributes().getModel();
			new ModelBO().populateTable(tbl, model, propertiesWindow.getRowEditor());		
		}*/
	}

}
