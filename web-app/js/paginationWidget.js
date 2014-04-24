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
  toalPages: null,
  numberOfResults: null,
  pageNavigators: null,
  
  nextPage: null,
  prevPage: null,
  firstPage: null,
  lastPage: null,
  
  nextPageMobile: null,
  prevPageMobile: null,
  
  extraControls: null,
  
  init: function(){
    this.resultsOverallIndex = $('.results-overall-index');
    this.pageNavigators = $('.page-nav-result');
    this.toalPages = $('.total-pages');
    this.nextPage = $('.page-nav .next-page');
    this.prevPage = $('.page-nav .prev-page');
    this.firstPage = $('.page-nav .first-page');
    this.lastPage = $('.page-nav .last-page');
    
    this.nextPage = $('.page-nav-mob .next-page');
    this.prevPage = $('.page-nav-mob .prev-page');
    
  },
  
  setPageNavigatorsClickHandler: function(clickHandler){
    this.pageNavigators.click(function(){
      clickHandler();
      return false;
    }
  }
  
});