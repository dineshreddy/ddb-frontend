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
$(document)
    .ready(
        function() {

          function initializeJwPlayer(divId, videoFile, previewImage, width, height,
              onReadyCallback, onErrorCallback) {
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

          if (jsPageName == "compare") {
            if (navigator.appName.indexOf("Internet Explorer") == -1) {
              if ($(window).width() > 530) {
                mediaQuery = true;
              } else {
                mediaQuery = false;
              }
            }
            $(function() {
              updatePreview($("div.all"));
            });

            function updatePreview(gallerydiv) {
              var a = gallerydiv.find("ul").children('li').eq(0).children('a');
              var previewUri = $(a).attr("href");
              var previewHref = $(a).attr("data-content");
              var type = $(a).attr("data-type");
              var title = $(a).find("span").text();
              var title_text = $(a).find("span").text();
              var title_tooltip = $(a).find("span").text();
              var author = $(a).attr("data-author");
              var rights = $(a).attr("data-rights");

              // Title limited to 200 characters
              title_text = cutoffStringAtSpace(title, 200);

              // The tooltip of the title should be limited to 270 characters
              title_tooltip = cutoffStringAtSpace(title, 270);

              // The text and the tooltip of the author should be limited to 270
              // characters
              author = cutoffStringAtSpace(author, 270);

              // The text and the tooltip of the rights should be limited to 270
              // characters
              rights = cutoffStringAtSpace(rights, 270);

              hideErrors();
              if (type == "image") {
                if ($("#jwplayer-container")) {
                  $("#jwplayer-container").remove();
                }
                if ($("#jwplayer-container_wrapper")) {
                  $("#jwplayer-container_wrapper").remove();
                }
                $(".previews").parent().addClass("off");
                $(".previews").each(function() {
                  if ($(this).attr("href") == previewHref) {
                    $(this).parent().removeClass("off");
                    return false;
                  } else {
                    $(this).parent().appendTo($("#previews-list"));
                  }
                });
              } else {
                jwPlayerSetup(previewHref, previewUri);
              }
              $("div.binary-title span").text(title_text);
              $("div.binary-title").attr("title", title_tooltip);

              $("div.binary-author span").text(author);
              $("div.binary-author").attr("title", author);

              $("div.binary-rights span").text(rights);
              $("div.binary-rights").attr("title", rights);
            };
            function cutoffStringAtSpace(text, limit) {
              if (text != null && text.toString().length > limit) {
                return $.trim(text.toString()).substring(0, limit).split(" ").slice(0, -1)
                    .join(" ")
                    + "...";
              }
              return text;
            }
            function jwPlayerSetup(content, poster) {
              if ($("#binary-viewer").length === 0) {
                return;
              }
              $(".previews").parent().addClass("off");
              $("#binary-viewer").append('<div id="jwplayer-container"></div>');
              var w = 445;
              var h = 320;
              var mediaQueryMatches = 1;
              if (navigator.appName.indexOf("Internet Explorer") == -1) {
                mediaQueryMatches = mediaQuery;
              }
              if (!mediaQueryMatches) {
                w = 260;
                h = 200;
              }

              initializeJwPlayer("jwplayer-container", content, poster, w, h, function(event) {
                if ($.browser.msie && this.getRenderingMode() === "html5") {
                  $("#binary-viewer").find("[id*='jwplayer']").each(function() {
                    $(this).attr("unselectable", "on");
                  });
                }
              }, function(event) {
                if ($("#jwplayer-container")) {
                  $("#jwplayer-container").remove();
                }
                if ($("#jwplayer-container_wrapper")) {
                  $("#jwplayer-container_wrapper").remove();
                }
                if ($("#jwplayer-container").attr("type") == "application/x-shockwave-flash") {
                  $("binary-viewer-flash-upgrade").removeClass("off");
                } else {
                  $("div.binary-viewer-error").removeClass("off");
                }
              });
            };
            function hideErrors() {
              $("div.binary-viewer-error").addClass("off");
              $("div.binary-viewer-flash-upgrade").addClass("off");
            }
            $(".previews")
                .click(
                    function(e) {
                      e.preventDefault();
                      $
                          .fancybox(
                              $(".previews"),
                              {
                                'padding' : 0,
                                'closeBtn' : false,
                                'overlayShow' : true,
                                'openEffect' : 'fade',
                                'closeEffect' : 'fade',
                                'prevEffect' : 'fade',
                                'nextEffect' : 'fade',
                                'tpl' : {
                                  wrap : '<div class="fancybox-wrap" tabIndex="-1"><div class="fancybox-skin"><div class="fancybox-toolbar"><span class="fancybox-toolbar-title">'
                                      + $("div.binary-title span").text()
                                      + '</span><span title="Close" class="fancybox-toolbar-close" onclick="$.fancybox.close();"></span></div><div class="fancybox-outer"><div class="fancybox-inner"><div class="fancybox-click-nav" onclick="$.fancybox.prev();"></div><div class="fancybox-click-nav" style="right: 0;" onclick="$.fancybox.next();"></div><div class="fancybox-pagination"><span></span></div></div></div></div></div>',
                                  prev : '<span title="Previous" class="fancybox-nav fancybox-prev" onclick="$.fancybox.prev();" onmouseover="$(\'.fancybox-pagination\').show();" onmouseout="$(\'.fancybox-pagination\').hide();"></span>',
                                  next : '<span title="Next" class="fancybox-nav fancybox-next" onclick="$.fancybox.next();" onmouseover="$(\'.fancybox-pagination\').show();" onmouseout="$(\'.fancybox-pagination\').hide();"></span>'
                                },
                                'afterLoad' : function() {
                                  var title = $(this.element).attr('data-caption');
                                  var position = $(this.element).attr('data-pos') + '/'
                                      + $("#previews-list li").size();
                                  $("span.fancybox-toolbar-title").text(title);
                                  $("div.fancybox-pagination span").text(position);
                                }
                              });
                      if ($("#previews-list li").size() === 1) {
                        $(".fancybox-pagination").addClass("off");
                      }
                      return false;
                    });
          }
        });
