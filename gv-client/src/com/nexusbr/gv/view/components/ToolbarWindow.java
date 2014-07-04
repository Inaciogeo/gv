package com.nexusbr.gv.view.components;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.glue.view.MainPanel;

import com.nexusbr.gv.controller.events.EventsForToolbarWindow;
import com.nexusbr.gv.singleton.GVSingleton;
import com.nexusbr.gv.view.GVClient;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JSeparator;

/**
 * Provides Components to simplify the code.
 * This class controll all things related to components, in their creation or changes.
 *
 * @author Severino, Bruno de Oliveira
 * @version 1.0
 */
public class ToolbarWindow extends JPanel{

	private static final long serialVersionUID = -5648980670837244854L;
	private EventsForToolbarWindow events;
	private PropertiesWindow frameProperties;

	private JToolBar toolMain = new JToolBar();
	private ButtonGroup btnGroup = new ButtonGroup();

	private JButton btnDelete = new JButton();
	private JButton btnSave = new JButton();
	private JButton btnUndo = new JButton();
	private JButton btnRedo = new JButton();    
	private JButton btnProperties= new JButton();
	private JButton btnAddLayers = new JButton();
	private JToggleButton btnSelectGeom = new JToggleButton();
	private JToggleButton btnMoveGeom = new JToggleButton();
	private JToggleButton btnEditPath = new JToggleButton();    
	private JToggleButton btnDNetwork = new JToggleButton();
	private JToggleButton btnInsPoint = new JToggleButton();
	private JToggleButton btnDLine = new JToggleButton();
	private JToggleButton btnDPoly = new JToggleButton();
	private JButton btnDashed1 = new JButton();
	private JButton btnDashed2 = new JButton();
	private JButton btnDashed3 = new JButton();
	private JButton btnDashed4 = new JButton();
	private JButton btnDashed5 = new JButton();
	private JToggleButton tglbtnEdition;
	private JSeparator separator;
	
	/**
	 * Creates the ToolbarCreator.
	 */
	public ToolbarWindow() 
	{  
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBackground(Color.white);
		this.setVisible(true);

		events = new EventsForToolbarWindow(this);
		tglbtnEdition = new JToggleButton("");
		
		tglbtnEdition.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(tglbtnEdition.isSelected()){
//					GVClient gv = new GVClient();
//					Thread threadDaBarra = new Thread(MainPanel.getInstance());
//				    threadDaBarra.start();
//				    
//					gv.initFeatures();
//					threadDaBarra.stop();
//					MainPanel.getInstance();
//					MainPanel.lblTextoLoading.setVisible(false);
					tglbtnEdition.setToolTipText("Desligar Edição");
					tglbtnEdition.setIcon(new ImageIcon(ToolbarWindow.class.getResource("/com/nexusbr/gv/images/btn_hide_edit.png")));
				}else{
					tglbtnEdition.setToolTipText("Ligar Edição");
					tglbtnEdition.setIcon(new ImageIcon(ToolbarWindow.class.getResource("/com/nexusbr/gv/images/btn_show_edit.png")));
				}
			}
		});
		tglbtnEdition.setIcon(new ImageIcon(ToolbarWindow.class.getResource("/com/nexusbr/gv/images/btn_show_edit.png")));
		tglbtnEdition.setToolTipText("Ligar Edição");
		tglbtnEdition.setBackground(new Color(255, 255, 255));
		//add(tglbtnEdition);
		
		separator = new JSeparator();
		//add(separator);
	}
	
	/**
	 * create toolbar
	 */
	public void createToolBar(){    	
		frameProperties = new PropertiesWindow();

		toolMain.setAlignmentX(Component.LEFT_ALIGNMENT);		
		toolMain.setBackground(Color.WHITE);
		toolMain.setName(GVSingleton.getInstance().getLanguage().getString("toolMain.text"));
		toolMain.setRollover(true);
		toolMain.setEnabled(true);
		this.add(toolMain);

		//BTN PROPERTIES
		btnProperties.setEnabled(false);
		btnProperties.setBorderPainted(false);    	
		btnProperties.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnProperties.toolTipText")
				);
		btnProperties.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/config.gif"))
				)
				));
		btnProperties.addActionListener(events);

		//BTN ADD LAYERS
		btnAddLayers.setEnabled(false);
		btnAddLayers.setBorderPainted(false);    	
		btnAddLayers.setToolTipText("Add Layer");
		btnAddLayers.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/addLayers.gif"))
				)
				));
		btnAddLayers.addActionListener(events);

		//BTN PROPERTIES
		btnSave.setEnabled(false);
		btnSave.setBorderPainted(false);    	
		btnSave.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnSave.toolTipText")
				);
		btnSave.setIcon(new ImageIcon(
				(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/save.gif"))
						)
				));
		btnSave.addActionListener(events);

		//BTN PROPERTIES
		btnDelete.setEnabled(false);
		btnDelete.setBorderPainted(false);    	
		btnDelete.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnDelete.toolTipText")
				);
		btnDelete.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/delete.gif"))
				)
				));
		btnDelete.addActionListener(events);

		//BTN PROPERTIES
		btnUndo.setEnabled(false);
		btnUndo.setBorderPainted(false);    	
		btnUndo.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnUndo.toolTipText")
				);
		btnUndo.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/undo.gif"))
				)
				));
		btnUndo.addActionListener(events);

		//BTN PROPERTIES
		btnRedo.setEnabled(false);
		btnRedo.setBorderPainted(false);    	
		btnRedo.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnRedo.toolTipText")
				);
		btnRedo.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/redo.gif"))
				)
				));
		btnRedo.addActionListener(events);

		//BTN SELECT GEOEMETRY
		btnSelectGeom.setEnabled(false);
		btnSelectGeom.setBorderPainted(false);
		btnSelectGeom.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnSelectGeom.toolTipText"));
		btnSelectGeom.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/selectGeom.gif")))));
		btnSelectGeom.addActionListener(events);

		//BTN EDITH PATH
//		btnEditPath.setEnabled(false);
//		btnEditPath.setBorderPainted(false);
//		btnEditPath.setToolTipText(
//				GVSingleton.getInstance().getLanguage().getString("btnEditPath.toolTipText"));
//		btnEditPath.setIcon(new ImageIcon((
//				Toolkit.getDefaultToolkit().getImage(
//						getClass().getResource("/com/nexusbr/gv/images/editPath.gif")))));
//		btnEditPath.addActionListener(events);

		//BTN MOVE GEOMETRY
		btnMoveGeom.setEnabled(false);
		btnMoveGeom.setBorderPainted(false);
		btnMoveGeom.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnMoveGeom.toolTipText"));
		btnMoveGeom.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/moveGeom.gif")))));
		btnMoveGeom.addActionListener(events);

		//BTN DRAW GEOMETRY
		btnDLine.setEnabled(false);
		btnDLine.setBorderPainted(false);
		btnDLine.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnDLine.toolTipText"));
		btnDLine.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/drawLine.gif")))));
		btnDLine.addActionListener(events);

		//BTN MOVE GEOMETRY
		btnDPoly.setEnabled(false);
		btnDPoly.setBorderPainted(false);
		btnDPoly.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnDPoly.toolTipText"));
		btnDPoly.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/drawPolygon.gif")))));
		btnDPoly.addActionListener(events);

		//BTN MOVE GEOMETRY
		btnDNetwork.setEnabled(false);
		btnDNetwork.setBorderPainted(false);
		btnDNetwork.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnDNetwork.toolTipText"));
		btnDNetwork.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/drawNetwork.gif")))));
		btnDNetwork.addActionListener(events);

		//BTN MOVE GEOMETRY
		btnInsPoint.setEnabled(false);
		btnInsPoint.setBorderPainted(false);
		btnInsPoint.setToolTipText(
				GVSingleton.getInstance().getLanguage().getString("btnInsPoint.toolTipText"));
		btnInsPoint.setIcon(new ImageIcon((
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/insPoint.gif")))));
		btnInsPoint.addActionListener(events);

		//DASHED LINES
		btnDashed1.setEnabled(false);
		btnDashed1.setBorderPainted(false);    	
		btnDashed1.setSize(32, 32);		
		btnDashed1.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/lineDashed.png"))));	    
		btnDashed2.setEnabled(false);
		btnDashed2.setBorderPainted(false);    	
		btnDashed2.setSize(32, 32);		
		btnDashed2.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/lineDashed.png"))));	    
		btnDashed3.setEnabled(false);
		btnDashed3.setBorderPainted(false);    	
		btnDashed3.setSize(32, 32);		
		btnDashed3.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/lineDashed.png"))));	    
		btnDashed4.setEnabled(false);
		btnDashed4.setBorderPainted(false);    	
		btnDashed4.setSize(32, 32);		
		btnDashed4.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/lineDashed.png"))));	    
		btnDashed5.setEnabled(false);
		btnDashed5.setBorderPainted(false);    	
		btnDashed5.setSize(32, 32);		
		btnDashed5.setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/com/nexusbr/gv/images/lineDashed.png"))));

		btnGroup.add(btnSelectGeom);
		btnGroup.add(btnEditPath);
		btnGroup.add(btnMoveGeom);
		btnGroup.add(btnDLine);
		btnGroup.add(btnDPoly);
		btnGroup.add(btnDNetwork);
		btnGroup.add(btnInsPoint);
		

		toolMain.add(btnAddLayers);    	
		toolMain.add(btnProperties);    	
		toolMain.add(btnDashed1); //dashedLine
		toolMain.add(btnSave);
		toolMain.add(btnDelete);
		toolMain.add(btnDashed2); //dashedLine
		toolMain.add(btnUndo);
		toolMain.add(btnRedo);
		toolMain.add(btnDashed3); //dashedLine
		toolMain.add(btnSelectGeom);
		toolMain.add(btnEditPath);
		toolMain.add(btnMoveGeom);
		toolMain.add(btnDashed4); //dashedLine
		toolMain.add(btnDLine);
		toolMain.add(btnDPoly);
		toolMain.add(btnDNetwork);
		toolMain.add(btnInsPoint);
		toolMain.add(tglbtnEdition);
		toolMain.add(separator);

		//ENABLING BUTTONS
		btnAddLayers.setEnabled(true);
		btnProperties.setEnabled(true);
		btnSave.setEnabled(true);
		btnDelete.setEnabled(true);
		btnUndo.setEnabled(false);
		btnRedo.setEnabled(false);
		btnSelectGeom.setEnabled(true);  
		btnMoveGeom.setEnabled(true);
		btnEditPath.setEnabled(true);
		btnDLine.setEnabled(true);
		btnDPoly.setEnabled(true);
		btnDNetwork.setEnabled(true);
		btnInsPoint.setEnabled(true);
	}
	
	/**
	 * populate properties
	 */
	private void populateProperties()
	{	
		frameProperties.populatePropertieWindow();				
	}	

	/** 
	 * @return features from propertie window
	 */
	public SimpleFeature getFeature() {
		return frameProperties.getFeature();
	}

	/**
	 * set feature in properties window
	 * @param feature 
	 */
	public void setFeature(SimpleFeature feature) {
		frameProperties.setFeature(feature);
		populateProperties();
	}

	/**
	 * get point1 
	 * @return point1
	 */
	public SimpleFeature getFeaturePoint1() {
		return frameProperties.getFeaturePoint1();
	}

	/**
	 * set point 1
	 * @param feature
	 */
	public void setFeaturePoint1(SimpleFeature feature) {
		frameProperties.setFeaturePoint1(feature);
	}

	/**
	 * get point 2
	 * @return point2
	 */
	public SimpleFeature getFeaturePoint2() {
		return frameProperties.getFeaturePoint2();
	}

	/*
	 * sets point 2
	 */
	public void setFeaturePoint2(SimpleFeature feature) {
		frameProperties.setFeaturePoint2(feature);
	}

	/**
	 * get toolmain
	 * @return toolMain
	 */
	public JToolBar getToolbar(){
		return toolMain;
	}

	/**
	 * get this class component
	 * @return this class
	 */
	public JPanel getToolbarPanel(){
		return this;
	}    

	/**
	 * get propertie window
	 * @return
	 */
	public PropertiesWindow getFramePropertie() {
		return frameProperties;
	}

	/**
	 * get toolbar text
	 * @return
	 */
	public String getToolBarText(){
		return GVSingleton.getInstance().getLanguage().getString("toolBar.text");
	}

	/**
	 * get toolbar toolip
	 * @return
	 */
	public String getToolBarTooltip(){
		return GVSingleton.getInstance().getLanguage().getString("toolBar.toolTipText");
	}

	/**
	 * @return the btnGroup
	 */
	public ButtonGroup getBtnGroup() {
		return btnGroup;
	}

	/**
	 * @return the btnDelete
	 */
	public JButton getBtnDelete() {
		return btnDelete;
	}

	/**
	 * @return the btnSave
	 */
	public JButton getBtnSave() {
		return btnSave;
	}

	/**
	 * @return the btnUndo
	 */
	public JButton getBtnUndo() {
		return btnUndo;
	}

	/**
	 * @return the btnRedo
	 */
	public JButton getBtnRedo() {
		return btnRedo;
	}

	/**
	 * @return the btnProperties
	 */
	public JButton getBtnProperties() {
		return btnProperties;
	}

	/**
	 * @return the btnAddLayers
	 */
	public JButton getBtnAddLayers() {
		return btnAddLayers;
	}

	/**
	 * @return the btnSelectGeom
	 */
	public JToggleButton getBtnSelectGeom() {
		return btnSelectGeom;
	}

	/**
	 * @return the btnMoveGeom
	 */
	public JToggleButton getBtnMoveGeom() {
		return btnMoveGeom;
	}

	/**
	 * @return the btnEditPath
	 */
	public JToggleButton getBtnEditPath() {
		return btnEditPath;
	}

	/**
	 * @return the btnDNetwork
	 */
	public JToggleButton getBtnDNetwork() {
		return btnDNetwork;
	}

	/**
	 * @return the btnInsPoint
	 */
	public JToggleButton getBtnInsPoint() {
		return btnInsPoint;
	}

	/**
	 * @return the btnDLine
	 */
	public JToggleButton getBtnDLine() {
		return btnDLine;
	}

	/**
	 * @return the btnDPoly
	 */
	public JToggleButton getBtnDPoly() {
		return btnDPoly;
	}

	/**
	 * @return the btnDashed1
	 */
	public JButton getBtnDashed1() {
		return btnDashed1;
	}

	/**
	 * @return the btnDashed2
	 */
	public JButton getBtnDashed2() {
		return btnDashed2;
	}

	/**
	 * @return the btnDashed3
	 */
	public JButton getBtnDashed3() {
		return btnDashed3;
	}

	/**
	 * @return the btnDashed4
	 */
	public JButton getBtnDashed4() {
		return btnDashed4;
	}

	/**
	 * @return the btnDashed5
	 */
	public JButton getBtnDashed5() {
		return btnDashed5;
	}   
}
