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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the search results page
$(function() {

  if (jsPageName == "favorites") {
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function () { $(this).blur(); });
    $("#sendbookmarks").click(function(event) {
      event.preventDefault()
      $('#favoritesModal').modal({remote: $(this).attr("href")})
    });

    // Fix for back-button problem with the searchfield: DDBNEXT-389
    if($.browser.msie){
      var queryCache = $("#querycache");
      var queryString = "";
      if(queryCache.length > 0){
        queryString = queryCache.val();
      }
      $("#form-search-header .query").val(queryString);
    }

    $('#checkall').checkAll('#slaves input:checkbox', {
      reportTo: function () {
        var prefix = this.prop('checked') ? 'un' : '';
        this.next().text(prefix + 'check all');
      }
    });	
    updateNavigationUrl();
    $('#favorites-remove').submit(function() {
      var selected = new Array();
      $('#slaves input:checked').each(function() {
        selected.push($(this).attr('value'));
      });
      var body = {
        ids : selected
      }
      jQuery.ajax({
        type : 'POST',
        contentType : "application/json; charset=utf-8",
        traditional : true,
        url : "/ddb-next/apis/favorites/_delete",
        data : JSON.stringify(body),
        dataType : "json",
        success : function(data) {
          $('#msDeleteFavorites').modal();
          window.setTimeout('location.reload()', 1000);
        }
      });
      return false;
    });
    

    $(document).on("click", "#btnSubmit", function(){ 
      console.log("TODO Email Validation HERE");
    }); 

  }

});



function updateNavigationUrl(){
  $.urlParam = function(name){
    var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return results[1] || 0;
  }
  var offset = getParam( 'offset' ); 
  if ((offset == null) || (offset<1)){
    $(".page-nav .prev-page").addClass("off");
    $(".page-nav .first-page").addClass("off");
  }

  var offset_endPg = $(".last-page").find('a').attr("href").match(/offset=([0-9]+)/);
  var offset_nextPg = $(".next-page").find('a').attr("href").match(/offset=([0-9]+)/);
  if (parseInt(offset_nextPg[1])>=parseInt(offset_endPg[1])){
    $(".page-nav .next-page").addClass("off");
    $(".page-nav .last-page").addClass("off");
  }
}

function getParam( name )
{
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var tmpURL = window.location.href;
  var results = regex.exec( tmpURL );
  if( results == null )
    return "";
  else
    return results[1];
}
