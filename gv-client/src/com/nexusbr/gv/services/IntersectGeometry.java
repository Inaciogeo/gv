package com.nexusbr.gv.services;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventDispatcher;
import br.org.funcate.eagles.kernel.transmitter.EventTransmitter;
import br.org.funcate.glue.event.GetScreenCoordinates;
import br.org.funcate.glue.event.GetWorldCoordinates;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Layer;
import br.org.funcate.glue.model.tree.CustomNode;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;

import com.nexusbr.gv.singleton.GVSingleton;
import com.nexusbr.gv.view.components.ToolbarWindow;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class IntersectGeometry {

	private SimpleFeature featureIntersected;
	private Geometry geomintersection;
	private ToolbarWindow toolbar = GVSingleton.getInstance().getToolbar();

	public IntersectGeometry(){

	}		

	public boolean checkIntersection(Geometry polygon, SimpleFeatureCollection featureCollection, Boolean... lineNW) {
		boolean found = false;		
		SimpleFeatureIterator iterator = null; 
		try {
			iterator = featureCollection.features();
			while( iterator.hasNext() )
			{				
				SimpleFeature feature = iterator.next();
				Geometry geom = (Geometry) feature.getDefaultGeometry();				
				if(polygon.intersects(geom)){
					if(feature.getType().getTypeName().equals("Point")){
						if(feature.getAttribute("networkMode").equals(true)){ 
							feature.setAttribute("selected", true);	
							toolbar.setFeature(feature);
							fillDynamicAttributes(4L);
							found = true;	
						}
					}
					else if(feature.getType().getTypeName().equals("Line")){
						if(lineNW[0]){
							if(feature.getAttribute("networkMode").equals(true)){
								feature.setAttribute("selected", true);
								toolbar.setFeature(feature);
								fillDynamicAttributes(2L);
								found = true;	
							}
						}
						else{
							if(feature.getAttribute("networkMode").equals(false)){
								feature.setAttribute("selected", true);
								toolbar.setFeature(feature);
								fillDynamicAttributes(2L);
								found = true;	
							}
						}		     				     			
					}	     			
					else if(feature.getType().getTypeName().equals("Polygon")){
						feature.setAttribute("selected", true);
						toolbar.setFeature(feature);
						fillDynamicAttributes(1L);
						found = true;
					}
				}else{	
					feature.setAttribute("selected", false);
				}
			}
		}catch(Exception e){}		
		return found;
	}

	@SuppressWarnings("unchecked")
	public boolean checkFirstLine(Geometry polygon,	SimpleFeatureCollection lineCollection, SimpleFeatureCollection pointCollection) {
		boolean foundNW = false;
		SimpleFeatureIterator iterator = lineCollection.features();
		while( iterator.hasNext() )
		{				
			SimpleFeature feature = iterator.next();
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			if(polygon.intersects(geom)){
				if(feature.getAttribute("networkMode").equals(true)){
					foundNW = true;
					String id1 = feature.getAttribute("point1").toString();
					String id2 = feature.getAttribute("point2").toString();
					SimpleFeatureIterator iteratorPoint =pointCollection.features(); 
					while(iteratorPoint.hasNext()){
						SimpleFeature point = iteratorPoint.next();
						if(point.getID().equals(id1))
							toolbar.setFeaturePoint1(point);
							
						if(point.getID().equals(id2))
							toolbar.setFeaturePoint2(point);
					}
					toolbar.setFeature(feature);
					fillDynamicAttributes(2L);
					break;
				}else{
					foundNW = false;
					break;
				}
			}
		}		
		return foundNW;
	}

	public SimpleFeature intersectPointFeature(Geometry polygon, SimpleFeatureCollection featureCollection) {
		// TODO Auto-generated method stub
		SimpleFeature found = null;
		SimpleFeatureIterator iterator = null; 
		try {
			iterator = featureCollection.features();
			while( iterator.hasNext() )
			{				
				SimpleFeature feature = iterator.next();
				Point point=((Point) feature.getAttribute(0));				
				if(polygon.intersects(point)){
					feature.setAttribute("selected", true);
					toolbar.setFeature(feature);
					fillDynamicAttributes(4L);
					found = feature; 			
				}else{	     			
					feature.setAttribute("selected", false);
				}
			}
		}catch(Exception e){}
		return found;
	}

	public void setSelected(SimpleFeatureCollection collection, boolean state)
	{
		SimpleFeatureIterator iterator = null;
		try {
			iterator = collection.features();
			while( iterator.hasNext() ){				
				SimpleFeature feature = iterator.next();
				feature.setAttribute("selected", state);
			}
		}catch(Exception e){}	
	}

	public Geometry getGeomIntersecting(){
		return geomintersection;
	}

	public SimpleFeature getFeature(){
		return featureIntersected;
	}

	public SimpleFeature getFeature(String id, SimpleFeatureCollection featureCollection) {
		SimpleFeature feature = null;
		SimpleFeatureIterator iterator = null;    	      	
		try {
			iterator = featureCollection.features();
			while( iterator.hasNext() ){				
				SimpleFeature featureInterator = iterator.next();
				if(featureInterator.getID().equals(id)){
					feature = featureInterator;	
				}   				
			}
		}catch(Exception e){}
		return feature;		
	}		

	public Polygon getSnapBox(double x, double y, double tolerance, EventDispatcher dispacher, EventTransmitter transmitter) throws Exception {
		Polygon snapBox = null;

		GetScreenCoordinates screenCoord = new GetScreenCoordinates(this, x, y);
		dispacher.dispatch(transmitter, screenCoord);

		double x0 = screenCoord.getX();
		double y0 = screenCoord.getY();

		double x1 = x0 - tolerance;
		double y1 = y0 - tolerance;	

		double x2 = x0 - tolerance;
		double y2 = y0 + tolerance;

		double x3 = x0 + tolerance;
		double y3 = y0 + tolerance;

		double x4 = x0 + tolerance;
		double y4 = y0 - tolerance;

		GetWorldCoordinates worldCoord1 = new GetWorldCoordinates(this, x1, y1);
		GetWorldCoordinates worldCoord2 = new GetWorldCoordinates(this, x2, y2);
		GetWorldCoordinates worldCoord3 = new GetWorldCoordinates(this, x3, y3);
		GetWorldCoordinates worldCoord4 = new GetWorldCoordinates(this, x4, y4);
		dispacher.dispatch(transmitter, worldCoord1);
		dispacher.dispatch(transmitter, worldCoord2);
		dispacher.dispatch(transmitter, worldCoord3);
		dispacher.dispatch(transmitter, worldCoord4);

		Coordinate[] coords  = new Coordinate[] {
				new Coordinate(worldCoord1.getX(), worldCoord1.getY()),
				new Coordinate(worldCoord2.getX(), worldCoord2.getY()),
				new Coordinate(worldCoord3.getX(), worldCoord3.getY()),
				new Coordinate(worldCoord4.getX(), worldCoord4.getY()),
				new Coordinate(worldCoord1.getX(), worldCoord1.getY())
		};

		snapBox = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing( coords ), null);

		return snapBox;
	}	
	
	public Polygon getSnapBox(double x, double y, double tolerance) {
		Polygon snapBox = null;

		double x0 = x;
		double y0 = y;

		double x1 = x0 - tolerance;
		double y1 = y0 - tolerance;	

		double x2 = x0 - tolerance;
		double y2 = y0 + tolerance;

		double x3 = x0 + tolerance;
		double y3 = y0 + tolerance;

		double x4 = x0 + tolerance;
		double y4 = y0 - tolerance;
		
		Coordinate[] coords  = new Coordinate[] {
				new Coordinate(x1, y1),
				new Coordinate(x2, y2),
				new Coordinate(x3, y3),
				new Coordinate(x4, y4),
				new Coordinate(x1, y1)
		};

		snapBox = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing( coords ), null);

		return snapBox;
	}	

	public boolean checkForSnap(double x, double y, double tolerance, SimpleFeatureCollection featCollection, EventDispatcher dispacher, EventTransmitter transmitter, boolean... movePolyAction) throws Exception {
		boolean found = false;

		Polygon snapBox = getSnapBox(x, y, tolerance, dispacher, transmitter);	

		SimpleFeatureIterator iterator = null;
		try {
			iterator = featCollection.features();
			while( iterator.hasNext() ){				
				SimpleFeature feature = iterator.next();				
				Geometry geom = ((Geometry) feature.getAttribute(0));		

				if(geom instanceof Polygon){
					if(movePolyAction.length>0){
						if(movePolyAction[0] == false){
							Polygon poly = (Polygon) geom;
							geom = poly.getExteriorRing();
						}
					}
				}

				if(snapBox.intersects(geom)){  		
					found = true;
					if(!(geom instanceof LineString)){
						feature.setAttribute("selected", true);
					}
					geomintersection = snapBox.intersection(geom);
					featureIntersected = feature;
					break;
				}else{
					feature.setAttribute("selected", false);
					geomintersection = null;
					found = false;
				}
			}
		}catch(Exception e){}	
		return found;
	}

	public boolean checkForSnap(double x, double y, double tolerance, Geometry geom, EventDispatcher dispacher, EventTransmitter transmitter) throws Exception  {
		boolean found = false;

		Polygon snapBox = getSnapBox(x, y, tolerance, dispacher, transmitter);

		if(snapBox.intersects(geom)){
			found = true;
		}

		return found;
	}

	public int checkForSnap(double x, double y, double tolerance, GeometryCollection geomCollection, EventDispatcher dispacher, EventTransmitter transmitter) throws Exception {
		int position = -1;

		Polygon snapBox = getSnapBox(x, y, tolerance, dispacher, transmitter);

		int cont = geomCollection.getNumGeometries();
		for (int i = 0; i < cont; i++){
			if(snapBox.intersects(geomCollection.getGeometryN(i))){
				position = i;
				break;
			}
		}

		return position;
	}

	public void checkForSnap(double x, double y, EventDispatcher dispacher,	EventTransmitter transmitter, double tolerance) throws Exception {
		GetFeatureEvent getLine = new GetFeatureEvent(this, "LINE");
		GetFeatureEvent getPoint = new GetFeatureEvent(this, "POINT");
		GetFeatureEvent getPolygon = new GetFeatureEvent(this, "POLYGON");
		dispacher.dispatch(transmitter, getPoint);
		dispacher.dispatch(transmitter, getLine);
		dispacher.dispatch(transmitter, getPolygon);
		SimpleFeatureCollection lineCollection = getLine.getFeatureCollection();
		SimpleFeatureCollection pointCollection = getPoint.getFeatureCollection();
		SimpleFeatureCollection polygonCollection = getPolygon.getFeatureCollection();

		intersectedPolygonGeom = checkForSnap(x, y, tolerance, polygonCollection, dispacher, transmitter, true);
		if(!intersectedPolygonGeom){
			intersectedPointGeom = checkForSnap(x, y, tolerance, pointCollection, dispacher, transmitter);			
			if(!intersectedPointGeom){
				intersectedLineGeom = checkForSnap(x, y, tolerance, lineCollection, dispacher, transmitter);
				if(intersectedLineGeom){
					snapPoint = new PointCreatorService().createPoint(((Geometry)geomintersection).getCentroid().getX(), ((Geometry)geomintersection).getCentroid().getY());
					dispacher.dispatch(transmitter,  new FeatureCreatedEvent(this, snapPoint , "SNAPPOINT", false));
				}
				new IntersectGeometry().setSelected(pointCollection, false);
			}
			new IntersectGeometry().setSelected(polygonCollection, false);
		}		
	}		

	public boolean getIntersectedPolygonGeom(){
		return intersectedPolygonGeom;
	}

	public boolean getIntersectedPointGeom(){
		return intersectedPointGeom;
	}

	public boolean getIntersectedLineGeom(){
		return intersectedLineGeom;
	}

	public SimpleFeature getSnapPoint(){
		return snapPoint;
	}
	
	private void fillDynamicAttributes(Long rep){
		CustomNode theme = AppSingleton.getInstance().getMediator().getCurrentTheme();
		if(theme!=null){
			Layer layer = theme.getLayer();
			if(layer!=null){
				// TODO Nova janela de atributos
				/*Table table = new TablesBO().getTableFromLayerID(layer.getId());
				if(table!=null){
					toolbar.getFramePropertie().popCmbLayer(rep);
					toolbar.getFramePropertie().setLayer(table.getLayerName());
					toolbar.getFramePropertie().cleanTable();
				}*/
			}
		}
	}

	private boolean intersectedPolygonGeom = false, intersectedPointGeom = false, intersectedLineGeom = false;
	private SimpleFeature snapPoint;
}
