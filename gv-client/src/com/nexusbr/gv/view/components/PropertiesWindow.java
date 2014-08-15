package com.nexusbr.gv.view.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import com.nexusbr.gv.controller.events.EventsForPropertiesWindow;
import com.nexusbr.gv.services.AttributesTableModel;
import com.nexusbr.gv.services.EachRowEditor;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Create the propertie window
 * 
 * @author Severino, Bruno de Oliveira
 * @version 1.0
 *
 */
public class PropertiesWindow extends JDialog {

	private static final long serialVersionUID = -3372271612111173120L;

	private ResourceBundle language;	
	private JTabbedPane tabbedGeometries;
	private JPanel contentPane;	
	private JLabel lblType;
	private JLabel lblSelect;
	JTabbedPane tabbedProperties;
	JPanel panelAttributes;
	JPanel panelGeometry;
	private JTextField txtType;
	//private JTextField txtLayer;
	@SuppressWarnings("rawtypes")
	private JComboBox cmbLayer;
	private EachRowEditor rowEditor;
	@SuppressWarnings("unused")
	private JLabel lblLayer;
	private JTable tableAttributes;
	JScrollPane scrollAttributes;	
	@SuppressWarnings("unused")
	private EventsForPropertiesWindow event;
	private JButton btnSave;
	private String layerSelected = null;
	private String objetID;
	private SimpleFeature feature;
	private SimpleFeature featurePoint1;
	private SimpleFeature featurePoint2;

	//For Line tabledPanel
	private JPanel panelLine;
	private JLabel lblLineID;
	public JTextField txtLineID;
	private JScrollPane scrollLine;
	public JTable tableLine;
	public JTable tableCoordLine;	
	private JPanel panelCoordLine;
	private JLabel lblCoordLine;
	private JButton btnCoodLine;
	private JScrollPane scrollCoordLine;

	//For Point tabledPanel
	private JPanel panelPoint;
	private JLabel lblPointID;
	public JTextField txtPointID;
	private JScrollPane scrollPoint;
	public JTable tablePoint;
	public JTable tableCoordPoint;	
	private JPanel panelCoordPoint;
	private JLabel lblCoordPoint;
	private JButton btnCoodPoint;
	private JScrollPane scrollCoordPoint;

	//For Point 2 tabledPanel
	private JPanel panelPoint2;
	private JLabel lblPoint2ID;
	public JTextField txtPoint2ID;
	private JScrollPane scrollPoint2;
	public JTable tablePoint2;
	public JTable tableCoordPoint2;	
	private JPanel panelCoordPoint2;
	private JLabel lblCoordPoint2;
	private JButton btnCoodPoint2;
	private JScrollPane scrollCoordPoint2;

	//For Polygon tabledPanel
	private JPanel panelPolygon;
	private JLabel lblPolygonID;
	public JTextField txtPolygonID;
	private JScrollPane scrollPolygon;
	public JTable tablePolygon;
	public JTable tableCoordPolygon;	
	private JPanel panelCoordPolygon;
	private JLabel lblCoordPolygon;
	private JButton btnCoodPolygon;
	private JScrollPane scrollCoordPolygon;	

	/**
	 * Create the JDialog.
	 */
	public PropertiesWindow() {
		language = GVSingleton.getInstance().getLanguage(); //Catch the Language Bundle;
		event = new EventsForPropertiesWindow(this);

		setAlwaysOnTop(true);
		setResizable(false);
		setBackground(Color.WHITE);
		setTitle(language.getString("PropertiesWindow.title"));
		setBounds(100, 100, 438, 401);	// size of the JDialog		

		createContentPane(); 
	}	

	/**
	 * Create the ContentPane.
	 */
	private void createContentPane(){
		//Create the content panel
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);		
		contentPane.setLayout(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);		

		//Create the JTabbledPanel Properties
		tabbedProperties = new JTabbedPane(JTabbedPane.TOP);
		tabbedProperties.setBounds(new Rectangle(0, 0, 431, 369));
		contentPane.add(tabbedProperties);

		//PAINEL DE ATRIBUTO
		createAttibutePanel();		

		//PAINEL DE GEOMETRIA
		createGeometryPanel();
	}
	
	/**
	 * create the attribute painel
	 */
	private void createAttibutePanel() {
		/*//Create the Atribute panel
		panelAttributes = new JPanel();
		panelAttributes.setBackground(Color.WHITE);		
		panelAttributes.setLayout(null);
		tabbedProperties.addTab("Atributos", null, panelAttributes, null);

		lblLayer = new JLabel("Tabela Dinamica:");
		lblLayer.setBounds(5, 10, 131, 30);
		panelAttributes.add(lblLayer);

		tableAttributes = new JTable();
		rowEditor = new EachRowEditor(tableAttributes);

		cmbLayer = new JComboBox();
		cmbLayer.addItemListener(event);
		cmbLayer.setBounds(137, 10, 284, 30);	
		cmbLayer.addItem("Nenhum");
		panelAttributes.add(cmbLayer);
		//		txtLayer = new JTextField();
		//		txtLayer.addPropertyChangeListener(event);
		//		txtLayer.setEnabled(false);		
		//		txtLayer.setBounds(137, 10, 284, 30);	
		//		txtLayer.setText("");		
		//		panelAttributes.add(txtLayer);

		scrollAttributes = new JScrollPane();
		scrollAttributes.setBounds(5, 50, 417, 265);
		panelAttributes.add(scrollAttributes);

		tableAttributes.setRowSelectionAllowed(false);
		tableAttributes.setRowHeight(23);
		tableAttributes.setModel(new AttributesTableModel());
		tableAttributes.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableAttributes.getColumnModel().getColumn(0).setMinWidth(100);
		tableAttributes.getColumnModel().getColumn(0).setMaxWidth(100);
		tableAttributes.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableAttributes.getColumnModel().getColumn(1).setMinWidth(100);
		tableAttributes.getColumn(tableAttributes.getColumnName(1)).setCellEditor(rowEditor);
		scrollAttributes.setViewportView(tableAttributes);

		btnSave = new JButton("Save");
		btnSave.setMargin(new Insets(0, 0, 0, 0));
		btnSave.setEnabled(false);
		btnSave.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/disk-small-black.png")));
		btnSave.setBounds(321, 320, 100, 20);
		btnSave.addActionListener(event);
		panelAttributes.add(btnSave);*/
	}

	/**
	 * 
	 * @param repID 1( polygon), 2 (line), 3 (point)
	 */
	
	public void popCmbLayer(Long repID){
		/*if(cmbLayer.getItemCount()>1)
			for(int i=1;i<cmbLayer.getItemCount();i++)
				cmbLayer.remove(i);
		List<Theme> themes = AppSingleton.getInstance().getCanvasState().getView().getThemes();
		for (Theme theme : themes) {
			for (Representation rep : theme.getReps()){
				if(rep.getId() == repID){
					List<Table> layers = (List<Table>) new TablesBO().getTables();				
					for(int i=0;i<layers.size();i++){
						if(layers.get(i).getLayerName().equals(theme.getName())){
							cmbLayer.addItem(layers.get(i));
						}
					}
				}
			}
		}*/		
	}

	/**
	 * clear table
	 */
	public void cleanTable(){
		/*AttributesTableModel model = (AttributesTableModel) getTableAttributes().getModel();
		model.removeAll();
		btnSave.setEnabled(false);

		if(layerSelected==null){
			cmbLayer.setSelectedItem("Nenhum");
		}else{
			cmbLayer.setSelectedItem(layerSelected);
		}

		if(getCmbLayer().getSelectedItem().toString() == "Nenhum")
			return;

		btnSave.setEnabled(true);
		//Table table = (Table) getCmbLayer().getSelectedItem();
		String layerName = getCmbLayer().getSelectedItem().toString();		
		Table table = new TablesBO().getTableFromLayerName(layerName);

		new ModelBO().populateTable(table, model, getRowEditor());

		ThreadVerifyGeom loading = new ThreadVerifyGeom(table.getTableValueName());
		loading.start();	*/

	}

	/**
	 * verify if the geometry has attributes
	 * @param tableStorage
	 */
	
	private void verifyIfGeomHasAttribute(String tableStorage){
		/*String tableValueName = tableStorage;			
		HashMap<String, String> dynAttr = (HashMap<String, String>) feature.getAttribute("dynamicAttributes");		
		System.out.println(dynAttr);
		if(dynAttr == null){
			List<Value> values = new ValuesBO().getValuesFromObjectID(getObjetID(), tableValueName);
			Collections.sort((List)values);
			for(Value vle : values){
				new ModelBO().populateTableValues(vle, (AttributesTableModel)tableAttributes.getModel());
			}
		}else{
			Set<String> attr = dynAttr.keySet();
			for (String attrName : attr)  {
				AttributesTableModel model = (AttributesTableModel)tableAttributes.getModel();
				for(int i=0;i<model.getRowCount();i++){
					if(model.getValueAt(i, 0).equals(attrName)){
						model.setValueAt(dynAttr.get(attrName), i, 1);
					}
				}				
			}
		}*/
	}

	/**
	 * Thread to verify the geometry attributes
	 * @author Severino, Bruno de Oliveira
	 * @versino 1.0
	 */
	class ThreadVerifyGeom extends Thread {
		String tableStorage;

		public ThreadVerifyGeom(String tableValueName) {
			tableStorage = tableValueName;
		}

		public void run() {
			verifyIfGeomHasAttribute(tableStorage);
		}
	}

	/**
	 * create the geometry Panel
	 */
	private void createGeometryPanel() {
		//Create the Geometry panel
		panelGeometry = new JPanel();
		panelGeometry.setBackground(Color.WHITE);		
		panelGeometry.setLayout(null);
		tabbedProperties.addTab("Geometry", null, panelGeometry, null);

		//TIPO DA GEOMETRIA
		lblType = new JLabel(language.getString("lblType.text"));
		lblType.setBounds(new Rectangle(5, 10, 100, 30));
		panelGeometry.add(lblType);

		txtType = new JTextField();
		txtType.setBounds(new Rectangle(105, 10, 316, 30));		
		txtType.setEditable(false);
		txtType.setEnabled(false);
		txtType.setColumns(10);
		panelGeometry.add(txtType);

		//LABEL DE SELECT	
		lblSelect = new JLabel(language.getString("lblSelect.text"));
		lblSelect.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelect.setBounds(new Rectangle(0, 51, 426, 333));
		contentPane.add(lblSelect);

		tabbedGeometries = new JTabbedPane(JTabbedPane.TOP);
		tabbedGeometries.setBackground(Color.WHITE);
		tabbedGeometries.setBounds(0, 50, 426, 295);
		panelGeometry.add(tabbedGeometries);		
	}	

	/**
	 * remove all tabs
	 */
	public void removeAllTabs(){
		tabbedGeometries.removeAll();
		getContentPane().add(lblSelect);
	}

	/**
	 * add line tab
	 */
	public void addLineTab(){
		panelLine = new JPanel();
		panelLine.setBackground(Color.WHITE);		
		panelLine.setLayout(null);

		lblLineID = new JLabel("ID:");
		lblLineID.setBounds(12, 12, 32, 25);
		panelLine.add(lblLineID);

		txtLineID = new JTextField();
		txtLineID.setBounds(48, 10, 361, 25);		
		txtLineID.setColumns(10);
		panelLine.add(txtLineID);

		scrollLine = new JScrollPane();
		scrollLine.setBounds(12, 56, 397, 115);
		panelLine.add(scrollLine);

		tableLine = new JTable();
		tableLine.setModel(new AttributesTableModel());
		tableLine.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableLine.getColumnModel().getColumn(0).setMinWidth(100);
		tableLine.getColumnModel().getColumn(0).setMaxWidth(100);
		tableLine.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableLine.getColumnModel().getColumn(1).setMinWidth(100);
		scrollLine.setViewportView(tableLine);

		panelCoordLine = new JPanel();
		panelCoordLine.setBackground(new Color(176, 196, 222));
		panelCoordLine.setBounds(12, 172, 397, 22);
		panelCoordLine.setLayout(null);
		panelLine.add(panelCoordLine);		

		lblCoordLine = new JLabel(language.getString("lblCoord.text"));
		lblCoordLine.setBounds(39, 0, 120, 22);
		panelCoordLine.add(lblCoordLine);

		btnCoodLine = new JButton("");
		btnCoodLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(panelCoordLine.getBounds().height == 22){
					panelCoordLine.setBounds(12, 172, 397, 85);
					btnCoodLine.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239592.png")));
				}
				else{
					panelCoordLine.setBounds(12, 172, 397, 22);
					btnCoodLine.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
				}
			}
		});
		btnCoodLine.setContentAreaFilled(false);
		btnCoodLine.setBorderPainted(false);
		btnCoodLine.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
		btnCoodLine.setBounds(0, 0, 30, 22);
		panelCoordLine.add(btnCoodLine);

		scrollCoordLine = new JScrollPane();
		scrollCoordLine.setBounds(2, 22, 393, 60);
		panelCoordLine.add(scrollCoordLine);

		tableCoordLine = new JTable();
		tableCoordLine.setModel(new AttributesTableModel());
		tableCoordLine.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableCoordLine.getColumnModel().getColumn(0).setMinWidth(100);
		tableCoordLine.getColumnModel().getColumn(0).setMaxWidth(100);
		tableCoordLine.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableCoordLine.getColumnModel().getColumn(1).setMinWidth(100);
		scrollCoordLine.setViewportView(tableCoordLine);

		this.remove(lblSelect);		
		tabbedGeometries.addTab(language.getString("tabbedLine.text"), null, panelLine, null);
	}

	/**
	 * add point tab
	 */
	public void addPointTab(){
		panelPoint = new JPanel();
		panelPoint.setBackground(Color.WHITE);		
		panelPoint.setLayout(null);

		lblPointID = new JLabel("ID:");
		lblPointID.setBounds(12, 12, 32, 25);
		panelPoint.add(lblPointID);

		txtPointID = new JTextField();
		txtPointID.setBounds(48, 10, 361, 25);		
		txtPointID.setColumns(10);
		panelPoint.add(txtPointID);

		scrollPoint = new JScrollPane();
		scrollPoint.setBounds(12, 56, 397, 115);
		panelPoint.add(scrollPoint);

		tablePoint = new JTable();
		tablePoint.setModel(new AttributesTableModel());
		tablePoint.getColumnModel().getColumn(0).setPreferredWidth(100);
		tablePoint.getColumnModel().getColumn(0).setMinWidth(100);
		tablePoint.getColumnModel().getColumn(0).setMaxWidth(100);
		tablePoint.getColumnModel().getColumn(1).setPreferredWidth(300);
		tablePoint.getColumnModel().getColumn(1).setMinWidth(100);
		scrollPoint.setViewportView(tablePoint);

		panelCoordPoint = new JPanel();
		panelCoordPoint.setBackground(new Color(176, 196, 222));
		panelCoordPoint.setBounds(12, 172, 397, 22);
		panelCoordPoint.setLayout(null);
		panelPoint.add(panelCoordPoint);		

		lblCoordPoint = new JLabel(language.getString("lblCoord.text"));
		lblCoordPoint.setBounds(39, 0, 120, 22);
		panelCoordPoint.add(lblCoordPoint);

		btnCoodPoint = new JButton("");
		btnCoodPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(panelCoordPoint.getBounds().height == 22){
					panelCoordPoint.setBounds(12, 172, 397, 85);
					btnCoodPoint.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239592.png")));
				}
				else{
					panelCoordPoint.setBounds(12, 172, 397, 22);
					btnCoodPoint.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
				}
			}
		});
		btnCoodPoint.setContentAreaFilled(false);
		btnCoodPoint.setBorderPainted(false);
		btnCoodPoint.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
		btnCoodPoint.setBounds(0, 0, 30, 22);
		panelCoordPoint.add(btnCoodPoint);

		scrollCoordPoint = new JScrollPane();
		scrollCoordPoint.setBounds(2, 22, 393, 60);
		panelCoordPoint.add(scrollCoordPoint);

		tableCoordPoint = new JTable();
		tableCoordPoint.setModel(new AttributesTableModel());
		tableCoordPoint.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableCoordPoint.getColumnModel().getColumn(0).setMinWidth(100);
		tableCoordPoint.getColumnModel().getColumn(0).setMaxWidth(100);
		tableCoordPoint.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableCoordPoint.getColumnModel().getColumn(1).setMinWidth(100);
		scrollCoordPoint.setViewportView(tableCoordPoint);

		this.remove(lblSelect);		
		tabbedGeometries.addTab(language.getString("tabbedPoint.text"), null, panelPoint, null);
	}

	/**
	 * add polygon tab
	 */
	public void addPolygonTab(){
		panelPolygon = new JPanel();
		panelPolygon.setBackground(Color.WHITE);		
		panelPolygon.setLayout(null);

		lblPolygonID = new JLabel("ID:");
		lblPolygonID.setBounds(12, 12, 32, 25);
		panelPolygon.add(lblPolygonID);

		txtPolygonID = new JTextField();
		txtPolygonID.setBounds(48, 10, 361, 25);		
		txtPolygonID.setColumns(10);
		panelPolygon.add(txtPolygonID);

		scrollPolygon = new JScrollPane();
		scrollPolygon.setBounds(12, 56, 397, 115);
		panelPolygon.add(scrollPolygon);

		tablePolygon = new JTable();
		tablePolygon.setModel(new AttributesTableModel());
		tablePolygon.getColumnModel().getColumn(0).setPreferredWidth(100);
		tablePolygon.getColumnModel().getColumn(0).setMinWidth(100);
		tablePolygon.getColumnModel().getColumn(0).setMaxWidth(100);
		tablePolygon.getColumnModel().getColumn(1).setPreferredWidth(300);
		tablePolygon.getColumnModel().getColumn(1).setMinWidth(100);
		scrollPolygon.setViewportView(tablePolygon);

		panelCoordPolygon = new JPanel();
		panelCoordPolygon.setBackground(new Color(176, 196, 222));
		panelCoordPolygon.setBounds(12, 172, 397, 22);
		panelCoordPolygon.setLayout(null);
		panelPolygon.add(panelCoordPolygon);		

		lblCoordPolygon = new JLabel(language.getString("lblCoord.text"));
		lblCoordPolygon.setBounds(39, 0, 120, 22);
		panelCoordPolygon.add(lblCoordPolygon);

		btnCoodPolygon = new JButton("");
		btnCoodPolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(panelCoordPolygon.getBounds().height == 22){
					panelCoordPolygon.setBounds(12, 172, 397, 85);
					btnCoodPolygon.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239592.png")));
				}
				else{
					panelCoordPolygon.setBounds(12, 172, 397, 22);
					btnCoodPolygon.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
				}
			}
		});
		btnCoodPolygon.setContentAreaFilled(false);
		btnCoodPolygon.setBorderPainted(false);
		btnCoodPolygon.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
		btnCoodPolygon.setBounds(0, 0, 30, 22);
		panelCoordPolygon.add(btnCoodPolygon);

		scrollCoordPolygon = new JScrollPane();
		scrollCoordPolygon.setBounds(2, 22, 393, 60);
		panelCoordPolygon.add(scrollCoordPolygon);

		tableCoordPolygon = new JTable();
		tableCoordPolygon.setModel(new AttributesTableModel());
		tableCoordPolygon.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableCoordPolygon.getColumnModel().getColumn(0).setMinWidth(100);
		tableCoordPolygon.getColumnModel().getColumn(0).setMaxWidth(100);
		tableCoordPolygon.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableCoordPolygon.getColumnModel().getColumn(1).setMinWidth(100);
		scrollCoordPolygon.setViewportView(tableCoordPolygon);

		this.remove(lblSelect);		
		tabbedGeometries.addTab(language.getString("tabbedPolygon.text"), null, panelPolygon, null);
	}

	/**
	 * case line network is create a point 1  tab
	 */
	public void addPoint1Tab(){
		addPointTab();
		tabbedGeometries.setTitleAt(tabbedGeometries.getTabCount()-1, language.getString("tabbedPoint1.text"));
	}

	/**
	 * case line network is create a point 2  tab
	 */
	public void addPoint2Tab(){
		panelPoint2 = new JPanel();
		panelPoint2.setBackground(Color.WHITE);		
		panelPoint2.setLayout(null);

		lblPoint2ID = new JLabel("ID:");
		lblPoint2ID.setBounds(12, 12, 32, 25);
		panelPoint2.add(lblPoint2ID);

		txtPoint2ID = new JTextField();
		txtPoint2ID.setBounds(48, 10, 361, 25);		
		txtPoint2ID.setColumns(10);
		panelPoint2.add(txtPoint2ID);

		scrollPoint2 = new JScrollPane();
		scrollPoint2.setBounds(12, 56, 397, 115);
		panelPoint2.add(scrollPoint2);

		tablePoint2 = new JTable();
		tablePoint2.setModel(new AttributesTableModel());
		tablePoint2.getColumnModel().getColumn(0).setPreferredWidth(100);
		tablePoint2.getColumnModel().getColumn(0).setMinWidth(100);
		tablePoint2.getColumnModel().getColumn(0).setMaxWidth(100);
		tablePoint2.getColumnModel().getColumn(1).setPreferredWidth(300);
		tablePoint2.getColumnModel().getColumn(1).setMinWidth(100);
		scrollPoint2.setViewportView(tablePoint2);

		panelCoordPoint2 = new JPanel();
		panelCoordPoint2.setBackground(new Color(176, 196, 222));
		panelCoordPoint2.setBounds(12, 172, 397, 22);
		panelCoordPoint2.setLayout(null);
		panelPoint2.add(panelCoordPoint2);		

		lblCoordPoint2 = new JLabel(language.getString("lblCoord.text"));
		lblCoordPoint2.setBounds(39, 0, 120, 22);
		panelCoordPoint2.add(lblCoordPoint2);

		btnCoodPoint2 = new JButton("");
		btnCoodPoint2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(panelCoordPoint2.getBounds().height == 22){
					panelCoordPoint2.setBounds(12, 172, 397, 85);
					btnCoodPoint2.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239592.png")));
				}
				else{
					panelCoordPoint2.setBounds(12, 172, 397, 22);
					btnCoodPoint2.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
				}
			}
		});
		btnCoodPoint2.setContentAreaFilled(false);
		btnCoodPoint2.setBorderPainted(false);
		btnCoodPoint2.setIcon(new ImageIcon(PropertiesWindow.class.getResource("/com/nexusbr/gv/images/1334239586.png")));
		btnCoodPoint2.setBounds(0, 0, 30, 22);
		panelCoordPoint2.add(btnCoodPoint2);

		scrollCoordPoint2 = new JScrollPane();
		scrollCoordPoint2.setBounds(2, 22, 393, 60);
		panelCoordPoint2.add(scrollCoordPoint2);

		tableCoordPoint2 = new JTable();
		tableCoordPoint2.setModel(new AttributesTableModel());
		tableCoordPoint2.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableCoordPoint2.getColumnModel().getColumn(0).setMinWidth(100);
		tableCoordPoint2.getColumnModel().getColumn(0).setMaxWidth(100);
		tableCoordPoint2.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableCoordPoint2.getColumnModel().getColumn(1).setMinWidth(100);
		scrollCoordPoint2.setViewportView(tableCoordPoint2);

		this.remove(lblSelect);		
		tabbedGeometries.addTab(language.getString("tabbedPoint2.text"), null, panelPoint2, null);
	}

	/**
	 * populate this class
	 */
	public void populatePropertieWindow() {    	
		getTxtType().setText(feature.getType().getTypeName());
		removeAllTabs();

		setObjetID(feature.getID());
		cleanTable();

		if(feature.getDefaultGeometry() instanceof Point){
			addPointTab();
			txtPointID.setText(feature.getID());
			fillAttributeTables(feature, (AttributesTableModel) tablePoint.getModel(), (AttributesTableModel) tableCoordPoint.getModel());
		}
		else if(feature.getDefaultGeometry() instanceof LineString){
			addLineTab();			
			txtLineID.setText(feature.getID());		
			fillAttributeTables(feature, (AttributesTableModel) tableLine.getModel(), (AttributesTableModel) tableCoordLine.getModel());

			if((Boolean) feature.getAttribute("network")){
				addPoint1Tab();
				txtPointID.setText((String) feature.getAttribute("point1"));
				fillAttributeTables(featurePoint1, (AttributesTableModel)tablePoint.getModel(), (AttributesTableModel)tableCoordPoint.getModel());
				
				addPoint2Tab();
				txtPoint2ID.setText((String) feature.getAttribute("point2"));
				fillAttributeTables(featurePoint2, (AttributesTableModel)tablePoint2.getModel(), (AttributesTableModel)tableCoordPoint2.getModel());
			}
		}
		else if(feature.getDefaultGeometry() instanceof Polygon){
			addPolygonTab();
			txtPolygonID.setText(feature.getID());
			fillAttributeTables(feature, (AttributesTableModel)tablePolygon.getModel(), (AttributesTableModel)tableCoordPolygon.getModel());
		}
	}

	private void fillAttributeTables(SimpleFeature feature, AttributesTableModel model, AttributesTableModel modelCoord){
		if(feature!=null){
		List<Object> attributesValue = feature.getAttributes();
		List<AttributeType> attributesName = feature.getType().getTypes();
		model.removeAll();
		Object[] id = new Object[] {"ID", feature.getID()};
		model.addRow(id);
		for(int i = 1; i<attributesValue.size();i++){
			Name name = attributesName.get(i).getName();
			if( !name.getLocalPart().equals("staticAttributes")
					&& !name.getLocalPart().equals("dynamicAttributes")
					&& !name.getLocalPart().equals("ID")){
				Object value = attributesValue.get(i);
				Object[] values = new Object[] { name , value };
				model.addRow(values);
			}
		}			
		Coordinate[] coordinates = ((Geometry)feature.getDefaultGeometry()).getCoordinates();
		modelCoord.removeAll();			
		for(int i = 0; i<coordinates.length;i++){
			Object[] values = new Object[] {"x"+(i+1), coordinates[i].x};
			modelCoord.addRow(values);
			Object[] values2 = new Object[] {"y"+(i+1), coordinates[i].y};
			modelCoord.addRow(values2);
		}
		}
	}

	/**
	 * @return the feature
	 */
	public SimpleFeature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}

	/**
	 * @return the featurePoint1
	 */
	public SimpleFeature getFeaturePoint1() {
		return featurePoint1;
	}

	/**
	 * @param featurePoint1 the featurePoint1 to set
	 */
	public void setFeaturePoint1(SimpleFeature featurePoint1) {
		this.featurePoint1 = featurePoint1;
	}

	/**
	 * @return the featurePoint2
	 */
	public SimpleFeature getFeaturePoint2() {
		return featurePoint2;
	}

	/**
	 * @param featurePoint2 the featurePoint2 to set
	 */
	public void setFeaturePoint2(SimpleFeature featurePoint2) {
		this.featurePoint2 = featurePoint2;
	}	

	/** 
	 * @param selectedLayerName
	 */
	public void setLayer(String selectedLayerName) {
		layerSelected = selectedLayerName;
		for (int i = 0; i < cmbLayer.getItemCount(); i++) {
			if(cmbLayer.getItemAt(i).toString().equals(layerSelected)){
				cmbLayer.setSelectedIndex(i);
			}
		}
	}

	/**
	 * @return the objetID
	 */
	public String getObjetID() {
		return objetID;
	}

	/**
	 * @param objetID the objetID to set
	 */
	public void setObjetID(String objetID) {
		this.objetID = objetID;
	}

	/**
	 * @return the txtType
	 */
	public JTextField getTxtType() {
		return txtType;
	}

	/**
	 * @return the cmbLayer
	 */
	@SuppressWarnings("rawtypes")
	public JComboBox getCmbLayer() {
		return cmbLayer;
	}

	/**
	 * @return the rowEditor
	 */
	public EachRowEditor getRowEditor() {
		return rowEditor;
	}

	/**
	 * @return the tableAttributes
	 */
	public JTable getTableAttributes() {
		return tableAttributes;
	}

	/**
	 * @return the btnSave
	 */
	public JButton getBtnSave() {
		return btnSave;
	}
}
