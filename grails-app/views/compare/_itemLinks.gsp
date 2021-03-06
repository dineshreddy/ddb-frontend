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
<%@page import="de.ddb.common.constants.Type"%>
<div class="row">
  <div class="span6 item-links bb">
    <ddbcommon:isLoggedIn>
      <div class="favorite" >
        <span class="favorite-actions <g:if test="${isFavorite}">favorite-selected</g:if><g:else>favorite-add</g:else>" <g:if test="${isFavorite}">title="<g:message encodeAs="html" code="ddbnext.favorites_already_saved"/>"</g:if><g:else>title="<g:message encodeAs="html" code="ddbnext.Add_To_Favorites"/>"</g:else>>
          <span data-itemid="${itemId}" data-actn="POST" data-objecttype="${Type.CULTURAL_ITEM.name}" id="idFavorite">
            <g:message encodeAs="html" code="ddbnext.Favorites" />
          </span>
        </span>
      </div>
    </ddbcommon:isLoggedIn>
    <g:render template="../common/addToFavorites"/>
    <div class="link-block">
      <a class="page-link page-link-popup-anchor" href="${itemUri}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
        <span><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
      </a>
    </div>
    <ddbcommon:isLoggedIn>
      <div class="link-block">
        <g:link class="xml-link" controller="item" action="showXml" params="[id: itemId]" target="_blank">
          <span><g:message encodeAs="html" code="ddbnext.View" /></span>
        </g:link>
      </div>
    </ddbcommon:isLoggedIn>
  </div>
</div>
