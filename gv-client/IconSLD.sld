<StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd">
<NamedLayer>
<Name>Point as graphic</Name>
<UserStyle>
<Title>GeoServer SLD Cook Book: Point as graphic</Title>
<FeatureTypeStyle>
<Rule>
<MaxScaleDenominator>20000</MaxScaleDenominator>
<PointSymbolizer>
<Graphic>
<ExternalGraphic>
<OnlineResource xlink:type="simple" xlink:href="Lightbulb.png"/>
<Format>image/png</Format>
</ExternalGraphic>
<Size>25</Size>
</Graphic>
</PointSymbolizer>
</Rule>
</FeatureTypeStyle>
</UserStyle>
</NamedLayer>
</StyledLayerDescriptor>