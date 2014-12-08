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

<div class="arrow-up"></div>
<div class="comment-container">
  <g:if test="${publicView && item.bookmark.description.isEmpty()}">
    <%-- if public and no comment -> show nothing --%>
  </g:if>
  <g:else>
    <div class="comment-text <g:if test="${!publicView}">comment-text-clickanchor</g:if>"
         id="comment-text-${item.bookmark.bookmarkId}" data-bookmark-id="${item.bookmark.bookmarkId}">
      <g:if test="${!(publicView || item.bookmark.description.isEmpty())}">
        <div class="comment-meta">
          ${item.bookmark.updateDateFormatted}
        </div>
      </g:if>
      <div class="comment-content"
           id="comment-text-dyn-${item.bookmark.bookmarkId}">
        <g:if test="${!item.bookmark.description.isEmpty()}">
          ${item.bookmark.description.trim()}
        </g:if>
        <g:else>
          <g:message encodeAs="html" code="ddbnext.Favorites_Comment_Label" />
        </g:else>
      </div>
      <g:if test="${publicView}">
        <div class="comment-meta fr">
          <g:message code="ddbnext.Public_Favorites_Comment_Of"/> ${item.folder.publishingName}
        </div>
        <div class="clearfix"></div>
      </g:if>
    </div>
    <g:if test="${!publicView}">
      <textarea class="comment-input off" id="comment-input-${item.bookmark.bookmarkId}" draggable="false" ><g:if test="${!item.bookmark.description.isEmpty()}">${item.bookmark.description.trim()}</g:if></textarea>
      <div class="comment-button off" id="comment-button-${item.bookmark.bookmarkId}">
        <g:form id="comment-save" method="POST" name="comment-save">
          <button type="submit" class="submit comment-save" title="<g:message encodeAs="html" code="ddbcommon.Save" />" data-bookmark-id="${item.bookmark.bookmarkId}">
            <span><g:message encodeAs="html" code="ddbcommon.Save"></g:message></span>
          </button>
          <button type="submit" class="submit comment-cancel grey" title="<g:message encodeAs="html" code="ddbcommon.Cancel" />" data-bookmark-id="${item.bookmark.bookmarkId}">
            <span><g:message encodeAs="html" code="ddbnext.Discard"></g:message></span>
          </button>
        </g:form>
      </div>
    </g:if>
  </g:else>
</div>


