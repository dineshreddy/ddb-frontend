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
        }
        else if (vActn=="DELETE") {
          delFromFavorites(data);
        }
        else {
          alert("Method not found...");
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
  }
