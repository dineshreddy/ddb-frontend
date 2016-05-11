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
<div class="favorites-results-controls">
  <div class="row hidden-phone">
    <div class="span9 header-row">
      <div class="list-name">
        <span>
          <g:if test="${selectedFolder.folderId == mainFavoriteFolder.folderId}">
            <g:message encodeAs="html" code="ddbnext.All_Favorites" />
          </g:if>
          <g:else>
            ${selectedFolder.title.capitalize()}
          </g:else>
        </span>
          (${results?.size()})
      </div>
      <div class="controls-container">
        <g:if test="${selectedFolder.isPublic && !folderEmpty}">
          <div class="link-block">
            <a class="page-link page-link-popup-anchor" href="${fullPublicLink}" title="<g:message encodeAs="html"
               code="ddbnext.favorites_list_publiclink" />" data-title="${selectedFolder.title}" >
              <span><g:message encodeAs="html" code="ddbnext.favorites_list_publiclink" /></span>
            </a>
          </div>
        </g:if>
        <g:if test="${selectedFolder.folderId != mainFavoriteFolder.folderId}">
          <g:if test="${selectedFolder.isBlocked}">
            <a class="bookmarks-list-publish" title="<g:message encodeAs="html" code="ddbnext.Blocked_Folder" />">
              <g:message encodeAs="html" code="ddbnext.public"/>
            </a>
          </g:if>
          <g:else>
            <g:if test="${!folderEmpty}">
              <a href="#" class="bookmarks-list-publish cursor-pointer publishfolder <g:if test="${selectedFolder.isPublic}">open-lock</g:if>"
                 data-folder-id="${selectedFolder.folderId}"
                 <g:if test="${selectedFolder.isPublic}">
                   title="<g:message encodeAs="html" code="ddbnext.Hide_Folder"/>"
                 </g:if>
                 <g:else>
                   title="<g:message encodeAs="html" code="ddbnext.Publish_Folder" />"
                 </g:else>
              >
                <g:if test="${selectedFolder.isPublic}">
                  <g:message encodeAs="html" code="ddbnext.public"/>
                </g:if>
                <g:else>
                  <g:message encodeAs="html" code="ddbnext.private"/>
                </g:else>
              </a>
            </g:if>
          </g:else>
          <g:if test="${!folderEmpty}">
            <a href="#" class="bookmarks-list-edit cursor-pointer editfolder"
               data-folder-id="${selectedFolder.folderId}" title="<g:message encodeAs="html" code="ddbnext.Edit_Folder"/>">
              <g:message encodeAs="html" code="ddbnext.properties"/>
            </a>
          </g:if>
          <g:link controller="favorites" action="deleteFavoritesFolder" class="bookmarks-list-delete deletefolders"
                  data-folder-id="${selectedFolder.folderId}" title="${message(code: 'ddbnext.delete_favorites')}">
            <g:message encodeAs="html" code="ddbcommon.Delete"/>
          </g:link>
        </g:if>
      </div>
    </div>
  </div>
  <g:if test="${!folderEmpty}">
    <div class="delete-container row">
      <div class="span9">
        <div class="mobile-list-actions visible-phone">
          <div class="mobile-list-title">
            <strong>
              <g:if test="${selectedFolder.folderId == mainFavoriteFolder.folderId}">
                <g:message encodeAs="html" code="ddbnext.All_Favorites" /> 
              </g:if>
              <g:else>
                ${selectedFolder.title.capitalize()}
              </g:else>
            </strong>
          </div>
          <div class="mobile-list-otions">
            (${selectedFolder.bookmarks.size()})
            <g:if test="${selectedFolder.folderId != mainFavoriteFolder.folderId}">
              <a href="#" data-toggle-elem="#collapsing-options">
                <i class="mobile-gear-icon visible-phone"></i>
              </a>
            </g:if>
          </div>
        </div>
        <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex,
                                           numberOfResults: numberOfResultsFormatted,
                                           page: page,
                                           totalPages: totalPages,
                                           paginationURL: paginationURL]}"/>
        <div id="collapsing-options" class="element-collapsed">
          <div class="controls-container">
            <g:if test="${selectedFolder.isPublic && !folderEmpty}">
              <div class="link-block">
                <a class="page-link page-link-popup-anchor" href="${fullPublicLink}" title="<g:message encodeAs="html"
                   code="ddbnext.favorites_list_publiclink" />" data-title="${selectedFolder.title}">
                  <span><g:message encodeAs="html" code="ddbnext.favorites_list_publiclink" /></span>
                </a>
              </div>
            </g:if>
            <g:if test="${selectedFolder.folderId != mainFavoriteFolder.folderId}">
              <g:if test="${selectedFolder.isBlocked}">
                <a class="bookmarks-list-publish" title="<g:message encodeAs="html" code="ddbnext.Blocked_Folder" />">
                  <g:message encodeAs="html" code="ddbnext.public"/>
                </a>
              </g:if>
              <g:else>
                <a href="#" class="bookmarks-list-publish cursor-pointer publishfolder <g:if test="${selectedFolder.isPublic}">open-lock</g:if>"
                   data-folder-id="${selectedFolder.folderId}"
                   <g:if test="${selectedFolder.isPublic}">
                     title="<g:message encodeAs="html" code="ddbnext.Hide_Folder" />"
                   </g:if>
                   <g:else>
                     title="<g:message encodeAs="html" code="ddbnext.Publish_Folder" />"
                   </g:else>
                >
                  <g:message encodeAs="html" code="ddbnext.public"/>
                </a>
              </g:else>
              <a href="#" class="bookmarks-list-edit cursor-pointer editfolder" data-folder-id="${selectedFolder.folderId}"
                 title="<g:message encodeAs="html" code="ddbnext.Edit_Folder" />">
                <g:message encodeAs="html" code="ddbnext.properties"/>
              </a>
              <g:link controller="favorites" action="deleteFavoritesFolder" class="bookmarks-list-delete deletefolders"
                      data-folder-id="${selectedFolder.folderId}" title="${message(code: 'ddbnext.delete_favorites')}">
                <g:message encodeAs="html" code="ddbcommon.Delete"/>
              </g:link>
            </g:if>
          </div>
        </div>
        <div class="options-buttons-container mobile-off">
          <div class="delete-btn">
            <g:form id="favorites-copy" method="POST" name="favorites-copy" mapping="copyFavorites">
              <button type="submit" class="submit disabled" title="<g:message code="ddbcommon.favorites_copy"/>">
                <span><g:message encodeAs="html" code="ddbcommon.Copy"/></span>
              </button>
            </g:form>
          </div>
          <div class="delete-btn">
            <g:form id="favorites-remove" method="POST" name="favorites-remove" mapping="delFavorites">
              <button type="submit" class="submit disabled" title="<g:message code="ddbcommon.favorites_delete"/>">
                <span><g:message encodeAs="html" code="ddbcommon.Delete"/></span>
              </button>
            </g:form>
          </div>
        </div>
      </div>
    </div>
    <div class="results-sorter">
      <span><input type="checkbox" class="select-all" id="checkall"></span>
      <span class="favorite-numberheader">
      <g:if test="${order == "desc"}">
        <a href="<ddb:doHtmlEncode url="${urlsForOrderNumber["asc"]}" />">
          #
          <span>
            <g:if test="${by == "number"}">
              <g:img dir="images/icons" file="asc.gif" class="orderList"
                     alt="${message(code: 'ddbnext.Order_Ascending')}"/>
            </g:if>
            <g:else>
              <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                     alt="${message(code: 'ddbnext.No_Order')}"/>
            </g:else>
          </span>
        </a>
      </g:if> 
      <g:else>
        <a href="<ddb:doHtmlEncode url="${urlsForOrderNumber["desc"]}" />">
          #
          <span>
            <g:if test="${by == "number"}">
              <g:img dir="images/icons" file="desc.gif" class="orderList"
                     alt="${message(code: 'ddbnext.Order_Descending')}"/>
            </g:if>
            <g:else>
              <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                     alt="${message(code: 'ddbnext.No_Order')}"/>
            </g:else>
          </span>
        </a>
      </g:else>
    </span>
      <span>
        <g:if test="${order == "desc"}">
          <a href="<ddb:doHtmlEncode url="${urlsForOrderTitle["asc"]}" />">
            <g:message encodeAs="html" code="ddbnext.Savedsearch_Title"></g:message>
            <span>
              <g:if test="${by == "title"}">
                <g:img dir="images/icons" file="asc.gif" class="orderList"
                       alt="${message(code: 'ddbnext.Order_Ascending')}"/>
              </g:if>
              <g:else>
                <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                       alt="${message(code: 'ddbnext.No_Order')}"/>
              </g:else>
            </span>
          </a>
        </g:if> 
        <g:else>
          <a href="<ddb:doHtmlEncode url="${urlsForOrderTitle["desc"]}" />">
            <g:message encodeAs="html" code="ddbnext.Savedsearch_Title"></g:message>
            <span>
              <g:if test="${by == "title"}">
                <g:img dir="images/icons" file="desc.gif" class="orderList"
                       alt="${message(code: 'ddbnext.Order_Descending')}"/>
              </g:if>
              <g:else>
                <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                       alt="${message(code: 'ddbnext.No_Order')}"/>
              </g:else>
            </span>
          </a>
        </g:else>
      </span>
      <span class="favorite-dateheader"> 
        <g:if test="${order == "desc"}" >
          <a href="<ddb:doHtmlEncode url="${urlsForOrderDate["asc"]}" />">
            <g:message encodeAs="html" code="ddbnext.Added_On" />
            <span>
              <g:if test="${by == "date"}">
                <g:img dir="images/icons" file="asc.gif" class="orderList"
                       alt="${message(code: 'ddbnext.Order_Ascending')}"/>
              </g:if>
              <g:else>
                <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                       alt="${message(code: 'ddbnext.No_Order')}"/>
              </g:else>
            </span>
          </a>
        </g:if> 
        <g:else>
          <a href="<ddb:doHtmlEncode url="${urlsForOrderDate["desc"]}" />">
            <g:message encodeAs="html" code="ddbnext.Added_On" />
            <span>
              <g:if test="${by == "date"}">
                <g:img dir="images/icons" file="desc.gif" class="orderList"
                       alt="${message(code: 'ddbnext.Order_Descending')}"/>
              </g:if>
              <g:else>
                <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList"
                       alt="${message(code: 'ddbnext.No_Order')}"/>
              </g:else>
            </span>
          </a>
        </g:else>
      </span>
    </div>
  </g:if>
</div>