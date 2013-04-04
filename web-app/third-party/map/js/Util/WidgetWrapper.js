/*
* WidgetWrapper.js
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
 * @class WidgetWrapper
 * Interface-like implementation for widgets interaction to each other; aimed to be modified for dynamic data sources
 * @author Stefan Jänicke (stjaenicke@informatik.uni-leipzig.de)
 * @release 1.0
 * @release date: 2012-07-27
 * @version date: 2012-07-27
 *
 * @param {Object} widget either a map, time or table widget
 */
function WidgetWrapper() {

	var wrapper = this;

	this.setWidget = function(widget) {
		this.widget = widget;
	}

	this.display = function(data) {
		if ( data instanceof Array) {
			GeoTemConfig.datasets = data.length;
			if ( typeof wrapper.widget != 'undefined') {
				this.widget.initWidget(data);
			}
		}
	};

	GeoPublisher.GeoSubscribe('highlight', this, function(data) {
		if (data == undefined) {
			return;
		}
		if ( typeof wrapper.widget != 'undefined') {
			wrapper.widget.highlightChanged(data);
		}
	});

	GeoPublisher.GeoSubscribe('selection', this, function(data) {
		if ( typeof wrapper.widget != 'undefined') {
			wrapper.widget.selectionChanged(data);
		}
	});

	GeoPublisher.GeoSubscribe('filter', this, function(data) {
		wrapper.display(data);
	});

	GeoPublisher.GeoSubscribe('rise', this, function(id) {
		if ( typeof wrapper.widget != 'undefined' && typeof wrapper.widget.riseLayer != 'undefined') {
			wrapper.widget.riseLayer(id);
		}
	});

	this.triggerRefining = function(datasets) {
		GeoPublisher.GeoPublish('filter', datasets, null);
	};

	this.triggerSelection = function(selectedObjects) {
		GeoPublisher.GeoPublish('selection', selectedObjects, this);
	};

	this.triggerHighlight = function(highlightedObjects) {
		GeoPublisher.GeoPublish('highlight', highlightedObjects, this);
	};

	this.triggerRise = function(id) {
		GeoPublisher.GeoPublish('rise', id);
	};

};
