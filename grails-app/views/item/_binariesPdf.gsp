<%--
Copyright (C) 2013 FIZ Karlsruhe
 
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
<div class="span6 slide-viewer item-detail">
  <div class="binary-viewer-container">
    <div id="binary-viewer">
      <ul id="previews-list">
        <g:set var="counter" value="${0}" />
        <g:each in="${binaryList}">
          <g:if test="${it.full.uri == '' && it.preview.uri == ''}">
            <g:set var="content" value="${it.thumbnail.uri}"/>
          </g:if>
          <g:elseif test="${it.full.uri == ''}">
            <g:set var="content" value="${it.preview.uri}"/>
          </g:elseif>
          <g:else>
            <g:set var="content" value="${it.full.uri}"/>
          </g:else>
          <g:if test="${it.preview.uri == ''}">
            <g:set var="viewerContent" value="${it.thumbnail.uri}"/>
          </g:if>
          <g:else>
            <g:set var="viewerContent" value="${it.preview.uri}"/>
          </g:else>
          <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
            <g:set var="counter" value="${counter + 1}" />
            <li>
              <a class="previews" href="${content}">
                <img src="${viewerContent}" alt="${(it.preview.title).encodeAsHTML()}" />
              </a>
            </li>
          </g:if>
        </g:each>
      </ul>
      <div class="binary-viewer-error off">
        <p class="error-header"><g:message code="ddbnext.We_could_not_play_the_file" /></p>
        <p>
          <g:message code="ddbnext.You_can_download_or_use_alternative" />
        </p>
      </div>
      <div class="binary-viewer-flash-upgrade off">
        <p class="error-header"><g:message code="ddbnext.BinaryViewer_FlashUpgrade_HeadingText" /></p>
        <p>
          <g:message code="ddbnext.BinaryViewer_FlashUpgrade_DownloadLocationHtml" />
        </p>
        <p class="error-header"><g:message code="ddbnext.We_could_not_play_the_file" /></p>
        <p>
          <g:message code="ddbnext.You_can_download_or_use_alternative" />
        </p>
      </div>
    </div>
  </div>

  <div class="binary-title">
    <span></span>
  </div>
  
  <div class="binary-author">
    <span></span>
  </div>

  <div class="binary-rights">
    <span></span>
  </div>

  <div class="tabs">
    <div>
      <p class="tab all">
        <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_All" 
                   args="${flashInformation.all}" 
                   default="ddbnext.BinaryViewer_MediaCountLabelFormat_All"/>
      </p>
    </div>
    <div class="scroller all pdf-scroller">
      <ul class="gallery-all">
        <g:each in="${binaryList}">
          <li>
            <div class="group">
              <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
                <g:set var="type" value="image"/>
              </g:if>
              <g:elseif test="${it.orig.uri.video != ''}">
                <g:set var="type" value="video"/>
              </g:elseif>
              <g:elseif test="${it.orig.uri.audio != ''}">
                <g:set var="type" value="audio"/>
              </g:elseif>
              <div class="thumbnail ${type}">
                <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
              </div>
              <span class="label off">
                <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
                  ${it.full.title}
                </g:if>
                <g:else>
                  ${it.orig.title}
                </g:else>
              </span>
            </div>
          </li>
        </g:each>
      </ul>
    </div>

    <div>
      <p class="tab images"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" args="${flashInformation.images}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" /></p>
    </div>
    <div class="scroller images pdf-scroller">
      <g:each in="${binaryList}">
        <g:if test="${it.full.uri != '' && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
          <g:set var="images" value="true"/>
        </g:if>
      </g:each>
      <g:if test="${images}">
        <ul class="gallery-images">
          <g:each in="${binaryList}">
            <g:if test="${it.full.uri != '' && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
              <li>
                <div class="group">
                  <div class="thumbnail image">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.preview.title}</span>
                </div>
              </li>
            </g:if>
          </g:each>
        </ul>
      </g:if>
    </div>

    <div>
      <p class="tab videos"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" args="${flashInformation.videos}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" /></p>
    </div>
    <div class="scroller videos pdf-scroller">
      <g:each in="${binaryList}">
        <g:if test="${it.orig.uri.video != '' }">
          <g:set var="videos" value="true"/>
        </g:if>
      </g:each>
      <g:if test="${videos}">
        <ul class="gallery-videos">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.video != '' }">
              <li>
                <div class="group">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </div>
              </li>
            </g:if>
          </g:each>
        </ul>
      </g:if>
    </div>

    <div>
      <p class="tab audios"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" args="${flashInformation.audios}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" /></p>
    </div>
    <div class="scroller audios pdf-scroller">
      <g:each in="${binaryList}">
        <g:if test="${it.orig.uri.audio != '' }">
          <g:set var="audios" value="true"/>
        </g:if>
      </g:each>
      <g:if test="${audios}">
        <ul class="gallery-audios">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.audio != '' }">
              <li>
                <div class="group">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </div>
              </li>
            </g:if>
          </g:each>
        </ul>
      </g:if>
    </div>
  </div>
</div>