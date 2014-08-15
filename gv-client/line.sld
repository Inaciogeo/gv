<StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd">
<NamedLayer>
<Name>Line with border</Name>
<UserStyle>
<Title>SLD Cook Book: Line w2th border</Title>
<FeatureTypeStyle>
<Rule>
<LineSymbolizer>
<Stroke>
<CssParameter name="stroke">#333333</CssParameter>
<CssParameter name="stroke-width">10</CssParameter>
<CssParameter name="stroke-linecap">round</CssParameter>
</Stroke>
</LineSymbolizer>
</Rule>
</FeatureTypeStyle>
<FeatureTypeStyle>
<Rule>
<LineSymbolizer>
<Stroke>
<CssParameter name="stroke">#FFFF00</CssParameter>
<CssParameter name="stroke-width">6</CssParameter>
<CssParameter name="stroke-linecap">round</CssParameter>
</Stroke>
</LineSymbolizer>
</Rule>
</FeatureTypeStyle>
</UserStyle>
</NamedLayer>
</StyledLayerDescriptor>