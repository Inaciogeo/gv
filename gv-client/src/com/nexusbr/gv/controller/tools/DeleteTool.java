package com.nexusbr.gv.controller.tools;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JOptionPane;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.tool.PanTool;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.plugin.GluePluginService;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This class recive the event Eagle, And Delete the selected Geometries
 *
 * @author Bruno Severino
 * @version 1.0
 */
public class DeleteTool extends Manager{

	List<SimpleFeature> listToDelete;
	List<SimpleFeature> listToDelete2;
	SimpleFeatureCollection pointCollection;
	SimpleFeatureCollection lineCollection;
	SimpleFeatureCollection polygonCollection;

	public DeleteTool(){
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());
	}
	@Override
	public void handle(EventObject e)  throws Exception{
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT		
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}
	}

	private void handle(AfterToolChangedEvent e) throws Exception{
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getPolygon = new GetFeatureEvent(this, "POLYGON");

		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		dispatch(transmitter, getPolygon);
		
		////
		// separa para remoção pontos e linhas associadas
		////
		pointCollection = getPoint.getFeatureCollection();
		lineCollection = getLine.getFeatureCollection();
		polygonCollection = getPolygon.getFeatureCollection();
		
		SimpleFeatureIterator featureIterator = pointCollection.features();
		List<SimpleFeature> featuresToDelete = new ArrayList<SimpleFeature>();
		while(featureIterator.hasNext()){
			SimpleFeature feature = featureIterator.next();
			
			if(feature.getAttribute("selected").equals(true)){
				featuresToDelete.add(feature);				
				feature.setAttribute("selected", false);
				//procura linhas apenas se é modo rede
				if(feature.getAttribute("networkMode").equals(1)){
					findLinesToRemove(feature.getID());
				}
			}
		}
		////
		// separa para remoção linhas e pontos órfãos
		////
		
		featureIterator = lineCollection.features();
		while(featureIterator.hasNext()){
			SimpleFeature feature = featureIterator.next();
			
			if(feature.getAttribute("selected").equals(true)){
				featuresToDelete.add(feature);				
				feature.setAttribute("selected", false);
				//verifica pontos apenas se é linha no modo rede
				if(feature.getAttribute("networkMode").equals(1)){
					String startPointID = (String) feature.getAttribute("point1");
					SimpleFeature lineStartPoint = findPointByID(startPointID);
					if(isOrphanedPoint(lineStartPoint, feature))
						featuresToDelete.add(lineStartPoint);
					String endPointID = (String) feature.getAttribute("point2");
					SimpleFeature lineEndPoint = findPointByID(endPointID);
					if(isOrphanedPoint(lineEndPoint, feature))
						featuresToDelete.add(lineEndPoint);
				}
			}
		}
		////
		// separa para remoção polígonos
		////
		
		featureIterator = polygonCollection.features();
		while(featureIterator.hasNext()){
			SimpleFeature feature = featureIterator.next();
			
			if(feature.getAttribute("selected").equals(true)){
				featuresToDelete.add(feature);				
				feature.setAttribute("selected", false);
			}
		}
		
		////
		// pergunta se deseja remover e limpa todos os selecionados nos loops anteriores
		////
		int option = 0;//JOptionPane.showConfirmDialog(null, GVSingleton.getInstance().getLanguage().getString("confDelete.text"));
		if(option == 0){
			for(SimpleFeature feature : featuresToDelete){
				if(feature.getDefaultGeometry() instanceof Point){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "POINT", true));
					GVSingleton.getInstance().getPointsDeleted().add(feature);
				}else if(feature.getDefaultGeometry() instanceof LineString){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "LINE", true));
					GVSingleton.getInstance().getLinesDeleted().add(feature);
				}else if(feature.getDefaultGeometry() instanceof Polygon){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "POLYGON", true));
					GVSingleton.getInstance().getPolygonDeleted().add(feature);
				}
			}
		}

		dispatchLayersEdition();
		
		GluePluginService.setCurrentTool(new PanTool());
	}
	
	private SimpleFeature findPointByID(String ID){
		SimpleFeatureIterator iterator = pointCollection.features();
		
		while (iterator.hasNext()){
			SimpleFeature feature = iterator.next();
			if(feature.getID().equals(ID))
				return feature;
		}
		return null;
	}
	
	private boolean isOrphanedPoint(SimpleFeature point, SimpleFeature line){
		SimpleFeatureIterator iterator = lineCollection.features();
		
		while (iterator.hasNext()){
			SimpleFeature feature = iterator.next();
			if(feature!=line){
				Geometry pointGeometry = (Geometry)point.getDefaultGeometry();
				Geometry lineGeometry = (Geometry)feature.getDefaultGeometry();
				if( pointGeometry.intersects(lineGeometry) && feature.getAttribute("selected").equals(false))
					return false;
			}
		}
		
		return true;
	}
	
	private void findLinesToRemove(String pointID){
		SimpleFeatureIterator iterator = lineCollection.features();

		while (iterator.hasNext()){
			SimpleFeature feature = iterator.next();
			if(feature.getAttribute("selected").equals(false)){
				String startPointID = (String) feature.getAttribute("point1");
				String endPointID = (String) feature.getAttribute("point2");
				if(pointID == startPointID || pointID == endPointID){
					feature.setAttribute("selected", true);
				}
			}
		}
	}
	
}
