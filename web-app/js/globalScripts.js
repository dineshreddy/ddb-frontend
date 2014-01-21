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

// Trim of the given text to the passed chars limit
$.cutoffStringAtSpace = function(text, limit) {
  if (text != null && text.toString().length > limit) {
    return $.trim(text.toString()).substring(0, limit).split(" ").slice(0, -1).join(" ") + "...";
  }
  return text;
}

// Initialization of the JWPlayer
$.initializeJwPlayer = function(divId, videoFile, previewImage, width, height, onReadyCallback, onErrorCallback) {
  jwplayer(divId).setup({
    'flashplayer' : jsContextPath + '/js/vendor/jwplayer-6.2.3115/jwplayer.flash.swf',
    'html5player' : jsContextPath + '/js/vendor/jwplayer-6.2.3115/jwplayer.html5.js',
    'modes' : [ {
      type : "html5",
      src : jsContextPath + "/js/vendor/jwplayer-6.2.3115/jwplayer.html5.js"
    }, {
      type : "flash",
      src : jsContextPath + "/js/vendor/jwplayer-6.2.3115/jwplayer.flash.swf"
    }, {
      type : "download"
    } ],
    'fallback' : true,
    'autostart' : false,
    'file' : videoFile,
    'skin' : jsContextPath + '/js/vendor/jwplayer-6.2.3115/skins/five.xml',
    'image' : previewImage,
    'controls' : true,
    'controlbar' : 'bottom',
    'stretching' : 'uniform',
    'width' : width,
    'height' : height,
    'primary' : 'html5',
    'startparam' : 'starttime',
    'events' : {
      onError : onErrorCallback,
      onReady : onReadyCallback
    }
  });
}

// Hiding of the errors in the binaries viewer 
$.hideErrors = function() {
  $("div.binary-viewer-error").addClass("off");
  $("div.binary-viewer-flash-upgrade").addClass("off");
}

$(document)
    .ready(
        function() {

          // Open all external links in a new window
          $(
              'a[href^="http"]:not([href^="http://localhost"],[href^="http://dev.escidoc.org"],[href^="https://www.deutsche-digitale-bibliothek.de"])')
              .attr('target', '_blank');

        });
