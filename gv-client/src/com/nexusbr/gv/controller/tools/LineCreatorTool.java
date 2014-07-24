package com.nexusbr.gv.controller.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.jtdk.edition.event.SetStyleEvent;

import com.nexusbr.gv.controller.Manager;

import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.EditionState;
import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.LineCreatorService;
import com.nexusbr.gv.services.PointCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This class recive the event Eagle, create the Line them send a event to redraw
 *
 * @author Bruno Severino
 * @version 1.0
 */
public class LineCreatorTool extends Manager {
	
	private double x1=0;
	private double y1=0;
	private double ySnap;
	private double xSnap;
	private boolean firstPoint = false;
	private boolean snapLineFound = false;
	private Geometry geomIntersected = null;
	private SimpleFeature ghostLine;
	private SimpleFeature snapPoint;
	SimpleFeatureCollection collection = FeatureCollections.newCollection();
			
	public LineCreatorTool(){				
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(MouseMovedEvent.class.getName());
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());	
		eventsToListen.add(AfterToolChangedEvent.class.getName());
		eventsToListen.add(BeforeToolChangedEvent.class.getName());

		//CHANGE CURSOR TO PRESSED
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");

		GVSingleton gvSingleton = GVSingleton.getInstance();
		toolbar = gvSingleton.getToolbar();
		
		firstPoint = true;
		
	}

	public void handle(EventObject e) throws Exception {
		// TODO Auto-generated method stub
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

		else if(e instanceof MouseMovedEvent){
			handle((MouseMovedEvent)e);
		}	
	}

	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE POINT STYLE				
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle(), "POINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
		
		// LOCATE OBJECTS IN SCREEN		
		//ShowPointFeatures( locatePoints() );
		//ShowLineFeatures( locateLines() );
		//ShowPolygonFeatures( locatePolygons() );
	}

	private void handle(BeforeToolChangedEvent e) throws Exception{
		dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MousePressedEvent e) throws Exception {					
		// CHANGE CURSOR 
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");
		dispatch(transmitter, new UpdateCursorEvent(this));

		if(e.getButton() == MouseEvent.BUTTON3){
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
			firstPoint = true;
			dispatchLayersFeedback();
		}
		else if(e.getButton() == MouseEvent.BUTTON1){
			// CREATE LINE
			double posX;
			double posY;
			if(snapLineFound){
				posX = xSnap;
				posY = ySnap;
			}
			else{
				posX = e.getX();
				posY = e.getY();
			}

			if(firstPoint){	
				firstPoint = false;	
				ghostLine = (new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(posX,posY), new Coordinate(posX,posY)}));
				dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));	 		
				x1 = posX;
				y1 = posY;
			}
			else{
				//REMOVE GHOST LINE
				dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));	
				
				// ADD GEOMETRIES IN EDITION CONTROLLER
				SimpleFeature lastLine = new LineCreatorService().createLine(((Geometry)ghostLine.getDefaultGeometry()).getCoordinates(), ghostLine);
				lastLine.setAttribute("layerName", AppSingleton.getInstance().getMediator().getCurrentTheme().getLayer().getName());
				dispatch(transmitter, new FeatureCreatedEvent(this, lastLine , "LINE", true));			
				firstPoint = true;
				
				//POPULATE PROPERTIES FRAME
				toolbar.setFeature(lastLine);
				EditionState.setEdited(true);
				
				/*Table table = new TablesBO().getTableFromLayerID(AppSingleton.getInstance().getMediator().getCurrentTheme().getLayer().getId());
				if(table!=null){
					toolbar.getFramePropertie().popCmbLayer(2L);
					toolbar.getFramePropertie().setLayer(table.getLayerName());
					toolbar.getFramePropertie().cleanTable();
				}*/
			}
		}
		dispatchLayersFeedback();
		dispatchLayersEdition();
		
	}

	private void handle(MouseReleasedEvent e) throws Exception {
		// TODO Auto-generated method stub
		if(!firstPoint){
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
					getClass().getResource("/com/nexusbr/gv/images/curReleased.png")),new Point(16,16),"draw");
		}else{
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
					getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");
		}
		dispatch(transmitter, new UpdateCursorEvent(this));
	}

	private void handle(MouseMovedEvent e) throws Exception {
		// TODO Auto-generated method stub		
		double x = e.getX();
		double y = e.getY();
		
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
		
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		dispatch(transmitter, getLine);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();

		IntersectGeometry intGeom = new IntersectGeometry();
		snapLineFound = intGeom.checkForSnap(e.getX(), e.getY(), lineSnapTolerance, lineCollection, this, transmitter);
		if(snapLineFound){			
			geomIntersected = intGeom.getGeomIntersecting();
			
			//DRAW SNAP POINT		
			SimpleFeature aux2 = snapPoint;
			dispatch(transmitter,  new FeatureRemovedEvent(this, aux2, "SNAPPOINT", false));			
			x = ((Geometry)geomIntersected).getCentroid().getX(); 
			y = ((Geometry)geomIntersected).getCentroid().getY();
			xSnap = x;
			ySnap = y;
			snapPoint = new PointCreatorService().createPoint(x, y);
			dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));	 		
		}else{
			dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
		}
		
		dispatchLayersFeedback();

		if(firstPoint)
			return;

		//DRAW GHOST LINE		
		SimpleFeature aux = ghostLine;	
		ghostLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(x1,y1), new Coordinate(x, y)});
		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));
		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));

		dispatchLayersFeedback();
		
	}	
}
