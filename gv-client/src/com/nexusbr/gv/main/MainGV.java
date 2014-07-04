package com.nexusbr.gv.main;

import br.org.funcate.glue.main.Main;
import br.org.funcate.jtdk.edition.EditionController;

import com.nexusbr.gv.view.GVClient;


/**
 * This class starts the Geo vector, edition controller and Glue
 * 
 * @author Severino, Bruno de Oliveira 
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MainGV extends Main {

	@Override
	/**
	 * Start application
	 */
	public void init() {
		super.init();
		new EditionController().run();
		new GVClient();
	}
	
	public static void main(String[] args) {
		MainGV gv = new MainGV();
		gv.init();
	}
}
