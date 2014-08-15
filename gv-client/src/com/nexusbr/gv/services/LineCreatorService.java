package com.nexusbr.gv.services;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.nexusbr.gv.model.DTO.PointReference;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class LineCreatorService {

	private SimpleFeatureType TYPEline;

	public LineCreatorService() {
		
		try {
			TYPEline = DataUtilities.createType("Line", "geom:LineString,"
					+ "selected:Boolean," + "network:Boolean,"
					+ "point1:String," + "point2:String," + "layerName:String,"
					+ "point1Ref:java.util.ArrayList,"
					+ "point2Ref:java.util.ArrayList");
		} catch (SchemaException e) {
		}
		
	}

	public SimpleFeature createFeatureLine(Coordinate[] coord) {
		
		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { new GeometryFactory().createLineString(coord), // GEOMETRY
						false, // SELECT MODE
						false, // NETWORK MODE
						"null", // POINT 1 ID
						"null", // POINT 2 ID
						new ArrayList<PointReference>(), // Point 1 References
						new ArrayList<PointReference>() // Point 2 References
				}, null));
		
		return feature;
	}

	public SimpleFeature createLine(Coordinate[] coord,
			SimpleFeature otherFeature) {
		
		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { new GeometryFactory().createLineString(coord), // GEOMETRY
						otherFeature.getAttribute("selected"), // SELECTED
						otherFeature.getAttribute("network"), // NETWORK
																	// MODE
						otherFeature.getAttribute("point1"), // POINT 1 ID
						otherFeature.getAttribute("point2"), // POINT 2 ID
						otherFeature.getAttribute("point1Ref"), // Point 1
																// References
						otherFeature.getAttribute("point2Ref"), // Point 2
																// References
				}, otherFeature.getID() // FEATURE ID
				));
		return feature;
	}

	public SimpleFeature createLine(SimpleFeature otherFeature) {
		
		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { (Geometry) otherFeature.getDefaultGeometry(), // GEOMETRY
						otherFeature.getAttribute("selected"), // SELECTED
						otherFeature.getAttribute("network"), // NETWORK
																	// MODE
						otherFeature.getAttribute("point1"), // POINT 1 ID
						otherFeature.getAttribute("point2"), // POINT 2 ID
						otherFeature.getAttribute("point1Ref"), // Point 1
																// References
						otherFeature.getAttribute("point2Ref"), // Point 2
																// References
				}, otherFeature.getID() // FEATURE ID
				));
		return feature;
	}

	public SimpleFeature createLine(Geometry geom, boolean selected,
			boolean network, String point1, String point2, String ID,
			ArrayList<PointReference> p1Ref, ArrayList<PointReference> p2Ref ) {
		
		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { geom, // GEOMETRY
						selected, network, // is NETWORK
						point1, // is POINT 1
						point2, // is POINT 2
						p1Ref, // Point 1 References
						p2Ref, // Point 2 References
						
				}, ID // ID AUTOMATICO
				));
		
		return feature;
	}

	public SimpleFeature createLine(List<Object> list,
			SimpleFeature otherFeature) {
		SimpleFeature feature = SimpleFeatureBuilder.build(TYPEline, list,
				otherFeature.getID());
		return feature;
	}

	public SimpleFeature createLine(List<Object> list) {
		SimpleFeature feature = SimpleFeatureBuilder
				.build(TYPEline, list, null);
		return feature;
	}

	public SimpleFeature cloneSimpleFeature(SimpleFeature feature) {
		// COPY ATTRIBUTES LIST FROM THE GEOM INTERSECTED
		Object[] list = feature.getAttributes().toArray().clone();

		// CONVERT OBJECT ARRAY TO ARRAYLIST
		List<Object> array = new ArrayList<Object>();
		for (Object obj : list) {
			array.add(obj);
		}

		SimpleFeature newFeature = createLine(array, feature);

		return newFeature;
	}

	public Geometry createLine(Coordinate[] coord) {
		return new GeometryFactory().createLineString(coord);
	}

	public LineString createLine(String wktReader) {
		WKTReader reader = new WKTReader(new GeometryFactory());
		LineString line = null;
		try {
			line = (LineString) reader.read(wktReader);
		} catch (ParseException e) {
		}
		return line;
	}

	public SimpleFeature createReferenceCircleLine(Geometry geom, String radius) {
		try {
			TYPEline = DataUtilities.createType("ReferenceCircleLine",
					"geometry:Geometry,radius:String");
		} catch (SchemaException e) {
		}

		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { geom, radius }, null));

		return feature;
	}

	public void setType(SimpleFeatureType newType) {
		TYPEline = newType;
	}

	public SimpleFeatureType getType() {
		return TYPEline;
	}

	public SimpleFeature createLine(Geometry defaultGeometry,
			SimpleFeature otherFeature) {
		SimpleFeature feature = (SimpleFeatureBuilder.build(TYPEline,
				new Object[] { defaultGeometry, // GEOMETRY
						otherFeature.getAttribute("selected"), // SELECTED
						otherFeature.getAttribute("network"), // NETWORK
																	// MODE
						otherFeature.getAttribute("point1"), // POINT 1 ID
						otherFeature.getAttribute("point2"), // POINT 2 ID
						otherFeature.getAttribute("point1Ref"), // Point 1
																// References
						otherFeature.getAttribute("point2Ref"), // Point 2
																// References
				}, otherFeature.getID() // FEATURE ID
				));
		return feature;

	}

}
