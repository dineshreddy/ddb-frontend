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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

/**
 * Is called after the DOM has been initialized, the new handler passed in will
 * be executed immediately
 */
$(function() {
  if (jsPageName === "results") {
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

    de.ddb.next.search.searchResultsInitializer();

    var stateManager = function(url) {
      $('#main-container').load(url + ' .search-results-container', function() {
        de.ddb.next.search.searchResultsInitializer();
      });
    }
  }
});

de.ddb.next.search.historyManager = function(path) {
  if (historySupport) {
    window.history.pushState({
      path : path
    }, '', path);
    historyedited = true;
  } else {
    globalUrl = (path.indexOf('?') > -1) ? path.split('?')[1] : path;
    window.location = path;
  }
};

de.ddb.next.search.getLocalizedFacetValue = function(facetField, facetValue) {
  if (facetField === 'affiliate_fct_role' || facetField === 'keywords_fct' || facetField === 'place_fct' || facetField === 'provider_fct') {
    return facetValue.toString();
  }
  if (facetField === 'type_fct') {
    return messages.ddbnext['type_fct_' + facetValue];
  }
  if (facetField === 'time_fct') {
    return messages.ddbnext['time_fct_' + facetValue];
  }
  if (facetField === 'language_fct') {
    return messages.ddbnext['language_fct_' + facetValue];
  }
  if (facetField === 'sector_fct') {
    return messages.ddbnext['sector_fct_' + facetValue];
  }
  return '';
};

de.ddb.next.search.getLocalizedFacetField = function(facetField) {
  return messages.ddbnext['facet_' + facetField];
};

de.ddb.next.search.fetchResultsList = function(url, errorCallback) {
  var divSearchResultsOverlayModal = $(document.createElement('div'));
  divSearchResultsOverlayModal.addClass('search-results-overlay-modal');
  var divSearchResultsOverlayWaiting = $(document.createElement('div'));
  divSearchResultsOverlayWaiting.addClass('search-results-overlay-waiting');
  var divSearchResultsOverlayImg = $(document.createElement('div'));
  divSearchResultsOverlayImg.addClass('small-loader');
  divSearchResultsOverlayWaiting.append(divSearchResultsOverlayImg);

  $('.search-results').append(divSearchResultsOverlayModal);
  $('.search-results').append(divSearchResultsOverlayWaiting);

  $.ajax({
    type : 'GET',
    dataType : 'json',
    async : true,
    url : url + '&reqType=ajax',
    success : function(data) {
      de.ddb.next.search.historyManager(url);
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
            if (JSONresponse.numberOfResults === '1') {
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

            $(window).trigger('searchChange');
          });
    },
    error : function() {
      divSearchResultsOverlayImg.remove();
      divSearchResultsOverlayWaiting.remove();
      divSearchResultsOverlayModal.remove();

      de.ddb.next.search.showError(messages.ddbnext.An_Error_Occured);

      if (errorCallback) {
        errorCallback();
      }
    }
  });
};

de.ddb.next.search.setSearchCookieParameter = function(arrayParamVal) {
  var searchParameters = de.ddb.next.search.readCookie("searchParameters" + jsContextPath);
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
};

de.ddb.next.search.removeSearchCookieParameter = function(paramName) {
  var searchParameters = de.ddb.next.search.readCookie("searchParameters" + jsContextPath);
  if (searchParameters != null && searchParameters.length > 0) {
    searchParameters = searchParameters.substring(1, searchParameters.length - 1);
    searchParameters = searchParameters.replace(/\\"/g, '"');
    var json = $.parseJSON(searchParameters);
    json[paramName] = null;
    document.cookie = "searchParameters" + jsContextPath + "=\""
        + JSON.stringify(json).replace(/"/g, '\\"') + "\"";
  }
};

de.ddb.next.search.readCookie = function(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for ( var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1, c.length);
    }
    if (c.indexOf(nameEQ) === 0) {
      return c.substring(nameEQ.length, c.length);
    }
  }
  return null;
};

de.ddb.next.search.hideError = function() {
  $('.errors-container').remove();
};

de.ddb.next.search.showError = function(errorHtml) {
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
};

de.ddb.next.search.initializeFacets = function() {
  var fctWidget = new de.ddb.next.search.FlyoutFacetsWidget();
  $('.facets-item a').each(function() {
    $(this).click(function(event) {
      event.preventDefault();
      fctWidget.build($(this));
    });
  });
  fctWidget.manageOutsideClicks(fctWidget);
};

de.ddb.next.search.searchResultsInitializer = function() {

  $(window).on("searchChange", function() {
    setHovercardEvents();
    var compareManager = new de.ddb.next.search.CompareManager();
    compareManager.initComparison();
    checkFavorites();
    checkSavedSearch();
  });

  $('.results-paginator-options').removeClass('off');
  $('.results-paginator-view').removeClass('off');
  $('.page-input').removeClass('off');
  $('.keep-filters').removeClass('off');
  $('.page-nonjs').addClass("off");

  $(window).trigger("searchChange");
  
  $('.page-filter select').change(
      function() {
        var paramsArray = [['rows', this.value], ['offset', 0]];
        de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray));
        if($('.clear-filters').attr('href')) {
          $('.clear-filters').attr('href',
              $('.clear-filters').attr('href').replace(/rows=\d+/g, 'rows=' + this.value));
          }
        return false;
      });

  $('.sort-results-switch select').change(
      function() {
        var paramsArray = [['sort', this.value], ['offset', 0]];
        de.ddb.next.search.fetchResultsList($.addParamToCurrentUrl(paramsArray));
        if($('.clear-filters').attr('href')) {
          $('.clear-filters').attr(
              'href',
              $('.clear-filters').attr('href').replace(/sort=(RELEVANCE|ALPHA_DESC|ALPHA_ASC)/i,
                  'sort=' + this.value));
        }
        return false;
      });

  $('.page-nav-result').click(function() {
    de.ddb.next.search.fetchResultsList(this.href);
    $('html, body').animate({
      scrollTop : 0
    }, 1000);
    return false;
  });
  $('#form-search-header button').click(
      function() {
        var searchParameters = de.ddb.next.search.readCookie("searchParameters" + jsContextPath);
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
        var paramsArray = [['viewType', 'list']];
        var newUrl = $.addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = $.addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });
        $('.results-list a').each(function() {
          this.href = addParamToUrl(paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=list'));
        de.ddb.next.search.historyManager(newUrl);
        de.ddb.next.search.setSearchCookieParameter(paramsArray);
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
      var paramsArray = [['offset', offset]];
      var newUrl = $.addParamToCurrentUrl(paramsArray);
      de.ddb.next.search.fetchResultsList(newUrl);
    }
  });
  $('#thumbnail-filter').click(function() {
    var valueCheck = $(this);
    if (valueCheck.is(':checked')) {
      var paramsArray = [['isThumbnailFiltered', 'true']];
    } else {
      var paramsArray = [['isThumbnailFiltered', 'false']];
    }
    paramsArray.push(['offset', 0]);
    var newUrl = $.addParamToCurrentUrl(paramsArray);
    de.ddb.next.search.fetchResultsList(newUrl);
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
        var paramsArray = [['viewType', 'grid']];
        var newUrl = $.addParamToCurrentUrl(paramsArray);
        $('.page-nav a, .page-nav-mob a').each(function() {
          this.href = $.addParamToCurrentUrl(paramsArray, this.href.split("?")[1]);
        });
        $('.results-list a').each(function() {
          this.href = addParamToUrl(paramsArray, this.href.split("?")[0], this.href.split("?")[1]);
        });
        $('.clear-filters a').attr('href',
            $('.clear-filters a').attr('href').replace(/viewType=(list|grid)/i, 'viewType=grid'));
        de.ddb.next.search.historyManager(newUrl);
        de.ddb.next.search.setSearchCookieParameter(paramsArray);
        historyedited = true;
      });
  $('#keep-filters').click(function() {
    var valueCheck = $(this);
    var paramsArray = [['keepFilters', 'false']];

    if (valueCheck.is(':checked')) {
      paramsArray = [['keepFilters', 'true']];
    }
    $.addParamToCurrentUrl(paramsArray);
    de.ddb.next.search.setSearchCookieParameter(paramsArray);
  });
  $('.clear-filters').click(function() {
    de.ddb.next.search.removeSearchCookieParameter('facetValues[]');
  });

  de.ddb.next.search.initializeFacets();

  function setHovercardEvents() {
    $('.information').each(function() {
      var infoItem = new de.ddb.next.search.HovercardInfoItem($(this));
    });
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
        de.ddb.next.search.hideError();
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
        de.ddb.next.search.showError(messages.ddbnext.Savedsearch_Without_Title);
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
    if (jsLoggedIn === "true") {

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
                    $.post(jsContextPath + "/apis/favorites/" + itemId, function() {
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
    if (jsLoggedIn === "true") {

      $.ajax({
        type : "POST",
        contentType : "application/json",
        dataType : "json",
        url : jsContextPath + "/apis/savedsearches/_get",
        data : JSON.stringify({
          query : window.location.search.substring(1)
        })
      }).statusCode( {
        200: function() { //SC_OK
          disableSavedSearch($(".add-to-saved-searches"));
        },
        204: function() { //SC_NO_CONTENT
          enableSavedSearch($(".added-to-saved-searches"));
        }
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
