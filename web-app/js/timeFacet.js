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
  if (jsPageName == "results") {   
  }
});


//#############################################################################################
//TimeSpan Object
//#############################################################################################
/**
 * TimeSpan Constructor Function
 */
function TimeSpan(fromDay,fromMonth, fromYear, tillDay, tillMonth, tillYear) {
  this.fromDay = fromDay;
  this.fromMonth = fromMonth;
  this.fromYear = fromYear;
  this.tillDay = tillDay;
  this.tillMonth = tillMonth;
  this.tillYear = tillYear;
  
//  console.log("Created a new instance of TimeSpan");
}


$.extend(TimeSpan.prototype, {
  print : function(facetsContainer) {
//    console.log("TimeSpan.print()");
    var currObjInstance = this;
    
//    console.log("fromDay: " + currObjInstance.fromDay);
//    console.log("fromMonth: " + currObjInstance.fromMonth);
//    console.log("fromYear: " + currObjInstance.fromYear);
//    console.log("tillDay: " + currObjInstance.tillDay);
//    console.log("tillMonth: " + currObjInstance.tillMonth);
//    console.log("tillYear: " + currObjInstance.tillYear);
  },
  
  /**
   * A from date needs at least a value for the year.
   * @returns <code>false<code> if no fromYear is set  
   */
  hasFromDate: function(){
//    console.log("TimeSpan.hasFromDate()");
    var currObjInstance = this;
    
    return currObjInstance.fromYear !== null;
  },
  
  /**
   * A till date needs at least a value for the year.
   * @returns <code>false<code> if no tillYear is set  
   */
  hasTillDate: function(){
//    console.log("TimeSpan.hasTillDate()");
    var currObjInstance = this;
    
    return currObjInstance.tillYear !== null;
  },
  
  /**
   * Return a String representation of the from date.
   * The method autocomplete missing fromDay and fromMonth values.
   * 
   * @returns <code>false<code> if no tillYear is set  
   */
  getFromDate: function(){
//    console.log("TimeSpan.getFromDate()");
    var currObjInstance = this;
    
    //Id no year is set -> return null
    if (!currObjInstance.hasFromDate) {
      return null;
    }
    //if no day is set fromDay to 1
    if (currObjInstance.fromDay === null) {
      currObjInstance.fromDay = 1;
    }
    
    //id no month is set -> return january
    if (currObjInstance.fromMonth === null) {
      currObjInstance.fromMonth = "january";
    }
    
    return new String(currObjInstance.fromDay + " " + currObjInstance.fromMonth + " " + currObjInstance.fromYear);
  },
  
  /**
   * Return a String representation of the till date.
   * The method autocomplete missing tillDay and tillMonth values.
   * 
   * @returns <code>false<code> if no tillYear is set  
   */
  getTillDate: function(){
//    console.log("TimeSpan.getTillDate()");
    var currObjInstance = this;
    
    //Id no year is set -> return null
    if (!currObjInstance.hasTillDate) {
      return null;
    }
    
    //if no day is set tillDay to ???
    if (currObjInstance.tillDay === null) {
      currObjInstance.tillDay = 28;//TODO get right day from calendar
    }
    
    //id no month is set -> return december
    if (currObjInstance.tillMonth === null) {
      currObjInstance.tillMonth = "december";
    }
    
    return new String(currObjInstance.tillDay + " " + currObjInstance.tillMonth + " " + currObjInstance.tillYear);
  } 
  
});

//#############################################################################################
//TimeFacet Object
//#############################################################################################
/**
 * TimeFacet Constructor Function
 */
function TimeFacet(facetsManager, fetchResultsList) {
  this.init(facetsManager, fetchResultsList);
//  console.log("Created a new instance of TimeFacet");
}

/**
 * TimeFacet prototype extension with JQuery
 */
$.extend(TimeFacet.prototype, {
  /**
   * TimeFacet attributes
   */
  facetsManager: null,
  fetchResultsList: null, //Defined in searchResults.js
  opened: false,
  selectedTimeSpan: null,
  localisation : "unscharf",
  facetsContainer: null,

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsManager, fetchResultsList) {
//    console.log("init() with facetsManager " + facetsManager);
    
    var currObjInstance = this;
    this.facetsManager = facetsManager;
    this.fetchResultsList = fetchResultsList;    
    this.facetsContainer = $(".facets-list");        
    
    //On initialisation hide the timespan form
    $("#timespan-form").hide();

    // Click handler for Opening|Closing the time facet 
    $(".time-facet a.h3").click(function(event) {
      event.preventDefault();
      currObjInstance.open();
    });
    
    // Click handler for adding a new TimeSpan
    $("#add-timespan").click(function(event) {
      event.preventDefault();
      currObjInstance.addTimeSpan();
    });
    
    // Click handler for reseting the time facet
    $("#reset-timefacet").click(function(event) {
      event.preventDefault();
      currObjInstance.reset();
    });
    
  },

  /**
   * This method is responsible for opening and closing the TimeFacet
   */
  open : function() {
//    console.log("open");
    
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form"); 
    var timeFacetDiv = $(".time-facet");
    
    if (!currObjInstance.opened) {
      currObjInstance.opened = true;
      timespanFormDiv.fadeIn('slow');
      timeFacetDiv.addClass('active');
    } else {
      //Prevent from closing if a timespan has been selected
      if (currObjInstance.selectedTimeSpan === null) {
        currObjInstance.opened = false;
        timespanFormDiv.fadeOut('slow');
        timeFacetDiv.removeClass('active');
      }
    }
  },
  
  /**
   * Checks the values of the form and adds a new timespan.
   */
  addTimeSpan : function() {
//    console.log("addTimeSpan");
    
    var currObjInstance = this;

    //Retrieve the values from the timespan form
    var fromDayValue = $("#fromDay").val() !== "Day" ? $("#fromDay").val() : null;
    var fromMonthValue = $("#fromMonth").val() !== "Month" ? $("#fromMonth").val() : null;
    var fromYearValue = $("#fromYear").val() !== "Year" ? $("#fromYear").val() : null;

    var tillDayValue = $("#tillDay").val() !== "Tag" ? $("#tillDay").val() : null;
    var tillMonthValue = $("#tillMonth").val() !== "Monat" ? $("#tillMonth").val() : null;
    var tillYearValue = $("#tillYear").val() !== "Jahr" ? $("#tillYear").val() : null;
    
    var newTimeSpan = new TimeSpan(fromDayValue, fromMonthValue, fromYearValue, tillDayValue, tillMonthValue, tillYearValue);
    
    currObjInstance.selectedTimeSpan = newTimeSpan;
    console.log(currObjInstance.selectedTimeSpan);
    
    currObjInstance.renderTimeSpan();
    currObjInstance.updateWindowUrl();
  },
  
  /**
   * Resets the input elements of the form. 
   * The window URL is reseted by calling addTimeSpan() which does this implicitly  
   */
  reset : function() {
//    console.log("reset");
    
    var currObjInstance = this;
    
    $("#fromDay").val("Day");
    $("#fromMonth").val("Month");
    $("#fromYear").val("Year");

    $("#tillDay").val("Tag");
    $("#tillMonth").val("Monat");
    $("#tillYear").val("Jahr");
    
    currObjInstance.addTimeSpan();        
  },  
  
  /**
   * Renders the selected TimeSpan
   */
  renderTimeSpan : function() {
//    console.log("renderTimeSpans");
    
    var currObjInstance = this;    
    var timeSpanList = $(".time-facet ul");
    
    //Remove all existing entries from the list
    timeSpanList.empty();
    
    if (currObjInstance.selectedTimeSpan.hasFromDate()) {
      console.log("hasFromDate: " + currObjInstance.selectedTimeSpan.hasFromDate());
      
      var facetValueContainer = $(document.createElement('li'));
      var facetValueSpan = $(document.createElement('span'));      
      facetValueSpan.html("ab " + currObjInstance.selectedTimeSpan.getFromDate());
      
      facetValueSpan.appendTo(facetValueContainer);
      facetValueContainer.appendTo(timeSpanList);
    }
    
    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      console.log("hasTillDate: " + currObjInstance.selectedTimeSpan.hasTillDate());
      
      var facetValueContainer = $(document.createElement('li'));
      var facetValueSpan = $(document.createElement('span'));      
      facetValueSpan.html("bis " + currObjInstance.selectedTimeSpan.getTillDate());
      
      facetValueSpan.appendTo(facetValueContainer);
      facetValueContainer.appendTo(timeSpanList);
    }

  },
  
  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateWindowUrl: function() {
//    console.log("TimeFacet:updateWindowUrl()");
    
    var currObjInstance = this;
    var paramsArray = null;
    var selectedFacetValues = [];
    
    // Update Url (We want to keep the already selected facet values, but throw away the offset etc.)
    var facetValuesFromUrl = currObjInstance.facetsManager.getUrlVar('facetValues%5B%5D');
    if (facetValuesFromUrl == null) {
      facetValuesFromUrl = currObjInstance.facetsManager.getUrlVar('facetValues[]');
    }

    if (facetValuesFromUrl) {
//      console.log("facetValuesFromUrl: " + facetValuesFromUrl)
      $.each(facetValuesFromUrl, function(key, value) {        
        //Only add facetValues that do not start with "begin_time" or "end_time"
        if ((facetValuesFromUrl[key].indexOf("begin_time") === -1) && (facetValuesFromUrl[key].indexOf("end_time") === -1)) {
          selectedFacetValues.push(decodeURIComponent(value.replace(/\+/g, '%20')));
        }
      });
    }
    
    if (currObjInstance.selectedTimeSpan.hasFromDate()) {
      selectedFacetValues.push('begin_time=687388');//FIXME
    }
    
    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      selectedFacetValues.push('end_time=733604');//FIXME
    }
    
    //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
    paramsArray = new Array(new Array('facetValues[]', selectedFacetValues));
//    console.log("paramsArray: " + paramsArray);
    
    //Perform the search with offset 0
    paramsArray.push(new Array('offset', 0));
    
    var newUrl = addParamToCurrentUrl(paramsArray);
//    console.log("new url: " + newUrl);
    currObjInstance.fetchResultsList(newUrl, function() {});
  }
});// End extend TimeFacet prototype
