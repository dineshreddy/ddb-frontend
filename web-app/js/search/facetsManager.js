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
 * The main intend of this class is to - retrieving facet data via ajax -
 * handlig all events coming from the DOM tree (add/remove facets, show next
 * page etc.) - updating the facets values in the browser url. This is
 * important for navigating with back/next butoon of the browser - triggering
 * the flyoutWidget to render the facets
 * 
 * Do all rendering in the flyoutWidget!
 * 
 * Do not make synchronous AJAX calls in this class. Otherwise the GUI might
 * freeze!
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
  currentFacetValuesSelected : new Array(),
  currentFacetValuesNotSelected : new Array(),
  roleFacets : new Array,
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
    timeFacet = new de.ddb.next.search.TimeFacet(this);
  },

  fetchRoleFacets : function(flyoutWidget) {
    var currObjInstance = this;
    var url = jsContextPath + '/rolefacets';
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url,
      complete : function(data) {
        var parsedResponse = jQuery.parseJSON(request.responseText);

        if (parsedResponse.length > 0) {
          currObjInstance.roleFacets = new Array();
          $.each((parsedResponse), function() {
            currObjInstance.roleFacets.push(this);
          });
        }
        // invoke the callback method to continue initializing the
        // facets
        currObjInstance.initializeSelectedFacetOnLoad(flyoutWidget);
      }
    });
  },

  fetchRoleFacetValues : function(facetValueContainer, facetValue, roleFacet) {
    var currObjInstance = this;
    var url = jsContextPath + '/facets' + '?name=' + roleFacet + '&query=' + facetValue;
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
    var oldParams = this.getUrlVars();
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
      url : jsContextPath + '/facets' + '?name=' + this.currentFacetField + '&searchQuery='
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

    // update selection lists
    this.currentFacetValuesSelected.push(facetValue);
    this.currentFacetValuesNotSelected = jQuery.grep(this.currentFacetValuesNotSelected,
        function(element) {
          return element.value !== facetValue;
        });

    // render the selected facet
    var facetValueContainer = this.connectedflyoutWidget.renderSelectedFacetValue(
        facetValue, localizedValue);

    // search for role based facets for the current field
    var roleFacets = currObjInstance.getRoleFacets(this.currentFacetField);

    if (roleFacets.length > 0) {
      $.each(currObjInstance.roleFacets, function() {
        currObjInstance.fetchRoleFacetValues(facetValueContainer, facetValue, this.name);
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
      this.connectedflyoutWidget.renderAddMoreFiltersButton(this.currentFacetField);
      this.connectedflyoutWidget.addMoreFilters.click(function(event) {
        currObjInstance.connectedflyoutWidget.build($(this));
      });
    }
    this.connectedflyoutWidget.close();

    // Update Url (We want to add the facet value selected, but at the same time we want to keep all the old selected values)
    //The facet values are stored in a two dimensional Array: ["facetValues[]",['type_fct=Dmediatype_003','language_fct=lat', 'time_end_fct=2014',]]
    var paramsFacetValues = this.getUrlVar('facetValues%5B%5D');
    if (paramsFacetValues == null) {
      paramsFacetValues = this.getUrlVar('facetValues[]');
    }

    if (paramsFacetValues) {
      $.each(paramsFacetValues, function(key, value) {
        paramsFacetValues[key] = decodeURIComponent(value.replace(/\+/g, '%20'));
      });
      paramsFacetValues.push(this.currentFacetField + '=' + facetValue);
      var paramsArray = new Array(new Array('facetValues[]', paramsFacetValues));
    } else {
      var paramsArray = new Array(new Array('facetValues[]', this.currentFacetField + '='
          + facetValue));
    }

    // perform search
    paramsArray.push(new Array('offset', 0));
    de.ddb.next.search.fetchResultsList(addParamToCurrentUrl(paramsArray), function() {
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

    var newUrl = removeParamFromUrl(facetsToRemove);

    if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
      de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
    }
    if (!unselectWithoutFetch) {
      de.ddb.next.search.fetchResultsList(addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
          .substr(newUrl.indexOf("?") + 1)));
    }
    element.remove();

    if ($('.facets-list').find('li[data-fctvalue]').length === 0) {
      $('.clear-filters').addClass('off');
    }
  },

  selectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    // We want to add the facet value selected, but at the same time
    // we want
    // to keep all the old selected values
    var paramsFacetValues = this.getUrlVar('facetValues%5B%5D');
    if (paramsFacetValues == null) {
      paramsFacetValues = this.getUrlVar('facetValues[]');
    }

    if (paramsFacetValues) {
      $.each(paramsFacetValues, function(key, value) {
        paramsFacetValues[key] = decodeURIComponent(value.replace(/\+/g, '%20'));
      });
      paramsFacetValues.push(facetField + '=' + facetValue);
      var paramsArray = new Array(new Array('facetValues[]', paramsFacetValues));
    } else {
      var paramsArray = new Array(new Array('facetValues[]', facetField + '='
          + facetValue));
    }

    paramsArray.push(new Array('offset', 0));
    de.ddb.next.search.fetchResultsList(addParamToCurrentUrl(paramsArray));
  },

  unselectRoleFacetValue : function(facetField, facetValue) {
    var currObjInstance = this;
    var newUrl = removeParamFromUrl(new Array(new Array('facetValues[]', facetField + '='
        + facetValue)));
    if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
      de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
    }

    de.ddb.next.search.fetchResultsList(addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
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

    // fetch all role facets asynchronusly. The success method will
    // select all facet values
    currObjInstance.fetchRoleFacets(connectedflyoutWidget);

    $('.clear-filters').removeClass('off');
  },

  initializeSelectedFacetOnLoad : function(connectedflyoutWidget) {
    var currObjInstance = this;

    this.connectedflyoutWidget = connectedflyoutWidget;
    var paramsFacetValues = this.getUrlVar('facetValues%5B%5D');

    if (paramsFacetValues == null) {
      paramsFacetValues = this.getUrlVar('facetValues[]');
    }

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
    var currObjInstance = this;
    var roleFacets = new Array();

    $.each(currObjInstance.roleFacets, function() {
      if (fctField == this.parent) {
        roleFacets.push(this);
      }
    });

    return roleFacets;
  },

  getUrlVars : function() {
    var vars = {}, hash;
    var hashes = (historySupport) ? window.location.href.slice(
        window.location.href.indexOf('?') + 1).split('&') : globalUrl.split('&');
    for ( var i = 0; i < hashes.length; i++) {
      hash = hashes[i].split('=');
      if (!Object.prototype.hasOwnProperty.call(vars, hash[0])) {
        vars[hash[0]] = new Array();
      }
      vars[hash[0]].push(hash[1]);
    }
    return vars;
  },
  getUrlVar : function(name) {
    return this.getUrlVars()[name];
  }
});