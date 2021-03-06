package com.nexusbr.gv.services;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

public class PointCreatorService {

	public SimpleFeatureType TYPEpoint;

	public PointCreatorService(){
		try {
			TYPEpoint = DataUtilities.createType("Point","geom:Point,selected:Boolean,network:Boolean");
		} catch (SchemaException e) {}
	}

	public Geometry createGeomPoint(Coordinate coord){
		return new GeometryFactory().createPoint(coord);	
	}
	
	public SimpleFeature createPointOS(double x, double y, boolean selected, boolean network,String osid,String ip ,String ocurrence,String status){

		try {
			TYPEpoint = DataUtilities.createType("Point","geom:Point,selected:Boolean,network:Boolean," +
						"osid:String,ocurrence:String,ip:String,status:String");
		} catch (SchemaException e) {}
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								new GeometryFactory().createPoint(new Coordinate(x, y)), //GEOMETRY 
								false, 	//is SELECTED
								false, 	//is NETWORK
								osid,
								ip,
								ocurrence,
								status
						},
						null	// ID AUTOMATICO
						));

		return feature;
	}	
	

	public SimpleFeature createPoint(double x, double y){
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								new GeometryFactory().createPoint(new Coordinate(x, y)), //GEOMETRY 
								false, 	//is SELECTED
								false, 	//is NETWORK
						},
						null	// ID AUTOMATICO
						));

		return feature;
	}	
	
	public SimpleFeature createPoint(Geometry geom, boolean selected, boolean network,String ID){
		//System.out.println("estamos em createPoint");
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								geom, //GEOMETRY 
								selected, 	//is SELECTED
								network, 	//is NETWORK
						},
						ID	// ID AUTOMATICO
						));

		return feature;
	}

	public SimpleFeature createPoint(double x, double y, SimpleFeature otherPoint) {
		
		Boolean selected = (Boolean) otherPoint.getAttribute("selected");
		if(selected == null){
			selected = false;
		}
		
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								new GeometryFactory().createPoint(new Coordinate(x, y)), //GEOMETRY 
								selected, //SELECTED
								otherPoint.getAttribute("network"), //NETWORK MODE
						},
						otherPoint.getID()
						));

		return feature;
	}

	public SimpleFeature createPoint(Coordinate coord, SimpleFeature otherPoint) {
		Boolean selected = (Boolean) otherPoint.getAttribute("selected");
		if(selected == null){
			selected = false;
		}
		
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								new GeometryFactory().createPoint(coord), //GEOMETRY 
								selected, //SELECTED
								otherPoint.getAttribute("network"), //NETWORK MODE
						},
						otherPoint.getID()
						));

		return feature;
	}	

	public SimpleFeature createPoint(Geometry geom, SimpleFeature otherPoint){
		Boolean selected = (Boolean) otherPoint.getAttribute("selected");
		if(selected == null){
			selected = false;
		}
		
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								geom, 
								selected, //SELECTED
								otherPoint.getAttribute("network"), //NETWORK MODE
						},
						otherPoint.getID()
						));

		return feature;
	}

	public SimpleFeature createPoint(SimpleFeature otherPoint) {
		Boolean selected = (Boolean) otherPoint.getAttribute("selected");
		if(selected == null){
			selected = false;
		}
		SimpleFeature feature =
				(SimpleFeatureBuilder.build(
						TYPEpoint, 
						new Object[]{ 
								(Geometry)otherPoint.getDefaultGeometry(),
								selected, //SELECTED
								otherPoint.getAttribute("network"), //NETWORK MODE
						},
						otherPoint.getID()
						));

		return feature;
	}

	public SimpleFeature createEdgesPoint(Geometry[] geom){
		try {
			TYPEpoint = DataUtilities.createType("ResizePoint","geometries:GeometryCollection");
		} catch (SchemaException e) {}

		GeometryCollection collectionGeom = new GeometryFactory().createGeometryCollection(geom);

		SimpleFeature feature = (SimpleFeatureBuilder.build(
				TYPEpoint, 
				new Object[]{ 
						collectionGeom
				},
				null
				));

		return feature;
	}

	public SimpleFeature createReferenceCircle(Geometry geom, String radius){
		try {
			TYPEpoint = DataUtilities.createType("ReferenceCircle","geometry:Geometry,radius:String");
		} catch (SchemaException e) {}

		SimpleFeature feature = (SimpleFeatureBuilder.build(
			TYPEpoint, 
			new Object[]{ 
				geom,
				radius
			},
			null
		));

		return feature;
	}

	public void setType(SimpleFeatureType newType){
		TYPEpoint = newType;
	}

	public SimpleFeatureType getType(){
		return TYPEpoint;
	}
}
