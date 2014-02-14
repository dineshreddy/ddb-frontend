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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};


//#############################################################################################
//TimeSpan Object
//#############################################################################################
/**
 * TimeSpan Constructor Function
 */
de.ddb.next.search.TimeSpan = function(fromDay,fromMonth, fromYear, tillDay, tillMonth, tillYear) {
  this.fromDay = fromDay;
  this.fromMonth = fromMonth;
  this.fromYear = fromYear;
  this.tillDay = tillDay;
  this.tillMonth = tillMonth;
  this.tillYear = tillYear;
}


$.extend(de.ddb.next.search.TimeSpan.prototype, {
  print : function(facetsContainer) {
    var currObjInstance = this;
    
    console.log("fromDay: " + currObjInstance.fromDay);
    console.log("fromMonth: " + currObjInstance.fromMonth);
    console.log("fromYear: " + currObjInstance.fromYear);
    console.log("tillDay: " + currObjInstance.tillDay);
    console.log("tillMonth: " + currObjInstance.tillMonth);
    console.log("tillYear: " + currObjInstance.tillYear);
  },
  
  /**
   * A from date needs at least a value for the year.
   * @returns <code>false<code> if no fromYear is set  
   */
  hasFromDate: function(){
    var currObjInstance = this;
    return currObjInstance.fromYear !== null;
  },
  
  /**
   * A till date needs at least a value for the year.
   * @returns <code>false<code> if no tillYear is set  
   */
  hasTillDate: function(){
    var currObjInstance = this;
    return currObjInstance.tillYear !== null;
  },
  
  /**
   * At least the year must be existing. The method completes missing fromDay and fromMonth values.
   */
  completeFromDate: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return;
    }
    //if no day is set fromDay to 1
    if (currObjInstance.fromDay === null) {
      currObjInstance.fromDay = 1;
    }
    
    //id no month is set fromMonth to 1
    if (currObjInstance.fromMonth === null) {
      currObjInstance.fromMonth = 1;
    }
  },
  
  /**
   * At least the year must be existing. The method complete missing tillDay and tillMonth values.
   */
  completeTillDate: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return;
    }
    
    //if no day is set tillDay to ???
    if (currObjInstance.tillDay === null) {
      currObjInstance.tillDay = 28;//TODO get right day from calendar
    }
    
    //id no month is set tillMonth to 12
    if (currObjInstance.tillMonth === null) {
      currObjInstance.tillMonth = 12;
    }
    
    return true;
  },

  /**
   * Formats the till date in this form: yyyy-MM-dd
   */
  formatFromDate: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return;
    }
    
    currObjInstance.completeFromDate();
    
    return currObjInstance.fromYear + "-" + currObjInstance.fromMonth + "-" + currObjInstance.fromDay;
  
  },
  
  /**
   * Formats the till date in this form: yyyy-MM-dd
   */
  formatTillDate: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return;
    }
    
    currObjInstance.completeTillDate();
    
    return currObjInstance.tillYear + "-" + currObjInstance.tillMonth + "-" + currObjInstance.tillDay;
  
  },
  
  /**
   * Returns a Date object for the from Date
   */
  getFromDateObject: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return null;
    }
    
    currObjInstance.completeFromDate();
    
    //Months starts from 0!
    return new Date(Date.UTC(currObjInstance.fromYear,(currObjInstance.fromMonth - 1),currObjInstance.fromDay));  
  },
  
  /**
   * Returns a Date object for the from Date 
   */
  getTillDateObject: function(){
    var currObjInstance = this;
    
    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return null;
    }
    
    currObjInstance.completeTillDate();

    //Months starts from 0!
    return new Date(Date.UTC(currObjInstance.tillYear,(currObjInstance.tillMonth - 1),currObjInstance.tillDay));
  }
  
});

//#############################################################################################
//TimeFacetHelper Object
//#############################################################################################
/**
 * TimeFacet Constructor Function
 */
de.ddb.next.search.TimeFacetHelper = function() {
}

/**
 * TimeFacetHelper prototype extension with JQuery
 * All calculations are based on this formula
 * (<Value> - 719164(Time from 0 to 01.01.1970 in Days)) * 86400000(Milliseconds of a day) = Time in Milliseconds since 01.01.1970 
 */
$.extend(de.ddb.next.search.TimeFacetHelper.prototype, {
  MILLISECONDS_DAY: 86400000,
  DAYS_FROM_YEAR_0_TO_1970: 719164,
  
  /**
   * Converts a Date object to a day representation for the time facet
   */
  convertDateObjectToFacetDays: function(date) {
    var currObjInstance = this;    
    var timeSince1970 = date.getTime()

    var days = (timeSince1970 / currObjInstance.MILLISECONDS_DAY) + currObjInstance.DAYS_FROM_YEAR_0_TO_1970;
       
    return days;
  },

  /**
   * Converts a day representation for the time facet to a Date object
   */
  convertFacetDaysToDate: function(days) {
    var time = (days - currObjInstance.DAYS_FROM_YEAR_0_TO_1970) * currObjInstance.MILLISECONDS_DAY;
    var date = Date.UTC(time);
    
    console.log("convertFacetDaysToDate time : " + date);
    
    return date;
  }
});


//#############################################################################################
//TimeFacet Object
//#############################################################################################
/**
 * TimeFacet Constructor Function
 */
de.ddb.next.search.TimeFacet = function(facetsManager) {
  this.init(facetsManager);
}

/**
 * TimeFacet prototype extension with JQuery
 */
$.extend(de.ddb.next.search.TimeFacet.prototype, {
  /* TimeFacet attributes  */
  facetsManager: null,
  opened: false,
  added: false,
  selectedTimeSpan: null,
  localisation : "unscharf",
  facetsContainer: null,
  timeFacetHelper: null,

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsManager) {
    var currObjInstance = this;
    currObjInstance.facetsManager = facetsManager;
    currObjInstance.timeFacetHelper = new de.ddb.next.search.TimeFacetHelper();
    
    currObjInstance.facetsContainer = $(".facets-list");        
    
    //During initialisation hide the timespan form
    $("#timespan-form").hide();

    // Click handler for Opening|Closing the time facet 
    $(".time-facet a.h3").click(function(event) {
      event.preventDefault();
      currObjInstance.open();
    });
    
    // Click handler for adding a new TimeSpan
    $("#add-timespan").click(function(event) {
      event.preventDefault();
      currObjInstance.assignTimeSpan();
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
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form"); 
    var timeFacetDiv = $(".time-facet");
    
    if (!currObjInstance.opened) {
      currObjInstance.opened = true;
      timespanFormDiv.fadeIn('fast');
      timeFacetDiv.addClass('active');
    } else {
      //Prevent from closing if a timespan has been selected
      if (!currObjInstance.added) {
        currObjInstance.opened = false;
        timespanFormDiv.fadeOut('fast');
        timeFacetDiv.removeClass('active');
      }
    }
  },
  
  /**
   * Checks the values of the form and adds a new timespan.
   */
  assignTimeSpan : function() {
    var currObjInstance = this;

    console.log("fromDay" + $("#fromDay").val());
    console.log("fromMonth" + $("#fromMonth").val());
    console.log("fromYear" + $("#fromYear").val());
    
    //Retrieve the values from the timespan form
    var fromDayValue = $("#fromDay").val() !== "" ? $("#fromDay").val() : null;
    var fromMonthValue = $("#fromMonth").val() !== "" ? $("#fromMonth").val() : null;
    var fromYearValue = $("#fromYear").val() !== "" ? $("#fromYear").val() : null;

    var tillDayValue = $("#tillDay").val() !== "" ? $("#tillDay").val() : null;
    var tillMonthValue = $("#tillMonth").val() !== "" ? $("#tillMonth").val() : null;
    var tillYearValue = $("#tillYear").val() !== "" ? $("#tillYear").val() : null;
    
    var newTimeSpan = new de.ddb.next.search.TimeSpan(fromDayValue, fromMonthValue, fromYearValue, tillDayValue, tillMonthValue, tillYearValue);
    
    currObjInstance.selectedTimeSpan = newTimeSpan;
    currObjInstance.added = true;
    
    currObjInstance.selectedTimeSpan.completeFromDate();
    currObjInstance.selectedTimeSpan.completeTillDate();
    
    currObjInstance.updateTimeSpanForm();
    currObjInstance.updateWindowUrl();
  },
  
  /**
   * Resets the input elements of the form. 
   * The window URL is reseted by calling assignTimeSpan() which does this implicitly  
   */
  reset : function() {
    var currObjInstance = this;
    
    $("#fromDay").val("");
    $("#fromMonth").val("");
    $("#fromYear").val("");

    $("#tillDay").val("");
    $("#tillMonth").val("");
    $("#tillYear").val("");
    
    currObjInstance.assignTimeSpan();
    currObjInstance.added = false;
  },  
  
  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateTimeSpanForm: function() {
    var currObjInstance = this;
    
    $("#fromDay").val(currObjInstance.selectedTimeSpan.fromDay);
    $("#fromMonth").val(currObjInstance.selectedTimeSpan.fromMonth);

    $("#tillDay").val(currObjInstance.selectedTimeSpan.tillDay);
    $("#tillMonth").val(currObjInstance.selectedTimeSpan.tillMonth);
  },
  
  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateWindowUrl: function() {
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
      var fromDate = currObjInstance.selectedTimeSpan.getFromDateObject();
      console.log(fromDate);
      var days = currObjInstance.timeFacetHelper.convertDateObjectToFacetDays(fromDate);
      
      selectedFacetValues.push('begin_time=' + days);
    }
    
    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      var tillDate = currObjInstance.selectedTimeSpan.getTillDateObject();
      console.log(tillDate);
      var days = currObjInstance.timeFacetHelper.convertDateObjectToFacetDays(tillDate);
      
      selectedFacetValues.push('end_time=' + days);
    }
    
    //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
    paramsArray = new Array(new Array('facetValues[]', selectedFacetValues));
//    console.log("paramsArray: " + paramsArray);
    
    //Perform the search with offset 0
    paramsArray.push(new Array('offset', 0));
    
    var newUrl = addParamToCurrentUrl(paramsArray);
//    console.log("new url: " + newUrl);
    de.ddb.next.search.fetchResultsList(newUrl, function() {});
  }
});// End extend TimeFacet prototype
