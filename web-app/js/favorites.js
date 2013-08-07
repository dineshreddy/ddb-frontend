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

    $('.page-filter select').change(function(){
      var url = jsContextPath + "/user/favorites/?rows="+this.value;
      var order = getParam("order");
      if ( order  ) {
        url = url + "&order="+order;
      }
      window.location.href=url;
      return false;
    });

    $('#checkall').checkAll('#slaves input:checkbox', {
      reportTo: function () {
        var prefix = this.prop('checked') ? 'un' : '';
        this.next().text(prefix + 'check all');
      }
    });	

    updateNavigationUrl();

    $('.page-input').removeClass('off');   
    $('.page-nonjs').addClass("off");

    $('.page-input').keyup(function(e){
      if(e.keyCode == 13) {
        if (/^[0-9]+$/.test(this.value)) {
          if (parseInt(this.value) <= 0) {
            this.value = 1;
          }
          else if (parseInt(this.value) > parseInt($('.result-pages-count').text())) {
            this.value = $('.result-pages-count').text();
          }
        }
        else {
          this.value = 1;
        }
        $('.page-input').attr('value', this.value);
        var paramsArray = new Array(new Array('offset', (this.value - 1) * getParam("rows")),new Array('rows', getParam("rows")),new Array('order', getParam("order")));
        var newUrl = addParamToUrl(paramsArray,null, paramsArray);
        window.location.href=newUrl;

      }
    });

    function addParamToUrl(arrayParamVal, path, urlString){
      console.log(urlString);
      var currentUrl = jsContextPath + "/user/favorites/";
      var queryParameters = {}, queryString = (urlString==null)?currentUrl:urlString,
          re = /([^&=]+)=([^&]*)/g, m;
      while (m = re.exec(queryString)) {
        var decodedKey = decodeURIComponent(m[1].replace(/\+/g,'%20'));
        if (queryParameters[decodedKey] == null) {
          queryParameters[decodedKey] = new Array();
        }
        queryParameters[decodeURIComponent(m[1].replace(/\+/g,'%20'))].push(decodeURIComponent(m[2].replace(/\+/g,'%20')));
      }
      $.each(arrayParamVal, function(key, value){
        queryParameters[value[0]] = value[1];
      });
      var tmp = jQuery.param(queryParameters, true);
      if (path == null) {
        return window.location.pathname+'?'+tmp;
      }
      else {
        return path+'?'+tmp;
      }
    }

    $('#favorites-remove').submit(function() {
      var selected = new Array();
      $('#slaves input:checked').each(function() {
        selected.push($(this).attr('value'));
      });
      $('#totalNrSelectedObjects').html(selected.length);
      $('#confirm-dialog').modal('show');
      $('#id-confirm').click(function() {
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
          url : jsContextPath + "/apis/favorites/_delete",
          data : JSON.stringify(body),
          dataType : "json",
          success : function(data) {
            //$('#msDeleteFavorites').modal();
            window.setTimeout('location.reload()', 500);
          }
        });
        $('#slaves input:checked').each(function() {
          selected.push($(this).attr('checked', false));
        });
        $('#confirm-dialog').modal('hide');
      });
      return false;
    });

    $('#deletedFavoritesBtnClose').click(function(){
      $('#msDeleteFavorites').modal('hide');
      window.setTimeout('location.reload()', 1000);
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
  if (parseInt(offset_nextPg[1])>parseInt(offset_endPg[1])){
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
