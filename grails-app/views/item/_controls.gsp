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
<!-- TODO: rewrite the class controls, disable back to results -->
<g:if test="${results}">
  <g:set var="prevId" value="${results.results["docs"][0]?.id}" />
  <g:if test="${hitNumber == 1 && results["numberOfResults"] > 1}">
    <g:set var="nextId" value="${results.results["docs"][1]?.id}" />
  </g:if>
  <g:elseif test="${hitNumber == results["numberOfResults"]}">
    <g:set var="nextId" value="${results.results["docs"][0]?.id}" />
  </g:elseif>
  <g:elseif test="${results.results["docs"].size() > 2}">
    <g:set var="nextId" value="${results.results["docs"][2]?.id}" />
  </g:elseif>
</g:if>

<div class="row item-detail">
  <div class="span12 object-controls bb">
    <!-- buttons -->
    <div class="span6 item-nav-left page-nav">
      <g:if test="${searchResultUri}">
          <a class="back-to-list" href="${searchResultUri}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Title" />">
              <span><g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /></span>
          </a>
      </g:if> 
      <g:else>
          <span class="back-to-list-greyed-out"><g:message encodeAs="html" code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /> </span>
      </g:else>
    </div>
    <!-- search results navigation -->
    <g:if test="${prevId && nextId && firstHit && lastHit && hitNumber && results}">
      <div class="span6 item-nav page-nav fr">
        <g:render template="/search/itemNavigation"/>
      </div>
      <div class="span6 item-nav-mob fr bb">
        <g:render template="/search/itemNavigationMob"/>
      </div> 
    </g:if>
    <g:else>
      <div class="span5 item-nav fr">
        <ul class="inline">
          <li class="items-overall-index">
              <span><g:message encodeAs="html" code="ddbnext.Hit" /> 1 <g:message encodeAs="html" code="ddbnext.Of" /> 1</span>
          </li>
        </ul>
      </div>
    </g:else>
  </div>
</div>
