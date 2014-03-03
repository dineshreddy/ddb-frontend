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

/**
 * Facets Manager
 * 
 * The main intend of this object is to - retrieving facet data via ajax -
 * handling all events coming from the DOM tree (add/remove facets, show next
 * page etc.) - updating the facets values in the browser url. This is
 * important for navigating with back/next button of the browser - triggering
 * the flyoutWidget to render the facets
 * 
 * Do all rendering in the flyoutWidget!
 * 
 * Do not make synchronous AJAX calls in this class. Otherwise the GUI might freeze!
 */
de.ddb.next.search.FacetsManager = function() {
  this.init();
};

/**
 * Extend the prototyp of the FacetsManager with jQuery
 */
$.extend(de.ddb.next.search.FacetsManager.prototype, {
  timeFacet: null,
  connectedflyoutWidget : null,
  currentOffset : 0,
  currentRows : -1, // all facets
  currentFacetField : null,
  currentFacetValuesSelected : [],
  currentFacetValuesNotSelected : [],
  allFacets : null,
  currentPage : 1,
  searchFacetValuesTimeout : 0,
  errorCaught : false,
  keyCode : {
    ALT : 18,
    BACKSPACE : 8,
    CAPS_LOCK : 20,
    COMMA : 188,
    CONTROL : 17,
    DELETE : 46,
    DOWN : 40,
    END : 35,
    ENTER : 13,
    ESCAPE : 27,
    HOME : 36,
    INSERT : 45,
    LEFT : 37,
    NUMPAD_ADD : 107,
    NUMPAD_DECIMAL : 110,
    NUMPAD_DIVIDE : 111,
    NUMPAD_ENTER : 108,
    NUMPAD_MULTIPLY : 106,
    NUMPAD_SUBTRACT : 109,
    PAGE_DOWN : 34,
    PAGE_UP : 33,
    PERIOD : 190,
    RIGHT : 39,
    SHIFT : 16,
    SPACE : 32,
    TAB : 9,
    UP : 38
  },

  /**
   * Initialize the new instance.
   */
  init : function() {
    var currObjInstance = this;
    currObjInstance.timeFacet = new de.ddb.next.search.TimeFacet(currObjInstance);
  },

  /**
   * Makes an AJAX request to fetch all facet definitions from the backend. 
   */
  fetchFacetsDefinition : function(flyoutWidget) {
    var currObjInstance = this;
    
    var url = jsContextPath + '/search/facets/';
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        currObjInstance.allFacets = jQuery.parseJSON(request.responseText);        
        
        // invoke the callback method to continue initializing the facets
        currObjInstance.initializeSelectedFacetOnLoad(flyoutWidget);
      }
    });
  },  

  /**
   * Makes an AJAX request to fetch the role values for the currently selected facet field and value
   * 
   * @param facetValueContainer: The DOM element
   * @param facetValue: The selected (main) facet value
   * @param facetField: The selected facet field. E.g: "affiliate_fct"
   * @param role: A specific role. E.g: "_1_affiliate_fct_involved" 
   */
  fetchRoleFacetValues : function(facetValueContainer, facetValue, facetField) {
    var currObjInstance = this;
    
    var oldParams = de.ddb.next.search.getUrlVars();
    var fctValues = '';
    var isThumbnailFIltered = '';
    var queryParam = '&query=' + facetValue;

    //Looking for existing facetvalues[] in the window url parameters
    if (oldParams['facetValues%5B%5D']) {
      $.each(oldParams['facetValues%5B%5D'], function(key, value) {
        fctValues = (value.indexOf(currObjInstance.currentFacetField) >= 0) ? fctValues : fctValues + '&facetValues%5B%5D=' + value;
      });
    }
    if (oldParams['isThumbnailFiltered'] && oldParams['isThumbnailFiltered'] == 'true') {
      isThumbnailFIltered = '&isThumbnailFiltered=true';
    }    
        
    var url = jsContextPath + '/rolefacets' + '?name=' + facetField + '&facetValues%5B%5D=' + facetField + "%3D" + facetValue + '&searchQuery='
    + oldParams['query'] + queryParam + fctValues + isThumbnailFIltered
    + '&offset=' + this.currentOffset + '&rows=' + this.currentRows
    
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(request.responseText);
        if (parsedResponse.values.length > 0) {
          currObjInstance.connectedflyoutWidget.renderRoleValues(facetValueContainer, facetValue, facetField, parsedResponse);
        }
      }
    });
  },

  /**
   * Makes an AJAX request to fetch the facet values for the currently selected facet field
   */
  fetchFacetValues : function(flyoutWidget, query) {
    if (flyoutWidget != null) {
      this.connectedflyoutWidget = flyoutWidget;
    }
    var oldParams = de.ddb.next.search.getUrlVars();
    var currObjInstance = this;
    var fctValues = '';
    var isThumbnailFIltered = '';
    var queryParam = '';
    
    //Looking for existing facetvalues[] in the window url parameters
    if (oldParams['facetValues%5B%5D']) {
      $.each(oldParams['facetValues%5B%5D'], function(key, value) {
        fctValues = (value.indexOf(currObjInstance.currentFacetField) >= 0) ? fctValues
            : fctValues + '&facetValues%5B%5D=' + value;
      });
    }
    if (oldParams['isThumbnailFiltered'] && oldParams['isThumbnailFiltered'] == 'true') {
      isThumbnailFIltered = '&isThumbnailFiltered=true';
    }
    if (query) {
      query = encodeURIComponent(query);
      queryParam = '&query=' + query;
    }
    
    this.connectedflyoutWidget.renderFacetLoader();
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : jsContextPath + '/facets' + '?name=' + currObjInstance.currentFacetField + '&searchQuery='
          + oldParams['query'] + queryParam + fctValues + isThumbnailFIltered
          + '&offset=' + this.currentOffset + '&rows=' + this.currentRows,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(data.responseText);
        // Initialization of currentFacetValuesSelected /
        // currentFacetValuesNotSelected
        currObjInstance.initializeFacetValuesStructures(parsedResponse.values);
        currObjInstance.connectedflyoutWidget.initializeFacetValues(parsedResponse.type,
            currObjInstance.currentFacetValuesNotSelected);
      }
    });
  },
  
  /**
   * Initialize the structures for the pagination logic inside facets flyoutWidget
   */ 
  initPagination : function() {
    this.currentPage = 1;
    this.currentOffset = 0;
    var currObjInstance = this;
    if (Math.round((this.currentFacetValuesNotSelected.length) / 10) > 1) {
      this.connectedflyoutWidget.paginationLiPrev.click(function(e) {
        e.preventDefault();
        currObjInstance.goPrevPage();
        return false;
      });
      this.connectedflyoutWidget.paginationLiNext.click(function(e) {
        e.preventDefault();
        currObjInstance.goNextPage();
        return false;
      });
      this.connectedflyoutWidget.paginationLiNext.removeClass('off');
    }
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);
  },
  
  /**
   * Initialize the FacetValues structures
   */
  initializeFacetValuesStructures : function(responseFacetValues) {
    // LeftBody will not exists on the first opening of the flyout
    if (this.connectedflyoutWidget.facetLeftContainer) {
      var currObjInstance = this;
      var selectedList = this.connectedflyoutWidget.facetLeftContainer
          .find('.selected-items li');

      this.currentFacetValuesSelected = new Array();
      this.currentFacetValuesNotSelected = responseFacetValues;

      if (selectedList.length > 0) {
        selectedList.each(function() {
          var tmpFacetValue = $(this).attr('data-fctvalue');
          currObjInstance.currentFacetValuesSelected.push(tmpFacetValue);
          currObjInstance.currentFacetValuesNotSelected = jQuery.grep(
              currObjInstance.currentFacetValuesNotSelected, function(element) {
                return element.value !== tmpFacetValue;
              });
        });
      }
    }
  },
  
  /**
   * Shows the facet values in the Flyout window for the next page. 
   */
  goNextPage : function() {
    this.currentOffset += 10;
    this.currentPage += 1;
    this.connectedflyoutWidget.renderFacetValues(this.currentFacetField,
        this.currentFacetValuesNotSelected.slice(this.currentOffset));
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);

    if (this.currentPage + 1 > Math
        .ceil((this.currentFacetValuesNotSelected.length) / 10)) {
      this.connectedflyoutWidget.paginationLiNext.addClass('off');
    }
    this.connectedflyoutWidget.paginationLiPrev.removeClass('off');
  },
  
  /**
   * Shows the facet values in the Flyout window for the previous page. 
   */
  goPrevPage : function() {
    this.currentOffset -= 10;
    this.currentPage -= 1;
    this.connectedflyoutWidget.renderFacetValues(this.currentFacetField,
        this.currentFacetValuesNotSelected.slice(this.currentOffset));
    this.connectedflyoutWidget.setFacetValuesPage(this.currentPage);

    if (this.currentPage - 1 < 1) {
      this.connectedflyoutWidget.paginationLiPrev.addClass('off');
    }
    this.connectedflyoutWidget.paginationLiNext.removeClass('off');
  },

  /**
   * Handles the selection of a facet value in the Flyout window.
   * Update the window url and Triggers a new search request!
   */
  selectFacetValue : function(facetValue, localizedValue) {
    var currObjInstance = this;
    
    // update selection lists
    this.currentFacetValuesSelected.push(facetValue);
    this.currentFacetValuesNotSelected = jQuery.grep(this.currentFacetValuesNotSelected,
        function(element) {
          return element.value !== facetValue;
        });

    // render the selected facet
    var facetValueContainer = this.connectedflyoutWidget.renderSelectedFacetValue(facetValue, localizedValue);
    
    // if the facet field contains the string "_role" -> search for role values    
    if (currObjInstance.currentFacetField.indexOf("_role") >= 0) {
      currObjInstance.fetchRoleFacetValues(facetValueContainer, facetValue, currObjInstance.currentFacetField);
    }

    // add event listener for removing facet
    facetValueContainer.find('.facet-remove').click(function(event) {
      event.preventDefault();
      currObjInstance.unselectFacetValue(facetValueContainer);
      return false;
    });

    // add event listener for add more facet filters
    if (this.currentFacetValuesSelected.length === 1) {
      this.connectedflyoutWidget.renderAddMoreFiltersButton(currObjInstance.currentFacetField);
      this.connectedflyoutWidget.addMoreFilters.click(function(event) {
        currObjInstance.connectedflyoutWidget.build($(this));
      });
    }
    this.connectedflyoutWidget.close();
    
    //Update the search params
    var paramsArray = de.ddb.next.search.addFacetValueToParams(this.currentFacetField, facetValue);
    
    //Perform a search with the new facet value
    de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray), function() {
      currObjInstance.unselectFacetValue(facetValueContainer, true);
    });

    $('.clear-filters').removeClass('off');
  },

  /**
   * Handles the unselection of a facet value.
   * Update the window url and Triggers a new search request!
   */
  unselectFacetValue : function(element, unselectWithoutFetch) {
    var facetFieldFilter = element.parents('.facets-item');
    var facetValue = element.attr('data-fctvalue');    

    if (this.connectedflyoutWidget.opened) {
      this.connectedflyoutWidget.close();
      this.currentFacetValuesSelected = jQuery.grep(this.currentFacetValuesSelected,
          function(el) {
            return el !== element.attr('data-fctvalue');
          });
    }
    // if in the list there is only one element means that is the case
    // of the
    // last element that we are going to remove
    if (facetFieldFilter.find('.selected-items li[data-fctvalue]').length === 1) {
      var facetFieldFilter = element.parents('.facets-item');
      this.connectedflyoutWidget.removeAddMoreFiltersButton(facetFieldFilter,
          facetFieldFilter.find('.add-more-filters'));
    }

    // Remove facet and all role based facets belonging to this facet
    // from the URL
    var facetsToRemove = new Array();
    facetsToRemove.push(new Array('facetValues[]', facetFieldFilter.find('.h3').attr(
        'data-fctname')
        + '=' + facetValue));

    var roleFacets = element.find('span.role-facet-value');
    $.each((roleFacets), function() {
      facetsToRemove.push(new Array('facetValues[]', $(this).attr("facetfield") + '=' + $(this).attr("roleValue")));
    });

    
    var newUrl = $.removeParamFromUrl(facetsToRemove);

    if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
      de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
    }
    if (!unselectWithoutFetch) {
      de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
          .substr(newUrl.indexOf("?") + 1)));
    }
    element.remove();

    if ($('.facets-list').find('li[data-fctvalue]').length === 0) {
      $('.clear-filters').addClass('off');
    }
  },

  /**
   * Handles the selection of a role facet.
   * Update the window url and Triggers a new search request!
   */
  selectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var paramsArray = de.ddb.next.search.addFacetValueToParams(facetField, facetValue);
    de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray));
  },

  /**
   * Handles the unselection of a role facet.
   * Update the window url and Triggers a new search request!
   */
  unselectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var newUrl = $.removeParamFromUrl(new Array(new Array('facetValues[]', facetField + '=' + facetValue)));
    if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
      de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
    }

    de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
        .substr(newUrl.indexOf("?") + 1)));
  },

  /**
   * Search handler for facet values.
   * Triggers a facet search and updates the values shown in the flyout window. 
   */
  initializeFacetValuesDynamicSearch : function(inputSearchElement) {
    var currObjInstance = this;
    inputSearchElement.keyup(function(e) {
      var code = (e.keyCode ? e.keyCode : e.which);
      var inputValue = this.value;
      if (code !== currObjInstance.keyCode.SHIFT
          && code !== currObjInstance.keyCode.CONTROL
          && code !== currObjInstance.keyCode.ALT
          && code !== currObjInstance.keyCode.LEFT && code !== currObjInstance.keyCode.UP
          && code !== currObjInstance.keyCode.RIGHT
          && code !== currObjInstance.keyCode.DOWN
          && code !== currObjInstance.keyCode.ENTER
          && code !== currObjInstance.keyCode.TAB) {
        var d = new Date();
        currObjInstance.searchFacetValuesTimeout = d.getTime();
        setTimeout(function() {
          var currentD = new Date();
          if (currObjInstance.searchFacetValuesTimeout + 400 < currentD.getTime()
              && currObjInstance.connectedflyoutWidget.opened) {
            currObjInstance.connectedflyoutWidget.parentMainElement.find(
                '.flyout-right-container').remove();
            currObjInstance.connectedflyoutWidget.buildStructure();
            currObjInstance.fetchFacetValues(null, inputValue);
            currObjInstance.currentPage = 1;
            currObjInstance.currentOffset = 0;
          } else {
            return;
          }
        }, 500);
      }
    });
  },

  /**
   * Initialize the Flyout widget when the page is loaded in a asynchronic way.
   * First fetch the facet definition from the backend.
   * A callback is triggering the further initialization which is done in initializeSelectedFacetOnLoad()
   */
  initializeOnLoad : function(connectedflyoutWidget) {
    // this methods initialize all selected facets and role facets
    var currObjInstance = this;

    // fetch all role facets asynchronously. The success method will
    // select all facet values
    currObjInstance.fetchFacetsDefinition(connectedflyoutWidget);

    $('.clear-filters').removeClass('off');
  },

  /**
   * Initialize the Facet Manager with the facets provided in the window url.
   * 
   * This method must be called by fetchFacetsDefinition()!
   */
  initializeSelectedFacetOnLoad : function(connectedflyoutWidget) {
    var currObjInstance = this;
    this.connectedflyoutWidget = connectedflyoutWidget;
    var paramsFacetValues = de.ddb.next.search.getFacetValuesFromUrl();

    if (paramsFacetValues) {
      var selectedFacets = {};
      $.each(paramsFacetValues, function(key, value) {
        var decodedElement = decodeURIComponent(value.replace(/\+/g, '%20')).split('=');
        var fctField = decodedElement[0];
        var fctValue = decodedElement[1];
        if (!selectedFacets[fctField]) {
          selectedFacets[fctField] = [];
        }
        selectedFacets[fctField].push(fctValue);
      });

      // handle selected facets
      $.each(selectedFacets,
              function(fctField, fctValues) {
                  currObjInstance.connectedflyoutWidget.mainElement = $('.facets-list')
                      .find('a[data-fctname="' + fctField + '"]');
                  currObjInstance.connectedflyoutWidget.parentMainElement = currObjInstance.connectedflyoutWidget.mainElement
                      .parent();
                  currObjInstance.currentFacetField = currObjInstance.connectedflyoutWidget.mainElement
                      .attr('data-fctname');
                  currObjInstance.connectedflyoutWidget.buildLeftContainer();
                  currObjInstance.connectedflyoutWidget.parentMainElement.find(
                      '.input-search-fct-container').hide();
                  
                  $.each(fctValues, function(fctValue) {
                    //Check if the value is a role. They need special handling!
                    var facetValue = this;
                    
                    //Skip if the facetValue is a role
                    if (de.ddb.next.search.isRole(facetValue)) {
                      return;
                    }
                    
                    var localizedValue = de.ddb.next.search.getLocalizedFacetValue(fctField, this);
                    if ( typeof(localizedValue) === 'function') {
                      localizedValue = localizedValue();
                    }
                    
                    var selectedFacetValue = currObjInstance.connectedflyoutWidget.renderSelectedFacetValue(this, localizedValue);
                    var roleFacets = currObjInstance.getRolesForFacet(currObjInstance.currentFacetField);
                    
                    
                    if (fctField.indexOf("_role") >= 0) {
                      currObjInstance.fetchRoleFacetValues(selectedFacetValue, facetValue, fctField);                      
                    }

                    selectedFacetValue.find('.facet-remove').click(function(event) {
                      event.preventDefault();
                      currObjInstance.unselectFacetValue(selectedFacetValue);
                      return false;
                    });
                  });

                  currObjInstance.connectedflyoutWidget
                      .renderAddMoreFiltersButton(fctField);
                  currObjInstance.connectedflyoutWidget.addMoreFilters.click(function(
                      event) {
                    currObjInstance.connectedflyoutWidget.build($(this));
                  });
              });
      $('.clear-filters').removeClass('off');
    }
    
    //init TimeFacet
    currObjInstance.timeFacet.initOnLoad();
  },
  
  /**
   * Search the allFacets definition for the given facet name.
   * Returns <code>true<code> if the given facet has been found.
   */
  isMainFacet : function(fctField) {
    var currObjInstance = this;
    var isMainFacet = false;

    $.each(currObjInstance.allFacets, function() {
      if (fctField == this.name) {        
        isMainFacet = true;
      }
    });

    return isMainFacet;
  },  

  /**
   * Searches the roles for a facet in the facetDefinition
   */
  getRolesForFacet : function(fctField) {
    var currObjInstance = this;
    var roleFacets = [];

    $.each(currObjInstance.allFacets, function() {
      if (fctField == this.name) {        
        //TODO get only roleFacets that are in the roleFacets list
        roleFacets = Object.keys(this.roles);
      }
    });

    return roleFacets;
  },
  
});