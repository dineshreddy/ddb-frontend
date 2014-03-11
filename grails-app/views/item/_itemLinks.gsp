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
      <ddb:isPersonalFavoritesAvailable>
        <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-body">
            <p><g:message code="ddbnext.Added_To_Favorites"/></p>
            <p><g:message code="ddbnext.Add_To_Personal_Favorites"/></p>
            <g:select name="favorite-folders" from="" multiple="true"/>
          </div>
          <div class="modal-footer">
            <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
              <g:message code="ddbnext.Close"/>
            </button>
            <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
              <g:message code="ddbnext.Save"/>
            </button>
          </div>
        </div>
      </ddb:isPersonalFavoritesAvailable>
    </ddb:isLoggedIn>
    <div class="link-block">
      <a class="page-link page-link-popup-anchor hidden-phone hidden-tablet" href="${itemUri}" title="<g:message code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
        <span><g:message code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
      </a>
    </div>
    <g:render template="pdfLink" />
    <ddb:isLoggedIn>
      <div class="link-block">
        <g:link class="xml-link hidden-phone hidden-tablet" controller="item" action="showXml" params="[id: itemId]" target="_blank">
          <span><g:message code="ddbnext.View" /></span>
        </g:link>
      </div>
    </ddb:isLoggedIn>
  </div>
</div>
