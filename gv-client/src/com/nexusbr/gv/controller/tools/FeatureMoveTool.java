package com.nexusbr.gv.controller.tools;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.KeyPressedEvent;
import br.org.funcate.glue.event.MouseDraggedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.FeatureEditedEvent;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;		
import br.org.funcate.jtdk.edition.event.SetStyleEvent;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.LineCreatorService;
import com.nexusbr.gv.services.MoveGeometry;
import com.nexusbr.gv.services.PointCreatorService;
import com.nexusbr.gv.services.PolygonCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class FeatureMoveTool extends Manager {

	private double clickPointX=0;
	private double clickPointY=0;
	private boolean  intersectedLineGeom=false;
	private boolean intersectedPointGeom=false;
	private boolean intersectedPolygonGeom=false;
	private Geometry geomIntersected;
	private SimpleFeature featIntersected;
	private SimpleFeature beforeFeaturePolygon;
	private SimpleFeature afterFeaturePolygon;
	private SimpleFeature ghostPoint;
	private SimpleFeature ghostLine;
	private SimpleFeature snapPoint;
	private ArrayList<SimpleFeature> beforeFeaturePoint;
	private ArrayList<SimpleFeature> afterFeaturePoint;	
	private ArrayList<SimpleFeature> beforeFeatureLine;
	private ArrayList<SimpleFeature> afterFeatureLine;
	private ArrayList<SimpleFeature> ghostLines;
	private ArrayList<SimpleFeature> ghostPoints;
	
	public FeatureMoveTool(){
		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);
		
		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(MouseMovedEvent.class.getName());
		eventsToListen.add(MouseDraggedEvent.class.getName());
		eventsToListen.add(KeyPressedEvent.class.getName());
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());	
		eventsToListen.add(AfterToolChangedEvent.class.getName());		
		
		//CHANGE CURSOR TO MOUSE RELASED
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/moveReleased.png")),new java.awt.Point(16,16),"draw");

		ghostLines = new ArrayList<SimpleFeature>();
		ghostPoints = new ArrayList<SimpleFeature>();
		
		afterFeaturePoint = new ArrayList<SimpleFeature>();
		beforeFeaturePoint = new ArrayList<SimpleFeature>();
		afterFeatureLine = new ArrayList<SimpleFeature>();
		beforeFeatureLine = new ArrayList<SimpleFeature>();
	}
	
	@Override
	public void handle(EventObject e) throws Exception {
		// TODO Auto-generated method stub
		if(e instanceof MousePressedEvent){
			handle((MousePressedEvent)e);
		}
		else if(e instanceof MouseReleasedEvent){
			handle((MouseReleasedEvent)e);
		}
		else if(e instanceof MouseMovedEvent){
			handle((MouseMovedEvent)e);
		}
		else if(e instanceof MouseDraggedEvent){
			handle((MouseDraggedEvent)e);
		}
		else if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}
	}
	
	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE POINT STYLE		
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle(), "POINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
		
		// LOCATE OBJECTS IN SCREEN		
		ShowPointFeatures( locatePoints() );
		ShowLineFeatures( locateLines() );
		ShowPolygonFeatures( locatePolygons() );
	}
	
	private void handle(MouseDraggedEvent e) throws Exception{	
		
		if(intersectedPolygonGeom){
			//REMOVE PREVIOUS GHOST SELECT POLYGON
			SimpleFeature aux = ghostLine;
			dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));				
							
			//DRAW GHOST SELECT POLYGON
			double xdist = (e.getX()-clickPointX);
			double ydist = (e.getY()-clickPointY);
			Geometry geomFeat = (Geometry) ((Geometry) beforeFeaturePolygon.getDefaultGeometry()).clone();
			Coordinate[] coord = geomFeat.getCoordinates();
			for(int i=0;i<coord.length;i++){
				coord[i].x += xdist;
				coord[i].y += ydist;
			}
			
    		ghostLine = new LineCreatorService().createLine(coord, featIntersected);			
	 		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));

		}
		else if(intersectedPointGeom)
    	{
			//VERIFY IF IT'S NETWORK POINT OR NOT, AND REDIRECT IT
			boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");
			if(networkMode){
				//REMOVE
				for(int i=0;i<ghostLines.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLines.get(i), "GHOSTLINE", false));				
				ghostLines.clear();		
				
				for(int i=0; i<beforeFeatureLine.size();i++){					
					if(featIntersected.getID().equals(beforeFeatureLine.get(i).getAttribute("point1"))){
						Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(i).getDefaultGeometry()).getCoordinates();
						double lastX  = coordLine[coordLine.length-1].x;
						double lastY  = coordLine[coordLine.length-1].y;
						Coordinate[] coord = new Coordinate[]{new Coordinate(e.getX(), e.getY()), new Coordinate(lastX, lastY)};
						ghostLines.add(new MoveGeometry().moveLine(coord, beforeFeatureLine.get(i)));
					}
					else if(featIntersected.getID().equals(beforeFeatureLine.get(i).getAttribute("point2"))){							
						Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(i).getDefaultGeometry()).getCoordinates();
						double firstX  = coordLine[0].x;
						double firstY  = coordLine[0].y;
						Coordinate[] coord = new Coordinate[]{new Coordinate(firstX, firstY), new Coordinate(e.getX(), e.getY())};
						ghostLines.add(new MoveGeometry().moveLine(coord, beforeFeatureLine.get(i)));
					}
				}
				
				//REMOVE PREVIOUS GHOST SELECT POINT
				SimpleFeature aux = ghostPoint;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTPOINT", false));				
								
				//DRAW GHOST SELECT POINT	
				ghostPoint = new PointCreatorService().createPoint(e.getX(), e.getY(), aux);		
				ghostPoint.setAttribute("selected", true);
				dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));
								
				//DRAW
				for(int i=0;i<ghostLines.size();i++)
					dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLines.get(i) , "GHOSTLINE", false));						
			}else{				
				//REMOVE PREVIOUS GHOST SELECT POINT
				SimpleFeature aux = ghostPoint;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTPOINT", false));				
								
				//DRAW GHOST SELECT POINT	
				ghostPoint = new PointCreatorService().createPoint(e.getX(), e.getY(), aux);		
				ghostPoint.setAttribute("selected", true);
				dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));
			}
			
    	}
		else if(intersectedLineGeom){						
			//VERIFY IF IT'S NETWORK LINE OR NOT, AND REDIRECT IT
			boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");
			if(networkMode){	
				//REMOVE
				ArrayList<SimpleFeature> auxP = ghostPoints;
				ArrayList<SimpleFeature> auxL = ghostLines;
				for(int i=0;i<auxP.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, auxP.get(i), "GHOSTPOINT", false));
				for(int i=0;i<auxL.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, auxL.get(i), "GHOSTLINE", false));	
				
				ghostLines.clear();
				ghostPoints.clear();
				
				//DRAW GHOST LINE E POINT 
				double xdist = (e.getX() - clickPointX);
				double ydist = (e.getY() - clickPointY);
				if(((Geometry)featIntersected.getDefaultGeometry()).getCoordinates().length>2){			
					Geometry geomFeat = (Geometry) ((Geometry) featIntersected.getDefaultGeometry()).clone();
					Coordinate[] newcoord = geomFeat.getCoordinates();
					for(int i=0;i<newcoord.length;i++){
						newcoord[i].x += xdist;
						newcoord[i].y += ydist;
					}
					ghostLines.add(new MoveGeometry().moveLine(newcoord, featIntersected));
				}else{
					double x1 = ((double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].x) + xdist;
					double y1 = ((double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].y) + ydist;
					double x2 = ((double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].x) + xdist;
					double y2 = ((double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].y) + ydist;
					Coordinate[] coord = new Coordinate[]{new Coordinate(x1, y1), new Coordinate(x2, y2)};
					ghostLines.add(new MoveGeometry().moveLine(coord, featIntersected));
				}
				
				for(int i=0; i<beforeFeaturePoint.size();i++){					
					if(featIntersected.getAttribute("point1").equals(beforeFeaturePoint.get(i).getID())){
						//CREATE THE NEW GHOST POINT 1
						double x  = ((double) ((Geometry) beforeFeaturePoint.get(i).getDefaultGeometry()).getCoordinate().x) + xdist;
						double y  = ((double) ((Geometry) beforeFeaturePoint.get(i).getDefaultGeometry()).getCoordinate().y) + ydist;	
						Coordinate coord = new Coordinate(x, y);
						ghostPoints.add(new PointCreatorService().createPoint(coord, beforeFeaturePoint.get(i)));
						ghostPoints.get(ghostPoints.size()-1).setAttribute("selected", true);							
						
						//CREATE THE NEW GHOST LINE FOR POINT 1
						for(int j=0; j<beforeFeatureLine.size();j++){					
							if(beforeFeaturePoint.get(i).getID().equals(beforeFeatureLine.get(j).getAttribute("point1"))){
								Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(j).getDefaultGeometry()).getCoordinates();
								double lastX  = coordLine[coordLine.length-1].x;
								double lastY  = coordLine[coordLine.length-1].y;
								Coordinate[] newcoord = new Coordinate[]{new Coordinate(x, y), new Coordinate(lastX, lastY)};
								ghostLines.add(new MoveGeometry().moveLine(newcoord, beforeFeatureLine.get(j)));
							}
							else if(beforeFeaturePoint.get(i).getID().equals(beforeFeatureLine.get(j).getAttribute("point2"))){	
								Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(j).getDefaultGeometry()).getCoordinates();
								double firstX  = coordLine[0].x;
								double firstY  = coordLine[0].y;
								Coordinate[] newcoord = new Coordinate[]{new Coordinate(firstX, firstY), new Coordinate(x, y)};
								ghostLines.add(new MoveGeometry().moveLine(newcoord, beforeFeatureLine.get(j)));
							}
						}
					}					
					else if(featIntersected.getAttribute("point2").equals(beforeFeaturePoint.get(i).getID())){
						//CREATE THE NEW GHOST POINT 2
						double x  = ((double) ((Geometry) beforeFeaturePoint.get(i).getDefaultGeometry()).getCoordinate().x) + xdist;
						double y  = ((double) ((Geometry) beforeFeaturePoint.get(i).getDefaultGeometry()).getCoordinate().y) + ydist;	
						Coordinate coord = new Coordinate(x, y);
						ghostPoints.add(new PointCreatorService().createPoint(coord, beforeFeaturePoint.get(i)));
						ghostPoints.get(ghostPoints.size()-1).setAttribute("selected", true);							
						
						//CREATE THE NEW GHOST LINE FOR POINT 2
						for(int j=0; j<beforeFeatureLine.size();j++){					
							if(beforeFeaturePoint.get(i).getID().equals(beforeFeatureLine.get(j).getAttribute("point1"))){
								Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(j).getDefaultGeometry()).getCoordinates();
								double lastX  = coordLine[coordLine.length-1].x;
								double lastY  = coordLine[coordLine.length-1].y;
								Coordinate[] newcoord = new Coordinate[]{new Coordinate(x, y), new Coordinate(lastX, lastY)};
								ghostLines.add(new MoveGeometry().moveLine(newcoord, beforeFeatureLine.get(j)));
							}
							else if(beforeFeaturePoint.get(i).getID().equals(beforeFeatureLine.get(j).getAttribute("point2"))){	
								Coordinate[] coordLine = ((Geometry)beforeFeatureLine.get(j).getDefaultGeometry()).getCoordinates();
								double firstX  = coordLine[0].x;
								double firstY  = coordLine[0].y;
								Coordinate[] newcoord = new Coordinate[]{new Coordinate(firstX, firstY), new Coordinate(x, y)};
								ghostLines.add(new MoveGeometry().moveLine(newcoord, beforeFeatureLine.get(j)));
							}
						}
					}
				}
				//DRAW
				for(int i=0;i<ghostLines.size();i++)
					dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLines.get(i) , "GHOSTLINE", false));
				
				for(int i=0;i<ghostPoints.size();i++)
					dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoints.get(i) , "GHOSTPOINT", false));	
			}else{		
				//REMOVE PREVIOUS GHOST SELECT POINT
				SimpleFeature aux = ghostLine;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));				
								
				//DRAW GHOST SELECT POINT	
				double x1 = ((double) ((Geometry)beforeFeatureLine.get(0).getAttribute(0)).getCoordinates()[0].x) + (e.getX() - clickPointX);
				double y1 = ((double) ((Geometry)beforeFeatureLine.get(0).getAttribute(0)).getCoordinates()[0].y) + (e.getY() - clickPointY);
				double x2 = ((double) ((Geometry)beforeFeatureLine.get(0).getAttribute(0)).getCoordinates()[1].x) + (e.getX() - clickPointX);
				double y2 = ((double) ((Geometry)beforeFeatureLine.get(0).getAttribute(0)).getCoordinates()[1].y) + (e.getY() - clickPointY);
				
				ghostLine = new LineCreatorService().createLine(new Coordinate[]{new Coordinate(x1,y1), new Coordinate(x2,y2)}, featIntersected);	
				ghostLine.setAttribute("selected", true);
				dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));
			}		
		}
		
		//PLOT LAYER
		dispatchLayersFeedback();
	}
	
	private void handle(MouseMovedEvent e) throws Exception {
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getPolygon = new GetFeatureEvent(this, "POLYGON");
		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		dispatch(transmitter, getPolygon);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
		SimpleFeatureCollection polygonCollection = getPolygon.getFeatureCollection();
		
		IntersectGeometry inters = new IntersectGeometry();
		SimpleFeature aux = snapPoint;	
		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "SNAPPOINT", false));
		
		intersectedPolygonGeom = inters.checkForSnap(e.getX(), e.getY(), polygonSnapTolerance, polygonCollection, this, transmitter, true);
		if(!intersectedPolygonGeom){
			intersectedPointGeom = inters.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, pointCollection, this, transmitter);			
			if(!intersectedPointGeom){
				intersectedLineGeom = inters.checkForSnap(e.getX(), e.getY(), lineSnapTolerance, lineCollection, this, transmitter);
				if(intersectedLineGeom){
					geomIntersected = inters.getGeomIntersecting();
					//DRAW SNAP POINT		
					snapPoint = new PointCreatorService().createPoint(((Geometry)geomIntersected).getCentroid().getX(), ((Geometry)geomIntersected).getCentroid().getY());		
			 		dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));
				}
				new IntersectGeometry().setSelected(pointCollection, false);
			}else{
				geomIntersected = inters.getGeomIntersecting();			
			}
			new IntersectGeometry().setSelected(polygonCollection, false);
		}else{
			geomIntersected = inters.getGeomIntersecting();
		}
		//PLOT LAYER
		dispatchLayersFeedback();
    	dispatchLayersEdition();
    	
    	featIntersected = inters.getFeature();
	}
	
	private void handle(MouseReleasedEvent e) throws Exception {
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
    	    	
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/moveReleased.png")),new java.awt.Point(16,16),"draw");
		dispatch(transmitter, new UpdateCursorEvent(this));
		
		if(intersectedPolygonGeom){
	 		SimpleFeature aux = ghostLine;
			dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));	
								
			LinearRing ring = new GeometryFactory().createLinearRing(((Geometry) ghostLine.getDefaultGeometry()).getCoordinates());			
			
			SimpleFeature polygon = new PolygonCreatorService().createPolygon(ring, beforeFeaturePolygon);			
			afterFeaturePolygon = polygon;
			dispatch(transmitter, new FeatureEditedEvent(this, beforeFeaturePolygon, afterFeaturePolygon, "POLYGON", true));
			GVSingleton.getInstance().getPolygonEdited().add(beforeFeaturePolygon);
    	}
		else if(intersectedPointGeom){
			boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");
			if(networkMode){
				//REMOVE				
				ArrayList<SimpleFeature> auxL = ghostLines;				
				for(int i=0;i<auxL.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, auxL.get(i), "GHOSTLINE", false));
					
				for(int i=0;i<ghostLines.size();i++){
					for(int j=0;j<beforeFeatureLine.size();j++){
						if(ghostLines.get(i).getID().equals(beforeFeatureLine.get(j).getID())){
							afterFeatureLine.add(new LineCreatorService().createLine((Geometry)ghostLines.get(i).getDefaultGeometry(), beforeFeatureLine.get(j)));							
							dispatch(transmitter, new FeatureEditedEvent(this, beforeFeatureLine.get(j), afterFeatureLine.get(afterFeatureLine.size()-1), "LINE", true));
							GVSingleton.getInstance().getLinesEdited().add(afterFeatureLine.get(j));
							continue;
						}
					}
					
				}		
				
				SimpleFeature aux = ghostPoint;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTPOINT", false));
				afterFeaturePoint.add(new PointCreatorService().createPoint(ghostPoint));
				afterFeaturePoint.get(0).setAttribute("networkMode", true);
				afterFeaturePoint.get(0).setAttribute("selected", false);
				dispatch(transmitter, new FeatureEditedEvent(this, beforeFeaturePoint.get(0), afterFeaturePoint.get(0), "POINT", true));
				GVSingleton.getInstance().getPointsEdited().add(afterFeaturePoint.get(0));			
			}else{
				SimpleFeature aux = ghostPoint;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTPOINT", false));
				afterFeaturePoint.add(new PointCreatorService().createPoint(ghostPoint));
				afterFeaturePoint.get(0).setAttribute("networkMode", false);
				afterFeaturePoint.get(0).setAttribute("selected", false);
				dispatch(transmitter, new FeatureEditedEvent(this, beforeFeaturePoint.get(0), afterFeaturePoint.get(0), "POINT", true));
				GVSingleton.getInstance().getPointsEdited().add(afterFeaturePoint.get(0));
			}
    	}
	    else if(intersectedLineGeom){
	    	boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");
			if(networkMode){
				//REMOVE
				ArrayList<SimpleFeature> auxP = ghostPoints;
				ArrayList<SimpleFeature> auxL = ghostLines;
				for(int i=0;i<auxP.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, auxP.get(i), "GHOSTPOINT", false));
				for(int i=0;i<auxL.size();i++)
					dispatch(transmitter,  new FeatureRemovedEvent(this, auxL.get(i), "GHOSTLINE", false));
					
				for(int j=0;j<beforeFeatureLine.size();j++){
					for(int i=0;i<ghostLines.size();i++){					
						if(ghostLines.get(i).getID().equals(beforeFeatureLine.get(j).getID())){
							afterFeatureLine.add(new LineCreatorService().createLine((Geometry)ghostLines.get(i).getAttribute(0), beforeFeatureLine.get(j)));							
							dispatch(transmitter, new FeatureEditedEvent(this, beforeFeatureLine.get(j), afterFeatureLine.get(afterFeatureLine.size()-1), "LINE", true));
							GVSingleton.getInstance().getLinesEdited().add(afterFeatureLine.get(j));
							break;
						}
					}					
				}				
				
				for(int i=0;i<ghostPoints.size();i++){
					for(int j=0;j<beforeFeaturePoint.size();j++){
						if(ghostPoints.get(i).getID().equals(beforeFeaturePoint.get(j).getID())){
							afterFeaturePoint.add(new PointCreatorService().createPoint((Geometry)ghostPoints.get(i).getDefaultGeometry(), beforeFeaturePoint.get(j)));
							afterFeaturePoint.get(afterFeaturePoint.size()-1).setAttribute("networkMode", true);							
							dispatch(transmitter, new FeatureEditedEvent(this, beforeFeaturePoint.get(j), afterFeaturePoint.get(afterFeaturePoint.size()-1), "POINT", true));
							GVSingleton.getInstance().getPointsEdited().add(afterFeaturePoint.get(j));
							continue;
						}
					}
				}
			}else{
				SimpleFeature aux = ghostLine;
				dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));
				afterFeatureLine.add(new LineCreatorService().createLine((Geometry)ghostLine.getAttribute(0), beforeFeatureLine.get(0)));
				afterFeatureLine.get(0).setAttribute("networkMode", false);			
				dispatch(transmitter, new FeatureEditedEvent(this, beforeFeatureLine.get(0), afterFeatureLine.get(0), "LINE", true));
				GVSingleton.getInstance().getLinesEdited().add(afterFeatureLine.get(0));
			}
	    }
	    ghostLines.clear();
		ghostPoints.clear();
		beforeFeaturePoint.clear();
		afterFeaturePoint.clear();
		beforeFeatureLine.clear();
		afterFeatureLine.clear();
	    	
    	//PLOT LAYER
		dispatchLayersFeedback();
    	dispatchLayersEdition();
		
		intersectedLineGeom = false;
    	intersectedPointGeom = false;
	}
	
	private void handle(MousePressedEvent e) throws Exception {
		ghostLines.clear();
		ghostPoints.clear();
		beforeFeaturePoint.clear();
		afterFeaturePoint.clear();
		beforeFeatureLine.clear();
		afterFeatureLine.clear();
		
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/movePressed.png")),new java.awt.Point(16,16),"draw");
		dispatch(transmitter, new UpdateCursorEvent(this));
		
		if(e.getButton()==MouseEvent.BUTTON2){
			// CHANGE TOOL TO NETWORKCREATORTOOL
			AppSingleton.getInstance().getMediator().setCurrentTool(new NetworkCreatorTool());
		}
		else if(e.getButton()==MouseEvent.BUTTON1){
			clickPointX = e.getX();
	    	clickPointY = e.getY();
	    	dispatch(transmitter, new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
	    	
	    	if(intersectedPolygonGeom){
	    		beforeFeaturePolygon = featIntersected;
	    		beforeFeaturePolygon.setAttribute("selected", false);
	    		dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeaturePolygon, "POLYGON", false));
	    		Polygon poly = (Polygon) featIntersected.getDefaultGeometry();
	    		Coordinate[] coords = poly.getExteriorRing().getCoordinates();
	    		
	    		ghostLine = new LineCreatorService().createLine(coords, featIntersected);
	    		ghostLine.setAttribute("selected", true);
		 		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));
		 		
		 		//Populate Properties Frame
		 		GVSingleton.getInstance().getToolbar().setFeature(featIntersected);		 		
	    	}
	    	else if(intersectedPointGeom){	    		
	    		boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");	    		
				if(networkMode){				
					
					//CATCH THE LINE COLLECTION
					GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
					dispatch(transmitter, getLine);
					SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
					
					//CREATE THE GHOST LINE
					SimpleFeatureIterator iterator = null;		
					try{
						iterator = lineCollection.features();			
						while(iterator.hasNext()){		
							SimpleFeature feature = iterator.next();
							if(((Geometry)feature.getAttribute(0)).intersects(((Geometry)featIntersected.getAttribute(0)))){
								beforeFeatureLine.add(feature);					
								Geometry geomTemp = (Geometry) feature.getAttribute(0);
								ghostLines.add(new LineCreatorService().createLine(geomTemp.getCoordinates(), beforeFeatureLine.get(beforeFeatureLine.size()-1)));
								ghostLines.get(ghostLines.size()-1).setAttribute("selected", true);					
							} 
						}
					}
					catch(Exception ex){}				
					for(int i=0;i<ghostLines.size();i++)
						dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLines.get(i) , "GHOSTLINE", false));					
					
					//CREATE THE GHOST POINT
					beforeFeaturePoint.add(featIntersected);
					double poX = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinate().x;
					double poY = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinate().y;
					ghostPoint = new PointCreatorService().createPoint(new Coordinate(poX, poY), featIntersected);
					ghostPoint.setAttribute("selected", true);		
					dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));		
					//REMOVE THE POINT
					for(int i=0;i<beforeFeaturePoint.size();i++){
						beforeFeaturePoint.get(i).setAttribute("selected", false);
						dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeaturePoint.get(i), "POINT", false));
					}
					//REMOVE THE LINE
					for(int i=0;i<beforeFeatureLine.size();i++){
						beforeFeatureLine.get(i).setAttribute("selected", false);
						dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeatureLine.get(i), "LINE", false));
					}
				}else{					
					beforeFeaturePoint.add(featIntersected);
					beforeFeaturePoint.get(0).setAttribute("selected", false);
		    		dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeaturePoint.get(0), "POINT", false));	   
		    		Geometry geomTemp = (Geometry) featIntersected.getDefaultGeometry();
		    		ghostPoint = new PointCreatorService().createPoint(geomTemp.getCoordinate().x, geomTemp.getCoordinate().y, featIntersected);		
		    		ghostPoint.setAttribute("selected", true);
		    		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));
				}
				
				//Populate Properties Frame
		 		GVSingleton.getInstance().getToolbar().setFeature(featIntersected);
	    	}
	    	else if(intersectedLineGeom){
	    		boolean networkMode = (Boolean) featIntersected.getAttribute("networkMode");	    		
				if(networkMode){
					GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
					GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
					dispatch(transmitter, getPoint);
					dispatch(transmitter, getLine);
					SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
					SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
					
					beforeFeatureLine.add(featIntersected);
					ghostLines.add(new LineCreatorService().createLine(((Geometry)featIntersected.getDefaultGeometry()).getCoordinates(), featIntersected));
					ghostLines.get(ghostLines.size()-1).setAttribute("selected", true);
					
					SimpleFeatureIterator iteratorPoint = null;
					try {
						iteratorPoint = pointCollection.features();			
						while(iteratorPoint.hasNext()){		
							SimpleFeature featurePoint = iteratorPoint.next();
							if(((featurePoint.getID()).equals(featIntersected.getAttribute("point1"))) || (featurePoint.getID()).equals(featIntersected.getAttribute("point2"))){
							
								beforeFeaturePoint.add(featurePoint);								
								Geometry geomTemp = (Geometry) featurePoint.getAttribute(0);
								ghostPoints.add(new PointCreatorService().createPoint(geomTemp.getCoordinate(), featurePoint));
								ghostPoints.get(ghostPoints.size()-1).setAttribute("selected", true);
								
								SimpleFeatureIterator iteratorLine = lineCollection.features();			
								while(iteratorLine.hasNext()){		
									SimpleFeature featureLine = iteratorLine.next();
									String point1 = (String) featureLine.getAttribute("point1");
									String point2 = (String) featureLine.getAttribute("point2");
									String pointID = (String) featurePoint.getID();
									if(!geomIntersected.equals(featureLine)){
										if(point1.equals(pointID) || point2.equals(pointID)){											
											beforeFeatureLine.add(featureLine);
											Geometry geomTemp2 = (Geometry) featureLine.getAttribute(0);
											ghostLines.add(new LineCreatorService().createLine(geomTemp2.getCoordinates(), featureLine));
											ghostLines.get(ghostLines.size()-1).setAttribute("selected", true);
										}
									}
								}
							} 
						}
					}
					catch(Exception ex){}
					
					for(int i=0;i<beforeFeatureLine.size();i++){
						beforeFeatureLine.get(i).setAttribute("selected", false);
						dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeatureLine.get(i), "LINE", false));
					}
					for(int i=0;i<beforeFeaturePoint.size();i++){
						beforeFeaturePoint.get(i).setAttribute("selected", false);
						dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeaturePoint.get(i), "POINT", false));
					}
					for(int i=0;i<ghostLines.size();i++)
						dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLines.get(i) , "GHOSTLINE", false));
					
					for(int i=0;i<ghostPoints.size();i++)
						dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoints.get(i) , "GHOSTPOINT", false));
				
					//Populate Properties Frame
			 		GVSingleton.getInstance().getToolbar().setFeaturePoint1(beforeFeaturePoint.get(0));
			 		GVSingleton.getInstance().getToolbar().setFeaturePoint2(beforeFeaturePoint.get(1));
			 		
				}else{					
					beforeFeatureLine.add(featIntersected);
					beforeFeatureLine.get(0).setAttribute("selected", false);
		    		dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeatureLine.get(0), "LINE", false));	   
		    		Geometry geomTemp = (Geometry) featIntersected.getDefaultGeometry();
		    		ghostLine = new LineCreatorService().createLine(new Coordinate[]{
	    				new Coordinate(geomTemp.getCoordinates()[0].x, geomTemp.getCoordinates()[0].y),
	    				new Coordinate(geomTemp.getCoordinates()[1].x, geomTemp.getCoordinates()[1].y)},
	    				featIntersected
		    		);
		    		ghostLine.setAttribute("selected", true);
			 		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));
				}
				
				//Populate Properties Frame
		 		GVSingleton.getInstance().getToolbar().setFeature(beforeFeatureLine.get(0));
	    	}
		}
		else if(e.getButton()==MouseEvent.BUTTON3){
			//DO NOTHING
			return;
		}
		
		//PLOT LAYER
		dispatchLayersFeedback();
    	dispatchLayersEdition();
	}
}

