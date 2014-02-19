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

$.extend(de.ddb.next.search.FacetsManager.prototype, {
  timeFacet: null,
  connectedflyoutWidget : null,
  currentOffset : 0,
  currentRows : -1, // all facets
  currentFacetField : null,
  currentFacetValuesSelected : [],
  currentFacetValuesNotSelected : [],
  roleFacets : ["_1_affiliate_fct_subject", "_1_affiliate_fct_involved" ],
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

  init : function() {
    var currObjInstance = this;
    currObjInstance.timeFacet = new de.ddb.next.search.TimeFacet(currObjInstance);
  },

  fetchFacetsDefinition : function(flyoutWidget) {
    var currObjInstance = this;
    
    console.log("fetchFacetsDefinition");
    var url = jsContextPath + '/search/facets/';
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        currObjInstance.allFacets = jQuery.parseJSON(request.responseText);        
        console.log("facetsDefinition: " + currObjInstance.allFacets);
        
        // invoke the callback method to continue initializing the facets
        currObjInstance.initializeSelectedFacetOnLoad(flyoutWidget);
      }
    });
  },  

  fetchRoleFacetValues : function(facetValueContainer, facetValue, roleFacet) {
    var currObjInstance = this;
    var url = jsContextPath + '/facets' + '?name=' + roleFacet + '&query=' + facetValue;    
    console.log("fetchRoleFacetValues url: " + url);
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(request.responseText);
        if (parsedResponse.values.length > 0) {
          currObjInstance.connectedflyoutWidget.renderRoleFacetValue(facetValueContainer,
              facetValue, roleFacet, parsedResponse);
        }
      }
    });
  },

  fetchFacetValues : function(flyoutWidget, query) {
    if (flyoutWidget != null) {
      this.connectedflyoutWidget = flyoutWidget;
    }
    var oldParams = de.ddb.next.search.getUrlVars();
    var currObjInstance = this;
    var fctValues = '';
    var isThumbnailFIltered = '';
    var queryParam = '';
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
  // Initialize the structures for the pagination logic inside facets
  // flyoutWidget
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

  selectFacetValue : function(facetValue, localizedValue) {
    var currObjInstance = this;
    
    console.log("selectFacetValue");
    // update selection lists
    this.currentFacetValuesSelected.push(facetValue);
    this.currentFacetValuesNotSelected = jQuery.grep(this.currentFacetValuesNotSelected,
        function(element) {
          return element.value !== facetValue;
        });

    // render the selected facet
    var facetValueContainer = this.connectedflyoutWidget.renderSelectedFacetValue(facetValue, localizedValue);

    // search for role based facets for the current field
    var currentFieldRoleFacets = currObjInstance.getRoleFacets(currObjInstance.currentFacetField);
    console.log("selectFacetValue currentFieldRoleFacets: " + currentFieldRoleFacets);
    
    if (currentFieldRoleFacets.length > 0) {
      $.each(currObjInstance.roleFacets, function() {
        currObjInstance.fetchRoleFacetValues(facetValueContainer, facetValue, currObjInstance.currentFacetField + "_role");
      });
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

    var roleFacets = facetFieldFilter.find('span.role-facet-value');
    $.each((roleFacets), function() {
      facetsToRemove.push(new Array('facetValues[]', $(this).attr("facetfield") + '='
          + facetValue));
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

  selectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    
    var paramsArray = de.ddb.next.search.addFacetValueToParams(facetField, facetValue);
    de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray));
  },

  unselectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var newUrl = $.removeParamFromUrl(new Array(new Array('facetValues[]', facetField + '='
        + facetValue)));
    if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
      de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
    }

    de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
        .substr(newUrl.indexOf("?") + 1)));
  },

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

  initializeOnLoad : function(connectedflyoutWidget) {
    // this methods initialize all selected facets and role facets
    var currObjInstance = this;

    // fetch all role facets asynchronously. The success method will
    // select all facet values
    currObjInstance.fetchFacetsDefinition(connectedflyoutWidget);

    $('.clear-filters').removeClass('off');
  },

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
          selectedFacets[fctField] = new Array();
        }
        selectedFacets[fctField].push(fctValue);
      });

      // handle selected facets
      $.each(selectedFacets,
              function(fctField, fctValues) {
                if (!currObjInstance.isRoleFacet(fctField)) {
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
                    var facetValue = this;
                    var selectedFacetValue = currObjInstance.connectedflyoutWidget
                        .renderSelectedFacetValue(this, de.ddb.next.search.getLocalizedFacetValue(fctField,
                            this));
                    var roleFacets = currObjInstance
                        .getRoleFacets(currObjInstance.currentFacetField);

                    if (roleFacets.length > 0) {
                      $.each(currObjInstance.roleFacets, function() {
                        currObjInstance.fetchRoleFacetValues(selectedFacetValue,
                            facetValue, this.name);
                      });
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
                }
              });
      $('.clear-filters').removeClass('off');
    }
    
    //init TimeFacet
    currObjInstance.timeFacet.initOnLoad();
  },

  isRoleFacet : function(fctField) {
    var currObjInstance = this;
    var isRoleFacet = false;

    $.each(currObjInstance.roleFacets, function() {
      if (fctField == this.name) {
        isRoleFacet = true;
      }
    });

    return isRoleFacet;
  },

  getRoleFacets : function(fctField) {
    console.log("getRoleFacets for field: " + fctField)
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