package com.nexusbr.gv.controller.tools;

import java.util.ArrayList;
import java.util.EventObject;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.jtdk.edition.undoredo.event.RedoEvent;

import com.nexusbr.gv.controller.Manager;

public class RedoTool extends Manager{
	public RedoTool(){
		
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
	
	private void handle(AfterToolChangedEvent e) throws Exception{
		dispatch(transmitter, new RedoEvent(this));
		dispatchLayersEdition();		
	}
}
