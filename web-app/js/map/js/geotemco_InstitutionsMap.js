

var arrayIndex = function(array, obj) {
	if (Array.indexOf) {
		return array.indexOf(obj);
	}
	for (var i = 0; i < array.length; i++) {
		if (array[i] == obj) {
			return i;
		}
	}
	return -1;
}

/*
* CircleObject.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class CircleObject
 * circle object aggregate for the map
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 *
 * @param {float} x the x (longitude) value for the circle
 * @param {float} y the y (latitude) value for the circle
 * @param {DataObject[]} elements array of data objects belonging to the circle
 * @param {float} radius the resulting radius (in pixel) for the circle
 * @param {int} search dataset index
 * @param {int} weight summed weight of all elements
 * @param {JSON} fatherBin bin of the circle object if its part of a circle pack
 */
CircleObject = function(originX, originY, shiftX, shiftY, elements, radius, search, weight, fatherBin) {

	this.originX = originX;
	this.originY = originY;
	this.shiftX = shiftX;
	this.shiftY = shiftY;
	this.elements = elements;
	this.radius = radius;
	this.search = search;
	this.weight = weight;
	this.overlay = 0;
	this.smoothness = 0;
	this.fatherBin = fatherBin;

	this.feature
	this.olFeature
	this.percentage = 0;
	this.selected = false;

};

CircleObject.prototype = {

	/**
	 * sets the OpenLayers point feature for this point object
	 * @param {OpenLayers.Feature} pointFeature the point feature for this object
	 */
	setFeature : function(feature) {
		this.feature = feature;
	},

	/**
	 * sets the OpenLayers point feature for this point object to manage its selection status
	 * @param {OpenLayers.Feature} olPointFeature the overlay point feature for this object
	 */
	setOlFeature : function(olFeature) {
		this.olFeature = olFeature;
	},

	reset : function() {
		this.overlay = 0;
		this.smoothness = 0;
	},

	setSelection : function(s) {
		this.selected = s;
	},

	toggleSelection : function() {
		this.selected = !this.selected;
	}
};

/*
* GeoTemConfig.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class GeoTemConfig
 * Global GeoTemCo Configuration File
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 */

var GeoTemConfig = {

	incompleteData : true, // show/hide data with either temporal or spatial metadata
	inverseFilter : true, // if inverse filtering is offered
	mouseWheelZoom : true, // enable/disable zoom with mouse wheel on map & timeplot
	language : 'de', // default language of GeoTemCo
	allowFilter : true, // if filtering should be allowed
	//colors for several datasets; rgb1 will be used for selected objects, rgb0 for unselected
	colors : [{
		r1 : 165, // 255,
		g1 : 0,   // 101,
		b1 : 59,  // 0,
		r0 : 240, // 253,
		g0 : 99,  // 229,
		b0 : 115  // 205
	}, {
		r1 : 144,
		g1 : 26,
		b1 : 255,
		r0 : 230,
		g0 : 225,
		b0 : 255
	}, {
		r1 : 0,
		g1 : 217,
		b1 : 0,
		r0 : 213,
		g0 : 255,
		b0 : 213
	}, {
		r1 : 240,
		g1 : 220,
		b1 : 0,
		r0 : 247,
		g0 : 244,
		b0 : 197
	}]

}

GeoTemConfig.ie = false;
GeoTemConfig.ie8 = false;

if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) {
	GeoTemConfig.ie = true;
	var ieversion = new Number(RegExp.$1);
	if (ieversion == 8) {
		GeoTemConfig.ie8 = true;
	}
}

GeoTemConfig.determineAndSetUrlPrefix = function(jsFilename) {
    if (typeof GeoTemCoMinifier_urlPrefix != "undefined") {
        // the url is already determined when loading a concatenated/minified distribution
        GeoTemConfig.configure(GeoTemCoMinifier_urlPrefix);
    } else {
        // locate the url of jsFileName
        var ux = GeoTemConfig.resolveUrlPrefix(jsFilename);
        GeoTemConfig.configure(ux);
    }
}

GeoTemConfig.resolveUrlPrefix = function(filename) {
    var sources = document.getElementsByTagName("script");
    for (var i = 0; i < sources.length; i++) {
        var index = sources[i].src.indexOf(filename);
        if (index != -1) {
            return sources[i].src.substring(0, index);
        }
    }
    if (typeof console != "undefined") {
        console.warn("cannot resolve url prefix of: " + filename);
    }
}

GeoTemConfig.configure = function(urlPrefix) {
//    if (typeof console != "undefined") {
//        console.log("GeoTemConfig.configure urlPrefix: " + urlPrefix);
//    };
	GeoTemConfig.urlPrefix = urlPrefix;
	GeoTemConfig.path = GeoTemConfig.urlPrefix + "images/";
}

GeoTemConfig.applySettings = function(settings) {
	$.extend(this, settings);
};

GeoTemConfig.getColor = function(id){
	if( GeoTemConfig.colors.length <= id ){
		GeoTemConfig.colors.push({
			r1 : Math.floor((Math.random()*255)+1),
			g1 : Math.floor((Math.random()*255)+1),
			b1 : Math.floor((Math.random()*255)+1),
			r0 : 230,
			g0 : 230,
			b0 : 230
		});
	}
	return GeoTemConfig.colors[id];
};

GeoTemConfig.getString = function(field) {
	if ( typeof Tooltips[GeoTemConfig.language] == 'undefined') {
		GeoTemConfig.language = 'de';
	}
	return Tooltips[GeoTemConfig.language][field];
}
/**
 * returns the actual mouse position
 * @param {Event} e the mouseevent
 * @return the top and left position on the screen
 */
GeoTemConfig.getMousePosition = function(e) {
	if (!e) {
		e = window.event;
	}
	var body = (window.document.compatMode && window.document.compatMode == "CSS1Compat") ? window.document.documentElement : window.document.body;
	return {
		top : e.pageY ? e.pageY : e.clientY,
		left : e.pageX ? e.pageX : e.clientX
	};
}
/**
 * returns the json object of the file from the given url
 * @param {String} url the url of the file to load
 * @return json object of given file
 */
GeoTemConfig.getJson = function(url) {
	var data;
	$.ajax({
		url : url,
		async : false,
		dataType : 'json',
		success : function(json) {
			data = json;
		}
	});
	return data;
}

GeoTemConfig.mergeObjects = function(set1, set2) {
	var inside = [];
	var newSet = [];
	for (var i = 0; i < set1.length; i++) {
		inside.push([]);
		newSet.push([]);
		for (var j = 0; j < set1[i].length; j++) {
			inside[i][set1[i][j].index] = true;
			newSet[i].push(set1[i][j]);
		}
	}
	for (var i = 0; i < set2.length; i++) {
		for (var j = 0; j < set2[i].length; j++) {
			if (!inside[i][set2[i][j].index]) {
				newSet[i].push(set2[i][j]);
			}
		}
	}
	return newSet;
}
/**
 * returns the xml dom object of the file from the given url
 * @param {String} url the url of the file to load
 * @return xml dom object of given file
 */
GeoTemConfig.getKml = function(url,asyncFunc) {
	var data;
	var async = false;
	if( asyncFunc ){
		async = true;
	}
	$.ajax({
		url : url,
		async : async,
		dataType : 'xml',
		success : function(xml) {
			if( asyncFunc ){
				asyncFunc(xml);
			}
			else {
				data = xml;
			}
		}
	});
	if( !async ){
		return data;
	}
}

/**
 * returns a Date and a SimileAjax.DateTime granularity value for a given XML time
 * @param {String} xmlTime the XML time as String
 * @return JSON object with a Date and a SimileAjax.DateTime granularity
 */
GeoTemConfig.getTimeData = function(xmlTime) {
	if (!xmlTime)
		return;
	var dateData;
	try {
		var bc = false;
		if (xmlTime.startsWith("-")) {
			bc = true;
			xmlTime = xmlTime.substring(1);
		}
		var timeSplit = xmlTime.split("T");
		var timeData = timeSplit[0].split("-");
		for (var i = 0; i < timeData.length; i++) {
			parseInt(timeData[i]);
		}
		if (bc) {
			timeData[0] = "-" + timeData[0];
		}
		if (timeSplit.length == 1) {
			dateData = timeData;
		} else {
			var dayData;
			if (timeSplit[1].indexOf("Z") != -1) {
				dayData = timeSplit[1].substring(0, timeSplit[1].indexOf("Z") - 1).split(":");
			} else {
				dayData = timeSplit[1].substring(0, timeSplit[1].indexOf("+") - 1).split(":");
			}
			for (var i = 0; i < timeData.length; i++) {
				parseInt(dayData[i]);
			}
			dateData = timeData.concat(dayData);
		}
	} catch (exception) {
		return null;
	}
	var date, granularity;
	if (dateData.length == 6) {
		granularity = SimileAjax.DateTime.SECOND;
		date = new Date(Date.UTC(dateData[0], dateData[1] - 1, dateData[2], dateData[3], dateData[4], dateData[5]));
	} else if (dateData.length == 3) {
		granularity = SimileAjax.DateTime.DAY;
		date = new Date(Date.UTC(dateData[0], dateData[1] - 1, dateData[2]));
	} else if (dateData.length == 2) {
		granularity = SimileAjax.DateTime.MONTH;
		date = new Date(Date.UTC(dateData[0], dateData[1] - 1, 1));
	} else if (dateData.length == 1) {
		granularity = SimileAjax.DateTime.YEAR;
		date = new Date(Date.UTC(dateData[0], 0, 1));
	}
	if (timeData[0] && timeData[0] < 100) {
		date.setFullYear(timeData[0]);
	}
	return {
		date : date,
		granularity : granularity
	};
}
/**
 * converts a JSON array into an array of data objects
 * @param {JSON} JSON a JSON array of data items
 * @return an array of data objects
 */
GeoTemConfig.loadJson = function(JSON) {
	var mapTimeObjects = [];
	var runningIndex = 0;
	for (var i in JSON ) {
		try {
			var item = JSON[i];
			var index = item.index || item.id || runningIndex++;
			var name = item.name || "";
			var description = item.description || "";
			var tableContent = item.tableContent || [];
			var locations = [];
			if (item.location instanceof Array) {
				for (var j = 0; j < item.location.length; j++) {
					var place = item.location[j].place || "unknown";
					var lon = item.location[j].lon || "";
					var lat = item.location[j].lat || "";
					if ((lon == "" || lat == "" || isNaN(lon) || isNaN(lat) ) && !GeoTemConfig.incompleteData) {
						throw "e";
					}
					locations.push({
						longitude : lon,
						latitude : lat,
						place : place
					});
				}
			} else {
				var place = item.place || "unknown";
				var lon = item.lon || "";
				var lat = item.lat || "";
				if ((lon == "" || lat == "" || isNaN(lon) || isNaN(lat) ) && !GeoTemConfig.incompleteData) {
					throw "e";
				}
				locations.push({
					longitude : lon,
					latitude : lat,
					place : place
				});
			}
			var dates = [];
			if (item.time instanceof Array) {
				for (var j = 0; j < item.time.length; j++) {
					var time = GeoTemConfig.getTimeData(item.time[j]);
					if (time == null && !GeoTemConfig.incompleteData) {
						throw "e";
					}
					dates.push(time);
				}
			} else {
				var time = GeoTemConfig.getTimeData(item.time);
				if (time == null && !GeoTemConfig.incompleteData) {
					throw "e";
				}
				dates.push(time);
			}
			var weight = item.weight || 1;
			var mapTimeObject = new DataObject(name, description, locations, dates, weight, tableContent);
			mapTimeObject.setIndex(index);
			mapTimeObjects.push(mapTimeObject);
		} catch(e) {
			continue;
		}
	}

	return mapTimeObjects;
}
/**
 * converts a KML dom into an array of data objects
 * @param {XML dom} kml the XML dom for the KML file
 * @return an array of data objects
 */
GeoTemConfig.loadKml = function(kml) {
	var mapObjects = [];
	var elements = kml.getElementsByTagName("Placemark");
	if (elements.length == 0) {
		return [];
	}
	var index = 0;
	for (var i = 0; i < elements.length; i++) {
		var placemark = elements[i];
		var name, description, place, granularity, lon, lat, tableContent = [], time = [], location = [];
		var weight = 1;
		var timeData = false, mapData = false;
		try {
			name = placemark.getElementsByTagName("name")[0].childNodes[0].nodeValue;
			tableContent["name"] = name;
		} catch(e) {
			name = "";
		}

		try {
			description = placemark.getElementsByTagName("description")[0].childNodes[0].nodeValue;
			tableContent["description"] = description;
		} catch(e) {
			description = "";
		}

		try {
			place = placemark.getElementsByTagName("address")[0].childNodes[0].nodeValue;
			tableContent["place"] = place;
		} catch(e) {
			place = "";
		}

		try {
			var coordinates = placemark.getElementsByTagName("Point")[0].getElementsByTagName("coordinates")[0].childNodes[0].nodeValue;
			var lonlat = coordinates.split(",");
			lon = lonlat[0];
			lat = lonlat[1];
			if (lon == "" || lat == "" || isNaN(lon) || isNaN(lat)) {
				throw "e";
			}
			location.push({
				longitude : lon,
				latitude : lat,
				place : place
			});
		} catch(e) {
			if (!GeoTemConfig.incompleteData) {
				continue;
			}
		}

		try {
			var tuple = GeoTemConfig.getTimeData(placemark.getElementsByTagName("TimeStamp")[0].getElementsByTagName("when")[0].childNodes[0].nodeValue);
			if (tuple != null) {
				time.push(tuple);
				timeData = true;
			} else if (!GeoTemConfig.incompleteData) {
				continue;
			}
		} catch(e) {
			try {
				throw "e";
				var timeSpanTag = placemark.getElementsByTagName("TimeSpan")[0];
				var tuple1 = GeoTemConfig.getTimeData(timeSpanTag.getElementsByTagName("begin")[0].childNodes[0].nodeValue);
				timeStart = tuple1.d;
				granularity = tuple1.g;
				var tuple2 = GeoTemConfig.getTimeData(timeSpanTag.getElementsByTagName("end")[0].childNodes[0].nodeValue);
				timeEnd = tuple2.d;
				if (tuple2.g > granularity) {
					granularity = tuple2.g;
				}
				timeData = true;
			} catch(e) {
				if (!GeoTemConfig.incompleteData) {
					continue;
				}
			}
		}
		var object = new DataObject(name, description, location, time, 1, tableContent);
		object.setIndex(index);
		index++;
		mapObjects.push(object);
	}
	return mapObjects;
};


/*
* MapConfig.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class MapConfig
 * Map Configuration File
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 */
function MapConfig(options) {

	this.options = {
		mapWidth : false, // false or desired width css definition for the map
		mapHeight : '580px', // false or desired height css definition for the map
		mapTitle : 'GeoTemCo Map View', // title will be shown in map header
		mapIndex : 0, // index = position in location array; for multiple locations the 2nd map refers to index 1
		alternativeMap : false, // alternative map definition for a web mapping service or 'false' for no alternative map
		/* an example:
		 {
		 name: 'someMapName',
		 url: '/geoserver/wms',
		 layer: 'namespace:layerName'
		 }
		 */
		googleMaps : false, // enable/disable Google maps (actually, no Google Maps API key is required)
		bingMaps : false, // enable/disable Bing maps (you need to set the Bing Maps API key below)
		bingApiKey : 'none', // bing maps api key, see informations at http://bingmapsportal.com/
		osmMaps : true, // enable/disable OSM maps
        osmTileset: null, // null: use the official OSM tileset, or specify another tileset, see:
                          // http://dev.openlayers.org/docs/files/OpenLayers/Layer/OSM-js.html;
		baseLayer : 'Open Street Map', // initial layer to show (e.g. 'Google Streets')
		resetMap : true, // show/hide map reset button
		countrySelect : true, // show/hide map country selection control button
		polygonSelect : true, // show/hide map polygon selection control button
		circleSelect : true, // show/hide map circle selection control button
		squareSelect : true, // show/hide map square selection control button
		multiSelection : true, // true, if multiple polygons or multiple circles should be selectable
		popups : true, // enabled popups will show popup windows for circles on the map
		olNavigation : false, // show/hide OpenLayers navigation panel
		olLayerSwitcher : false, // show/hide OpenLayers layer switcher
		olMapOverview : false, // show/hide OpenLayers map overview
		olKeyboardDefaults : true, // (de)activate Openlayers keyboard defaults
		olScaleLine : false, // (de)activate Openlayers keyboard defaults
		geoLocation : true, // show/hide GeoLocation feature
		boundaries : {
			minLon : -29,
			minLat : 35,
			maxLon : 44,
			maxLat : 67
		}, // initial map boundaries or 'false' for no boundaries
		mapCanvasFrom : '#9db9d8', // map widget background gradient color top
		mapCanvasTo : '#5783b5', // map widget background gradient color bottom
		labelGrid : true, // show label grid on hover (enables/disables hover)
		maxPlaceLabels : 0, // Integer value for fixed number of place labels:
                            //  0 --> (default) unlimited, without "all" and "others" label
                            //  1 --> 1 label (won't be shown in popup),
                            //  2 --> handled as 3, 2 is not possible because of others & all labels,
                            // [3,...,N] --> [3,...,N] place labels
		selectDefault : true, // true, if strongest label should be selected as default
		maxLabelIncrease : 2, // maximum increase (in em) for the font size of a label
		labelHover : true, // true, to update on label hover (hover labels are clickable to be selected for filtering)
		ieHighlightLabel : "color: COLOR1; background-color: COLOR0; filter:'progid:DXImageTransform.Microsoft.Alpha(Opacity=80)';-ms-filter:'progid:DXImageTransform.Microsoft.Alpha(Opacity=80)';", // css code for a highlighted place label in IE
		highlightLabel : "color: COLOR0; text-shadow: 0 0 0.4em black, 0 0 0.4em black, 0 0 0.4em black, 0 0 0.4em COLOR0;", // css code for a highlighted place label
		ieSelectedLabel : "color: COLOR1; font-weight: bold;", // css code for a selected place label in IE
		selectedLabel : "color: COLOR1; font-weight: bold;", // css code for a selected place label
		ieUnselectedLabel : "color: COLOR1; font-weight: normal;", // css code for an unselected place label in IE
		unselectedLabel : "color: COLOR1; font-weight: normal;", // css code for an unselected place label
		ieHoveredLabel : "color: COLOR1; font-weight: bold;", // css code for a hovered place label in IE
		hoveredLabel : "color: COLOR1; font-weight: bold;", // css code for a hovered place label
		circleGap : 0, // gap between the circles on the map (>=0)
		minimumRadius : 4, // minimum radius of a circle with mimimal weight (>0)
		circleOutline : true, // true if circles should have a default outline
		circleTransparency : true, // transparency of the circles
		minTransparency : 0.4, // maximum transparency of a circle
		maxTransparency : 0.8, // minimum transparency of a circle
		binning : 'generic', // binning algorithm for the map, possible values are: 'generic', 'square', 'hexagonal', 'triangular' or false for 'no binning'
		noBinningRadii : 'dynamic', // for 'no binning': 'static' for only minimum radii, 'dynamic' for increasing radii for increasing weights
		circlePackings : true, // if circles of multiple result sets should be displayed in circle packs, if a binning is performed
		binCount : 10, // number of bins for x and y dimension for lowest zoom level
		showDescriptions : true, // true to show descriptions of data items (must be provided by kml/json), false if not
		mapSelection : true, // show/hide select map dropdown
		binningSelection : false, // show/hide binning algorithms dropdown
		mapSelectionTools : true, // show/hide map selector tools
		dataInformation : true, // show/hide data information
		overlayVisibility : false, // initial visibility of additional overlays
		proxyHost : ''	//required for selectCountry feature, if the requested GeoServer and GeoTemCo are NOT on the same server

	};
	if ( typeof options != 'undefined') {
		$.extend(this.options, options);
	}

};

/*
* DataObject.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class DataObject
 * GeoTemCo's data object class
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 *
 * @param {String} name name of the data object
 * @param {String} description description of the data object
 * @param {JSON} locations a list of locations with longitude, latitide and place name
 * @param {JSON} dates a list of dates
 * @param {float} lon longitude value of the given place
 * @param {float} lat latitude value of the given place
 * @param {Date} timeStart start time of the data object
 * @param {Date} timeEnd end time of the data object
 * @param {int} granularity granularity of the given time
 * @param {int} weight weight of the time object
 */

function DataObject(name, description, locations, dates, weight, tableContent) {

	this.name = name;
	this.description = description;
	this.weight = weight;
	this.tableContent = tableContent;

	this.percentage = 0;
	this.setPercentage = function(percentage) {
		this.percentage = percentage;
	}

	this.locations = locations;
	this.isGeospatial = false;
	if (this.locations.length > 0) {
		this.isGeospatial = true;
	}

	this.placeDetails = [];
	for (var i = 0; i < this.locations.length; i++) {
		this.placeDetails.push(this.locations[i].place.split("/"));
	}

	this.getLatitude = function(locationId) {
		return this.locations[locationId].latitude;
	}

	this.getLongitude = function(locationId) {
		return this.locations[locationId].longitude;
	}

	this.getPlace = function(locationId, level) {
		if (level >= this.placeDetails[locationId].length) {
			return this.placeDetails[locationId][this.placeDetails[locationId].length - 1];
		}
		return this.placeDetails[locationId][level];
	}

	this.dates = dates;
	this.isTemporal = false;
	if (this.dates.length > 0) {
		this.isTemporal = true;
	}

	this.getDate = function(dateId) {
		return this.dates[dateId].date;
	}

	this.getTimeGranularity = function(dateId) {
		return this.dates[dateId].granularity;
	}

	this.setIndex = function(index) {
		this.index = index;
	}

	this.getTimeString = function() {
		if (this.timeStart != this.timeEnd) {
			return (SimileAjax.DateTime.getTimeString(this.granularity, this.timeStart) + " - " + SimileAjax.DateTime.getTimeString(this.granularity, this.timeEnd));
		} else {
			return SimileAjax.DateTime.getTimeString(this.granularity, this.timeStart) + "";
		}
	};

};
/*
* Dataset.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class Dataset
 * GeoTemCo's Dataset class
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 *
 * @param {Array} objects data item arrays from different datasets
 * @param {String} label label for the datasets
 */
function Dataset(objects, label) {

	this.objects = objects;
	this.label = label;

}
/*
* Binning.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class Binning
 * Calculates map aggregation with several binning algorithms
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 */
Binning = function(map, options) {

	this.map = map;
	this.options = options;
	this.reset();

};

Binning.prototype = {

	getSet : function() {
  console.log("#################### 318 Binning getSet");
		var type = this.options.binning;
		if (!type) {
			return this.getExactBinning();
		} else if (type == 'generic') {
			return this.getGenericBinning();
		} else if (type == 'square') {
			return this.getSquareBinning();
		} else if (type == 'hexagonal') {
			return this.getHexagonalBinning();
		} else if (type == 'triangular') {
			return this.getTriangularBinning();
		}
	},

	getExactBinning : function() {
		if ( typeof this.binnings['exact'] == 'undefined') {
			this.exactBinning();
		}
		return this.binnings['exact'];
	},

	getGenericBinning : function() {
  console.log("#################### 318 Binning getGenericBinning");
		if ( typeof this.binnings['generic'] == 'undefined') {
			this.genericBinning();
		}
		return this.binnings['generic'];
	},

	getSquareBinning : function() {
		if ( typeof this.binnings['square'] == 'undefined') {
			this.squareBinning();
		}
		return this.binnings['square'];
	},

	getHexagonalBinning : function() {
		if ( typeof this.binnings['hexagonal'] == 'undefined') {
			this.hexagonalBinning();
		}
		return this.binnings['hexagonal'];
	},

	getTriangularBinning : function() {
		if ( typeof this.binnings['triangular'] == 'undefined') {
			this.triangularBinning();
		}
		return this.binnings['triangular'];
	},

	reset : function() {
		this.zoomLevels = this.map.getNumZoomLevels();
		this.binnings = [];
		this.minimumRadius = this.options.minimumRadius;
		this.maximumRadius = 0;
		this.maximumPoints = 0;
		this.minArea = 0;
		this.maxArea = 0;
	},

	getMaxRadius : function(size) {
		return 4 * Math.log(size) / Math.log(2);
	},

	setObjects : function(objects) {
		this.objects = objects;
		for (var i = 0; i < this.objects.length; i++) {
			var weight = 0;
			for (var j = 0; j < this.objects[i].length; j++) {
				if (this.objects[i][j].isGeospatial) {
					weight += this.objects[i][j].weight;
				}
			}
			var r = this.getMaxRadius(weight);
			if (r > this.maximumRadius) {
				this.maximumRadius = r;
				this.maximumPoints = weight;
				this.maxArea = Math.PI * this.maximumRadius * this.maximumRadius;
				this.minArea = Math.PI * this.minimumRadius * this.minimumRadius;
			}
		}
	},

	dist : function(x1, y1, x2, y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	},

	exactBinning : function() {
		var circleSets = [];
		var hashMaps = [];
		var selectionHashs = [];

		var circleAggregates = [];
		var bins = [];
		for (var i = 0; i < this.objects.length; i++) {
			bins.push([]);
			circleAggregates.push([]);
			for (var j = 0; j < this.objects[i].length; j++) {
				var o = this.objects[i][j];
				if (o.isGeospatial) {
					if ( typeof circleAggregates[i]['' + o.getLongitude(this.options.mapIndex)] == 'undefined') {
						circleAggregates[i]['' + o.getLongitude(this.options.mapIndex)] = [];
					}
					if ( typeof circleAggregates[i][''+o.getLongitude(this.options.mapIndex)]['' + o.getLatitude(this.options.mapIndex)] == 'undefined') {
						circleAggregates[i][''+o.getLongitude(this.options.mapIndex)]['' + o.getLatitude(this.options.mapIndex)] = [];
						bins[i].push(circleAggregates[i][''+o.getLongitude(this.options.mapIndex)]['' + o.getLatitude(this.options.mapIndex)]);
					}
					circleAggregates[i][''+o.getLongitude(this.options.mapIndex)]['' + o.getLatitude(this.options.mapIndex)].push(o);
				}
			}
		}

		var circles = [];
		var hashMap = [];
		var selectionMap = [];
		for (var i = 0; i < bins.length; i++) {
			circles.push([]);
			hashMap.push([]);
			selectionMap.push([]);
			for (var j = 0; j < bins[i].length; j++) {
				var bin = bins[i][j];
				var p = new OpenLayers.Geometry.Point(bin[0].getLongitude(this.options.mapIndex), bin[0].getLatitude(this.options.mapIndex), null);
				p.transform(this.map.displayProjection, this.map.projection);
				var weight = 0;
				for (var z = 0; z < bin.length; z++) {
					weight += bin[z].weight;
				}
				var radius = this.options.minimumRadius;
				if (this.options.noBinningRadii == 'dynamic') {
					radius = this.getRadius(weight);
				}
				var circle = new CircleObject(p.x, p.y, 0, 0, bin, radius, i, weight);
				circles[i].push(circle);
				for (var z = 0; z < bin.length; z++) {
					hashMap[i][bin[z].index] = circle;
					selectionMap[i][bin[z].index] = false;
				}
			}
		}
		for (var k = 0; k < this.zoomLevels; k++) {
			circleSets.push(circles);
			hashMaps.push(hashMap);
			selectionHashs.push(selectionMap);
		}
		this.binnings['exact'] = {
			circleSets : circleSets,
			hashMaps : hashMaps,
			selectionHashs : selectionHashs
		};
	},

	genericClustering : function(objects, id) {
  console.log("#################### 318 Binning genericClustering");
		var binSets = [];
		var circleSets = [];
		var hashMaps = [];
		var selectionHashs = [];
		var clustering = new Clustering(-20037508.34, -20037508.34, 20037508.34, 20037508.34);
		for (var i = 0; i < objects.length; i++) {
			for (var j = 0; j < objects[i].length; j++) {
				var o = objects[i][j];
				if (o.isGeospatial) {
					var p = new OpenLayers.Geometry.Point(o.getLongitude(this.options.mapIndex), o.getLatitude(this.options.mapIndex), null);
					p.transform(this.map.displayProjection, this.map.projection);
					var point = new Vertex(Math.floor(p.x), Math.floor(p.y), objects.length, this);
					point.addElement(o, o.weight, i);
					clustering.add(point);
				}
			}
		}

		for (var i = 0; i < this.zoomLevels; i++) {
			var bins = [];
			var circles = [];
			var hashMap = [];
			var selectionMap = [];
			for (var j = 0; j < objects.length; j++) {
				circles.push([]);
				hashMap.push([]);
				selectionMap.push([]);
			}
			var resolution = this.map.getResolutionForZoom(this.zoomLevels - i - 1);
			clustering.mergeForResolution(resolution, this.options.circleGap);
			for (var j = 0; j < clustering.vertices.length; j++) {
				var point = clustering.vertices[j];
				if (!point.legal) {
					continue;
				}
				var balls = [];
				for (var k = 0; k < point.elements.length; k++) {
					if (point.elements[k].length > 0) {
						balls.push({
							search : k,
							elements : point.elements[k],
							radius : point.radii[k],
							weight : point.weights[k]
						});
					}
				}
				var orderBalls = function(b1, b2) {
					if (b1.radius > b2.radius) {
						return -1;
					}
					if (b2.radius > b1.radius) {
						return 1;
					}
					return 0;
				}
				var fatherBin = {
					circles : [],
					length : 0,
					radius : point.radius / resolution,
					x : point.x,
					y : point.y
				};
				for (var k = 0; k < objects.length; k++) {
					fatherBin.circles.push(false);
				}
				var createCircle = function(sx, sy, ball) {
					var index = id || ball.search;
					var circle = new CircleObject(point.x, point.y, sx, sy, ball.elements, ball.radius, index, ball.weight, fatherBin);
					circles[ball.search].push(circle);
					fatherBin.circles[index] = circle;
					fatherBin.length++;
					for (var k = 0; k < ball.elements.length; k++) {
						hashMap[ball.search][ball.elements[k].index] = circle;
						selectionMap[ball.search][ball.elements[k].index] = false;
					}
				}
				if (balls.length == 1) {
					createCircle(0, 0, balls[0]);
				} else if (balls.length == 2) {
					var r1 = balls[0].radius;
					var r2 = balls[1].radius;
					createCircle(-1 * r2, 0, balls[0]);
					createCircle(r1, 0, balls[1]);
				} else if (balls.length == 3) {
					balls.sort(orderBalls);
					var r1 = balls[0].radius;
					var r2 = balls[1].radius;
					var r3 = balls[2].radius;
					var d = ((2 / 3 * Math.sqrt(3) - 1) / 2) * r2;
					var delta1 = point.radius / resolution - r1 - d;
					var delta2 = r1 - delta1;
					createCircle(-delta1, 0, balls[0]);
					createCircle(delta2 + r2 - 3 * d, r2, balls[1]);
					createCircle(delta2 + r3 - (3 * d * r3 / r2), -1 * r3, balls[2]);
				} else if (balls.length == 4) {
					balls.sort(orderBalls);
					var r1 = balls[0].radius;
					var r2 = balls[1].radius;
					var r3 = balls[2].radius;
					var r4 = balls[3].radius;
					var d = (Math.sqrt(2) - 1) * r2;
					createCircle(-1 * d - r2, 0, balls[0]);
					createCircle(r1 - r2, -1 * d - r4, balls[3]);
					createCircle(r1 - r2, d + r3, balls[2]);
					createCircle(d + r1, 0, balls[1]);
				}
				if (fatherBin.length > 1) {
					bins.push(fatherBin);
				}
			}
			circleSets.push(circles);
			binSets.push(bins);
			hashMaps.push(hashMap);
			selectionHashs.push(selectionMap);
		}
		circleSets.reverse();
		binSets.reverse();
		hashMaps.reverse();
		selectionHashs.reverse();
		return {
			circleSets : circleSets,
			binSets : binSets,
			hashMaps : hashMaps,
			selectionHashs : selectionHashs
		};
	},

	genericBinning : function() {
  console.log("#################### 318 Binning genericBinning");
  console.log("#################### 318 Binning options:");
  console.log(this.options);
  console.log("#################### 318 Binning objects:");
  console.log(this.objects);
		if (this.options.circlePackings || this.objects.length == 1) {
			this.binnings['generic'] = this.genericClustering(this.objects);
		} else {
			var circleSets = [];
			var hashMaps = [];
			var selectionHashs = [];
			for (var i = 0; i < this.objects.length; i++) {
				var sets = this.genericClustering([this.objects[i]], i);
				if (i == 0) {
					circleSets = sets.circleSets;
					hashMaps = sets.hashMaps;
					selectionHashs = sets.selectionHashs;
				} else {
					for (var j = 0; j < circleSets.length; j++) {
						circleSets[j] = circleSets[j].concat(sets.circleSets[j]);
						hashMaps[j] = hashMaps[j].concat(sets.hashMaps[j]);
						selectionHashs[j] = selectionHashs[j].concat(sets.selectionHashs[j]);
					}
				}
			}
			this.binnings['generic'] = {
				circleSets : circleSets,
				hashMaps : hashMaps,
				selectionHashs : selectionHashs
			};
		}
	},

	getRadius : function(n) {
		if (n == 0) {
			return 0;
		}
		if (n == 1) {
			return this.minimumRadius;
		}
		return Math.sqrt((this.minArea + (this.maxArea - this.minArea) / (this.maximumPoints - 1) * (n - 1) ) / Math.PI);
	},

	getBinRadius : function(n, r_max, N) {
		if (n == 0) {
			return 0;
		}
		/*
		function log2(x) {
			return (Math.log(x)) / (Math.log(2));
		}
		var r0 = this.options.minimumRadius;
		var r;
		if ( typeof r_max == 'undefined') {
			return r0 + n / Math.sqrt(this.options.maximumPoints);
		}
		return r0 + (r_max - r0 ) * log2(n) / log2(N);
		*/
		var minArea = Math.PI * this.options.minimumRadius * this.options.minimumRadius;
		var maxArea = Math.PI * r_max * r_max;
		return Math.sqrt((minArea + (maxArea - minArea) / (N - 1) * (n - 1) ) / Math.PI);
	},

	shift : function(type, bin, radius, elements) {

		var x1 = bin.x, x2 = 0;
		var y1 = bin.y, y2 = 0;
		for (var i = 0; i < elements.length; i++) {
			x2 += elements[i].x / elements.length;
			y2 += elements[i].y / elements.length;
		}

		var sx = 0, sy = 0;

		if (type == 'square') {
			var dx = Math.abs(x2 - x1);
			var dy = Math.abs(y2 - y1);
			var m = dy / dx;
			var n = y1 - m * x1;
			if (dx > dy) {
				sx = bin.x - (x1 + bin.r - radius );
				sy = bin.y - (m * bin.x + n );
			} else {
				sy = bin.y - (y1 + bin.r - radius );
				sx = bin.x - (bin.y - n) / m;
			}
		}

		return {
			x : sx,
			y : sy
		};

	},

	binSize : function(elements) {
		var size = 0;
		for (var i in elements ) {
			size += elements[i].weight;
		}
		return size;
	},

	setCircleSet : function(id, binData) {
		var circleSets = [];
		var hashMaps = [];
		var selectionHashs = [];
		for (var i = 0; i < binData.length; i++) {
			var circles = [];
			var hashMap = [];
			var selectionMap = [];
			for (var j = 0; j < this.objects.length; j++) {
				circles.push([]);
				hashMap.push([]);
				selectionMap.push([]);
			}
			var points = [];
			var max = 0;
			var radius = 0;
			var resolution = this.map.getResolutionForZoom(i);
			for (var j = 0; j < binData[i].length; j++) {
				for (var k = 0; k < binData[i][j].bin.length; k++) {
					var bs = this.binSize(binData[i][j].bin[k]);
					if (bs > max) {
						max = bs;
						radius = binData[i][j].r / resolution;
					}
				}
			}
			for (var j = 0; j < binData[i].length; j++) {
				var bin = binData[i][j];
				for (var k = 0; k < bin.bin.length; k++) {
					if (bin.bin[k].length == 0) {
						continue;
					}
					var weight = this.binSize(bin.bin[k]);
					var r = this.getBinRadius(weight, radius, max);
					var shift = this.shift(id, bin, r * resolution, bin.bin[k], i);
					var circle = new CircleObject(bin.x - shift.x, bin.y - shift.y, 0, 0, bin.bin[k], r, k, weight);
					circles[k].push(circle);
					for (var z = 0; z < bin.bin[k].length; z++) {
						hashMap[k][bin.bin[k][z].index] = circle;
						selectionMap[k][bin.bin[k][z].index] = false;
					}
				}
			}
			circleSets.push(circles);
			hashMaps.push(hashMap);
			selectionHashs.push(selectionMap);
		}
		this.binnings[id] = {
			circleSets : circleSets,
			hashMaps : hashMaps,
			selectionHashs : selectionHashs
		};
	},

	squareBinning : function() {

		var l = 20037508.34;
		var area0 = l * l * 4;
		var binCount = this.options.binCount;

		var bins = [];
		var binData = [];
		for (var k = 0; k < this.zoomLevels; k++) {
			bins.push([]);
			binData.push([]);
		}

		for (var i = 0; i < this.objects.length; i++) {
			for (var j = 0; j < this.objects[i].length; j++) {
				var o = this.objects[i][j];
				if (!o.isGeospatial) {
					continue;
				}
				var p = new OpenLayers.Geometry.Point(o.getLongitude(this.options.mapIndex), o.getLatitude(this.options.mapIndex), null);
				p.transform(this.map.displayProjection, this.map.projection);
				o.x = p.x;
				o.y = p.y;
				for (var k = 0; k < this.zoomLevels; k++) {
					var bc = binCount * Math.pow(2, k);
					var a = 2 * l / bc;
					var binX = Math.floor((p.x + l) / (2 * l) * bc);
					var binY = Math.floor((p.y + l) / (2 * l) * bc);
					if ( typeof bins[k]['' + binX] == 'undefined') {
						bins[k]['' + binX] = [];
					}
					if ( typeof bins[k][''+binX]['' + binY] == 'undefined') {
						bins[k][''+binX]['' + binY] = [];
						for (var z = 0; z < this.objects.length; z++) {
							bins[k][''+binX]['' + binY].push([]);
						}
						var x = binX * a + a / 2 - l;
						var y = binY * a + a / 2 - l;
						binData[k].push({
							bin : bins[k][''+binX]['' + binY],
							x : x,
							y : y,
							a : a,
							r : a / 2
						});
					}
					bins[k][''+binX][''+binY][i].push(o);
				}
			}
		}

		this.setCircleSet('square', binData);

	},

	triangularBinning : function() {

		var l = 20037508.34;
		var a0 = this.options.binCount;
		var a1 = Math.sqrt(4 * a0 * a0 / Math.sqrt(3));
		var binCount = a0 / a1 * a0;

		var bins = [];
		var binData = [];
		for (var k = 0; k < this.zoomLevels; k++) {
			bins.push([]);
			binData.push([]);
		}

		for (var i = 0; i < this.objects.length; i++) {
			for (var j = 0; j < this.objects[i].length; j++) {
				var o = this.objects[i][j];
				if (!o.isGeospatial) {
					continue;
				}
				var p = new OpenLayers.Geometry.Point(o.getLongitude(this.options.mapIndex), o.getLatitude(this.options.mapIndex), null);
				p.transform(this.map.displayProjection, this.map.projection);
				o.x = p.x;
				o.y = p.y;
				for (var k = 0; k < this.zoomLevels; k++) {
					var x_bc = binCount * Math.pow(2, k);
					var y_bc = x_bc * x_bc / Math.sqrt(x_bc * x_bc - x_bc * x_bc / 4);
					var a = 2 * l / x_bc;
					var h = 2 * l / y_bc;
					var binY = Math.floor((p.y + l) / (2 * l) * y_bc);
					if ( typeof bins[k]['' + binY] == 'undefined') {
						bins[k]['' + binY] = [];
					}
					var triangleIndex;
					var partitionsX = x_bc * 2;
					var partition = Math.floor((p.x + l) / (2 * l) * partitionsX);
					var xMax = a / 2;
					var yMax = h;
					var x = p.x + l - partition * a / 2;
					var y = p.y + l - binY * h;
					if (binY % 2 == 0 && partition % 2 == 1 || binY % 2 == 1 && partition % 2 == 0) {
						if (y + yMax / xMax * x < yMax) {
							triangleIndex = partition;
						} else {
							triangleIndex = partition + 1;
						}
					} else {
						if (y > yMax / xMax * x) {
							triangleIndex = partition;
						} else {
							triangleIndex = partition + 1;
						}
					}
					if ( typeof bins[k][''+binY]['' + triangleIndex] == 'undefined') {
						bins[k][''+binY]['' + triangleIndex] = [];
						for (var z = 0; z < this.objects.length; z++) {
							bins[k][''+binY]['' + triangleIndex].push([]);
						}
						var r = Math.sqrt(3) / 6 * a;
						var x = (triangleIndex - 1) * a / 2 + a / 2 - l;
						var y;
						if (binY % 2 == 0 && triangleIndex % 2 == 0 || binY % 2 == 1 && triangleIndex % 2 == 1) {
							y = binY * h + h - r - l;
						} else {
							y = binY * h + r - l;
						}
						binData[k].push({
							bin : bins[k][''+binY]['' + triangleIndex],
							x : x,
							y : y,
							a : a,
							r : r
						});
					}
					bins[k][''+binY][''+triangleIndex][i].push(o);
				}
			}
		}

		this.setCircleSet('triangular', binData);

	},

	hexagonalBinning : function() {

		var l = 20037508.34;
		var a0 = this.options.binCount;
		var a2 = Math.sqrt(4 * a0 * a0 / Math.sqrt(3)) / Math.sqrt(6);
		var binCount = a0 / a2 * a0;

		var bins = [];
		var binData = [];
		for (var k = 0; k < this.zoomLevels; k++) {
			bins.push([]);
			binData.push([]);
		}

		for (var i = 0; i < this.objects.length; i++) {
			for (var j = 0; j < this.objects[i].length; j++) {
				var o = this.objects[i][j];
				if (!o.isGeospatial) {
					continue;
				}
				var p = new OpenLayers.Geometry.Point(o.getLongitude(this.options.mapIndex), o.getLatitude(this.options.mapIndex), null);
				p.transform(this.map.displayProjection, this.map.projection);
				o.x = p.x;
				o.y = p.y;
				for (var k = 0; k < this.zoomLevels; k++) {
					var x_bc = binCount * Math.pow(2, k);
					var y_bc = x_bc * x_bc / Math.sqrt(x_bc * x_bc - x_bc * x_bc / 4);
					var a = 2 * l / x_bc;
					var h = 2 * l / y_bc;
					var binY = Math.floor((p.y + l) / (2 * l) * y_bc);
					if ( typeof bins[k]['' + binY] == 'undefined') {
						bins[k]['' + binY] = [];
					}
					var triangleIndex;
					var partitionsX = x_bc * 2;
					var partition = Math.floor((p.x + l) / (2 * l) * partitionsX);
					var xMax = a / 2;
					var yMax = h;
					var x = p.x + l - partition * a / 2;
					var y = p.y + l - binY * h;
					if (binY % 2 == 0 && partition % 2 == 1 || binY % 2 == 1 && partition % 2 == 0) {
						if (y + yMax / xMax * x < yMax) {
							triangleIndex = partition;
						} else {
							triangleIndex = partition + 1;
						}
					} else {
						if (y > yMax / xMax * x) {
							triangleIndex = partition;
						} else {
							triangleIndex = partition + 1;
						}
					}
					if ( typeof bins[k][''+binY]['' + triangleIndex] == 'undefined') {
						bins[k][''+binY]['' + triangleIndex] = [];
						for (var z = 0; z < this.objects.length; z++) {
							bins[k][''+binY]['' + triangleIndex].push([]);
						}
						var r = Math.sqrt(3) / 6 * a;
						var x = (triangleIndex - 1) * a / 2 + a / 2 - l;
						var y;
						if (binY % 2 == 0 && triangleIndex % 2 == 0 || binY % 2 == 1 && triangleIndex % 2 == 1) {
							y = binY * h + h - r - l;
						} else {
							y = binY * h + r - l;
						}
						binData[k].push({
							bin : bins[k][''+binY]['' + triangleIndex],
							x : x,
							y : y,
							a : a,
							r : r,
							h : h,
							binX : triangleIndex,
							binY : binY
						});
					}
					bins[k][''+binY][''+triangleIndex][i].push(o);
				}
			}
		}

		var hexaBins = [];
		var hexaBinData = [];
		for (var k = 0; k < this.zoomLevels; k++) {
			hexaBins.push([]);
			hexaBinData.push([]);
		}

		for (var i = 0; i < binData.length; i++) {
			for (var j = 0; j < binData[i].length; j++) {
				var bin = binData[i][j];
				var binY = Math.floor(bin.binY / 2);
				var binX = Math.floor(bin.binX / 3);
				var x, y;
				var a = bin.a;
				var h = bin.h;
				if (bin.binX % 6 < 3) {
					if ( typeof hexaBins[i]['' + binY] == 'undefined') {
						hexaBins[i]['' + binY] = [];
					}
					y = binY * 2 * bin.h + bin.h - l;
					x = binX * 1.5 * bin.a + a / 2 - l;
				} else {
					if (bin.binY % 2 == 1) {
						binY++;
					}
					if ( typeof hexaBins[i]['' + binY] == 'undefined') {
						hexaBins[i]['' + binY] = [];
					}
					y = binY * 2 * bin.h - l;
					x = binX * 1.5 * bin.a + a / 2 - l;
				}
				if ( typeof hexaBins[i][''+binY]['' + binX] == 'undefined') {
					hexaBins[i][''+binY]['' + binX] = [];
					for (var z = 0; z < this.objects.length; z++) {
						hexaBins[i][''+binY]['' + binX].push([]);
					}
					hexaBinData[i].push({
						bin : hexaBins[i][''+binY]['' + binX],
						x : x,
						y : y,
						a : bin.a,
						r : bin.h
					});
				}
				for (var k = 0; k < bin.bin.length; k++) {
					for (var m = 0; m < bin.bin[k].length; m++) {
						hexaBins[i][''+binY][''+binX][k].push(bin.bin[k][m]);
					}
				}
			}
		}

		this.setCircleSet('hexagonal', hexaBinData);

	}
}

/*
* MapDataSource.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class MapDataSource
 * implementation for aggregation of map items
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 *
 * @param {OpenLayers.Map} olMap openlayers map object of the map widget
 * @param {JSON} options map configuration
 */
function MapDataSource(olMap, options) {

	this.olMap = olMap;
	this.circleSets = [];
	this.binning = new Binning(olMap, options);

};

MapDataSource.prototype = {

	/**
	 * initializes the MapDataSource
	 * @param {MapObject[][]} mapObjects an array of map objects of different sets
	 */
	initialize : function(mapObjects) {

		if (mapObjects != this.mapObjects) {
			this.binning.reset();
			this.binning.setObjects(mapObjects);
		}
		this.mapObjects = mapObjects;

        var time0 = (new Date()).getTime();
		var set = this.binning.getSet();
		this.circleSets = set.circleSets;
		this.binSets = set.binSets;
		this.hashMapping = set.hashMaps;

        timeCluster = (new Date()).getTime();
        var timeClusterInfo = (this.mapObjects[0].length +
                               " elements clustered in " + (timeCluster - time0) + "ms");
// XXXXX   alert(timeClusterInfo);
//        if (typeof console != 'undefined') {
//            console.log(timeClusterInfo);
//        }
    

    },

	getObjectsByZoom : function() {
		var zoom = Math.floor(this.olMap.getZoom());
		if (this.circleSets.length < zoom) {
			return null;
		}
		return this.circleSets[zoom];
	},

	getAllObjects : function() {
		if (this.circleSets.length == 0) {
			return null;
		}
		return this.circleSets;
	},

	getAllBins : function() {
		if (this.binSets.length == 0) {
			return null;
		}
		return this.binSets;
	},

	clearOverlay : function() {
		var zoom = Math.floor(this.olMap.getZoom());
		var circles = this.circleSets[zoom];
		for (var i in circles ) {
			for (var j in circles[i] ) {
				circles[i][j].reset();
			}
		}
	},

	setOverlay : function(mapObjects) {
		var zoom = Math.floor(this.olMap.getZoom());
		for (var j in mapObjects ) {
			for (var k in mapObjects[j] ) {
				var o = mapObjects[j][k];
				if (o.isGeospatial) {
					this.hashMapping[zoom][j][o.index].overlay += o.weight;
				}
			}
		}
	},

	size : function() {
		if (this.circleSets.length == 0) {
			return 0;
		}
		return this.circleSets[0].length;
	},

	getCircle : function(index, id) {
		var zoom = Math.floor(this.olMap.getZoom());
		return this.hashMapping[zoom][index][id];
	}
};
/*
* Clustering.js
*
* Copyright (c) 2012, Stefan Jänicke. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301  USA
*/

/**
 * @class Vertex, Edge, Triangle, Clustering, BinaryHeap
 * Dynamic Delaunay clustering algorithm (see GeoTemCo paper)
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 */

function Vertex(x, y, categories, binning) {
	this.x = x;
	this.y = y;
	this.radius
	this.size = 0;
	this.elements = [];
	this.radii = [];
	this.weights = [];
	this.legal = true;
	this.binning = binning;
	if (categories != undefined) {
		for (var i = 0; i < categories; i++) {
			this.elements.push([]);
			this.weights.push(0);
		}
	}
}

Vertex.prototype.merge = function(v0, v1) {
	for (var i = 0; i < v0.elements.length; i++) {
		this.elements[i] = v0.elements[i].concat(v1.elements[i]);
		this.weights[i] = v0.weights[i] + v1.weights[i];
		this.size += this.weights[i];
	}
}

Vertex.prototype.CalculateRadius = function(resolution) {
	this.radii = [];
	for (i in this.elements ) {
		this.radii.push(this.binning.getRadius(this.weights[i]));
	}
	if (this.radii.length == 1) {
		this.radius = this.radii[0] * resolution;
	} else {
		var count = 0;
		var max1 = 0;
		var max2 = 0;
		for (i in this.radii ) {
			if (this.radii[i] != 0) {
				count++;
			}
			if (this.radii[i] > max1) {
				if (max1 > max2) {
					max2 = max1;
				}
				max1 = this.radii[i];
			} else if (this.radii[i] > max2) {
				max2 = this.radii[i];
			}
		}
		if (count == 1) {
			this.radius = max1 * resolution;
		} else if (count == 2) {
			this.radius = (max1 + max2) * resolution;
		} else if (count == 3) {
			var d = (2 / 3 * Math.sqrt(3) - 1) * max1;
			this.radius = (d + max1 + max2) * resolution;
		} else if (count == 4) {
			var d = (Math.sqrt(2) - 1) * max2;
			this.radius = (d + max1 + max2) * resolution;
		}
	}
}

Vertex.prototype.addElement = function(e, weight, index) {
	this.elements[index].push(e);
	this.size += weight;
	this.weights[index] += weight;
}
function Edge(v0, v1) {
	this.v0 = v0;
	this.v1 = v1;
	this.leftFace
	this.rightFace
	this.legal = true;
	this.setLength();
}

Edge.prototype.setLength = function() {
	var dx = this.v0.x - this.v1.x;
	var dy = this.v0.y - this.v1.y;
	this.length = Math.sqrt(dx * dx + dy * dy);
}

Edge.prototype.contains = function(v) {
	if (this.v0 == v || this.v1 == v) {
		return true;
	}
	return false;
}

Edge.prototype.replaceFace = function(f_old, f_new) {
	if (this.leftFace == f_old) {
		this.leftFace = f_new;
	} else if (this.rightFace == f_old) {
		this.rightFace = f_new;
	}
}

Edge.prototype.setFace = function(f) {
	if (f.leftOf(this)) {
		this.leftFace = f;
	} else {
		this.rightFace = f;
	}
}

Edge.prototype.setFaces = function(f1, f2) {
	if (f1.leftOf(this)) {
		this.leftFace = f1;
		this.rightFace = f2;
	} else {
		this.leftFace = f2;
		this.rightFace = f1;
	}
}

Edge.prototype.removeFace = function(f) {
	if (this.leftFace == f) {
		this.leftFace = null;
	} else {
		this.rightFace = null;
	}
}

Edge.prototype.equals = function(e) {
	if (this.v0 == e.v0 && this.v1 == e.v1 || this.v0 == e.v1 && this.v1 == e.v0) {
		return true;
	}
	return false;
}
function Triangle(edges) {
	this.edges = edges;
	this.setVertices();
	this.descendants = [];
}

Triangle.prototype.getTriple = function(e) {
	var i = arrayIndex(this.edges, e);
	return {
		e_s : this.edges[(i + 1) % 3],
		e_p : this.edges[(i + 2) % 3],
		u : this.vertices[(i + 2) % 3]
	};
}

Triangle.prototype.leftOf = function(e) {
	var i = arrayIndex(this.edges, e);
	if (this.vertices[i].y != this.vertices[(i + 1) % 3].y) {
		return this.vertices[i].y > this.vertices[(i + 1) % 3].y;
	}
	return this.vertices[i].y > this.vertices[(i + 2) % 3].y;
}

Triangle.prototype.getNext = function(v) {
	var i = arrayIndex(this.vertices, v);
	return this.vertices[(i + 1) % 3];
}

Triangle.prototype.oppositeEdge = function(v) {
	var i = arrayIndex(this.vertices, v);
	return this.edges[(i + 1) % 3];
}

Triangle.prototype.contains = function(v) {
	return arrayIndex(this.vertices, v) != -1;
}

Triangle.prototype.replace = function(e_old, e_new) {
	this.edges[arrayIndex(this.edges, e_old)] = e_new;
}

Triangle.prototype.setVertices = function() {
	if (this.edges[1].v0 == this.edges[0].v0 || this.edges[1].v1 == this.edges[0].v0) {
		this.vertices = [this.edges[0].v1, this.edges[0].v0];
	} else {
		this.vertices = [this.edges[0].v0, this.edges[0].v1];
	}
	if (this.edges[2].v0 == this.vertices[0]) {
		this.vertices.push(this.edges[2].v1);
	} else {
		this.vertices.push(this.edges[2].v0);
	}
}

Triangle.prototype.replaceBy = function(triangles) {
	this.descendants = triangles;
	this.edges[0].replaceFace(this, triangles[0]);
	this.edges[1].replaceFace(this, triangles[1]);
	this.edges[2].replaceFace(this, triangles[2]);
}

Triangle.prototype.CalcCircumcircle = function() {
	var v0 = this.vertices[0];
	var v1 = this.vertices[1];
	var v2 = this.vertices[2];
	var A = v1.x - v0.x;
	var B = v1.y - v0.y;
	var C = v2.x - v0.x;
	var D = v2.y - v0.y;
	var E = A * (v0.x + v1.x) + B * (v0.y + v1.y);
	var F = C * (v0.x + v2.x) + D * (v0.y + v2.y);
	var G = 2.0 * (A * (v2.y - v1.y) - B * (v2.x - v1.x));
	var cx = (D * E - B * F) / G;
	var cy = (A * F - C * E) / G;
	this.center = new Vertex(cx, cy);
	var dx = this.center.x - v0.x;
	var dy = this.center.y - v0.y;
	this.radius_squared = dx * dx + dy * dy;
};

Triangle.prototype.inCircumcircle = function(v) {
	if (this.radius_squared == undefined) {
		this.CalcCircumcircle();
	}
	var dx = this.center.x - v.x;
	var dy = this.center.y - v.y;
	var dist_squared = dx * dx + dy * dy;
	return (dist_squared <= this.radius_squared );
};

Triangle.prototype.interior = function(v) {
	var v0 = this.vertices[0];
	var v1 = this.vertices[1];
	var v2 = this.vertices[2];
	var dotAB = (v.x - v0.x ) * (v0.y - v1.y ) + (v.y - v0.y ) * (v1.x - v0.x );
	var dotBC = (v.x - v1.x ) * (v1.y - v2.y ) + (v.y - v1.y ) * (v2.x - v1.x );
	var dotCA = (v.x - v2.x ) * (v2.y - v0.y ) + (v.y - v2.y ) * (v0.x - v2.x );
	if (dotAB > 0 || dotBC > 0 || dotCA > 0) {
		return null;
	} else if (dotAB < 0 && dotBC < 0 && dotCA < 0) {
		return this;
	} else if (dotAB == 0) {
		if (dotBC == 0) {
			return this.vertices[1];
		} else if (dotCA == 0) {
			return this.vertices[0];
		}
		return this.edges[0];
	} else if (dotBC == 0) {
		if (dotCA == 0) {
			return this.vertices[2];
		}
		return this.edges[1];
	} else if (dotCA == 0) {
		return this.edges[2];
	}
};

function Clustering(xMin, yMin, xMax, yMax) {
	this.triangles = [];
	this.newTriangles = [];
	this.bbox = {
		x1 : xMin,
		y1 : yMin,
		x2 : xMax,
		y2 : yMax
	};
	this.CreateBoundingTriangle();
	this.edges = [];
	this.vertices = [];
	this.legalizes = 0;
	this.collapses = 0;
}

Clustering.prototype.locate = function(v) {
	if (this.boundingTriangle.descendants.length == 0) {
		return this.boundingTriangle;
	}
	var triangles = this.boundingTriangle.descendants;
	while (true) {
		for (var i = 0; i < triangles.length; i++) {
			var simplex = triangles[i].interior(v);
			if (simplex == null) {
				continue;
			}
			if ( simplex instanceof Vertex || this.isLeaf(triangles[i])) {
				return simplex;
			}
			triangles = triangles[i].descendants;
			break;
		}
	}
}

Clustering.prototype.legalize = function(v, e, t0_old) {
	if (!e.v0.legal && !e.v1.legal) {
		return;
	}
	this.legalizes++;
	var flip = false;
	var t1_old, tr1;
	if (e.leftFace == t0_old && e.rightFace.inCircumcircle(v)) {
		flip = true;
		t1_old = e.rightFace;
	} else if (e.rightFace == t0_old && e.leftFace.inCircumcircle(v)) {
		flip = true;
		t1_old = e.leftFace;
	}
	if (flip) {
		var tr0 = t0_old.getTriple(e);
		var tr1 = t1_old.getTriple(e);
		var e_flip = new Edge(tr0.u, tr1.u);
		var poly = [];
		poly.push(e.v0);
		poly.push(e_flip.v0);
		poly.push(e.v1);
		poly.push(e_flip.v1);
		if (!this.JordanTest(poly, e_flip)) {
			return;
		}
		e.legal = false;
		this.edges.push(e_flip);
		var t0_new = new Triangle([e_flip, tr0.e_p, tr1.e_s]);
		var t1_new = new Triangle([e_flip, tr1.e_p, tr0.e_s]);
		e_flip.setFaces(t0_new, t1_new);
		tr0.e_p.replaceFace(t0_old, t0_new);
		tr1.e_s.replaceFace(t1_old, t0_new);
		tr1.e_p.replaceFace(t1_old, t1_new);
		tr0.e_s.replaceFace(t0_old, t1_new);
		t0_old.descendants = [t0_new, t1_new];
		t1_old.descendants = [t0_new, t1_new];
		this.legalize(v, t0_new.edges[2], t0_new);
		this.legalize(v, t1_new.edges[1], t1_new);
	}
}

Clustering.prototype.add = function(v) {
	this.addVertex(v, this.locate(v));
}

Clustering.prototype.addVertex = function(v, simplex) {
	if ( simplex instanceof Vertex) {
		simplex.merge(simplex, v);
	} else if ( simplex instanceof Edge) {
		this.vertices.push(v);
		simplex.legal = false;
		var tr0 = simplex.leftFace.getTriple(simplex);
		var tr1 = simplex.rightFace.getTriple(simplex);
		var e0 = new Edge(v, tr0.u);
		var e1 = new Edge(v, simplex.leftFace.getNext(tr0.u));
		var e2 = new Edge(v, tr1.u);
		var e3 = new Edge(v, simplex.rightFace.getNext(tr1.u));
		var t0 = new Triangle([e0, tr0.e_p, e1]);
		var t1 = new Triangle([e1, tr1.e_s, e2]);
		var t2 = new Triangle([e2, tr1.e_p, e3]);
		var t3 = new Triangle([e3, tr0.e_s, e0]);
		simplex.leftFace.descendants = [t0, t3];
		simplex.rightFace.descendants = [t1, t2];
		this.edges.push(e0);
		this.edges.push(e1);
		this.edges.push(e2);
		this.edges.push(e3);
		e0.setFaces(t0, t3);
		e1.setFaces(t0, t1);
		e2.setFaces(t1, t2);
		e3.setFaces(t2, t3);
		tr0.e_p.replaceFace(simplex.leftFace, t0);
		tr1.e_s.replaceFace(simplex.rightFace, t1);
		tr1.e_p.replaceFace(simplex.rightFace, t2);
		tr0.e_s.replaceFace(simplex.leftFace, t3);
		this.legalize(v, tr0.e_p, t0);
		this.legalize(v, tr1.e_s, t1);
		this.legalize(v, tr1.e_p, t2);
		this.legalize(v, tr0.e_s, t3);
	} else {
		this.vertices.push(v);
		var e_i = new Edge(simplex.vertices[0], v);
		var e_j = new Edge(simplex.vertices[1], v);
		var e_k = new Edge(simplex.vertices[2], v);
		this.edges.push(e_i);
		this.edges.push(e_j);
		this.edges.push(e_k);
		var t0 = new Triangle([e_i, simplex.edges[0], e_j]);
		var t1 = new Triangle([e_j, simplex.edges[1], e_k]);
		var t2 = new Triangle([e_k, simplex.edges[2], e_i]);
		e_i.setFaces(t0, t2);
		e_j.setFaces(t0, t1);
		e_k.setFaces(t1, t2);
		simplex.replaceBy([t0, t1, t2]);
		this.legalize(v, simplex.edges[0], t0);
		this.legalize(v, simplex.edges[1], t1);
		this.legalize(v, simplex.edges[2], t2);
	}
}

Clustering.prototype.isLeaf = function(t) {
	return t.descendants.length == 0;
}

Clustering.prototype.CreateBoundingTriangle = function() {
	var dx = (this.bbox.x2 - this.bbox.x1 ) * 10;
	var dy = (this.bbox.y2 - this.bbox.y1 ) * 10;
	var v0 = new Vertex(this.bbox.x1 - dx, this.bbox.y1 - dy * 3);
	var v1 = new Vertex(this.bbox.x2 + dx * 3, this.bbox.y2 + dy);
	var v2 = new Vertex(this.bbox.x1 - dx, this.bbox.y2 + dy);
	var e0 = new Edge(v1, v0);
	var e1 = new Edge(v0, v2);
	var e2 = new Edge(v2, v1);
	v0.legal = false;
	v1.legal = false;
	v2.legal = false;
	this.boundingTriangle = new Triangle([e0, e1, e2]);
	var inf = new Triangle([e0, e1, e2]);
	e0.setFaces(this.boundingTriangle, inf);
	e1.setFaces(this.boundingTriangle, inf);
	e2.setFaces(this.boundingTriangle, inf);
}

Clustering.prototype.mergeVertices = function(e) {
  if(e.length == 1.0636266792645501) {
  console.log("#################### 313 Clustering.prototype.mergeVertices");
  console.log(e);
	console.log("#################### 313 Clustering.prototype.mergeVertices problem edge");
  }
	this.collapses++;
	var s0 = e.v0.size;
	var s1 = e.v1.size;
	var x = (e.v0.x * s0 + e.v1.x * s1 ) / (s0 + s1 );
	var y = (e.v0.y * s0 + e.v1.y * s1 ) / (s0 + s1 );
	var v = new Vertex(x, y, e.v0.elements.length, e.v0.binning);
	v.merge(e.v0, e.v1);

	e.v0.legal = false;
	e.v1.legal = false;

	var hole = [];
	var oldFacets = [];
	e.legal = false;

	var vertices = [];
	var traverse = function(eLeft, eRight, triangle) {
		eLeft.legal = false;
		do {
			var triple;
			if (eLeft.leftFace == triangle) {
				triple = eLeft.rightFace.getTriple(eLeft);
				oldFacets.push(eLeft.rightFace);
				triple.e_s.removeFace(eLeft.rightFace);
				triangle = eLeft.rightFace;
			} else {
				triple = eLeft.leftFace.getTriple(eLeft);
				oldFacets.push(eLeft.leftFace);
				triple.e_s.removeFace(eLeft.leftFace);
				triangle = eLeft.leftFace;
			}
			if (arrayIndex(hole, triple.e_s) == -1) {
				hole.push(triple.e_s);
			}
			vertices.push(triple.u);
			eLeft = triple.e_p;
			eLeft.legal = false;
		} while( eLeft != eRight );
	}
	var tr0 = e.leftFace.getTriple(e);
	var tr1 = e.rightFace.getTriple(e);
	oldFacets.push(e.leftFace);
	oldFacets.push(e.rightFace);
	traverse(tr0.e_p, tr1.e_s, e.leftFace);
	traverse(tr1.e_p, tr0.e_s, e.rightFace);

	var hd = new Clustering(this.bbox.x1 - 10, this.bbox.y1 - 10, this.bbox.x2 + 10, this.bbox.y2 + 10);
	var hull = [];
	for (var i in hole ) {
		if (!(hole[i].leftFace == null && hole[i].rightFace == null)) {
			hull.push(hole[i].v0);
			hull.push(hole[i].v1);
		}
	}
	var hullVertices = [];
	var distinct = [];
	for (var i in vertices ) {
		if (arrayIndex(distinct, vertices[i]) == -1) {
			hd.add(vertices[i]);
			distinct.push(vertices[i]);
		}
		if (arrayIndex(hull, vertices[i]) != -1) {
			hullVertices.push(vertices[i]);
		}
	}

	var newFacets = [];
	var isBoundary = function(e) {
		for (var i = 0; i < hole.length; i++) {
			if (hole[i].equals(e)) {
				return i;
			}
		}
		return -1;
	}
	var holeEdges = new Array(hole.length);
	var nonHoleEdges = [];

	for (var i = 0; i < hd.edges.length; i++) {
		var e = hd.edges[i];
		var b = isBoundary(e);
		if (b != -1) {
			if (!e.legal) {
				var t1 = e.leftFace.getTriple(e);
				var t2 = e.rightFace.getTriple(e);
				var edge = new Edge(t1.u, t2.u);
				for (var j = 0; j < hd.edges.length; j++) {
					if (hd.edges[j].equals(edge) && hd.edges[j].legal) {
						hd.edges[j].legal = false;
						break;
					}
				}
				t1.e_p.setFace(e.leftFace);
				t1.e_s.setFace(e.leftFace);
				t2.e_p.setFace(e.rightFace);
				t2.e_s.setFace(e.rightFace);

				e.legal = true;
			}
			holeEdges[b] = e;
		} else {
			nonHoleEdges.push(e);
		}
	}


	for (var i = 0; i < holeEdges.length; i++) {
		var e = holeEdges[i];
		if(e == null) {
	  console.log("#################### 313 Clustering.prototype.mergeVertices holeEdges.length="+holeEdges.length);
	  console.log("#################### 313 Clustering.prototype.mergeVertices undefined index="+i);
	  console.log(holeEdges);
		}
		if (hole[i].leftFace == null) {
			hole[i].leftFace = e.leftFace;
			hole[i].leftFace.replace(e, hole[i]);
			if (arrayIndex(newFacets, hole[i].leftFace) == -1) {
				newFacets.push(hole[i].leftFace);
			}
		}
		if (hole[i].rightFace == null) {
			hole[i].rightFace = e.rightFace;
			hole[i].rightFace.replace(e, hole[i]);
			if (arrayIndex(newFacets, hole[i].rightFace) == -1) {
				newFacets.push(hole[i].rightFace);
			}
		}
	}

	for (var i = 0; i < nonHoleEdges.length; i++) {
		var e = nonHoleEdges[i];
		if (!e.legal) {
			continue;
		}
		if (this.JordanTest(hullVertices, e)) {
			this.edges.push(e);
			if (arrayIndex(newFacets, e.rightFace) == -1) {
				newFacets.push(e.rightFace);
			}
			if (arrayIndex(newFacets, e.leftFace) == -1) {
				newFacets.push(e.leftFace);
			}
		}
	}

	for (var i in oldFacets ) {
		oldFacets[i].descendants = newFacets;
	}

	for (var i = 0; i < newFacets.length; i++) {
		var simplex = newFacets[i].interior(v);
		if (simplex == null) {
			continue;
		} else {
			this.addVertex(v, simplex);
			break;
		}
	}

	return v;

}

Clustering.prototype.JordanTest = function(pol, e) {
	var p = new Vertex((e.v0.x + e.v1.x) * 0.5, (e.v0.y + e.v1.y) * 0.5);
	var inside = false;
	var i, j = pol.length - 1;
	for ( i = 0; i < pol.length; j = i++) {
		var p1 = pol[i];
		var p2 = pol[j];
		if ((((p1.y <= p.y) && (p.y < p2.y)) || ((p2.y <= p.y) && (p.y < p1.y))) && (p.x < (p2.x - p1.x) * (p.y - p1.y) / (p2.y - p1.y) + p1.x))
			inside = !inside;
	}
	return inside;
}

Clustering.prototype.mergeForResolution = function(resolution, circleGap) {
  console.log("#################### 314 Clustering.prototype.mergeForResolution: resolution="+resolution+" / circleGap=" +circleGap);
  if(resolution == 0.5971642833948135){
	  console.log("#################### 314 Clustering.prototype.mergeForResolution: this.edges: "+this.edges.length);
	  console.log(this.edges);
  }
	this.deleteEdges = new BinaryHeap(function(e) {
		return e.weight;
	});
	this.weightEdges(resolution, circleGap);
	
if(resolution == 0.5971642833948135){
	console.log("#################### 314 Clustering.prototype.mergeForResolution: weightEdges() this.edges: "+this.edges.length);
	console.log(this.edges);
}

	var index = 0;
	while (this.deleteEdges.size() > 0) {
		var e = this.deleteEdges.pop();
		if (e.legal) {
			var l = this.edges.length;
			var newVertex = this.mergeVertices(e);
			newVertex.CalculateRadius(resolution);
			for (var k = l; k < this.edges.length; k++) {
				var eNew = this.edges[k];
				if (eNew.legal) {
					eNew.weight = eNew.length / (eNew.v0.radius + eNew.v1.radius + circleGap * resolution );
					if (eNew.weight < 1) {
						this.deleteEdges.push(eNew);
					}
				}
			}
		}
	}
}

Clustering.prototype.weightEdges = function(resolution, circleGap) {
	for (var i = 0; i < this.vertices.length; i++) {
		if (this.vertices[i].legal) {
			this.vertices[i].CalculateRadius(resolution);
		}
	}
	var newEdges = [];
	for (var i = 0; i < this.edges.length; i++) {
		var e = this.edges[i];
		if (e.legal) {
			if (!e.v0.legal || !e.v1.legal) {
				e.weight = 1;
			} else {
				e.weight = e.length / (e.v0.radius + e.v1.radius + circleGap * resolution );
				if (e.weight < 1) {
					this.deleteEdges.push(e);
				}
			}
			newEdges.push(e);
		}
	}
	this.edges = newEdges;
}

Clustering.prototype.ValidityTest = function() {
	console.info("Test 1: Valid Delaunay ...");
	/*
	var leafs = [];
	var triangles = this.boundingTriangle.descendants;
	var j = 0;
	while( triangles.length > j ){
	var t = triangles[j];
	if( t.taken == undefined ){
	t.taken = true;
	if( this.isLeaf(t) ){
	leafs.push(t);
	}
	else {
	triangles = triangles.concat(t.descendants);
	}
	}
	j++;
	}
	console.info("  Number of Triangles: "+leafs.length);

	var c = 0;
	for( i in this.edges ){
	if( this.edges[i].legal ){
	c++;
	}
	}
	console.info("  Number of Edges: "+c);*/
	/*

	for( var i=0; i<leafs.length; i++ ){
	for( var j=0; j<vertices.length; j++ ){
	if( !leafs[i].contains(vertices[j]) && leafs[i].inCircumcircle(vertices[j]) ){
	console.info(leafs[i],vertices[j]);

	}
	}
	}
	*/

	//console.info("Test 2: Edges Facets (null) ...");
	for (i in this.edges ) {
		var e = this.edges[i];
		if (e.leftFace == null || e.rightFace == null) {
			console.info(e);
			alert();
		}
	}

	//console.info("Test 3: Edges Facets ...");
	var leftOf = function(v1, v2, v) {
		var x2 = v1.x - v2.x;
		var x3 = v1.x - v.x;
		var y2 = v1.y - v2.y;
		var y3 = v1.y - v.y;
		if (x2 * y3 - y2 * x3 < 0) {
			return true;
		}
		return false;
	}
	var c = 0;
	for (i in this.edges ) {
		var e = this.edges[i];
		var t1 = e.leftFace.getTriple(e);
		var t2 = e.rightFace.getTriple(e);
		if (e.v0.y == e.v1.y) {
			if (t1.u.y > t2.u.y) {
				console.info("equal y conflict ...");
				console.info(e);
				alert();
				c++;
			}
		} else {
			var v1, v2;
			if (e.v0.y > e.v1.y) {
				v1 = e.v0;
				v2 = e.v1;
			} else {
				v1 = e.v1;
				v2 = e.v0;
			}
			if (!leftOf(v1, v2, t1.u)) {
				console.info("left right conflict ... left is right");
				console.info(e);
				alert();
				c++;
			}
			if (leftOf(v1, v2, t2.u)) {
				console.info("left right conflict ... right is left");
				console.info(e);
				alert();
				c++;
			}
		}
	}
	//console.info("Number of Edges: "+this.edges.length);
	//console.info("Number of Conflicts: "+c);

	for (i in this.edges ) {
		if (this.edges[i].legal) {
			var e = this.edges[i];
			var tr0 = e.leftFace.getTriple(e);
			var tr1 = e.rightFace.getTriple(e);
			if (!tr0.e_p.legal || !tr0.e_s.legal || !tr1.e_p.legal || !tr1.e_s.legal) {
				console.info(e);
				console.info("conflict in edge continuity");
				return;
			}
		}
	}

}
function BinaryHeap(scoreFunction) {
	this.content = [];
	this.scoreFunction = scoreFunction;
}

BinaryHeap.prototype = {
	push : function(element) {
		// Add the new element to the end of the array.
		this.content.push(element);
		// Allow it to bubble up.
		this.bubbleUp(this.content.length - 1);
	},

	pop : function() {
		// Store the first element so we can return it later.
		var result = this.content[0];
		// Get the element at the end of the array.
		var end = this.content.pop();
		// If there are any elements left, put the end element at the
		// start, and let it sink down.
		if (this.content.length > 0) {
			this.content[0] = end;
			this.sinkDown(0);
		}
		return result;
	},

	remove : function(node) {
		var len = this.content.length;
		// To remove a value, we must search through the array to find
		// it.
		for (var i = 0; i < len; i++) {
			if (this.content[i] == node) {
				// When it is found, the process seen in 'pop' is repeated
				// to fill up the hole.
				var end = this.content.pop();
				if (i != len - 1) {
					this.content[i] = end;
					if (this.scoreFunction(end) < this.scoreFunction(node))
						this.bubbleUp(i);
					else
						this.sinkDown(i);
				}
				return;
			}
		}
		throw new Error("Node not found.");
	},

	size : function() {
		return this.content.length;
	},

	bubbleUp : function(n) {
		// Fetch the element that has to be moved.
		var element = this.content[n];
		// When at 0, an element can not go up any further.
		while (n > 0) {
			// Compute the parent element's index, and fetch it.
			var parentN = Math.floor((n + 1) / 2) - 1, parent = this.content[parentN];
			// Swap the elements if the parent is greater.
			if (this.scoreFunction(element) < this.scoreFunction(parent)) {
				this.content[parentN] = element;
				this.content[n] = parent;
				// Update 'n' to continue at the new position.
				n = parentN;
			}
			// Found a parent that is less, no need to move it further.
			else {
				break;
			}
		}
	},

	sinkDown : function(n) {
		// Look up the target element and its score.
		var length = this.content.length, element = this.content[n], elemScore = this.scoreFunction(element);

		while (true) {
			// Compute the indices of the child elements.
			var child2N = (n + 1) * 2, child1N = child2N - 1;
			// This is used to store the new position of the element,
			// if any.
			var swap = null;
			// If the first child exists (is inside the array)...
			if (child1N < length) {
				// Look it up and compute its score.
				var child1 = this.content[child1N], child1Score = this.scoreFunction(child1);
				// If the score is less than our element's, we need to swap.
				if (child1Score < elemScore)
					swap = child1N;
			}
			// Do the same checks for the other child.
			if (child2N < length) {
				var child2 = this.content[child2N], child2Score = this.scoreFunction(child2);
				if (child2Score < (swap == null ? elemScore : child1Score))
					swap = child2N;
			}

			// If the element needs to be moved, swap it, and continue.
			if (swap != null) {
				this.content[n] = this.content[swap];
				this.content[swap] = element;
				n = swap;
			}
			// Otherwise, we are done.
			else {
				break;
			}
		}
	}
};


/**
 * @class Publisher
 * Publish/Subscribe mechanism
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 */
if ( typeof GeoPublisher == 'undefined') {

	GeoPublisher = function() {

		var _topics = {};

		var f_get = function(topic) {
			var value = _topics[topic];
			if (!value) {
				value = _topics[topic] = [];
			}
			return value;
		};

		var f_publish = function(topic, data, publisher) {
			var subscribers = f_get(topic);
			for (var i = 0; i < subscribers.length; i++) {
				if (publisher == null || subscribers[i].client != publisher) {
					subscribers[i].callback(data);
				}
			}
		};

		var f_subscribe = function(topic, subscriber, callbackFn) {
			var subscribers = f_get(topic);
			subscribers.push({ client : subscriber, callback : callbackFn });
		};

		var f_unsubscribe = function(topic, unsubscriber) {
			var subscribers = f_get(topic);
			for (var i = 0; i < subscribers.length; i++) {
				if (subscribers[i].client == unsubscriber) {
					subscribers.splice(i, 1);
					return;
				}
			}
		};

		return {
            GeoPublish: f_publish,
            GeoSubscribe: f_subscribe,
            GeoUnsubscribe: f_unsubscribe,
            topics : _topics
        };
	}();
//    if (typeof console != "undefined") {
//        console.log("GeoPublisher created");
//    }
}



if ( typeof InstitutionsMapModel == 'undefined' ) {

    InstitutionsMapModel = (function () {

        var _initialized = false;
        var _urlPrefix;

        var _deBoundaries = {
            minLat: 47.2703, // 46.69258,
            minLon:  5.8667, //  2.48328,
            maxLat: 55.0556, // 55.86127,
            maxLon: 15.0419  // 18.5233
        };

        var _mapWidget;         // 
        var _allMapData;        // array of institutionData
        var _institutionsById;  // an object/map: institutionData.id->institutionData

        var _sectorSelection;   // as last published on InstitutionsSectors
        var _selectedSectors;   // an object to identify the selected sectors
        var _filterSectors;     // sectors used for filtering
        var _sectorsMapData;    // the data to display depending on selected sectors
        var _allSectors;        // count and name of sectors selected

        var _isIE8; // is this script running in MS Internetexplorer Version 8 or less
        var _clusterSet; // the set of clustered institutions

        // Returns the version of Internet Explorer or -1
        // (indicating the use of another browser).
        var f_getInternetExplorerVersion = function () {
            var rv = -1; // Return value assumes failure.
            if (navigator.appName == 'Microsoft Internet Explorer')
            {
                var ua = navigator.userAgent;
                var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
                if (re.exec(ua) != null)
                rv = parseFloat( RegExp.$1 );
            }
            return rv;
        }

        var f_isInitialized = function() {
            return _initialized;
        };

        /**
         * @param {string} mapDivId the id if the div to display the map in.
         * @param {language} "en" or "de".
         * @param {object} optional. specific Map options
         */
        var f_initialize = function (mapDivId, language, startupOptions) {
  console.log("#################### 31 f_initialize");
            GeoTemConfig.determineAndSetUrlPrefix("InstitutionsMapModel.js");

            var MSVersion = f_getInternetExplorerVersion();

            _isIE8 = ( MSVersion > -1 && MSVersion <= 8.0 );
//            if (_isIE8 && typeof console != "undefined") {
//                console.warn("running Internet Explorer 8 or less");
//            }
            
//            if (typeof console != 'undefined') {
//                console.log("f_initialize mapDivId=" + mapDivId + " language=" + language);
//                console.log("f_initialize _urlPrefix=" + f_getUrlPrefix());
//            }

            GeoTemConfig.language = language;
            GeoTemConfig.allowFilter = false;


//            if (typeof console != 'undefined') {
//                console.log("MapWidget displayed");
//            }

            _initialized = true;

        };

        var f_makeSectorsObject = function (selectorList) {
  console.log("#################### 32 f_makeSectorsObject");
            var sectorObject = {};
            for (var xel = 0; xel < selectorList.length; xel++) {
                var el = selectorList[xel];
                sectorObject[el.sector] = {count: 0, name: el.name};
            };
  console.log("#################### 32 f_makeSectorsObject sectorObject="+sectorObject);
            return sectorObject;
        };

        var f_selectSectors = function (aSectorSelection) {
  console.log("#################### 33 f_selectSectors");
//            if (typeof console != 'undefined') {
//                console.log("sectors selected: " + aSectorSelection.selected.length + " deselected: " + aSectorSelection.deselected.length);
//            }
            _sectorSelection = aSectorSelection; // save the published state

            var time0 = (new Date()).getTime();

            _selectedSectors = f_makeSectorsObject(_sectorSelection.selected);
            var deselectedSectors = f_makeSectorsObject(_sectorSelection.deselected);
            _allSectors = {};
		    $.extend(_allSectors, _selectedSectors);
		    $.extend(_allSectors, deselectedSectors);

            _filterSectors = (_sectorSelection.selected.length === 0) ? deselectedSectors:_selectedSectors;
            if (_isIE8) {
                _clusterSet = f_getMapNameAndClusterSet(_filterSectors);
                var arr = [];
                arr.push(_clusterSet);
                GeoPublisher.GeoPublish('filter', arr, null);
            } else {
                _sectorsMapData = [];
                for (var xel = 0; xel <_allMapData.length; xel++) {
                    var el = _allMapData[xel];
                    var elSector = el.description.node.sector;
                    if (_filterSectors[elSector] !== undefined) {
                        _sectorsMapData.push(el);
                        _filterSectors[elSector].count++;
                    };
                };

                var datasets = [];
                datasets.push(new Dataset(_sectorsMapData, "institutions"));
  console.log("#################### 33 f_selectSectors datasets="+datasets);
  console.log(datasets);
                GeoPublisher.GeoPublish('filter', datasets, null);
            }

        };

        var f_sectorName = function(sectorId) {
            if (_allSectors) {
                return _allSectors[sectorId].name;
            } else {
                return undefined;
            }
        };

        var f_isSelected = function(sectorId) {
            if (_selectedSectors) {
                return _selectedSectors[sectorId];
            } else {
                return undefined;
            }
        }

        var f_getMapNameAndClusterSet = function(filterSet) {
  console.log("#################### 34 f_getMapNameAndClusterSet");
            var urlPrefix = GeoTemConfig.urlPrefix + "ddb-map/";
            var sectorsName = "";
            var sortedSectorNames = [];
            for (var fkey in filterSet) {
                sortedSectorNames.push(fkey.replace('_',''));
            }
            sortedSectorNames.sort();
            sectorsName = ('_' + sortedSectorNames.join('_'));

            var clusterUrl = urlPrefix + "cluster" + sectorsName + ".json";
            var mapUrl = urlPrefix + "map" + sectorsName + ".jpg";
//            if (typeof console != "undefined") {
//                console.log(clusterUrl);
//            }
            clusterSet = GeoTemConfig.getJson(clusterUrl);
//            if (typeof console != "undefined") {
//                console.log("clusterSet name: " + clusterSet.name);
//                console.log("clusterSet size: " + clusterSet.clusters.length + " clusters");
//            }

            _ie8MapCluster = {
                clusterUrl: clusterUrl,
                mapUrl: mapUrl,
                clusterSet: clusterSet
            };
            return _ie8MapCluster;
        };

        /**
         * process the data about all institutions and build the structures needed
         * for map presentation and sorting subsets of all institutions
         */
        var f_prepareInstitutionsData = function (institutionMapData) {
  console.log("#################### 35 f_prepareInstitutionsData institutionMapData: "+institutionMapData.size);
  //console.log(institutionMapData);

            var allInstitutionsArray = flatten(institutionMapData.institutions);
  console.log("#################### 35 f_prepareInstitutionsData allInstitutionsArray: "+allInstitutionsArray.length);
  //console.log(allInstitutionsArray);

            _allMapData = GeoTemConfig.loadJson(allInstitutionsArray);
  console.log("#################### 35 f_prepareInstitutionsData _allMapData: "+_allMapData.length);
  //console.log(_allMapData);

            _institutionsById = {};
            for (var xel = 0; xel < _allMapData.length; xel++) {
                var el = _allMapData[xel];
                _institutionsById[el.description.node.id] = el;
                el.description.node.detailViewUri = "institutions/item/" + el.description.node.id;
            };

            /*
             * make an object that can be handled by
             * GeoTemCoConfig.loadJSON(:Array)
             */
            function tree2mapElement(treeNode,superNode,number,indentLevel) {
                var latitude = treeNode.latitude;
                var longitude = treeNode.longitude;
                if (latitude < _deBoundaries.minLat || latitude > _deBoundaries.maxLat
                    || longitude < _deBoundaries.minLon || longitude > _deBoundaries.maxLon
                   ) {
                    return null;
                }

                var descr = {
                    indentLevel: indentLevel,
                    number: number,
                    node: treeNode,
                    superNode: superNode
                }

                return { // contains required elements and element 'description'
                    id: treeNode.id,
                    place: treeNode.name,
                    sector: treeNode.sector,
                    lat: latitude,
                    lon: longitude,
                    description: descr
                }
            };

            function flatten(aTree) {
                var result = flatten_r(aTree,null,0,{count: 0, list: []});
//                if (typeof console != 'undefined') {
//                    console.log("number of institutions: " + result.count);
//                }
                return result.list;
            }
            function flatten_r(aTree, superNode, level, result) {
                for (var i = 0; i < aTree.length; i++) {
                    var treeNode = aTree[i];
                    if (treeNode !== null) {
                        var mapElement = tree2mapElement(treeNode,
                                                         superNode,
                                                         result.count,
                                                         level);
                        result.count++;
                        if (mapElement !== null) {
                            result.list.push(mapElement);
                        } else {
//                            if (typeof console !== "undefined") {
//                                console.log("out of germanys bounding box:");
//                            }
                        }
                        var children = treeNode.children;
                        if (children instanceof Array) {
                            flatten_r(children, mapElement.id, level+1, result);
                        }
                    }
                }
                return result;
            };
        };

        var f_filterSectors = function() {
            if (_filterSectors) {
                return _filterSectors;
            } else {
                return undefined;
            }
        };

        var f_getUrlPrefix = function() {
            return GeoTemConfig.urlPrefix;
        }

        var f_mapWidget = function() {
            return _mapWidget;
        }

        var f_getCluster = function(index) {
            return _clusterSet.clusterSet.clusters[index];
        }

        var f_getNode = function(id) {
            return _institutionsById[id];
        }

		var f_getAllMapData = function(id) {
            return _allMapData;
        }

        return {
            initialize: f_initialize,
            isInitialized: f_isInitialized,
            getUrlPrefix: f_getUrlPrefix,
            prepareInstitutionsData: f_prepareInstitutionsData,
            selectSectors: f_selectSectors,
            isSelected: f_isSelected,
            sectorName: f_sectorName,
            filterSectors: f_filterSectors,
            getNode : f_getNode,
            getCluster : f_getCluster,
            mapWidget : f_mapWidget,
			getAllMapData : f_getAllMapData
        };
    })();
}
