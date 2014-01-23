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
      <div class="binary-viewer">
        <ul class="previews-list">
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
                <a class="previews" data-caption="${(it.preview.title).encodeAsHTML()}" data-pos="${counter}" href="${content}">
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
    <div class="tabs off">
      <div role="tablist">
        <p class="tab all" role="tab">
          <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_All" 
                     args="${flashInformation.all}" 
                     default="ddbnext.BinaryViewer_MediaCountLabelFormat_All"/>
        </p>
      </div>
      <div class="scroller all ${position}" role="tabpanel">
        <ul class="gallery-all gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.full.uri == ''}">
              <g:set var="content" value="${it.preview.uri}"/>
            </g:if>
            <g:else>
              <g:set var="content" value="${it.full.uri}"/>
            </g:else>
            <li>
              <a class="group" 
                <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
                  href="${it.preview.uri}"
                  data-content="${content}"
                  data-type="image"
                  data-author="${it.preview.author}"
                  data-rights="${it.preview.rights}"
                  title="${(it.full.title).encodeAsHTML()}"
                  <g:set var="type" value="image"/>
                </g:if>
                <g:elseif test="${it.orig.uri.video != ''}">
                  <g:if test="${it.preview.uri == ''}">
                    href="../images/bg/videoPoster.png"
                  </g:if>
                  <g:else>
                    href="${it.preview.uri}"
                  </g:else>
                  data-content="${it.orig.uri.video}"
                  data-type="video"
                  data-author="${it.orig.author}"
                  data-rights="${it.orig.rights}"
                  <g:set var="type" value="video"/>
                </g:elseif>
                <g:elseif test="${it.orig.uri.audio != ''}">
                  <g:if test="${it.preview.uri == ''}">
                    href="../images/bg/audioPoster.png"
                  </g:if>
                  <g:else>
                    href="${it.preview.uri}"
                  </g:else>
                  data-content="${it.orig.uri.audio}"
                  data-type="audio"
                  data-author="${it.orig.author}"
                  data-rights="${it.orig.rights}"
                  title="${(it.orig.title).encodeAsHTML()}"
                  <g:set var="type" value="audio"/>
                </g:elseif>>
                <div class="thumbnail ${type}">
                  <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                </div>
                <span class="label off">
                  <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
	                  <%-- Implement a fallback for showing the image title --%>
	                  <g:if test="${it?.full?.title != ''}">
	                  	${it?.full?.title}
	                  </g:if>
	                  <g:elseif test="${it?.preview?.title != ''}">
	                  	${it?.preview?.title}
	                  </g:elseif>
	                  <g:elseif test="${it?.thumbnail?.title != ''}">
	                  	${it?.thumbnail?.title}
	                  </g:elseif>
                  </g:if>
                  <g:else>
                    ${it.orig.title}
                  </g:else>
                </span>
              </a>
            </li>
          </g:each>
        </ul>
        <button class="btn-prev">
          <g:message code="ddbnext.Previous_Label" />
          <span class="opaque"></span>
        </button>
        <button class="btn-next">
          <g:message code="ddbnext.Next_Label" />
          <span class="opaque"></span>
        </button>
      </div>
      <div role="tablist">
        <p class="tab images" role="tab"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" args="${flashInformation.images}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" /></p>
      </div>
      <div class="scroller images" role="tabpanel">
        <ul class="gallery-images gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.full.uri == ''}">
              <g:set var="content" value="${it.preview.uri}"/>
            </g:if>
            <g:else>
              <g:set var="content" value="${it.full.uri}"/>
            </g:else>
            <g:if test="${it.full.uri != '' && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
              <li>
                <a class="group" href="${it.preview.uri}" data-content="${content}" data-type="image" data-author="${it.preview.author}" data-rights="${it.preview.rights}" title="${(it.preview.title).encodeAsHTML()}">
                  <div class="thumbnail image">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.preview.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
        <button class="btn-prev">
          <g:message code="ddbnext.Previous_Label" />
          <span class="opaque"></span>
        </button>
        <button class="btn-next">
          <g:message code="ddbnext.Next_Label" />
          <span class="opaque"></span>
        </button>
        <p class="gallery-pagination" data-pag="0"></p>
      </div>
      <div role="tablist">
        <p class="tab videos" role="tab"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" args="${flashInformation.videos}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" /></p>
      </div>
      <div class="scroller videos" role="tabpanel">
        <ul class="gallery-videos gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.video != '' }">
              <li>
                <a class="group"
                   <g:if test="${it.preview.uri == ''}">
                     href="../images/bg/videoPoster.png"
                   </g:if>
                   <g:else>
                     href="${it.preview.uri}"
                   </g:else>  
                   data-content="${it.orig.uri.video}"  data-author="${it.orig.author}" data-rights="${it.orig.rights}" data-type="video" title="${(it.orig.title).encodeAsHTML()}">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
        <button class="btn-prev">
          <g:message code="ddbnext.Previous_Label" />
          <span class="opaque"></span>
        </button>
        <button class="btn-next">
          <g:message code="ddbnext.Next_Label" />
          <span class="opaque"></span>
        </button>
        <p class="gallery-pagination" data-pag="0"></p>
      </div>
      <div role="tablist">
        <p class="tab audios" role="tab"><g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" args="${flashInformation.audios}" default="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" /></p>
      </div>
      <div class="scroller audios" role="tabpanel">
        <ul class="gallery-audios gallery-tab">
          <g:each in="${binaryList}">
            <g:if test="${it.orig.uri.audio != '' }">
              <li>
                <a class="group"
                   <g:if test="${it.preview.uri == ''}">
                     href="../images/bg/audioPoster.png"
                   </g:if>
                   <g:else>
                     href="${it.preview.uri}"
                   </g:else>
                   data-content="${it.orig.uri.audio}" data-author="${it.orig.author}" data-rights="${it.orig.rights}" data-type="audio" title="${(it.orig.title).encodeAsHTML()}">
                  <div class="thumbnail video">
                    <img src="${it.thumbnail.uri}" alt="${(it.thumbnail.title).encodeAsHTML()}" />
                  </div>
                  <span class="label off">${it.orig.title}</span>
                </a>
              </li>
            </g:if>
          </g:each>
        </ul>
        <button class="btn-prev">
          <g:message code="ddbnext.Previous_Label" />
          <span class="opaque"></span>
        </button>
        <button class="btn-next">
          <g:message code="ddbnext.Next_Label" />
          <span class="opaque"></span>
        </button>
        <p class="gallery-pagination" data-pag="0"></p>
      </div>
    </div>
  </div>
</div>
