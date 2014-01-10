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
$(document).ready(
    function() {

      if (jsPageName == "entity") {

        var defaultRowCount = 10;

        var allRowCount = 0;

        var offset = 0;

        $('#normdata-involved-checkbox').bind('click', function() {
          updateRoleDivs();
        });

        $('#normdata-subject-checkbox').bind('click', function() {
          updateRoleDivs();          
        });        
        
        function updateRoleDivs() {
          if ($('#normdata-involved-checkbox').is(":checked")) {
            $('#search-involved').hide();
            $('#search-involved-normdata').show();
          } else {
            $('#search-involved').show();
            $('#search-involved-normdata').hide();
          }
          
          if ($('#normdata-subject-checkbox').is(":checked")) {
            $('#search-subject').hide();
            $('#search-subject-normdata').show();
          } else {
            $('#search-subject').show();
            $('#search-subject-normdata').hide();
          }
        }
        

        function getNewSearchResults(query, offset, rows) {
          var request = $.ajax({
            type : 'GET',
            dataType : 'json',
            async : true,
            url : jsContextPath + '/entity/ajax/searchresults?query=' + query + '&offset=' + offset
                + '&rows=' + rows,
            complete : function(data) {
              var jsonResponse = $.parseJSON(data.responseText);
              var items = $.parseHTML(jsonResponse.html);
              
              //Adds the items from the search to the carousel. Doing this one by one to avoid problems with the carousel.
              $.each(items, function(index, value) {
                if (value.tagName == 'DIV') {
                  $("#items").triggerHandler("insertItem", [ value, "end", true ]);
                }
              });

              allRowCount = jsonResponse.resultCount;
            }
          });

        }

        function getUrlParam(name) {
          name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
          var regexS = "[\\?&]" + name + "=([^&#]*)";
          var regex = new RegExp(regexS);
          var results = regex.exec(window.location.hash);

          if (results == null) {
            results = regex.exec(window.location.search);
          }

          if (results == null) {
            return "";
          } else {
            return decodeURIComponent(results[1].replace(/\+/g, " "));
          }
        }

        function initPage() {
          initCarousel();

          var query = $("#entity-title").html();
          var entityid = $("#entity-id").attr("data-entityid");

          var History = window.History;
          var urlParameters = "?query=" + query + "&offset=" + offset + "&rows=" + defaultRowCount;
          History.pushState("", document.title, decodeURI(urlParameters));

          // Initialize Search results
          getNewSearchResults(query, 0, defaultRowCount);
          
          updateRoleDivs();
        }

        function initCarousel() {
          var carouselItems = $("#items");

          $('div.carousel').show();

          if ($(".item .caption").length > 0) {
            $(".item .caption").dotdotdot({});
          }

          $("#next").click(
              function() {
                carouselItems.trigger("next", 1);

                var currentLoadItems = $(".preview-item");

                var currentVisibleItems = carouselItems.triggerHandler("currentVisible");
                var numberOfVisibleItems = currentVisibleItems.length;

                var currentPosition = carouselItems.triggerHandler("currentPosition");
                var nextVisbleItem = currentPosition + numberOfVisibleItems;

                // console.log( "The carousel is at number " + currentPosition +
                // " of " + currentLoadItems.length + "items");

                if ((nextVisbleItem > (currentLoadItems.length - 1))
                    && (currentLoadItems.length < allRowCount)) {
                  var query = $("#entity-title").html();
                  var History = window.History;
                  var urlParameters = "?query=" + query + "&offset=" + currentLoadItems.length
                      + "&rows=" + defaultRowCount;
                  History.pushState("", document.title, decodeURI(urlParameters));

                  // Initialize Search results
                  getNewSearchResults(query, currentLoadItems.length, defaultRowCount);
                }
              });

          $("#previous").click(function() {
            carouselItems.trigger("prev", 1);
          });

          if (carouselItems.length) {
            carouselItems.carouFredSel({
              infinite : false,
              auto : false,
              scroll : 1,
              prev : {
                button : "#previous"
              },
              next : {
                button : "#next"
              }
            });
          }
        }

        initPage();
      }
    });