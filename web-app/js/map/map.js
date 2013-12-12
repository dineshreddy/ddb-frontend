
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
          this.osmMap.addControlToMap(new OpenLayers.Control.DDBHome(this.imageFolder), new OpenLayers.Pixel(150,150));
          
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
        
        var institutionCircleStyle = new OpenLayers.Style({
          strokeColor: "#A5003B",
          strokeOpacity: 1,
          strokeWidth: 3,
          fillColor: "#EF7E89",
          fillOpacity: 0.5,
          pointRadius: "${radius}",
          pointerEvents: "visiblePainted",
        });
        
        this.vectorLayer = new OpenLayers.Layer.Vector("Institutions", {
          styleMap: new OpenLayers.StyleMap({
            "default": institutionCircleStyle, 
            "select": institutionCircleStyle
          }),
          renderers: renderer
        });
        this.osmMap.addLayer(this.vectorLayer);
        
        // Variant 1 to add points
        var institutions1 = [
          {id: "DGD2452DFGDHN23dBSDRS242SDFV", name: "Goethe Museum", type: "Museum"},
          {id: "EGD2452DFGDHN23dBSDRS242SDFV", name: "Faust Museum", type: "Museum"},
          {id: "FGD2452DFGDHN23dBSDRS242SDFV", name: "Nietzsche Museum", type: "Museum"}
        ];
        var institutionCollection1 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 0, this.initLat + 0), {radius: 4, institutions: institutions1});
        var institutions2 = [
          {id: "ASDVASRG3456236521DFGBSDFV34", name: "Faust Archiv", type: "Archiv"}
        ];
        var institutionCollection2 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 1, this.initLat + 2), {radius: 8, institutions: institutions2});
        var institutions3 = [
          {id: "3462ASDGSDFHSDFG4562BSDFG47V", name: "Naturkundemuseum", type: "Museum"},
          {id: "4523ASDGSDFHSDFG4562BSDFG47V", name: "Archivalien", type: "Archiv"},
          {id: "5672ASDGSDFHSDFG4562BSDFG47V", name: "Lesezirkel Süd", type: "Bibliothek"},
          {id: "6782ASDGSDFHSDFG4562BSDFG47V", name: "Universalmuseum Neckarsulm-Buxtehude", type: "Museum"},
          {id: "8462ASDGSDFHSDFG4562BSDFG47V", name: "Freelancereimuseum", type: "Museum"},
          {id: "9462ASDGSDFHSDFG4562BSDFG47V", name: "Imperiale Droidenmuseum", type: "Museum"},
          {id: "1062ASDGSDFHSDFG4562BSDFG47V", name: "Museum dies sein muss", type: "Archiv"},
          {id: "1162ASDGSDFHSDFG4562BSDFG47V", name: "Richtig sein Archiv dies", type: "Archiv"},
          {id: "1262ASDGSDFHSDFG4562BSDFG47V", name: "Naturalienmuseum", type: "Museum"}
        ];
        var institutionCollection3 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 3, this.initLat + 1), {radius: 12, institutions: institutions3});

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
          var institutionList = feature.data.institutions;
          
          var popup = new OpenLayers.Popup.FramedDDB(
            "institutionPopup", 
            feature.geometry.getBounds().getCenterLonLat(),
            new OpenLayers.Size(315,100),
            self.getContentHtml(institutionList),
            null, 
            true, 
            onPopupClose, 
            self.imageFolder);
          
          feature.popup = popup;
          popup.feature = feature;
          self.osmMap.addPopup(popup, true);
        };
        
        function onFeatureUnselect(event) {
          feature = event.feature;
          var popup = feature.popup;
          if (feature.popup) {
              popup.feature = null;
              self.osmMap.removePopup(popup);
              popup.destroy();
              feature.popup = null;
          }
        };
        
        function onPopupClose(event) {
          var feature = this.feature;
          if (feature.popup) {
            selectionEventControl.unselect(feature);
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
      
      getContentHtml : function(institutionList) {
        var html = "";
        html += "<div class='olPopupDDBContent'>";
        html += "  <div class='olPopupDDBHeader'>";
        html += "    " + institutionList.length + " Institutionen";
        html += "  </div>";
        html += "  <div class='olPopupDDBBody'>";
        html += "    <div class='olPopupDDBScroll'>";
        html += "      <ul>";
        for(var i=0; i<institutionList.length; i++){
          html += "      <li>";
          html += "        "+institutionList[i].name + " (" + institutionList[i].type + ")";
          html += "      </li>";
        }
        html += "      </ul>";
        html += "    </div>";
        html += "  </div>";
        html += "</div>";
        return html;
      },
      
      

  });  
  
  

  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  

  
  
  /**
   * Class: OpenLayers.Popup.FramedDDB
   * 
   * Inherits from: 
   *  - <OpenLayers.Popup.Framed>
   */
  OpenLayers.Popup.FramedDDB = 
    OpenLayers.Class(OpenLayers.Popup.Framed, {

      /** 
       * Property: contentDisplayClass
       * {String} The CSS class of the popup content div.
       */
      contentDisplayClass: "olFramedDDBPopupContent",

      /**
       * APIProperty: autoSize
       * {Boolean} Framed Cloud is autosizing by default.
       */
      autoSize: true,

      /**
       * APIProperty: panMapIfOutOfView
       * {Boolean} Framed Cloud does pan into view by default.
       */
      panMapIfOutOfView: true,

      /**
       * APIProperty: imageSize
       * {<OpenLayers.Size>}
       */
      imageSize: new OpenLayers.Size(26, 29),

      /**
       * APIProperty: isAlphaImage
       * {Boolean} The FramedCloud does not use an alpha image (in honor of the 
       *     good ie6 folk out there)
       */
      isAlphaImage: false,

      /** 
       * APIProperty: fixedRelativePosition
       * {Boolean} The Framed Cloud popup works in just one fixed position.
       */
      fixedRelativePosition: false,

      /**
       * Property: positionBlocks
       * {Object} Hash of differen position blocks, keyed by relativePosition
       *     two-character code string (ie "tl", "tr", "bl", "br")
       */
      positionBlocks: {
        "tl": {
          'offset': new OpenLayers.Pixel(20, 5),
          'padding': new OpenLayers.Bounds(15, 15, 15, 15),
          'blocks': [
              { // top-left
                  size: new OpenLayers.Size('auto', 'auto'),
                  anchor: new OpenLayers.Bounds(0, 51, 22, 0),
                  position: new OpenLayers.Pixel(-9999, -9999)
              },
              { //top-right
                  size: new OpenLayers.Size(22, 'auto'),
                  anchor: new OpenLayers.Bounds(null, 50, 0, 0),
                  position: new OpenLayers.Pixel(-9999, -9999)
              },
              { //bottom-left
                  size: new OpenLayers.Size('auto', 19),
                  anchor: new OpenLayers.Bounds(0, 32, 22, null),
                  position: new OpenLayers.Pixel(-9999, -9999)
              },
              { //bottom-right
                  size: new OpenLayers.Size(25, 25),
                  anchor: new OpenLayers.Bounds(null, 5, 20, null), //left, bottom, right, top
                  position: new OpenLayers.Pixel(0, 0)
              },
              { // stem
                  size: new OpenLayers.Size(0, 0),
                  anchor: new OpenLayers.Bounds(null, 0, 0, null),
                  position: new OpenLayers.Pixel(0, 0)
              }
          ]
        },
        "tr": {
            'offset': new OpenLayers.Pixel(-5, 5),
            'padding': new OpenLayers.Bounds(15, 15, 15, 15),
            'blocks': [
                { // top-left
                    size: new OpenLayers.Size('auto', 'auto'),
                    anchor: new OpenLayers.Bounds(0, 51, 22, 0),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //top-right
                    size: new OpenLayers.Size(22, 'auto'),
                    anchor: new OpenLayers.Bounds(null, 50, 0, 0),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //bottom-left
                    size: new OpenLayers.Size('auto', 25),
                    anchor: new OpenLayers.Bounds(5, 5, 0, null), //left, bottom, right, top
                    position: new OpenLayers.Pixel(0, 0)
                },
                { //bottom-right
                    size: new OpenLayers.Size(22, 19),
                    anchor: new OpenLayers.Bounds(null, 32, 0, null),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { // stem
                    size: new OpenLayers.Size(81, 35),
                    anchor: new OpenLayers.Bounds(0, 0, null, null),
                    position: new OpenLayers.Pixel(-215, -687)
                }
            ]
        },
        "bl": {
            'offset': new OpenLayers.Pixel(22, -2),
            'padding': new OpenLayers.Bounds(15, 15, 15, 15),
            'blocks': [
                { // top-left
                    size: new OpenLayers.Size('auto', 'auto'),
                    anchor: new OpenLayers.Bounds(0, 21, 22, 32),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //top-right
                    size: new OpenLayers.Size(25, 'auto'),
                    anchor: new OpenLayers.Bounds(null, 20, 22, 0), //left, bottom, right, top
                    position: new OpenLayers.Pixel(0, 0)
                },
                { //bottom-left
                    size: new OpenLayers.Size('auto', 21),
                    anchor: new OpenLayers.Bounds(0, 0, 22, null),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //bottom-right
                    size: new OpenLayers.Size(22, 21),
                    anchor: new OpenLayers.Bounds(null, 0, 0, null),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { // stem
                    size: new OpenLayers.Size(81, 33),
                    anchor: new OpenLayers.Bounds(null, null, 0, 0),
                    position: new OpenLayers.Pixel(-101, -674)
                }
            ]
        },
        "br": {
            'offset': new OpenLayers.Pixel(-5, -3),
            'padding': new OpenLayers.Bounds(15, 15, 15, 15),
            'blocks': [
                { // top-left
                    size: new OpenLayers.Size('auto', 'auto'),
                    anchor: new OpenLayers.Bounds(5, 0, 0, 0), //left, bottom, right, top
                    position: new OpenLayers.Pixel(0, 0)
                },
                { //top-right
                    size: new OpenLayers.Size(22, 'auto'),
                    anchor: new OpenLayers.Bounds(null, 21, 0, 32),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //bottom-left
                    size: new OpenLayers.Size('auto', 21),
                    anchor: new OpenLayers.Bounds(0, 0, 22, null),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //bottom-right
                    size: new OpenLayers.Size(22, 21),
                    anchor: new OpenLayers.Bounds(null, 0, 0, null),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { // stem
                    size: new OpenLayers.Size(81, 33),
                    anchor: new OpenLayers.Bounds(0, null, null, 0),
                    position: new OpenLayers.Pixel(-311, -674)
                }
            ]
        }
      },

      /**
       * APIProperty: minSize
       * {<OpenLayers.Size>}
       */
      minSize: new OpenLayers.Size(105, 10),

      /**
       * APIProperty: maxSize
       * {<OpenLayers.Size>}
       */
      maxSize: new OpenLayers.Size(1200, 660),
      
      imageSrc: null,

      /** 
       * Constructor: OpenLayers.Popup.FramedCloud
       * 
       * Parameters:
       * id - {String}
       * lonlat - {<OpenLayers.LonLat>}
       * contentSize - {<OpenLayers.Size>}
       * contentHTML - {String}
       * anchor - {Object} Object to which we'll anchor the popup. Must expose 
       *     a 'size' (<OpenLayers.Size>) and 'offset' (<OpenLayers.Pixel>) 
       *     (Note that this is generally an <OpenLayers.Icon>).
       * closeBox - {Boolean}
       * closeBoxCallback - {Function} Function to be called on closeBox click.
       */
      initialize:function(id, lonlat, contentSize, contentHTML, anchor, closeBox, 
                          closeBoxCallback, imageSrc) {

          //this.imageSrc = OpenLayers.Util.getImageLocation('cloud-popup-relative.png');
          OpenLayers.Popup.Framed.prototype.initialize.apply(this, arguments);
          this.contentDiv.className = this.contentDisplayClass;
          
          this.imageSrc = imageSrc;
          
          this.contentDiv.className = this.contentDisplayClass;

      },

      /**
       * Method: createBlocks
       */
      createBlocks: function() {
          this.blocks = [];

          //since all positions contain the same number of blocks, we can 
          // just pick the first position and use its blocks array to create
          // our blocks array
          var firstPosition = null;
          for(var key in this.positionBlocks) {
              firstPosition = key;
              break;
          }
          
          var position = this.positionBlocks[firstPosition];
          for (var i = 0; i < position.blocks.length; i++) {

              var block = {};
              this.blocks.push(block);

              var divId = this.id + '_FrameDecorationDiv_' + i;
              block.div = OpenLayers.Util.createDiv(divId, 
                  null, null, null, "absolute", null, "hidden", null
              );

              var imgId = this.id + '_FrameDecorationImg_' + i;
              var imageCreator = 
                  (this.isAlphaImage) ? OpenLayers.Util.createAlphaImageDiv
                                      : OpenLayers.Util.createImage;

              var imageUrl = this.imageSrc + imgId + ".png";
              block.image = imageCreator(imgId, null, this.imageSize, imageUrl, "absolute", null, null, null);

              block.div.appendChild(block.image);
              this.groupDiv.appendChild(block.div);
          }
      },

      CLASS_NAME: "OpenLayers.Popup.FramedDDB"
  });
  
  
  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
  
  
  OpenLayers.Control.DDBHome = OpenLayers.Class(OpenLayers.Control, {
      
      /**
       * APIProperty: zoomInId
       * {String}
       * Instead of having the control create a zoom in link, you can provide 
       *     the identifier for an anchor element already added to the document.
       *     By default, an element with id "olZoomInLink" will be searched for
       *     and used if it exists.
       */
      ddbHomeId: "olDDBHomeLink",

      ddbHomeImg: "ddb_ResetMap.png",
      
      imageFolder: "",

      initialize:function(imageFolder) {
        this.imageFolder = imageFolder;
      },
  
      /**
       * Method: draw
       *
       * Returns:
       * {DOMElement} A reference to the DOMElement containing the zoom links.
       */
      draw: function() {
          var div = OpenLayers.Control.prototype.draw.apply(this),
              links = this.getOrCreateLinks(div),
              ddbHome = links.ddbHome,
              eventsInstance = this.map.events;
          
          eventsInstance.register("buttonclick", this, this.onDDBHomeClick);
          
          this.ddbHome = ddbHome;
          $(div).addClass("olControlDDBHome");
          return div;
      },
      
      /**
       * Method: getOrCreateLinks
       * 
       * Parameters:
       * el - {DOMElement}
       *
       * Return: 
       * {Object} Object with zoomIn and zoomOut properties referencing links.
       */
      getOrCreateLinks: function(el) {
          var ddbHome = document.getElementById(this.ddbHome);
          if (!ddbHome) {
            ddbHome = document.createElement("a");
            ddbHome.href = "#ddbHome";
            var ddbHomeImg = document.createElement("img");
            $(ddbHomeImg).attr("src", this.imageFolder + this.ddbHomeImg);
            $(ddbHomeImg).addClass("olDDBHomeImg");
            ddbHome.appendChild(ddbHomeImg);
            ddbHome.className = "olControlDDBLink";
              el.appendChild(ddbHome);
          }
          OpenLayers.Element.addClass(ddbHome, "olButton");
          
          return {
            ddbHome: ddbHome
          };
      },
      
      /**
       * Method: onZoomClick
       * Called when zoomin/out link is clicked.
       */
      onDDBHomeClick: function(evt) {
          var button = evt.buttonElement;
          if (button === this.ddbHome) {
              this.map.zoomIn();
              //TODO
          } 
      },
  
      /** 
       * Method: destroy
       * Clean up.
       */
      destroy: function() {
          if (this.map) {
              this.map.events.unregister("buttonclick", this, this.onDDBHomeClick);
          }
          delete this.ddbHomeLink;
          OpenLayers.Control.prototype.destroy.apply(this);
      },
  
      CLASS_NAME: "OpenLayers.Control.DDBHome"
  });  
  
  
  
  var map = new DDBMap();
  map.display({});
  map.addInstitutionsLayer();
  map.addInstitutionsClickListener();
  
});