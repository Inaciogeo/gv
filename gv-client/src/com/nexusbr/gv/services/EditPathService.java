package com.nexusbr.gv.services;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class EditPathService {
	public EditPathService(){
		
	}

	public Geometry createEnvelopeBox(SimpleFeature feature) throws Exception{
		Geometry envelopeBox = null;		
		
		//CREATE ENVELOPE		
		Envelope env = ((Geometry)feature.getDefaultGeometry()).getEnvelopeInternal();				
		
		//CREATE GEOMETRY   	
    	Coordinate[] coords  = new Coordinate[] {
				new Coordinate(env.getMinX(), env.getMinY()), 
				new Coordinate(env.getMinX(), env.getMaxY()),
				new Coordinate(env.getMaxX(), env.getMaxY()),
				new Coordinate(env.getMaxX(), env.getMinY()),  
				new Coordinate(env.getMinX(), env.getMinY())
		};
 		envelopeBox = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(coords), null); 		
		return envelopeBox;
	}
	
	public SimpleFeature createResizeEdges(Geometry envelopeBox) throws Exception{		
		Coordinate coord;		
		Geometry[] geom = new Geometry[8];
		double x1,x2,y1,y2;
		SimpleFeature editpath_resize = null;
		
		//1 Resize EDGE
		coord = envelopeBox.getCoordinates()[0];
		geom[0] = new GeometryFactory().createPoint(coord);		
		
		//2 Resize EDGE
		x1 = envelopeBox.getCoordinates()[0].x;
		x2 = envelopeBox.getCoordinates()[1].x;
		y1 = envelopeBox.getCoordinates()[0].y;
		y2 = envelopeBox.getCoordinates()[1].y;
		coord = new Coordinate(x1, (y2+y1)/2);		
		geom[1] = new GeometryFactory().createPoint(coord);
		
		//3 Resize EDGE
		coord = envelopeBox.getCoordinates()[1];
		geom[2] = new GeometryFactory().createPoint(coord);
				
		//4 Resize EDGE
		x1 = envelopeBox.getCoordinates()[1].x;
		x2 = envelopeBox.getCoordinates()[2].x;
		y1 = envelopeBox.getCoordinates()[1].y;
		y2 = envelopeBox.getCoordinates()[2].y;
		coord = new Coordinate((x2+x1)/2, y2);		
		geom[3] = new GeometryFactory().createPoint(coord);
		
		//5 Resize EDGE
		coord = envelopeBox.getCoordinates()[2];
		geom[4] = new GeometryFactory().createPoint(coord);
				
		//6 Resize EDGE
		x1 = envelopeBox.getCoordinates()[3].x;
		x2 = envelopeBox.getCoordinates()[2].x;
		y1 = envelopeBox.getCoordinates()[3].y;
		y2 = envelopeBox.getCoordinates()[2].y;
		coord = new Coordinate(x2, (y2+y1)/2);		
		geom[5] = new GeometryFactory().createPoint(coord);
		
		//7 Resize EDGE
		coord = envelopeBox.getCoordinates()[3];
		geom[6] = new GeometryFactory().createPoint(coord);
		
		//8 Resize EDGE
		x1 = envelopeBox.getCoordinates()[0].x;
		x2 = envelopeBox.getCoordinates()[3].x;
		y1 = envelopeBox.getCoordinates()[0].y;
		y2 = envelopeBox.getCoordinates()[3].y;
		coord = new Coordinate((x2+x1)/2, y1);
		geom[7] = new GeometryFactory().createPoint(coord);	
		
		editpath_resize = (new PointCreatorService().createEdgesPoint(geom));
		return editpath_resize;
	}
	
	public SimpleFeature createPathEdges(SimpleFeature feature) throws Exception{
		SimpleFeature editpath_edges = null;
		
		List<Geometry> geom = new ArrayList<Geometry>();
		if(feature.getDefaultGeometry() instanceof LineString){
			Coordinate[] coord = ((Geometry)feature.getDefaultGeometry()).getCoordinates();
			if(coord.length>2){
				for(int i=0;i<coord.length;i++){
					geom.add(new GeometryFactory().createPoint(coord[i]));
				}
			}
			Geometry[] geometries = new Geometry[geom.size()];
			for(int i=0;i<geom.size();i++){
				geometries[i] = geom.get(i);
			}
			
			editpath_edges = (new PointCreatorService().createEdgesPoint(geometries));			
		}		
		return editpath_edges;
	}
}
