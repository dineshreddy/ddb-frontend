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
};


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
      var days_in_month = [31,28,31,30,31,30,31,31,30,31,30,31];

      if(currObjInstance.tillYear %4 === 0 && currObjInstance.tillYear !== 1900)
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
    var date = null;

    //If no year is set -> return
    if (!currObjInstance.hasFromDate()) {
      return;
    }

    currObjInstance.completeFromDate();

    if(currObjInstance.fromYear >= 0) {
     date = "AD-" + currObjInstance.fromYear + "-" + currObjInstance.fromMonth + "-" + currObjInstance.fromDay;
    }
    else {
      date = "BC" + currObjInstance.fromYear + "-" + currObjInstance.fromMonth + "-" + currObjInstance.fromDay;
    }
    return date;

  },

  /**
   * Formats the till date in this form: yyyy-MM-dd
   */
  formatTillDate: function(){
    var currObjInstance = this;
    var date = null;

    //If no year is set -> return
    if (!currObjInstance.hasTillDate()) {
      return;
    }

    currObjInstance.completeTillDate();

    if(currObjInstance.tillYear >= 0) {
      date = "AD-" + currObjInstance.tillYear + "-" + currObjInstance.tillMonth + "-" + currObjInstance.tillDay;
     }
     else {
       date = "BC" + currObjInstance.tillYear + "-" + currObjInstance.tillMonth + "-" + currObjInstance.tillDay;
     }
    return date;

  },

  /**
   * Setter for the from Date
   */
  setFromDate: function(date){
    var currObjInstance = this;

    currObjInstance.fromDay =  date.getDate();
    currObjInstance.fromMonth = date.getMonth() + 1;
    currObjInstance.fromYear = date.getFullYear();
  },

  /**
   * Setter for the from Date
   */
  setTillDate: function(date){
    var currObjInstance = this;

    currObjInstance.tillDay = date.getDate();
    currObjInstance.tillMonth = date.getMonth() + 1;
    currObjInstance.tillYear = date.getFullYear();
  },

  /**
   * Clear parameter from From Date, because it doesn't find in the URL
   */
  clearFromDate: function(){
    this.fromDay = null;
    this.fromMonth = null;
    this.fromYear = null;
  },

  /**
   * Clear parameter from Till Date, because it doesn't find in the URL
   */
  clearTillDate: function(){
    this.tillDay = null;
    this.tillMonth = null;
    this.tillYear = null;
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
};

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

  /**
   * Initialize the TimeFacet object
   */
  init : function(facetsManager) {
    var currObjInstance = this;

    currObjInstance.facetsManager = facetsManager;
    currObjInstance.selectedTimeSpan = new de.ddb.next.search.TimeSpan();

    //Remove the off class for Non Javascript
    $(".time-facet").removeClass("off");

    //During initialisation hide the timespan form and disable the form elements
    $("#timespan-form").hide();
    currObjInstance.disableFromDayAndMonth(true);
    currObjInstance.disableTillDayAndMonth(true);

    // Click handler for Opening|Closing the time facet
    $(".time-facet a.h3").click(function(event) {
      event.preventDefault();
      currObjInstance.toggleForm();
    });

    // Click handler for adding a new TimeSpan
    $("#add-timespan").click(function(event) {
      event.preventDefault();
      currObjInstance.assignTimeSpan(true);
    });

    // Click handler for reseting the time facet
    $("#reset-timefacet").click(function(event) {
      event.preventDefault();
      currObjInstance.reset();
    });

    $("#fromYear").change(function(){
      if ($("#fromYear").val()) {
        currObjInstance.disableFromDayAndMonth(false);
        $("#add-timespan").removeClass('without-date');
      } else {
        currObjInstance.disableFromDayAndMonth(true);
      }
    });

    $("#tillYear").change(function(){
      if ($("#tillYear").val()) {
        currObjInstance.disableTillDayAndMonth(false);
        $("#add-timespan").removeClass('without-date');
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
  initOnLoad: function(endDate, beginDate) {
    var currObjInstance = this;
        
    //Asynchronously calculate the dates for the form! Continue the initialization in the success part
    currObjInstance.calculateFacetDates();
  },

  convertServerDateToJsDate: function(serverDate) {
    var currObjInstance = this;
    var date = null;

    if(serverDate) {
      var year = null;    
      var dateArray = serverDate.split("-")

      if(dateArray[0].indexOf("BC") != -1){
        year = "-" + dateArray[1];
      }
      else {
        year = dateArray[1];
      }

      //Months starts in Javascript with 0!
      //To work with years between 1-99
      date = new Date();
      date.setFullYear(year,dateArray[2] -1,dateArray[3])
    }

    return date;

  },

  /**
  * This method initialize the TimeFacet widget based on the window url.
  * It search for facetValues[] 'begin_time' and 'end_time'. Contained values will be set into the form.
  */
  initFormOnLoad: function(beginDateStr, endDateStr, exact) {
    var currObjInstance = this;
    var hasSelectedDate = false;
    var updatedFrom = false;
    var updatedTill = false;

    var beginDate = currObjInstance.convertServerDateToJsDate(beginDateStr);
    var endDate = currObjInstance.convertServerDateToJsDate(endDateStr);
    
    $(".time-facet").removeClass("off");
    
    if(exact) {
      $("#limitationExact").prop("checked", true);
    }
    else {
      $("#limitationFuzzy").prop("checked", true);
    }
    
    if (beginDate) {

      currObjInstance.selectedTimeSpan.setFromDate(beginDate);
    } else {
      currObjInstance.selectedTimeSpan.clearFromDate();
    }

    if (endDate) {
      currObjInstance.selectedTimeSpan.setTillDate(endDate);
    } else {
      currObjInstance.selectedTimeSpan.clearTillDate();
    }

    //Initialize the form
    if (beginDate || endDate) {
      currObjInstance.updateTimeSpanForm();
      currObjInstance.openForm();
      $("#add-timespan").removeClass('without-date');
    } else {
      //Close the form if no values has been found.
      currObjInstance.closeForm();
    }
  },

  /**
   * Parses the browser url for time facet values.
   */
   parseWindowsUrl: function() {
     var currObjInstance = this;
     var dividerPattern = /\-?[0-9]+/;
     var beginDays = null;
     var endDays = null;
     var exact = null;
     
     // Search for time facetValues[] in the window url
     var facetValuesFromUrl = de.ddb.next.search.getFacetValuesFromUrl();
     
     if (facetValuesFromUrl) {
       $.each(facetValuesFromUrl, function(key) {

         var decodedValue = decodeURIComponent(facetValuesFromUrl[key]);
         if ((facetValuesFromUrl[key].indexOf("begin_time") === 0)) {
           var split = dividerPattern.exec(decodedValue);

           //Unscharf/Fuzzy
           if(decodedValue.substr(12,1) === '*'){
             endDays = split[0];
             exact = false;
           }else {//Genau/Exactly
             beginDays = split[0];
             exact = true;
           }
         }

         if ((facetValuesFromUrl[key].indexOf("end_time") === 0)) {

           //Unscharf/Fuzzy
           if(decodedValue.indexOf('TO+*') !== -1){
             var split = dividerPattern.exec(decodedValue);
             beginDays = split[0];
             exact = false;
           }else {//Genau/Exactly
             var indexOfTo = decodedValue.indexOf('TO');
             var endSubstring = decodedValue.substr(indexOfTo);
             var split = dividerPattern.exec(endSubstring);
             endDays = split[0];
             exact = true;
           }
         }
       });
     }

     //Return an object containing the parsed information
     return {
       beginDays:beginDays, 
       endDays:endDays,
       exact:exact
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
    //currObjInstance.manageOutsideClicks(currObjInstance);
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
   * Close the time facet form when the user clicks outside of it.
   */
  manageOutsideClicks : function(thisInstance) {
    $(document).mouseup(function(e) {
      var container = $(".time-facet");
      if(!$(e.target).parents(container).is(container) && thisInstance.opened) {
        thisInstance.closeForm();
      }
    });
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

    if (checkYears && fromYearValue === null && tillYearValue === null) {
      de.ddb.next.search.showError("Bitte geben Sie in eines der Zeit-Eingabefelder 'Von' oder 'Bis' eine Jahreszahl ein.");
      return;
    }

    var newTimeSpan = new de.ddb.next.search.TimeSpan(fromDayValue, fromMonthValue, fromYearValue, tillDayValue, tillMonthValue, tillYearValue);
    currObjInstance.selectedTimeSpan = newTimeSpan;
    currObjInstance.added = true;

    currObjInstance.selectedTimeSpan.completeFromDate();
    currObjInstance.selectedTimeSpan.completeTillDate();

    currObjInstance.updateTimeSpanForm();    
    
    currObjInstance.calculateFacetDays();
  },

  /**
   * Resets the input elements of the form.
   * The window URL is reseted by calling assignTimeSpan() which does this implicitly
   */
  reset : function() {
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

    //reset buton Apply
    $("#add-timespan").addClass('without-date');

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

    if ($("#fromYear").val()) {
      currObjInstance.disableFromDayAndMonth(false);
    } else {
      currObjInstance.disableFromDayAndMonth(true);
    }

    if ($("#tillYear").val()) {
      currObjInstance.disableTillDayAndMonth(false);
    } else {
      currObjInstance.disableTillDayAndMonth(true);
    }
  },

  /**
   * Updates the browser URL and performs a new search with the given time facet values.
   */
  updateWindowUrl: function(daysFrom, daysTill) {
    var currObjInstance = this;
    var paramsArray = null;
    var selectedFacetValues = [];

    daysFrom = daysFrom || '*';
    daysTill = daysTill || '*';
    
    // Update Url (We want to keep the already selected facet values, but throw away the offset etc.)
    var facetValuesFromUrl = de.ddb.next.search.getFacetValuesFromUrl();

    if (facetValuesFromUrl) {
      $.each(facetValuesFromUrl, function(key, value) {
        //Only add facetValues that do not start with "begin_time" or "end_time"
        if ((facetValuesFromUrl[key].indexOf("begin_time") === -1) && (facetValuesFromUrl[key].indexOf("end_time") === -1)) {
          selectedFacetValues.push(decodeURIComponent(value.replace(/\+/g, '%20')));
        }
      });
    }

    //Genau
    if($("#limitationExact").is(":checked")) {
      if(daysFrom !== '*') {
        selectedFacetValues.push('begin_time=[' + daysFrom + ' TO ' + daysTill + ']');
      }
      if(daysTill !== '*') {
        selectedFacetValues.push('end_time=[' + daysFrom + ' TO ' + daysTill + ']');
      }

    }
    else{//Unscharf
      if(daysTill !== '*') {
        selectedFacetValues.push('begin_time=[* TO '+ daysTill + ']');
      }
      if(daysFrom !== '*') {
        selectedFacetValues.push('end_time=[' + daysFrom + ' TO *]');
      }
    }

    //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
    paramsArray = [['facetValues[]', selectedFacetValues]];

    //Perform the search with offset 0
    paramsArray.push(['offset', 0]);

    var newUrl = $.addParamToCurrentUrl(paramsArray);

    de.ddb.next.search.fetchResultsList(newUrl, function() {});
  },
  
  /**
   * Converts a Date object to a Day representation for the time facet
   */
  calculateFacetDays: function() {
    var currObjInstance = this;

    var url = jsContextPath + '/facets/calculateTimeFacetDays' + '?';
    if (currObjInstance.selectedTimeSpan.hasFromDate()) {
      url += 'dateFrom='+ currObjInstance.selectedTimeSpan.formatFromDate();
    }

    if (currObjInstance.selectedTimeSpan.hasTillDate()) {
      if (url.indexOf("dateFrom") != -1) {
        url += "&";
      }

      url += 'dateTill=' + currObjInstance.selectedTimeSpan.formatTillDate();
    }
    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);
        currObjInstance.updateWindowUrl(parsedResponse.daysFrom, parsedResponse.daysTill);
      }
    });
  },

  /**
   * Converts a Day representation for the time facet to a Javascript Date object
   * 
   * @param days An object containing the url values to be converted
   */
  calculateFacetDates: function(days) {
    var currObjInstance = this;

    // Search for time facet parameter in the window url
    var urlValues = currObjInstance.parseWindowsUrl();
    
    var url = jsContextPath + '/facets/calculateTimeFacetDates' + '?';
    if (urlValues.beginDays) {
      url += 'beginDays='+ urlValues.beginDays;
    }

    if (urlValues.endDays) {
      if (url.indexOf("beginDays") != -1) {
        url += "&";
      }

      url += 'endDays=' + urlValues.endDays;
    }

    $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);  
        
        //Continue with initializing the form
        currObjInstance.initFormOnLoad(parsedResponse.dateFrom, parsedResponse.dateTill, urlValues.exact);
      }
    }); 
  }

});// End extend TimeFacet prototype
