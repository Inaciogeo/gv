package com.nexusbr.gv.controller.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JOptionPane;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.MouseDraggedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.tree.CustomNode;
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
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * This class recive the event Eagle, create the Point them send a event to redraw
 *
 * @author Bruno Severino
 * @version 1.0
 */
public class PointCreatorTool extends Manager {
	
	private double x1;
	private double y1;
	private boolean btn1Pressed;
	private boolean btn2Pressed;
	private boolean snapPointFound = false;	
	private boolean snapLineFound = false;
	private String idPointOne;
	private Geometry geomIntersected = null;
	private SimpleFeature ghostPoint;
	private SimpleFeature snapPoint;
	private SimpleFeature featIntersected;	
	private SimpleFeature lastFeaturePoint;
	private SimpleFeature lastFeatureLine;
	public String layerName;
	
	public PointCreatorTool(){
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
		eventsToListen.add(MouseMovedEvent.class.getName());
		eventsToListen.add(MouseDraggedEvent.class.getName());

		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");
		
		GVSingleton gvSingleton = GVSingleton.getInstance();
		toolbar = gvSingleton.getToolbar();	
		layerName = "";
	}

	public void handle(EventObject e) throws Exception {
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT
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

		else if(e instanceof MouseDraggedEvent){
			handle((MouseDraggedEvent)e);
		}
	}

	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE POINT STYLE
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle(), "POINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPolygonStyle(), "POLYGON"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
		
		// LOCATE OBJECTS IN SCREEN		
//		ShowPointFeatures( locatePoints() );
//		ShowLineFeatures( locateLines() );
//		ShowPolygonFeatures( locatePolygons() );
	}

	private void handle(BeforeToolChangedEvent e) throws Exception{
		dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MousePressedEvent e) throws Exception 
	{	
		// CHANGE CURSOR
		changeCursorToDraw();		
		
		//REMOVE SNAP POINT
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		
		//CATCH X AND Y COORDINATE
		x1 = e.getX();
		y1 = e.getY();
		
		if(e.getButton() == MouseEvent.BUTTON3){ //if it's mouse right button
			if(btn1Pressed){
				dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));
			}
			btn2Pressed = true;
		}
		
		else if(e.getButton() == MouseEvent.BUTTON1){
			btn1Pressed = true;
			if(!snapPointFound){
				// if Intersect another geometry, the x and y coordinate changes to geometry point
				if(featIntersected != null){			
					if((Boolean) featIntersected.getAttribute("selected")){
						x1 = ((Geometry)geomIntersected).getCentroid().getX(); 
						y1 = ((Geometry)geomIntersected).getCentroid().getY();
					}
				}
				//create a ghost Point in the coordinate
				ghostPoint = new PointCreatorService().createPoint(x1, y1);				
				dispatch(transmitter, new FeatureCreatedEvent(this, ghostPoint, "GHOSTPOINT", false));
			}
			
		}

		dispatchLayersFeedback();		
		dispatchLayersEdition();
		EditionState.setEdited(true);
	}

	private void handle(MouseReleasedEvent e) throws Exception {
		// CHANGE CURSOR
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");		
		dispatch(transmitter, new UpdateCursorEvent(this));
		
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		
		if(e.getEvent().getButton() == MouseEvent.BUTTON3){
			return;
		}
		else if(e.getEvent().getButton() == MouseEvent.BUTTON1){
			if(!btn2Pressed){
				if(!snapPointFound){
					double posX = ((Geometry)ghostPoint.getAttribute(0)).getCoordinate().x;
					double posY = ((Geometry)ghostPoint.getAttribute(0)).getCoordinate().y;
	
					dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));
					
					lastFeaturePoint = new PointCreatorService().createPoint(posX, posY);
					
					CustomNode t = AppSingleton.getInstance().getTreeState().getCurrentTheme();
					
					if(t==null){
						JOptionPane.showMessageDialog(null, "Nenhum tema selecionado!");
						return;
					}
										
					idPointOne = lastFeaturePoint.getID();				
					GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
					dispatch(transmitter, getLine);
					SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
					
					IntersectGeometry intGeom = new IntersectGeometry();
					snapLineFound = intGeom.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, lineCollection, this, transmitter);
					if(snapLineFound){
						geomIntersected = intGeom.getGeomIntersecting();
						featIntersected = intGeom.getFeature();
						
						//VERIFY IF THE FEATURE IS A NETWORK
						boolean state = (Boolean) featIntersected.getAttribute("networkMode");
						if(state){
							lastFeaturePoint.setAttribute("networkMode", true);
							//lastFeaturePoint.setAttribute("networkMode", true);
							
							//SAVE THE FIRST AND THE LAST COORDINATE OF THE NETWORK
							Coordinate[] coord = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates();
							double px1 = (double) coord[0].x;
							double py1 = (double) coord[0].y;
							double px2 = (double) coord[coord.length-1].x;
							double py2 = (double) coord[coord.length-1].y;
	
							//CATCH THE FIRST AND LAST POINT OF THE GEOMETRY
							//String point1ID = (String) featIntersected.getAttribute("point1");
							//String point2ID = (String) featIntersected.getAttribute("point2");			
							
							//NOW VERIFY IF THE GEOMETRY INTERSECTED IS A NETWORK VERTICE
							if(((Geometry)featIntersected.getDefaultGeometry()).getCoordinates().length > 2){
								//CALL A METHODE TO BREAK LINE
								breakVertLine(coord, x1, y1, idPointOne, featIntersected);
								dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", true));						
							}				
							else{			
								//CREATE FIRST LINE 
								Coordinate[] newCoord = new Coordinate[]{new Coordinate(px1,py1),new Coordinate(x1,y1)};
								lastFeatureLine = new LineCreatorService().createLine(newCoord, featIntersected);
								lastFeatureLine.setAttribute("networkMode", true);
								lastFeatureLine.setAttribute("point2", idPointOne);						
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));							
	
								//CREATE SECOND LINE							
								Coordinate[] newCoord2 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(px2,py2)};
								lastFeatureLine = new LineCreatorService().createLine(newCoord2, featIntersected);
								lastFeatureLine.setAttribute("networkMode", true);
								lastFeatureLine.setAttribute("point1", idPointOne);	
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
								
								//DELETE PREVIOUS LINE
								dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", true));		    			
							}	
						}
					}
					
					dispatch(transmitter, new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));
					
					//POPULATE PROPERTIES FRAME
					toolbar.setFeature(lastFeaturePoint);		
					// TODO Nova janela de atributos
					/*Table table = new TablesBO().getTableFromLayerID(AppSingleton.getInstance().getMediator().getCurrentTheme().getLayer().getId());
					if(table!=null){
						toolbar.getFramePropertie().popCmbLayer(3L);
						toolbar.getFramePropertie().setLayer(table.getLayerName());
						toolbar.getFramePropertie().cleanTable();
					}*/
				}
			}
		}

		snapLineFound = false;
		snapPointFound = false; 
		featIntersected = null;
		btn1Pressed = false;
		btn2Pressed = false;
		
		dispatchLayersFeedback();		
		dispatchLayersEdition();
	}

	private void handle(MouseMovedEvent e) throws Exception {
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();		

		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		
		snapPointFound = false;
		snapLineFound = false;
		featIntersected = null;
		
		IntersectGeometry intGeom = new IntersectGeometry();
		snapPointFound = intGeom.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, pointCollection, this, transmitter);				
		if(snapPointFound){
			snapLineFound = false;			
			geomIntersected = intGeom.getGeomIntersecting();
			featIntersected = intGeom.getFeature();
		}else{
			new IntersectGeometry().setSelected(pointCollection, false);
			snapLineFound = intGeom.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, lineCollection, this, transmitter);
			if(snapLineFound){			
				geomIntersected = intGeom.getGeomIntersecting();
				featIntersected = intGeom.getFeature();
				snapPointFound = false;	
				
				if((Boolean) featIntersected.getAttribute("networkMode")){
					//DRAW SNAP POINT		
					SimpleFeature aux = snapPoint;
					dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "SNAPPOINT", false));
					double x = ((Geometry)geomIntersected).getCentroid().getX(); 
					double y = ((Geometry)geomIntersected).getCentroid().getY();
					snapPoint = new PointCreatorService().createPoint(x, y);			
					dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));
				}
			}
		}
		//PLOT LAYER
		dispatchLayersFeedback();
		dispatchLayersEdition(); 
	}
	
	private void handle(MouseDraggedEvent e) throws Exception{
		//REMOVE PREVIOUS GHOST SELECT POINT
		SimpleFeature aux = ghostPoint;
		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTPOINT", false));
		//DRAW GHOST SELECT POINT	
		ghostPoint = new PointCreatorService().createPoint(e.getX(), e.getY());
		dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));

		dispatchLayersFeedback();			
	}
	
	public void breakVertLine(Coordinate[] coord, double x1, double y1, String idPointSnaped, SimpleFeature feature) throws Exception{
		SimpleFeature lastFeatureLine;
		for(int i=0;i<coord.length-1;i++){			
			//CREATE A TEMPORARY LINE FROM EACH LINE IN VERTICE AND VERIFY IF IT INTERSECT WITH THE FIRST POINT 
			Coordinate[] tempCoord = new Coordinate[]{new Coordinate(coord[i].x, coord[i].y), new Coordinate(coord[i+1].x, coord[i+1].y)};
			if(new IntersectGeometry().checkForSnap(x1, y1, lineSnapTolerance, new GeometryFactory().createLineString(tempCoord), this, transmitter)){

				//NOW CREATE A NEW NETWORK VERTICE CATCHING THE COORDINATES UNTIL i CURRENT POSITION
				Coordinate[] newCoord = new Coordinate[i+2];
				for(int j=0;j<=i;j++)
					newCoord[j] = coord[j];
				newCoord[i+1] = new Coordinate(x1, y1);

				//ADD FIRST NEW NETWORK VERTICE IM EXISTED LIST				
				lastFeatureLine = new LineCreatorService().createLine(newCoord, feature);
				lastFeatureLine.setAttribute("point2", idPointSnaped);
				dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

				//NOW CREATE A SECOND NEW NETWORK VERTICE CATCHING THE COORDINATES FROM i CURRENT POSITION TO LAST
				Coordinate[] newCoord2 = new Coordinate[(coord.length-i)];
				newCoord2[0] = new Coordinate(x1, y1);
				int cont = 1;
				for(int k=i+1;k<coord.length;k++){
					newCoord2[cont] = coord[k];
					cont++;
				}

				//ADD FIRST NEW NETWORK VERTICE IM EXISTED LIST
				lastFeatureLine = new LineCreatorService().createLine(newCoord, feature);
				lastFeatureLine.setAttribute("point1", idPointSnaped);
				dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
			}
		}
	}
}
