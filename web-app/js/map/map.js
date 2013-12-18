
$(document).ready(function() {
  
  DDBMap = function() {
    this.init();
  }

  /** Capsulate main logic in object * */
  $.extend(DDBMap.prototype, {

      /** Configuration * */
      rootDivId: "ddb-map",
      initLat: 51.55,
      initLon: 10.00,
      initZoom: 5,
      tileServerUrls: ["http://a.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://b.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png", "http://c.tile.maps.deutsche-digitale-bibliothek.de/${z}/${x}/${y}.png"],
      imageFolder: jsContextPath+"/js/map/img/",
      themeFolder: jsContextPath+"/js/vendor/openlayers-2.13.1/theme/default/style.css",
      osmMap: null,
      vectorLayer: null,
      fromProjection: new OpenLayers.Projection("EPSG:4326"),   // Transform from WGS 1984
      toProjection: new OpenLayers.Projection("EPSG:900913"), // to Spherical Mercator Projection
      apiInstitutionsUrl: "/apis/institutionsmap?clusterid=-1",
      apiClusteredInstitutionsUrl: "/apis/clusteredInstitutionsmap",
      clusters: null,
      waitingLayer: null,
      institutionList: null,


      /** Initialization * */
      init : function() {
      },

      display : function(config) {
        var self = this;
        
        this._applyConfiguration(config);
        
        var rootDiv = $("#"+this.rootDivId);
        if(rootDiv.length > 0){

          //Set the base folder for images
          OpenLayers.ImgPath = this.imageFolder;
          
          //Initialize Map
          var options = {
            theme: this.themeFolder, 
            projection: "EPSG:900913"
          };
          this.osmMap = new OpenLayers.Map(this.rootDivId, options);
          this.osmMap.displayProjection = this.fromProjection;

          //Add controls to map
          this.osmMap.addControlToMap(new OpenLayers.Control.Navigation(), new OpenLayers.Pixel(0,0));
          this.osmMap.addControlToMap(new OpenLayers.Control.DDBPanZoomBar({"zoomBarOffsetTop": 17, "zoomStopHeight": 7}), new OpenLayers.Pixel(5,-25));
          this.osmMap.addControlToMap(new OpenLayers.Control.Attribution());
          this.osmMap.addControlToMap(new OpenLayers.Control.DDBHome(this.imageFolder, this), new OpenLayers.Pixel(150,150));
          
          //Set the tiles data provider
          //var tiles = new OpenLayers.Layer.OSM("DDB tile server layer", this.tileServerUrls, {numZoomLevels: 19});
          var tiles = new OpenLayers.Layer.OSM();
          this.osmMap.addLayer(tiles);
          
          //Adds the waiting overlay
          this._createWaitingLayer();
          
          //Centers and zooms the map to the initial point
          var position = this._getLonLat(this.initLon, this.initLat);   
          this.osmMap.setCenter(position, this.initZoom); 

          //Add the institutions vector layer
          this._addInstitutionsLayer();

          //Add the popup functionality to the institutions layer
          this._addInstitutionsClickListener();

          //Register a zoom listener
          this.osmMap.events.register("zoomend", null, function(event){
            self._drawClustersOnMap();
          });
          
          //Register a load tiles finished event listener
          function onTilesLoaded() { //on load finished
            
            //Show the waiting layer 
            self._showWaitingLayer();
            
//            self._loadClusteredInstitutionList(function() { //on clusters loaded
//              
//              //Draws the institutions on the vector layer
//              self._drawClustersOnMap();
//  
//              //Hide the waiting layer again
//              self._hideWaitingLayer();
//  
//              //Remove the tiles load listener again. We only want it on initialization.
//              tiles.events.unregister("loadend", tiles, onTilesLoaded);
//            });
            
            //Loads all institutions over ajax
            self._loadFullInstitutionList(function() { //on build model finished

              //Draws the institutions on the vector layer
              self._drawClustersOnMap();

              //Hide the waiting layer again
              self._hideWaitingLayer();

              //Remove the tiles load listener again. We only want it on initialization.
              tiles.events.unregister("loadend", tiles, onTilesLoaded);
            });
          }
          tiles.events.register("loadend", tiles, onTilesLoaded);   
          
        }
        
      },
      
      applyFilters : function() {
        var self = this;
        
//        //Show the waiting layer 
//        self._showWaitingLayer();
//          
//        self._loadClusteredInstitutionList(function(){
//          
//          //Draws the institutions on the vector layer
//          self._drawClustersOnMap();
//  
//          //Hide the waiting layer again
//          self._hideWaitingLayer();
//          
//        });
        
            
        //Show the waiting layer 
        self._showWaitingLayer();

        window.setTimeout(function(){ //Give the browser time to display waiting gif
          self._buildModel(self.osmMap, self.institutionList, function() { //on build model finished

            //Draws the institutions on the vector layer
            self._drawClustersOnMap();

            //Hide the waiting layer again
            self._hideWaitingLayer();

          });
        }, 100);
      },
      
      
      _addInstitutionsLayer : function() {
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
        
      },

      _addInstitutionsClickListener : function(){
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
            self._getPopupContentHtml(institutionList),
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
      
      _applyConfiguration : function(config) {
        for (var key in config) {
          if (config.hasOwnProperty(key)) {
            this[key] = config[key];
          }
        }
      },
      
      _log : function(text){
        if("console" in window && "log" in window.console){
          console.log(text);
        }
      },
      
      _getLonLat : function(lon, lat) {
        return new OpenLayers.LonLat(lon, lat).transform(this.fromProjection, this.toProjection);
      },

      _getPoint : function(lon, lat) {
        return new OpenLayers.Geometry.Point(lon, lat).transform(this.fromProjection, this.toProjection);
      },
      
      _getPopupContentHtml : function(dataObjectList) {
        var html = "";
        html += "<div class='olPopupDDBContent'>";
        html += "  <div class='olPopupDDBHeader'>";
        if(dataObjectList.length > 1){
          html += "    " + dataObjectList.length + " "+ messages.ddbnext.Institutions();
        }else{
          html += "    " + dataObjectList.length + " "+ messages.ddbnext.Institution();
        }
        html += "  </div>";
        html += "  <div class='olPopupDDBBody'>";
        html += "    <div class='olPopupDDBScroll' id='olPopupDDBScroll'>";
        html += "      <ul>";
        for(var i=0; i<dataObjectList.length; i++){
          var institutionItem = dataObjectList[i].description.node;
          html += "      <li>";
          html += "        <a href=" + jsContextPath + "/about-us/institutions/item/" + institutionItem.id + ">";
          html += "          "+institutionItem.name + " (" + messages.ddbnext[institutionItem.sector]() + ")";
          html += "        </a>";
          html += "      </li>";
        }
        html += "      </ul>";
        html += "    </div>";
        html += "  </div>";
        html += "</div>";
        return html;
      },
      
      _getSectorSelection : function() {
        var sectors = {};
        sectors['selected'] = [];
        sectors['deselected'] = [];
        $('.sector-facet').each(function() {
          var sectorData = {};
          sectorData['sector'] = $(this).find('input').data('sector');
          sectorData['name'] = $.trim($(this).children('label').text());
          if ($(this).find('input').is(':checked')) {
            sectors['selected'].push(sectorData);
          } else {
            sectors['deselected'].push(sectorData);
          }
        });
        return sectors;
      },

      _getSelectedSectors : function() {
        var sectors = [];
        $('.sector-facet').each(function() {
          var sector = $(this).find('input').data('sector');
          if ($(this).find('input').is(':checked')) {
            sectors.push(sector);
          }
        });
        return sectors;
      },

      
      _loadFullInstitutionList : function(onCompleteCallbackFunction) {
        var self = this;
        $.ajax({
          type : 'GET',
          dataType : 'json',
          async : true,
          url : jsContextPath + this.apiInstitutionsUrl,
          success : function(institutionList){
            self.institutionList = institutionList;
            self._buildModel(self.osmMap, institutionList, onCompleteCallbackFunction);
          }
        });
      },

      _loadClusteredInstitutionList : function(onCompleteCallbackFunction) {
        var self = this;
        
        var selectedSectors = this._getSelectedSectors();
        var selectedSectorsText = JSON.stringify(selectedSectors);
        
        $.ajax({
          type : 'GET',
          dataType : 'text',
          async : true,
          cache: true,
          url : jsContextPath + this.apiClusteredInstitutionsUrl+"?selectedSectors="+selectedSectorsText,
          success : function(dataText){
            var data = JSON.parse(dataText);
            self.clusters = data.circleSets;
            onCompleteCallbackFunction();
          }
        });
      },

      _buildModel : function(osmMap, institutionList, onCompleteCallbackFunction) {
        var self = this;
        
        GeoPublisher.GeoSubscribe('filter', this, function(filteredInstitutions) {
          
          transformedInstitutionList = self._transformFilteredInstitutions(filteredInstitutions)
          
          var options = {
            mapIndex: 0,
            circleGap: 0,
            circlePackings: true,
            binning: "generic",
            minimumRadius: 4,
            noBinningRadii: "dynamic",
            binCount: 10
          };
          var binning = new Binning(osmMap, options);
          binning.setObjects(transformedInstitutionList);
          var circles = binning.getSet().circleSets;

          self.clusters = circles;
          
          onCompleteCallbackFunction();
        });
        
        InstitutionsMapModel.prepareInstitutionsData(institutionList);

        var selectedSectors = this._getSectorSelection();
        InstitutionsMapModel.selectSectors(selectedSectors);

    },
    
    
    _transformFilteredInstitutions : function(datasets) {
      var mapObjects = [];
      for (var i = 0; i < datasets.length; i++) {
        mapObjects.push(datasets[i].objects);
      }

      return mapObjects;
    },
    
    _drawClustersOnMap : function() {
      this.vectorLayer.removeAllFeatures();
      if(this.clusters != null) {
        
        var zoomLevel = this.osmMap.getZoom();
        if(this.clusters[zoomLevel] != null) {
          
          var clustersToDisplay = this.clusters[zoomLevel][0];
    
          var institutionCollections = [];
          for(var i=0;i<clustersToDisplay.length; i++){
            var clusterItem = clustersToDisplay[i];
            var lon = clusterItem.originX;
            var lat = clusterItem.originY;
            var radius = clusterItem.radius;
    
            var point = new OpenLayers.Geometry.Point(lon, lat);
            var institutionCollection = new OpenLayers.Feature.Vector(point, {radius: radius, institutions: clusterItem.elements});
            
            institutionCollections.push(institutionCollection);
          }
    
          this.vectorLayer.addFeatures(institutionCollections);
        }
      }
    },

    _createWaitingLayer : function(){
      var mapDiv = $("#"+this.rootDivId);
      
      //Create overlay div
      this.waitingLayer = $(document.createElement("div"));
      this.waitingLayer.addClass("osm-load-waiting");
      this.waitingLayer.addClass("off");
      
      //Create waiting img div
      var waitingImg = $(document.createElement("div"));
      waitingImg.addClass("osm-load-waiting-img");

      //Create transparancy div
      var transparancyDiv = $(document.createElement("div"));
      transparancyDiv.addClass("osm-load-waiting-div");
      
      
      //Join stuff
      this.waitingLayer.prepend(waitingImg);
      this.waitingLayer.prepend(transparancyDiv);
      mapDiv.prepend(this.waitingLayer);

    },
    
    _showWaitingLayer : function() {
      this.waitingLayer.removeClass("off");
    },

    _hideWaitingLayer : function() {
      this.waitingLayer.addClass("off");
    },

    refresh : function() {
      var self = this;
      
      //Loads all institutions over ajax
      this._loadFullInstitutionList(function(){
        
        //Draws the institutions on the vector layer
        self._drawClustersOnMap();
      });
    }

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
      
      ddbMap: null,

      initialize:function(imageFolder, ddbMap) {
        this.imageFolder = imageFolder;
        this.ddbMap = ddbMap;
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
              var position = this.ddbMap._getLonLat(this.ddbMap.initLon, this.ddbMap.initLat);
              this.map.setCenter(position, this.ddbMap.initZoom, false, true);
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
  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
  
  
  OpenLayers.Control.DDBPanZoomBar = OpenLayers.Class(OpenLayers.Control.PanZoom, {

      /** 
       * APIProperty: zoomStopWidth
       */
      zoomStopWidth: 18,

      /** 
       * APIProperty: zoomStopHeight
       */
      zoomStopHeight: 11,

      /** 
       * Property: slider
       */
      slider: null,

      /** 
       * Property: sliderEvents
       * {<OpenLayers.Events>}
       */
      sliderEvents: null,

      /** 
       * Property: zoombarDiv
       * {DOMElement}
       */
      zoombarDiv: null,

      /** 
       * APIProperty: zoomWorldIcon
       * {Boolean}
       */
      zoomWorldIcon: false,

      /**
       * APIProperty: panIcons
       * {Boolean} Set this property to false not to display the pan icons. If
       * false the zoom world icon is placed under the zoom bar. Defaults to
       * true.
       */
      panIcons: true,

      /**
       * APIProperty: forceFixedZoomLevel
       * {Boolean} Force a fixed zoom level even though the map has 
       *     fractionalZoom
       */
      forceFixedZoomLevel: false,

      /**
       * Property: mouseDragStart
       * {<OpenLayers.Pixel>}
       */
      mouseDragStart: null,

      /**
       * Property: deltaY
       * {Number} The cumulative vertical pixel offset during a zoom bar drag.
       */
      deltaY: null,

      /**
       * Property: zoomStart
       * {<OpenLayers.Pixel>}
       */
      zoomStart: null,
      
      zoomBarOffsetTop: 0,

      /**
       * Constructor: OpenLayers.Control.PanZoomBar
       */ 

      /**
       * APIMethod: destroy
       */
      destroy: function() {

          this._removeZoomBar();

          this.map.events.un({
              "changebaselayer": this.redraw,
              "updatesize": this.redraw,
              scope: this
          });

          OpenLayers.Control.PanZoom.prototype.destroy.apply(this, arguments);

          delete this.mouseDragStart;
          delete this.zoomStart;
      },
      
      /**
       * Method: setMap
       * 
       * Parameters:
       * map - {<OpenLayers.Map>} 
       */
      setMap: function(map) {
          OpenLayers.Control.PanZoom.prototype.setMap.apply(this, arguments);
          
          if (this.outsideViewport) {
              this.events.attachToElement(this.div);
          }

          this.map.events.on({
              "changebaselayer": this.redraw,
              "updatesize": this.redraw,
              scope: this
          });
      },

      /** 
       * Method: redraw
       * clear the div and start over.
       */
      redraw: function() {
          if (this.div != null) {
              this.removeButtons();
              this._removeZoomBar();
          }  
          this.draw();
      },
      
      /**
      * Method: draw 
      *
      * Parameters:
      * px - {<OpenLayers.Pixel>} 
      */
      draw: function(px) {
          // initialize our internal div
          OpenLayers.Control.prototype.draw.apply(this, arguments);
          px = this.position.clone();

          // place the controls
          this.buttons = [];

          var sz = {w: 18, h: 18};
          if (this.panIcons) {
              var centered = new OpenLayers.Pixel(px.x+sz.w/2, px.y);
              var wposition = sz.w;

              if (this.zoomWorldIcon) {
                  centered = new OpenLayers.Pixel(px.x+sz.w, px.y);
              }

              this._addButton("panup", "north-mini.png", centered, sz);
              px.y = centered.y+sz.h;
              this._addButton("panleft", "west-mini.png", px, sz);
              if (this.zoomWorldIcon) {
                  this._addButton("zoomworld", "zoom-world-mini.png", px.add(sz.w, 0), sz);

                  wposition *= 2;
              }
              this._addButton("panright", "east-mini.png", px.add(wposition, 0), sz);
              this._addButton("pandown", "south-mini.png", centered.add(0, sz.h*2), sz);
              this._addButton("zoomin", "zoom-plus-mini.png", centered.add(0, sz.h*3+5), sz);
              centered = this._addZoomBar(centered.add(0, sz.h*4 + 5));
              this._addButton("zoomout", "zoom-minus-mini.png", centered, sz);
          }
          else {
              this._addButton("zoomin", "zoom-plus-mini.png", px, sz);
              centered = this._addZoomBar(px.add(0, sz.h));
              this._addButton("zoomout", "zoom-minus-mini.png", centered, sz);
              if (this.zoomWorldIcon) {
                  centered = centered.add(0, sz.h+3);
                  this._addButton("zoomworld", "zoom-world-mini.png", centered, sz);
              }
          }
          return this.div;
      },

      /** 
      * Method: _addZoomBar
      * 
      * Parameters:
      * centered - {<OpenLayers.Pixel>} where zoombar drawing is to start.
      */
      _addZoomBar:function(centered) {
          var imgLocation = OpenLayers.Util.getImageLocation("slider.png");
          var id = this.id + "_" + this.map.id;
          var minZoom = this.map.getMinZoom();
          var zoomsToEnd = this.map.getNumZoomLevels() - 1 - this.map.getZoom();
          var slider = OpenLayers.Util.createAlphaImageDiv(id,
                         centered.add(-1, zoomsToEnd * this.zoomStopHeight), 
                         {w: 20, h: 9},
                         imgLocation,
                         "absolute");
          slider.style.cursor = "move";
          this.slider = slider;
          
          this.sliderEvents = new OpenLayers.Events(this, slider, null, true,
                                              {includeXY: true});
          this.sliderEvents.on({
              "touchstart": this.zoomBarDown,
              "touchmove": this.zoomBarDrag,
              "touchend": this.zoomBarUp,
              "mousedown": this.zoomBarDown,
              "mousemove": this.zoomBarDrag,
              "mouseup": this.zoomBarUp
          });
          
          var sz = {
              w: this.zoomStopWidth,
              h: this.zoomStopHeight * (this.map.getNumZoomLevels() - minZoom)
          };
          var imgLocation = OpenLayers.Util.getImageLocation("zoombar.png");
          var div = null;
          
          if (OpenLayers.Util.alphaHack()) {
              var id = this.id + "_" + this.map.id;
              div = OpenLayers.Util.createAlphaImageDiv(id, centered,
                                        {w: sz.w, h: this.zoomStopHeight},
                                        imgLocation,
                                        "absolute", null, "crop");
              div.style.height = sz.h + "px";
          } else {
              div = OpenLayers.Util.createDiv(
                          'OpenLayers_Control_DDBPanZoomBar_Zoombar' + this.map.id,
                          centered,
                          sz,
                          imgLocation);
          }
          div.style.cursor = "pointer";
          div.className = "olButton";
          this.zoombarDiv = div;
          
          this.div.appendChild(div);

          this.startTop = parseInt(div.style.top);
          this.div.appendChild(slider);

          this.map.events.register("zoomend", this, this.moveZoomBar);

          centered = centered.add(0, 
              this.zoomStopHeight * (this.map.getNumZoomLevels() - minZoom));
          return centered; 
      },
      
      /**
       * Method: _removeZoomBar
       */
      _removeZoomBar: function() {
          this.sliderEvents.un({
              "touchstart": this.zoomBarDown,
              "touchmove": this.zoomBarDrag,
              "touchend": this.zoomBarUp,
              "mousedown": this.zoomBarDown,
              "mousemove": this.zoomBarDrag,
              "mouseup": this.zoomBarUp
          });
          this.sliderEvents.destroy();
          
          this.div.removeChild(this.zoombarDiv);
          this.zoombarDiv = null;
          this.div.removeChild(this.slider);
          this.slider = null;
          
          this.map.events.unregister("zoomend", this, this.moveZoomBar);
      },
      
      /**
       * Method: onButtonClick
       *
       * Parameters:
       * evt - {Event}
       */
      onButtonClick: function(evt) {
          OpenLayers.Control.PanZoom.prototype.onButtonClick.apply(this, arguments);
          if (evt.buttonElement === this.zoombarDiv) {
              var levels = evt.buttonXY.y / this.zoomStopHeight;
              if(this.forceFixedZoomLevel || !this.map.fractionalZoom) {
                  levels = Math.floor(levels);
              }    
              var zoom = (this.map.getNumZoomLevels() - 1) - levels; 
              zoom = Math.min(Math.max(zoom, 0), this.map.getNumZoomLevels() - 1);
              this.map.zoomTo(zoom);
          }
      },
      
      /**
       * Method: passEventToSlider
       * This function is used to pass events that happen on the div, or the map,
       * through to the slider, which then does its moving thing.
       *
       * Parameters:
       * evt - {<OpenLayers.Event>} 
       */
      passEventToSlider:function(evt) {
          this.sliderEvents.handleBrowserEvent(evt);
      },
      
      /*
       * Method: zoomBarDown
       * event listener for clicks on the slider
       *
       * Parameters:
       * evt - {<OpenLayers.Event>} 
       */
      zoomBarDown:function(evt) {
          if (!OpenLayers.Event.isLeftClick(evt) && !OpenLayers.Event.isSingleTouch(evt)) {
              return;
          }
          var target = this.outsideViewport ? this : this.map;
          target.events.on({
              touchmove: this.passEventToSlider,
              mousemove: this.passEventToSlider,
              mouseup: this.passEventToSlider,
              scope: this
          });
          
          this.mouseDragStart = evt.xy.clone();
          this.zoomStart = evt.xy.clone();
          this.div.style.cursor = "move";
          // reset the div offsets just in case the div moved
          this.zoombarDiv.offsets = null; 
          OpenLayers.Event.stop(evt);
      },
      
      /*
       * Method: zoomBarDrag
       * This is what happens when a click has occurred, and the client is
       * dragging.  Here we must ensure that the slider doesn't go beyond the
       * bottom/top of the zoombar div, as well as moving the slider to its new
       * visual location
       *
       * Parameters:
       * evt - {<OpenLayers.Event>} 
       */
      zoomBarDrag:function(evt) {
          if (this.mouseDragStart != null) {
              var deltaY = this.mouseDragStart.y - evt.xy.y;
              var offsets = OpenLayers.Util.pagePosition(this.zoombarDiv);
              if ((evt.clientY - offsets[1]) > 0 && 
                  (evt.clientY - offsets[1]) < parseInt(this.zoombarDiv.style.height) - 2) {
                  var newTop = parseInt(this.slider.style.top) - deltaY;
                  this.slider.style.top = newTop+"px";
                  this.mouseDragStart = evt.xy.clone();
              }
              // set cumulative displacement
              this.deltaY = this.zoomStart.y - evt.xy.y;
              OpenLayers.Event.stop(evt);
          }
      },
      
      /*
       * Method: zoomBarUp
       * Perform cleanup when a mouseup event is received -- discover new zoom
       * level and switch to it.
       *
       * Parameters:
       * evt - {<OpenLayers.Event>} 
       */
      zoomBarUp:function(evt) {
          if (!OpenLayers.Event.isLeftClick(evt) && evt.type !== "touchend") {
              return;
          }
          if (this.mouseDragStart) {
              this.div.style.cursor="";
              var target = this.outsideViewport ? this : this.map;
              target.events.un({
                  "touchmove": this.passEventToSlider,
                  "mouseup": this.passEventToSlider,
                  "mousemove": this.passEventToSlider,
                  scope: this
              });
              var zoomLevel = this.map.zoom;
              if (!this.forceFixedZoomLevel && this.map.fractionalZoom) {
                  zoomLevel += this.deltaY/this.zoomStopHeight;
                  zoomLevel = Math.min(Math.max(zoomLevel, 0), 
                                       this.map.getNumZoomLevels() - 1);
              } else {
                  zoomLevel += this.deltaY/this.zoomStopHeight;
                  zoomLevel = Math.max(Math.round(zoomLevel), 0);      
              }
              this.map.zoomTo(zoomLevel);
              this.mouseDragStart = null;
              this.zoomStart = null;
              this.deltaY = 0;
              OpenLayers.Event.stop(evt);
          }
      },
      
      /*
      * Method: moveZoomBar
      * Change the location of the slider to match the current zoom level.
      */
      moveZoomBar:function() {
          var newTop = 
              ((this.map.getNumZoomLevels()-1) - this.map.getZoom()) * 
              this.zoomStopHeight + this.startTop + 1 + this.zoomBarOffsetTop;
          this.slider.style.top = newTop + "px";
      },    
      
      CLASS_NAME: "OpenLayers.Control.DDBPanZoomBar"
  });  
  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
  

  
  var map = new DDBMap();
  map.display({});
  
  $('.sector-facet input').each(function() {
    $(this).click(function(){
      map.applyFilters();
    });
  });

  
});