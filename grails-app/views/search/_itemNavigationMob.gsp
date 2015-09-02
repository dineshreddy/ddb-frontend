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
<g:set var="enableLeftPagination" value="${hitNumber == 1}"/>
<g:set var="displayRightPagination" value="${!nextId.isEmpty()}"/>

<ul class="inline">
  <li>
    <g:if test="${searchResultUri != null}">
      <a class="back-to-list" href="${searchResultUri}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Title" />">
        <span><g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /></span>
      </a>
    </g:if> 
    <g:else>
      <span class="back-to-list-greyed-out"><g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /> </span>
    </g:else>
  </li>
  <li class="next-item bl fr">
    <g:if test="${enableRightPagination}">
      <g:link controller="item" action="findById" params="${params + [id: nextId, hitNumber: hitNumber + 1]}" ><div><span><g:message encodeAs="html" code="ddbnext.Next_Label" /></span></div></g:link>
    </g:if>
    <g:else>
      <div class="disabled-arrow"></div>
    </g:else>
  </li>
  <li class="prev-item bl fr">
    <g:if test="${enableLeftPagination}">
      <g:link controller="item" action="findById" params="${params + [id: prevId, hitNumber: hitNumber - 1]}" ><div><span><g:message encodeAs="html" code="ddbnext.Previous_Label" /></span></div></g:link>
    </g:if>
    <g:else>
      <div class="disabled-arrow"></div>
    </g:else>
  </li>
</ul>
