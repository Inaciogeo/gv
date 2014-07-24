package com.nexusbr.gv.controller;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.eagles.kernel.transmitter.EventTransmitter;
import br.org.funcate.glue.event.CleanBufferEvent;
import br.org.funcate.glue.event.DrawLayersEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Box;
import br.org.funcate.glue.model.Projection;
import br.org.funcate.glue.model.Representation;
import br.org.funcate.glue.model.Theme;
import br.org.funcate.glue.model.View;
import br.org.funcate.glue.model.canvas.BufferEnum;
import br.org.funcate.glue.model.tree.TreeState;
import br.org.funcate.glue.service.TerraJavaClient;
import br.org.funcate.glue.tool.Tool;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.jtdk.model.FeatureManipulation;
import br.org.funcate.jtdk.model.FeatureManipulationService;
import br.org.funcate.jtdk.model.dto.FeatureDTO;

import com.nexusbr.gv.model.DTO.PointReference;
import com.nexusbr.gv.services.LineCreatorService;
import com.nexusbr.gv.services.PointCreatorService;
import com.nexusbr.gv.services.PolygonCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.nexusbr.gv.view.components.ToolbarWindow;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class Manager implements Tool {

	// VARIAVEIS GLOBAIS
	public Cursor cursor;
	public ListenersHandler listeners;
	public EventHandler eventHandler;
	public EventTransmitter transmitter;
	public ArrayList<String> eventsToListen;
	public DrawLayersEvent drawLayers;
	public ToolbarWindow toolbar;
	public double pointSnapTolerance = 15;
	public double lineSnapTolerance = 15;
	public double polygonSnapTolerance = 15;
	public ArrayList<SimpleFeature> pointsList;
	public ArrayList<SimpleFeature> linesList;

	private FeatureManipulation featureManipulation;
	private String point1;
	private String point2;

	private String object_id_point;
	private double idPoint;
	private String object_id_line;
	private double idLine;
	private Theme th;
	
	public Manager() {
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);
		featureManipulation = new FeatureManipulationService();

	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public ListenersHandler getListenersHandler() {
		return this.listeners;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public void dispatch(EventTransmitter tc, EventObject e) throws Exception {
		tc.dispatch(e);
	}

	public EventTransmitter getTransmitter() {
		return this.transmitter;
	}

	public void handle(EventObject e) throws Exception {

	}

	public List<String> getEventsToListen() {
		return eventsToListen;
	}

	public void dispatchLayersEdition() {
		drawLayers = new DrawLayersEvent(this, BufferEnum.EDITION);
		drawLayers.getLayerNames().add("POLYGON");
		drawLayers.getLayerNames().add("LINE");
		drawLayers.getLayerNames().add("POINT");

		try {
			dispatch(transmitter, drawLayers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dispatchLayersFeedback() {
		drawLayers = new DrawLayersEvent(this, BufferEnum.FEEDBACK);
		drawLayers.getLayerNames().add("GHOSTLINE");
		drawLayers.getLayerNames().add("GHOSTPOINT");
		drawLayers.getLayerNames().add("SNAPLINE");
		drawLayers.getLayerNames().add("SNAPPOINT");
		drawLayers.getLayerNames().add("SELECTBOX");
		drawLayers.getLayerNames().add("REFCIRCLE");
		drawLayers.getLayerNames().add("REFCIRCLELINE");
		drawLayers.getLayerNames().add("EDITPATH_BOX");
		drawLayers.getLayerNames().add("EDITPATH_RESIZE");
		drawLayers.getLayerNames().add("EDITPATH_EDGES");

		try {
			dispatch(transmitter, drawLayers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param themeName
	 * @return theme
	 */
	public Theme getTheme(String themeName) {
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		Theme theme = null;
		for (Theme them : view.getThemes()) {
			if (them.getName().equals(themeName)) {
				theme = them;
			}
		}
		return theme;
	}

	/**
	 * 
	 * @param layerEditionName
	 * @param featIDs
	 * @param representation
	 * @return list
	 * @throws Exception
	 */
	public ArrayList<String> getFeaturesID(String layerEditionName,
			Set<String> featIDs) throws Exception {
		Collection<String> listFeat = new ArrayList<String>();
		GetFeatureEvent getFeature = new GetFeatureEvent(this, layerEditionName);
		dispatch(transmitter, getFeature);
		SimpleFeatureCollection collection = getFeature.getFeatureCollection();
		SimpleFeatureIterator iterator = collection.features();
		while (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			listFeat.add(feature.getID());
		}

		Collection<String> similar = new HashSet<String>(featIDs);
		similar.retainAll(listFeat);
		Collection<String> different = new HashSet<String>();
		different.addAll(featIDs);
		different.removeAll(similar);

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(different);

		return list;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<FeatureDTO> locatePoints() throws Exception {
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		List<Theme> themes = view.getThemes();
		
		ArrayList<FeatureDTO> listF = new ArrayList<FeatureDTO>();
		for (Theme theme : themes) {
			if(!theme.isVisibility())
				continue;
			
			for (Representation rep : theme.getReps()) {
				if (rep.getId() == 4l) {
					Projection layerProjection = theme.getLayer()
							.getProjection();
					Projection canvasProjection = AppSingleton.getInstance()
							.getCanvasState().getProjection();
					Box currentBox = AppSingleton.getInstance()
							.getCanvasState().getBox();

					TerraJavaClient services = AppSingleton.getInstance()
							.getServices();
					Box convertedBox = services.remapCoordinates(currentBox,
							canvasProjection, layerProjection);

					ArrayList<FeatureDTO> listObj = featureManipulation
							.getFeatures(convertedBox, theme);
					for (FeatureDTO feature : listObj) {
						GVSingleton.getInstance().getPointIDs()
								.add(feature.getObjectId());
					}

					// VERIFY IF THERE ALREADY EXIST THE POINT IN SCREEN
					@SuppressWarnings("unused")
					ArrayList<String> listIDs = getFeaturesID("POINT",
							GVSingleton.getInstance().getPointIDs());

					// GET FEATURES From Object Ids
					listF.addAll(listObj);
				}
			}
		}

		return listF;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<FeatureDTO> locateLines() throws Exception {
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		List<Theme> themes = view.getThemes();
		
		ArrayList<FeatureDTO> listF = new ArrayList<FeatureDTO>();
		
		for (Theme theme : themes) {
			if(!theme.isVisibility())
				continue;
			
			for (Representation rep : theme.getReps()) {
				if (rep.getId() == 2l) {
					Projection layerProjection = theme.getLayer().getProjection();
					Projection canvasProjection = AppSingleton.getInstance().getCanvasState().getProjection();
					Box currentBox = AppSingleton.getInstance().getCanvasState().getBox();

					TerraJavaClient services = AppSingleton.getInstance().getServices();
					Box convertedBox = services.remapCoordinates(currentBox,canvasProjection,layerProjection);

					ArrayList<FeatureDTO> listObj = featureManipulation.getFeatures(convertedBox, theme);
					for (FeatureDTO feature : listObj) {
						GVSingleton.getInstance().getLineIDs().add(feature.getObjectId());
					}

					// VERIFY IF THERE ALREADY EXIST THE POINT IN SCREEN
					@SuppressWarnings("unused")
					ArrayList<String> listIDs = getFeaturesID("LINE",
							GVSingleton.getInstance().getLineIDs());

					// GET FEATURES From Object Ids
					listF.addAll(listObj);

				}
			}
			
		}
		
		return listF;
	}
	
	public ArrayList<FeatureDTO> locateLinesByID(ArrayList<String> ids){
		
		ArrayList<FeatureDTO> listF = new ArrayList<FeatureDTO>();
		
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		List<Theme> themes = view.getThemes();
		
		for (Theme theme : themes) {
			if(theme.getId() == 13){ // ID do tema de arruamentos,(id gerado no banco para o tema de arruamentos). 
				th = theme;
			}
		}
		
		ArrayList<FeatureDTO> listObj = featureManipulation.getFeatures(ids, th);
		for (FeatureDTO feature : listObj) {
			GVSingleton.getInstance().getLineIDs().add(feature.getObjectId());
			
		}
		listF.addAll(listObj);
		
		return listF  ;
		
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<FeatureDTO> locatePolygons() throws Exception {
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		List<Theme> themes = view.getThemes();
		
		ArrayList<FeatureDTO> listF = new ArrayList<FeatureDTO>();

		for (Theme theme : themes) {
			if(!theme.isVisibility())
				continue;
			
			for (Representation rep : theme.getReps()) {
				if (rep.getId() == 1l) {
					Projection layerProjection = theme.getLayer()
							.getProjection();
					Projection canvasProjection = AppSingleton.getInstance()
							.getCanvasState().getProjection();
					Box currentBox = AppSingleton.getInstance()
							.getCanvasState().getBox();

					TerraJavaClient services = AppSingleton.getInstance()
							.getServices();
					Box convertedBox = services.remapCoordinates(currentBox,
							canvasProjection, layerProjection);

					ArrayList<FeatureDTO> listObj = featureManipulation
							.getFeatures(convertedBox, theme);
					for (FeatureDTO feature : listObj) {
						GVSingleton.getInstance().getPolygonIDs().add(feature.getObjectId());
					}

					// VERIFY IF THERE ALREADY EXIST THE POINT IN SCREEN
					@SuppressWarnings("unused")
					ArrayList<String> listIDs = getFeaturesID("POLYGON",GVSingleton.getInstance().getPolygonIDs());

					// GET FEATURES From Object Ids
					listF.addAll(listObj);
				}
			}
		}
		return listF;
	}
	public ArrayList<FeatureDTO> locatePolygonsByID(ArrayList<String> ids) throws Exception {
		ArrayList<FeatureDTO> listF = new ArrayList<FeatureDTO>();
		
		AppSingleton singleton = AppSingleton.getInstance();
		TreeState state = singleton.getTreeState();
		View view = state.getCurrentView().getView();
		List<Theme> themes = view.getThemes();
		
		for (Theme theme : themes) {
			if(theme.getId()==14){ // ID do tema lotes: id gerado no modelo bd.
				th = theme;
			}
		}
		
		ArrayList<FeatureDTO> listObj = featureManipulation.getFeatures(ids, th);
		for (FeatureDTO feature : listObj) {
			GVSingleton.getInstance().getPolygonIDs().add(feature.getObjectId());
		}

		// VERIFY IF THERE ALREADY EXIST THE POINT IN SCREEN
		@SuppressWarnings("unused")
		ArrayList<String> listIDs = getFeaturesID("POLYGON",GVSingleton.getInstance().getPolygonIDs());

		// GET FEATURES From Object Ids
		listF.addAll(listObj);
		
		return listF;
	}

	/**
	 * 
	 * @param featureCollection
	 * @throws Exception
	 */
	
	public void ShowPointFeatures(ArrayList<FeatureDTO> listF) throws Exception {
		
		CleanBufferEvent clearEvent = new CleanBufferEvent(this, BufferEnum.EDITION);
		dispatch(transmitter, clearEvent);
		
		for (FeatureDTO feature : listF) {
			
			SimpleFeature simpleFeature = feature.getSimpleFeature();
			Point point = (Point) simpleFeature.getDefaultGeometry();
			Geometry geom = new PointCreatorService().createGeomPoint(new Coordinate(point.getX(), point.getY()));
			
			boolean selected = false;
			
			Boolean networkMode = (Boolean) simpleFeature.getAttribute("networkmode");
			if (networkMode == null){
				networkMode = true;
			}
			object_id_point = simpleFeature.getAttribute("object_id").toString();
			
			if(!object_id_point.isEmpty() && object_id_point != null && object_id_point !=""){
				idPoint = Double.parseDouble(object_id_point);
				
				@SuppressWarnings("unused")
				SimpleFeature feat = new PointCreatorService().createPoint(geom,
						selected, networkMode, String.valueOf(idPoint));
				dispatch(transmitter, new FeatureCreatedEvent(this, simpleFeature, "POINT", true));
				
			}else{
				
				@SuppressWarnings("unused")
				SimpleFeature feat = new PointCreatorService().createPoint(geom,
						selected, networkMode,simpleFeature.getID());
				dispatch(transmitter, new FeatureCreatedEvent(this, simpleFeature, "POINT", true));
			}		
			
		}
		
	}

	/**
	 * 
	 * @param listF
	 * @throws Exception
	 */
	
	public void ShowLineFeatures(ArrayList<FeatureDTO> listF) throws Exception {
		
		for (FeatureDTO feature : listF) {
			
			SimpleFeature simpleFeature = feature.getSimpleFeature();
			LineString geom = (LineString) simpleFeature.getDefaultGeometry();
			boolean selected = false;
			Boolean networkMode = false;//(Boolean) simpleFeature.getAttribute("networkmode");
			
			if (simpleFeature.getAttributes().size() > 15) {
				networkMode = true;
				 point1 = simpleFeature.getAttribute("noinic").toString();
				 point2 = simpleFeature.getAttribute("nofinal").toString();
			}
			
			object_id_line = simpleFeature.getAttribute("object_id").toString();
			
			if(!object_id_line.isEmpty() && object_id_line != null && object_id_line !=""){
				idLine = Double.parseDouble(object_id_line);
				SimpleFeature feat = new LineCreatorService().createLine(
						geom, selected, networkMode, point1, point2, String.valueOf(idLine), 
						new ArrayList<PointReference>(), new ArrayList<PointReference>());
				
				dispatch(transmitter, new FeatureCreatedEvent(this, feat, "LINE",true));
			}else{
				SimpleFeature feat = new LineCreatorService().createLine(
						geom, selected, networkMode, point1, point2, simpleFeature.getID(), 
						new ArrayList<PointReference>(), new ArrayList<PointReference>());
				
				dispatch(transmitter, new FeatureCreatedEvent(this, feat, "LINE",true));
			}
		}
	}
	
	public void ShowLineFeaturesByID(ArrayList<FeatureDTO> listF) throws Exception {
		
		for (FeatureDTO feature : listF) {
			
			SimpleFeature simpleFeature = feature.getSimpleFeature();
			LineString geom = (LineString) simpleFeature.getDefaultGeometry();
			boolean selected = true;
			Boolean networkMode = false;//(Boolean) simpleFeature.getAttribute("networkmode");
			
			object_id_line = simpleFeature.getAttribute("object_id").toString();
			
			if(!object_id_line.isEmpty() && object_id_line != null && object_id_line !=""){
				idLine = Double.parseDouble(object_id_line);
				SimpleFeature feat = new LineCreatorService().createLine(
						geom, selected, networkMode, point1, point2, String.valueOf(idLine), 
						new ArrayList<PointReference>(), new ArrayList<PointReference>());
				
				dispatch(transmitter, new FeatureCreatedEvent(this, feat, "LINE",true));
			}else{
				SimpleFeature feat = new LineCreatorService().createLine(
						geom, selected, networkMode, point1, point2, simpleFeature.getID(), 
						new ArrayList<PointReference>(), new ArrayList<PointReference>());
				
				dispatch(transmitter, new FeatureCreatedEvent(this, feat, "LINE",true));
			}
		}
	}
	

	/**
	 * 
	 * @param listF
	 * @throws Exception
	 */
	public void ShowPolygonFeatures(ArrayList<FeatureDTO> listF) throws Exception {
		for (FeatureDTO feature : listF) {
			SimpleFeature simpleFeature = feature.getSimpleFeature();
			Polygon geom = (Polygon)simpleFeature.getDefaultGeometry();

			SimpleFeature feat = new PolygonCreatorService().createPolygon(
					geom, false, feature.getObjectId());
			dispatch(transmitter, new FeatureCreatedEvent(this, feat,
					"POLYGON", true));

			// Creates another feature with attribute 'selected', which is used
			// to display selected geometries in draw
			SimpleFeature featureToDraw = new PolygonCreatorService()
					.createPolygon(feature.getSimpleFeature());
			dispatch(transmitter, new FeatureCreatedEvent(this, featureToDraw,
					"POLYGON", true));
		}
	}
	
	public void ShowPolygonFeaturesByID(ArrayList<FeatureDTO> listF) throws Exception {
		for (FeatureDTO feature : listF) {
			SimpleFeature simpleFeature = feature.getSimpleFeature();
			Polygon geom = (Polygon)simpleFeature.getDefaultGeometry();

			SimpleFeature feat = new PolygonCreatorService().createPolygon(
					geom, false, feature.getObjectId());
			dispatch(transmitter, new FeatureCreatedEvent(this, feat,
					"POLYGON", true));

			// Creates another feature with attribute 'selected', which is used
			// to display selected geometries in draw
			SimpleFeature featureToDraw = new PolygonCreatorService()
					.createPolygon(feature.getSimpleFeature());
			dispatch(transmitter, new FeatureCreatedEvent(this, featureToDraw,
					"POLYGON", true));
		}
	}

	public LineString getLineFromSpatialData(String sData) throws Exception {
		String spatialData = sData;
		spatialData = spatialData.replace(",", " ");
		spatialData = spatialData.replace(") (", "),(");
		spatialData = spatialData.replace(")", "");
		spatialData = spatialData.replace("(", "");
		String[] array = spatialData.split(",");
		ArrayList<String> coord = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			coord.add(array[i].trim());
		}

		String wktLine = "LINESTRING (";
		for (int i = 0; i < coord.size(); i++) {
			if (i == coord.size() - 1) {
				wktLine += coord.get(i) + ")";
			} else {
				wktLine += coord.get(i) + ", ";
			}
		}
		return new LineCreatorService().createLine(wktLine);
	}

	public Polygon getPolygonFromSpatialData(String sData) throws Exception {
		String spatialData = sData;
		spatialData = spatialData.replace(",", " ");
		spatialData = spatialData.replace(") (", "),(");
		spatialData = spatialData.replace(")", "");
		spatialData = spatialData.replace("(", "");
		String[] array = spatialData.split(",");
		ArrayList<String> coord = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			coord.add(array[i].trim());
		}

		String wkt = "POLYGON ((";
		for (int i = 0; i < coord.size(); i++) {
			if (i == coord.size() - 1) {
				wkt += coord.get(i) + "))";
			} else {
				wkt += coord.get(i) + ", ";
			}
		}
		return (Polygon) new WKTReader().read(wkt);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void changeCursorToDraw() throws Exception {
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
				Toolkit.getDefaultToolkit().createImage(
						getClass().getResource(
								"/com/nexusbr/gv/images/curReleased.png")),
				new java.awt.Point(16, 16), "draw");
		dispatch(transmitter, new UpdateCursorEvent(this));
	}

}
