<g:each var="publicFolder" in="${publicFolders}">
  <li>
    <i class="icon-institution"></i>
    <div>
      <g:link class="folder-siblings" controller="favoritesview" action="publicFavorites" params="${[userId: selectedUserId, folderId: publicFolder.folderId, showLinkAllList: showLinkAllList]}">
        ${publicFolder.title}
      </g:link>
    </div>
  </li>
</g:each>
<g:if test="${showLinkAllList}" >
    <br />
    <g:link elementId="alle-listen" data-userId="${selectedUserId}" data-selectedFolderId="${selectedFolder.folderId}" class="underlined">
      <g:message encodeAs="html" code="ddbnext.ALL_List_Of"/> ${selectedUserFirstnameAndLastnameOrNickname}
    </g:link>
</g:if>