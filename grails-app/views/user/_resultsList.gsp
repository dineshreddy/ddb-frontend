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
    <g:if test="${it.preview.category == 'Institution'}">
        <g:set var="controller" value="institution" />
        <g:set var="action" value="showInstitutionsTreeByItemId" />
    </g:if>
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span5">
          <input type="checkbox" name="id[${index++}]" value="${it.id}" class="remove-item-check">
          <div class="summary-main">
            <h2 class="title">
              <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]" title="${truncateHovercardTitle(title: it.label, length: 350)}">
                <g:truncateItemTitle title="${ it.preview.title }" length="${ 100 }"></g:truncateItemTitle>
              </g:link>
            </h2>
            <div class="subtitle">
              <g:if test="${(it.preview?.subtitle != null) && (it.preview?.subtitle?.toString() != "null")}">
                ${it.preview.subtitle}
              </g:if>
              
            </div>
          </div>
          <div class="extra">
            <g:mediaTypesListRender mediaTypesArray="${it.preview.media}"></g:mediaTypesListRender>
          </div>
        </div>
        <div class="span2 thumbnail">
          <g:link class="persist" controller="${ controller }" action="${ action }" params="[id: it.id]">
            <img src="<g:if test="${it.preview.thumbnail.contains('binary')}">${confBinary}</g:if>${it.preview.thumbnail}" alt="<g:removeTags>${it.preview.title}</g:removeTags>" />
          </g:link>
        </div>
        <div class="span2 created-at">
          <div>${it.creationDate}</div>
        </div>
      </div>
    </li>
  </g:each>
</ul>