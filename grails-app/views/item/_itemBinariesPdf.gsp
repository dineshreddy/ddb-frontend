<table border="0" cellpadding="0" cellspacing="0" width="100%" class="slide-viewer item-detail">
  <tr>
    <td id="binary-viewer">
      <ul id="previews-list">
        <g:set var="counter" value="${0}" />
        <g:each in="${binaryList}">
          <g:if test="${it.full.uri == '' && it.preview.uri == ''}">
            <g:set var="content" value="${it.thumbnail.uri}" />
          </g:if>
          <g:elseif test="${it.full.uri == ''}">
            <g:set var="content" value="${it.preview.uri}" />
          </g:elseif>
          <g:else>
            <g:set var="content" value="${it.full.uri}" />
          </g:else>
          <g:if test="${it.preview.uri == ''}">
            <g:set var="viewerContent" value="${it.thumbnail.uri}" />
          </g:if>
          <g:else>
            <g:set var="viewerContent" value="${it.preview.uri}" />
          </g:else>
          <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
            <g:set var="counter" value="${counter + 1}" />
            <li><a class="previews" href="${content}">
                <rendering:inlineJpeg bytes="${binariesListViewerContent}" />
            </a></li>
          </g:if>
        </g:each>
      </ul>
    </td>
  </tr>
  <tr>
    <td class="slice-viewer-tabsection">
      <!--  This section should contain info on the first viewed Item -->
      <div class="binary-title">
        <span></span>
      </div>
      <div class="binary-author">
        <span></span>
      </div>
      <div class="binary-rights">
        <span></span>
      </div>
      <p class="tab all">
        <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_All" args="${flashInformation.all}"
          default="ddbnext.BinaryViewer_MediaCountLabelFormat_All" />
      </p>
    </td>
  </tr>
  <tr>
    <td class="all pdf-scroller">
      <ul class="gallery-all">
        <g:each in="${binaryList}">
          <li>
            <div class="group">
              <g:if test="${it.orig.uri.video == '' && it.orig.uri.audio == ''}">
                <g:set var="type" value="image" />
              </g:if>
              <g:elseif test="${it.orig.uri.video != ''}">
                <g:set var="type" value="video" />
              </g:elseif>
              <g:elseif test="${it.orig.uri.audio != ''}">
                <g:set var="type" value="audio" />
              </g:elseif>
              <div class="thumbnail ${type}"><%--
                <rendering:inlineJpeg bytes="${binariesListThumbnail}" />
              --%></div>
            </div>
          </li>
        </g:each>
      </ul>
    </td>
  </tr>
  <tr>
    <td class="slice-viewer-tabsection">
      <p class="tab images">
        <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" args="${flashInformation.images}"
          default="ddbnext.BinaryViewer_MediaCountLabelFormat_Images" />
      </p>
    </td>
  </tr>
  <tr>
    <td>
      <div class="scroller images pdf-scroller">
        <g:each in="${binaryList}">
          <g:if test="${it.full.uri != '' && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
            <g:set var="images" value="true" />
          </g:if>
        </g:each>
        <g:if test="${images}">
          <ul class="gallery-images">
            <g:each in="${binaryList}">
              <g:if test="${it.full.uri != '' && it.orig.uri.video == '' && it.orig.uri.audio == ''}">
                <li>
                  <div class="group">
                    <div class="thumbnail image"><%--
                      <rendering:inlineJpeg bytes="${binariesListThumbnail}" />
                    --%></div>
                  </div>
                </li>
              </g:if>
            </g:each>
          </ul>
        </g:if>
      </div>
    </td>
  </tr>
  <tr>
    <td class="slice-viewer-tabsection">
      <p class="tab videos">
        <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" args="${flashInformation.videos}"
          default="ddbnext.BinaryViewer_MediaCountLabelFormat_Videos" />
      </p>
    </td>
  </tr>
  <tr>
    <td>
      <div class="scroller videos pdf-scroller">
        <g:each in="${binaryList}">
          <g:if test="${it.orig.uri.video != '' }">
            <g:set var="videos" value="true" />
          </g:if>
        </g:each>
        <g:if test="${videos}">
          <ul class="gallery-videos">
            <g:each in="${binaryList}">
              <g:if test="${it.orig.uri.video != '' }">
                <li>
                  <div class="group">
                    <div class="thumbnail video"><%--
                      <rendering:inlineJpeg bytes="${binariesListThumbnail}" />
                    --%></div>
                  </div>
                </li>
              </g:if>
            </g:each>
          </ul>
        </g:if>
      </div>
    </td>
  </tr>
  <tr>
    <td class="slice-viewer-tabsection">
      <p class="tab videos">
        <g:message code="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" args="${flashInformation.audios}"
          default="ddbnext.BinaryViewer_MediaCountLabelFormat_Audios" />
      </p>
    </td>
  </tr>
  <tr>
    <td>
      <div class="scroller audios pdf-scroller">
        <g:each in="${binaryList}">
          <g:if test="${it.orig.uri.audio != '' }">
            <g:set var="audios" value="true" />
          </g:if>
        </g:each>
        <g:if test="${audios}">
          <ul class="gallery-audios">
            <g:each in="${binaryList}">
              <g:if test="${it.orig.uri.audio != '' }">
                <li>
                  <div class="group">
                    <div class="thumbnail video"><%--
                      <rendering:inlineJpeg bytes="${binariesListThumbnail}" />
                    --%></div>
                  </div>
                </li>
              </g:if>
            </g:each>
          </ul>
        </g:if>
      </div>
    </td>
  </tr>
</table>