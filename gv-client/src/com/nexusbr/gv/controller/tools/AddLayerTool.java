package com.nexusbr.gv.controller.tools;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.GetCanvasStateEvent;
import br.org.funcate.glue.main.AppSingleton;
import br.org.funcate.glue.model.Attribute;
import br.org.funcate.glue.model.Box;
import br.org.funcate.glue.model.Layer;
import br.org.funcate.glue.model.Projection;
import br.org.funcate.glue.model.Representation;

import com.nexusbr.gv.controller.Manager;

public class AddLayerTool extends Manager {
	public AddLayerTool(){		
		//SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		//CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());		
	}
	public void handle(EventObject e) throws Exception {
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT
		if(e instanceof AfterToolChangedEvent){
			handle((AfterToolChangedEvent) e);
		}
	}
	@SuppressWarnings("unused")
	private void handle(AfterToolChangedEvent e) throws Exception{
		//testando 1 2 3
		List<Layer> layers = AppSingleton.getInstance().getTreeState().getLayers();
		
		
		for (int i = 0; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			Projection p =layer.getProjection();
			Projection newProj = new Projection();
			if(p!=null){
				newProj.setDatum(p.getDatum());
				newProj.setHemNorth(p.getHemNorth());
				newProj.setLat0(p.getLat0());
				newProj.setLon0(p.getLon0());
				newProj.setName(p.getName());
				newProj.setOffx(p.getOffx());
				newProj.setOffy(p.getOffy());
				newProj.setScale(p.getScale());
				newProj.setSelected(p.getSelected());
				newProj.setStlat1(p.getStlat1());
				newProj.setStlat2(p.getStlat2());
				newProj.setUnits(p.getUnits());
				newProj.setZone(p.getZone());
			}
			GetCanvasStateEvent canvasState = new GetCanvasStateEvent(this);
			dispatch(transmitter, canvasState);				
			Box box = canvasState.getCanvasState().getBox();
			
			String sessionId = AppSingleton.getInstance().getServices().getSessionId();
			
			if(layer.getRepresentations()!=null&&layer.getRepresentations().size()>0){
				List<Representation> list = layer.getRepresentations();
				
				for (int j = 0; j < list.size(); j++){
					Representation r = list.get(j);
					if(Integer.parseInt(r.getName())<16){
						Representation newRep = new Representation();
						newRep.setName(r.getName());
						newRep.setId(r.getId());
						/*if(AppSingleton.getInstance().getServices().createLayer("teste", newProj, newRep, box, getAttributes(), sessionId)){
							System.out.println("Criou o layer!");
						}else{
							System.out.println("Erro ao criar o layer!");
						}*/
					}
				}
			}
		}
		dispatchLayersEdition();
		dispatchLayersFeedback();
		
	}
	
	@SuppressWarnings("unused")
	private List<Attribute> getAttributes(){
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		Attribute attr = new Attribute();
		attr.setDataType("string");
		attr.setName("object_id0");
		attr.setPrimaryKey(1);
		attr.setSize(16);
		attributes.add(attr);
		
		attr = new Attribute();
		attr.setDataType("string");
		attr.setName("type");
		attr.setPrimaryKey(0);
		attr.setSize(100);
		attributes.add(attr);
		
		attr = new Attribute();
		attr.setDataType("integer");
		attr.setName("networkmode");
		attributes.add(attr);
		
		attr = new Attribute();
		attr.setDataType("integer");
		attr.setName("point1");
		attributes.add(attr);
		
		attr = new Attribute();
		attr.setDataType("integer");
		attr.setName("point2");
		attributes.add(attr);
		
		return attributes;
	}
}
