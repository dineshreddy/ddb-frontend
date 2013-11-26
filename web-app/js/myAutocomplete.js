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
function monkeyPatchAutocomplete() {
    $.ui.autocomplete.prototype._renderItem = function( ul, item) {
        var re = new RegExp("^" + this.term) ;
        var t = item.label.replace(re,"<span style='font-weight:bold;'>" +
                this.term +
                "</span>");
        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append( "<a>" + t + "</a>" )
            .appendTo( ul );
    };
}
$(function() {
  monkeyPatchAutocomplete();
  $('input.query').autocomplete(
    {
      source : function(request, response) {
      $.ajax(
        {
            url : jsContextPath+"/apis/autocomplete/",
            dataType : "jsonp",
            data : {
              query : request.term
            },
            success : function(data) {
              response($.map(data, function(n,i) {
                //console.log(n)
                return {
                  label : n.substring(0,45),
                  value : n
                }
              }));
            }
         });
      },
      minLength : 2,
      open : function() {
        $(this).removeClass("ui-corner-all").addClass("ui-corner-top");
        $(this).autocomplete("widget").css('width',(parseInt($(this).outerWidth())-6)+'px');
      },
      select : function (a, b) {
            $(this).val(b.item.value);
            $(this).parents('form').submit();
      },
      close : function() {
        $(this).removeClass("ui-corner-top").addClass("ui-corner-all");
      }
    });
    $(window).resize(function() {
        var mainInputs = $('input[type="search"].query');
        mainInputs.each(function(index, input){
            var mainInput = $(input);
            var position = $(mainInput).offset();
            $(mainInput).autocomplete("widget").css('left', position.left+'px');
            $(mainInput).autocomplete("widget").css('top', (position.top+$(mainInput).outerHeight())+'px');
            $(mainInput).autocomplete("widget").css('width',(parseInt($(mainInput).outerWidth())-6)+'px');
            if($(window).width()<768 && $(mainInput).parents('#form-search').length<1){
                $(mainInput).autocomplete( "close" );
            }
        });
    });
});
