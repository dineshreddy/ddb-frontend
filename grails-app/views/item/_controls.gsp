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
<!-- TODO: rewrite the class controls, disable back to results -->
<div class="row item-detail">
  <div class="span12 object-controls">
    <!-- buttons -->
    <div class="span6 item-nav-left">
      <g:if test="${searchResultUri != null}">
          <a class="back-to-list" href="${searchResultUri}" title="<g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Title" />">
              <span><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /></span>
          </a>
      </g:if> 
      <g:else>
          <span class="back-to-list-greyed-out"><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /> </span>
      </g:else>
    </div>
    <!-- search results navigation -->
    <g:if test="${hitNumber != null && results != null && firstHit != null && lastHit != null}">
      <div class="span6 item-nav fr">
        <g:itemDetailInfoNavRender navData="${[firstHit: firstHit, lastHit: lastHit, hitNumber: hitNumber, results: results]}"></g:itemDetailInfoNavRender>
      </div>
      <div class="span6 item-nav-mob fr bb">
        <g:itemDetailInfoNavMobRender navData="${[firstHit: firstHit, lastHit: lastHit, hitNumber: hitNumber, results: results, searchResultUri: searchResultUri]}"></g:itemDetailInfoNavMobRender>
      </div>
    </g:if>
    <g:else>
      <div class="span5 item-nav fr">
        <ul class="inline">
          <li class="items-overall-index">
              <span><g:message code="ddbnext.Hit" /> 1 <g:message code="ddbnext.Of" /> 1</span>
          </li>
        </ul>
      </div>
    </g:else>
  </div>
</div>
