<g:each var="publicFolder" in="${publicFolders}">
  <li>
    <i class="icon-institution"></i>
    <div>
      <g:link class="folder-siblings" controller="favoritesview" action="publicFavorites" params="${[userId: selectedUserId, folderId: publicFolder.folderId]}">
        ${publicFolder.title}
      </g:link>
    </div>
  </li>
</g:each>
<g:if test="${showAllList}" >
    <g:link elementId="alle-listen" data-userId="${selectedUserId}" data-selectedFolderId="${selectedFolder.folderId}" class="folder-siblings">
      <g:message encodeAs="html" code="ddbnext.AlleListen" />
    </g:link>
</g:if>