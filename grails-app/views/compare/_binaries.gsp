<%--
Copyright (C) 2014 FIZ Karlsruhe
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<div class="row">
  <div class="span6 slide-viewer item-detail ${position}">
    <div class="binary-viewer-container">
      <div id="binary-viewer" <g:if test="${flashInformation.images[0] > 0 || !binaryList}">class="img-binary"</g:if>>
        <ul class="previews-list">
          <g:set var="counter" value="${0}" />
          <g:if test="${binaryList}">
            <g:each in="${binaryList}">
              <g:if test="${it.full.uri.isEmpty() && it.preview.uri.isEmpty()}">
                <g:set var="content" value="${it.thumbnail.uri}"/>
              </g:if>
              <g:elseif test="${it.full.uri.isEmpty()}">
                <g:set var="content" value="${it.preview.uri}"/>
              </g:elseif>
              <g:else>
                <g:set var="content" value="${it.full.uri}"/>
              </g:else>
              <g:if test="${it.preview.uri.isEmpty()}">
                <g:set var="viewerContent" value="${it.thumbnail.uri}"/>
              </g:if>
              <g:else>
                <g:set var="viewerContent" value="${it.preview.uri}"/>
              </g:else>
              <g:if test="${it.orig.uri.video.isEmpty() && it.orig.uri.audio.isEmpty()}">
                <g:set var="counter" value="${counter + 1}" />
                <li>
                  <g:if test="${it.full.uri.isEmpty()}">
                    <g:if test="${!originUrl.isEmpty()}">
                      <div class="viewer-icon"> 
                        <a target="_blank" class="show-origin" href="${originUrl}" title="<g:message encodeAs="html" code="ddbnext.stat_008" />"></a>
                      </div> 
                    </g:if>
                    <a target="_blank" class="no-previews no-external-link-icon" href="${originUrl}" title="<g:message encodeAs="html" code="ddbnext.stat_008" />">
                      <img src="${viewerContent}" alt="${it.preview.title}" />
                    </a>
                  </g:if>
                  <g:else>
                    <div class="viewer-icon">
                      <a class="<g:if test="${it.orig.uri.pdf.isEmpty()}">show-lightbox</g:if><g:else>show-pdf</g:else>" data-caption="${it.preview.title}" data-pos="${counter}" href="${content}" <g:if test="${it.orig.uri.pdf != ''}">target="_blank"</g:if>></a>
                    </div>
                    <a class="<g:if test="${it.orig.uri.pdf.isEmpty()}">previews</g:if><g:else>pdf-previews</g:else>" data-caption="${it.preview.title}" data-pos="${g.message(code: 'ddbnext.BinaryViewer_ImageCount', args: [counter])}" href="${content}" <g:if test="${it.orig.uri.pdf != ''}">target="_blank"</g:if>>
                      <img src="${viewerContent}" alt="${it.preview.title}" />
                    </a>
                  </g:else>
                </li>
              </g:if>
            </g:each>
          </g:if>
          <g:else>
            <li>
              <g:if test="${item.media}">
                <g:set var="mediatype" value="${item.media}"/>
              </g:if>
              <g:else>
                <g:set var="mediatype" value="image"/>
              </g:else>
              <div class="viewer-icon">
                <a target="_blank" class="show-origin" href="${originUrl}" title="<g:message encodeAs="html" code="ddbnext.stat_008" />"></a>
              </div>
              <a target="_blank" class="no-external-link-icon" href="${originUrl}" title="<g:message encodeAs="html" code="ddbnext.stat_008" />">
                <img src="${request.getContextPath() + '/images/placeholder/' + mediatype + '.png'}" alt="${itemTitle}" class="viewer-placeholder"/>
              </a>
            </li>
          </g:else>
        </ul>
        <div class="binary-viewer-error off">
          <p class="error-header"><g:message encodeAs="html" code="ddbnext.We_could_not_play_the_file" /></p>
          <p>
            <g:message encodeAs="html" code="ddbnext.You_can_download_or_use_alternative" />
          </p>
        </div>
        <div class="binary-viewer-flash-upgrade off">
          <p class="error-header"><g:message encodeAs="html" code="ddbnext.BinaryViewer_FlashUpgrade_HeadingText" /></p>
          <p>
            <g:message encodeAs="none" code="ddbcommon.BinaryViewer_FlashUpgrade_DownloadLocationHtml" />
          </p>
          <p class="error-header"><g:message encodeAs="html" code="ddbnext.We_could_not_play_the_file" /></p>
          <p>
            <g:message encodeAs="html" code="ddbnext.You_can_download_or_use_alternative" />
          </p>
        </div>
      </div>
    </div>
    <div class="binary-title">
      <span>${itemTitle}</span>
    </div>
    <div class="binary-author">
      <span></span>
    </div>
    <div class="binary-rights">
      <span></span>
    </div>
    <div class="tabs off">
      <div role="tablist">
        <p class="tab images" role="tab"><g:message encodeAs="html" code="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" args="${flashInformation.images}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" /></p>
      </div>
      <div class="scroller images ${position}" role="tabpanel">
        <ul class="gallery-images gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.full.uri == ''}">
              <g:set var="content" value="${it.preview.uri}"/>
            </g:if>
            <g:else>
              <g:set var="content" value="${it.full.uri}"/>
            </g:else>
            <g:if test="${(it.full.uri != '' || it.preview.uri != '') && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
              <li>
                <a class="group" href="${it.preview.uri}" data-content="${content}" data-type="image" data-author="${it.preview.author}" data-rights="${it.preview.rights}" title="${(it.preview.title).encodeAsHTML()}">
                  <div class="thumbnail image">
                    <img src="${it.thumbnail.uri}" alt="${it.thumbnail.title}" />
                  </div>
                  <span class="label off">${it.preview.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
      </div>
      <div role="tablist">
        <p class="tab videos" role="tab"><g:message encodeAs="html" code="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" args="${flashInformation.videos}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" /></p>
      </div>
      <div class="scroller videos ${position}" role="tabpanel">
        <ul class="gallery-videos gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.video != '' }">
              <li>
                <a class="group"
                   <g:if test="${it.preview.uri == ''}">
                     href="../images/placeholder/video.png"
                   </g:if>
                   <g:else>
                     href="${it.preview.uri}"
                   </g:else>  
                   data-content="${it.orig.uri.video}"  data-author="${it.orig.author}" data-rights="${it.orig.rights}" data-type="video" title="${it.orig.title}">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${it.thumbnail.title}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
        <p class="gallery-pagination" data-pag="0"></p>
      </div>
      <div role="tablist">
        <p class="tab audios" role="tab"><g:message encodeAs="html" code="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" args="${flashInformation.audios}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" /></p>
      </div>
      <div class="scroller audios ${position}" role="tabpanel">
        <ul class="gallery-audios gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.audio != '' }">
              <li>
                <a class="group"
                   <g:if test="${it.preview.uri == ''}">
                     href="../images/placeholder/audio.png"
                   </g:if>
                   <g:else>
                     href="${it.preview.uri}"
                   </g:else>
                   data-content="${it.orig.uri.audio}" data-author="${it.orig.author}" data-rights="${it.orig.rights}" data-type="audio" title="${it.orig.title}">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${it.thumbnail.title}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
        <p class="gallery-pagination" data-pag="0"></p>
      </div>
    </div>
  </div>
</div>
