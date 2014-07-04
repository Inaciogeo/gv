package com.nexusbr.gv.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import br.org.funcate.glue.model.Representation;

import com.nexusbr.gv.services.ChangesCommitService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;

public class TestingCommitFeature {

	public static void main(String[] args) throws SchemaException,
			ParseException {
		// TesteEnvelope();
		// new TestingCommitFeature().commitPoint();
		new TestingCommitFeature().commitLine();
		// new TestingCommitFeature().commitPolygon();
		/*
		 * Collection<String> listOne = Arrays.asList("1","2", "3","4",
		 * "5","6"); Collection<String> listTwo = Arrays.asList("1", "2", "700",
		 * "800", "900","100","110"); Collection<String> similar = new
		 * HashSet<String>( listOne ); Collection<String> different = new
		 * HashSet<String>(); different.addAll( listOne ); //different.addAll(
		 * listTwo ); similar.retainAll( listTwo ); different.removeAll( similar
		 * ); System.out.printf("One:%s%nTwo:%s%nSimilar:%s%nDifferent:%s%n",
		 * listOne, listTwo, similar, different);
		 */

		/*
		 * String spatialData = "((100,100),(200,200),(300,300),(100,100))";
		 * spatialData = spatialData.replace(",", " "); spatialData =
		 * spatialData.replace(") (", "),("); spatialData =
		 * spatialData.replace(")", ""); spatialData = spatialData.replace("(",
		 * ""); String[] array = spatialData.split(","); ArrayList<String> coord
		 * = new ArrayList<String>(); for (int i = 0; i < array.length; i++) {
		 * coord.add(array[i].trim()); }
		 * 
		 * String WKT = "POLYGON (("; for (int i = 0; i < coord.size(); i++) {
		 * if(i==coord.size()-1){ WKT += coord.get(i)+"))"; }else{ WKT +=
		 * coord.get(i)+", "; } } Polygon polygon = (Polygon) new
		 * WKTReader().read(WKT); System.out.println(polygon);
		 */

	}

	public static void TesteEnvelope() {
		Geometry geom = new GeometryFactory()
				.createPoint(new Coordinate(15, 70));
		// Geometry geom = new GeometryFactory().createLineString(new
		// Coordinate[]{new Coordinate(15, 70), new Coordinate(16, 71)});
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(geom.getEnvelopeInternal().getMinX());
		list.add(geom.getEnvelopeInternal().getMinY());
		list.add(geom.getEnvelopeInternal().getMaxX());
		list.add(geom.getEnvelopeInternal().getMaxY());

		System.out.println(geom.getEnvelope());
		System.out.println(geom.getBoundary());
		// System.out.println(geom.getEnvelopeInternal().);

		// String wkt = new WKTWriter().write();
		// System.out.println(wkt);
	}

	public void commitPoint() throws SchemaException {

		SimpleFeatureCollection collecionPoint = FeatureCollections
				.newCollection();

		SimpleFeatureType TYPEpoint = DataUtilities
				.createType(
						"Point",
						"geom:Point,layerName:String,staticAttributes:java.util.HashMap,dynamicAttributes:java.util.HashMap");

		HashMap<String, String> staticAttr = new HashMap<String, String>();
		staticAttr.put("selected", "true");
		staticAttr.put("networkMode", "false");
		HashMap<String, String> dynamicAttr = new HashMap<String, String>();
		dynamicAttr.put("Nome", "Bruno");
		dynamicAttr.put("Idade", "23");
		dynamicAttr.put("Sexo", "Masculino");

		SimpleFeature feature = (SimpleFeatureBuilder.build(
				TYPEpoint,
				new Object[] {
						new GeometryFactory()
								.createPoint(new Coordinate(10, 10)), "Pessoa",
						staticAttr, dynamicAttr }, null));
		collecionPoint.add(feature);

		HashMap<String, String> staticAttr2 = new HashMap<String, String>();
		staticAttr2.put("selected", "false");
		staticAttr.put("networkMode", "false");
		HashMap<String, String> dynamicAttr2 = new HashMap<String, String>();
		dynamicAttr2.put("Nome", "JOÃO");
		dynamicAttr2.put("Idade", "20");
		dynamicAttr2.put("Sexo", "Masculino");

		SimpleFeature feature2 = (SimpleFeatureBuilder.build(
				TYPEpoint,
				new Object[] {
						new GeometryFactory()
								.createPoint(new Coordinate(40, 40)), "Pessoa",
						staticAttr2, dynamicAttr2 }, null));
		collecionPoint.add(feature2);

		new ChangesCommitService().saveFeatures(collecionPoint, Representation.POINT);
	}

	public void commitLine() throws SchemaException {
		SimpleFeatureCollection collecionLine = FeatureCollections
				.newCollection();
		SimpleFeatureType TYPELine = DataUtilities.createType("LineString",
				"geom:LineString,layerName:String,fonte:String,nome:String,situaa_a3:String,extensam:Double");
		
		SimpleFeature feature = (SimpleFeatureBuilder.build(
				TYPELine,
				new Object[] {
						new GeometryFactory()
								.createLineString(new Coordinate[] {
										new Coordinate(10, 10),
										new Coordinate(20, 20) }), "Rua",
						"IMAGEM SPOT/DER", "ACESSO", "Não Pavimentada", 8255.15 }, null));
		collecionLine.add(feature);

		new ChangesCommitService().saveFeatures(collecionLine, Representation.LINE);
	}

	public void commitPolygon() throws SchemaException {
		SimpleFeatureCollection collecionPolygon = FeatureCollections
				.newCollection();
		SimpleFeatureType TYPELine = DataUtilities
				.createType(
						"Polygon",
						"geom:Polygon,layerName:String,staticAttributes:java.util.HashMap,dynamicAttributes:java.util.HashMap");

		// poligono 1
		HashMap<String, String> staticAttr = new HashMap<String, String>();
		staticAttr.put("selected", "true");
		HashMap<String, String> dynamicAttr = new HashMap<String, String>();
		dynamicAttr.put("Nome", "Quadra A");

		SimpleFeature feature = (SimpleFeatureBuilder.build(
				TYPELine,
				new Object[] {
						new GeometryFactory().createPolygon(
								new GeometryFactory()
										.createLinearRing((new Coordinate[] {
												new Coordinate(10, 10),
												new Coordinate(30, 30),
												new Coordinate(30, 10),
												new Coordinate(10, 10) })),
								null), "Lote", staticAttr, dynamicAttr }, null));
		collecionPolygon.add(feature);

		// poligono 2
		HashMap<String, String> staticAttr2 = new HashMap<String, String>();
		staticAttr2.put("selected", "false");
		HashMap<String, String> dynamicAttr2 = new HashMap<String, String>();
		dynamicAttr2.put("Nome", "Quadra A");

		SimpleFeature feature2 = (SimpleFeatureBuilder.build(
				TYPELine,
				new Object[] {
						new GeometryFactory().createPolygon(
								new GeometryFactory()
										.createLinearRing((new Coordinate[] {
												new Coordinate(70, 20),
												new Coordinate(80, 50),
												new Coordinate(90, 10),
												new Coordinate(70, 20) })),
								null), "Lote", staticAttr2, dynamicAttr2 },
				null));
		collecionPolygon.add(feature2);

		new ChangesCommitService().saveFeatures(collecionPolygon, Representation.POLYGON);
	}
}
