package com.nexusbr.gv.controller.tools;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import br.org.funcate.eagles.kernel.dispatcher.EventHandler;
import br.org.funcate.eagles.kernel.listener.ListenersHandlerImpl;
import br.org.funcate.eagles.kernel.transmitter.DirectedEventTransmitter;
import br.org.funcate.glue.event.AfterToolChangedEvent;
import br.org.funcate.glue.event.BeforeToolChangedEvent;
import br.org.funcate.glue.event.MouseMovedEvent;
import br.org.funcate.glue.event.MousePressedEvent;
import br.org.funcate.glue.event.MouseReleasedEvent;
import br.org.funcate.glue.event.UpdateCursorEvent;
import br.org.funcate.jtdk.edition.event.FeatureCreatedEvent;
import br.org.funcate.jtdk.edition.event.FeatureRemovedEvent;
import br.org.funcate.jtdk.edition.event.GetFeatureEvent;
import br.org.funcate.jtdk.edition.event.SetStyleEvent;

import com.nexusbr.gv.controller.Manager;
import com.nexusbr.gv.services.CreateFeatureStyle;
import com.nexusbr.gv.services.EditionState;
import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.LineCreatorService;
import com.nexusbr.gv.services.PointCreatorService;
import com.nexusbr.gv.services.PolygonCreatorService;
import com.nexusbr.gv.singleton.GVSingleton;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * This class recive the event Eagle, create the Polygon them send a event to
 * redraw
 * 
 * @author Bruno Severino
 * @version 1.0
 */
public class PolygonCreatorTool extends Manager {

	private boolean fisrtPolygonPoint = true;
	private boolean snapPolyFound = false;
	private ArrayList<Coordinate> temporaryArrayPolygon;
	private Geometry geomIntersected = null;
	private SimpleFeature ghostLine;
	// private SimpleFeature ghostPolygon;
	private SimpleFeature snapPoint;

	public PolygonCreatorTool() {
		// SET THE EAGLE EVENTS
		listeners = new ListenersHandlerImpl();
		eventHandler = new EventHandler();
		transmitter = new DirectedEventTransmitter(this);

		// CREATE THE EVENT LIST
		eventsToListen = new ArrayList<String>();
		eventsToListen.add(AfterToolChangedEvent.class.getName());
		eventsToListen.add(BeforeToolChangedEvent.class.getName());
		eventsToListen.add(MousePressedEvent.class.getName());
		eventsToListen.add(MouseReleasedEvent.class.getName());
		eventsToListen.add(MouseMovedEvent.class.getName());

		// CHANGE CURSOR TO PRESSED
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
				Toolkit.getDefaultToolkit().createImage(
						getClass().getResource(
								"/com/nexusbr/gv/images/curPressed.png")),
				new Point(16, 16), "draw");

		toolbar = GVSingleton.getInstance().getToolbar();

		// SET STATE
		fisrtPolygonPoint = true;
		temporaryArrayPolygon = new ArrayList<Coordinate>();
	}

	public void handle(EventObject e) throws Exception {
		// VERIFY WHAT EVENT IT IS, AND REDIRECT IT
		if (e instanceof AfterToolChangedEvent) {
			handle((AfterToolChangedEvent) e);
		}

		else if (e instanceof BeforeToolChangedEvent) {
			handle((BeforeToolChangedEvent) e);
		}

		else if (e instanceof MousePressedEvent) {
			handle((MousePressedEvent) e);
		}

		else if (e instanceof MouseReleasedEvent) {
			handle((MouseReleasedEvent) e);
		}

		else if (e instanceof MouseMovedEvent) {
			handle((MouseMovedEvent) e);
		}
	}

	private void handle(AfterToolChangedEvent e) throws Exception {
		// CREATE POINT STYLE
		dispatch(
				transmitter,
				new SetStyleEvent(this, new CreateFeatureStyle()
						.createPolygonStyle(), "POLYGON"));
		dispatch(
				transmitter,
				new SetStyleEvent(this, new CreateFeatureStyle()
						.createGhostLineStyle(), "GHOSTLINE"));
		dispatch(
				transmitter,
				new SetStyleEvent(this, new CreateFeatureStyle()
						.createSnapPointStyle(), "SNAPPOINT"));
	}

	private void handle(BeforeToolChangedEvent e) throws Exception {
		dispatch(transmitter, new FeatureRemovedEvent(this, ghostLine,
				"GHOSTLINE", false));
		dispatchLayersFeedback();
	}

	private void handle(MouseMovedEvent e) throws Exception {
		if (fisrtPolygonPoint) {
			return;
		}

		double x = e.getX();
		double y = e.getY();

		GetFeatureEvent getPoly = new GetFeatureEvent(this, "POLYGON");
		dispatch(transmitter, getPoly);
		SimpleFeatureCollection polyCollection = getPoly.getFeatureCollection();

		IntersectGeometry intGeom = new IntersectGeometry();
		snapPolyFound = intGeom.checkForSnap(e.getX(), e.getY(),
				polygonSnapTolerance, polyCollection, this, transmitter, false);
		if (snapPolyFound) {
			geomIntersected = intGeom.getGeomIntersecting();

			// DRAW SNAP POINT
			SimpleFeature aux2 = snapPoint;
			dispatch(transmitter, new FeatureRemovedEvent(this, aux2,
					"SNAPPOINT", false));
			x = ((Geometry) geomIntersected).getCentroid().getX();
			y = ((Geometry) geomIntersected).getCentroid().getY();
			snapPoint = new PointCreatorService().createPoint(x, y);
			dispatch(transmitter, new FeatureCreatedEvent(this, snapPoint,
					"SNAPPOINT", false));
		} else {
			SimpleFeature aux2 = snapPoint;
			dispatch(transmitter, new FeatureRemovedEvent(this, aux2,
					"SNAPPOINT", false));
		}

		// REMOVE GHOST LINE
		dispatch(transmitter, new FeatureRemovedEvent(this, ghostLine,
				"GHOSTLINE", false));

		// DRAW GHOST POLYGON

		// temporaryArrayPolygon.add(new Coordinate(e.getX(),e.getY()));
		// temporaryArrayPolygon.add(temporaryArrayPolygon.get(0));
		// Coordinate[] coords = new Coordinate[temporaryArrayPolygon.size()];
		// for(int i = 0; i < temporaryArrayPolygon.size(); i++)
		// coords[i] = temporaryArrayPolygon.get(i);
		// LinearRing ring = new
		// GeometryFactory().createLinearRing(coords);//Creates a linear ring
		// JTS object with the coordinates just created.
		//
		// // ADD GEOMETRIES IN EDITION CONTROLLER
		// lastFeaturePolygon = new PolygonCreatorService().createPolygon(ring);
		// dispatch(transmitter, new FeatureCreatedEvent(this,lastFeaturePolygon
		// , "POLYGON", true));
		// fisrtPolygonPoint = true;

		Coordinate[] coords = new Coordinate[temporaryArrayPolygon.size() + 1];
		for (int i = 0; i < temporaryArrayPolygon.size(); i++)
			coords[i] = temporaryArrayPolygon.get(i);

		coords[coords.length - 1] = new Coordinate(x, y);

		ghostLine = new LineCreatorService().createFeatureLine(coords);
		dispatch(transmitter, new FeatureCreatedEvent(this, ghostLine,
				"GHOSTLINE", false));

		dispatchLayersFeedback();
		EditionState.setEdited(true);
	}

	private void handle(MouseReleasedEvent e) throws Exception {
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
				Toolkit.getDefaultToolkit().createImage(
						getClass().getResource(
								"/com/nexusbr/gv/images/curReleased.png")),
				new Point(16, 16), "draw");
		dispatch(transmitter, new UpdateCursorEvent(this));
	}

	private void handle(MousePressedEvent e) throws Exception {
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
				Toolkit.getDefaultToolkit().createImage(
						getClass().getResource(
								"/com/nexusbr/gv/images/curPressed.png")),
				new Point(16, 16), "draw");
		dispatch(transmitter, new UpdateCursorEvent(this));

		if (e.getButton() == MouseEvent.BUTTON1) {
			double x = e.getX();
			double y = e.getY();

			GetFeatureEvent getPoly = new GetFeatureEvent(this, "POLYGON");
			dispatch(transmitter, getPoly);
			SimpleFeatureCollection polyCollection = getPoly
					.getFeatureCollection();

			IntersectGeometry intGeom = new IntersectGeometry();
			snapPolyFound = intGeom.checkForSnap(e.getX(), e.getY(),
					polygonSnapTolerance, polyCollection, this, transmitter,
					false);
			if (snapPolyFound) {
				geomIntersected = intGeom.getGeomIntersecting();
				x = ((Geometry) geomIntersected).getCentroid().getX();
				y = ((Geometry) geomIntersected).getCentroid().getY();
			}

			Coordinate coord = new Coordinate(x, y);
			if (fisrtPolygonPoint) {
				temporaryArrayPolygon.clear();
				fisrtPolygonPoint = false;
				Coordinate[] coords = new Coordinate[] { coord, coord }; // /DRAW
																			// GHOST
																			// LINE\
				ghostLine = new LineCreatorService().createLine(coords, null); // \---------------/
			}
			temporaryArrayPolygon.add(coord);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			dispatch(transmitter, new FeatureRemovedEvent(this, ghostLine,
					"GHOSTLINE", false));
			fisrtPolygonPoint = true;
			dispatchLayersFeedback();
		} else if (e.getButton() == MouseEvent.BUTTON3) // This method is to
														// close the polygon by
														// right cick of mouse
		{
			dispatch(transmitter, new FeatureRemovedEvent(this, ghostLine,
					"GHOSTLINE", false)); // REMOVE GHOST LINE

			temporaryArrayPolygon.add(new Coordinate(e.getX(), e.getY()));
			temporaryArrayPolygon.add(temporaryArrayPolygon.get(0));
			Coordinate[] coords = new Coordinate[temporaryArrayPolygon.size()];
			for (int i = 0; i < temporaryArrayPolygon.size(); i++)
				coords[i] = temporaryArrayPolygon.get(i);
			LinearRing ring = new GeometryFactory().createLinearRing(coords);// Creates
																				// a
																				// linear
																				// ring
																				// JTS
																				// object
																				// with
																				// the
																				// coordinates
																				// just
																				// created.

			// ADD GEOMETRIES IN EDITION CONTROLLER
			SimpleFeature lastFeaturePolygon = new PolygonCreatorService().createPolygon(ring);
			dispatch(transmitter, new FeatureCreatedEvent(this,lastFeaturePolygon, "POLYGON", true));
			fisrtPolygonPoint = true;

			// POPULATE PROPERTIES FRAME
			toolbar.setFeature(lastFeaturePolygon);

			dispatchLayersFeedback();
			dispatchLayersEdition();
		}
	}
}