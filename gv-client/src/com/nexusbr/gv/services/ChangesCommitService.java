package com.nexusbr.gv.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;

import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Box;
import br.org.funcate.glue.model.Layer;
import br.org.funcate.glue.model.Representation;
import br.org.funcate.glue.model.Theme;
import br.org.funcate.glue.model.canvas.CanvasState;
import br.org.funcate.glue.model.exception.GlueServerException;
import br.org.funcate.glue.model.tree.CustomNode;
import br.org.funcate.glue.view.GlueMessageDialog;
import br.org.funcate.glue.view.NodeType;
import br.org.funcate.jtdk.model.FeatureManipulation;
import br.org.funcate.jtdk.model.FeatureManipulationService;
import br.org.funcate.jtdk.model.FeatureTransformer;
import br.org.funcate.jtdk.model.dto.FeatureDTO;

import com.vividsolutions.jts.geom.Point;

public class ChangesCommitService{
	
	FeatureManipulation featureManipulation = new FeatureManipulationService();
	FeatureTransformer featureTransformer = new FeatureTransformer();
	private boolean isNetwork;
	private ArrayList<String> idspoint = new ArrayList<String>();
	private ArrayList<String> idsline = new ArrayList<String>();
	
	public boolean saveFeatures(SimpleFeatureCollection collection, int representation){
		CustomNode themeNode = AppSingleton.getInstance().getTreeState().getCurrentTheme();
		
		CustomNode pointsNode = null;
		CustomNode linesNode = null;
		
		if(themeNode==null){
			//JOptionPane.showMessageDialog(null, "Nenhum tema selecionado!");
			GlueMessageDialog.show("Nenhum tema selecionado!", null, 3);
			return false;
		}
		Layer layer = null;
		Theme theme = null;
		
		if(themeNode.getNodeType() == NodeType.NETWORK_THEME){
			
			pointsNode = (CustomNode) themeNode.getChildAt(0);
			linesNode = (CustomNode)themeNode.getChildAt(1);
			if(representation == Representation.POINT){
				themeNode = pointsNode;
			}
			else if(representation == Representation.LINE){
				pointsNode = (CustomNode) themeNode.getChildAt(0);
				themeNode = linesNode;
			}
		}else if(themeNode.getNodeType() == NodeType.DEFAULT){
			representation = Integer.parseInt(themeNode.getTheme().getReps().get(0).getName());
		}
		
		theme = themeNode.getTheme();
		layer = theme.getLayer();
		
		if(layer==null){
			//JOptionPane.showMessageDialog(null, "Não há layer associado ao tema selecionado!");
			GlueMessageDialog.show("Não há layer associado ao tema selecionado!", null, 3);
			return false;
		}
		List<Representation> reps = layer.getRepresentations();
		boolean sameRepresentation = false;
		for(Representation layerRepresentation : reps){
			if(layerRepresentation.getId() == representation)
				sameRepresentation = true;
		}
		
		if(!collection.isEmpty()&&!sameRepresentation){
			//JOptionPane.showMessageDialog(null, "O tema selecionado não possui este tipo de representação!");
			GlueMessageDialog.show("O tema selecionado não possui este tipo de representação!", null, 3);
			return false;
		}		
		List<FeatureDTO> featureList = new ArrayList<FeatureDTO>();
		SimpleFeatureIterator iterator = collection.features();
		int p=0;
		while(iterator.hasNext()){
			
			SimpleFeature feature = iterator.next();
			isNetwork = (Boolean) feature.getAttribute("networkMode");
	
			if (themeNode.getTheme().getId() != 5 || !isNetwork) {
				if (representation == 2 && idspoint.size()==p) {
					//LineString line = (LineString) feature.getDefaultGeometry();
//					Point point1 = line.getPointN(0);
//					Point point2 = line.getPointN(1);
					if (pointsNode != null) {
						//String id = getPointID(pointsNode, point1);
						feature.setAttribute("point1", idspoint.get(p+1));
						//feature.setAttribute("point2", id);
						//id = getPointID(pointsNode, point2);
						feature.setAttribute("point2", idspoint.get(p));
						//feature.setAttribute("point2", id);
						p++;
					} else {
						feature.setAttribute("point1", "");
						feature.setAttribute("point2", "");
					}
				}

				FeatureDTO featureDTO = new FeatureDTO(feature);
				featureList.add(featureDTO);
			}
		}

		if (featureList.isEmpty()) {
			return false;
		}
		
		Vector<String> vecIds = featureManipulation.saveFeatures(theme, featureList);
	
		if(vecIds.isEmpty() || vecIds.size() != featureList.size()){
			return false;
		}
		
		for (int i = 0; i < vecIds.size(); i++) {
			String id = vecIds.get(i);
			if (representation == 4){
				idspoint.add(id);
			}
				
			if (representation == 2){
				idsline.add(id);
			}
			FeatureDTO feature = featureList.get(i);
			FeatureId featureId = feature.getSimpleFeature().getIdentifier();
			if(featureId instanceof FeatureIdImpl){
				FeatureIdImpl imp = (FeatureIdImpl) featureId;
				imp.setID(id);
			}
		}
			
		EditionState.setEdited(false);
		
		if(!idsline.isEmpty() && !idspoint.isEmpty() && idsline.size()==featureList.size()){
			for (int i = 0; i < idsline.size(); i++) {
				setBDPointID(idsline.get(i), idspoint.get(i+1), idspoint.get(i));
			}
		}
		return true;
	}
	
	public boolean removeFeatures(List<String> toDelete, int representation){
		CustomNode themeNode = AppSingleton.getInstance().getTreeState().getCurrentTheme();
		CustomNode pointsNode = null;
		CustomNode linesNode = null;
		
		if(themeNode==null){
			//JOptionPane.showMessageDialog(null, "Nenhum tema selecionado!");
			GlueMessageDialog.show("Nenhum tema selecionado!", null, 3);
			return false;
		}
		
		Layer layer = null;
		Theme theme = null;
		if(themeNode.getNodeType() == NodeType.NETWORK_THEME){
			pointsNode = (CustomNode) themeNode.getChildAt(0);
			linesNode = (CustomNode)themeNode.getChildAt(1);
			if(representation == Representation.POINT){
				themeNode = pointsNode;
			}
			else if(representation == Representation.LINE){	
				themeNode = linesNode;
			}
			else{
				return false;
			}
		}
		
		theme = themeNode.getTheme();
		layer = theme.getLayer();
		
		if(layer==null){
			//JOptionPane.showMessageDialog(null, "Não há layer associado ao tema selecionado!");
			GlueMessageDialog.show("Não há layer associado ao tema selecionado!", null, 3);
			return false;
		}
		List<Representation> reps = layer.getRepresentations();
		boolean sameRepresentation = false;
		for(Representation layerRepresentation : reps){
			if(layerRepresentation.getId() == representation)
				sameRepresentation = true;
		}
		
		
		if(!toDelete.isEmpty()&&!sameRepresentation){
			//JOptionPane.showMessageDialog(null, "O tema selecionado não possui este tipo de representação!");
			GlueMessageDialog.show("O tema selecionado não possui este tipo de representação!", null, 3);
			return false;
		}
		EditionState.setEdited(false);
		return featureManipulation.removeFeatures(toDelete, theme);		
	}
	
	public void updateFeatures(HashMap<String, SimpleFeature> editedfeatures){
		List<FeatureDTO> featuresList = new ArrayList<FeatureDTO>();
		for (String key : editedfeatures.keySet()) {
			FeatureDTO featureDTO = new FeatureDTO(editedfeatures.get(key));
			featuresList.add(featureDTO);
		}
		if(featuresList.isEmpty() == false){
			
			CustomNode themeNode = AppSingleton.getInstance().getTreeState().getCurrentTheme();
			Layer layer = null;
			if(themeNode==null){
				JOptionPane.showMessageDialog(null, "Nenhum tema selecionado!");
				return;
			}
			layer = themeNode.getTheme().getLayer();
			if(layer==null){
				JOptionPane.showMessageDialog(null, "Não há layer associado ao tema selecionado!");
				return;
			}

			Theme currentTheme = themeNode.getTheme();
			
			featureManipulation.updateFeatures(currentTheme, featuresList);	
			EditionState.setEdited(false);
		}
	}
	
	@SuppressWarnings("unused")
	private String getPointID(CustomNode theme, Point point){
		String id = "";
		CanvasState canvas = AppSingleton.getInstance().getCanvasState();
		Theme currentTheme = theme.getTheme();
		Box currentBox = AppSingleton.getInstance().getCanvasState().getBox();
		try {
			List<String> ids = AppSingleton.getInstance().getServices().locateObject(currentBox, canvas.getCanvasWidth(), canvas.getCanvasHeight(), point.getX(), point.getY(), currentTheme);
			if(ids == null)
				return "";
			else
				return ids.get(0);
		} catch (GlueServerException e) {
			e.printStackTrace();
			id=null;
		}
		return id;
	}
	
	private String setBDPointID(String object_id, String point1, String point2){
		String id = "";
		try {

			String url = "jdbc:postgresql://localhost:5432/valinhos";
			String usuario = "postgres";
			String senha = "postgres";

			Class.forName("org.postgresql.Driver");

			Connection con;
			
			con = DriverManager.getConnection(url, usuario, senha);

			Statement stm = con.createStatement();
		
			stm.executeUpdate("update redesagua set noinic ="+"'"+point1+"'"+", nofinal="+"'"+point2+"'"+ "where object_id = "+"'"+object_id+"'");
			con.close();
		

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}
}
