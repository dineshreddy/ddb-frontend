
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
        var institution1 = {id: "DGD2452DFGDHN23dBSDRS242SDFV", name: "Goethe Museum", type: "Museum"};
        var institutionCollection1 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 0, this.initLat + 0), {radius: 4, institution: institution1});
        var institution2 = {id: "ASDVASRG3456236521DFGBSDFV34", name: "Faust Archiv", type: "Archiv"};
        var institutionCollection2 = new OpenLayers.Feature.Vector(this.getPoint(this.initLon + 1, this.initLat + 2), {radius: 8, institution: institution2});
        var institution3 = {id: "3462ASDGSDFHSDFG4562BSDFG47V", name: "Naturkundemuseum", type: "Museum"};
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
          var institutionList = [feature.data.institution];
          
          var popup = new OpenLayers.Popup.FramedDDB(
            "institutionPopup", 
            feature.geometry.getBounds().getCenterLonLat(),
            new OpenLayers.Size(315,100),
            self.getContentHtml(institutionList),
            null, 
            true, 
            this.onPopupClose, 
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
      
      getContentHtml : function(institutionList) {
        var html = "";
        html += "<div class='olPopupDDBContent'>";
        html += "  <div class='olPopupDDBHeader'>";
        html += "    " + institutionList.length + " Institutionen";
        html += "  </div>";
        html += "  <div class='olPopupDDBContent'>";
        html += "    <ul>";
        for(var i=0; i<institutionList.length; i++){
          html += "    <li>";
          html += "      "+institutionList[i].name + "(" + institutionList[i].type + ")";
          html += "    </li>";
        }
        html += "    </ul>";
        html += "  </div>";
        html += "</div>";
        return html;
      },
      
      

  });  
  

  OpenLayers.Popup.FramedDDB =
    OpenLayers.Class(OpenLayers.Popup.Anchored, {

      /** 
       * Property: contentDisplayClass
       * {String} The CSS class of the popup content div.
       */
      contentDisplayClass: "olFramedDDBPopupContent",

      /**
       * Property: imageSrc
       * {String} location of the image to be used as the popup frame
       */
      imageSrc: null,

      /**
       * Property: imageSize
       * {<OpenLayers.Size>} Size (measured in pixels) of the image located
       *     by the 'imageSrc' property.
       */
      imageSize: null,

      /**
       * APIProperty: isAlphaImage
       * {Boolean} The image has some alpha and thus needs to use the alpha 
       *     image hack. Note that setting this to true will have no noticeable
       *     effect in FF or IE7 browsers, but will all but crush the ie6 
       *     browser. 
       *     Default is false.
       */
      isAlphaImage: false,

      /**
       * Property: positionBlocks
       * {Object} Hash of different position blocks (Object/Hashs). Each block 
       *     will be keyed by a two-character 'relativePosition' 
       *     code string (ie "tl", "tr", "bl", "br"). Block properties are 
       *     'offset', 'padding' (self-explanatory), and finally the 'blocks'
       *     parameter, which is an array of the block objects. 
       * 
       *     Each block object must have 'size', 'anchor', and 'position' 
       *     properties.
       * 
       *     Note that positionBlocks should never be modified at runtime.
       */
      positionBlocks: {
        "tl": {
          'offset': new OpenLayers.Pixel(20, 0),
          'padding': new OpenLayers.Bounds(10, 10, 10, 10),
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
                  anchor: new OpenLayers.Bounds(null, 0, 20, null), //left, bottom, right, top
                  position: new OpenLayers.Pixel(0, 0)
              },
              { // stem
                  size: new OpenLayers.Size(81, 35),
                  anchor: new OpenLayers.Bounds(null, 0, 0, null),
                  position: new OpenLayers.Pixel(0, -688)
              }
          ]
        },
        "tr": {
            'offset': new OpenLayers.Pixel(0, 0),
            'padding': new OpenLayers.Bounds(10, 10, 10, 10),
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
                    anchor: new OpenLayers.Bounds(0, 0, 0, null), //left, bottom, right, top
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
            'offset': new OpenLayers.Pixel(20, 0),
            'padding': new OpenLayers.Bounds(10, 10, 10, 10),
            'blocks': [
                { // top-left
                    size: new OpenLayers.Size('auto', 'auto'),
                    anchor: new OpenLayers.Bounds(0, 21, 22, 32),
                    position: new OpenLayers.Pixel(-9999, -9999)
                },
                { //top-right
                    size: new OpenLayers.Size(25, 'auto'),
                    anchor: new OpenLayers.Bounds(null, 20, 17, -3), //left, bottom, right, top
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
            'offset': new OpenLayers.Pixel(0, 0),
            'padding': new OpenLayers.Bounds(10, 10, 10, 10),
            'blocks': [
                { // top-left
                    size: new OpenLayers.Size('auto', 'auto'),
                    anchor: new OpenLayers.Bounds(0, 20, 0, -3), //left, bottom, right, top
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
       * Property: blocks
       * {Array[Object]} Array of objects, each of which is one "block" of the 
       *     popup. Each block has a 'div' and an 'image' property, both of 
       *     which are DOMElements, and the latter of which is appended to the 
       *     former. These are reused as the popup goes changing positions for
       *     great economy and elegance.
       */
      blocks: null,

      /** 
       * APIProperty: fixedRelativePosition
       * {Boolean} We want the framed popup to work dynamically placed relative
       *     to its anchor but also in just one fixed position. A well designed
       *     framed popup will have the pixels and logic to display itself in 
       *     any of the four relative positions, but (understandably), this will
       *     not be the case for all of them. By setting this property to 'true', 
       *     framed popup will not recalculate for the best placement each time
       *     it's open, but will always open the same way. 
       *     Note that if this is set to true, it is generally advisable to also
       *     set the 'panIntoView' property to true so that the popup can be 
       *     scrolled into view (since it will often be offscreen on open)
       *     Default is false.
       */
      fixedRelativePosition: false,

      /** 
       * Constructor: OpenLayers.Popup.Framed
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

          OpenLayers.Popup.Anchored.prototype.initialize.apply(this, arguments);

          if (this.fixedRelativePosition) {
              //based on our decided relativePostion, set the current padding
              // this keeps us from getting into trouble 
              this.updateRelativePosition();
              
              //make calculateRelativePosition always return the specified
              // fixed position.
              this.calculateRelativePosition = function(px) {
                  return this.relativePosition;
              };
          }

          this.contentDiv.style.position = "absolute";
          this.contentDiv.style.zIndex = 1;

          if (closeBox) {
              this.closeDiv.style.zIndex = 1;
          }

          this.groupDiv.style.position = "absolute";
          this.groupDiv.style.top = "0px";
          this.groupDiv.style.left = "0px";
          this.groupDiv.style.height = "100%";
          this.groupDiv.style.width = "100%";
          
          this.contentDiv.className = this.contentDisplayClass;
          
          this.imageSrc = imageSrc;
      },

      /** 
       * APIMethod: destroy
       */
      destroy: function() {
          this.imageSrc = null;
          this.imageSize = null;
          this.isAlphaImage = null;

          this.fixedRelativePosition = false;
          this.positionBlocks = null;

          //remove our blocks
          for(var i = 0; i < this.blocks.length; i++) {
              var block = this.blocks[i];

              if (block.image) {
                  block.div.removeChild(block.image);
              }
              block.image = null;

              if (block.div) {
                  this.groupDiv.removeChild(block.div);
              }
              block.div = null;
          }
          this.blocks = null;

          OpenLayers.Popup.Anchored.prototype.destroy.apply(this, arguments);
      },

      /**
       * APIMethod: setBackgroundColor
       */
      setBackgroundColor:function(color) {
          //does nothing since the framed popup's entire scheme is based on a 
          // an image -- changing the background color makes no sense. 
      },

      /**
       * APIMethod: setBorder
       */
      setBorder:function() {
          //does nothing since the framed popup's entire scheme is based on a 
          // an image -- changing the popup's border makes no sense. 
      },

      /**
       * Method: setOpacity
       * Sets the opacity of the popup.
       * 
       * Parameters:
       * opacity - {float} A value between 0.0 (transparent) and 1.0 (solid).   
       */
      setOpacity:function(opacity) {
          //does nothing since we suppose that we'll never apply an opacity
          // to a framed popup
      },

      /**
       * APIMethod: setSize
       * Overridden here, because we need to update the blocks whenever the size
       *     of the popup has changed.
       * 
       * Parameters:
       * contentSize - {<OpenLayers.Size>} the new size for the popup's 
       *     contents div (in pixels).
       */
      setSize:function(contentSize) { 
          OpenLayers.Popup.Anchored.prototype.setSize.apply(this, arguments);

          this.updateBlocks();
      },

      /**
       * Method: updateRelativePosition
       * When the relative position changes, we need to set the new padding 
       *     BBOX on the popup, reposition the close div, and update the blocks.
       */
      updateRelativePosition: function() {

          //update the padding
          this.padding = this.positionBlocks[this.relativePosition].padding;

          var closeDivShiftRight = 19;
          var closeDivShiftTop = 2;
          
          //update the position of our close box to new padding
          if (this.closeDiv) {
              // use the content div's css padding to determine if we should
              //  padd the close div
              var contentDivPadding = this.getContentDivPadding();

              this.closeDiv.style.right = contentDivPadding.right + 
                                          this.padding.right + closeDivShiftRight + "px";
              this.closeDiv.style.top = contentDivPadding.top + 
                                        this.padding.top + closeDivShiftTop + "px";
          }

          this.updateBlocks();
      },

      /** 
       * Method: calculateNewPx
       * Besides the standard offset as determined by the Anchored class, our 
       *     Framed popups have a special 'offset' property for each of their 
       *     positions, which is used to offset the popup relative to its anchor.
       * 
       * Parameters:
       * px - {<OpenLayers.Pixel>}
       * 
       * Returns:
       * {<OpenLayers.Pixel>} The the new px position of the popup on the screen
       *     relative to the passed-in px.
       */
      calculateNewPx:function(px) {
          var newPx = OpenLayers.Popup.Anchored.prototype.calculateNewPx.apply(
              this, arguments
          );

          newPx = newPx.offset(this.positionBlocks[this.relativePosition].offset);

          return newPx;
      },

      /**
       * Method: createBlocks
       */
      createBlocks: function() {
//        console.log("################## 01 createBlocks");
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

      /**
       * Method: updateBlocks
       * Internal method, called on initialize and when the popup's relative
       *     position has changed. This function takes care of re-positioning
       *     the popup's blocks in their appropropriate places.
       */
      updateBlocks: function() {
//        console.log("################## 02 updateBlocks");
          if (!this.blocks) {
              this.createBlocks();
          }
          
//          console.log("################## 03 relativePosition: "+this.relativePosition);
          if (this.size && this.relativePosition) {
              var position = this.positionBlocks[this.relativePosition];
//              console.log("################## 04 position: "+position);
              for (var i = 0; i < position.blocks.length; i++) {
      
                  var positionBlock = position.blocks[i];
                  var block = this.blocks[i];
      
                  // adjust sizes
                  var l = positionBlock.anchor.left;
                  var b = positionBlock.anchor.bottom;
                  var r = positionBlock.anchor.right;
                  var t = positionBlock.anchor.top;
      
                  //note that we use the isNaN() test here because if the 
                  // size object is initialized with a "auto" parameter, the 
                  // size constructor calls parseFloat() on the string, 
                  // which will turn it into NaN
                  //
                  var w = (isNaN(positionBlock.size.w)) ? this.size.w - (r + l) 
                                                        : positionBlock.size.w;
      
                  var h = (isNaN(positionBlock.size.h)) ? this.size.h - (b + t) 
                                                        : positionBlock.size.h;
      
                  block.div.style.width = (w < 0 ? 0 : w) + 'px';
                  block.div.style.height = (h < 0 ? 0 : h) + 'px';
      
                  block.div.style.left = (l != null) ? l + 'px' : '';
                  block.div.style.bottom = (b != null) ? b + 'px' : '';
                  block.div.style.right = (r != null) ? r + 'px' : '';            
                  block.div.style.top = (t != null) ? t + 'px' : '';
      
                  block.image.style.left = positionBlock.position.x + 'px';
                  block.image.style.top = positionBlock.position.y + 'px';
              }
      
              this.contentDiv.style.left = this.padding.left + "px";
              this.contentDiv.style.top = this.padding.top + "px";
          }
      },

      CLASS_NAME: "OpenLayers.Popup.FramedDDB"
  });
  
  
  var map = new DDBMap();
  map.display({});
  map.addInstitutionsLayer();
  map.addInstitutionsClickListener();
  
});