package com.nexusbr.gv.services;

import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class PolygonCreatorService {

	private SimpleFeatureType TYPEpolygon;
	
	public PolygonCreatorService(){
		try {
			TYPEpolygon = DataUtilities.createType("Polygon","geom:Polygon,selected:Boolean");
		} catch (SchemaException e) {}
	}
	
	public void setType(SimpleFeatureType newType){
		TYPEpolygon = newType;
	}
	
	public SimpleFeatureType getType(){
		return TYPEpolygon;
	}
	
	public SimpleFeature createPolygon(Geometry geom, boolean selected, String ID){
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpolygon, 
						new Object[]{ 
								geom, //GEOMETRY 
								selected 	//is SELECTED
						},
						ID	// ID AUTOMATICO
						));

		return feature;
	}
	
	public SimpleFeature createPolygon(LinearRing ring, SimpleFeature otherPoint) {
		
		Boolean selected = (Boolean) otherPoint
				.getAttribute("selected");
		if (selected == null) {
			selected = false;
		}
		
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpolygon, 
						new Object[]{ 
								new GeometryFactory().createPolygon(ring, null), // GEOM
								selected
						},
						otherPoint.getID()
						));

		return feature;
	}
	
	public SimpleFeature createPolygon(LinearRing ring){		
		SimpleFeature feature =
			 SimpleFeatureBuilder.build( TYPEpolygon, 
				new Object[]{ 
					new GeometryFactory().createPolygon(ring, null), // GEOM 
					false 		//SELECTED
				},
				null
			);
		
		return feature;
	}
	
	public SimpleFeature createPolygon(Geometry polygon){		
		SimpleFeature feature =
			 SimpleFeatureBuilder.build( TYPEpolygon, 
				new Object[]{ 
					polygon,
					false
				},
				null
			);
		
		return feature;
	}
	
	public SimpleFeature createPolygon(List<Object> list, String ID){		
		SimpleFeature feature =
			 SimpleFeatureBuilder.build( TYPEpolygon, 
				list,
				ID
		 );

		return feature;
	}

	public Geometry createCircle(double x, double y, double RADIUS) {
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
		shapeFactory.setNumPoints(100);
		shapeFactory.setCentre(new Coordinate(x, y));
		shapeFactory.setSize(RADIUS * 2);
		return shapeFactory.createCircle();
	} 
	

	public SimpleFeature createPolygon(SimpleFeature other) {
		Boolean selected = (Boolean) other.getAttribute("selected");
		if(selected == null){
			selected = false;
		}
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpolygon, 
						new Object[]{ 
								(Geometry)other.getDefaultGeometry(),
								false
						},
						other.getID()
						));

		return feature;
	}
}
