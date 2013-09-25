/*
 * Copyright (C) 2013 FIZ Karlsruhe
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

/* We tell JS Hint, that $ and _ are not global.
 * See Also: http://www.jshint.com/docs/#config
 */
/* global $:false, _: false */

/* Even if jsContextPath is global, we load it first before any other JavaScripts.
 * We therefore tell JS Hint, jsContextPath is not global
 */
/* global jsContextPath: false */

(function() {
  'use strict';

  // TODO: remove `use strict` via automate script in the production mode.
  // SEE: http://scriptogr.am/micmath/post/should-you-use-strict-in-your-production-javascript

  // TODO: use other pattern for namespace in JS
  // SEE: http://enterprisejquery.com/2010/10/how-good-c-habits-can-encourage-bad-javascript-habits-part-1/
  var ddb = {

    Config: {
      ddbBackendUrl: '/apis/institutions'
    },

    // TODO: does not work => cache for all institution, including children and their descendants
    $all: $('li.institution-listitem'),

    institutionsByFirstChar: null,

    // find index[All| A | B |...| Z | 0-9] with no members after filtered by sectors.
    findNoMember: function(visible) {
      return _.reduce(ddb.institutionsByFirstChar, function(memo, array, key) {
        if (_.intersection(array, visible).length === 0) {
         memo.push(key);
      }
       return memo;
      }, []);
      
    },

    filterDescendants: function(institution, memory, selectedSector, parentList) {
      if (institution.children && institution.children.length > 0) {
        // when an institution has a least one child.
        _.reduce(institution.children, function(otherMemory, child) {
          if (_.contains(selectedSector, child.sector)) {
            otherMemory.push(child);
            // the institution is the parent.
            parentList.push(institution);
          }
          ddb.filterDescendants(child, otherMemory, selectedSector, parentList);
        return otherMemory;
        }, memory);
      }
    },

    findElements: function(list) {
      var idList = _.pluck(list, 'id');
      
      return $('li.institution-listitem').filter(function() {
        var institutionId = $(this).data('institution-id');
        return _.contains(idList, $(this).data('institution-id'));
      });
    },

    getInstitutionsByFirstChar: function(onFilterSelect, onIndexClick, onPageLoad) {
      if (ddb.institutionsByFirstChar === null) {
        $.getJSON(jsContextPath + ddb.Config.ddbBackendUrl, function(response) {
          ddb.institutionsByFirstChar = response.data;

          $('.filter').show();
          // call the callback, once data is loaded.
          onPageLoad();
          onIndexClick();
          onFilterSelect();
          window.onhashchange = ddb.onHashChange;
        })
        .error(function(jqXhr, textStatus, errorThrown) {
          /* when we fail to fetch the JSON via AJAX, then we do not activate the
          JS-feature.*/
        });
      }

    },

    onPageLoad: function() {
      var hash = window.location.hash.substring(1);
      ddb.styleIndex(hash);
      if (hash === '' || hash.toLowerCase() === 'all' || hash === 'list') {
        /* we check if the user return to the page using the web browser's back
         * button and if they performed the sector filters before.
         */

        if ($('.multiselect').is(':visible')) {
          var isChecked = $('.multiselect option:selected').filter(':selected').length;
        } else {
          var isChecked = $('.sector-facet input:checked').filter(':checked').length;
        }

        // apply the filter, if the filters is not empty.
        if (isChecked) {
          ddb.applyFilter();
        }
      } else {
        ddb.applyFilter();
      }
    },

    onFilterSelect: function() {
      $('input:checkbox').click(function() {
        ddb.applyFilter();
      });
    },

    /* Function Callback for the URI's hash change event. */
    onHashChange: function() {
      var hash = window.location.hash.substring(1);
      ddb.styleIndex(hash);
      ddb.applyFilter();
    },

    styleIndex: function(hash) {
      if (hash === '' || hash.toLowerCase() === 'all' || hash === 'list') {
        var $allHref = $('#first-letter-index a[href="#All"]');
        var $allLi = $allHref.parent();
        $allLi.addClass('active');
        $allHref.addClass('selected');
        //$allHref.css('color', '#a5003b');
      } else {
        var $aHref = $('#first-letter-index a[href="' + '#' + hash + '"]');
        var $li = $aHref.parent();

        if ($li.hasClass('disabled')) {
          //$('#no-match-message').css('display', 'block');
          $('#no-match-message').addClass('visible');
          return false;
        }
        // style the selected index.
        $li.addClass('active');
        $li.addClass('selected');
        // TODO: refactor this, a lot of duplicate code.
        // reset other indexes to the initial style.
        var $firstCharLinks = $('#first-letter-index a');
        var $otherLinks = $firstCharLinks.not($aHref);
        $otherLinks.parent().removeClass('active');
        $otherLinks.removeClass('selected');
        //$otherLinks.removeAttr('style');
      }
      
      return true;
    },

    applyFilter: function() {
      var institutionList = ddb.getInstitutionAsList();
      var sectors = ddb.getSelectedSectors();
      var firstLetter = ddb.getFirstLetter();
      ddb.filter(institutionList, sectors, firstLetter);
    },

    getInstitutionAsList: function() {
      if (ddb.institutionList) {
        return ddb.institutionList;
      } else {
        ddb.institutionList = _.chain(ddb.institutionsByFirstChar)
          .values()
          .flatten()
          .value();
      }
      return ddb.institutionList;
    },

    getFirstLetter: function() {
      var result = '';
      var hashValue = $('#first-letter-index')
                        .find('li.active')
                        .find('a')
                        .attr('href');
      
      if(hashValue) {
        var hash = hashValue.substring(1).toLowerCase();
        if (hash !== '' && hash !== 'all' && hash !== 'list') {
          result = hash;
        }
      }
      
      result = result.toUpperCase();

      return result;
    },

    /*
    * get an array of selected sectors, for example: [sec_1, sec_3]
    */
    getSelectedSectors: function() {

      /*
       * Now we have two sector widgets. Based on the screen resolution,
       * we show either the checkboxes or multiselect.
       *
       * Depends on which widget is visible, we get the selected values.
       */
      if ($('.multiselect').is(':visible')) {
        var allSelectedSectors = $('.multiselect option:selected');
      } else {
        var allSelectedSectors = $('.sector-facet input:checked');
      }
      
      return _.reduce(allSelectedSectors, function(sectors, el) {
        sectors.push($(el).val());
        return sectors;
      }, []);
    },

    filter: function(institutionList, sectors, firstLetter) {
      // reset the view to empty.
      var $listItems = $('li.institution-listitem');
      //$listItems.css('display', 'none');
      $listItems.addClass('off');
      $listItems.removeClass('highlight');

      var parentList = [];

      if (sectors.length > 0 && firstLetter === '') {

        // when at least one sector selected _and_ no first letter filter.
        // e.g. sector = ['Media'], index = All
        var filteredBySector = ddb.filterBySectors(institutionList, sectors, parentList);
        var visible = _.union(_.uniq(parentList), filteredBySector);

        var hasNoMember = ddb.findNoMember(visible);
        ddb.showResult(_.union(parentList, filteredBySector), filteredBySector);
        ddb.updateIndex(hasNoMember);
      } else if (sectors.length > 0 && firstLetter !== '') {
        /*
        when at least one sector selected _and_ one of the first letter is
        selected, e.g.,  sector = ['Library', 'Media'], index = 'B'
        */
        // In this case, we don't need a parent list. TODO: refactor

        /*
        1. we collect all root institutions start with the selected firstLetter,
          for example 'W', including their children. The children do *not* have
          to start with the selected first letter.

        2. we start to apply the sector filter, for example [Library] to all
          institutions(roots and their children) collected from the first step.
        */

        var filteredByFirstLetter = ddb.institutionsByFirstChar[firstLetter];
        var filteredBySector = _.reduce(filteredByFirstLetter, function(memory, institution) {
          // assert
          if (institution.firstChar !== firstLetter) {
            // do nothing.
          } else {
            if (_.contains(sectors, institution.sector)) {
              memory.push(institution);
            }

            ddb.filterDescendants(institution, memory, sectors, parentList);
          }

          return memory;
        }, []);

        parentList = _.filter(parentList, function(parent) {
          return parent.firstChar === firstLetter;
        });

        var visible = _.union(parentList, filteredBySector);
        ddb.showResult(visible, filteredBySector);

        // find all root institutions filtered by sectors.
        // get the first letter, e.g., only As and Ls
        // show only A and L in Index.
        var filtered = ddb.filterBySectors(institutionList, sectors, parentList);
        var hasNoMember = ddb.findNoMember(_.union(_.uniq(parentList), filtered));
        ddb.updateIndex(hasNoMember);
      } else if (sectors.length === 0 && firstLetter !== '') {
        /*
        When no sector selected _and_ one of the first letter is selected.
        e.g. sector = [], index = 'C'
        */
        ddb.showByFirstLetter(firstLetter);
      } else {
        // the last case: sectors.length === 0 && firstLetter === ''.
        // when no sector is selected _and_ no first letter filter.
        // e.g. sector = [], index = All
        $('#institution-list')
          .empty()
          .html(ddb.$institutionList.html());

        var $currentIndex = $('#first-letter-index');
        $currentIndex.html(ddb.$index.html());
        
        ddb.styleIndex('All');
      }
      
    },

    filterBySectors: function(institutionList, sectors, parentList) {
      var reduced = _.reduce(institutionList, function(memory, institution) {
        if (_.contains(sectors, institution.sector)) {
          memory.push(institution);
        }
        ddb.filterDescendants(institution, memory, sectors, parentList);
        
        return memory;
      }, []);
      
      return reduced
    },

    showAll: function() {
      //$('#no-match-message').css('display', 'none');
      $('#no-match-message').addClass('off');
      //$('li.institution-listitem').css('display', '');
      $('li.institution-listitem').removeClass('off');
    },

    updateIndex: function(hasNoMember) {
      if (hasNoMember) {
        // enable all index. It means visually that the index all not grey.
        $('#first-letter-index li').removeClass('disabled');

        // update index view, i.e., A..Z
        _.each(hasNoMember, function(letter) {
          var $aHref = $('#first-letter-index a[href="' + '#' + letter + '"]');
          $aHref.parent().addClass('disabled');
          $aHref.click(function(e) {
            e.preventDefault();
          });
        });
      }
      
    },

    // visible institutions are filtered institutions and their descendants.
    showResult: function(visibleInstitution, filteredBySector) {
      var $msg = $('#no-match-message');

      // view manipulation
      if (visibleInstitution.length) {
        // hide the 'no result' message
        //$msg.css('display', 'none');
        $msg.addClass('off');

        ddb.findElements(filteredBySector).addClass('highlight');
        var $visible = ddb.findElements(visibleInstitution);
        //$visible.css('display', '');
        $visible.removeClass('off');
      } else {
        //$msg.css('display', 'block');
        $msg.removeClass('off');
        $msg.addClass('visible');
      }
      
    },

    // TODO: we should *not* this extra function. We can reuse the logic in
    // if (sectors.length > 0 && firstLetter !== '') {...} with sectors empty
    showByFirstLetter: function(firstLetter) {
      // get all institution start with the letter `firstLetter`
      var idList = _.pluck(ddb.institutionsByFirstChar[firstLetter], 'id');

      // find all institutions match idList
      var $listItems = $('li.institution-listitem');
      
      ddb.filteredEl = $listItems.filter(function() {
        return _.contains(idList, $(this).data('institution-id'));
      });
      
      // find all first level institutions which are not start with
      // firstLetter
      var restKeys = _.chain(ddb.institutionsByFirstChar)
        .keys()
        .reject(function(key) { return key === firstLetter; })
        .value();

      // get all values from restKeys
      var restIdList = _.chain(ddb.institutionsByFirstChar)
        .filter(function(val, key) { return _.contains(restKeys, key); })
        .flatten()
        .pluck('id')
        .value();

      // collect the HTML elements that match id in the restIdList
      ddb.restEl = $listItems.filter(function() {
        return _.contains(restIdList, $(this).data('institution-id'));
      });

      ddb.showAll();
      //ddb.restEl.css('display', 'none');
      ddb.restEl.addClass('off');
      if (idList.length === 0) {
        var $msg = $('#no-match-message');
        //$msg.css('display', 'block');
        $msg.addClass('visible');
      }
      
      // update the indext with cache
      var $currentIndex = $('#first-letter-index');
      $currentIndex.html(ddb.$index.html());

      ddb.onIndexClick();

      // style the selected index.
      var $aHref = $('#first-letter-index a[href="' + '#' + firstLetter + '"]');
      $aHref.parent().addClass('active');
      // TODO: replace this with a class.
      //$aHref.css('color', '#a5003b');
      $aHref.addClass('selected');
      
    },

    onIndexClick: function() {
      // we catch the click event on index, does *not* when the user goes directly
      // to a page with #{first-character}, for example: //institutions#A
      var $firstCharLinks = $('#first-letter-index a');
      $firstCharLinks.click(function(event) {
        event.preventDefault();

        var $this = $(this);
        var $li = $this.parent();

        if ($li.hasClass('disabled')) {
          return false;
        }

        // style the selected index.
        $li.addClass('active');
        $this.addClass('selected');
        // TODO: replace this with a class.
        //$this.css('color', '#a5003b');

        // reset other indexes to the initial style.
        var $otherLinks = $firstCharLinks.not(this);
        $otherLinks.parent().removeClass('active');
        //$otherLinks.removeAttr('style');
        $otherLinks.removeClass('selected');

        if (history.pushState) {
          history.pushState({}, '', $this.attr('href'));
          // TODO for android 2.3.3 we have to pass the clicked first letter.
          ddb.applyFilter();
        }
        else {
          // TODO: test on IE8,9
          window.location.hash = this.hash;
          //window.location.reload(false);
        }

        return false;
      });
    }
  };


  $(function() {
    var institutionList = $('#institution-list');

    $('.multiselect').multiselect({
      buttonClass: 'btn btn-small',
      buttonWidth: 'auto',
      maxHeight: false,
      field_NoneSelected: messages.ddbnext.None_Selected,
      buttonText: function(options) {
        if (options.length === 0) {
          var textNode = $(document.createElement('span')).html(this.field_NoneSelected);
          textNode.append($(document.createElement('b')).addClass('caret'));
          return textNode;
        } else if (options.length > 4) {
          return options.length + ' selected <b class="caret"></b>';
        } else {
          var selected = '';
          options.each(function() {
            selected += $(this).text() + ', ';
          });
          return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
          }
        }
    });

    // Only execute the script when the user is in the institution list page.
    if (institutionList.length) {

     // When the User Agent enables JS, shows the `filter by sector` Check Boxes.
      ddb.$index = $('#first-letter-index').clone(true, true);
      ddb.$institutionList = institutionList.clone();
      ddb.getInstitutionsByFirstChar(ddb.onFilterSelect, ddb.onIndexClick, ddb.onPageLoad);
    }
    
  });

}());
