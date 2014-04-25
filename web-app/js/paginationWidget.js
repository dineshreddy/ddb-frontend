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

de.ddb.next.PaginationWidget = function() {
  this.init();
};

$.extend(de.ddb.next.PaginationWidget.prototype, {
  
  resultsOverallIndex: null,
  pagesOverallIndex: null,
  totalPages: null,
  numberOfResults: null,
  pageNavigators: null,
  pageInput: null,
  
  nextPage: null,
  prevPage: null,
  firstPage: null,
  lastPage: null,
  
  nextPageMobile: null,
  prevPageMobile: null,
  
  extraControls: null,
  goToPage: null,
  
  init: function(){
    this.resultsOverallIndex = $('.results-overall-index');
    this.pagesOverallIndex = $('.page-nav .pages-overall-index');
    this.pageNavigators = $('.page-nav-result');
    this.totalPages = $('.total-pages');
    this.nextPage = $('.page-nav .next-page');
    this.prevPage = $('.page-nav .prev-page');
    this.firstPage = $('.page-nav .first-page');
    this.lastPage = $('.page-nav .last-page');
    this.pageInput = $('.page-input');
    
    this.nextPageMobile = $('.page-nav-mob .next-page');
    this.prevPageMobile = $('.page-nav-mob .prev-page');
    
    this.extraControls = $('.extra-controls');
    this.goToPage = $('.go-to-page');
  },
  
  resetNavigationElements: function(JSONresponse){
    this.totalPages.html(JSONresponse.totalPages);
    
    //Next/Last-page button
    if (JSONresponse.paginationURL.nextPg) {
      this.nextPage.removeClass('off');
      this.lastPage.removeClass('off');
      
      this.nextPage.find('a').attr('href', JSONresponse.paginationURL.nextPg);
      this.lastPage.find('a').attr('href', JSONresponse.paginationURL.lastPg);
      
      //Mobile
      this.nextPageMobile.find('a').removeClass('off');
      this.nextPageMobile.find('.disabled-arrow').addClass('off');
      
      this.nextPageMobile.find('a').attr('href', JSONresponse.paginationURL.nextPg);
    }else{
      this.nextPage.addClass('off');
      this.lastPage.addClass('off');
      
      //Mobile
      this.nextPageMobile.find('a').addClass('off');
      this.nextPageMobile.find('.disabled-arrow').removeClass('off');
    }
    
    //Prev/First-page button
    if (JSONresponse.paginationURL.firstPg) {
      this.prevPage.removeClass('off');
      this.firstPage.removeClass('off');
      
      this.prevPage.find('a').attr('href', JSONresponse.paginationURL.prevPg);
      this.firstPage.find('a').attr('href', JSONresponse.paginationURL.firstPg);
      
      //Mobile
      this.prevPageMobile.find('a').removeClass('off');
      this.prevPageMobile.find('.disabled-arrow').addClass('off');
      
      this.prevPageMobile.find('a').attr('href', JSONresponse.paginationURL.prevPg);
    }else{
      this.prevPage.addClass('off');
      this.firstPage.addClass('off');
      
      //Mobile
      this.prevPageMobile.find('a').addClass('off');
      this.prevPageMobile.find('.disabled-arrow').removeClass('off');
    }
    
    //Setting pages
    if(JSONresponse.paginationURL.pages){
      $.each(this.pagesOverallIndex, function(){
        
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
      this.extraControls.removeClass('off');
    }else{
      this.extraControls.addClass('off');
    }
    
  },
  
  setNavigatorsClickHandler: function(clickHandler){
    this.pageNavigators.click(function(){
      clickHandler($(this));
      return false;
    });
  },
  
  setPageInputKeyupHandler: function(keyupHandler){
    var currObjInstance = this;
    this.pageInput.keyup(function(e){
      keyupHandler(e, this);
    });
    
    var enterButtonEvent = jQuery.Event("keyup");
    enterButtonEvent.keyCode = 13;
    this.goToPage.click(function(){
      currObjInstance.pageInput.trigger(enterButtonEvent);
    });
  }
  
  
});