
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
          this.osmMap.addControlToMap(new OpenLayers.Control.PanZoomBar(), new OpenLayers.Pixel(5,-25));
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
    
    applyFilters : function() {
      var self = this;
      
      //Show the waiting layer 
      self._showWaitingLayer();

      self._buildModel(self.osmMap, self.institutionList, function() { //on build model finished

        //Draws the institutions on the vector layer
        self._drawClustersOnMap();

        //Hide the waiting layer again
        self._hideWaitingLayer();

      });
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
  

  
  var map = new DDBMap();
  map.display({});
  
  $('.sector-facet input').each(function() {
    $(this).click(function(){
      map.applyFilters();
    });
  });

  
});