//name of page where map with all institutions is written in
var INSTITUTIONLIST_PAGE_NAME = 'institutionList';

//name of page where map for 1 institution is written in
var INSTITUTION_PAGE_NAME = 'institution';

var MAP_DIV = 'ddb-map';

//only initialize map once, then remember in this variable
var mapInitialized = false;

var map;

var InstitutionsMapAdapter = (function($) {
  'use strict';

  //for public properties. avoid the reserved keyword "public"
  var Public = {};

  Public.drawInstitution = function(mapDiv, lang, lon, lat) {
    if(typeof map === "undefined"){
      map = new DDBMap();
    }
    map.displayMarker({"rootDivId": MAP_DIV}, lang, lon, lat, 16);
  };

  Public.selectSectors = function() {
    if (mapInitialized) {
      map.applyFilters();
    }
  };

  var _initializeMap = function() {
    if (!mapInitialized && !$('#institution-map').hasClass('off')) {
      mapInitialized = true;
      if(typeof map === "undefined"){
        map = new DDBMap();
      }
      map.displayClusters({"rootDivId": MAP_DIV, "initZoom": 6});
    }
  };

  var _enableListView = function() {
    window.location.hash = 'list';
    $('#institution-map').addClass('off');
    $('#institution-list').removeClass('off');

    $('.view-type-switch').removeClass('off');
    $('#first-letter-index').removeClass('off');

    $('#view-institution-list').addClass('selected');
    $('#view-institution-list').attr("disabled", "disabled");
    $('#view-institution-map').removeClass('selected');
    $('#view-institution-map').removeAttr('disabled');

    $('#main-container').removeClass('map');
    $('#main-container').addClass('list');
    _initializeMap();
  };

  var _enableMapView = function() {
    window.location.hash = 'map';
    $('#institution-list').addClass('off');
    $('#institution-map').removeClass('off');

    $('.view-type-switch').removeClass('off');
    $('#first-letter-index').addClass('off');

    $('#view-institution-map').addClass('selected');
    $('#view-institution-map').attr('disabled', 'disabled');
    $('#view-institution-list').removeClass('selected');
    $('#view-institution-list').removeAttr('disabled');

    $('#main-container').addClass('map');
    $('#main-container').removeClass('list');
    _initializeMap();
  };

  Public.setupDom4MapDisplay = function() {
    var hash = window.location.hash.substring(1);
    if (hash === 'map' || hash === '') {
      _enableMapView();
    } else {
      _enableListView();
    }

    $('#view-institution-list').click(function() {
      _enableListView();
    });

    $('#view-institution-map').click(function() {
      _enableMapView();
    });

    $('.sector-facet input:checkbox').click(function() {
      Public.selectSectors();
    });

    $('.institution-with-data input:checkbox').click(function() {
      Public.selectSectors();
    });

    $('.multiselect-container input:checkbox').click(function() {
      Public.selectSectors();
    });
  };

  return Public;

})(jQuery);

$('#institution-list').ready(function() {
  $('#institution-list').addClass('off');
  return;
});

$(document).ready(function() {
  if (jsPageName === INSTITUTION_PAGE_NAME) {
    InstitutionsMapAdapter.drawInstitution(MAP_DIV, jsLanguage, jsLongitude, jsLatitude);
  } else if (jsPageName === INSTITUTIONLIST_PAGE_NAME) {
    $('.loader').addClass('off');
    InstitutionsMapAdapter.setupDom4MapDisplay();
  }
  return
});
