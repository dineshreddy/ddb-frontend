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
    <g:isLoggedIn>
      <g:if test="${isFavorite}">
        <div class="favorite" title="<g:message code="ddbnext.favorites_already_saved"/>">
          <g:link params="${params+[reqActn:'del']}" class="favorite-actions favorite-selected">
            <span data-itemid="${itemId}" data-actn="DELETE" title="<g:message code='ddbnext.stat_011' />" id="idFavorite">
              <g:message code="ddbnext.favorit" />
            </span>
          </g:link>
        </div>
      </g:if>
      <g:else>
        <div class="favorite" title="<g:message code="ddbnext.Add_To_Favorites"/>">
          <g:link params="${params+[reqActn:'add']}" class="favorite-actions favorite-add">
            <span data-itemid="${itemId}" data-actn="POST" id="idFavorite">
              <g:message code="ddbnext.favorit" />
            </span>
          </g:link>
        </div>
      </g:else>
      <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-body">
          <p><g:message code="ddbnext.Added_To_Favorites"/></p>
          <g:hasPersonalFavorites>
            <p><g:message code="ddbnext.Add_To_Personal_Favorites"/></p>
            <g:select name="favorite-folders" from=""/>
          </g:hasPersonalFavorites>
        </div>
        <g:hasPersonalFavorites>
          <div class="modal-footer">
            <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
              <g:message code="ddbnext.Close"/>
            </button>
            <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
              <g:message code="ddbnext.Save"/>
            </button>
          </div>
        </g:hasPersonalFavorites>
      </div>
    </g:isLoggedIn>
    <div class="link-block">
      <a class="page-link page-link-popup-anchor" href="${itemUri}" title="<g:message code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
        <span><g:message code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
      </a>
    </div>
    <g:isLoggedIn>
      <div class="link-block">
        <g:link class="xml-link" controller="item" action="showXml" params="[id: itemId]" target="_blank">
              <g:message code="ddbnext.View" />
        </g:link>
      </div>
    </g:isLoggedIn>
  </div>
</div>
