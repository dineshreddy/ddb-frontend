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
<ul class="results-list unstyled" id="slaves">
  <g:set var="offset" value="${0}"/>
  <g:set var="index" value="${0}"/>
  <g:each in="${results}" >

    <g:set var="controller" value="item" />
    <g:set var="action" value="findById" />
    <g:if test="${it.category == 'Institution'}">
        <g:set var="controller" value="institution" />
        <g:set var="action" value="showInstitutionsTreeByItemId" />
    </g:if>
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span5">
          <g:if test="${!publicView}">
            <input type="checkbox" name="id[${index++}]" value="${it.id}" data-bookmark-id="${it.bookmark.bookmarkId}" class="remove-item-check">
          </g:if>
          <div class="summary-main">
            <h2 class="title">
              <g:if test="${it.category == "orphaned"}">
                <a title="${truncateHovercardTitle(title: it.label, length: 350)}">
                  <ddb:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                </a>
              </g:if>
              <g:else>
                <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]" title="${truncateHovercardTitle(title: it.label, length: 350)}">
                  <ddb:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                </g:link>
              </g:else>
            </h2>
            <div class="subtitle">
              <g:if test="${(it.preview?.subtitle != null) && (it.preview?.subtitle?.toString() != "null")}">
                ${it.preview.subtitle}
              </g:if>
              
            </div>
          </div>
          <div class="extra">
            <ddb:renderMediaTypesList mediaTypesArray="${it.preview.media}"></ddb:renderMediaTypesList>
          </div>
        </div>
        <div class="span2 thumbnail">
          <g:if test="${it.category == "orphaned"}">
            <a>
              <img src="<g:if test="${it.preview.thumbnail.contains('binary')}">${confBinary}</g:if>${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
            </a>
          </g:if>
          <g:else>
            <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]">
              <img src="<g:if test="${it.preview.thumbnail.contains('binary')}">${confBinary}</g:if>${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
            </g:link>
          </g:else>
        </div>
        <div class="span2 created-at">
          <div>${it.bookmark.creationDateFormatted}</div>
        </div>
      </div>
      <g:if test="${it.category != "orphaned" }">
        <div class="comment row">
          <div class="span9">
            <g:render template="favoritesComment" model="${[item: it, publicView: publicView]}" />
          </div>
        </div>
      </g:if>
    </li>
  </g:each>
</ul>