package com.nexusbr.gv.services;

import java.util.ResourceBundle;

import javax.swing.table.DefaultTableModel;

import com.nexusbr.gv.singleton.GVSingleton;

public class AttributesTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -7680115018571409140L;
	private String[] columnNames;

	public AttributesTableModel() {
		ResourceBundle language = GVSingleton.getInstance().getLanguage();
		columnNames = new String[]{
			language.getString("tableModel_column0.text"),
			language.getString("tableModel_column1.text")
		};
		this.setColumnIdentifiers(columnNames);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}	
	
	public void removeAll(){
		while (this.getRowCount() > 0){
			this.removeRow(getRowCount() - 1);
		}

	}

	@Override
	public boolean isCellEditable(int row, int col) {		
		if(col==0){
			return false; 
		}else
			return true;
	}

}
