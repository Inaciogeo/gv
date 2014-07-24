package com.nexusbr.gv.services;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.FilterFactory;

public class CreateFeatureStyle  {
         
	private static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
	private static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
	    
    public Style createLineStyle(){            
        Stroke stroke;
        LineSymbolizer lineSymbolizer;
        
        //LINE
    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(124,252,100)), filterFactory.literal(2));
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.setName("Line");        
        rule.setTitle("Line");
        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));
        rule.symbolizers().add(lineSymbolizer);
        
    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(255,0,0)), filterFactory.literal(4));
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Line Selected");
        ruleSelected.setTitle("Line Selected");
        ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(true)));
        ruleSelected.symbolizers().add(lineSymbolizer);
        
        FeatureTypeStyle ftsLine = styleFactory.createFeatureTypeStyle(new Rule[]{rule,ruleSelected});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(ftsLine);        
        return style;
    }
    
    public Style createLineStyle2(){            
        Stroke stroke;
        LineSymbolizer lineSymbolizer;
        
        // LINE REDE
//    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(124,252,100)), filterFactory.literal(10));
//        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
//        Rule rule = styleFactory.createRule();
//        rule.setName("Line");        
//        rule.setTitle("Line");
//        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));
//        rule.symbolizers().add(lineSymbolizer);
        
    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(255,100,0)), filterFactory.literal(4));
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Line Selected");
        ruleSelected.setTitle("Line Selected");
        ruleSelected.setFilter(filterFactory.notEqual(filterFactory.property("nome_logradouro"), filterFactory.literal("")));
        ruleSelected.symbolizers().add(lineSymbolizer);
        
        FeatureTypeStyle ftsLine = styleFactory.createFeatureTypeStyle(new Rule[]{ruleSelected});
        Style style = styleFactory.createStyle();
       
        style.featureTypeStyles().add(ftsLine);
        
        return style;
    }
    
    public Style createGhostLineStyle()
    {    
    	Stroke stroke;
        LineSymbolizer lineSymbolizer;
        
        // GHOST LINE
    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(100, 100, 255,100)), filterFactory.literal(2));
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.setName("Ghost Line");        
        rule.setTitle("Ghost Line");
        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));
        rule.symbolizers().add(lineSymbolizer);
        
        // GHOST SELECTED LINE
    	stroke = styleFactory.createStroke(filterFactory.literal(new Color(255,0,0,100)), filterFactory.literal(3));
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Ghost Line Selected");
        ruleSelected.setTitle("Ghost Line Selected");
        ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(true)));
        ruleSelected.symbolizers().add(lineSymbolizer);
        
        FeatureTypeStyle ftsLine = styleFactory.createFeatureTypeStyle(new Rule[]{rule, ruleSelected});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(ftsLine);        
        return style;
    }    
    
    public Style createPointStyle() 
    {
    	Graphic gr;
    	Mark mark;
    	PointSymbolizer pointSymbolizer;
    
    	//POINT
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(2)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(new Color(255,36,0))));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule rule = styleFactory.createRule();
        rule.setName("Point");        
        rule.setTitle("Point");             
        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal("false")));            
        rule.symbolizers().add(pointSymbolizer);
        
        //POINT WITH SELECTED TRUE
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getSquareMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.RED)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Point Selected");
        ruleSelected.setTitle("Point Selected");
        ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal("true")));        
        ruleSelected.symbolizers().add(pointSymbolizer);
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule, ruleSelected});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);                
        return style;
    }
    public Style createPointStyle2() throws URISyntaxException 
    {
    	Graphic gr;
    	Mark mark;
    	PointSymbolizer pointSymbolizer;
    
    	//POINT CAP
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getSquareMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(2)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(new Color(255,36,0))));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule ruleCAP = styleFactory.createRule();
        ruleCAP.setName("PointCAP");        
        ruleCAP.setTitle("PointCAP");             
        ruleCAP.setFilter(filterFactory.equals(filterFactory.property("descric"), filterFactory.literal("CAP")));            
        ruleCAP.symbolizers().add(pointSymbolizer);
        
        //POINT TE
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getTriangleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(2)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.BLUE)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(14));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);
        Rule ruleTE = styleFactory.createRule();
        ruleTE.setName("PointTE");
        ruleTE.setTitle("PointTE");
        ruleTE.setFilter(filterFactory.equals(filterFactory.property("descric"), filterFactory.literal("TE")));        
        ruleTE.symbolizers().add(pointSymbolizer);
        
      //POINT default
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(2)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.ORANGE)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);
        Rule ruleT = styleFactory.createRule();
        ruleT.setName("PointTE");
        ruleT.setTitle("PointTE"); 
        ruleT.setElseFilter(true);
        ruleT.symbolizers().add(pointSymbolizer);
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{ruleCAP,ruleTE,ruleT});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);                
        return style;
    }
    public Style createPointStyle3() 
    {	
    	 SLDParser stylereader = null; //We use SLDParser instead of SLDStyle
    	
    	    try {
    	    	File file = new File("IconSLD.sld");  
    	    	URL url = new URL("file:"+file.getAbsoluteFile());
    	        stylereader = new SLDParser(styleFactory, url );
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	    Style[] style = stylereader.readXML();

        return style[0];
    }
    
    public Style createGhostPointStyle() {
    	Graphic gr;
    	Mark mark;
    	PointSymbolizer pointSymbolizer;
    	
    	//GHOST POINT
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(new Color(255,255,0,100))));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(7));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule rule = styleFactory.createRule();
        rule.setName("Ghost Point");        
        rule.setTitle("Ghost Point");        
        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal("false")));
        rule.symbolizers().add(pointSymbolizer);
        
        //GHOST SELECTED POINT
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(new Color(255,0,0,100))));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Ghost Selected Point");        
        ruleSelected.setTitle("Ghost Selected Point");
        ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal("true")));
        ruleSelected.symbolizers().add(pointSymbolizer);
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule, ruleSelected});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);                
        return style;      
    } 
    
    public Style createPolygonStyle() 
    {	
    	Fill fill;
    	Stroke stroke;
    	PolygonSymbolizer polygonSymbolizer;

	   	// POLYGON
    	fill = styleFactory.createFill(filterFactory.literal(new Color(0,255,0)));    	 
   	 	stroke = styleFactory.createStroke(filterFactory.literal(Color.BLUE),filterFactory.literal(2));
		polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);
		Rule rule = styleFactory.createRule();
		rule.setName("Polygon");        
		rule.setTitle("Polygon");
		rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));
		rule.symbolizers().add(polygonSymbolizer);

		// SELECTED POLYGON
    	fill = styleFactory.createFill(filterFactory.literal(new Color(255,0,0)));    	 
   	 	stroke = styleFactory.createStroke(filterFactory.literal(Color.BLUE),filterFactory.literal(2));
		polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);
		Rule ruleSelected = styleFactory.createRule();
		ruleSelected.setName("Select Polygon");        
		ruleSelected.setTitle("Select Polygon");
		ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(true)));
		ruleSelected.symbolizers().add(polygonSymbolizer);
		
		FeatureTypeStyle ftsPoly = styleFactory.createFeatureTypeStyle(new Rule[]{rule, ruleSelected});
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(ftsPoly);
		return style;
    }
    
    public Style createSnapLineStyle()
    {    
    	Stroke stroke;
        LineSymbolizer lineSymbolizer;
        
        // GHOST LINE
    	stroke = styleFactory.createStroke(
			filterFactory.literal(new Color(255,0,0,100)), filterFactory.literal(1), null, null,null, new float[] { 5, 2 }, null, null, null
		);    	
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.setName("Snap Line");        
        rule.setTitle("Snap Line");
        rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));
        rule.symbolizers().add(lineSymbolizer);
        
        // GHOST SELECTED LINE
    	stroke = styleFactory.createStroke(
			filterFactory.literal(new Color(255,0,0,100)), filterFactory.literal(1), null, null,null, new float[] { 5, 2 }, null, null, null
		); 
        lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule ruleSelected = styleFactory.createRule();
        ruleSelected.setName("Snap Line Selected");
        ruleSelected.setTitle("Snap Line Selected");
        ruleSelected.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(true)));
        ruleSelected.symbolizers().add(lineSymbolizer);
        
        FeatureTypeStyle ftsLine = styleFactory.createFeatureTypeStyle(new Rule[]{rule, ruleSelected});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(ftsLine);        
        return style;
    }    
    
    public Style createSnapPointStyle() {
    	//Objeto que define como uma geometria ou um raster devem ser exibidos, com relacao a cores, tamanho, etc.
        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark;
        
        Graphic gr2 = styleFactory.createDefaultGraphic();
        Mark mark2;
        
        // criar o ponto
        mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(new Color(0,255,204)), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(new Color(0,255,204,50))));                
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(30));        
        PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);
    	
        
        mark2 = styleFactory.getCircleMark();
        //mark2.setStroke(styleFactory.createStroke(filterFactory.literal(Color.RED), filterFactory.literal(1)));
        mark2.setFill(styleFactory.createFill(filterFactory.literal(Color.GRAY)));                
        gr2.graphicalSymbols().clear();
        gr2.graphicalSymbols().add(mark2);
        gr2.setSize(filterFactory.literal(5));        
        PointSymbolizer pointSymbolizer2 = styleFactory.createPointSymbolizer(gr2, null);
        
        //Regra para um estilo de fei��o.
        Rule rule = styleFactory.createRule();
                
        //Adiciona o PointSymbolizer na regra.
        //rule.setName("Point");        
        //rule.setFilter(filterFactory.equals(filterFactory.property("selected"), filterFactory.literal(false)));        
        rule.symbolizers().add(pointSymbolizer);       
        rule.symbolizers().add(pointSymbolizer2);
        
        
        //Adiciona uma ou N regras em uma estilo de uma determinada fei��o.
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
                
        //Cria o estilo (SLD).
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
         
        return style;
    }      

    
    public Style createSelectBoxStyle() {
	   	// create the Fill color
   	 	//Fill fill = styleFactory.createFill(filterFactory.literal(new Color(0,255,102,150)));    	 
   	 
   		// create a partially opaque outline stroke
   	 	Stroke stroke = styleFactory.createStroke(
   	 			filterFactory.literal(Color.BLUE),
   	 			filterFactory.literal(1),
          	null,
          	null,
          	null,
          	new float[] { 5, 2 },
          	null,
          	null,
          	null
		);
		//Cria um Symbolizer para linha.
		PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, null, null);
		//Regra para um estilo de fei��o.
		Rule rulePoly = styleFactory.createRule();
		
		//Adiciona o PointSymbolizer na regra.
		//rulePoly.setName("Polygon");        
		//ruleLine.setFilter(filterFactory.equals(filterFactory.property("geom"), filterFactory.literal(false)));
		rulePoly.symbolizers().add(polygonSymbolizer);        
			  
		//Adiciona uma ou N regras em uma estilo de uma determinada fei��o.
		FeatureTypeStyle ftsPoly = styleFactory.createFeatureTypeStyle(new Rule[]{rulePoly});
		 
		//Cria o estilo (SLD).
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(ftsPoly);
		    
		return style;
    }
    
    public Style createEditPathBox() { 	 //CRIA O QUADRADO VERMELHO EM VOLTA DO OBJETO
   		// create a outline stroke
   	 	Stroke stroke = styleFactory.createStroke(
   	 		filterFactory.literal(Color.RED), filterFactory.literal(1), null, null,null, new float[] { 5, 2 }, null, null, null
		);
   	 	
		//Cria um Symbolizer para linha.
		PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, null, null);
		//Regra para um estilo de fei��o.
		Rule rulePoly = styleFactory.createRule();
		
		//Adiciona o PointSymbolizer na regra.
		//rulePoly.setName("Polygon");        
		//ruleLine.setFilter(filterFactory.equals(filterFactory.property("geom"), filterFactory.literal(false)));
		rulePoly.symbolizers().add(polygonSymbolizer);        
			  
		//Adiciona uma ou N regras em uma estilo de uma determinada fei��o.
		FeatureTypeStyle ftsPoly = styleFactory.createFeatureTypeStyle(new Rule[]{rulePoly});
		 
		//Cria o estilo (SLD).
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(ftsPoly);
		    
		return style;
    }   
    public Style createEditPathResize() { 	 //CRIA O QUADRADO PRETO PARA RESIZE
    	Graphic gr;
    	Mark mark;
    	PointSymbolizer pointSymbolizer;
    	
    	//POINT
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getSquareMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.GRAY)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(4));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(pointSymbolizer);
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);                
        return style;
    }  
    public Style createEditPathEdges() { 	 //CRIA O QUADRADO BRANCO NAS ARESTAS PARA MOVER
    	Graphic gr;
    	Mark mark;
    	PointSymbolizer pointSymbolizer;
    	
    	//POINT
        gr = styleFactory.createDefaultGraphic();
        mark = styleFactory.getSquareMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.WHITE)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(4));
        pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(pointSymbolizer);
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);                
        return style;
    }  
    public Style createReferenceCircleLineStyle() { 
    	
	    // LINE
	    Stroke stroke = styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1));
		LineSymbolizer lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);	 
	    	    
		// TEXT
        StyleBuilder sb = new StyleBuilder();
        
        LinePlacement linePlacement = sb.createLinePlacement(15);
        linePlacement.setRepeated(false);
        linePlacement.setGeneralized(false);
        
        TextSymbolizer textSymbolizer = sb.createTextSymbolizer();
        textSymbolizer.setLabel(sb.attributeExpression("radius"));
        textSymbolizer.setFill(sb.createFill(Color.BLUE));
        textSymbolizer.setFont(sb.createFont("Lucida Sans", 15));
        textSymbolizer.setHalo(sb.createHalo(Color.WHITE, 0));
        textSymbolizer.setLabelPlacement(linePlacement);   
        
        // RULE
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(lineSymbolizer);	  
        //rule.symbolizers().add(textSymbolizer);
        
        // STYLE
        Style style = sb.createStyle();
        //FeatureTypeStyle featureTypeStyle = sb.createFeatureTypeStyle("labelLine", new Symbolizer[] {textSymbolizer, lineSymbolizer });
        FeatureTypeStyle featureTypeStyle2 = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        
        //style.featureTypeStyles().add(featureTypeStyle);
        style.featureTypeStyles().add(featureTypeStyle2);
        
        return style;   
    }
    public Style createReferenceCircleStyle() {          
    	//Create the circle
    	Stroke stroke = styleFactory.createStroke(
   	 		filterFactory.literal(Color.GREEN),
   	 		filterFactory.literal(1),
          	null, null, null, new float[] { 5, 2 }, null, null, null
		);
		PolygonSymbolizer polygonSymbolizer = styleFactory.createPolygonSymbolizer(stroke, null, null);
		Rule rulePoly = styleFactory.createRule();		
		rulePoly.symbolizers().add(polygonSymbolizer);        
		
		//create the center point
		Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.GRAY)));                
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(5));        
        
        PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer(gr, null);        
        Rule rulePoint = styleFactory.createRule();
        rulePoint.symbolizers().add(pointSymbolizer);
        
        StyleBuilder sb = new StyleBuilder();
        
        TextSymbolizer textSymbolizer = sb.createTextSymbolizer();
        textSymbolizer.setLabel(sb.attributeExpression("radius"));
        textSymbolizer.setFill(sb.createFill(Color.BLUE));
        textSymbolizer.setFont(sb.createFont("Lucida Sans", 15));
        textSymbolizer.setHalo(sb.createHalo(Color.WHITE, 15));
        textSymbolizer.setLabelPlacement(null);
        
        Rule ruleText = styleFactory.createRule();
        ruleText.symbolizers().add(textSymbolizer);	
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rulePoly, rulePoint});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
         
        return style;        
    }
    public Style createImagePointStyle(){
    	StyleBuilder styleBuilder = new StyleBuilder();
        Style style = styleBuilder.createStyle();
        
        PointSymbolizer pointSymbolizer = styleBuilder.createPointSymbolizer();
        
        Graphic graphic = styleBuilder.createGraphic();
        ExternalGraphic external = styleBuilder.createExternalGraphic("../JTDK/src/main/java/br/org/funcate/jtdk/teste/fogo2.png",
                "image/png");
        graphic.graphicalSymbols().add(external);
        graphic.graphicalSymbols().add(styleBuilder.createMark("circle"));
        
        pointSymbolizer.setGraphic(graphic);
        
        Rule rule = styleBuilder.createRule(pointSymbolizer);
        FeatureTypeStyle featureTypeStyle = styleBuilder.createFeatureTypeStyle("Feature", rule);
        style.featureTypeStyles().add(featureTypeStyle);
    	return style;
    }
}
