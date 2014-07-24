package com.nexusbr.gv.controller.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Representation;
import br.org.funcate.glue.model.tree.CustomNode;
import br.org.funcate.glue.view.NodeType;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.jtdk.model.dto.FeatureDTO;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.services.ChangesCommitService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Geometry;


/**
 * 
 * @author BRUNO SEVERINO
 *
 */
public class CommitTool extends Manager{

	/**
	 * 
	 */
	public CommitTool(){		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());		
	}

	/**
	 * 
	 */
	public void handle(EventObject e) throws Exception {
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}
	}

	/**
	 * 
	 * @param e
	 * @throws Exception
	 */
	private void handle(AfterToolChangedEvent e) throws Exception{
		
		/* UPDATES AND DELETE POINTS, THEM ADD TO ARRALIST */

		/*  SAVE FEATURES */
		/* REPLACE FEATURES */
		ChangesCommitService service = new ChangesCommitService();
		CustomNode themeNode = AppSingleton.getInstance().getTreeState().getCurrentTheme();
		
		if (themeNode.getNodeType() == NodeType.NETWORK_THEME) {
			SimpleFeatureCollection points = forPoints();
			if (points.isEmpty() == false) {
				if (service.saveFeatures(points, Representation.POINT)) {
					//replacePoints();
				}
			}

			SimpleFeatureCollection lines = forLines();
			if (lines.isEmpty() == false) {
				if (service.saveFeatures(lines, Representation.LINE)) {
					//replaceLines();
				}
			}
		}else if(themeNode.getNodeType() == NodeType.DEFAULT){
			SimpleFeatureCollection lines = forLines();
			if (lines.isEmpty() == false) {
				
				if (service.saveFeatures(lines, Representation.LINE)) {
					//replaceLines();
				}
			}
		}
		
		SimpleFeatureCollection polygons = forPolygon();
		if(polygons.isEmpty() == false){
			if(service.saveFeatures(polygons, Representation.POLYGON)){
				//replacePolygon();
			}
			else{
				
			}
		}

		/* REDRAW CANVAS VIEW */
		AppSingleton.getInstance().getMediator().reDrawView();
	}

	/**
	 * To Change Commits for Points (Delete, Update)
	 * @return collection of Points
	 * @throws Exception
	 * @see {@link SimpleFeatureCollection}
	 */
	private SimpleFeatureCollection forPoints() throws Exception{
		SimpleFeatureCollection collection = getCollection("POINT");	
		
		GVSingleton gvSingleton = GVSingleton.getInstance();

		/* DELETE FEATURES */
		ArrayList<SimpleFeature> deletedFeatures = gvSingleton.getPointsDeleted();
		if(deletedFeatures.isEmpty() == false){
			deleteFeatures( deletedFeatures, Representation.POINT);
		}

		/* UPDATE FEATURE */
		ArrayList<SimpleFeature> editedFeatures =  gvSingleton.getPointsEdited();
		if(editedFeatures.isEmpty() == false){
			updateFeatures(editedFeatures );
		}

		//REMOVE THE OBJECT LIST THAT WASN'T USED
		
		Set<String> faeturesIds = gvSingleton.getPointIDs();
		removeFromList(collection, faeturesIds, deletedFeatures, editedFeatures );

		//CLEAR ARRAYLISTs
		faeturesIds.clear();
		deletedFeatures.clear();
		editedFeatures.clear();

		return collection;
	}

	/**
	 * To Change Commits for Lines (Delete, Update)
	 * @return collection of Lines
	 * @throws Exception
	 * @see {@link SimpleFeatureCollection}
	 */
	private SimpleFeatureCollection forLines() throws Exception{
		SimpleFeatureCollection collection = getCollection("LINE");		
		
		GVSingleton gvSingleton = GVSingleton.getInstance();
		
		/* DELETE FEATURES */
		ArrayList<SimpleFeature> deletedFeatures = gvSingleton.getLinesDeleted();
		if(deletedFeatures.isEmpty() == false){
			deleteFeatures( deletedFeatures, Representation.LINE);
		}

		/* UPDATE FEATURE */
		ArrayList<SimpleFeature> editedFeatures =  gvSingleton.getLinesEdited();
		if(editedFeatures.isEmpty() == false){
			updateFeatures(editedFeatures );
		}

		//REMOVE THE OBJECT LIST THAT WASN'T USED
		
		Set<String> faeturesIds = gvSingleton.getLineIDs();
		removeFromList(collection, faeturesIds, deletedFeatures, editedFeatures);		

		//CLEAR ARRAYLISTs
		faeturesIds.clear();
		deletedFeatures.clear();
		editedFeatures.clear();

		return collection;
	}

	/**
	 * To Change Commits for Polygon (Delete, Update)
	 * @return collection of Polygon
	 * @throws Exception
	 * @see {@link SimpleFeatureCollection}
	 */
	private SimpleFeatureCollection forPolygon() throws Exception{
		SimpleFeatureCollection collection = getCollection("POLYGON");		
		
		GVSingleton gvSingleton = GVSingleton.getInstance();
		
		/* DELETE FEATURES */
		ArrayList<SimpleFeature> deletedFeatures = gvSingleton.getPolygonDeleted();
		if(deletedFeatures.isEmpty() == false){
			deleteFeatures( deletedFeatures, Representation.POLYGON);
		}

		/* UPDATE FEATURE */
		ArrayList<SimpleFeature> editedFeatures =  gvSingleton.getPolygonEdited();
		if(editedFeatures.isEmpty() == false){
			updateFeatures(editedFeatures );
		}

		//REMOVE THE OBJECT LIST THAT WASN'T USED
		
		Set<String> faeturesIds = gvSingleton.getPolygonIDs();
		removeFromList(collection, faeturesIds, deletedFeatures, editedFeatures );		

		//CLEAR ARRAYLISTs
		faeturesIds.clear();
		deletedFeatures.clear();
		editedFeatures.clear();
		
		return collection;
	}

	/**
	 * Replace Saved Points, updating the id
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void replacePoints() throws Exception{		
		SimpleFeatureCollection pointCollection = getCollection("POINT");		
		ArrayList<FeatureDTO> feat = locatePoints();
		ArrayList<SimpleFeature> featuresToDelete = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator it = pointCollection.features();
		while (it.hasNext()) {
			SimpleFeature sfeature = it.next();
			for (FeatureDTO feature : feat) {
				Geometry point = (Geometry)feature.getSimpleFeature().getDefaultGeometry();
				if(point.intersects((Geometry) sfeature.getDefaultGeometry())){
					featuresToDelete.add(sfeature);
				}
			}
		}
		for(SimpleFeature sfeature : featuresToDelete){
			dispatch(transmitter, new FeatureRemovedEvent(this, sfeature, "POINT", true));
		}
		ShowPointFeatures(feat);
	}

	/**
	 * Replace Saved Line, updating the id
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void replaceLines() throws Exception{		
		SimpleFeatureCollection collection = getCollection("LINE");		
		ArrayList<FeatureDTO> feat = locateLines();		
		ArrayList<SimpleFeature> featuresToDelete = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator it = collection.features();
		while (it.hasNext()) {
			SimpleFeature sfeature = it.next();
			for (FeatureDTO feature : feat) {
				Geometry line = (Geometry)feature.getSimpleFeature().getDefaultGeometry();		
				if(line.intersects((Geometry) sfeature.getDefaultGeometry())){
					featuresToDelete.add(sfeature);
				}
			}
		}
		for(SimpleFeature sfeature : featuresToDelete){
			dispatch(transmitter, new FeatureRemovedEvent(this, sfeature, "LINE", true));
		}		
		ShowLineFeatures(feat);
	}

	/**
	 * Replace Saved Polygon, updating the id
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void replacePolygon() throws Exception{		
		SimpleFeatureCollection collection = getCollection("POLYGON");		
		ArrayList<FeatureDTO> feat = locateLines();
		ArrayList<SimpleFeature> featuresToDelete = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator it = collection.features();
		while (it.hasNext()) {
			SimpleFeature sfeature = it.next();
			for (FeatureDTO feature : feat) {
				Geometry geom = (Geometry)feature.getSimpleFeature().getDefaultGeometry();
				if(geom.intersects((Geometry) sfeature.getDefaultGeometry())){
					featuresToDelete.add(sfeature);
					
				}
			}
		}	
		for(SimpleFeature sfeature : featuresToDelete){
			dispatch(transmitter, new FeatureRemovedEvent(this, sfeature, "POLYGON", true));
		}
		ShowLineFeatures(feat);
	}

	/**
	 * 
	 * @param collection
	 * @param featIDs
	 * @param featDeleted
	 * @param featEdited
	 */
	public void removeFromList(SimpleFeatureCollection collection, Set<String> featIDs, ArrayList<SimpleFeature> featDeleted, ArrayList<SimpleFeature> featEdited) {
		collection.removeAll(featEdited);
		collection.removeAll(featDeleted);
		Collection<SimpleFeature> coll = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator iterator = collection.features();
		while (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			for (String id : featIDs) {
				if(feature.getID().equals(id)){
					coll.add(feature);					
				}
			}
		}
		collection.removeAll(coll);	
	}

	/**
	 * 
	 * @param featEdited
	 */
	public void updateFeatures(ArrayList<SimpleFeature> featEdited) {
		List<SimpleFeature> featureEdited = featEdited;		
		HashMap<String, SimpleFeature> hashFeature = new HashMap<String, SimpleFeature>();
		for (SimpleFeature simpleFeature : featureEdited) {
			hashFeature.put(simpleFeature.getID(), simpleFeature);
		}
		new ChangesCommitService().updateFeatures(hashFeature);
	}

	/**
	 * 
	 * @param featDeleted
	 */
	public void deleteFeatures(ArrayList<SimpleFeature> featDeleted, int representation) {
		List<String> toDelete = new ArrayList<String>();
		
		for(SimpleFeature feature : featDeleted)
		{
			toDelete.add(feature.getID());
		}
		
		new ChangesCommitService().removeFeatures(toDelete, representation);
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public SimpleFeatureCollection getCollection(String name) throws Exception{
		GetFeatureEvent event = new GetFeatureEvent(this, name);
		dispatch(transmitter, event);
		return event.getFeatureCollection();		
	}
}

