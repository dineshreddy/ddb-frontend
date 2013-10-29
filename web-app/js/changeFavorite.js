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

  $("#idFavorite").parent().click(function(event) {
    event.preventDefault();
    changeFavoriteState();
    return false;
    
  });
  
  function changeFavoriteState() {
    var jElemFavorite = $("#idFavorite");
    disableFavorite(jElemFavorite.parent());
    var vActn = jElemFavorite.attr("data-actn");
    var url = jsContextPath + "/apis/favorites/" + jElemFavorite.attr("data-itemid") + '/?reqType=ajax';
    var request = $.ajax({
      type: vActn,
      dataType: 'json',
      async: true,
      url: url + (vActn=="DELETE" ? "&reqActn=del" : "&reqActn=add"),
      complete: function(data) {
        if (vActn=="POST") {
          addToFavorites(data);
        } else if (vActn=="DELETE") {
          delFromFavorites(data);
        } else {       
        }
      }
    });
  }
  
  function addToFavorites(data) {
    var jElemFavorite = $("#idFavorite");
    switch (data.status) {
      case 200: case 201:
        // -- success
        //var JSONresponse = jQuery.parseJSON(data.responseText);
        jElemFavorite.attr("data-actn", "DELETE");
        $("#favorite-confirmation").modal("show");
        $.post(jsContextPath + "/apis/favorites/folders", function(folders) {
          if (folders.length > 1) {
            var itemId = jElemFavorite.attr("data-itemid");

            $.each(folders, function(index, folder) {
              if (!folder.isMainFolder) {
                // show select box with all folder names
                var selectEntry = "<option value=" + folder.folderId + ">" +
                  folder.title.charAt(0).toUpperCase() + folder.title.slice(1) + "</option>";

                $("#favorite-folders").append(selectEntry);
              }
            });
            $("#favoriteId").val(itemId);
            $("#addToFavoritesConfirm").click(function() {
              $("#favorite-confirmation").modal("hide");
              $.each($("#favorite-folders").val(), function(index, value) {
                $.post(jsContextPath + "/apis/favorites/folders/" + value + "/" + itemId);
              });
            });
          }
          else {
            window.setTimeout(function() {
              $("#favorite-confirmation").modal("hide");
            }, 1500);
          }
        });
        break;
      case 400:
        // -- bad request
        break;
      case 401:
        // -- handle unauthorized
        break;
      case 500:
        // -- internal error
        aler("Internal Server Error");
        break;
      default:
        // -- bad response
        alert("Bad response: status: " + data.status);
        break;
    }
  }
  
  function delFromFavorites(data) {
    var jElemFavorite = $("#idFavorite");
    switch (data.status) {
      case 200: case 204:
        // -- success
        //var JSONresponse = jQuery.parseJSON(data.responseText);
        jElemFavorite.attr("data-actn", "POST");
        jElemFavorite.parent().removeClass("favorite-selected");
        jElemFavorite.parent().addClass("favorite-add");
        jElemFavorite.parent().parent().attr('title', messages.ddbnext.Add_To_Favorites);
        break;
      case 401:
        // -- handle unauthorized
        break;
      case 404:
        // -- not found
        break;
      case 500:
        // -- internal error
        aler("Internal Server Error");
        break;
      default:
        // -- bad response
        alert("Bad response: status: " + data.status);
        break;
    }
  }

  /**
   * Disable a favorite button.
   *
   * @param link LINK element which handles the favorite event
   */
  function disableFavorite(link) {
    link.unbind("click");
    link.removeClass("favorite-add");
    link.addClass("favorite-selected");
    link.parent().attr('title', messages.ddbnext.favorites_already_saved);
  }
