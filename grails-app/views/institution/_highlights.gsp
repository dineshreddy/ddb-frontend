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
 <div class="span12 highlights off">
  <h3><g:message encodeAs="html" code="ddbnext.InstitutionItem_CollectionHighlights" /></h3>
   <div class="carousel bb bt">
      <div id="items">
      <%--      Items will be retrieved via Javascript       --%>
      </div>
      <div class="clearfix"></div>
      <button class="btn-prev disabled"><span class="opaque"><g:message encodeAs="html" code="ddbnext.Previous_Label" /></span></button>
      <button class="btn-next disabled"><span class="opaque"><g:message encodeAs="html" code="ddbnext.Next_Label" /></span></button>
    </div>
    <div class="link-highlights">
      <g:if test="${folder}">
        <g:link controller="favoritesview" action="publicFavorites" params="[userId: folder.userId, folderId: folder.folderId]">
          <g:message encodeAs="html" code="ddbnext.InstitutionItem_LinkHighlights" />
        </g:link>
      </g:if>
    </div>
 </div>
</div>
