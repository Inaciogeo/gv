package com.nexusbr.gv.services;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class MoveGeometry {

	public MoveGeometry(){
		
	}	
	
	public SimpleFeature moveLine(Coordinate[] coord, SimpleFeature feat)
	{
		SimpleFeature feature = null;
		if(((Geometry) feat.getAttribute(0)).getCoordinates().length>2){
			Coordinate[] newcoord;
			if(coord.length>2){
				newcoord = coord;
			}else{
				Geometry geomFeat = (Geometry) ((Geometry) feat.getAttribute(0)).clone();
				newcoord = geomFeat.getCoordinates();
				newcoord[0].x = coord[0].x;
				newcoord[0].y = coord[0].y;
				newcoord[newcoord.length-1].x = coord[coord.length-1].x;
				newcoord[newcoord.length-1].y = coord[coord.length-1].y;			
			}
			feature = new LineCreatorService().createLine(newcoord,feat);
			feature.setAttribute("selected", true);
				
		}else{
			feature = new LineCreatorService().createLine(coord,feat);
			feature.setAttribute("selected", true);
		}
		return feature;
	}
}
