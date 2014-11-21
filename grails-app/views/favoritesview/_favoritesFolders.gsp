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
<g:set var="doNotShowFavorites" value="flash.error || flash.message"/>
<div class="header-row">
  <h3><g:message encodeAs="html" code="ddbnext.lists"/></h3>
</div>
<ul class="bookmarks-lists unstyled" id="folder-list" data-folder-selected="${selectedFolder.folderId}">
  <g:each in="${allFolders}">
    <li class="bookmarks-list bt bb bl br <g:if test="${it.folder.folderId == selectedFolder.folderId }">selected-folder</g:if>">
      <div class="fav-text h3">
        <g:set var="folderTooltip" value="${it.folder.description}" />
        <g:if test="${it.folder.folderId == mainFavoriteFolder.folderId}">
          <g:set var="folderTooltip" value="${g.message(code:"ddbnext.All_Favorites")}" />
        </g:if>

        <g:if test="${it.folder.folderId != selectedFolder.folderId || doNotShowFavorites}">
          <g:if test="${it.folder.folderId == mainFavoriteFolder.folderId}">
            <g:link controller="favoritesview" action="favorites" params="${[id: it.folder.folderId]}"
                    title="${folderTooltip}">
              <g:message encodeAs="html" code="ddbnext.All_Favorites" />
            </g:link>
          </g:if>
          <g:else>
            <g:link controller="favoritesview" action="favorites" params="${[id: it.folder.folderId]}"
                    title="${folderTooltip}">
              ${it.folder.title.capitalize()}
            </g:link>
          </g:else>
        </g:if>
        <g:else>
          <b>
            <a title="${folderTooltip}">
              <g:if test="${it.folder.folderId == mainFavoriteFolder.folderId}">
                <g:message encodeAs="html" code="ddbnext.All_Favorites" />
              </g:if>
              <g:else>
                ${it.folder.title.capitalize()}
              </g:else>
            </a>
          </b>
        </g:else>
      </div> 
    </li>
  </g:each>
  <li class="">
    <span class="h3">
      <g:form id="folder-create" method="POST" name="folder-create">
        <button id="button-new" type="submit" class="submit" title="<g:message code="ddbnext.Create_Folder_Title"/>">
          <span><g:message encodeAs="html" code="ddbnext.Create_Folder"></g:message></span>
        </button>
      </g:form>
    </span>
  </li>
</ul>
