package com.nexusbr.gv.view;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;

import br.org.funcate.eagles.kernel.dispatcher.EventDispatcher;
import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.EventListener;
import br.org.funcate.eagles.kernel.listener.ListenersHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.eagles.kernel.transmitter.EventTransmitter;
import br.org.funcate.glue.controller.CanvasController;
import br.org.funcate.glue.controller.Mediator;
import br.org.funcate.glue.event.BoxChangedEvent;
import br.org.funcate.glue.event.DrawFeatureEvent;
import br.org.funcate.glue.event.GetLocaleEvent;
import br.org.funcate.glue.event.ScaleChangedEvent;
import br.org.funcate.glue.event.SelectedThemeEvent;
import br.org.funcate.glue.event.TreeThemeChangeEvent;
import br.org.funcate.glue.event.UnselectedThemeEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.tree.CustomNode;

import br.org.funcate.jtdk.edition.event.SetStyleEvent;
import br.org.funcate.jtdk.edition.undoredo.event.UndoRedoActiveEvent;
import br.org.funcate.plugin.GluePluginService;
import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.controller.tools.DeleteTool;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.singleton.GVSingleton;

import com.nexusbr.gv.view.components.ToolbarWindow;

/**
 * Provides plugin access to the glue system.
 * This class  inicializate the components.
 *
 * @author Severino, Bruno de Olivera
 * @version 1.0
 */
public class GVClient implements EventDispatcher, EventListener{

	private ToolbarWindow toolbarPanel;
	private Locale currentLocale;
	private ResourceBundle language;
	private EventTransmitter transmitter;
	private ListenersHandler listeners;
	private EventHandler eventHandler;
	private List<String> eventsToListen;
	private Manager manager;
	private double showScaleValue;
	private ArrayList<String> idsOld;
	private ArrayList<String> streetIds;
	
	/**
	 * Creates the GVClient.
	 */	
	public void setPlugin(){
		//GluePluginService.insertPluginToolBar(toolbarPanel.getToolBarText(), createToolbar(), toolbarPanel.getToolBarTooltip());
	}
	
	/**
	 * this class constructor
	 */
	public GVClient()
	{	
		GetLocaleEvent localeEvent = new GetLocaleEvent(this);
		manager = new Manager();
		GluePluginService.setCurrentTool(manager);
		try {
			manager.dispatch(manager.getTransmitter(), localeEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentLocale = localeEvent.getLocale();
		if((currentLocale.getCountry() != "US") && (currentLocale.getCountry() != "JP") && (currentLocale.getCountry() != "BR")){
			currentLocale = new Locale("","");
		}

		currentLocale = new Locale("en","US");
		language = ResourceBundle.getBundle("com/nexusbr/gv/languages/Bundle",currentLocale);

		//InitDynamicDB initDynamicDB = new InitDynamicDB();
		//initDynamicDB.start();
		
    	toolbarPanel = new ToolbarWindow();
    
    	GVSingleton singleton = GVSingleton.getInstance();
    	singleton.setLanguage(language);
    	singleton.setToolbar(toolbarPanel);
    	
    	this.initListeners();

    	//this.initFeatures();
    	
    	this.setPlugin();
	}

	/**
	 * starts eagles events
	 */
	private void initListeners(){
		this.listeners = new ListenersHandlerImpl();
		this.eventHandler = new EventHandler();
		this.transmitter = new DirectedEventTransmitter(this);

		this.eventsToListen = new ArrayList<String>();
		this.eventsToListen.add(UndoRedoActiveEvent.class.getName());
		this.eventsToListen.add(TreeThemeChangeEvent.class.getName());
		this.eventsToListen.add(UnselectedThemeEvent.class.getName());
		this.eventsToListen.add(SelectedThemeEvent.class.getName());
		this.eventsToListen.add(ScaleChangedEvent.class.getName());
		this.eventsToListen.add(BoxChangedEvent.class.getName());
		this.eventsToListen.add(DrawFeatureEvent.class.getName());

		AppSingleton singleton = AppSingleton.getInstance();
		Mediator mediator = singleton.getMediator();

		CanvasController canvasController = mediator.getCanvasController();

		canvasController.getListenersHandler().attachListener(this, eventsToListen);
	}

	/**
	 * Thread to Initialize the Dynamic DatabBase AGM
	 * @author Severino, Bruno de Oliveira
	 * @throws Exception 
	 *
	 */
//	class InitDynamicDB extends Thread {
//		public void run() {
//			new DataBaseCreator().createDataBase();
//		}
//	}
	public void drawFeatureLineById(ArrayList<String> ids) throws Exception{
		
		if(ids != null && !ids.isEmpty()){
			if(idsOld!=ids){
				GluePluginService.setCurrentTool(new DeleteTool());
				idsOld = ids;
			}
			
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
			manager.ShowLineFeaturesByID(manager.locateLinesByID(ids));	
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
		}	
	}
	
	public void drawFeaturePolygonById(ArrayList<String> ids) throws Exception{		
		dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(),"LINE"));
		manager.ShowPolygonFeaturesByID(manager.locatePolygonsByID(ids));	
		manager.dispatchLayersFeedback();
		manager.dispatchLayersEdition();
			
	}
	
	public void initFeatures(){
//		try {
//			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
//			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle(), "POINT"));
//			manager.ShowPointFeatures(manager.locatePoints());
//			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
//			manager.ShowLineFeatures(manager.locateLines());
//			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
//			manager.ShowPolygonFeatures(manager.locatePolygons());
//			
//			manager.dispatchLayersFeedback();
//			manager.dispatchLayersEdition();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * This method create a geovector toolbar.
	 * @return toolbarPanel a <code>JPanel</code> Component
	 */
	private JPanel createToolbar()
	{			
		toolbarPanel.createToolBar();
		return toolbarPanel.getToolbarPanel();
	}
	
	@Override
	public void handle(EventObject event) throws Exception {
		if (event instanceof UndoRedoActiveEvent){
			this.handle((UndoRedoActiveEvent) event);
		} else if(event instanceof TreeThemeChangeEvent){
			this.handle((TreeThemeChangeEvent) event);
		}
		else if(event instanceof SelectedThemeEvent){
			this.handle((SelectedThemeEvent) event);
		}
		else if(event instanceof UnselectedThemeEvent){
			this.handle((UnselectedThemeEvent) event);
		}
		else if(event instanceof UnselectedThemeEvent){
			this.handle((UnselectedThemeEvent) event);
		}
		else if (event instanceof ScaleChangedEvent){
			this.handle((ScaleChangedEvent)event);
		}
		else if(event instanceof BoxChangedEvent){
			this.handle((BoxChangedEvent) event);
		}
		else if(event instanceof DrawFeatureEvent){
			this.handle((DrawFeatureEvent)event);
		}
	}
	private void handle (DrawFeatureEvent e){
		 streetIds = new ArrayList<String>();
		 streetIds = e.getLineIds(); 
		 try {
				this.drawFeatureLineById(streetIds);
				//this.drawFeaturePolygonById(streetIds);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	}

	/**
	 * handle the event of undo and redo
	 * @param event
	 */
	private void handle(UndoRedoActiveEvent event){
		JButton btnRedo = toolbarPanel.getBtnRedo();
		JButton btnUndo = toolbarPanel.getBtnUndo();
		btnRedo.setEnabled(event.isRedoable());
		btnUndo.setEnabled(event.isUndoable());
	}
	static CustomNode theme;
	static CustomNode themeOld;
	private void handle(TreeThemeChangeEvent event){
		
//		if(event.isChange() && EditionState.isEdited()){
//			GlueMessageDialog.show("Deseja Salvar as Alterações no Tema "+event.getOldTheme().getName().toUpperCase()+" ?", 
//				"Você realizou alterações durante o processo de edição que podem ser \nperdidas se não forem salvas!", 2);
//			EditionState.setEdited(false);
//		}
//		if(GlueMessageDialog.isOK()){
//			AppSingleton.getInstance().getTreeState().setCurrentTheme(event.getOldTheme());
//			GluePluginService.setCurrentTool(new CommitTool());
//			
//		}
	}
	private void handle(ScaleChangedEvent event){
		
	}
	private void handle(SelectedThemeEvent event){
		updateSelectedThemes();		
	}
	private static double oldx1;
	private static double oldy1;

	private boolean start = false;
	private boolean lock = false;
	private void handle(BoxChangedEvent event) throws Exception{
		
		boolean editIsSet = GVSingleton.getInstance().getToolbar().isShowing();
		double scale = Math.round(AppSingleton.getInstance().getCanvasState().getScale());
		String proj = AppSingleton.getInstance().getCanvasState().getProjection().getName();
		double x1 = AppSingleton.getInstance().getCanvasState().getBox().getX1();	
		double y1 = AppSingleton.getInstance().getCanvasState().getBox().getY1();
		
		double difX1 = Math.abs(oldx1 - x1);
		double difY1 = Math.abs(oldy1 - y1);
		
		 if(proj.equals("LatLong"))
			 showScaleValue = 123;
		 else if(proj.equals("UTM"))
			 showScaleValue = 141;
		 
		 if(scale<=showScaleValue && !lock ){
			 start = true;
			 lock = true;
		 }
		 showScaleValue = 1500;
		 
		if(scale<=showScaleValue && !editIsSet && (difX1 > 630 || difY1 > 140) || start){
			oldx1 = AppSingleton.getInstance().getCanvasState().getBox().getX1();
			oldy1 = AppSingleton.getInstance().getCanvasState().getBox().getY1();
			initFeatures();
			start = false;
		}
	}
	
	private void handle(UnselectedThemeEvent event){
		updateSelectedThemes();
	}
	
	@Override
	public ListenersHandler getListenersHandler() {
		return this.listeners;
	}
	
	@Override
	public EventHandler getEventHandler() {
		return eventHandler;
	}
	
	@Override
	public void dispatch(EventTransmitter tc, EventObject e) throws Exception {
		tc.dispatch(e);
	}
	

	private void updateSelectedThemes() {
		try {
			//manager.clear();
			manager.ShowLineFeatures(manager.locateLines());
			manager.ShowPointFeatures(manager.locatePoints());
			manager.ShowPolygonFeatures(manager.locatePolygons());

			manager.dispatchLayersEdition();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
