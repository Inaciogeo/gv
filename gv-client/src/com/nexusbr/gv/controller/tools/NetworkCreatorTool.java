package com.nexusbr.gv.controller.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventObject;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.GetCanvasStateEvent;
import br.org.funcate.glue.event.KeyPressedEvent;
import br.org.funcate.glue.event.KeyReleasedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Box;
import br.org.funcate.glue.model.CalculatorService;
import br.org.funcate.glue.model.Layer;
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
import com.nexusbr.gv.services.PolygonCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * This class recive the event Eagle, create the Network them send a event to redraw
 *
 * @author Bruno Severino
 * @version 1.0
 */
public class NetworkCreatorTool extends Manager {
	
	private double x1=0;
	private double y1=0;	
	private double x2=0;
	private double y2=0;
	private double xClick=0;
	private double yClick=0;
	private boolean firstPointSnap = false;
	private boolean firstPointIntersecting=false;
	private boolean drawLineWithVertices=false;
	private boolean firstPoint = false;
	private boolean snapLine = false;
	private boolean snapPointFound = false;
	private boolean firstLineSnap=false;
	private boolean lastPoint = false;
	private boolean snapLineFound = false;
	//private boolean firstTime = false;
	private String lineVertice = null;
	private String idPointOne;	
	private String idPointTwo;
	private Geometry geomIntersected = null;
	private SimpleFeature featIntersected = null;
	private SimpleFeature firstPointIntersected=null;	
	private SimpleFeature intersectedLine;		
	private SimpleFeature lastFeaturePoint;
	private SimpleFeature lastFeatureLine;	
	private SimpleFeature snapLineFeature;
	private SimpleFeature ghostPoint;
	private SimpleFeature ghostLine;
	private SimpleFeature snapPoint;
	private SimpleFeature referenceCircle;
	private SimpleFeature referenceCircleLine;
	private boolean pointReferenceState = false;
	private boolean pointRefFirstClick = true;
	private boolean snapParallel = false;
	
	
	public NetworkCreatorTool(){		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		//eventsToListen.add(KeyTypedEvent.class.getName());
		eventsToListen.add(KeyPressedEvent.class.getName());
		eventsToListen.add(KeyReleasedEvent.class.getName());
		eventsToListen.add(MouseMovedEvent.class.getName());		
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());		
		eventsToListen.add(AfterToolChangedEvent.class.getName());		
		eventsToListen.add(BeforeToolChangedEvent.class.getName());

		//CHANGE CURSOR TO PRESSED
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new java.awt.Point(16,16),"draw");

		GVSingleton gvSingleton = GVSingleton.getInstance();
		toolbar = gvSingleton.getToolbar();
		
		//AppSingleton.getInstance().getMediator().getCanvasController().setCanvasViewFocusable();

		firstPoint = true;
		snapPointFound=false;
		snapLine = false;
	}

	public void handle(EventObject e) throws Exception {
		// TODO Auto-generated method stub
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}

		if(e instanceof BeforeToolChangedEvent){
			handle((BeforeToolChangedEvent) e);
		}

		if(e instanceof KeyPressedEvent){
			handle((KeyPressedEvent)e);
		}

		if(e instanceof KeyReleasedEvent){
			handle((KeyReleasedEvent)e);
		}
		
		if(e instanceof MousePressedEvent){
			handle((MousePressedEvent)e);
		}

		if(e instanceof MouseReleasedEvent){
			handle((MouseReleasedEvent)e);
		}

		if(e instanceof MouseMovedEvent){
			handle((MouseMovedEvent)e);
		}		
	}

	private void handle(AfterToolChangedEvent e) throws Exception{
		//CREATE POINT STYLE
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createLineStyle(), "LINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createPointStyle3(), "POINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSnapLineStyle(), "SNAPLINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createSnapPointStyle(), "SNAPPOINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostLineStyle(), "GHOSTLINE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createGhostPointStyle(), "GHOSTPOINT"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createReferenceCircleStyle(), "REFCIRCLE"));
		dispatch(transmitter, new SetStyleEvent(this, new CreateFeatureStyle().createReferenceCircleLineStyle(), "REFCIRCLELINE"));
	}

	private void handle(BeforeToolChangedEvent e) throws Exception{
		//REMOVE GHOST LINE AND POINT
		dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		
		dispatchLayersFeedback();
		dispatchLayersEdition();
	}

	private void handle(MousePressedEvent e) throws Exception {
		
		if(e.getButton()==MouseEvent.BUTTON3){
			//REMOVE GHOST LINE AND POINT
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));

			//REMOVE firstPoint, ONLY IF THE FIRST POINT ISN'T SNAPING
			if(firstPoint)
				dispatch(transmitter,  new FeatureRemovedEvent(this, lastFeaturePoint, "POINT", true));

			firstPoint=true;
			firstLineSnap=false;
			lastPoint = false;
			//firstTime = true;
			lineVertice = null;
			pointReferenceState = false;
			drawLineWithVertices = false;
		}
		else if(e.getButton()==MouseEvent.BUTTON2){
			
			// CHANGE TOOL TO GEOMETRYMOVETOOL
			AppSingleton.getInstance().getMediator().setCurrentTool(new FeatureMoveTool());
		}
		else if(e.getButton()==MouseEvent.BUTTON1){
			if(pointReferenceState){
				if(pointRefFirstClick){					
					cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
					dispatch(transmitter, new UpdateCursorEvent(this));
					
					xClick = e.getX();
					yClick = e.getY();
					
					pointRefFirstClick = false;					
				}else{
					cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
							getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");
					dispatch(transmitter, new UpdateCursorEvent(this));
					
					ArrayList<SimpleFeature> referencePoint = new ArrayList<SimpleFeature>();
					referencePoint.add(referenceCircle);
					referencePoint.add(referenceCircleLine);
					
					dispatch(transmitter,  new FeatureRemovedEvent(this, referenceCircle , "REFCIRCLE", false));
					dispatch(transmitter,  new FeatureRemovedEvent(this, referenceCircleLine , "REFCIRCLELINE", false));
					
					
					/*
					Geometry tempGeom = new PolygonCreatorService().createCircle();	
					 = new PointCreatorService().createReferenceCircle(circle);
					*/
					Geometry tempGeomCir = new LineCreatorService().createLine(((Geometry)referenceCircle.getDefaultGeometry()).getCoordinates().clone());
					SimpleFeature tempFeatCir = new LineCreatorService().createReferenceCircleLine(tempGeomCir, referenceCircle.getAttribute("radius").toString());
					dispatch(transmitter,  new FeatureCreatedEvent(this, tempFeatCir , "REFCIRCLE", true));
				
					Geometry tempGeomCirLine = new LineCreatorService().createLine(((Geometry)referenceCircleLine.getDefaultGeometry()).getCoordinates().clone());
					SimpleFeature tempFeatCirLine = new LineCreatorService().createReferenceCircleLine(tempGeomCirLine, referenceCircleLine.getAttribute("radius").toString());
					dispatch(transmitter,  new FeatureCreatedEvent(this, tempFeatCirLine , "REFCIRCLELINE", true));
					
					//NADA POR ENQUANTO
					
					pointRefFirstClick = true;
					pointReferenceState = false;
					
				}
				return;
			}

			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(
					getClass().getResource("/com/nexusbr/gv/images/curPressed.png")),new Point(16,16),"draw");
			dispatch(transmitter, new UpdateCursorEvent(this));
			
			if(!drawLineWithVertices){
				if(firstPoint){				// IF FIRST POINT IN NETWORK
					if(snapLine){				// IF THE SNAP LINE IS TRUE
						x1 = (double) geomIntersected.getCentroid().getCoordinate().x;			// SAVE NEW X FROM SCREEN
						y1 = (double) geomIntersected.getCentroid().getCoordinate().y;			// SAVE NEW Y FROM SCREEN
						lastFeaturePoint = new PointCreatorService().createPoint(x1, y1); //CREATE AND ADD POINT IN A COLLECTION
						lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
						idPointOne = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED  	    			

						ghostPoint = new PointCreatorService().createPoint(x1, y1);
						dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));

						intersectedLine = featIntersected;
						firstLineSnap = true;	    	
					}
					else{						// IF NOT SNAPING IN A LINE
						if(snapPointFound){		// IF SNAPING A POINT
							x1 = (double) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().x;		// SAVE COORDINATE X FROM POINT FEATURE SNAPED
							y1 = (double) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().y;		// SAVE COORDINATE Y FROM POINT FEATURE SNAPED
							idPointOne = featIntersected.getID();							// SAVE ID FROM POINT FEATURE SNAPED
							firstPointSnap = true;														// SET firstPointNWNWSNAP, TRUE
						}
						else{					// ELSE IF IT ISN'T SNAPING A POINT
							x1 = e.getX();		// SAVE NEW X FROM SCREEN
							y1 = e.getY();		// SAVE NEW Y FROM SCREEN
							lastFeaturePoint = new PointCreatorService().createPoint(x1, y1); //CREATE AND ADD POINT IN A COLLECTION
							lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
							idPointOne = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED    			

							ghostPoint = new PointCreatorService().createPoint(x1, y1);
							dispatch(transmitter,  new FeatureCreatedEvent(this, ghostPoint , "GHOSTPOINT", false));

							firstPointSnap = false;
						}
						firstLineSnap = false;
					}	
					firstPoint=false;
				}
				else{							// ELSE IF FIRST POINT IS FALSE
					if(snapLine){				// IF THE SNAP LINE IS TRUE					
						//ADDING FIRST POINT IN EXISTEDLIST IF IT IS NOT SNAPED
						if(firstLineSnap || !firstPointSnap)
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));

						//CREATE SECOND POINT
						//if(!snapParallel){
							x2 = geomIntersected.getCentroid().getX();
							y2 = geomIntersected.getCentroid().getY();
						//}
						lastFeaturePoint = new PointCreatorService().createPoint(x2, y2); //CREATE AND ADD POINT IN A COLLECTION
						lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
						idPointTwo = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED  
						dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));

						//CREATE THE LINE
						Coordinate[] newCoord0 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(x2,y2)};			
						lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord0);
						lastFeatureLine.setAttribute("network", true);
						lastFeatureLine.setAttribute("point1", idPointOne);
						lastFeatureLine.setAttribute("point2", idPointTwo);
						dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

						GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
						dispatch(transmitter, getPoint);
						SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();

						//VERIFY IF FIRST POINT WAS INTERSECTED IN A LINE, IF TRUE, BREAK PREVIOUS LINE IN TWO LINES
						if(firstLineSnap){	    				
							//SAVE THE FIRST AND THE LAST COORDINATE OF THE NETWORK
							Coordinate[] coord = ((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates();
							double px1 = (double) coord[0].x;
							double py1 = (double) coord[0].y;
							double px2 = (double) coord[coord.length-1].x;
							double py2 = (double) coord[coord.length-1].y;


							//CATCH THE FIRST AND LAST POINT OF THE GEOMETRY
							SimpleFeature point1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
							SimpleFeature point2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

							//NOW VERIFY IF THE GEOMETRY INTERSECTED IS A NETWORK VERTICE
							if(((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates().length > 2){
								//CALL A METHODE TO BREAK LINE
								breakVertLine(coord, x1, y1, idPointOne, point1.getID(), point2.getID());
								dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
							}
							//ELSE IF IT ISN'T A NETWORK VERTICE
							else{			
								//CREATE FIRST LINE 
								Coordinate[] newCoord = new Coordinate[]{new Coordinate(px1,py1),new Coordinate(x1,y1)};		    
								lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
								lastFeatureLine.setAttribute("network", true);
								lastFeatureLine.setAttribute("point1", point1.getID());
								lastFeatureLine.setAttribute("point2", idPointOne);
								
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

								//CREATE SECOND LINE
								Coordinate[] newCoord2 = new Coordinate[]{new Coordinate(x1,y1), new Coordinate(px2,py2)};
								lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord2);
								lastFeatureLine.setAttribute("network", true);
								lastFeatureLine.setAttribute("point1", idPointOne);
								lastFeatureLine.setAttribute("point2", point2.getID());
								
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
								dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
							}
						}
						
						//if(!snapParallel){
							//BREAK THE SECOND LINE TWO PARTS
							//SAVE THE FIRST AND THE LAST COORDINATE OF THE NETWORK
							Coordinate[] coord = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates();
							double px1 = (double) coord[0].x;
							double py1 = (double) coord[0].y;
							double px2 = (double) coord[coord.length-1].x;
							double py2 = (double) coord[coord.length-1].y;
	
							//CATCH THE FIRST AND LAST POINT OF THE GEOMETRY
							SimpleFeature point1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
							SimpleFeature point2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);
	
							//NOW VERIFY IF THE GEOMETRY INTERSECTED IS A NETWORK VERTICE
							if(((Geometry)featIntersected.getDefaultGeometry()).getCoordinates().length > 2){
								//CALL A METHODE TO BREAK LINE
								breakVertLine(coord, x2, y2, idPointTwo, point1.getID(), point2.getID());
								dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", true));
							}
							//ELSE IF IT ISN'T A NETWORK VERTICE
							else{			
								
								// TODO BREAK LINE IN TWO PIECES
								LineCreatorService service = new LineCreatorService();

								
								//CREATE FIRST LINE 
								Coordinate[] newCoord = new Coordinate[]{new Coordinate(px1,py1),new Coordinate(x2,y2)};	
								lastFeatureLine = service.createFeatureLine(newCoord);
								lastFeatureLine.setAttribute("network", true);
								lastFeatureLine.setAttribute("point1", point1.getID());
								lastFeatureLine.setAttribute("point2", idPointTwo);
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
	
								//CREATE SECOND LINE
								Coordinate[] newCoord2 = new Coordinate[]{new Coordinate(x2,y2),new Coordinate(px2,py2)};
								
								lastFeatureLine = service.createFeatureLine(newCoord2);
								lastFeatureLine.setAttribute("network", true);
								lastFeatureLine.setAttribute("point1", idPointTwo);
								lastFeatureLine.setAttribute("point2", point2.getID());
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
								
								// Add line to be removed
								GVSingleton.getInstance().getLinesDeleted().add(featIntersected);
								dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", true));
							}	
						//}
						x1 = x2;
						y1 = y2;
						idPointOne = idPointTwo;		    			
						firstLineSnap=false;
						firstPointSnap=true;
						snapPointFound = false;
					}
					// IF NOT SNAPING IN A LINE
					else{
						//REMOVE GHOST LINE AND POINT
						dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
						dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));


						if(snapPointFound){			// IF SNAPING A POINT
							//ADDING FIRST POINT IN EXISTEDLIST IF IT IS NOT SNAPED
							if(firstLineSnap || !firstPointSnap)
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));

							//CATCHING THE SECOND POINT
							x2 = (double) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().x;
							y2 = (double) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().y;
							idPointTwo = featIntersected.getID();

							//CREATE THE LINE
							Coordinate[] newCoord0 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(x2,y2)};
							lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord0);
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", idPointOne);
							lastFeatureLine.setAttribute("point2", idPointTwo);
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

							//VERIFY IF FIRST POINT WAS INTERSECTED IN A LINE, IF TRUE, BREAK PREVIOUS LINE IN TWO LINES
							if(firstLineSnap){	    				
								//SAVE THE FIRST AND THE LAST COORDINATE OF THE NETWORK
								Coordinate[] coord = ((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates();
								double px1 = (double) coord[0].x;
								double py1 = (double) coord[0].y;
								double px2 = (double) coord[coord.length-1].x;
								double py2 = (double) coord[coord.length-1].y;

								//CATCH THE FIRST AND LAST POINT OF THE GEOMETRY
								GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
								dispatch(transmitter, getPoint);
								SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
								SimpleFeature point1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
								SimpleFeature point2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

								//NOW VERIFY IF THE GEOMETRY INTERSECTED IS A NETWORK VERTICE
								if(((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates().length > 2){
									//CALL A METHODE TO BREAK LINE
									breakVertLine(coord, x1, y1, idPointOne, point1.getID(), point2.getID());
									dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
								}
								//ELSE IF IT ISN'T A NETWORK VERTICE
								else{			
									//CREATE FIRST LINE 
									Coordinate[] newCoord = new Coordinate[]{new Coordinate(px1,py1),new Coordinate(x1,y1)};
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", point1.getID());
									lastFeatureLine.setAttribute("point2", idPointOne);
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

									//CREATE SECOND LINE
									Coordinate[] newCoord2 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(px2,py2)};
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord2);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", idPointOne);
									lastFeatureLine.setAttribute("point2", point2.getID());	    					
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
									dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
								}
							}

							x1 = x2;
							y1 = y2;
							idPointOne = idPointTwo;		    			
							firstLineSnap=false;
							firstPointSnap=true;
							snapPointFound = false;

						}
						else{					// ELSE IF IT ISN'T SNAPING A POINT
							//ADDING FIRST POINT IN EXISTEDLIST IF IT IS NOT SNAPED
							if(firstLineSnap || !firstPointSnap)
								dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));

							//CREATE SECOND POINT
							if(!snapParallel){
								x2 = e.getX();
								y2 = e.getY();
							}
							lastFeaturePoint = new PointCreatorService().createPoint(x2, y2); //CREATE AND ADD POINT IN A COLLECTION
							lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
							idPointTwo = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));

							//CREATE THE LINE
							Coordinate[] newCoord0 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(x2,y2)};
							lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord0);
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", idPointOne);
							lastFeatureLine.setAttribute("point2", idPointTwo);	
							
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

							GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
							dispatch(transmitter, getPoint);
							SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();

							//VERIFY IF FIRST POINT WAS INTERSECTED IN A LINE, IF TRUE, BREAK PREVIOUS LINE IN TWO LINES
							if(firstLineSnap){	    				
								//SAVE THE FIRST AND THE LAST COORDINATE OF THE NETWORK
								Coordinate[] coord = ((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates();
								double px1 = (double) coord[0].x;
								double py1 = (double) coord[0].y;
								double px2 = (double) coord[coord.length-1].x;
								double py2 = (double) coord[coord.length-1].y;

								//CATCH THE FIRST AND LAST POINT OF THE GEOMETRY
								SimpleFeature point1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
								SimpleFeature point2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

								//NOW VERIFY IF THE GEOMETRY INTERSECTED IS A NETWORK VERTICE
								if(((Geometry)intersectedLine.getDefaultGeometry()).getCoordinates().length > 2){
									//CALL A METHODE TO BREAK LINE
									breakVertLine(coord, x1, y1, idPointOne, point1.getID(), point2.getID());
									dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
								}
								//ELSE IF IT ISN'T A NETWORK VERTICE
								else{			
									//CREATE FIRST LINE 
									// TODO COMO FUNCIONA A PARTIR DE UMA LINHA QUE NAO FAZ PARTE DE NETWORK 
									Coordinate[] newCoord = new Coordinate[]{new Coordinate(px1,py1),new Coordinate(x1,y1)};
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", point1.getID());
									lastFeatureLine.setAttribute("point2", idPointOne);	
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

									//CREATE SECOND LINE
									Coordinate[] newCoord2 = new Coordinate[]{new Coordinate(x1,y1),new Coordinate(px2,py2)};
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord2);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", idPointOne);
									lastFeatureLine.setAttribute("point2", point2.getID());	
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
									dispatch(transmitter,  new FeatureRemovedEvent(this, intersectedLine, "LINE", true));
									EditionState.setEdited(true);
								}			
							}
							
							x1 = x2;
							y1 = y2;
							idPointOne = idPointTwo;		    			
							firstLineSnap=false;
							firstPointSnap=true;
							snapPointFound = false;
						}
					}

					//POPULATE PROPERTIES FRAME					
					GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
					dispatch(transmitter, getPoint);
					SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();

					toolbar.setFeaturePoint1(new IntersectGeometry().getFeature((String) lastFeatureLine.getAttribute("point1"), pointCollection));
					toolbar.setFeaturePoint2(new IntersectGeometry().getFeature((String) lastFeatureLine.getAttribute("point2"), pointCollection));
					toolbar.setFeature(lastFeatureLine);
					CustomNode theme = AppSingleton.getInstance().getMediator().getCurrentTheme();
					if(theme!=null){
						Layer layer = theme.getLayer();
						if(layer!=null){
							// TODO Nova janela de atributos
							/*Table table = new TablesBO().getTableFromLayerID(layer.getId());
							if(table!=null){
								toolbar.getFramePropertie().popCmbLayer(2L);
								toolbar.getFramePropertie().setLayer(table.getLayerName());
								toolbar.getFramePropertie().cleanTable();
							}*/
						}
					}
				}	
			}
			else{
				if(!lastPoint){
					if(!firstPoint){
						x2 = e.getX();
						y2 = e.getY();			
						lineVertice +=  x2+" "+y2+", ";
					}
				}else{
					//REMOVE GHOUST LINE AND POINT
					dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
					dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));

					//ADDING FIRST POINT IN THE EXISTING LIST ONLY IF THE FIRST POINT WASN'T
					if(!firstPointIntersecting){
						if(!firstPointSnap)
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));
					}else{
						dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));
					}

					IntersectGeometry intGeom = new IntersectGeometry();
					GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
					GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
					dispatch(transmitter, getPoint);
					dispatch(transmitter, getLine);
					SimpleFeatureCollection pointCollection2 = getPoint.getFeatureCollection();
					SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();

					dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));

					snapPointFound = intGeom.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, pointCollection2, this, transmitter);
					if(snapPointFound){
						snapLine = false;
						geomIntersected = intGeom.getGeomIntersecting();
						featIntersected = intGeom.getFeature();
						featIntersected.setAttribute("selected", true);
						snapPointFound = true;			

					}else{
						snapLineFound = intGeom.checkForSnap(e.getX(), e.getY(), lineSnapTolerance, lineCollection, this, transmitter);
						if(snapLineFound){					
							snapLine = true;
							geomIntersected = intGeom.getGeomIntersecting();
							featIntersected = intGeom.getFeature();
							snapPointFound = false;	
						}
					}

					//CREATE SECOUND POINT IF IT ISN'T SNAPED
					if(!snapLine){
						if(!snapPointFound){
							x2 = e.getX();
							y2 = e.getY();
							lineVertice += x2+" "+y2+")";
							lastFeaturePoint = new PointCreatorService().createPoint(x2, y2); //CREATE AND ADD POINT IN A COLLECTION
							lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
							idPointTwo = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));
						}else{
							x2 = (int) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().x;
							y2 = (int) ((Geometry) featIntersected.getDefaultGeometry()).getCoordinate().y;
							lineVertice += x2+" "+y2+")";
							idPointTwo = featIntersected.getID();
						}
					}else{
						x2 = e.getX();
						y2 = e.getY();
						lineVertice += x2+" "+y2+")";
						lastFeaturePoint = new PointCreatorService().createPoint(x2, y2); //CREATE AND ADD POINT IN A COLLECTION
						lastFeaturePoint.setAttribute("network", true);					// SET ATTRIBUTE 2(NETWORK), TRUE
						idPointTwo = lastFeaturePoint.getID();		// GET ID FROM THE POINT CREATED
						dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeaturePoint, "POINT", true));
					}

					//CREATE THE LINE
					lastFeatureLine = new LineCreatorService().createFeatureLine(new LineCreatorService().createLine(lineVertice).getCoordinates());
					lastFeatureLine.setAttribute("network", true);
					lastFeatureLine.setAttribute("point1", idPointOne);
					lastFeatureLine.setAttribute("point2", idPointTwo);	
					dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

					GetFeatureEvent getPoint2 = new GetFeatureEvent(this, "POINT");
					dispatch(transmitter, getPoint2);
					SimpleFeatureCollection pointCollection = getPoint2.getFeatureCollection();

					//BREAK PREVIOUS LINE IN TWO PARTS
					if(firstPointIntersecting){						
						if(((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates().length>2){
							Coordinate[] coord = ((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates();
							double px1 = (double) coord[0].x;
							double py1 = (double) coord[0].y;
							double px2 = (double) coord[coord.length-1].x;
							double py2 = (double) coord[coord.length-1].y;

							SimpleFeature ponto1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
							SimpleFeature ponto2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

							for(int i=0;i<coord.length-1;i++){		
								Coordinate[] tempCoord = new Coordinate[]{new Coordinate(coord[i].x, coord[i].y), new Coordinate(coord[i+1].x, coord[i+1].y)};
								if(new IntersectGeometry().checkForSnap(x1, y1, lineSnapTolerance, new GeometryFactory().createLineString(tempCoord), this, transmitter)){
									Coordinate[] newCoord = new Coordinate[i+2];
									for(int j=0;j<=i;j++){
										newCoord[j] = coord[j];
									}				
									newCoord[i+1] = new Coordinate(x1, y1);
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", ponto1.getID());
									lastFeatureLine.setAttribute("point2", idPointOne);
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

									Coordinate[] newCoord2 = new Coordinate[(coord.length-i)];
									newCoord2[0] = new Coordinate(x1, y1);
									int cont = 1;
									for(int k=i+1;k<coord.length;k++){
										newCoord2[cont] = coord[k];
										cont++;
									}
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", idPointOne);
									lastFeatureLine.setAttribute("point2", ponto2.getID());	
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
								}
							}
						}
						else{
							double px1 = (double) ((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates()[0].x;
							double py1 = (double) ((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates()[0].y;
							double px2 = (double) ((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates()[1].x;
							double py2 = (double) ((Geometry)firstPointIntersected.getDefaultGeometry()).getCoordinates()[1].y;    			

							SimpleFeature ponto1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);

							lastFeatureLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(px1, py1),new Coordinate(x1,y1)});
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", ponto1.getID());
							lastFeatureLine.setAttribute("point2", idPointOne);
							
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));				

							SimpleFeature ponto2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

							lastFeatureLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(px2, py2),new Coordinate(x1,y1)});
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", idPointOne);
							lastFeatureLine.setAttribute("point2", ponto2.getID());
							
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
						}

						//akee
						dispatch(transmitter,  new FeatureRemovedEvent(this, firstPointIntersected, "LINE", true));						
						firstPointIntersecting=false;
					}					

					if(snapLine){		    			
						//BREAK PREVIOUS LINE IN TWO PARTS
						if(((Geometry)featIntersected.getDefaultGeometry()).getCoordinates().length>2){
							Coordinate[] coord = ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates();
							double px1 = (double) coord[0].x;
							double py1 = (double) coord[0].y;
							double px2 = (double) coord[coord.length-1].x;
							double py2 = (double) coord[coord.length-1].y;

							SimpleFeature ponto1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);
							SimpleFeature ponto2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);

							for(int i=0;i<coord.length-1;i++){		
								Coordinate[] tempCoord = new Coordinate[]{new Coordinate(coord[i].x, coord[i].y), new Coordinate(coord[i+1].x, coord[i+1].y)};
								if(new IntersectGeometry().checkForSnap(x2, y2, lineSnapTolerance, new GeometryFactory().createLineString(tempCoord), this, transmitter)){
									Coordinate[] newCoord = new Coordinate[i+2];
									for(int j=0;j<=i;j++){
										newCoord[j] = coord[j];
									}				
									newCoord[i+1] = new Coordinate(x2, y2);	
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", ponto1.getID());
									lastFeatureLine.setAttribute("point2", idPointTwo);
									
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));			

									Coordinate[] newCoord2 = new Coordinate[(coord.length-i)];
									newCoord2[0] = new Coordinate(x2, y2);
									int cont = 1;
									for(int k=i+1;k<coord.length;k++){
										newCoord2[cont] = coord[k];
										cont++;
									}
									lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord2);
									lastFeatureLine.setAttribute("network", true);
									lastFeatureLine.setAttribute("point1", idPointTwo);
									lastFeatureLine.setAttribute("point2", ponto2.getID());
									
									dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
								}
							}
						}
						else{
							double px1 = (double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].x;
							double py1 = (double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].y;
							double px2 = (double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].x;
							double py2 = (double) ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].y;    		

							SimpleFeature ponto1 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px1, py1)), pointCollection);		    			
							lastFeatureLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(px1, py1),new Coordinate(x2,y2)});
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", ponto1.getID());
							lastFeatureLine.setAttribute("point2", idPointTwo);
							
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));

							SimpleFeature ponto2 = new IntersectGeometry().intersectPointFeature(new GeometryFactory().createPoint(new Coordinate(px2, py2)), pointCollection);	 
							lastFeatureLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(px2, py2),new Coordinate(x2,y2)});
							lastFeatureLine.setAttribute("network", true);
							lastFeatureLine.setAttribute("point1", idPointOne);
							lastFeatureLine.setAttribute("point2", ponto2.getID());
							
							dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
						}
						dispatch(transmitter,  new FeatureRemovedEvent(this, featIntersected, "LINE", true));
					}	  
					firstPointIntersecting=false;
					lastPoint = false;
					//firstTime = true;
					x1 = x2;
					y1 = y2;
					idPointOne = idPointTwo;
					drawLineWithVertices = false;
					firstPointSnap=true;

					//POPULATE PROPERTIES FRAME
					GetFeatureEvent getPoint3 = new GetFeatureEvent(this, "POINT");
					dispatch(transmitter, getPoint3);
					SimpleFeatureCollection pointCollection3 = getPoint3.getFeatureCollection();

					toolbar.setFeaturePoint1(new IntersectGeometry().getFeature((String) lastFeatureLine.getAttribute("point1"), pointCollection3));
					toolbar.setFeaturePoint2(new IntersectGeometry().getFeature((String) lastFeatureLine.getAttribute("point2"), pointCollection3));
					toolbar.setFeature(lastFeatureLine);
					// TODO Nova janela de atributos
					/*Table table = new TablesBO().getTableFromLayerID(AppSingleton.getInstance().getMediator().getCurrentTheme().getLayer().getId());
					if(table!=null){
						toolbar.getFramePropertie().popCmbLayer(2L);
						toolbar.getFramePropertie().setLayer(table.getLayerName());
						toolbar.getFramePropertie().cleanTable();
					}*/
				}
			}
		}

		dispatchLayersFeedback();
		dispatchLayersEdition();
		EditionState.setEdited(true);
	}

	private void handle(MouseReleasedEvent e) throws Exception{
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
		IntersectGeometry intGeom = new IntersectGeometry();
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		dispatch(transmitter, getPoint);
		dispatch(transmitter, getLine);
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();

		double xValue = e.getX();
		double yValue = e.getY();
		
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		
		if(pointReferenceState){
			if(!pointRefFirstClick){								
				dispatch(transmitter,  new FeatureRemovedEvent(this, referenceCircle , "REFCIRCLE", false));
				dispatch(transmitter,  new FeatureRemovedEvent(this, referenceCircleLine , "REFCIRCLELINE", false));
				
				cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				dispatch(transmitter, new UpdateCursorEvent(this));
				
				double catOP = xValue - xClick;
				double catADJ = yValue - yClick;
				double radius = Math.hypot(catOP, catADJ);
				
				double[] point1 = CalculatorService.convertFromWorldToPixel(xClick, yClick);
				double[] point2 = CalculatorService.convertFromWorldToPixel(xValue, yValue);
				
				double dist = (CalculatorService.getMeterConversor(AppSingleton.getInstance().getCanvasState().getProjection())
						* AppSingleton.getInstance().getCanvasState().getResolution() * Math.hypot((point2[0]-point1[0]), (point2[1]-point1[1])));
				DecimalFormat df = new DecimalFormat("#,###.00");
				String distance = df.format(dist);
				
				Geometry circle = new PolygonCreatorService().createCircle(xClick, yClick, radius);	
				referenceCircle = new PointCreatorService().createReferenceCircle(circle, distance);
				dispatch(transmitter,  new FeatureCreatedEvent(this, referenceCircle , "REFCIRCLE", false));
				
				Geometry circleLine = new LineCreatorService().createLine(new Coordinate[]{new Coordinate(xClick, yClick), new Coordinate(xValue, yValue)});
				referenceCircleLine = new LineCreatorService().createReferenceCircleLine(circleLine, distance);
				dispatch(transmitter,  new FeatureCreatedEvent(this, referenceCircleLine , "REFCIRCLELINE", false));	
				
			}
		}
		
		if(snapParallel){
			snapLine = false; 
			
			Geometry pointTemp = new PointCreatorService().createGeomPoint(new Coordinate(e.getX(), e.getY()));
			Geometry pointSnaped = new PointCreatorService().createGeomPoint(((Coordinate[]) new DistanceOp(pointTemp, (Geometry) snapLineFeature.getDefaultGeometry()).nearestPoints())[1]);
			
			snapPoint = new PointCreatorService().createPoint(pointSnaped.getCoordinate().x, pointSnaped.getCoordinate().y);		
			dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));
			
			xValue = pointSnaped.getCoordinate().x;
			yValue = pointSnaped.getCoordinate().y;
			x2 = pointSnaped.getCoordinate().x;
			y2 = pointSnaped.getCoordinate().y;			
			
			snapPointFound = intGeom.checkForSnap(xValue, yValue, pointSnapTolerance, pointCollection, this, transmitter);
			if(snapPointFound){
				snapLine = false;
				geomIntersected = intGeom.getGeomIntersecting();
				featIntersected = intGeom.getFeature();
				featIntersected.setAttribute("selected", true);
				snapPointFound = true;			
	
			}else{
				snapLineFound = intGeom.checkForSnap(xValue, yValue, lineSnapTolerance, lineCollection, this, transmitter);
				if(snapLineFound){					
					snapLine = true;
					geomIntersected = intGeom.getGeomIntersecting();
					xValue = geomIntersected.getCentroid().getX();
					yValue = geomIntersected.getCentroid().getY();
					featIntersected = intGeom.getFeature();
					snapPointFound = false;
					
					dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
					
					snapPoint = new PointCreatorService().createPoint(xValue, yValue);		
					dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));						
				}
			}
		}else{		
			snapPointFound = intGeom.checkForSnap(e.getX(), e.getY(), pointSnapTolerance, pointCollection, this, transmitter);
			if(snapPointFound){
				snapLine = false;
				geomIntersected = intGeom.getGeomIntersecting();
				featIntersected = intGeom.getFeature();
				featIntersected.setAttribute("selected", true);
				snapPointFound = true;			
	
			}else{
				snapLineFound = intGeom.checkForSnap(e.getX(), e.getY(), lineSnapTolerance, lineCollection, this, transmitter);
				if(snapLineFound){					
					snapLine = true;
					geomIntersected = intGeom.getGeomIntersecting();
					xValue = geomIntersected.getCentroid().getX();
					yValue = geomIntersected.getCentroid().getY();
					featIntersected = intGeom.getFeature();
					snapPointFound = false;
					snapPoint = new PointCreatorService().createPoint(((Geometry)geomIntersected).getCentroid().getX(), ((Geometry)geomIntersected).getCentroid().getY());		
					dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));		
					
				}
			}
			if(!snapPointFound && !snapLineFound)
			{
				snapPointFound=false;
				snapLine=false;
			}	
		}

		dispatchLayersFeedback();
		dispatchLayersEdition();

		if(firstPoint){
			return;
		}			
		
		//DRAW GHOST LINE		
		SimpleFeature aux = ghostLine;
		if(drawLineWithVertices){		
			Geometry tempGeom = new LineCreatorService().createLine(lineVertice+xValue+" "+yValue+")");		
			ghostLine = new LineCreatorService().createFeatureLine(tempGeom.getCoordinates());
			dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));	 		
		}
		else {
			ghostLine = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(x1,y1), new Coordinate(xValue, yValue)});
			dispatch(transmitter,  new FeatureCreatedEvent(this, ghostLine , "GHOSTLINE", false));
		}

		dispatch(transmitter,  new FeatureRemovedEvent(this, aux, "GHOSTLINE", false));

		dispatchLayersFeedback();
		dispatchLayersEdition();
	}
		
	private void handle(KeyPressedEvent e) throws Exception {	
		if(e.getKeyEvent().getKeyCode() == KeyEvent.VK_ESCAPE)
		{	
			//REMOVE GHOST LINE AND POINT
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostLine, "GHOSTLINE", false));
			dispatch(transmitter,  new FeatureRemovedEvent(this, ghostPoint, "GHOSTPOINT", false));

			//REMOVE firstPoint, ONLY IF THE FIRST POINT ISN'T SNAPING
			if(firstPoint)
				dispatch(transmitter,  new FeatureRemovedEvent(this, lastFeaturePoint, "POINT", true));

			firstPoint=true;
			firstLineSnap=false;
			lastPoint = false;
			//firstTime = true;
			lineVertice = null;
			drawLineWithVertices = false;
		}
		else if(e.getKeyEvent().isControlDown()){
			if(!firstPoint){
				if(snapLine){							
					dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
					if(!snapParallel){
						//CREATE THE GHOST DOTTED SNAP LINE
						snapParallel = true;					
						dispatch(transmitter, new FeatureRemovedEvent(this, snapLineFeature, "SNAPLINE", false));						
						
						GetCanvasStateEvent canvasState = new GetCanvasStateEvent(this);
						dispatch(transmitter, canvasState);				
						Box box = canvasState.getCanvasState().getBox();
						
						double distX = (((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].x - x1);
						double distY = (((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[0].y - y1);					
						double pX2Temp =  ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].x - distX;
						double pY2Temp =  ((Geometry)featIntersected.getDefaultGeometry()).getCoordinates()[1].y - distY;					
						double width = pX2Temp - x1;
						double height = pY2Temp - y1;
						
						// PEGANDO O PONTO 1 
						double newDistY = box.getY1() - y1;
						double newPY1 = y1 + newDistY;					
						double newHeight = pY2Temp - newPY1;					 												
						double newWidth = (width * newHeight)/height;
						double newPX1 = x1 + (width - newWidth);
						
						// PEGANDO O PONTO 2
						double newDistY2 = box.getY2() - pY2Temp;
						double newPY2 = pY2Temp + newDistY2;
						double newHeight2 = newPY2 - y1;					
						double newWidth2 = (width * newHeight2)/ height;					
						double newPX2 = pX2Temp - (width - newWidth2);
											
						// CRIANDO A LINHA PONTILHADA
						snapLineFeature = new LineCreatorService().createFeatureLine(new Coordinate[]{new Coordinate(newPX1, newPY1), new Coordinate(newPX2, newPY2)});
						dispatch(transmitter, new FeatureCreatedEvent(this, snapLineFeature, "SNAPLINE", false));
						
						dispatchLayersFeedback();
					}
				}else{
					if(firstLineSnap){
						//snapPerpendicular = true;	
					}
				}
			}			
		}
	}

	private void handle(KeyReleasedEvent e) throws Exception {
		// TODO Auto-generated method stub
		lastPoint = true;
		snapParallel = false;
		
		dispatch(transmitter,  new FeatureRemovedEvent(this, snapPoint , "SNAPPOINT", false));
		dispatch(transmitter, new FeatureRemovedEvent(this, snapLineFeature, "SNAPLINE", false));
	}
	
	/**
	 * Break Previous Line in two new Lines
	 * @param coord - <i><code>Coordinate[]</code></i> is required, the value coordinates </br>
	 * @param x1 - <i><code>Integer</code></i> is required, the value of x coordinate </br>
	 * @param y1 - <i><code>Integer</code></i> is required, the value of y coordinate </br>
	 * @param idPointSnaped - <i><code>String</code></i> is required, the value of id from the Point Intersected </br>
	 * @param idPoint1 - <i><code>String</code></i> is required, the value of id from first point </br>
	 * @param idPoint2 - <i><code>String</code></i> is required, the value of id from second point </br>
	 * @throws Exception 
	 */
	public void breakVertLine(Coordinate[] coord, double x1, double y1, String idPointSnaped, String idPoint1, String idPoint2) throws Exception{
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
				lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord);
				lastFeatureLine.setAttribute("point1", idPoint1);
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
				lastFeatureLine = new LineCreatorService().createFeatureLine(newCoord2);
				lastFeatureLine.setAttribute("point1", idPointSnaped);
				lastFeatureLine.setAttribute("point2", idPoint2);
				dispatch(transmitter,  new FeatureCreatedEvent(this, lastFeatureLine, "LINE", true));
			}
		}
	}
}