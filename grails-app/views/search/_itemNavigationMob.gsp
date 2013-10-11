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
<g:set var="prevId" value="${navData.results.results["docs"][0].id}" />
<g:set var="nextId" value="nodisplay" />
<g:if test="${navData.hitNumber == 1}">
<g:set var="displayLeftPagination" value="off" />
</g:if>
<g:if test="${navData.hitNumber == navData.results["numberOfResults"] || navData.results["numberOfResults"] == 1}">
  <g:set var="displayRightPagination" value="${false}" />
</g:if>
<g:else>
  <g:set var="displayRightPagination" value="${true}" />
</g:else>

<g:if test="${navData.hitNumber == 1 && navData.results["numberOfResults"] > 1}">
<g:set var="nextId" value="${navData.results.results["docs"][1].id}" />
</g:if>
<g:elseif test="${navData.hitNumber == navData.results["numberOfResults"]}">
<g:set var="nextId" value="${navData.results.results["docs"][0].id}" />
</g:elseif>
<g:else>
<g:set var="nextId" value="${navData.results.results["docs"][2].id}" />
</g:else>

<ul class="inline">
  <li>
    <g:if test="${searchResultUri != null}">
      <a class="back-to-list" href="${searchResultUri}" title="<g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Title" />">
        <span><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /></span>
      </a>
    </g:if> 
    <g:else>
      <span class="back-to-list-greyed-out"><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /> </span>
    </g:else>
  </li>
  <li class="prev-item bl ${displayLeftPagination}">
    <g:link controller="item" action="findById" params="${params + [id: prevId, hitNumber: navData.hitNumber - 1]}" ><span>Previous</span></g:link>
  </li>
  <li class="next-item bl <g:if test="${!displayRightPagination}">opaque</g:if>">
    <g:link controller="item" action="findById" params="${params + [id: nextId, hitNumber: navData.hitNumber + 1]}" ><span>Next</span></g:link>
  </li>
</ul>
