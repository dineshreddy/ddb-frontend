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

$(document).ready(
    function() {
      var popupAnchor = $('.sendmail-link-popup-anchor');

      var content = $(document.createElement('input'));
      content.attr('type', 'email');
      content.attr('id', 'send-pdf');
      var title = $(document.createElement('a'));
      title.addClass('sendmail-link');
      var titleString = $(document.createElement('span'));
      var popupSpan = popupAnchor.children("span");
      if (popupSpan != null && popupSpan.length > 0) {
        titleString.html(popupSpan[0].innerHTML);
      }
      titleString.appendTo(title);
      var popupManager = new PopupManagerSendPdf();
      popupManager.registerPopupSendmail(popupAnchor[0], title, content, 162, -31);
    });

// Popup Manager --
PopupManagerSendPdf = function() {
  this.init();
}

$.extend(PopupManagerSendPdf.prototype, {

  init : function() {
  },

  registerPopupSendmail : function(anchorTag, title, content, offsetX, offsetY) {

    var closeTitle = messages.ddbnext.Close;
    var popupDialogWrapper = $(document.createElement('div'));
    var popupDialogTitle = $(document.createElement('div'));
    var popupDialogFooter = $(document.createElement('div'));
    var popupDialogCloseImage = $(document.createElement('div'));
    var popupDialogCloseButton = $(document.createElement('a'));
    var popupDialogSubmitButton = $(document.createElement('button'));
    var popupDialogContent = $(document.createElement('div'));

    popupDialogCloseImage.attr('title', closeTitle);

    popupDialogWrapper.addClass('popup-sendpdf-dialog-wrapper');
    popupDialogFooter.addClass('popup-dialog-footer');
    popupDialogCloseImage.addClass('popup-dialog-close-image');
    popupDialogCloseButton.addClass('popup-dialog-button');
    popupDialogSubmitButton.addClass('btn-padding');
    popupDialogTitle.addClass('popup-dialog-title bb');
    popupDialogContent.addClass('popup-dialog-content');
    popupDialogSubmitButton.html('Send');

    if (title) {
      popupDialogTitle.html(title);
    }
    if (content) {
      content.appendTo(popupDialogContent);
    }
    
    popupDialogCloseButton.attr('href', '#');

    popupDialogCloseImage.appendTo(popupDialogCloseButton);
    popupDialogTitle.appendTo(popupDialogWrapper);
    popupDialogContent.appendTo(popupDialogWrapper);
    popupDialogCloseButton.appendTo(popupDialogWrapper);
    popupDialogSubmitButton.appendTo(popupDialogWrapper);
    popupDialogFooter.appendTo(popupDialogWrapper);

    //popupDialogWrapper.css('margin-left', offsetX + 'px');
    popupDialogWrapper.css('margin-top', offsetY + 'px');

    popupDialogWrapper.insertAfter(anchorTag);
    popupDialogWrapper.hide();

    popupDialogCloseButton.click(function() {
      popupDialogWrapper.fadeOut('fast', function() {
      });
    });
//    popupDialogSubmitButtonclick(function() {
//    	var popupAnchor = $('.page-link-popup-anchor');
//    	var url = window.location.protocol + "//" + window.location.host + popupAnchor.attr('href');
//    	var email = $("#send-pdf").val()
//    	$.ajax({
//            url: url,
//            type: 'GET',
//            data: $(this).serialize(),
//            dataType: 'text',
//            success: function(data){
//            	alert "Success";
//            },
//            error: function(data){
//                alert('error');
//            }
//        });
//    });

    $(anchorTag).click(function(event) {
      if (event.which === 1) {
        event.preventDefault();
        popupDialogWrapper.fadeIn('fast', function() {
          popupDialogWrapper.find('input')[0].select();
        });
      }
    });

    $(document).mouseup(function(event) {
      if (popupDialogWrapper.has(event.target).length === 0) {
        popupDialogWrapper.fadeOut('fast', function() {
        });
      } else {
        popupDialogWrapper.find('input')[0].select();
      }
    });

  }
});