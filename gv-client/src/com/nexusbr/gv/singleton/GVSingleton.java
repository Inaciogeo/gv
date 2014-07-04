package com.nexusbr.gv.singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.opengis.feature.simple.SimpleFeature;

import com.nexusbr.gv.view.components.ToolbarWindow;

/**
 * this Class is a Singleton for geo vector, tis saves the language that this
 * application is going to use, se toolbar controller, layerManager and list of
 * edited an deleted feature
 * 
 * @author Severino, Bruno de Oliveira
 * @version 1.0
 * 
 */
public class GVSingleton {

	private static GVSingleton instance;
	
	private ResourceBundle language;
	private ToolbarWindow toolbar;
	
	private ArrayList<SimpleFeature> pointsEdited = new ArrayList<SimpleFeature>();
	private ArrayList<SimpleFeature> pointsDeleted = new ArrayList<SimpleFeature>();
	private Set<String> pointIDs = new HashSet<String>();
	
	private ArrayList<SimpleFeature> linesEdited = new ArrayList<SimpleFeature>();
	private ArrayList<SimpleFeature> linesDeleted = new ArrayList<SimpleFeature>();
	private Set<String> lineIDs = new HashSet<String>();
	
	private ArrayList<SimpleFeature> polygonEdited = new ArrayList<SimpleFeature>();
	private ArrayList<SimpleFeature> polygonDeleted = new ArrayList<SimpleFeature>();
	private Set<String> polygonIDs = new HashSet<String>();
	
	/**
	 * Gets Instance.
	 * @return instance
	 */
	public static GVSingleton getInstance(){
		if (instance == null){
			instance = new GVSingleton();
		}
		
		return instance;
	}
	
	/**
	 * set Application Language
	 * @param language a ResourceBundle
	 */
	public void setLanguage(ResourceBundle language) {
		this.language = language;
	}

	/**
	 * get Application language
	 * @return language
	 */
	public ResourceBundle getLanguage() {
		return language;
	}

	/**
	 * Set toolbar that this application is using
	 * @param toolbarPanel
	 */
	public void setToolbar(ToolbarWindow toolbarPanel) {
		this.toolbar = toolbarPanel;
	}

	/**
	 * gets toolbar
	 * @return toolbar
	 */
	public ToolbarWindow getToolbar() {
		return toolbar;
	}

	/**
	 * get point ids
	 * @return pointIDs
	 */
	public Set<String> getPointIDs() {
		return pointIDs;
	}

	/**
	 * set PointIds
	 * @param pointIDs
	 */
	public void setPointIDs(Set<String> pointIDs) {
		this.pointIDs = pointIDs;
	}

	/**
	 * get PointsEdited
	 * @return pointEdited
	 */
	public ArrayList<SimpleFeature> getPointsEdited() {
		return pointsEdited;
	}

	/**
	 * set PointEdited
	 * @param pointsEdited
	 */
	public void setPointsEdited(ArrayList<SimpleFeature> pointsEdited) {
		this.pointsEdited = pointsEdited;
	}

	/**
	 * getPointsDeleted
	 * @return pointDeleted
	 */
	public ArrayList<SimpleFeature> getPointsDeleted() {
		return pointsDeleted;
	}

	/**
	 * set Points Deleted
	 * @param pointsDeleted
	 */
	public void setPointsDeleted(ArrayList<SimpleFeature> pointsDeleted) {
		this.pointsDeleted = pointsDeleted;
	}

	/**
	 * get Lines Edited
	 * @return linesEdited
	 */
	public ArrayList<SimpleFeature> getLinesEdited() {
		return linesEdited;
	}

	/**
	 * set Lines Edited
	 * @param linesEdited
	 */
	public void setLinesEdited(ArrayList<SimpleFeature> linesEdited) {
		this.linesEdited = linesEdited;
	}

	/**
	 * get Line Deleted
	 * @return linesDeleted
	 */
	public ArrayList<SimpleFeature> getLinesDeleted() {
		return linesDeleted;
	}

	/**
	 * set Lines Deleted
	 * @param linesDeleted
	 */
	public void setLinesDeleted(ArrayList<SimpleFeature> linesDeleted) {
		this.linesDeleted = linesDeleted;
	}

	/**
	 * get Lines Ids
	 * @return lineIDs
	 */
	public Set<String> getLineIDs() {
		return lineIDs;
	}

	/**
	 * set LineIds
	 * @param lineIDs
	 */
	public void setLineIDs(Set<String> lineIDs) {
		this.lineIDs = lineIDs;
	}

	/**
	 * get Polygons Edited
	 * @return polygonEdited
	 */
	public ArrayList<SimpleFeature> getPolygonEdited() {
		return polygonEdited;
	}

	/**
	 * set Polygons Edited
	 * @param polygonEdited
	 */
	public void setPolygonEdited(ArrayList<SimpleFeature> polygonEdited) {
		this.polygonEdited = polygonEdited;
	}

	/**
	 * get Polygons Deleted
	 * @return polygonDeleted
	 */
	public ArrayList<SimpleFeature> getPolygonDeleted() {
		return polygonDeleted;
	}

	/**
	 * set Polygons Deleted
	 * @param polygonDeleted
	 */
	public void setPolygonDeleted(ArrayList<SimpleFeature> polygonDeleted) {
		this.polygonDeleted = polygonDeleted;
	}

	/**
	 * set Polygons IDs
	 * @return polygonIDs
	 */
	public Set<String> getPolygonIDs() {
		return polygonIDs;
	}

	/**
	 * set Polygon Ids
	 * @param polygonIDs
	 */
	public void setPolygonIDs(Set<String> polygonIDs) {
		this.polygonIDs = polygonIDs;
	}
}
