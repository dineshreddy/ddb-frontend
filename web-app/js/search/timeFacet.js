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
  print : function() {
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

    //id no month is set tillMonth to 12
    if (currObjInstance.tillMonth === null) {
      currObjInstance.tillMonth = 12;
    }

    //if no day is set tillDay to ???
    if (currObjInstance.tillDay === null) {
      var days_in_month = new Array(31,28,31,30,31,30,31,31,30,31,30,31);

      if(currObjInstance.tillYear %4 == 0 && currObjInstance.tillYear != 1900)
      {
         days_in_month[1]=29;
      }
      currObjInstance.tillDay = days_in_month[currObjInstance.tillMonth-1];
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
  },
  
  /**
   * Setter for the from Date
   */
  setFromDate: function(date){
    var currObjInstance = this;
    
    currObjInstance.fromDay = date.getUTCDay();
    currObjInstance.fromMonth = date.getUTCMonth() +1;
    currObjInstance.fromYear = date.getFullYear();
  },
  
  /**
   * Setter for the from Date
   */
  setTillDate: function(date){
    var currObjInstance = this;
    
    currObjInstance.tillDay = date.getUTCDay();
    currObjInstance.tillMonth = date.getUTCMonth() +1;
    currObjInstance.tillYear = date.getFullYear();
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
    var currObjInstance = this;    
    
    var time = (days - currObjInstance.DAYS_FROM_YEAR_0_TO_1970) * currObjInstance.MILLISECONDS_DAY;
    var date = new Date(time);
    
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
  timeFacetHelper: null,

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsManager) {
//    console.log("init");
    var currObjInstance = this;
    
    currObjInstance.facetsManager = facetsManager;
    currObjInstance.timeFacetHelper = new de.ddb.next.search.TimeFacetHelper();
    currObjInstance.selectedTimeSpan = new de.ddb.next.search.TimeSpan();
    
    //During initialisation hide the timespan form and disable the form elements
    $("#timespan-form").hide();
    currObjInstance.disableFromDayAndMonth(true);
    currObjInstance.disableTillDayAndMonth(true);
    
    // Click handler for Opening|Closing the time facet 
    $(".time-facet a.h3").click(function(event) {
//      console.log("click toggle");
      event.preventDefault();
      currObjInstance.toggleForm();
    });
    
    // Click handler for adding a new TimeSpan
    $("#add-timespan").click(function(event) {
//      console.log("click add");
      event.preventDefault();
      currObjInstance.assignTimeSpan(true);
    });
    
    // Click handler for reseting the time facet
    $("#reset-timefacet").click(function(event) {
//      console.log("click reset");
      event.preventDefault();
      currObjInstance.reset();
    });
    
    $("#fromYear").change(function(){ 
      if ($("#fromYear").val()) {
        currObjInstance.disableFromDayAndMonth(false);
      } else {
        currObjInstance.disableFromDayAndMonth(true);
      }
    });

    $("#tillYear").change(function(){ 
      if ($("#tillYear").val()) {
        currObjInstance.disableTillDayAndMonth(false);
      } else {
        currObjInstance.disableTillDayAndMonth(true);
      }
    });
  },

  /**
   * Dis-/Enables the day and month input field for the from date
   */
  disableFromDayAndMonth: function(disable) {
    $("#fromDay").prop('disabled', disable);
    $("#fromMonth").prop('disabled', disable);    
  },
  
  /**
   * Dis-/Enables the day and month input field for the till date
   */
  disableTillDayAndMonth: function(disable) {
    $("#tillDay").prop('disabled', disable);
    $("#tillMonth").prop('disabled', disable);    
  },
  
  /**
   * This method initialize the TimeFacet widget based on the window url.
   * It search for facetValues[] 'begin_time' and 'end_time'. Contained values will be set into the form.
   */
  initOnLoad: function() {
//    console.log("initOnLoad");

    var currObjInstance = this;
    var hasSelectedDate = false;
    
    // Search for time facetValues[] in the window url
    var facetValuesFromUrl = de.ddb.next.search.getFacetValuesFromUrl();

    if (facetValuesFromUrl) {
      $.each(facetValuesFromUrl, function(key, value) {
        
        if ((facetValuesFromUrl[key].indexOf("begin_time") === 0)) {
          var beginDays = facetValuesFromUrl[key].substr(13)
          var beginDate = currObjInstance.timeFacetHelper.convertFacetDaysToDate(beginDays);
          currObjInstance.selectedTimeSpan.setFromDate(beginDate);
          
          hasSelectedDate = true;
        }
        
        if ((facetValuesFromUrl[key].indexOf("end_time") === 0)) {
          var endDays = facetValuesFromUrl[key].substr(11)
          var endDate = currObjInstance.timeFacetHelper.convertFacetDaysToDate(endDays);
          currObjInstance.selectedTimeSpan.setTillDate(endDate);
          
          hasSelectedDate = true;
        }        
      });     
    }

    //Initialize the form
    if (hasSelectedDate) {
      currObjInstance.updateTimeSpanForm();
      currObjInstance.openForm();
    } else {
      //Close the form if no values has been found.
      currObjInstance.closeForm();
    }
  },
  
  /**
   * This method is toggles between the open and closed state of the timefacet form
   */
  toggleForm : function() {
    var currObjInstance = this;
    
    if (!currObjInstance.opened) {
      currObjInstance.openForm();
    } else {
      //Prevent from closing if a timespan has been selected
      if (!currObjInstance.added) {
        currObjInstance.closeForm();
      }
    }
  },
  
  /**
   * This method opens the timefacet form
   */
  openForm : function() {
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form"); 
    var timeFacetDiv = $(".time-facet");
    
    currObjInstance.opened = true;
    timespanFormDiv.fadeIn('fast');
    timeFacetDiv.addClass('active');
  },
  
  /**
   * This method closes the timefacet form
   */
  closeForm : function() {
    var currObjInstance = this;
    var timespanFormDiv = $("#timespan-form"); 
    var timeFacetDiv = $(".time-facet");
    
    currObjInstance.opened = false;
    timespanFormDiv.fadeOut('fast');
    timeFacetDiv.removeClass('active');
  },
  
  
  
  /**
   * Checks the values of the form and adds a new timespan.
   */
  assignTimeSpan : function(checkYears) {
    var currObjInstance = this;
    
    de.ddb.next.search.hideError();
    
    //Retrieve the values from the timespan form
    var fromDayValue = $("#fromDay").val() !== "" ? $("#fromDay").val() : null;
    var fromMonthValue = $("#fromMonth").val() !== "" ? $("#fromMonth").val() : null;
    var fromYearValue = $("#fromYear").val() !== "" ? $("#fromYear").val() : null;

    var tillDayValue = $("#tillDay").val() !== "" ? $("#tillDay").val() : null;
    var tillMonthValue = $("#tillMonth").val() !== "" ? $("#tillMonth").val() : null;
    var tillYearValue = $("#tillYear").val() !== "" ? $("#tillYear").val() : null;
    
    if (checkYears) {
      if (fromYearValue === null && tillYearValue === null) {
        de.ddb.next.search.showError("Bitte geben Sie in eines der Zeit-Eingabefelder 'Von' oder 'Bis' eine Jahreszahl ein.");
        return;
      }
    }
    
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
//    console.log("reset");
    var currObjInstance = this;
    
    //Hide error if available
    de.ddb.next.search.hideError();
    
    //Set an empty TimeSpan
    var newTimeSpan = new de.ddb.next.search.TimeSpan();
    currObjInstance.selectedTimeSpan = newTimeSpan;
    
    //reset the GUI
    currObjInstance.disableFromDayAndMonth(true);
    currObjInstance.disableTillDayAndMonth(true);
    currObjInstance.updateTimeSpanForm();    
    
    //asign the timeSpan to reset also the window url etc!
    currObjInstance.assignTimeSpan(false);
    
    currObjInstance.added = false;
  },  
  
  /**
   * Updates the form fields
   */
  updateTimeSpanForm: function() {
    var currObjInstance = this;
    
    $("#fromDay").val(currObjInstance.selectedTimeSpan.fromDay);
    $("#fromMonth").val(currObjInstance.selectedTimeSpan.fromMonth);
    $("#fromYear").val(currObjInstance.selectedTimeSpan.fromYear);

    $("#tillDay").val(currObjInstance.selectedTimeSpan.tillDay);
    $("#tillMonth").val(currObjInstance.selectedTimeSpan.tillMonth);
    $("#tillYear").val(currObjInstance.selectedTimeSpan.tillYear);
  },
  
  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateWindowUrl: function() {
//    console.log("updateWindowUrl: ")
    var currObjInstance = this;
    var paramsArray = null;
    var selectedFacetValues = [];
    
    // Update Url (We want to keep the already selected facet values, but throw away the offset etc.)
    var facetValuesFromUrl = de.ddb.next.search.getFacetValuesFromUrl();

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
//      console.log(fromDate);
      var days = currObjInstance.timeFacetHelper.convertDateObjectToFacetDays(fromDate);

      selectedFacetValues.push('begin_time=' + days);
    }
    
    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      var tillDate = currObjInstance.selectedTimeSpan.getTillDateObject();
//      console.log(tillDate);
      var days = currObjInstance.timeFacetHelper.convertDateObjectToFacetDays(tillDate);

      selectedFacetValues.push('end_time=' + days);
    }
    
    //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
    paramsArray = new Array(new Array('facetValues[]', selectedFacetValues));
//    console.log("paramsArray: " + paramsArray);
    
    //Perform the search with offset 0
    paramsArray.push(new Array('offset', 0));
    
    var newUrl = $.addParamToCurrentUrl(paramsArray);
//    console.log("new url: " + newUrl);
    de.ddb.next.search.fetchResultsList(newUrl, function() {});
  }
});// End extend TimeFacet prototype
