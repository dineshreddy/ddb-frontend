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
<div class="span9 lists-content">
  <g:if test="${folders?.size() > 0}">
    <div class="lists-paginator-options bb">
      <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResults, page: page, totalPages: totalPages, paginationURL: paginationURL]}" />
    </div>
    <div class="list-items">
      <ul class="unstyled">
        <g:each in="${folders}" var="folder">
          <li class="item bb">
            <div class="summary row">
              <div class="summary-main-wrapper span7">
                <h2 class="title">
                  <a href="<g:createLink controller="favoritesview" action="publicFavorites" params="${[userId: folder?.userId, folderId: folder?.folderId]}" />" title="${folder?.description}" data-title="${folder?.title}" >
                  ${folder?.title}
                  </a>
                </h2>
                <div class="item-details"><g:message encodeAs="html" code="ddbnext.lists.itemDetails" args="${[folder?.publishingName, folder?.count, folder?.creationDateFormatted]}"/></div>
                <div class="item-description">${folder?.description}</div>
              </div>
              <div class="span2">
                  <a href="<g:createLink controller="favoritesview" action="publicFavorites" params="${[userId: folder?.userId, folderId: folder?.folderId]}" />" title="${folder?.description}" data-title="${folder?.title}" >
                    <img src="<g:if test="${folder?.thumbnailItemMetaData?.preview?.thumbnail?.contains('binary')}">${request.getContextPath()}</g:if>${folder?.thumbnailItemMetaData?.preview?.thumbnail}" alt="<ddb:getWithoutTags>${folder?.thumbnailItemMetaData?.preview?.title}</ddb:getWithoutTags>" />
                  </a>
              </div>
            </div>
          </li>
       </g:each>
      </ul>
    </div>
    <div class="lists-paginator-options bb">
      <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResults, page: page, totalPages: totalPages, paginationURL: paginationURL]}" />
    </div>    
  </g:if>
  <g:else>
    <g:message encodeAs="html" code="${errorMessage}" />
  </g:else>
</div>
