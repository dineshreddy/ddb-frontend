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
  <div class="span12 item-links bb">
    <ddb:isLoggedIn>
      <div class="favorite" >
        <span class="favorite-actions <g:if test="${isFavorite}">favorite-selected</g:if><g:else>favorite-add</g:else>" <g:if test="${isFavorite}">title="<g:message code="ddbnext.favorites_already_saved"/>"</g:if><g:else>title="<g:message code="ddbnext.Add_To_Favorites"/>"</g:else>>
          <span data-itemid="${itemId}" data-actn="POST" id="idFavorite">
            <g:message code="ddbnext.favorit" />
          </span>
        </span>
      </div>
    </ddb:isLoggedIn>
    <div class="link-block">
      <a class="page-link page-link-popup-anchor" href="${itemUri}" title="<g:message code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
        <span><g:message code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
      </a>
    </div>
    <ddb:isLoggedIn>
      <div class="link-block">
        <g:link class="xml-link" controller="item" action="showXml" params="[id: itemId]">
              <g:message code="ddbnext.View" />
        </g:link>
      </div>
    </ddb:isLoggedIn>
  </div>
</div>
