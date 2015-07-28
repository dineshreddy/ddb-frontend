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
    <g:elseif test="${it.category == 'Entity'}">
        <g:set var="controller" value="entity" />
        <g:set var="action" value="index" />
    </g:elseif>
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span5">
          <g:if test="${publicView}">
            <g:if test="${it.orderNumber instanceof Number}">
              <div class="rank">${it.orderNumber + 1}</div>
            </g:if>
          </g:if>
          <g:else>
            <input type="checkbox" name="id[${index++}]" value="${it.id}" data-bookmark-id="${it.bookmark.bookmarkId}" class="remove-item-check">
            <div class="rank-wrapper" data-bookmark-id="${it.bookmark.bookmarkId}" data-folder-id="${it.folder.folderId}">
              <input type="text" value="${it.orderNumber + 1}" class="rank-input" autocomplete="off" <g:if test="${!orderBy.equals("number")}">disabled</g:if>>
              <div class="rank-arrows <g:if test="${!orderBy.equals("number")}">disabled</g:if>">
                <div class="up bb">+</div>
                <div class="down">-</div>
              </div>
            </div>
          </g:else>
          <div class="summary-main">
            <h2 class="title">
              <g:if test="${it.category == "orphaned"}">
                <a title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                  <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                </a>
              </g:if>
              <g:else>
                <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]" title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
                  <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }" length="${ 100 }" />
                </g:link>
              </g:else>
            </h2>
            <div class="subtitle">
              <g:if test="${(it.preview?.subtitle != null) && (it.preview?.subtitle?.toString() != "null")}">
                <ddbcommon:stripTags text="${it.preview.subtitle.replaceAll('match', 'strong')}" allowedTags="strong"/>
              </g:if>
            </div>
          </div>
        </div>
        <div class="span2 thumbnail">
          <g:if test="${it.category == "orphaned"}">
            <a>
              <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
            </a>
          </g:if>
          <g:else>
            <g:if test="${it.preview.media[0] == "entity"}">
              <g:set var="entityLink" value="persist entity-link" />
            </g:if>
            <g:else>
              <g:set var="entityLink" value="persist" />
            </g:else>
            <g:link class="${entityLink}" controller="${ controller }" action="${ action }" params="[id: it.id]">
              <img src="${it.preview.thumbnail}" alt="<ddb:getWithoutTags>${it.preview.title}</ddb:getWithoutTags>" />
            </g:link>
          </g:else>
        </div>
        <div class="span2 created-at">
         <div>${it.bookmark?.creationDateFormatted}</div>
        </div>
      </div>
      <g:if test="${it.category != "orphaned" }">
        <div class="comment row">
          <div class="span9">
            <g:render template="/favoritesview/favoritesComment" model="${[item: it, publicView: publicView]}" />
          </div>
        </div>
      </g:if>
    </li>
  </g:each>
</ul>
