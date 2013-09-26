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
//IMPORTANT FOR MERGING: This is the main function that has to be called when we are in the saved searches page
$(function() {
  if (jsPageName == "savedsearches") {
    $('.page-input').removeClass('off');
    $('.page-nonjs').addClass("off");
    // workaround for ffox + ie click focus - prevents links that load dynamic
    // content to be focussed/active.
    $("a.noclickfocus").live('mouseup', function() {
      $(this).blur();
    });

    $("#sendSavedSearches").click(function(event) {
      event.preventDefault();
      $('#sendSavedSearchesModal').modal({remote: $(this).attr("href")});
    });

    $('.page-filter select').change(function() {
      var url = jsContextPath + "/user/savedsearches?rows=" + this.value;
      var order = getParam("order");
      if (order) {
        url += "&order=" + order;
      }
      window.location.href = url;
      return false;
    });

    $('#checkall').checkAll('#slaves input:checkbox', {
      reportTo: function () {
        var prefix = this.prop('checked') ? 'un' : '';
        this.next().text(prefix + 'check all');
      }
    });	

    $('.page-input').keyup(function(e) {
      if (e.keyCode == 13) {
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

        var paramsArray = new Array(new Array('offset', (this.value - 1) * getParamWithDefault("rows", 20)),
                                    new Array('rows', getParamWithDefault("rows", 20)),
                                    new Array('order', getParam("order")));

        window.location.href = addParamToUrl(jsContextPath + "/user/savedsearches/", paramsArray, null, paramsArray);
      }
    });

    $('#deleteSavedSearches').submit(function() {
      var selected = new Array();
      $('#slaves input:checked').each(function() {
        selected.push($(this).attr('value'));
      });
      $('#totalNrSelectedObjects').html(selected.length);
      $('#deleteSavedSearchesModal').modal('show');
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
          url : jsContextPath + "/apis/savedsearches/_delete",
          data : JSON.stringify(body),
          dataType : "json",
          success : function(data) {
            window.setTimeout('window.location.reload()', 500);
          }
        });
        $('#slaves input:checked').each(function() {
          selected.push($(this).attr('checked', false));
        });
        $('#deleteSavedSearchesModal').modal('hide');
      });
      return false;
    });
  }
});