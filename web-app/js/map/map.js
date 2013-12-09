
$(document).ready(function() {
  
  DDBMap = function() {
    this.init();
  }

  /** Capsulate main logic in object * */
  $.extend(DDBMap.prototype, {

      /** Configuration * */
      rootDivId: "ddb-map",
      initLat: 49.00,
      initLon: 9.00,
      initZoom: 8,
      tileServerUrls: ["http://a.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://b.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://c.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png"],
      imageFolder: jsContextPath+"/js/vendor/openlayers-2.13.1/img/",
      themeFolder: jsContextPath+"/js/vendor/openlayers-2.13.1/theme/default/style.css",
      osmMap: null,

      /** Initialization * */
      init : function() {
      },

      display : function(config) {
        this.log("################ 00 : display");
        
        this.applyConfiguration(config);
        
        var rootDiv = $("#"+this.rootDivId);
        if(rootDiv.length > 0){

          OpenLayers.ImgPath = this.imageFolder;
          
          var options = {
            theme: this.themeFolder, 
            controls: [
//              new OpenLayers.Control.Navigation(),
//              new OpenLayers.Control.PanZoomBar(),
//              new OpenLayers.Control.Attribution(),
                new OpenLayers.Control.MousePosition()
            ]
          };
          this.osmMap = new OpenLayers.Map(this.rootDivId, options);
          
          //var tiles          = new OpenLayers.Layer.OSM("DDB tile server layer", this.tileServerUrls, {numZoomLevels: 19});
          var tiles          = new OpenLayers.Layer.OSM();
          var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
          var toProjection   = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
          var position       = new OpenLayers.LonLat(this.initLon, this.initLat).transform( fromProjection, toProjection);
          var zoom           = this.initZoom; 
   
          this.osmMap.addLayer(tiles);
          this.osmMap.setCenter(position, zoom );
          

        }
        
      },
      
      addInstitutionsLayer : function() {
        this.log("################### 01: addInstitutionsLayer");
        // allow testing of specific renderers via "?renderer=Canvas", etc
        var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
        renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;
        
        var vectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", {
          styleMap: new OpenLayers.StyleMap({'default':{
              strokeColor: "#00FF00",
              strokeOpacity: 1,
              strokeWidth: 3,
              fillColor: "#FF5500",
              fillOpacity: 0.5,
              pointRadius: 6,
              pointerEvents: "visiblePainted",
              // label with \n linebreaks
              label : "name: ${name}\n\nage: ${age}",
              
              fontColor: "${favColor}",
              fontSize: "12px",
              fontFamily: "Courier New, monospace",
              fontWeight: "bold",
              labelAlign: "${align}",
              labelXOffset: "${xOffset}",
              labelYOffset: "${yOffset}",
              labelOutlineColor: "white",
              labelOutlineWidth: 3
          }}),
          renderers: renderer
        });
        
        // create a point feature
        var point = new OpenLayers.Geometry.Point(-111.04, 45.68);
        var pointFeature = new OpenLayers.Feature.Vector(point);
        pointFeature.attributes = {
            name: "toto",
            age: 20,
            favColor: 'red',
            align: "cm"
        };
        
        // create a polygon feature from a linear ring of points
        var pointList = [];
        for(var p=0; p<6; ++p) {
            var a = p * (2 * Math.PI) / 7;
            var r = Math.random(1) + 1;
            var newPoint = new OpenLayers.Geometry.Point(point.x + 5 + (r * Math.cos(a)),
                                                         point.y + 5 + (r * Math.sin(a)));
            pointList.push(newPoint);
        }
        pointList.push(pointList[0]);
        
        var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
        var polygonFeature = new OpenLayers.Feature.Vector(
            new OpenLayers.Geometry.Polygon([linearRing]));
        polygonFeature.attributes = {
            name: "dude",
            age: 21,
            favColor: 'purple',
            align: 'lb'
        };
        
        multiFeature = new OpenLayers.Feature.Vector(
            new OpenLayers.Geometry.Collection([
                new OpenLayers.Geometry.LineString([
                    new OpenLayers.Geometry.Point(-105,40),
                    new OpenLayers.Geometry.Point(-95,45)
                ]),
                new OpenLayers.Geometry.Point(-105, 40)
            ]),
            {
                name: "ball-and-chain",
                age: 30,
                favColor: 'black',
                align: 'rt'
            });
  
        // Create a point feature to show the label offset options
        var labelOffsetPoint = new OpenLayers.Geometry.Point(-101.04, 35.68);
        var labelOffsetFeature = new OpenLayers.Feature.Vector(labelOffsetPoint);
        labelOffsetFeature.attributes = {
            name: "offset",
            age: 22,
            favColor: 'blue',
            align: "cm",
            // positive value moves the label to the right
            xOffset: 50,
            // negative value moves the label down
            yOffset: -15
        };
  
  
        var nullFeature = new OpenLayers.Feature.Vector(null);
        nullFeature.attributes = {
            name: "toto is some text about the world",
            age: 20,
            favColor: 'red',
            align: "cm"
        };
        
        this.osmMap.addLayer(vectorLayer);
        vectorLayer.drawFeature(multiFeature);
        this.osmMap.setCenter(new OpenLayers.LonLat(50.04, 9.68), 4);
        vectorLayer.addFeatures([pointFeature, polygonFeature, multiFeature, labelOffsetFeature, nullFeature ]);

        this.log("################### 02: addInstitutionsLayer");
      },

      applyConfiguration : function(config) {
        for (var key in config) {
          if (config.hasOwnProperty(key)) {
            this[key] = config[key];
          }
        }
      },
      
      log : function(text){
        if("console" in window && "log" in window.console){
          console.log(text);
        }
      },

  });  
  
  
  
  
  var map = new DDBMap();
  map.display({});
  map.addInstitutionsLayer();
  
});