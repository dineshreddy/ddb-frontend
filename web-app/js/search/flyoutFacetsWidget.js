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
 * Flyout Widget
 * 
 * The main intend of this object is to render all content in the context of
 * facets. It's doing this by DOM manipulation triggered by the FacetManager
 * instance. The Flyout Widget contains 
 * - facetLeftContainer: showing the facet fields and the selected facets 
 * - facetRightContainer: showing the unselected facets
 * 
 * Do not use AJAX calls in this class!
 */
de.ddb.next.search.FlyoutFacetsWidget = function() {
  this.init();
};

$.extend(de.ddb.next.search.FlyoutFacetsWidget.prototype,{
  mainElement : null,
  parentMainElement : null,
  opened : false,
  fctManager : new de.ddb.next.search.FacetsManager(),

  facetLeftContainer : null,
  facetRightContainer : null,
  rightBody : null,
  leftBody : null,
  selectedItems : null,
  paginationLiPrev : null,
  paginationLiNext : null,
  paginationLiSeite : null,
  addMoreFilters : null,
  inputSearch : null,

  // i18n variables

  field_MostRelevant : messages.ddbnext.Most_relevant,
  field_NoAvailableValues : messages.ddbnext.No_Available_Values,
  field_AddMoreFiltersButtonTooltip : messages.ddbnext.Add_More_Filters_ButtonTooltip,
  field_SearchResultsFacetValueNext : messages.ddbnext.SearchResultsFacetValue_Next,
  field_SearchResultsFacetValuePrevious : messages.ddbnext.SearchResultsFacetValue_Previous,
  field_Page : messages.ddbnext.Page,
  field_RemoveSelectedItem : messages.ddbnext.Remove_selected_item,
  field_RemoveButton : messages.ddbnext.Remove_Button,

  init : function() {
    console.log("FlyoutFacetsWidget init()");
    this.cleanNonJsStructures();
    this.fctManager.initializeOnLoad(this);
  },

  build : function(element) {
    if ((element.attr('class') == 'h3' && element.parent().find('.selected-items li').length === 0)
        || element.attr('class') == 'add-more-filters') {
      if ((element.attr('data-fctname') !== this.fctManager.currentFacetField || (element
          .attr('data-fctname') == this.fctManager.currentFacetField && !this.opened))) {
        if (this.opened) {
          this.close();
        }
        this.mainElement = element.parents('.facets-item').find('.h3');
        this.parentMainElement = this.mainElement.parent();
        this.fctManager.currentFacetField = this.mainElement.attr('data-fctname');
        if (!this.parentMainElement.hasClass('active')) {
          this.parentMainElement.hide();
          this.parentMainElement.addClass('active');
        }
        this.buildStructure();
        this.fctManager.fetchFacetValues(this);
        this.opened = true;
        this.parentMainElement.find('.input-search-fct').focus();
      } else if (this.opened) {
        this.close();
      }
    } else if (element.attr('class') == 'h3' && this.opened) {
      this.close();
    } else {
      return false;
    }
  },

  buildStructure : function() {

    if (this.parentMainElement.find('.flyout-left-container').length > 0) {
      this.facetLeftContainer = this.parentMainElement.find('.flyout-left-container');
      this.selectedItems = this.parentMainElement.find('.selected-items');
      var inputSearchContainer = this.parentMainElement
          .find('.input-search-fct-container');
      this.inputSearch = this.parentMainElement.find('.input-search-fct');
    } else {
      this.buildLeftContainer();
    }

    this.facetRightContainer = $(document.createElement('div'));
    var rightHead = $(document.createElement('div'));
    this.rightBody = $(document.createElement('div'));
    // pagination structure for facets
    var paginationContainer = $(document.createElement('div'));
    var paginationUl = $(document.createElement('ul'));
    this.paginationLiPrev = $(document.createElement('li'));
    this.paginationLiNext = $(document.createElement('li'));
    this.paginationLiSeite = $(document.createElement('li'));
    var paginationAPrev = $(document.createElement('a'));
    var paginationANext = $(document.createElement('a'));
    var spanSeiteNumber = $(document.createElement('span'));

    this.facetRightContainer.addClass('flyout-right-container');
    this.facetRightContainer.hide();
    rightHead.addClass('flyout-right-head');
    this.rightBody.addClass('flyout-right-body');
    paginationContainer.addClass('flyout-page-nav fr');
    paginationUl.addClass('inline');
    this.paginationLiPrev.addClass('prev-page br off');
    this.paginationLiNext.addClass('next-page bl off');
    this.paginationLiSeite.addClass('pages-overall-index');
    paginationAPrev.attr('href', '');
    paginationANext.attr('href', '');

    this.facetRightContainer.appendTo(this.mainElement.parent());
    rightHead.appendTo(this.facetRightContainer);
    this.rightBody.appendTo(this.facetRightContainer);

    paginationAPrev.html(this.field_SearchResultsFacetValuePrevious);
    paginationANext.html(this.field_SearchResultsFacetValueNext);
    this.paginationLiSeite.html(this.field_Page);

    paginationContainer.appendTo(rightHead);
    paginationUl.appendTo(paginationContainer);
    paginationAPrev.appendTo(this.paginationLiPrev);
    paginationANext.appendTo(this.paginationLiNext);
    spanSeiteNumber.appendTo(this.paginationLiSeite);
    this.paginationLiPrev.appendTo(paginationUl);
    this.paginationLiSeite.appendTo(paginationUl);
    this.paginationLiNext.appendTo(paginationUl);

    this.parentMainElement.fadeIn('fast');
    this.facetRightContainer.fadeIn('fast');
    this.parentMainElement.find('.input-search-fct-container').fadeIn('fast');
  },

  buildLeftContainer : function() {
    this.facetLeftContainer = $(document.createElement('div'));
    this.selectedItems = $(document.createElement('ul'));
    var inputSearchContainer = $(document.createElement('div'));
    this.inputSearch = $(document.createElement('input'));

    this.facetLeftContainer.addClass('flyout-left-container');
    this.selectedItems.addClass('selected-items unstyled');
    inputSearchContainer.addClass('input-search-fct-container');
    this.inputSearch.attr('type', 'text');
    this.inputSearch.addClass('input-search-fct');

    this.facetLeftContainer.appendTo(this.mainElement.parent());
    this.selectedItems.appendTo(this.facetLeftContainer);
    this.inputSearch.appendTo(inputSearchContainer);
    inputSearchContainer.appendTo(this.facetLeftContainer);
    this.fctManager.initializeFacetValuesDynamicSearch(this.inputSearch);
  },

  initializeFacetValues : function(field, facetValues) {
    var leftCol = $(document.createElement('ul'));
    var rightCol = $(document.createElement('ul'));

    this.rightBody.empty();

    leftCol.addClass('left-col unstyled');
    rightCol.addClass('right-col unstyled');

    leftCol.appendTo(this.rightBody);
    rightCol.appendTo(this.rightBody);

    this.renderFacetValues(field, facetValues);
    this.fctManager.initPagination();
  },

  renderFacetValues : function(field, facetValues) {
    var currObjInstance = this;

    var leftCol = this.rightBody.find('.left-col');
    var rightCol = this.rightBody.find('.right-col');
    var flyoutRightHeadTitle;
    if (this.facetRightContainer.find('.flyout-right-head span').length > 0) {
      flyoutRightHeadTitle = $(this.facetRightContainer.find('.flyout-right-head span')[0]);
    } else {
      flyoutRightHeadTitle = $(document.createElement('span'));
    }
    if (field == this.fctManager.currentFacetField && facetValues.length > 0) {
      flyoutRightHeadTitle.html(this.field_MostRelevant);
      if (facetValues.length > 5) {
        this.rightBody.addClass('body-extender');
      } else {
        this.rightBody.removeClass('body-extender');
      }
      this.rightBody.fadeOut('fast', function() {
        leftCol.empty();
        rightCol.empty();

        $.each(facetValues, function(index) {
          if (jQuery.inArray(this.value,
              currObjInstance.fctManager.currentFacetValuesSelected) == -1) {
            var facetValueContainer = $(document.createElement('li'));
            var facetValueAnchor = $(document.createElement('a'));
            var spanCount = $(document.createElement('span'));

            facetValueAnchor.attr('href', '#');

            var facetValue = this.value;
            var localizedValue = this.localizedValue;

            facetValueContainer.click(function() {
              currObjInstance.fctManager.selectFacetValue($(this).attr('data-fctvalue'),
                  localizedValue.replace('<strong>', '').replace('</strong>', ''));
              $(this).remove();
              return false;
            });

            facetValueContainer.attr('data-fctvalue', _.escape(facetValue));
            spanCount.html('(' + this.count + ')');

            if (index < 5) {
              facetValueContainer.appendTo(leftCol);
            } else if (index < 10) {
              facetValueContainer.appendTo(rightCol);
            }
            facetValueAnchor.appendTo(facetValueContainer);
            facetValueAnchor.html(localizedValue);
            spanCount.prependTo(facetValueAnchor);
          }
        });
        currObjInstance.rightBody.fadeIn('fast');
      });
    } else {
      flyoutRightHeadTitle.html(this.field_NoAvailableValues);
    }
    flyoutRightHeadTitle.prependTo(this.facetRightContainer.find('.flyout-right-head'));
  },

  renderSelectedFacetValue : function(facetValue, localizedValue) {
    var facetValueContainer = $(document.createElement('li'));
    var facetValueSpan = $(document.createElement('span'));
    var facetValueRemove = $(document.createElement('a'));

    facetValueContainer.attr('data-fctvalue', facetValue);
    facetValueSpan.attr('title', localizedValue);
    facetValueSpan.html(localizedValue);
    facetValueSpan.addClass('facet-value');

    facetValueRemove.attr('href', '#');
    facetValueRemove.attr('title', this.field_RemoveButton);
    facetValueRemove.addClass('facet-remove fr');

    facetValueSpan.appendTo(facetValueContainer);

    facetValueRemove.appendTo(facetValueContainer);
    facetValueContainer.appendTo(this.selectedItems);

    return facetValueContainer;
  },

  renderRoleFacetValues : function(facetValueContainer, facetValue, facetField, roleFacetValues) {
    var currObjInstance = this;

    console.log("renderRoleFacetValue() " + facetValue + " | " + facetField + " | " + roleFacetValues);
    
    // Find the span element of the facetvalue
    var facetValueSpan = facetValueContainer.find('.facet-value');

    var newUl = false;
    var roleFacetValueUl = facetValueContainer.find('ul');
    if (roleFacetValueUl.length === 0) {
      newUl = true;
      roleFacetValueUl = $(document.createElement('ul'));
      roleFacetValueUl.addClass('unstyled');
    }

    // Create the role based facets and add them to the container
    $.each(roleFacetValues.values,
        function(index, value) {
          console.log("each value: " + value.value)

          var roleFacetValueLi = $(document.createElement('li'));
          var roleFacetValueSpan = $(document.createElement('span'));
          var roleFacetValueCheckbox = $(document.createElement('input'));
          var roleFieldMessage = messages.ddbnext['facet' + currObjInstance.fctManager.getRoleFromFacetValue(value.value)];

          roleFacetValueLi.addClass('role-facet');

          roleFacetValueSpan.attr('title', "RoleValue");
          roleFacetValueSpan.attr('facetField', facetField);
          roleFacetValueSpan.attr('roleValue', value.value);
          roleFacetValueSpan.html(roleFieldMessage() + ' (' + value.count + ')');
          roleFacetValueSpan.addClass('role-facet-value');

          roleFacetValueCheckbox.attr('type', "checkbox");
          roleFacetValueCheckbox.addClass('role-facet-checkbox');

          // If renderRoleFacetValue is invoked by initializeSelectedFacetOnLoad
          // we have to find out if the checkbox must be checked
          var paramsFacetValues = de.ddb.next.search.getFacetValuesFromUrl();

          if (paramsFacetValues) {
            var search = facetField + '=' + value.value;
            $.each(paramsFacetValues, function(key, value) {
              paramsFacetValues[key] = decodeURIComponent(value.replace(/\+/g, '%20'));
            });

            if (jQuery.inArray(search, paramsFacetValues) != -1) {
              roleFacetValueCheckbox.prop('checked', true);
            }
          }

          // add action handler
          roleFacetValueCheckbox
              .click(function(event) {
                if (this.checked) {
                  currObjInstance.fctManager.selectRoleFacetValue(facetField, value.value);
                } else {
                  currObjInstance.fctManager.unselectRoleFacetValue(facetField, value.value);
                }
              });

          roleFacetValueSpan.appendTo(roleFacetValueLi);
          roleFacetValueCheckbox.appendTo(roleFacetValueLi);
          roleFacetValueLi.appendTo(roleFacetValueUl);
          
        });

    if (newUl) {
      roleFacetValueUl.insertAfter(facetValueSpan);
    }
  },

  renderAddMoreFiltersButton : function(facetField) {
    this.addMoreFilters = $(document.createElement('div'));
    var text = $(document.createElement('span'));
    var icon = $(document.createElement('span'));

    this.addMoreFilters.attr('data-fctname', facetField);
    text.html(this.field_AddMoreFiltersButtonTooltip);

    this.addMoreFilters.addClass('add-more-filters');
    icon.addClass('icon');

    text.appendTo(this.addMoreFilters);
    icon.appendTo(this.addMoreFilters);
    this.addMoreFilters.appendTo(this.facetLeftContainer);
    this.facetLeftContainer.find('.input-search-fct-container').appendTo(
        this.facetLeftContainer);

    this.addMoreFilters.click(function(event) {
      $(this).hide();
    });
  },

  removeAddMoreFiltersButton : function(FacetFieldFilter, addMoreFiltersElement) {
    addMoreFiltersElement.remove();
    this.resetFacetFieldFilter(FacetFieldFilter);
  },

  setFacetValuesPage : function(pageNumber) {
    var spanPGNumber = this.paginationLiSeite.find('span');
    if (spanPGNumber.length === 0) {
      ($(document.createElement('span')).html(pageNumber))
          .appendTo(this.paginationLiSeite)
    }
    $(spanPGNumber[0]).html(pageNumber);
  },

  renderFacetLoader : function() {
    this.rightBody.empty();
    var imgLoader = $(document.createElement('div'));
    imgLoader.addClass('small-loader');
    this.rightBody.prepend(imgLoader);
  },

  manageOutsideClicks : function(thisInstance) {
    $(document).mouseup(function(e) {
      var container = $(".facets-list");
      if (container.has(e.target).length === 0 && thisInstance.opened === true) {
        thisInstance.close();
      }
    });
  },

  cleanNonJsStructures : function() {
    $('.facets-item >ul').remove();
    $('.clear-filters').addClass('off');
  },

  close : function() {
    var currObjInstance = this;
    var oldParentMainElement = this.parentMainElement;
    oldParentMainElement.find('.input-search-fct-container').hide('100', function() {
      if (oldParentMainElement.find('.flyout-left-container ul li').length > 0) {
        oldParentMainElement.find('.add-more-filters').show('100');
      } else {
        currObjInstance.resetFacetFieldFilter(oldParentMainElement);
      }
    });
    oldParentMainElement.find('.flyout-right-container').hide('100', function() {
      oldParentMainElement.find('.flyout-right-container').remove();
    });
    this.inputSearch.attr('value', '');
    this.fctManager.currentPage = 1;
    this.fctManager.currentOffset = 0;
    this.fctManager.currentFacetValuesSelected = new Array();
    this.opened = false;
  },

  resetFacetFieldFilter : function(element) {
    element.fadeOut('fast', function() {
      element.removeClass('active');
      element.fadeIn('fast');
    });
  }
});