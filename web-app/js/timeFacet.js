/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

$(function() {
  // TimeFacet should be only available in results page
  if (jsPageName == "results") {
    var timeFacet = new TimeFacet($(".facets-list"));
    timeFacet.renderTimefacet();
  }
});


/**
 * TimeFacet Constructor Function
 */
function TimeFacet(facetsContainer) {
  this.init(facetsContainer);
  console.log("Create new instance of TimeFacet");
}

/**
 * TimeFacet prototype extension with JQuery
 */
$.extend(TimeFacet.prototype, {
  /**
   * TimeFacet attributes
   */
  fromDay : 1,
  fromMonth : 1,
  fromYear : 1939,
  tillDay : 31,
  tillMonth : 12,
  tillYear : 1945,
  localisation : "unscharf",
  facetsContainer: null,

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsContainer) {    
    this.facetsContainer = facetsContainer;    
    console.log("init() with facetsContainer " + this.facetsContainer);
  },
  
  /**
   * Renders the TimeFacet object
   */
  renderTimefacet : function() {
    console.log("renderTimefacet()");
    var currObjInstance = this;
    
    var timeFacetDiv = $(document.createElement('div'));
    timeFacetDiv.addClass('facet-item bt bb bl br');
    
    timeFacetDiv.appendTo(currObjInstance.facetsContainer);
    
    var fromDaySpinner = $(document.createElement('div')); 
  }
});// End extend TimeFacet prototype
