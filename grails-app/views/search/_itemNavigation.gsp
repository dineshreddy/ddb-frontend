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
  <li class="first-item ${displayLeftPagination}">
    <g:link controller="item" action="findById" params="${params + [id: navData.firstHit, hitNumber: '1']}"><g:message code="ddbnext.First_Label" /></g:link>
  </li>
  <li class="prev-item br-white ${displayLeftPagination}">
    <g:link controller="item" action="findById" params="${params + [id: prevId, hitNumber: navData.hitNumber - 1]}"><g:message code="ddbnext.Previous_Label" /></g:link>
  </li>
  <li class="<g:if test="${!displayRightPagination}">last-item-noicon</g:if>">
    <span class="result-label"><g:message code="ddbnext.Hit" /> </span><span class="hit-number"><ddb:getLocalizedNumber>${navData.hitNumber}</ddb:getLocalizedNumber></span><span> <g:message code="ddbnext.Of" /> </span><span class="hit-count"><ddb:getLocalizedNumber>${navData.results["numberOfResults"]}</ddb:getLocalizedNumber></span>
  </li>
  <li class="next-item bl-white <g:if test="${!displayRightPagination}">off</g:if>">
    <g:link controller="item" action="findById" params="${params + [id: nextId, hitNumber: navData.hitNumber + 1]}"><g:message code="ddbnext.Next_Label" /></g:link>
  </li>
  <li class="last-item <g:if test="${!displayRightPagination}">off</g:if>">
    <g:link controller="item" action="findById" params="${params + [id: navData.lastHit, hitNumber: navData.results['numberOfResults']]}"><g:message code="ddbnext.Last_Label" /></g:link>
  </li>
</ul>
