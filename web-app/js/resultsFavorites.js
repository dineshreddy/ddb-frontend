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
$(function() {
  if ((jsPageName === "person") || jsPageName === "searchinstitution") {
    checkFavorites();
  }
});
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
      $(".search-results-content .persist").each(function() {
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
    console.log(result);
    return result;
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