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
          if (jsPageName == "staticcontent") {
            var videoDivs = $(".jwplayer-video");
            for ( var i = 0; i < videoDivs.length; i++) {
              var videoDiv = videoDivs[i];
              var id = $(videoDiv).attr("id");
              var width = $(videoDiv).data("jwplayer-width");
              var height = $(videoDiv).data("jwplayer-height");
              var file = $(videoDiv).data("jwplayer-file");
              var image = $(videoDiv).data("jwplayer-image");

              initializeJwPlayer(id, file, image, width, height, function(event) {
              }, function(event) {
                var playerDiv = $("#mediaspace_wrapper");
                if (playerDiv.length === 0) {
                  playerDiv = $("#mediaspace");
                }
                if (playerDiv.length > 0) {
                  // console.log(playerDiv);
                  var errorDiv = $(document.createElement('div'));
                  errorDiv.addClass('static-content-viewer-error');
                  errorDiv.css('width', width + 'px');
                  errorDiv.css('height', height + 'px');
                  var errorHeaderDiv = $(document.createElement('div'));
                  errorHeaderDiv.text(messages.ddbnext.We_could_not_play_the_file);
                  errorHeaderDiv.addClass('viewer-error-header');
                  var errorBodyDiv = $(document.createElement('div'));
                  errorBodyDiv.text(messages.ddbnext.You_can_download_or_use_alternative);
                  errorBodyDiv.addClass('viewer-error-body');
                  errorDiv.append(errorHeaderDiv);
                  errorDiv.append(errorBodyDiv);
                  playerDiv.append(errorDiv);
                }
              });
            }
          }

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

          if (jsPageName == "item") {
            if (navigator.appName.indexOf("Internet Explorer") == -1) {
              if ($(window).width() > 530) {
                mediaQuery = true
              }
              else {
                mediaQuery = false
              }
            }
            $(function() {
              currentTab($("p.all"));
              $("div.all").show();
              $("p.divider").show();
              $(".tab").addClass('show-divider');
              $("div.tabs").addClass("fix");
              updatePreview($("div.all"));
              createGallery($(".gallery-all"));
              updateGalleryPagination(0, ".gallery-all li");
            });
            function updateGalleryPagination(pag, list) {
              var pos;
              var tot = $(list).size();
              var mediaQueryMatches = 1;
              if (navigator.appName.indexOf("Internet Explorer") == -1) {
                mediaQueryMatches = mediaQuery;
              }
              if (mediaQueryMatches) {
                // window width is at least 530px
                if (tot > 1) {
                  if (tot === 2) {
                    pos = "1-2";
                  }
                  else {
                    a = 1 + pag * 3;
                    b = 3 + pag * 3;
                    while (b > tot) {
                      a--;
                      b--;
                    }
                    pos = a + "-" + b;
                  }
                }
                else
                  pos = "1";
              }
              else {
                // window width is less than 530px
                if (tot > 1) {
                  a = 1 + pag * 2;
                  b = 2 + pag * 2;
                  while (b > tot) {
                    a--;
                    b--;
                  }
                  pos = a + "-" + b;
                }
                else
                  pos = "1";
              }
              $("p.gallery-pagination").text(pos + "/" + tot)
            }
            ;
            function currentTab(el) {
              $("p.tab").removeClass("current-tab")
              $(el).addClass("current-tab");
            }
            ;
            function updatePreview(gallerydiv) {
              var a = gallerydiv.find("ul").children('li').eq(0).children('a');
              var previewUri = $(a).attr("href");
              var previewTitle = $(a).attr("title");
              var previewHref = $(a).attr("data-content");
              var type = $(a).attr("data-type");
              var title = $(a).find("span").text();
              var title_text = $(a).find("span").text();
              var title_tooltip = $(a).find("span").text();
              var author = $(a).attr("data-author");
              var rights = $(a).attr("data-rights");

              // DDBNEXT-800 the title can use more than one line but should be
              // limited to 200 characters
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
                if ($("#jwplayer-container"))
                  $("#jwplayer-container").remove();
                if ($("#jwplayer-container_wrapper"))
                  $("#jwplayer-container_wrapper").remove();
                $(".previews").parent().addClass("off");
                $(".previews").each(function() {
                  if ($(this).attr("href") == previewHref) {
                    $(this).parent().removeClass("off");
                    return false;
                  }
                  else {
                    $(this).parent().appendTo($("#previews-list"));
                  }
                });
              }
              else {
                jwPlayerSetup(previewHref, previewUri);
              }
              $("div.binary-title span").text(title_text);
              $("div.binary-title").attr("title", title_tooltip);

              $("div.binary-author span").text(author);
              $("div.binary-author").attr("title", author);

              $("div.binary-rights span").text(rights);
              $("div.binary-rights").attr("title", rights);
            }
            ;
            function cutoffStringAtSpace(text, limit) {
              if (text != null && text.toString().length > limit) {
                return $.trim(text.toString()).substring(0, limit).split(" ").slice(0, -1)
                    .join(" ")
                    + "...";
              }
              return text;
            }
            function jwPlayerSetup(content, poster) {
              if ($("#binary-viewer").length === 0)
                return;
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
                    $(this).attr("unselectable", "on")
                  })
                }
              }, function(event) {
                if ($("#jwplayer-container"))
                  $("#jwplayer-container").remove();
                if ($("#jwplayer-container_wrapper"))
                  $("#jwplayer-container_wrapper").remove();
                if ($("#jwplayer-container").attr("type") == "application/x-shockwave-flash") {
                  $("binary-viewer-flash-upgrade").removeClass("off");
                }
                else {
                  $("div.binary-viewer-error").removeClass("off");
                }
              });
            }
            ;
            function createGallery(el) {
              var img = 3;
              var mediaQueryMatches = 1;
              if (navigator.appName.indexOf("Internet Explorer") == -1) {
                mediaQueryMatches = mediaQuery;
              }
              if (!mediaQueryMatches) {
                img = 2;
              }
              el.carouFredSel({
                circular : false,
                infinite : false,
                width : 445,
                align : false,
                height : 110,
                items : {
                  visible : img,
                  minimum : 1
                },
                scroll : {
                  items : img,
                  fx : "fade"
                },
                auto : false,
                prev : ".btn-prev",
                next : ".btn-next"
              });
              if (el.find('li').size() < 4) {
                $(".btn-next").addClass("disabled");
                $(".btn-next").attr("disabled", true);
              }
              else {
                $(".btn-next").attr("disabled", false);
              }
            }
            ;
            $(".btn-prev").click(function() {
              if (!$(this).hasClass("disabled")) {
                var currentTabPage = $(this).parent().find(".gallery-pagination").attr("data-pag");
                var prevPage = parseInt(currentTabPage) - 1;
                updateGalleryPagination(prevPage, $(this).parent().find(".gallery-tab li"));
                $(this).parent().find(".gallery-pagination").attr("data-pag", prevPage);
                $(this).addClass("disabled");
                setTimeout(function() {
                  $(this).removeClass("disabled");
                }, 500);
              }
            });
            $(".btn-next").click(function() {
              if (!$(this).hasClass("disabled")) {
                var currentTabPage = $(this).parent().find(".gallery-pagination").attr("data-pag");
                var nextPage = parseInt(currentTabPage) + 1;
                updateGalleryPagination(nextPage, $(this).parent().find(".gallery-tab li"));
                $(this).parent().find(".gallery-pagination").attr("data-pag", nextPage);
                $(this).addClass("disabled");
                setTimeout(function() {
                  $(this).removeClass("disabled");
                }, 500);
              }
            });
            function hideErrors() {
              $("div.binary-viewer-error").addClass("off");
              $("div.binary-viewer-flash-upgrade").addClass("off");
            }
            $("p.all").click(
                function() {
                  var tab = $("div.all");
                  currentTab(this);
                  $("div.scroller").hide();
                  tab.show();
                  if ($(".gallery-all").find('li').size() > 3) {
                    createGallery($(".gallery-all"));
                  }
                  updatePreview(tab);
                  updateGalleryPagination(tab.find(".gallery-pagination").attr("data-pag"),
                      ".gallery-all li");
                });
            $("p.images").click(
                function() {
                  var tab = $("div.images");
                  if (tab.find("li").size() === 0)
                    return false;
                  currentTab(this);
                  $("div.scroller").hide();
                  tab.show();
                  createGallery($(".gallery-images"));
                  updatePreview(tab);
                  updateGalleryPagination(tab.find(".gallery-pagination").attr("data-pag"),
                      ".gallery-images li");
                });
            $("p.videos").click(
                function() {
                  var tab = $("div.videos");
                  if (tab.find("li").size() === 0)
                    return false;
                  currentTab(this);
                  $("div.scroller").hide();
                  tab.show();
                  createGallery($(".gallery-videos"));
                  updatePreview(tab);
                  updateGalleryPagination(tab.find(".gallery-pagination").attr("data-pag"),
                      ".gallery-videos li");
                });
            $("p.audios").click(
                function() {
                  var tab = $("div.audios");
                  if (tab.find("li").size() === 0)
                    return false;
                  currentTab(this);
                  $("div.scroller").hide();
                  tab.show();
                  createGallery($(".gallery-audios"));
                  updatePreview(tab);
                  updateGalleryPagination(tab.find(".gallery-pagination").attr("data-pag"),
                      ".gallery-audios li");
                });
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
            $("a.group").click(function(e) {
              e.preventDefault();
              var previewUri = $(this).attr("href");
              var previewTitle = $(this).attr("title");
              var previewHref = $(this).attr("data-content");
              var type = $(this).attr("data-type");
              var title = $(this).find("span").text();
              hideErrors();
              if (type == "image") {
                if ($("#jwplayer-container"))
                  $("#jwplayer-container").remove();
                if ($("#jwplayer-container_wrapper"))
                  $("#jwplayer-container_wrapper").remove();
                $(".previews").parent().addClass("off");
                $(".previews").each(function() {
                  if ($(this).attr("href") == previewHref) {
                    $(this).parent().removeClass("off");
                    return false;
                  }
                  else {
                    $(this).parent().appendTo($("#previews-list"));
                  }
                });
              }
              else {
                jwPlayerSetup(previewHref, previewUri);
              }
              $("div.binary-title span").text(title);
              return false;
            });
          }
        });
