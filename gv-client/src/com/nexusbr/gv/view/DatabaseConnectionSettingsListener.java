package com.nexusbr.gv.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.org.funcate.glue.model.Database;
import br.org.funcate.glue.model.UserType;
import br.org.funcate.glue.service.utils.DatabaseConnection;

public class DatabaseConnectionSettingsListener implements ActionListener {
	
	private DatabaseConnectionSettings connectionDB_UI;
	
	
	public DatabaseConnectionSettingsListener(DatabaseConnectionSettings dbSettings){
		
		this.connectionDB_UI = dbSettings;
		//this.ConnectionsDBController = new DatabaseConnectionSettingsController(this.connectionDB_UI);
		Database db = DatabaseConnection.getDatabase(UserType.ADMIN.toString());
		if(db != null){
			this.connectionDB_UI.setUser(db.getUser());
			this.connectionDB_UI.setPassword(db.getPassword());
			this.connectionDB_UI.setPort(db.getPort().toString());
			this.connectionDB_UI.setDbName(db.getDatabase());
			this.connectionDB_UI.setHost(db.getHost());
		}
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == connectionDB_UI.getBtnConectar()){
			this.connectionDB_UI.dispose();
			this.connectionDB_UI.connect();
		

			
			//Database db = new Database();
			//db.setDatabase(connectionDB_UI.getDbName().getText());
			//db.setDbType(2);
			//db.setHost(connectionDB_UI.getHost().getText());
			//db.setPassword(connectionDB_UI.getPassword().getText());
			//db.setPort(Integer.valueOf(connectionDB_UI.getPort().getText()));
			//db.setUser(connectionDB_UI.getUser().getText());
			//ConnectionsDBController.setDatabase(db);
			//	this.connectionDB_UI.dispose();
		
		}
		
		if(e.getSource() == connectionDB_UI.getBtnCancelar()){
			this.connectionDB_UI.dispose();
		}
		
	}

}
