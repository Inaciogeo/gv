package com.nexusbr.gv.controller.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.KeyPressedEvent;
import br.org.funcate.glue.event.MouseClickedEvent;
import br.org.funcate.glue.event.MouseDraggedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.FeatureEditedEvent;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.SetStyleEvent;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.EditPathService;
import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.LineCreatorService;
import com.nexusbr.gv.services.PolygonCreatorService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class EditPathTool extends Manager {
	private int positionResize;
	private int positionEdge;
	private boolean selected = false;
	private boolean edgeMoving =false;	
	private Geometry envelopeBox;
	private Geometry geomAtual;
	private GeometryCollection resizeCollection;	
	private GeometryCollection edgeCollection ;
	private SimpleFeature editpath_box;
	private SimpleFeature editpath_resize;
	private SimpleFeature editpath_edges;
	private SimpleFeature snapPoint;	
	private SimpleFeature ghostLine;
	private SimpleFeature featIntersected = null;
	private ArrayList<SimpleFeature> beforeFeatureLine;
	private ArrayList<SimpleFeature> afterFeatureLine;

	public EditPathTool(){		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());	
		eventsToListen.add(BeforeToolChangedEvent.class.getName());
		eventsToListen.add(MouseClickedEvent.class.getName());
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());
		eventsToListen.add(MouseMovedEvent.class.getName());		
		eventsToListen.add(MouseDraggedEvent.class.getName());				
		eventsToListen.add(KeyPressedEvent.class.getName());

		//CHANGE CURSOR TO PRESSED
		cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

		beforeFeatureLine = new ArrayList<SimpleFeature>();
		afterFeatureLine = new ArrayList<SimpleFeature>();
		positionEdge = -1;
		edgeMoving = false;		
	}

	@Override
	public void handle(EventObject e) throws Exception {
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}

		else if(e instanceof BeforeToolChangedEvent){
			handle((BeforeToolChangedEvent) e);
		}

		else if(e instanceof MouseClickedEvent){
			handle((MouseClickedEvent)e);
		}

		else if(e instanceof MousePressedEvent){
			handle((MousePressedEvent)e);
		}

		else if(e instanceof MouseReleasedEvent){
			handle((MouseReleasedEvent)e);
		}

		else if(e instanceof MouseMovedEvent){
			handle((MouseMovedEvent) e);
		}

		else if(e instanceof MouseDraggedEvent){
			handle((MouseDraggedEvent) e);
		}

		else if(e instanceof KeyPressedEvent){
			handle((KeyPressedEvent) e);
		}
	}

	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE POINT STYLE				
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createEditPathBox(), "EDITPATH_BOX"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createEditPathResize(), "EDITPATH_RESIZE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createEditPathEdges(), "EDITPATH_EDGES"));

	}

	private void handle(BeforeToolChangedEvent e) throws Exception{
		dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));		
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_box, "EDITPATH_BOX", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_edges, "EDITPATH_EDGES", false));

		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MouseClickedEvent e) throws Exception{
		if(featIntersected == null){
			return;
		}
		if(featIntersected.getDefaultGeometry() instanceof LineString)
		{
			if(e.getMouseEvent().isControlDown()){
				beforeFeatureLine.clear();
				beforeFeatureLine.add(featIntersected);
				SimpleFeature newFeature = new LineCreatorService().cloneSimpleFeature(featIntersected);
				Coordinate[] coords = ((Geometry)newFeature.getDefaultGeometry()).getCoordinates();
				geomAtual = new IntersectGeometry().getSnapBox(e.getX(), e.getY(), 10, this, transmitter);
				boolean removeEdge = false;				

				for(int i=0; i<((GeometryCollection)editpath_edges.getDefaultGeometry()).getNumGeometries(); i++){
					if(geomAtual.intersects(((GeometryCollection)editpath_edges.getDefaultGeometry()).getGeometryN(i))){
						removeEdge = true;
						break;
					}
				}

				if(removeEdge){

				}
				else{
					List<Coordinate> coordinates =new ArrayList<Coordinate>();				
					for(int i=0; i<coords.length; i++){					
						Coordinate[] coord = new Coordinate[]{new Coordinate(coords[i].x, coords[i].y), new Coordinate(coords[i+1].x, coords[i+1].y)};
						Geometry line = new GeometryFactory().createLineString(coord);
						if(geomAtual.intersects(line)){
							coordinates.add(coords[i]);
							coordinates.add(geomAtual.intersection(line).getCoordinate());
							for(int j=i+1; j<coords.length; j++){					
								coordinates.add(coords[j]);
							}
							break;
						}
						else{
							coordinates.add(coords[i]);	
						}
					}

					Coordinate[] newCoord = new Coordinate[coordinates.size()];
					for(int i=0; i<coordinates.size();i++){
						newCoord[i] = coordinates.get(i);
					}

					newFeature.setDefaultGeometry(new LineCreatorService().createLine(newCoord));
					afterFeatureLine.add(newFeature);

					dispatch(transmitter, new FeatureEditedEvent(this, beforeFeatureLine.get(0), afterFeatureLine.get(0), "LINE", true));		

					dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_edges, "EDITPATH_EDGES", false));
					editpath_edges = new EditPathService().createPathEdges(afterFeatureLine.get(0));
					dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_edges, "EDITPATH_EDGES", false));
				}	
				beforeFeatureLine.clear();
				afterFeatureLine.clear();				
			}
		}

		//PLOT LAYERS
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MousePressedEvent e) throws Exception{

		if(featIntersected == null){
			return;
		}

		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_box, "EDITPATH_BOX", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_edges, "EDITPATH_EDGES", false));

		if(featIntersected.getDefaultGeometry() instanceof LineString)
		{	

			envelopeBox =	new EditPathService().createEnvelopeBox(featIntersected);
			editpath_box = new PolygonCreatorService().createPolygon(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_box, "EDITPATH_BOX", false));

			editpath_resize = new EditPathService().createResizeEdges(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));

			editpath_edges = new EditPathService().createPathEdges(featIntersected);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_edges, "EDITPATH_EDGES", false));

			selected = true;	
		}

		if(positionEdge!=-1){	

			//REMOVE LINE;
			beforeFeatureLine.add(featIntersected);

			dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", false));

			//CREATE AND DISPATCH THE NEW SIMPLEFEATURE GHOST LINE
			Coordinate[] newCoord = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates().clone();
			ghostLine = new LineCreatorService().createLine(newCoord, featIntersected);
			ghostLine.setAttribute("selected", true);
			dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine, "GHOSTLINE", false));

			//REDRAW THE BOX / RESIZE / EDGES
			dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_box, "EDITPATH_BOX", false));
			dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));

			envelopeBox = new EditPathService().createEnvelopeBox(ghostLine);
			editpath_box = new PolygonCreatorService().createPolygon(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_box, "EDITPATH_BOX", false));

			editpath_resize = new EditPathService().createResizeEdges(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));

			edgeMoving = true;
		}

		//PLOT LAYER
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MouseReleasedEvent e) throws Exception{

		if(edgeMoving){
			//COPY ATTRIBUTES LIST FROM THE GEOM INTERSECTED
			Object[] list = beforeFeatureLine.get(0).getAttributes().toArray().clone();		

			//CREATE THE NEW GEOMETRY
			for(int i=0;i<list.length;i++){
				if(list[i] instanceof Geometry){
					list[i] = ((Geometry)ghostLine.getDefaultGeometry()).clone();
				}
			}

			//CONVERT OBJECT ARRAY TO ARRAYLIST
			List<Object> array = new ArrayList<Object>();
			for(Object obj : list){
				array.add(obj);
			}

			//REMOVE GHOSTLINE;			
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));

			//CREATE AND DISPATCH THE NEW SIMPLEFEATURE GHOST LINE
			afterFeatureLine.add(new LineCreatorService().createLine(array, featIntersected));
			dispatch(transmitter,  new FeatureEditedEvent(this, beforeFeatureLine.get(0), afterFeatureLine.get(0), "LINE", true));

			dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_edges, "EDITPATH_EDGES", false));
			editpath_edges = new EditPathService().createPathEdges(afterFeatureLine.get(0));
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_edges, "EDITPATH_EDGES", false));

			edgeMoving= false;
			afterFeatureLine.clear();
			beforeFeatureLine.clear();
		}

		//PLOT LAYER
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MouseMovedEvent e) throws Exception
	{		
		SimpleFeature aux = snapPoint;	
		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "SNAPPOINT", false));

		IntersectGeometry intersectGeometry = new IntersectGeometry();		
		intersectGeometry.checkForSnap(e.getX(), e.getY(), this, transmitter, pointSnapTolerance);

		featIntersected = intersectGeometry.getFeature();

		//DRAW SNAP POINT		
		snapPoint = intersectGeometry.getSnapPoint();
		dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));

		if(selected){
			Coordinate coord = new Coordinate(e.getX(), e.getY()); 
			geomAtual = new GeometryFactory().createPoint(coord);

			resizeCollection = ((GeometryCollection)editpath_resize.getDefaultGeometry());
			positionResize = new IntersectGeometry().checkForSnap(e.getX(), e.getY(), 10, resizeCollection, this, transmitter);

			edgeCollection = ((GeometryCollection)editpath_edges.getDefaultGeometry());
			positionEdge = new IntersectGeometry().checkForSnap(e.getX(), e.getY(), 10, edgeCollection, this, transmitter);

			setCursor();
		}
		//PLOT LAYER
		dispatchLayersFeedback();
	}

	private void handle(MouseDraggedEvent e) throws Exception{

		if(featIntersected==null){
			return;
		}		

		if(positionEdge != -1){
			//COPY ATTRIBUTES LIST FROM THE GEOM INTERSECTED
			Object[] list = beforeFeatureLine.get(0).getAttributes().toArray().clone();		
			Coordinate[] newCoord = ((Geometry)beforeFeatureLine.get(0).getDefaultGeometry()).getCoordinates().clone();

			//CREATE THE NEW COORDINATE FOR THE LINE
			for(int i=0; i<newCoord.length; i++){
				if(((GeometryCollection)editpath_edges.getDefaultGeometry()).getGeometryN(positionEdge).getCoordinate().equals(newCoord[i])){
					newCoord[i] = new Coordinate(e.getX(), e.getY());
				}
			}

			//CREATE THE NEW GEOMETRY
			for(int i=0;i<list.length;i++){
				if(list[i] instanceof Geometry){
					list[i] = new LineCreatorService().createLine(newCoord);
				}
			}

			//CONVERT OBJECT ARRAY TO ARRAYLIST
			List<Object> array = new ArrayList<Object>();
			for(Object obj : list){
				array.add(obj);
			}		

			//REMOVE GHOSTLINE;					
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));

			ghostLine = new LineCreatorService().createLine(array, featIntersected);
			ghostLine.setAttribute("selected", true);
			dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine, "GHOSTLINE", false));			
			/*
			//RELOCATE THE POINTS IF POSITION IS FIRST OR THE LAST EDGE;
			GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
			GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
			dispatch(transmitter, getPoint);
			dispatch(transmitter, getLine);
			SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
			SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();


			if(positionEdge==0){
				SimpleFeature point = new IntersectGeometry().getFeature((String) geomIntersected.getAttribute("point1"), pointCollection);
				beforeFeaturePoint.add(point);

				Geometry geomTemp = (Geometry) point.getAttribute(0);
				ghostPoint = new PointCreatorService().createPoint(geomTemp.getCoordinate(), point.getID());
				ghostPoint.setAttribute("selected", true);

				SimpleFeatureIterator iteratorLine = lineCollection.features();			
				while(iteratorLine.hasNext()){		
					SimpleFeature featureLine = iteratorLine.next();
					String point1 = (String) featureLine.getAttribute("point1");
					String point2 = (String) featureLine.getAttribute("point2");
					String pointID = (String) point.getID();
					if(!geomIntersected.equals(featureLine)){
						if(point1.equals(pointID) || point2.equals(pointID)){											
							beforeFeatureLine.add(featureLine);
							Geometry geomTemp2 = (Geometry) featureLine.getAttribute(0);
							ghostLines.add(new LineCreatorService().createLine(geomTemp2.getCoordinates(), featureLine.getID()));
							ghostLines.get(ghostLines.size()-1).setAttribute("selected", true);
						}
					}
				}

				for(int i=0;i<beforeFeaturePoint.size();i++){
					beforeFeaturePoint.get(i).setAttribute("selected", false);
					dispatch(transmitter, new FeatureRemovedEvent(this, beforeFeaturePoint.get(i), "POINT", false));
				}				
				for(int i=0;i<ghostPoints.size();i++)
					dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoints.get(i) , "GHOSTPOINT", false));
			}*/		

			//REDRAW THE BOX / RESIZE / EDGES
			dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_box, "EDITPATH_BOX", false));
			dispatch(transmitter,  new FeatureRemovedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));

			envelopeBox = new EditPathService().createEnvelopeBox(ghostLine);
			editpath_box = new PolygonCreatorService().createPolygon(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_box, "EDITPATH_BOX", false));

			editpath_resize = new EditPathService().createResizeEdges(envelopeBox);
			dispatch(transmitter,  new FeatureCreatedEvent(this, editpath_resize, "EDITPATH_RESIZE", false));

			Geometry geom = ((GeometryCollection)editpath_edges.getDefaultGeometry()).getGeometryN(positionEdge);
			geom.getCoordinate().setCoordinate(new Coordinate(e.getX(), e.getY()));
		}

		//PLOT LAYER
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(KeyPressedEvent e) throws Exception{

	}

	private void setCursor()  throws Exception{
		geomAtual = new IntersectGeometry().getSnapBox(geomAtual.getCoordinate().x, geomAtual.getCoordinate().y, 10, this, transmitter);
		if(geomAtual.intersects(envelopeBox.getEnvelope())){
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
					getClass().getResource("/com/nexusbr/gv/images/moveReleased.png")),new Point(16,16),"move");

			if(positionEdge!=-1){
				cursor = Cursor.getDefaultCursor();
				dispatch(transmitter, new FeatureRemovedEvent(this, snapPoint, "SNAPPOINT", false));
			}else{				
				if(positionResize == 0){
					cursor = new Cursor(Cursor.SW_RESIZE_CURSOR);
				}
				else if(positionResize == 1){
					cursor = new Cursor(Cursor.W_RESIZE_CURSOR);
				}
				else if(positionResize == 2){
					cursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
				}
				else if(positionResize == 3){
					cursor = new Cursor(Cursor.N_RESIZE_CURSOR);
				}
				else if(positionResize == 4){
					cursor = new Cursor(Cursor.NE_RESIZE_CURSOR);
				}
				else if(positionResize == 5){
					cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
				}
				else if(positionResize == 6){
					cursor = new Cursor(Cursor.SE_RESIZE_CURSOR);
				}
				else if(positionResize == 7){
					cursor = new Cursor(Cursor.S_RESIZE_CURSOR);
				}
			}
		}else{
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
					getClass().getResource("/com/nexusbr/gv/images/rotate.png")),new Point(16,16),"rotate");
		}	

		dispatch(transmitter, new UpdateCursorEvent(this));
	}
}

