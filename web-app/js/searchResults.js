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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the search results page
/**
 * Is called after the DOM has been initialized, the new handler passed in will
 * be executed immediately
 */
$(function() {
  if (jsPageName == "results") {
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    // Fix for back-button problem with the searchfield: DDBNEXT-389
    if ($.browser.msie) {
      var queryCache = $("#querycache");
      var queryString = "";
      if (queryCache.length > 0) {
        queryString = queryCache.val();
      }
      $("#form-search-header .query").val(queryString);
    }

    if (window.history && history.pushState) {
      historyedited = false;
      historySupport = true;
      $(window).bind('popstate', function(e) {
        if (historyedited) {
          stateManager(location.pathname + location.search);
        }
      });
    } else {
      historySupport = false;
      // Utilized for browser that doesn't supports pushState.
      // It will be used as reference URL for all the ajax actions
      globalUrl = location.search.substring(1);
    }

    searchResultsInitializer();

    function stateManager(url) {
      $('#main-container').load(url + ' .search-results-container', function() {
        searchResultsInitializer();
      });
    }
  }
});

function historyManager(path) {
  if (historySupport) {
    window.history.pushState({
      path : path
    }, '', path);
    historyedited = true;
  } else {
    globalUrl = (path.indexOf('?') > -1) ? path.split('?')[1] : path;
    window.location = path;
  }
}

function getLocalizedFacetValue(facetField, facetValue) {
  if (facetField == 'affiliate_fct' || facetField == 'keywords_fct' || facetField == 'place_fct'
      || facetField == 'provider_fct') {
    return facetValue.toString();
  }
  if (facetField == 'type_fct') {
    return messages.ddbnext['type_fct_' + facetValue];
  }
  if (facetField == 'time_fct') {
    return messages.ddbnext['time_fct_' + facetValue];
  }
  if (facetField == 'language_fct') {
    return messages.ddbnext['language_fct_' + facetValue];
  }
  if (facetField == 'sector_fct') {
    return messages.ddbnext['sector_fct_' + facetValue];
  }
  return '';
}

function getLocalizedFacetField(facetField) {
  return messages.ddbnext['facet_' + facetField];
}

// Hovercard Information Item Manager
HovercardInfoItem = function(element) {
  this.init(element);
};

$.extend(HovercardInfoItem.prototype, {

  infoButton : null,
  hovercard : null,
  iid : null,

  opened : false,
  lock : false,

  hoverTime : 0,
  hoverTimeout : 300,

  init : function(element) {
    var currObjInstance = this;
    this.infoButton = element;
    this.hovercard = this.infoButton.find('.hovercard-info-item');
    this.iid = this.hovercard.attr('data-iid');

    this.infoButton.mouseenter(function() {
      var d = new Date();
      currObjInstance.hoverTime = d.getTime();
      currObjInstance.open();
    });
    this.hovercard.mouseenter(function() {
      currObjInstance.lock = true;
    });
    this.hovercard.mouseleave(function() {
      currObjInstance.close();
    });
    this.infoButton.mouseleave(function() {
      setTimeout(
          function() {
            var currentD = new Date();
            if (!currObjInstance.lock
                && currObjInstance.hoverTime + currObjInstance.hoverTimeout - 100 < currentD
                    .getTime()) {
              currObjInstance.close();
            }
          }, currObjInstance.hoverTimeout);
    });
  },
  open : function() {
    if (!this.opened) {
      this.opened = true;
      this.hovercard.fadeIn('fast');
      if (this.hovercard.find('.small-loader').length !== 0) {
        this.fetchInformationItem();
      }
    }
  },
  close : function() {
    this.hovercard.fadeOut('fast');
    this.opened = false;
    this.lock = false;
  },
  fetchInformationItem : function() {
    var currObjInstance = this;
    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : jsContextPath + '/informationitem/' + this.iid,
      complete : function(data) {
        var content = currObjInstance.hovercard.find('ul.unstyled');
        content.empty();
        var JSONresponse = jQuery.parseJSON(data.responseText);
        $.each(JSONresponse, function(key, value) {
          if (key !== 'last_update' && value != "") {
            var li = $(document.createElement('li'));
            var fieldName = $(document.createElement('span'));
            var fieldContent = $(document.createElement('span'));

            fieldName.addClass('fieldName');
            fieldContent.addClass('fieldContent');

            facetValues = new Array();
            for (i = 0; i < value.length; i++) {
              facetValues.push(value[i]);
            }

            fieldName.text(getLocalizedFacetField(key));
            fieldContent.text(facetValues.join());

            li.append(fieldName);
            li.append(fieldContent);
            content.append(li);
          }
        });
      }
    });
  }
});

function searchResultsInitializer() {
  $(this).on("searchChange", function() {
    setHovercardEvents();
    initComparison();
    checkFavorites();
    checkSavedSearch();
  });

  $('.results-paginator-options').removeClass('off');
  $('.results-paginator-view').removeClass('off');
  $('.page-input').removeClass('off');
  $('.keep-filters').removeClass('off');
  $('.page-nonjs').addClass("off");
  // $('.hovercard-info-item').removeClass('off');
  // $('.hovercard-info-item').fadeOut('fast');

  $(this).trigger("searchChange");

  $('.page-filter select').change(
      function() {
        var paramsArray = new Array(new Array('rows', this.value), new Array('offset', 0));
        fetchResultsList(addParamToCurrentUrl(paramsArray));
        $('.clear-filters').attr('href',
            $('.clear-filters').attr('href').replace(/rows=\d+/g, 'rows=' + this.value));
        return false;
      });

  $('.sort-results-switch select').change(
      function() {
        var paramsArray = new Array(new Array('sort', this.value), new Array('offset', 0));
        fetchResultsList(addParamToCurrentUrl(paramsArray));
        $('.clear-filters').attr(
            'href',
            $('.clear-filters').attr('href').replace(/sort=(RELEVANCE|ALPHA_DESC|ALPHA_ASC)/i,
                'sort=' + this.value));
        return false;
      });

  function addParamToCurrentUrl(arrayParamVal, urlString) {
    return addParamToUrl(arrayParamVal, null, urlString);
  }
  // This function will give you back the current url (if no urlParameters is
  // setted) plus the new parameters added
  // IMPORTANT: remember to pass your arrayParamVal already URL decoded
  function addParamToUrl(arrayParamVal, path, urlString) {
    var currentUrl = (historySupport) ? location.search.substring(1) : globalUrl;
    var queryParameters = {}, queryString = (urlString == null) ? currentUrl : urlString, re = /([^&=]+)=([^&]*)/g, m;
    while (m = re.exec(queryString)) {
      var decodedKey = decodeURIComponent(m[1].replace(/\+/g, '%20'));
      if (queryParameters[decodedKey] == null) {
        queryParameters[decodedKey] = new Array();
      }
      queryParameters[decodeURIComponent(m[1].replace(/\+/g, '%20'))].push(decodeURIComponent(m[2]
          .replace(/\+/g, '%20')));
    }
    $.each(arrayParamVal, function(key, value) {
      queryParameters[value[0]] = value[1];
    });
    var tmp = jQuery.param(queryParameters, true);
    updateLanguageSwitch(tmp);
    if (path == null) {
      return window.location.pathname + '?' + tmp;
    } else {
      return path + '?' + tmp;
    }
  }

  function removeParamFromUrl(arrayParamVal, path, urlString) {
    var currentUrl = (historySupport) ? location.search.substring(1) : globalUrl;
    var queryParameters = {}, queryString = (urlString == null) ? currentUrl : urlString, re = /([^&=]+)=([^&]*)/g, m;
    while (m = re.exec(queryString)) {
      var keyParam = decodeURIComponent(m[1].replace(/\+/g, '%20'));
      if (queryParameters[keyParam] == null) {
        queryParameters[keyParam] = new Array();
      }
      queryParameters[keyParam].push(decodeURIComponent(m[2].replace(/\+/g, '%20')));
    }
    $.each(arrayParamVal, function(key, value) {
      if (queryParameters[value[0]]
          && (paramIndex = $.inArray(value[1], queryParameters[value[0]])) > -1) {
        queryParameters[value[0]] = jQuery.grep(queryParameters[value[0]], function(cValue) {
          return cValue !== value[1];
        });
      }
    });
    var tmp = jQuery.param(queryParameters, true);
    updateLanguageSwitch(tmp);
    if (path == null) {
      return window.location.pathname + '?' + tmp;
    } else {
      return path + '?' + tmp;
    }
  }

  function updateLanguageSwitch(params) {
    params = params.replace(/\&?lang=[^\&]*/g, '');
    if (params.length > 0) {
      params += '&';
    }
    if (params.indexOf('&') === 0) {
      params = params.substring(1);
    }
    var pattern = /(.*?\?).*?(lang=\w*)/;
    $('.language-wrapper .selector').find('a[href]').each(function() {
      var matches = pattern.exec($(this).attr('href'));
      $(this).attr('href', matches[1] + params + matches[2]);
    });
  }
  
  function setSearchCookieParameter(arrayParamVal) {
    var searchParameters = readCookie("searchParameters" + jsContextPath);
    if (searchParameters != null && searchParameters.length > 0) {
      searchParameters = searchParameters.substring(1, searchParameters.length - 1);
      searchParameters = searchParameters.replace(/\\"/g, '"');
      var json = $.parseJSON(searchParameters);
      $.each(arrayParamVal, function(key, value) {
        if (value[1].constructor === Array) {
          for ( var i = 0; i < value[1].length; i++) {
            if (value[1][i].constructor === String) {
              value[1][i] = encodeURIComponent(value[1][i]).replace(/%20/g, '\+');
            }
          }
        } else if (value[1].constructor === String) {
          value[1] = encodeURIComponent(value[1]).replace(/%20/g, '\+');
        }
        json[value[0]] = value[1];
      });
      document.cookie = "searchParameters" + jsContextPath + "=\""
          + JSON.stringify(json).replace(/"/g, '\\"') + "\"";
    }
  }

  function removeSearchCookieParameter(paramName) {
    var searchParameters = readCookie("searchParameters" + jsContextPath);
    if (searchParameters != null && searchParameters.length > 0) {
      searchParameters = searchParameters.substring(1, searchParameters.length - 1);
      searchParameters = searchParameters.replace(/\\"/g, '"');
      var json = $.parseJSON(searchParameters);
      json[paramName] = null;
      document.cookie = "searchParameters" + jsContextPath + "=\""
          + JSON.stringify(json).replace(/"/g, '\\"') + "\"";
    }
  }

  function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for ( var i = 0; i < ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1, c.length);
      }
      if (c.indexOf(nameEQ) === 0) {
        return c.substring(nameEQ.length, c.length);
      }
    }
    return null;
  }

  $('.page-nav-result').click(function() {
    fetchResultsList(this.href);
    $('html, body').animate({
      scrollTop : 0
    }, 1000);
    return false;
  });
  $('#form-search-header button').click(
      function() {
        var searchParameters = readCookie("searchParameters" + jsContextPath);
        if (searchParameters != null && searchParameters.length > 0) {
          searchParameters = searchParameters.substring(1, searchParameters.length - 1);
          searchParameters = searchParameters.replace(/\\"/g, '"');
          var json = $.parseJSON(searchParameters);
          if (json["rows"]) {
            $(this).append('<input type="hidden" name="rows" value="' + json["rows"] + '"/>');
          }
          if (json["clustered"]) {
            $(this).append(
                '<input type="hidden" name="clustered" value="' + json["clustered"] + '"/>');
          }
          if (json["isThumbnailFiltered"]) {
            $(this).append(
                '<input type="hidden" name="isThumbnailFiltered" value="'
                    + json["isThumbnailFiltered"] + '"/>');
          }
          if (json["viewType"]) {
            $(this).append(
                '<input type="hidden" name="viewType" value="' + json["viewType"] + '"/>');
          }
          if (json["sort"]) {
            $(this).append('<input type="hidden" name="sort" value="' + json["sort"] + '"/>');
          }
        }
      });
  $('#form-search-header input').keyup(function(e) {
    if (e.keyCode === 13 && $.browser.msie && parseFloat($.browser.version) <= 8.0) {
      $('#form-search-header button').click();
    }
    return false;
  });
  $('#view-list').click(
      function() {
        $('.summary-main .title a').each(
            function(index, value) {
              var newTitle = value.title.toString();
              if (newTitle.length > 100) {
                newTitle = $.trim(newTitle).substring(0, 100).split(" ").slice(0, -1).join(" ")
                    + "...";
              }
              if ($(this).closest('.summary-main').find('.matches li span strong').length === 0
                  && jQuery.trim($(value).find('strong')).length > 0) {
                newTitle = jQuery.trim($(value).html());
              } else {
                var replacementsRegex = new StringBuilder();
                replacementsRegex.append("(");
                $(this).closest('.summary-main').find('.matches li span strong').each(
                    function(sindex, svalue) {
                      var tmpSvalueText = (svalue.innerHTML + '').replace(/([.?*+^$[\]\\(){}|-])/g,
                          "\\$1");
                      if (replacementsRegex.getLength() > 1) {
                        replacementsRegex.append("|");
                      }
                      replacementsRegex.append(tmpSvalueText);
                    });
                replacementsRegex.append(")");
                newTitle = newTitle.replace(new RegExp(replacementsRegex.toString(), 'gi'),
                    "<strong>\$1</strong>");
              }
              value.innerHTML = newTitle;
            });
        $('#view-list').addClass('selected');
        $('#view-list').attr("disabled", "disabled");
        $('#view-grid').removeClass('selected');
        $('#view-grid').removeAttr('disabled');
        $('.search-results').fadeOut(
            'fast',
            function() {
              $('.results-list .summary').addClass('row');
              $('.summary-main-wrapper').not('.summary-main-wrapper-gnd').addClass('span6');
              $('.thumbnail-wrapper').addClass('span3');
              $('.results-list .item-options').not('.entity-list .item-options').addClass('bl');
              $('.results-list .item-options .information').not(
                  '.entity-list .item-options .information').addClass('bb');
              $('.results-list .item-options .compare').not('.entity-list .item-options .compare')
                  .addClass('bb');
              $('.results-list').removeClass("grid");
              $('.search-results').fadeOut('fast');
              $('.summary .thumbnail-wrapper').each(function() {
                $(this).appendTo(this.parentNode);
              });
              $('.search-results').fadeIn('fast');
            });
        var paramsArray = new Array(new Array('viewType', 'list'));
        var newUrl = addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });
        $('.results-list a').each(function() {
          this.href = addParamToUrl(paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=list'));
        historyManager(newUrl);
        setSearchCookieParameter(paramsArray);
      });
  $('.page-input').keyup(function(e) {
    if (e.keyCode === 13) {
      if (/^[0-9]+$/.test(this.value)) {
        var resultPagesCountText = $('.result-pages-count').text();
        var resultPagesCountInt = parseInt(resultPagesCountText.replace(/[^0-9]/g, ''));

        if (parseInt(this.value) <= 0) {
          this.value = 1;
        } else if (parseInt(this.value) > resultPagesCountInt) {
          this.value = $('.result-pages-count').text();
        }
      } else {
        this.value = 1;
      }

      $('.page-input').attr('value', this.value);

      var valueInteger = parseInt(this.value.replace(/[^0-9]/g, ''));
      var filterInteger = parseInt($('.page-filter').find("select").val());
      var offset = (valueInteger - 1) * filterInteger;
      var paramsArray = new Array(new Array('offset', offset));
      var newUrl = addParamToCurrentUrl(paramsArray);
      fetchResultsList(newUrl);
    }
  });
  $('#thumbnail-filter').click(function() {
    var valueCheck = $(this);
    if (valueCheck.is(':checked')) {
      var paramsArray = new Array(new Array('isThumbnailFiltered', 'true'));
    } else {
      var paramsArray = new Array(new Array('isThumbnailFiltered', 'false'));
    }
    paramsArray.push(new Array('offset', 0));
    var newUrl = addParamToCurrentUrl(paramsArray);
    fetchResultsList(newUrl);
  });
  $('#view-grid').click(
      function() {
        $('.summary-main .title a').each(
            function(index, value) {
              var newTitle = value.title.toString();
              if (newTitle.length > 53) {
                newTitle = $.trim(newTitle).substring(0, 53).split(" ").slice(0, -1).join(" ")
                    + "...";
              }
              if ($(this).closest('.summary-main').find('.matches li span strong').length === 0
                  && jQuery.trim($(value).find('strong')).length > 0) {
                newTitle = jQuery.trim($(value).html());
              } else {
                var replacementsRegex = new StringBuilder();
                replacementsRegex.append("(");
                $(this).closest('.summary-main').find('.matches li span strong').each(
                    function(sindex, svalue) {
                      var tmpSvalueText = (svalue.innerHTML + '').replace(/([.?*+^$[\]\\(){}|-])/g,
                          "\\$1");
                      if (replacementsRegex.getLength() > 1) {
                        replacementsRegex.append("|");
                      }
                      replacementsRegex.append(tmpSvalueText);
                    });
                replacementsRegex.append(")");
                newTitle = newTitle.replace(new RegExp(replacementsRegex.toString(), 'gi'),
                    "<strong>\$1</strong>");
              }
              value.innerHTML = newTitle;
            });
        var newTitle = $('.summary-main .title a').title;
        $('#view-list').removeClass('selected');
        $('#view-list').removeAttr('disabled');
        $('#view-grid').addClass('selected');
        $('#view-grid').attr("disabled", "disabled");
        $('.search-results').fadeOut(
            'fast',
            function() {
              // For no special line view of entity search results in grid ->
              // remove the not() statements again
              $('.results-list .summary').not('.entity-list .summary').removeClass('row');
              $('.results-list .summary-main-wrapper').not('.entity-list .summary-main-wrapper')
                  .removeClass('span6');
              $('.results-list .thumbnail-wrapper').not('.entity-list .thumbnail-wrapper')
                  .removeClass('span3');
              $('.results-list .item-options').not('.entity-list .item-options').removeClass('bl');
              $('.results-list .item-options .information').not(
                  '.entity-list .item-options .information').removeClass('bb');
              $('.results-list .item-options .compare').not('.entity-list .item-options .compare')
                  .removeClass('bb');
              $('.results-list').not('.entity-list').addClass("grid");
              $('.search-results').fadeOut('fast');
              $('.results-list .summary .summary-main-wrapper').not(
                  '.entity-list .summary .summary-main-wrapper').each(function() {
                $(this).appendTo(this.parentNode);
              });
              $('.search-results').fadeIn('fast');
            });
        var paramsArray = new Array(new Array('viewType', 'grid'));
        var newUrl = addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });
        $('.results-list a').each(function() {
          this.href = addParamToUrl(paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=grid'));
        historyManager(newUrl);
        setSearchCookieParameter(paramsArray);
        historyedited = true;
      });
  $('#keep-filters').click(function() {
    var valueCheck = $(this);
    if (valueCheck.is(':checked')) {
      var paramsArray = new Array(new Array('keepFilters', 'true'));
    } else {
      var paramsArray = new Array(new Array('keepFilters', 'false'));
    }
    addParamToCurrentUrl(paramsArray);
    setSearchCookieParameter(paramsArray);
  });
  $('.clear-filters').click(function() {
    removeSearchCookieParameter('facetValues[]');
  });

  function hideError() {
    $('.errors-container').remove();
  }

  function showError(errorHtml) {
    var errorContainer = ($('.search-results-list').find('.errors-container').length > 0) ? $(
        '.search-results-list').find('.errors-container') : $(document.createElement('div'));
    var errorIcon = $(document.createElement('i'));
    errorContainer.addClass('errors-container');
    errorIcon.addClass('icon-exclamation-sign');
    errorContainer.html(errorHtml);
    errorContainer.prepend(errorIcon);
    $('.search-results-list').prepend(errorContainer);
    var offset = errorContainer.offset();
    $('html, body').animate({
      scrollTop: offset.top,
      scrollLeft: offset.left
    });
  }

  function fetchResultsList(url, errorCallback) {

    var divSearchResultsOverlayModal = $(document.createElement('div'));
    divSearchResultsOverlayModal.addClass('search-results-overlay-modal');
    var divSearchResultsOverlayWaiting = $(document.createElement('div'));
    divSearchResultsOverlayWaiting.addClass('search-results-overlay-waiting');
    var divSearchResultsOverlayImg = $(document.createElement('div'));
    divSearchResultsOverlayImg.addClass('small-loader');
    divSearchResultsOverlayWaiting.append(divSearchResultsOverlayImg);

    $('.search-results').append(divSearchResultsOverlayModal);
    $('.search-results').append(divSearchResultsOverlayWaiting);

    var request = $.ajax({
      type : 'GET',
      dataType : 'json',
      async : true,
      url : url + '&reqType=ajax',
      success : function(data) {
        historyManager(url);
        if (!historySupport) {
          return;
        }

        $('.search-results-list').fadeOut(
            'fast',
            function() {
              var JSONresponse = data;
              if (JSONresponse.numberOfResults === 0) {
                $('.search-noresults-content').removeClass("off");
                $('.search-results-content').addClass("off");
              } else {
                $('.search-noresults-content').addClass("off");
                $('.search-results-content').removeClass("off");
              }
              $('.search-results-list').html(JSONresponse.results);
              $('.results-overall-index').html(JSONresponse.resultsOverallIndex);
              $('.page-input').attr('value', JSONresponse.page);
              $('.page-nonjs').html(JSONresponse.page);
              $('.total-pages').html(JSONresponse.totalPages);
              $('.result-pages-count').html(JSONresponse.totalPages);
              $('.results-total').html(JSONresponse.numberOfResults);
              if (JSONresponse.numberOfResults == '1') {
                $('.results-label').html(messages.ddbnext.Result_lowercase);
              } else {
                $('.results-label').html(messages.ddbnext.Results_lowercase);
              }
              if (JSONresponse.paginationURL.nextPg) {
                // first selector for desktop view, the second one for mobile
                // view
                $('.page-nav .next-page, .page-nav-mob .next-page a').removeClass('off');
                $('.page-nav .last-page').removeClass('off');
                // hide disabledArrow in mobile view
                $('.page-nav-mob .next-page .disabled-arrow').addClass('off');
                $('.page-nav .next-page a, .page-nav-mob .next-page a').attr('href',
                    JSONresponse.paginationURL.nextPg);
                $('.page-nav .last-page a').attr('href', JSONresponse.paginationURL.lastPg);
              } else {
                // first selector for desktop view, the second one for mobile
                // view
                $('.page-nav .next-page, .page-nav-mob .next-page a').addClass('off');
                // show disabledArrow in mobile view
                $('.page-nav-mob .next-page .disabled-arrow').removeClass('off');
                $('.page-nav .last-page').addClass('off');
              }
              if (JSONresponse.paginationURL.firstPg) {
                // first selector for desktop view, the second one for mobile
                // view
                $('.page-nav .prev-page, .page-nav-mob .prev-page a').removeClass('off');
                $('.page-nav .first-page').removeClass('off');
                // hide disabledArrow in mobile view
                $('.page-nav-mob .prev-page .disabled-arrow').addClass('off');
                $('.page-nav .prev-page a, .page-nav-mob .prev-page a').attr('href',
                    JSONresponse.paginationURL.prevPg);
                $('.page-nav .first-page a').attr('href', JSONresponse.paginationURL.firstPg);
              } else {
                // first selector for desktop view, the second one for mobile
                // view
                $('.page-nav .prev-page, .page-nav-mob .prev-page a').addClass('off');
                // show disabledArrow in mobile view
                $('.page-nav-mob .prev-page .disabled-arrow').removeClass('off');
                $('.page-nav .first-page').addClass('off');
              }

              $('.search-results-list').fadeIn('fast');

              divSearchResultsOverlayImg.remove();
              divSearchResultsOverlayWaiting.remove();
              divSearchResultsOverlayModal.remove();

              $(this).trigger('searchChange');
            });
      },
      error : function() {
        divSearchResultsOverlayImg.remove();
        divSearchResultsOverlayWaiting.remove();
        divSearchResultsOverlayModal.remove();

        showError(messages.ddbnext.An_Error_Occured);

        if (errorCallback) {
          errorCallback();
        }
      }
    });
  }

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
  FacetsManager = function() {
    this.init();
  };

  $
      .extend(
          FacetsManager.prototype,
          {

            connectedflyoutWidget : null,
            facetsEndPoint : jsContextPath + '/facets',
            rolefacetsEndPoint : jsContextPath + '/rolefacets',
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
            },

            fetchRoleFacets : function(flyoutWidget) {
              var currObjInstance = this;
              var url = this.rolefacetsEndPoint;
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
              var url = this.facetsEndPoint + '?name=' + roleFacet + '&query=' + facetValue;
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
                queryParam = '&query=' + query;
              }
              this.connectedflyoutWidget.renderFacetLoader();
              var request = $.ajax({
                type : 'GET',
                dataType : 'json',
                async : true,
                url : this.facetsEndPoint + '?name=' + this.currentFacetField + '&searchQuery='
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

              // Update Url (We want to add the facet value selected, but at the
              // same time we want to keep all the old selected values)
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
              fetchResultsList(addParamToCurrentUrl(paramsArray), function() {
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
                removeSearchCookieParameter('facetValues[]');
              }
              if (!unselectWithoutFetch) {
                fetchResultsList(addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
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
              fetchResultsList(addParamToCurrentUrl(paramsArray));
            },

            unselectRoleFacetValue : function(facetField, facetValue) {
              var currObjInstance = this;
              var newUrl = removeParamFromUrl(new Array(new Array('facetValues[]', facetField + '='
                  + facetValue)));
              if (decodeURIComponent(newUrl).indexOf('facetValues[]') == -1) {
                removeSearchCookieParameter('facetValues[]');
              }

              fetchResultsList(addParamToCurrentUrl(new Array(new Array('offset', 0)), newUrl
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
                $
                    .each(
                        selectedFacets,
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
                                  .renderSelectedFacetValue(this, getLocalizedFacetValue(fctField,
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

  /**
   * Flyout Widget
   * 
   * The main intend of this class is to render all content in the context of
   * facets Its doing this by DOM manipulation triggered by the FacetManager
   * instance. The Flyout Widget contains - facetLeftContainer: showing the
   * facet fields and the selected facets - facetRightContainer: showing the
   * unselected facets
   * 
   * Do not use AJAX calls in this class!
   */
  FlyoutFacetsWidget = function() {
    this.init();
  };

  $
      .extend(
          FlyoutFacetsWidget.prototype,
          {
            mainElement : null,
            parentMainElement : null,
            opened : false,
            fctManager : new FacetsManager(),

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

            renderRoleFacetValue : function(facetValueContainer, facetValue, facetField,
                roleFacetValues) {
              var currObjInstance = this;

              // Find the span element of the facetvalue
              var facetValueSpan = facetValueContainer.find('.facet-value');

              var newUl = false;
              var roleFacetValueUl = facetValueContainer.find('ul');
              if (roleFacetValueUl.length === 0) {
                newUl = true;
                roleFacetValueUl = $(document.createElement('ul'));
                roleFacetValueUl.addClass('unstyled');

                // FIXME Due to DDBNEXT-973 we do not show the role facet list
                roleFacetValueUl.addClass('off');
              }

              // Create the role based facets and add them to the container
              $.each(roleFacetValues.values,
                  function(index, value) {
                    // The role search could return more than one role. So the
                    // roleFacetValue must match exactly the facetValue!
                    if (facetValue === value.value) {
                      var roleFacetValueLi = $(document.createElement('li'));
                      var roleFacetValueSpan = $(document.createElement('span'));
                      var roleFacetValueCheckbox = $(document.createElement('input'));
                      var roleFieldMessage = messages.ddbnext['facet_' + facetField];

                      roleFacetValueLi.addClass('role-facet');

                      roleFacetValueSpan.attr('title', "RoleValue");
                      roleFacetValueSpan.attr('facetField', facetField);
                      roleFacetValueSpan.html(roleFieldMessage() + ' (' + value.count + ')');
                      roleFacetValueSpan.addClass('role-facet-value');

                      roleFacetValueCheckbox.attr('type', "checkbox");
                      roleFacetValueCheckbox.addClass('role-facet-checkbox');

                      // If renderRoleFacetValue is invoked by
                      // initializeSelectedFacetOnLoad
                      // we have to find out if the checkbox must be checked
                      var paramsFacetValues = currObjInstance.fctManager
                          .getUrlVar('facetValues%5B%5D');
                      if (paramsFacetValues == null) {
                        paramsFacetValues = currObjInstance.fctManager.getUrlVar('facetValues[]');
                      }

                      if (paramsFacetValues) {
                        var search = facetField + '=' + facetValue;
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
                              currObjInstance.fctManager.selectRoleFacetValue(facetField,
                                  facetValue);
                            } else {
                              currObjInstance.fctManager.unselectRoleFacetValue(facetField,
                                  facetValue);
                            }
                          });

                      roleFacetValueSpan.appendTo(roleFacetValueLi);
                      roleFacetValueCheckbox.appendTo(roleFacetValueLi);
                      roleFacetValueLi.appendTo(roleFacetValueUl);
                    }
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

  function initializeFacets() {
    var fctWidget = new FlyoutFacetsWidget();
    $('.facets-item a').each(function() {
      $(this).click(function(event) {
        event.preventDefault();
        fctWidget.build($(this));
      });
    });
    fctWidget.manageOutsideClicks(fctWidget);
  }
  initializeFacets();
  // -- End Facet Manager

  function setHovercardEvents() {
    //  $('.thumbnail a').mouseenter(function(){
    //      $(this).parents('.thumbnail-wrapper').find('.hovercard-info-item').addClass('on');
    //  });
    //  $('.thumbnail a').mouseleave(function(){
    //      $(this).parents('.thumbnail-wrapper').find('.hovercard-info-item').removeClass('on');
    //  });
    $('.information').each(function() {
      new HovercardInfoItem($(this));
    });
  }

  /**
   * Initialize the components for the object comparison
   */
  function initComparison() {
    // Comparison should only works with Javascript. So remove the CSS class
    // 'off' from the compare components
    $('.compare').removeClass("off");
    $('.compare-objects').removeClass("off");

    $('.compare-objects .fancybox-toolbar-close').click(function(event) {
      event.preventDefault();
      // Get the index of the compare-object.
      var index = $(event.target).attr("data-index");
      removeCompareCookieParameter(index);
      renderCompareObjects();
    });

    renderCompareObjects();
  }
  
  
  /**
   * This functions selects the item from the result list and try to store some parameters in the comparison cookie.
   */
  function selectCompareItem(id) {	  
    var itemId = id;

    // select the thumbnail image of the selected item
    var image = $('#thumbnail-' + itemId + ' img');
    var imageSrc = null;

    if (image.attr("src").indexOf(itemId) != -1) {
      imageSrc = image.attr("src");
    }

    var text = image.attr("alt");
    if (text.length > 30) {
      text = text.substr(0, 30) + "...";
    }

    setCompareCookieParameter(itemId, imageSrc, text);

    renderCompareObjects();
  }
  
  /**
   * This funtion is responsible for rendering the compare components.
   * There are two compare obejects which can hold either
   * <ul>
   * <li>a default text</li>
   * <li>an image of the item</li>
   * <li>a text of the item</li>
   * </ul>
   * 
   * The rendering is based on the item values stored in the comparison cookie.
   * 
   */
  function renderCompareObjects() {
    var cookieVal = getComparisonCookieVal();

    // Rendering the value for bothe compare-objects
    $('.compare-object').each(
        function(index) {
          // closure index starts with 0!
          var itemNumber = index + 1;

          // Retrieve the elements via JQuery
          var compareObjectId = '#compare-object' + itemNumber;
          var compareLink = $(compareObjectId + ' .compare-link');
          var compareImage = $(compareObjectId + ' .compare-img');
          var compareText = $(compareObjectId + ' .compare-text');
          var compareDefault = $(compareObjectId + ' .compare-default');
          var compareRemove = $(compareObjectId + ' .fancybox-toolbar-close');

          // Get the associated item id from the cookie
          var cookieId = (cookieVal !== null) ? cookieVal['id' + itemNumber]
              : null;

          // Set default message if no cookie exists or itemId is null
          if (cookieVal === null || cookieId === null) {
            compareDefault.removeClass("off");
            compareText.addClass("off");
            compareImage.addClass("off");
            compareRemove.addClass("off");
          } else {
            var cookieSrc = cookieVal['src' + itemNumber];
            var cookieText = cookieVal['text' + itemNumber];
            
            //Hide default message
            compareDefault.addClass("off");
            compareRemove.removeClass("off");

            // be sure to get the latest compare items and url queries (facets etc.) for the anchor reference. So use an click event for this issue
            compareLink.off();
            compareLink.on("click", function(event) {
              //Update the url of the link
              var urlQuery = window.location.search
              var url = jsContextPath + '/item/' + cookieId + urlQuery;
              compareLink.attr("href", url);
            });
            
            // show the item's image or text
            if (cookieSrc !== null && cookieSrc.length !== -1) {
              compareImage.attr("src", cookieSrc);
              compareImage.attr("alt", cookieText);
              compareImage.attr("title", cookieText);
              
              compareText.addClass("off");
              compareImage.removeClass("off");                                 
            } else {
              compareText.html(cookieText);
              
              compareText.removeClass("off");
              compareImage.addClass("off");
            }
          }
        });

    setComparisonButtonState();
    setItemCompareButtonState();
  }
  
  /**
   * Try to set an item to the comparison cookie. The cookie has two slots for holding two unequal items. Equalsness is checked via the item id.
   * 
   * An item is set to the first free slot found in the cookie.
   * 
   * The value of the cookie is in JSON format and can hold the id, src and text of an item. 
   */
  function setCompareCookieParameter(itemId, imgSrc, text) {
    var cookieVal = getComparisonCookieVal();

    hideError();

    // if the cookie exists, check if the first or second compare slot is free
    if (cookieVal !== null) {
      if ((cookieVal.id1 !== null) && (cookieVal.id2 !== null)) {
        showError(messages.ddbnext.SearchResultsCompareItemOnly2);
      } else if ((cookieVal.id1 === itemId) || (cookieVal.id2 === itemId)) {
        showError(messages.ddbnext.SearchResultsCompareItemAlreadySet);
      } else if (cookieVal.id1 === null) {
        cookieVal.id1 = itemId;
        cookieVal.src1 = imgSrc;
        cookieVal.text1 = text;
        itemAdded = true;
      } else if (cookieVal.id2 === null) {
        cookieVal.id2 = itemId;
        cookieVal.src2 = imgSrc;
        cookieVal.text2 = text;
        itemAdded = true;
      }
    }
    // if the cookie don't exist create a new value and set the item on the
    // first position
    else {
      cookieVal = {
        id1 : itemId,
        src1 : imgSrc,
        text1 : text,
        id2 : null,
        src2 : null,
        text2 : null
      }
    }

    // Set the cookie
    setComparisonCookieVal(cookieVal);
  }
  
  /**
   * Removes an comparison item from the cookie. 
   * The index parameters can have the values 1 and 2 
   */
  function removeCompareCookieParameter(index) {
    var cookieVal = getComparisonCookieVal();
    hideError();

    if ((1 != index) && (2 != index)) {
      return;
    }

    if (cookieVal !== null) {
      cookieVal['id' + index] = null;
      cookieVal['src' + index] = null;
      cookieVal['text' + index] = null;

      setComparisonCookieVal(cookieVal);
    }
  }
  

  /**
   * Activating and deactivating of the compare icon of each item.
   * The state of the item depends on the item id's stored in the cookie
   */
  function setItemCompareButtonState() {
    var cookieVal = getComparisonCookieVal();

    // By default all compare buttons of the items are enabled
    $('.compare').each(function() {
      $(this).off();
      $(this).removeClass("disabled");

      $(this).click(function(event) {
        event.stopPropagation();
        var item = $(event.target);
        var itemId = item.attr('data-iid');
        selectCompareItem(itemId);
      });
    });

    // Disable the item compare buttons for all selected items
    var selectedItems = $('.compare').filter(
        function(index) {
          if (cookieVal) {
            return ($(this).attr('data-iid') == cookieVal.id1 || $(this).attr('data-iid') == cookieVal.id2);
          }
        });
    selectedItems.each(function(index) {
      $(this).off();
      $(this).addClass("disabled");
    });
  }

  
  /**
   * Enable/Disable the comparison button.
   * For this the selected items id's stored in the cookie are needed.
   * 
   * Activate the button if two items are selected. Otherwise disable the button.
   */
  function setComparisonButtonState() {
    var cookieVal = getComparisonCookieVal();

    // The compare buton is disabled by default
    var compareButton = $('#compare-button');
    compareButton.removeClass('button');
    compareButton.addClass('button-disabled')

    if (cookieVal !== null) {
      // Enable the compare button only if two items are selected for comparison
      if ((cookieVal.id1 !== null) && (cookieVal.id2 !== null)) {
        compareButton.removeClass('button-disabled');
        compareButton.addClass('button');
                       
        // be sure to get the latest compare items and url queries (facets etc.) for the anchor reference. So use an click event for this issue
        compareButton.off();
        compareButton.on("click", function(event) {
          var urlQuery = window.location.search
          var url = jsContextPath + '/compare/' + cookieVal.id1 + '/with/'
              + cookieVal.id2 + urlQuery;
          compareButton.attr("href", url);
        });
      }
    }
  }
  
  function getComparisonCookieVal() {
    var cookieName = 'compareParameters' + jsContextPath;
    var cookieVal = $.cookies.get(cookieName);
    
    return cookieVal;
  }
  
  function setComparisonCookieVal(cookieVal) {
    var cookieName = 'compareParameters' + jsContextPath;
    $.cookies.set(cookieName, cookieVal);
  }
  
  
  function addToSavedSearches() {
    $.urlParam = function(name) {
      var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
      if (results == null) {
        return null;
      } else {
        return results[1] || "";
      }
    };
    $.truncateTitle = function(string) {
      var result = "";
      var words = string.split(/\s+/);
      for ( var index = 0; index < 3 && index < words.length; index++) {
        if (result.length > 0) {
          result += " ";
        }
        result += words[index];
      }
      return result;
    };
    var queryString = decodeURIComponent($.urlParam("query").replace(/\+/g, '%20'));
    // take only the first 3 words as title
    $("#addToSavedSearchesTitle").val($.truncateTitle(queryString));
    $("#addToSavedSearchesModal").modal("show");
    $("#addToSavedSearchesConfirm").unbind("click");
    $("#addToSavedSearchesConfirm").click(function(e) {
      $("#addToSavedSearchesModal").modal("hide");
      var title = $("#addToSavedSearchesTitle").val();
      if (title.length > 0) {
        hideError();
        $.ajax({
          type : "PUT",
          contentType : "application/json",
          dataType : "json",
          url : jsContextPath + "/apis/savedsearches",
          data : JSON.stringify({
            query : window.location.search.substring(1),
            title : title
          })
        }).done(function() {
          disableSavedSearch($(".add-to-saved-searches"));
        });
      } else {
        showError(messages.ddbnext.Savedsearch_Without_Title);
      }
    });
  }

  /**
   * AJAX request to check if a result hit is already stored in the list of favorites.
   *
   * Install a click event handler to add a result hit to the list of favorites.
   */
  function checkFavorites() {
    var itemIds = [];

    // Only perform this check if a user is logged in
    if (jsLoggedIn == "true") {

      // collect all item ids on the page
      $(".search-results .summary-main .persist").each(function() {
        itemIds.push(extractItemId($(this).attr("href")));
      });

      // check if a result hit is already stored in the list of favorites
      $.ajax({
        type : "POST",
        url : jsContextPath + "/apis/favorites/_get",
        contentType : "application/json",
        data : JSON.stringify(itemIds),
        success : function(favoriteItemIds) {
          $.each(itemIds, function(index, itemId) {
            var div = $("#favorite-" + itemId);

            if ($.inArray(itemId, favoriteItemIds) >= 0) {
              disableFavorite(div);
            } else {
              $(div).click(
                  function() {
                    disableFavorite(div);
                    // add a result hit to the list of favorites
                    $.post(jsContextPath + "/apis/favorites/" + itemId, function(data) {
                      $("#favorite-confirmation").modal("show");
                      $.post(jsContextPath + "/apis/favorites/folders", function(folders) {
                        if (folders.length > 1) {
                          $("#favorite-folders").empty();
                          $.each(folders, function(index, folder) {
                            if (!folder.isMainFolder) {
                              // show select box with all folder names
                              var selectEntry = "<option value=" + folder.folderId + ">"
                                  + folder.title.charAt(0).toUpperCase() + folder.title.slice(1)
                                  + "</option>";

                              $("#favorite-folders").append(selectEntry);
                            }
                          });
                          $("#favoriteId").val(itemId);
                          $("#addToFavoritesConfirm").click(
                              function() {
                                $("#favorite-confirmation").modal("hide");
                                $.each($("#favorite-folders").val(), function(index, value) {
                                  $.post(jsContextPath + "/apis/favorites/folders/" + value + "/"
                                      + itemId);
                                });
                              });
                        } else {
                          window.setTimeout(function() {
                            $("#favorite-confirmation").modal("hide");
                          }, 1500);
                        }
                      });
                    });
                  });
            }
          });
        }
      });
    }
  }

  /**
   * Check if the current search string is already stored as a saved search.
   */
  function checkSavedSearch() {
    // Only perform this check if a user is logged in
    if (jsLoggedIn == "true") {

      $.ajax({
        type : "POST",
        contentType : "application/json",
        dataType : "json",
        url : jsContextPath + "/apis/savedsearches/_get",
        data : JSON.stringify({
          query : window.location.search.substring(1)
        })
      }).done(function() {
        disableSavedSearch($(".add-to-saved-searches"));
      }).fail(function() {
        enableSavedSearch($(".added-to-saved-searches"));
      });

    }
  }

  /**
   * Disable a favorite button.
   *
   * @param div DIV element which handles the favorite event
   */
  function disableFavorite(div) {
    div.unbind("click");
    div.removeAttr("title");
    div.removeClass("add-to-favorites");
    div.addClass("added-to-favorites");
    div.attr('title', messages.ddbnext.favorites_already_saved);
  }

  /**
   * Disable the saved search button.
   *
   * @param div DIV element which handles the saved search event
   */
  function disableSavedSearch(div) {
    $("#addToSavedSearches").unbind("click");
    div.removeClass("add-to-saved-searches");
    div.addClass("added-to-saved-searches");
    $("#addToSavedSearchesAnchor").addClass("off");
    $("#addToSavedSearchesSpan").removeClass("off");
  }

  /**
   * Enable the saved search button.
   *
   * @param div DIV element which handles the saved search event
   */
  function enableSavedSearch(div) {
    $("#addToSavedSearches").unbind("click");
    $("#addToSavedSearches").click(function() {
      addToSavedSearches();
    });
    div.removeClass("added-to-saved-searches");
    div.addClass("add-to-saved-searches");
    $("#addToSavedSearchesSpan").addClass("off");
    $("#addToSavedSearchesAnchor").removeClass("off");
  }

  /**
   * Extract the item id from the given URL.
   *
   * @param url the URL containing the item id
   *
   * @returns item id
   */
  function extractItemId(url) {
    var result = null;
    var parts = url.split("/");

    result = parts[parts.length - 1];

    var queryParameters = result.indexOf("?");

    if (queryParameters >= 0) {
      result = result.substring(0, queryParameters);
    }
    return result;
  }
};
