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
de.ddb.next.search.institution = de.ddb.next.search.institution || {};

$(function() {
  if (jsPageName === "searchinstitution") {

    //Callback function for history changes
    var stateManager = function(url) {
      $('#main-container').load(url + ' .search-results-container', function() {
        de.ddb.next.search.searchResultsInitializer();
      });
    };
    
    //Initialize the history support
    de.ddb.next.search.initHistorySupport(stateManager);

    //Initialize the search page
    de.ddb.next.search.institution.searchResultsInitializer();
  }
});

/**
 * Initialize the searchResultPage 
 */
de.ddb.next.search.institution.searchResultsInitializer = function() {
  $(window).on("searchChange", function() {
    de.ddb.next.search.institution.setHovercardEvents();
  });
  
  de.ddb.next.search.institution.setHovercardEvents();
  de.ddb.next.search.institution.initializeFacets();
}

/**
 * Makes an AJAX request for searching institutions with the given filter criterias.
 */
de.ddb.next.search.institution.fetchResultsList = function(url, errorCallback) {
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
            
            if(JSONresponse.paginationURL.pages){
              var indexes = $('.page-nav .pages-overall-index');
              $.each(indexes, function(){
                
                var spanContainer = $(this).find('span')
                
                $(this).find('a').each(function(){
                  $(this).remove();
                });
                
                $.each(JSONresponse.paginationURL.pages, function(){
                  var tmpAnchor = $(document.createElement('a'));
                  tmpAnchor.addClass('page-nav-result');
                  tmpAnchor.html(this.pageNumber);
                  if(this.active){
                    tmpAnchor.addClass('active');
                  }
                  else{
                    tmpAnchor.attr('href', this.url);
                  }
                  spanContainer.append(tmpAnchor);
                });
              });
            }
            
            //Showing extra arrow
            if(JSONresponse.totalPages > 5){
              $('.extra-controls').removeClass('off');
            }else{
              $('.extra-controls').addClass('off');
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

/**
 * Initialize the facets filter.
 */
de.ddb.next.search.institution.initializeFacets = function() {
  var facetsManager = new de.ddb.next.search.FacetsManager(de.ddb.next.search.institution.fetchResultsList);
  var fctWidget = new de.ddb.next.search.FlyoutFacetsWidget(facetsManager);
  $('.facets-item a').each(function() {
    $(this).click(function(event) {
      event.preventDefault();
      fctWidget.build($(this));
    });
  });
  fctWidget.manageOutsideClicks(fctWidget);
};

/**
 * Sets the HovercardInfoItem showing some facet values for each result item
 */
de.ddb.next.search.institution.setHovercardEvents = function() {
  $('.information').each(function() {
    var infoItem = new de.ddb.next.search.HovercardInfoItem($(this));
  });
}

