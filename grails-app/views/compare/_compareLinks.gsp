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

<div class="row">
  <div class="span12 compare-links bb">
    <div class="link-block">
      <g:if test="${searchResultUri != null}">
          <a class="back-to-list" href="${searchResultUri}" title="<g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" />">
              <span><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Label" /></span>
          </a>
      </g:if> 
      <g:else>
          <span class="back-to-list-greyed-out"><g:message code="ddbnext.CulturalItem_ReturnToSearchResults_Title" /> </span>
      </g:else>
    </div>  
    <div class="link-block">
      <a class="page-link page-link-popup-anchor" href="${itemUri}" title="<g:message code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
        <span><g:message code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
      </a>
    </div>
    <div class="share-block">
      <ddb:getSocialmediaBody />
    </div>
  </div>
</div>
