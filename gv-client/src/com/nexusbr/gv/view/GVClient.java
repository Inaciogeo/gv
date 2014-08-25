package com.nexusbr.gv.view;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventDispatcher;
import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.EventListener;
import br.org.funcate.eagles.kernel.listener.ListenersHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.EventTransmitter;
import br.org.funcate.glue.controller.CanvasController;
import br.org.funcate.glue.controller.Mediator;
import br.org.funcate.glue.event.BoxChangedEvent;
import br.org.funcate.glue.event.CommitFeatureEvent;
import br.org.funcate.glue.event.DrawFeatureEvent;
import br.org.funcate.glue.event.GetLocaleEvent;
import br.org.funcate.glue.event.GetOsEvent;
import br.org.funcate.glue.event.GetSelectFeatureEvent;
import br.org.funcate.glue.event.ScaleChangedEvent;
import br.org.funcate.glue.event.SelectFeatureEvent;
import br.org.funcate.glue.event.SelectedThemeEvent;
import br.org.funcate.glue.event.TreeThemeChangeEvent;
import br.org.funcate.glue.event.UnselectedThemeEvent;
import br.org.funcate.glue.main.AppSingleton;

import br.org.funcate.glue.model.Representation;
import br.org.funcate.glue.model.Theme;
import br.org.funcate.glue.model.tree.CustomNode;

import br.org.funcate.glue.view.GlueMessageDialog;
import br.org.funcate.jtdk.edition.event.SetStyleEvent;
import br.org.funcate.jtdk.edition.undoredo.event.UndoRedoActiveEvent;
import br.org.funcate.jtdk.model.dto.FeatureDTO;
import br.org.funcate.plugin.GluePluginService;
import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.controller.tools.DeleteTool;
import com.nexusbr.gv.controller.tools.FeatureSelectTool;
import com.nexusbr.gv.services.ChangesCommitService;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.SelectFeatureService;
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
	//private EventTransmitter transmitter;
	private ListenersHandler listeners;
	private EventHandler eventHandler;
	private List<String> eventsToListen;
	private Manager manager;
	private double showScaleValue;
	private ArrayList<String> idsOld;
	private ArrayList<String> streetIds;
	private ArrayList<String> lotIds;
	
	
	/**
	 * Creates the GVClient.
	 */	
	public void setPlugin(){
		//GluePluginService.insertPluginToolBar(toolbarPanel.getToolBarText(), createToolbar(), toolbarPanel.getToolBarTooltip());
	}
	
	/**
	 * this class constructor.
	 */
	public GVClient()
	{	
		GetLocaleEvent localeEvent = new GetLocaleEvent(this);
		manager = new Manager();
		GluePluginService.setCurrentTool(manager);
		try {
			manager.dispatch(manager.getTransmitter(), localeEvent);
		} catch (Exception e) {
			
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
		//this.transmitter = new DirectedEventTransmitter(this);

		this.eventsToListen = new ArrayList<String>();
		this.eventsToListen.add(UndoRedoActiveEvent.class.getName());
		this.eventsToListen.add(TreeThemeChangeEvent.class.getName());
		this.eventsToListen.add(UnselectedThemeEvent.class.getName());
		this.eventsToListen.add(SelectedThemeEvent.class.getName());
		this.eventsToListen.add(ScaleChangedEvent.class.getName());
		this.eventsToListen.add(BoxChangedEvent.class.getName());
		this.eventsToListen.add(DrawFeatureEvent.class.getName());
		this.eventsToListen.add(SelectFeatureEvent.class.getName());
		this.eventsToListen.add(CommitFeatureEvent.class.getName());

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
			manager.ShowLineFeaturesByID(manager.locateLinesByID(ids));	
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle3(), "LINE"));
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
		}	
	}
	
	public void drawFeaturePolygonById(ArrayList<String> ids) throws Exception{
		try {
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
			manager.ShowPolygonFeaturesByID(manager.locatePolygonsByID(ids));
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	public void initFeaturesIP(){
		try {
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
			
			ArrayList<FeatureDTO> points = manager.locatePointsIP();
			
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle(), "POINT"));
			manager.ShowPointFeatures(points);
			
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initFeaturesOS(){
		try {
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
			
			ArrayList<FeatureDTO> points = manager.locatePointsOS();
			
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle3(), "POINT"));
			manager.ShowPointFeatures(points);
			
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	public boolean initFeatures(){
		boolean hasFeatures = false;
		try {
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
			
			ArrayList<FeatureDTO> points = manager.locatePoints();
			
			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle3(), "POINT"));
			manager.ShowPointFeatures(points);
			
			ArrayList<FeatureDTO> lines = manager.locateLines();
			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
//			manager.ShowLineFeatures(lines);
			
//			dispatch(manager.getTransmitter(), new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
//			manager.ShowPolygonFeaturesByID(manager.locatePolygonsByID(ids));
			
			manager.dispatchLayersFeedback();
			manager.dispatchLayersEdition();
			
			if(!points.isEmpty()){
				hasFeatures = true;
			}else if (!lines.isEmpty()){
				hasFeatures = true;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasFeatures;
	}

	/**
	 * This method create a geovector toolbar.
	 * @return toolbarPanel a <code>JPanel</code> Component
	 */
	
	@SuppressWarnings("unused")
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
			
		}else if(event instanceof SelectFeatureEvent){
			this.handle((SelectFeatureEvent)event);
			
		}else if(event instanceof CommitFeatureEvent){
			this.handle((CommitFeatureEvent)event);
		}
	}
	
	private void handle(CommitFeatureEvent e){
		CustomNode nodeCurrentTheme = AppSingleton.getInstance()
				.getTreeState().getCurrentTheme();

		if (nodeCurrentTheme != null && nodeCurrentTheme.getTheme().getId()==21) {

			Theme theme = nodeCurrentTheme.getTheme();
			if (theme != null) {
				List<Representation> reps = theme.getReps();
				boolean validRep = false;
				for (Representation representation : reps) {
					if (representation.getId() == Representation.POINT) {
						validRep = true;
					}
				}
				if (validRep == false) {
					GlueMessageDialog.show("Por favor, selecione um tema OS!",null, 3);
					return;
				}
			} else {
				GlueMessageDialog.show("Por favor, selecione o tema OS!",null, 3);
				return;
			}

		} else {
			GlueMessageDialog.show("Por favor, selecione um tema OS!", null, 3);
			return;
		}
		
		//GluePluginService.setCurrentTool(new PointCreatorTool());
		//ChangesCommitService.saveOS(e.getOsCoords(), e.isSelected(), e.isNetwork(), e.getOsid(), e.getIp(), e.getIp(), e.getStatus());
		
		
		ChangesCommitService.saveOS(Double.parseDouble(e.getX()), Double.parseDouble(e.getY()), false, false, e.getOsid(), e.getOcurrence(),e.getIp(), 
					 "aberto");
		
	}
	
	private void handle(SelectFeatureEvent e){
		String type = e.getType();
		GluePluginService.setCurrentTool(new DeleteTool());
		GluePluginService.setCurrentTool(new FeatureSelectTool());
		
		if(type=="os"){
			initFeaturesOS();
		}else if (type=="ip"){
			initFeaturesIP();	
		}
	}
	
	
	private void handle(DrawFeatureEvent e){
		 streetIds = e.getLineIds(); 
		 lotIds = e.getPolygonIds();
		 
		 try {
			 if(streetIds!=null)
				drawFeatureLineById(streetIds);
			 else{
				 ArrayList<FeatureDTO> points = manager.locatePoints();
				 setOsAttributes(points);
			 }
			 if(lotIds!=null)
				drawFeaturePolygonById(lotIds);
				
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
	
	@SuppressWarnings("unused")
	private static double oldx1;
	@SuppressWarnings("unused")
	private static double oldy1;

	@SuppressWarnings("unused")
	private boolean start = false;
	@SuppressWarnings("unused")
	private boolean lock = false;

	private void handle(BoxChangedEvent event) throws Exception{
		
		//boolean editIsSet = GVSingleton.getInstance().getToolbar().isShowing();
		double scale = Math.round(AppSingleton.getInstance().getCanvasState().getScale());
		
//		String x1 = AppSingleton.getInstance().getCanvasState().getBox().getX1().toString();	
//		String y1 = AppSingleton.getInstance().getCanvasState().getBox().getY1().toString();
//		String x2 = String.valueOf(AppSingleton.getInstance().getCanvasState().getBox().getX2()/2);	
//		String y2 = String.valueOf(AppSingleton.getInstance().getCanvasState().getBox().getY2()/2);
		
		//double difX1 = Math.abs(oldx1 - x1);
		//double difY1 = Math.abs(oldy1 - y1);
		
		showScaleValue = 20.0;
	
		if(scale<=showScaleValue /*&& (difX1 > 430 || difY1 > 220)*/){
			@SuppressWarnings("unused")
			boolean hasFeatures = false;
			//oldx1 = AppSingleton.getInstance().getCanvasState().getBox().getX1();
			//oldy1 = AppSingleton.getInstance().getCanvasState().getBox().getY1();
			//limpa dataSource - remover quando implementar getfeatures com projeção virtual earth mercato 
			//hasFeatures = initFeatures();
			GluePluginService.setCurrentTool(new DeleteTool());
			initFeaturesOS();
//			if(hasFeatures)
//				cleanDataSource();
			//start = false;		
		}
	}

	public void cleanDataSource(){
		AppSingleton singleton = AppSingleton.getInstance();
		Mediator mediator = singleton.getMediator();
//		CanvasState state = singleton.getCanvasState();
//		state.setDataSource("OpenStreetMap");
//		ScreenRequetServices.setValue("OpenStreetMap");
		
		mediator.setToolBarSource(null);	
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
			//manager.ShowLineFeatures(manager.locateLines());
			//manager.ShowPointFeatures(manager.locatePoints());
			//manager.ShowPolygonFeatures(manager.locatePolygons());

			manager.dispatchLayersEdition();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setSelectFeature(){
		GetSelectFeatureEvent getSelectFeatureEvent = new GetSelectFeatureEvent(this);
		getSelectFeatureEvent.setFeatureId(SelectFeatureService.getFeatureId());
		getSelectFeatureEvent.setOsIP(SelectFeatureService.getOsIP());
		getSelectFeatureEvent.setOsX(SelectFeatureService.getOsX());
		getSelectFeatureEvent.setOsY(SelectFeatureService.getOsY());
		
		try {
			this.dispatch(manager.transmitter,getSelectFeatureEvent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setOsAttributes(ArrayList<FeatureDTO> pointsF){
		GetOsEvent getOsEvent = new GetOsEvent(this);
		ArrayList<String> osIDs = new ArrayList<String>();
		ArrayList<String> ocorrences = new ArrayList<String>();
		ArrayList<String> status = new ArrayList<String>();
		try {
			for (FeatureDTO featurePoint : pointsF) {
				SimpleFeature simpleFeature = featurePoint.getSimpleFeature();
				if(simpleFeature.getAttribute("osid")!=null){
					String osid = simpleFeature.getAttribute("osid").toString();
					osIDs.add(osid);
				}
				if(simpleFeature.getAttribute("ocurrence")!=null){
					String ocurrence = simpleFeature.getAttribute("ocurrence").toString();
					ocorrences.add(ocurrence);
				}
				if(simpleFeature.getAttribute("status")!=null){
					String st = simpleFeature.getAttribute("status").toString();
					status.add(st);	
				}		
			}
			
			getOsEvent.setOsIDs(osIDs);
			getOsEvent.setOcorrences(ocorrences);
			getOsEvent.setStatus(status);
			
			this.dispatch(manager.transmitter,getOsEvent);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
