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
  <div class="object-controls span12">
    <!-- buttons -->
    <div class="span6">
          <ddbcommon:isLoggedIn>
            <span class="favorite-actions <g:if test="${isFavorite}">favorite-selected</g:if><g:else>favorite-add</g:else>" <g:if test="${isFavorite}">title="<g:message encodeAs="html" code="ddbnext.favorites_already_saved"/>"</g:if><g:else>title="<g:message encodeAs="html" code="ddbnext.Add_To_Favorites"/>"</g:else>>
              <span data-itemid="${entityId}" data-objecttype="entity" data-actn="POST" id="idFavorite">
                <g:message encodeAs="html" code="ddbnext.favorit" />
              </span>
            </span>
          </ddbcommon:isLoggedIn>
          <div class="link-block">
            <a class="page-link page-link-popup-anchor" href="${entityUri}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
              <span><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
            </a>
          </div>
    </div>
  </div>

  <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-body">
      <p><g:message encodeAs="html" code="ddbnext.Added_To_Favorites"/></p>
      <p><g:message encodeAs="html" code="ddbnext.Add_To_Personal_Favorites"/></p>
      <g:select name="favorite-folders" from="" multiple="multiple"/>
    </div>
    <div class="modal-footer">
      <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
        <g:message encodeAs="html" code="ddbcommon.Close"/>
      </button>
      <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
        <g:message encodeAs="html" code="ddbcommon.Save"/>
      </button>
    </div>
  </div>

</div>
