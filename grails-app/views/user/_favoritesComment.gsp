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

<div class="comment-container">
  <div class="comment-text" id="comment-text-${item.bookmark.bookmarkId}" data-bookmark-id="${item.bookmark.bookmarkId}">
    <span class="comment-meta">
    <%-- 
      <g:if test="${!item.bookmark.description.isEmpty()}">
        Kommentar von ${item.bookmark.updateDate}:
      </g:if>
    --%>
    </span>
    <span id="comment-text-dyn-${item.bookmark.bookmarkId}">
      <g:if test="${!item.bookmark.description.isEmpty()}">
        ${item.bookmark.description.trim()}
      </g:if>
      <g:else>
        <g:message code="ddbnext.Favorites_Comment_Label" />
      </g:else>
    </span>
  </div>
  <textarea class="comment-input off" id="comment-input-${item.bookmark.bookmarkId}" draggable="false" ><g:if test="${!item.bookmark.description.isEmpty()}">${item.bookmark.description.trim()}</g:if></textarea>
  <br />
  <div class="comment-button off" id="comment-button-${item.bookmark.bookmarkId}">
    <g:form id="comment-save" method="POST" name="comment-save">
      <button type="submit" class="submit comment-save" title="<g:message code="ddbnext.Save" />" data-bookmark-id="${item.bookmark.bookmarkId}">
        <span><g:message code="ddbnext.Save"></g:message></span>
      </button>
      <button type="submit" class="submit comment-cancel" title="<g:message code="ddbnext.Cancel" />" data-bookmark-id="${item.bookmark.bookmarkId}">
        <span><g:message code="ddbnext.Cancel"></g:message></span>
      </button>
    </g:form>
  </div>
  
</div>


