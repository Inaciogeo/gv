package com.nexusbr.gv.controller.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JOptionPane;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.KeyPressedEvent;
import br.org.funcate.glue.event.MouseDraggedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Projection;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.jtdk.edition.event.SetStyleEvent;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.PolygonCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class FeatureSelectTool extends Manager {
	private double x1;
	private double y1;
	private boolean pointFound;
	private boolean lineFound;
	private boolean polyFound;
	private boolean firstLineNW;
	private boolean firstTimeIntersecting;
	private boolean firstTime = false;
	private SimpleFeature selectBox;
	private List<SimpleFeature> listToDelete;
	private List<SimpleFeature> listToDelete2;
	private double    precision;
	
	public FeatureSelectTool(){
		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);
		
		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());	
		eventsToListen.add(BeforeToolChangedEvent.class.getName());
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());		
		eventsToListen.add(MouseDraggedEvent.class.getName());				
		eventsToListen.add(KeyPressedEvent.class.getName());
		
		//CHANGE CURSOR TO PRESSED
		cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		
		firstTimeIntersecting = true;
		firstLineNW = false;
	}
	
	@Override
	public void handle(EventObject e) throws Exception {
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}
		
		else if(e instanceof BeforeToolChangedEvent){
			handle((BeforeToolChangedEvent) e);
		}
		
		else if(e instanceof MousePressedEvent){
			handle((MousePressedEvent)e);
		}
		
		else if(e instanceof MouseReleasedEvent){
			handle((MouseReleasedEvent)e);
		}
		
		else if(e instanceof MouseDraggedEvent){
			handle((MouseDraggedEvent) e);
		}
		
		else if(e instanceof KeyPressedEvent){
			handle((KeyPressedEvent) e);
		}
		
	}
	
	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE SELECTBOX STYLE
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSelectBoxStyle(), "SELECTBOX"));
		
	}
		
	private void handle(BeforeToolChangedEvent e) throws Exception{
		dispatch(transmitter,  new FeatureRemovedEvent(this, selectBox, "SELECTBOX", false));
		dispatchLayersFeedback();
	}
	
	private void handle(MousePressedEvent e) throws Exception {
		AppSingleton singleton = AppSingleton.getInstance();
		double scale = singleton.getCanvasState().getScale();
		String proj = singleton.getCanvasState().getProjection().getName();
		 if(proj.equals("LatLong"))
			 precision = scale*0.00000003;
		 else if(proj.equals("UTM"))
			 precision = scale*0.0000015;
		
		double xs1 = e.getX()+precision;
		double ys1 = e.getY();
		
		double xs2 = e.getX()+(e.getX()*precision);
		double ys2 = e.getY();
		
		double xs3 = e.getX()+(e.getX()*precision);
		double ys3 = e.getY()+(e.getX()*precision);
		
		double xs4 = e.getX();
		double ys4 = e.getY()+(e.getX()*precision);
		
		firstTime = true;
		Coordinate[] coords  = new Coordinate[] {
				new Coordinate(xs1, ys1), 
				new Coordinate(xs2, ys2), 
				new Coordinate(xs3, ys3),  
				new Coordinate(xs4, ys4),  
				new Coordinate(xs1, ys1) 
		};
		Geometry polygon = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(coords), null); 		
		createSelectBox(polygon);
		//dispatch(transmitter,  new FeatureCreatedEvent(this, selectBox, "SELECTBOX", false));
		intersectGeom(polygon);		
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}
	
	private void handle(MouseReleasedEvent e) throws Exception {
		SimpleFeature aux = selectBox;
		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "SELECTBOX", false));
		dispatchLayersFeedback();
		dispatchLayersEdition();
		
		firstTime = true;
		firstTimeIntersecting = true;
	}
	
	private void handle(MouseDraggedEvent e) throws Exception{
		
//		double x2 = e.getX();
//    	double y2 = e.getY();    	
//    	Coordinate[] coords  = new Coordinate[] {
//				new Coordinate(x1, y1), 
//				new Coordinate(x2, y1), 
//				new Coordinate(x2, y2),  
//				new Coordinate(x1, y2),  
//				new Coordinate(x1, y1) 
//		};
// 		Geometry polygon = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(coords), null);    	
//    	
// 		//DRAW GHOST LINE		
//		SimpleFeature aux = selectBox;
//		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "SELECTBOX", false));
//		
//		createSelectBox(polygon); 
// 		dispatch(transmitter,  new FeatureCreatedEvent(this, selectBox , "SELECTBOX", false));
// 		
//		intersectGeom(polygon);			
//		dispatchLayersFeedback();
//		dispatchLayersEdition();
	}
	
	private void handle(KeyPressedEvent e) throws Exception{
		if(e.getKeyEvent().getKeyCode() == KeyEvent.VK_DELETE){
			deletefeatures();
		}
	}

	private void createSelectBox(Geometry polygon) throws Exception{
		PolygonCreatorService selectBoxS = new PolygonCreatorService();
		try {
			selectBoxS.setType(DataUtilities.createType("Polygon","geom:Polygon"));
		} catch (SchemaException e1) {	e1.printStackTrace();	}
		
		List<Object> listType = new ArrayList<Object>();
		listType.add(polygon);
		
		selectBox =  selectBoxS.createPolygon(listType, null);
		
	}
	
	private void intersectGeom(Geometry polygon) throws Exception{
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getPolygon = new GetFeatureEvent(this, "POLYGON");
		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		dispatch(transmitter, getPolygon);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
		SimpleFeatureCollection polygonCollection = getPolygon.getFeatureCollection();
		
		SimpleFeatureIterator iterator_ = pointCollection.features();
//	    try {
//	        while( iterator_.hasNext() ){
//	            SimpleFeature feature = iterator_.next();
//	            System.out.println(feature.getAttributes());
//	        }
//	    }
//	    finally {
//	        iterator_.close();
//	    }
		
		if(firstTime){			
			pointFound = true;
			lineFound = false;
			polyFound = false;
			firstTime = false;
		}
		
		if(pointFound && !lineFound && !polyFound){
			if(!new IntersectGeometry().checkIntersection(polygon, pointCollection)){
				lineFound = true;
				pointFound = false;
				polyFound = false;
			}
		}
		if(!pointFound && lineFound && !polyFound){
			if(firstTimeIntersecting){
				firstLineNW = new IntersectGeometry().checkFirstLine(polygon, lineCollection, pointCollection);
			}			
			if(!new IntersectGeometry().checkIntersection(polygon, lineCollection, firstLineNW)){
				lineFound = false;
				pointFound = false;
				polyFound = true;
			}else{
				firstTimeIntersecting = false;
			}
		}
		if(!pointFound && !lineFound && polyFound){
			if(!new IntersectGeometry().checkIntersection(polygon, polygonCollection)){
				lineFound = false;
				pointFound = true;
				polyFound = false;
			}
		}			
	}
	 
	private void deletefeatures() throws Exception{
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getPolygon = new GetFeatureEvent(this, "POLYGON");
		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		dispatch(transmitter, getPolygon);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
		SimpleFeatureCollection polygonCollection = getPolygon.getFeatureCollection();
		
		listToDelete = new ArrayList<SimpleFeature>();
		listToDelete2 = new ArrayList<SimpleFeature>();
		
		SimpleFeatureIterator iterator = null; 		
		//DELETE POINT
		iterator = pointCollection.features();
		while( iterator.hasNext() )
		{				
			SimpleFeature feature = iterator.next();
			if(feature.getAttribute("selected").equals(true)){
				listToDelete.add(feature);
			}
		}  
		
		if(listToDelete.size()>0){
			int option = JOptionPane.showConfirmDialog(null, GVSingleton.getInstance().getLanguage().getString("confDelete.text"));
			if(option == 0){
				for (SimpleFeature feature : listToDelete){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "POINT", true));
				}				
			}
		}				
		listToDelete.clear();	
		
    	//DELETE LINE
		iterator = lineCollection.features();
		while( iterator.hasNext() )
		{				
			SimpleFeature feature = iterator.next();
			if(feature.getAttribute("selected").equals(true)){
				listToDelete.add(feature);
			}
		}
		for(SimpleFeature lineFeature : listToDelete){			
			SimpleFeatureIterator iteratorPoint = pointCollection.features();			
			while(iteratorPoint.hasNext()){		
				SimpleFeature featurePoint = iteratorPoint.next();
				if(((featurePoint.getID()).equals(lineFeature.getAttribute("point1"))) || (featurePoint.getID()).equals(lineFeature.getAttribute("point2"))){				
					listToDelete2.add(featurePoint);									
				} 
			}
			
		}	
		    	
		if(listToDelete.size()>0){			
			int option = JOptionPane.showConfirmDialog(null, GVSingleton.getInstance().getLanguage().getString("confDelete.text"));
			if(option == 0){
				for (SimpleFeature feature : listToDelete){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "LINE", true));
				}				
				for (SimpleFeature pointFeat : listToDelete2){
					
					GetFeatureEvent getLine2 = new GetFeatureEvent(this, "LINE");
					dispatch(transmitter, getLine2);
					SimpleFeatureCollection lineCollection2 = getLine2.getFeatureCollection();
					
					boolean removePoint = true;
					SimpleFeatureIterator iteratorLine = lineCollection2.features();			
					while(iteratorLine.hasNext()){		
						SimpleFeature featureLine = iteratorLine.next();
						if(((Geometry)pointFeat.getDefaultGeometry()).intersects((Geometry)featureLine.getDefaultGeometry())){
							removePoint = false;
						}						
					}
					if(removePoint){
						dispatch(transmitter, new FeatureRemovedEvent(this, pointFeat, "POINT", true));	
					}
				}	
			}
		}		
		listToDelete.clear();
		listToDelete2.clear();
		
    	//DELETE POLYGON
		iterator = polygonCollection.features();
		while( iterator.hasNext() )
		{				
			SimpleFeature feature = iterator.next();
			if(feature.getAttribute("selected").equals(true)){
				listToDelete.add(feature);					
			}
		}
    	
		if(listToDelete.size()>0){
			int option = JOptionPane.showConfirmDialog(null, GVSingleton.getInstance().getLanguage().getString("confDelete.text"));
			if(option == 0){
				for (SimpleFeature feature : listToDelete){
					dispatch(transmitter, new FeatureRemovedEvent(this, feature, "POLYGON", true));
				}
			}
		}
		
    	dispatchLayersEdition();
	}
}
