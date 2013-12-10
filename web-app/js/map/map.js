
$(document).ready(function() {
  
  DDBMap = function() {
    this.init();
  }

  /** Capsulate main logic in object * */
  $.extend(DDBMap.prototype, {

      /** Configuration * */
      rootDivId: "ddb-map",
      initLat: 49.1,
      initLon: 8.24,
      initZoom: 5,
      tileServerUrls: ["http://a.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://b.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://c.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png"],
      imageFolder: jsContextPath+"/js/map/img/",
      themeFolder: jsContextPath+"/js/vendor/openlayers-2.13.1/theme/default/style.css",
      osmMap: null,
      vectorLayer: null,
      fromProjection: new OpenLayers.Projection("EPSG:4326"),   // Transform from WGS 1984
      toProjection: new OpenLayers.Projection("EPSG:900913"), // to Spherical Mercator Projection


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
          };
          this.osmMap = new OpenLayers.Map(this.rootDivId, options);
          this.osmMap.addControlToMap(new OpenLayers.Control.Navigation(), new OpenLayers.Pixel(0,0));
          this.osmMap.addControlToMap(new OpenLayers.Control.PanZoomBar(), new OpenLayers.Pixel(5,-25));
          this.osmMap.addControlToMap(new OpenLayers.Control.Attribution());
          
          var tiles          = new OpenLayers.Layer.OSM("DDB tile server layer", this.tileServerUrls, {numZoomLevels: 19});
          //var tiles          = new OpenLayers.Layer.OSM();
          var position       = this.getLonLat(this.initLon, this.initLat);
          var zoom           = this.initZoom; 
   
          this.osmMap.addLayer(tiles);
          this.osmMap.setCenter(position, zoom); 
          
        }
        
      },
      
      addInstitutionsLayer : function() {
        var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
        renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;
        
        this.vectorLayer = new OpenLayers.Layer.Vector("Institutions", {
          styleMap: new OpenLayers.StyleMap({'default':{
              strokeColor: "#A5003B",
              strokeOpacity: 1,
              strokeWidth: 3,
              fillColor: "#EF7E89",
              fillOpacity: 0.5,
              pointRadius: "${radius}",
              pointerEvents: "visiblePainted",
          }}),
          renderers: renderer
        });
        this.osmMap.addLayer(this.vectorLayer);
        
        // Variant 1 to add points
        var institution1 = {id: "DGD2452DFGDHN23dBSDRS242SDFV", name: "Goethe Museum"};
        var institutionCollection1 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 0, this.initLat + 0), {radius: 4, institution: institution1});
        var institution2 = {id: "ASDVASRG3456236521DFGBSDFV34", name: "Faust Archiv"};
        var institutionCollection2 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 1, this.initLat + 2), {radius: 8, institution: institution2});
        var institution3 = {id: "3462ASDGSDFHSDFG4562BSDFG47V", name: "Naturkundemuseum"};
        var institutionCollection3 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 3, this.initLat + 1), {radius: 12, institution: institution3});

        var institutionCollections = [institutionCollection1, institutionCollection2, institutionCollection3]
        
        this.vectorLayer.addFeatures(institutionCollections);
      },

      addInstitutionsClickListener : function(){
        var self = this;
        
        var selectionEventControl = new OpenLayers.Control.SelectFeature(this.vectorLayer);
        this.osmMap.addControl(selectionEventControl);
        selectionEventControl.activate();
        this.vectorLayer.events.on({
            'featureselected': onFeatureSelect,
            'featureunselected': onFeatureUnselect
        });          
        
        
        function onFeatureSelect(event) {
          var feature = event.feature;
          var institution = feature.data.institution;
          
          var popup = new OpenLayers.Popup.FramedCloud(
            "institutionPopup", 
            feature.geometry.getBounds().getCenterLonLat(),
            new OpenLayers.Size(100,100),
            "<h2>" + institution.name + "</h2>",
            null, 
            true, 
            this.onPopupClose);
          
          feature.popup = popup;
          popup.feature = feature;
          self.osmMap.addPopup(popup, true);
        };
        
        function onFeatureUnselect(event) {
//          feature = event.feature;
//          if (feature.popup) {
//              popup.feature = null;
//              self.osmMap.removePopup(feature.popup);
//              feature.popup.destroy();
//              feature.popup = null;
//          }
        };
        
        function onPopupClose(event) {
          // 'this' is the popup.
          var feature = this.feature;
          if (feature.layer) { 
              selectControl.unselect(feature);
          } else { // After "moveend" or "refresh" events on POIs layer all 
                   //     features have been destroyed by the Strategy.BBOX
              this.destroy();
          }
        };


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
      
      getLonLat : function(lon, lat) {
        return new OpenLayers.LonLat(lon, lat).transform(this.fromProjection, this.toProjection);
      },

      getPoint : function(lon, lat) {
        return new OpenLayers.Geometry.Point(lon, lat).transform(this.fromProjection, this.toProjection);
      },
      
      

  });  
  
  
  
  
  var map = new DDBMap();
  map.display({});
  map.addInstitutionsLayer();
  map.addInstitutionsClickListener();
  
});