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
    <div class="results-paginator-options bb">
      <strong>1&nbsp;&nbsp;2&nbsp;&nbsp;3&nbsp;&nbsp;4&nbsp;&nbsp;5&nbsp;&nbsp;Weiter</strong>
    </div>
    <div class="list-items">
      <ul class="unstyled">
        <g:each in="${folders}" var="folder">
          <li class="item bt">
            <div class="summary row">
              <div class="summary-main-wrapper span7">
                <h2 class="title">
                  <a href="<g:createLink controller="favoritesview" action="publicFavorites" params="${[userId: folder?.userId, folderId: folder?.folderId]}" />" title="${folder?.description}" data-title="${folder?.title}" >
                  ${folder?.title}
                  </a>
                </h2>
                <div class="item-details">Eine Liste von ${folder?.publishingName} mit ${folder?.count} Objekten, erstellt: ${folder?.creationDateFormatted}</div>
                <div class="item-description"><span title="${folder?.description}"><ddb:getTruncatedItemTitle title="${folder?.description}" length="${ 100 }"/></span></div>
              </div>
              <div class="span2">
                  <a href="<g:createLink controller="favoritesview" action="publicFavorites" params="${[userId: folder?.userId, folderId: folder?.folderId]}" />" title="${folder?.description}" data-title="${folder?.title}" >
                    <img src="<g:if test="${folder?.oldestItemMetaData?.preview?.thumbnail?.contains('binary')}">${request.getContextPath()}</g:if>${folder?.oldestItemMetaData?.preview?.thumbnail}" alt="<ddb:getWithoutTags>${folder?.oldestItemMetaData?.preview?.title}</ddb:getWithoutTags>" />
                  </a>
              </div>
            </div>
          </li>
       </g:each>
      </ul>
    </div>
  </g:if>
  <g:else>
    <g:message encodeAs="html" code="${errorMessage}" />
  </g:else>
</div>