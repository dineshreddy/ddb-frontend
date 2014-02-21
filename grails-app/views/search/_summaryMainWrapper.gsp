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
<%@page import="de.ddb.next.constants.SearchParamEnum"%>
<div class="summary-main-wrapper <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">span6</g:if>">
  <div class="summary-main">
    <h2 class="title">
      <g:link class="persist" controller="${ controller }" action="${ action }" params="${params + [id:item.id, hitNumber:hitNumber]}" title="${ddb.getTruncatedHovercardTitle(title: item.label, length: 350)}">
      <ddb:getTruncatedItemTitle title="${ item.preview.title }" length="${ 100 }" />
      </g:link>
    </h2>
    <div class="subtitle">
      <g:if test="${(item.preview?.subtitle != null) && (item.preview?.subtitle?.toString() != "null")}">
        ${item.preview.subtitle}
      </g:if>
    </div>
    <ul class="matches unstyled">
      <li class="matching-item">
        <span>
          <g:each var="match" in="${item.view}">
            ...${match.replaceAll('match', 'strong')}...
          </g:each>
        </span>
      </li>
    </ul>
  </div>
  <div class="extra">
    <ddb:renderMediaTypesList mediaTypesArray="${item.preview.media}"></ddb:renderMediaTypesList>
  </div>
</div>
