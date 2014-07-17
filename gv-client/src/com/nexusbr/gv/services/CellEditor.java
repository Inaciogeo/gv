package com.nexusbr.gv.services;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class CellEditor {
	
	public CellEditor() {
		
	}
	
	@SuppressWarnings("rawtypes")
	public CmbEditor cmbColEditor(JComboBox cmb){
		return new CmbEditor(cmb);
	}
	
	public JButton btnColEditor(JTable table, int column, Icon icon) {
		ButtonColumn btnCol = new ButtonColumn(table, column, icon);
		return btnCol.getButton();
	}
			
	public class CmbEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = -8021818909797674772L;
		@SuppressWarnings("rawtypes")
		private JComboBox editorComponent;

		@SuppressWarnings("rawtypes")
		public CmbEditor(JComboBox cmb) {
			this.editorComponent = cmb;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editorComponent.setSelectedItem(value);
			return editorComponent;
		}

		public Object getCellEditorValue() {
			return editorComponent.getSelectedItem();
		}
	}
		
	public class ButtonColumn extends AbstractCellEditor  implements TableCellRenderer, TableCellEditor, ActionListener {  
		private static final long serialVersionUID = 1L;
		JTable tableLayer;  
        JButton renderButton;  
        JButton editButton;  
        String text;  
        Icon icon;
   
        public ButtonColumn(JTable table, int column, Icon icon)  
        {  
            super();  
            this.tableLayer = table;  
            renderButton = new JButton();  
            this.icon = icon;
            editButton = new JButton();  
            editButton.setContentAreaFilled(false);            
            editButton.addActionListener( this );  
            editButton.setHorizontalAlignment(SwingConstants.LEFT);
            editButton.setMargin(new Insets(0, 0, 0, 0));
            editButton.setBackground(Color.WHITE);
            editButton.setIcon(icon);
   
            TableColumnModel columnModel = table.getColumnModel();  
            columnModel.getColumn(column).setCellRenderer( this );  
            columnModel.getColumn(column).setCellEditor( this );  
        }  
   
        public Component getTableCellRendererComponent(  
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)  
        {  
            renderButton.setText( (value == null) ? "" : value.toString() );
            renderButton.setHorizontalAlignment(SwingConstants.LEFT);
            renderButton.setIcon(icon);
            renderButton.setMargin(new Insets(0, 0, 0, 0));
            renderButton.setBackground(Color.WHITE);
            
            return renderButton;  
        }  
   
        public Component getTableCellEditorComponent(  
            JTable table, Object value, boolean isSelected, int row, int column)  
        {  
            text = (value == null) ? "" : value.toString();  
            editButton.setText( text );  
            return editButton;  
        }  
   
        public Object getCellEditorValue()  
        {  
            return text;  
        }  
        
        public JButton getButton(){
        	return editButton;
        }
   
        public void actionPerformed(ActionEvent e)  
        {  
            fireEditingStopped();            
        }  
    }
}