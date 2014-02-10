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
  // TimeFacet is only available in results page
  if (jsPageName == "results") {
    var timeFacet = new TimeFacet($(".facets-list"));
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
  
  console.log("Created a new instance of TimeSpan");
}


$.extend(TimeSpan.prototype, {
  print : function(facetsContainer) {
    console.log("TimeSpan.print()");
    var currObjInstance = this;
    
    console.log("fromDay: " + currObjInstance.fromDay);
    console.log("fromMonth: " + currObjInstance.fromMonth);
    console.log("fromYear: " + currObjInstance.fromYear);
    console.log("tillDay: " + currObjInstance.tillDay);
    console.log("tillMonth: " + currObjInstance.tillMonth);
    console.log("tillYear: " + currObjInstance.tillYear);
  },
  hasFromDate: function(){
    console.log("TimeSpan.hasFromDate()");
    var currObjInstance = this;
    
    return currObjInstance.fromYear !== null;
  },
  hasTillDate: function(){
    console.log("TimeSpan.hasTillDate()");
    var currObjInstance = this;
    
    return currObjInstance.tillYear !== null;
  },
  getFromDate: function(){
    console.log("TimeSpan.getFromDate()");
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
  getTillDate: function(){
    console.log("TimeSpan.getTillDate()");
    var currObjInstance = this;
    
    //Id no year is set -> return null
    if (!currObjInstance.hasTillDate) {
      return null;
    }
    
    //if no day is set tillDay to ???
    if (currObjInstance.tillDay === null) {
      //TODO get right day from calendar
      currObjInstance.tillDay = 28;
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
function TimeFacet(facetsContainer) {
  this.init(facetsContainer);
  console.log("Created a new instance of TimeFacet");
}

/**
 * TimeFacet prototype extension with JQuery
 */
$.extend(TimeFacet.prototype, {
  /**
   * TimeFacet attributes
   */
  opened: false,
  selectedTimeSpan: null,
  localisation : "unscharf",
  facetsContainer: null,

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsContainer) {
    console.log("init() with facetsContainer " + this.facetsContainer);
    
    var currObjInstance = this;
    this.facetsContainer = facetsContainer;        
    
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
    
  },

  /**
   * This method is responsible for opening and closing the TimeFacet
   */
  open : function() {
    console.log("open");
    
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form"); 
    var timeFacetDiv = $(".time-facet");
    
    if (!currObjInstance.opened) {
      currObjInstance.opened = true;
      timespanFormDiv.fadeIn('slow');
      timeFacetDiv.addClass('active');
    } else {
      currObjInstance.opened = false;
      timespanFormDiv.fadeOut('slow');
      timeFacetDiv.removeClass('active');
    }
  },
  
  /**
   * Checks the values of the form and adds a new timespan.
   */
  addTimeSpan : function() {
    console.log("addTimeSpan");
    
    var currObjInstance = this;

    //Retrive the values from the timespan form
    var fromDayValue = $("#fromDay").val() !== "Day" ? $("#fromDay").val() : null;
    var fromMonthValue = $("#fromMonth").val() !== "Month" ? $("#fromMonth").val() : null;
    var fromYearValue = $("#fromYear").val() !== "Year" ? $("#fromYear").val() : null;

    var tillDayValue = $("#tillDay").val() !== "Tag" ? $("#tillDay").val() : null;
    var tillMonthValue = $("#tillMonth").val() !== "Monat" ? $("#tillMonth").val() : null;
    var tillYearValue = $("#tillYear").val() !== "Jahr" ? $("#tillYear").val() : null;
    
    
    var newTimeSpan = new TimeSpan(fromDayValue, fromMonthValue, fromYearValue, tillDayValue, tillMonthValue, tillYearValue);
    newTimeSpan.print();
    
    currObjInstance.selectedTimeSpan = newTimeSpan;
    console.log(currObjInstance.selectedTimeSpan);
    
    currObjInstance.renderTimeSpan();
  },
  
  /**
   * Renders the selected TimeSpans above the form div
   */
  renderTimeSpan : function() {
    console.log("renderTimeSpans");
    
    var currObjInstance = this;    
    var timeSpanList = $(".time-facet ul");
    
    //Remove all existing entries from the list
    timeSpanList.empty();

    console.log(timeSpanList);
    
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
});// End extend TimeFacet prototype
