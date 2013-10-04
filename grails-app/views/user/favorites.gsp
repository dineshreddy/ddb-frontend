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
<%@page import="org.h2.command.ddl.CreateLinkedTable"%>
<g:set var="resultsPaginatorOptions" value="${[pageFilter: [10,20,40], pageFilterSelected: 20]}"></g:set>
<g:set var="navigationData"
  value="${[paginationURL: [firstPg: createAllFavoritesLink["firstPg"], lastPg: createAllFavoritesLink["lastPg"], prevPg: createAllFavoritesLink["prevPg"], nextPg: createAllFavoritesLink["nextPg"]], page: page, totalPages: totalPages ]}"
></g:set>
<html>
<head>
<title><g:message code="ddbnext.Favorites_List_Of" args="${[userName]}" default="ddbnext.Favorites_List_Of" /> - <g:message
    code="ddbnext.Deutsche_Digitale_Bibliothek"
  /></title>
<meta name="page" content="favorites">
<meta name="layout" content="main">
</head>
<body>
  <div class="favorites-results-container">
    <div class="row favorites-results-head">
      <div class="span8">
        <h1>
          <g:message code="ddbnext.Favorites_Header" /> 
          <g:if test="${selectedFolderTitle == "favorites"}">
            <g:message code="ddbnext.All_Favorites" /> 
          </g:if>
          <g:else>
            ${selectedFolderTitle.capitalize()}
          </g:else>
        </h1>
      </div>
      <div class="print-header">
        <h3>
          <g:message code="ddbnext.Favorites_List_Of_Printed" args="${[userName, dateString]}" default="ddbnext.Favorites_List_Of" />
        </h3>
        <%--
          <div class="page-info">
            <span class="results-overall-index">1-2 </span> 
            <span><g:message code="ddbnext.Of" /> </span> 
            <span><strong><span class="results-total">2 </span></strong> </span> 
            <g:if test="${numberOfResultsFormatted == '1'}"> 
                <span id="results-label"><g:message code="ddbnext.Result_lowercase" /></span>
            </g:if>
            <g:else>
                <span id="results-label"><g:message code="ddbnext.Results_lowercase" /></span>
            </g:else>
          </div>
          --%>
      </div>
      <div class="span4 results-paginator-options">
        <div class="page-filter">
          <label><g:message code="ddbnext.SearchResultsPagination_Display" /></label> <span> <select class="select">
              <g:each in="${resultsPaginatorOptions.pageFilter}">
                <option value="${it}" <g:if test="${rows == it}">selected</g:if>>
                  ${it}
                </option>
              </g:each>
          </select>
          </span>
        </div>
      </div>
      <div class="span12">
        <hr>
      </div>
    </div>
    <div class="row favorites-results-container">
      <div class="span3 bookmarks-container">
        <ul class="bookmarks-lists unstyled" id="folder-list" data-folder-selected="${selectedFolderId}">
          <g:each in="${allFolders}">
            <li class="bookmarks-list bt bb bl br <g:if test="${it.folder.folderId == selectedFolderId }">selected-folder</g:if>">
              <span class="h3"> 
                <g:if test="${it.folder.folderId != selectedFolderId }">
                  <g:link controller="user" action="favorites" params="${[id: it.folder.folderId]}">
                    <g:if test="${it.folder.title == "favorites"}">
                      <g:message code="ddbnext.All_Favorites" />
                    </g:if>
                    <g:else>
                      ${it.folder.title.capitalize()}
                    </g:else>
                  </g:link>
                </g:if>
                <g:else>
                  <b>
                    <g:if test="${it.folder.title == "favorites"}">
                      <g:message code="ddbnext.All_Favorites" />
                    </g:if>
                    <g:else>
                      ${it.folder.title.capitalize()}
                    </g:else>
                  </b>
                </g:else>
              </span> 
              <span class="bookmarks-list-number"> ${it.count}</span>
              <a href="#" class="bookmarks-list-envelope cursor-pointer sendbookmarks">  
                <i class="icon-envelope" title="<g:message code="ddbnext.send_favorites" />" ></i>
              </a>
              <g:if test="${it.folder.title != "favorites"}">
                <g:link controller="favorites" action="deleteFavoritesFolder" class="bookmarks-list-delete deletefolders" data-folder-id="${it.folder.folderId}">
                  <i class="icon-remove" title="<g:message code="ddbnext.delete_favorites" />" ></i>
                </g:link>
              </g:if>
            </li>
          </g:each>
          <li class="">
            <span class="h3">
              <g:form id="folder-create" method="POST" name="folder-create">
                <button type="submit" class="submit" title="<g:message code="ddbnext.Create_Favorites" />">
                  <span><g:message code="ddbnext.Create_Folder"></g:message></span>
                </button>
              </g:form>
            </span>
          </li> 
        </ul>
      </div>
      <div class="span9 favorites-results-content">
        <g:if test="${flash.message}">
          <div class="messages-container">
            <ul class="unstyled">
              <li><i class="icon-ok-circle"></i><span><g:message code="${flash.message}" /></span></li>
            </ul>
          </div>
        </g:if>
        <g:if test="${flash.email_error}">
          <div class="errors-container">
            <ul class="unstyled">
              <li><i class="icon-exclamation-sign"></i><span><g:message code="${flash.email_error}" /></span></li>
            </ul>
          </div>
        </g:if>
        <g:if test="${resultsNumber > 0}">
          <div class="favorites-results-controls">
            <div class="deleteContainer row">
              <div class="deleteBtn span1">
                <g:form id="favorites-remove" method="POST" name="favorites-remove" mapping="delFavorites">
                  <button type="submit" class="submit" title="<g:message code="ddbnext.Delete_Favorites" />">
                    <span><g:message code="ddbnext.Delete"></g:message></span>
                  </button>
                </g:form>
              </div>
              <div class="deleteBtn span1">
                <g:form id="favorites-copy" method="POST" name="favorites-copy" mapping="copyFavorites">
                  <button type="submit" class="submit" title="<g:message code="ddbnext.Copy_Favorites" />">
                    <span><g:message code="ddbnext.Copy"></g:message></span>
                  </button>
                </g:form>
              </div>
              <div class="results-pagination">
                <g:paginationControlsRender navData="${navigationData}"></g:paginationControlsRender>
              </div>
            </div>
            <div class="results-sorter">
              <span><input type="checkbox" class="select-all" id="checkall"></span> 
              <span>
              <g:if test="${params.order== 'desc'}" >
                  <a href="${urlsForOrderTitle["asc"].encodeAsHTML()}">
                    <g:message code="ddbnext.HierarchyHelp_Leaf"></g:message>
                    <span><g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/></span>
                  </a>
                </g:if> 
                <g:else>
                  <a href="${urlsForOrderTitle["desc"].encodeAsHTML()}">
                    <g:message code="ddbnext.HierarchyHelp_Leaf"></g:message>
                    <span><g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/></span>
                  </a>
                </g:else>
              </span>
              <span class="favorite-dateheader"> 
                <g:if test="${params.order== 'desc'}" >
                  <a href="${urlsForOrder["asc"].encodeAsHTML()}">
                    <g:message code="ddbnext.Added_On" />
                    <span><g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/></span>
                  </a>
                </g:if> 
                <g:else>
                  <a href="${urlsForOrder["desc"].encodeAsHTML()}">
                    <g:message code="ddbnext.Added_On" />
                    <span><g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/></span>
                  </a>
                </g:else>
              </span>
            </div>
          </div>
          <div class="favorites-results">
            <g:favoritesResultsRender results="${results}"></g:favoritesResultsRender>
          </div>
        </g:if>
        <g:else>
          <div class="messages-container">
            <ul class="unstyled">
              <li><span><g:message code="ddbnext.no_favorites" /></span></li>
            </ul>
          </div>
        </g:else>
      </div>
    </div>
  </div>
  
  <%-- Modal "Send email" --%>
  <g:if test="${resultsNumber > 0}">
    <div id="favoritesModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="favoritesLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="favoritesLabel">
          <g:message code="ddbnext.send_favorites" />
        </h3>
      </div>
      <form method="POST" id="sendFavorites">
        <div class="modal-body">
          <fieldset>
            <input placeholder="<g:message code="ddbnext.send_favorites_email" />" type="email" name="email" required>
            <br /> 
            <small class="muted"><g:message code="ddbnext.send_favorites_more_recipients" /></small>
            <br />
          </fieldset>
        </div>
        <div class="modal-footer">
          <button class="btn-padding" data-dismiss="modal" aria-hidden="true"><g:message code="ddbnext.Close" /></button>
          <button class="btn-padding" type="submit" id="btnSubmit"><g:message code="ddbnext.send_now" /></button>
        </div>
      </form>
    </div>
  </g:if>
  
  
  <%-- Modal "Delete Favorites" --%>
  <%-- 
  <div id="msDeleteFavorites" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="msDeleteFavoritesLabel" aria-hidden="true">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3 id="msDeleteFavoritesLabel">
        <g:message code="ddbnext.delete_favorites" />
      </h3>
    </div>
    <div class="modal-body">
      <p>
        <g:message code="ddbnext.delete_favorites_succ" />
      </p>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn btn-danger" id="deletedFavoritesBtnClose"><g:message code="ddbnext.Close" /></a>
    </div>
  </div>
  --%>
  
  <%-- Modal "Confirm favorites delete" --%>
  <div class="modal hide fade" id="favoritesDeleteConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="favoritesDeleteConfirmLabel" aria-hidden="true">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3 id="favoritesDeleteConfirmLabel">
        <g:message code="ddbnext.delete_confirmation" />
      </h3>
    </div>
    <div class="modal-body">
      <g:if test="${selectedFolderTitle == "favorites"}">
        <g:message code="ddbnext.delete_favorites_from_all_dialog" />
      </g:if>
      <g:else>
        <g:message code="ddbnext.delete_favorites_dialog" />
      </g:else>
      <span class="totalNrSelectedObjects"></span>
    </div>
    <div class="modal-footer">
      <button class="submit" id="id-confirm"><g:message code="ddbnext.Yes" /></button> 
      <button class="submit" data-dismiss="modal" ><g:message code="ddbnext.No" /></button>
    </div>
  </div>


  <%-- Modal "Favorites copy" --%>
  <div class="modal hide fade" id="favoritesCopyDialog" tabindex="-1" role="dialog" aria-labelledby="favoritesCopyLabel" aria-hidden="true">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3 id="favoritesCopyLabel">
        <g:message code="ddbnext.copy_confirmation" />
      </h3>
    </div>
    <div class="modal-body">
      <g:message code="ddbnext.copy_favorites_dialog" />
      <span class="totalNrSelectedObjects"></span>
      <br />
      <select name="copyTargets" size="10" multiple="multiple" class="favorites-copy-selection">
        <g:each in="${allFolders}">
          <g:if test="${it.folder.folderId != selectedFolderId && it.folder.folderId != mainFavoriteFolderId}" >
            <option value="${it.folder.folderId}">${it.folder.title}</option>
          </g:if>
        </g:each>
      </select>
    </div>
    <div class="modal-footer">
      <button class="submit" data-dismiss="modal" ><g:message code="ddbnext.Close" /></button>
      <button class="submit" id="copy-confirm"><g:message code="ddbnext.Save" /> </button> 
    </div>
  </div>
  
  
  <%-- Modal "Create folder" --%>
  <div class="modal hide fade" id="folderCreateConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderCreateConfirmLabel" aria-hidden="true">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3 id="folderCreateConfirmLabel">
        <g:message code="ddbnext.Create_Folder_Title" />
      </h3>
    </div>
    <div class="modal-body">
      <div>
        <g:message code="ddbnext.Create_Folder_Name" />*
        <br />
        <input type="text" class="folder-create-title" id="folder-create-name"  required="required">
      </div>
      <div>
        <g:message code="ddbnext.Create_Folder_Description" />
        <br />
        <textarea rows="10" cols="20" class="folder-create-description" id="folder-create-description"></textarea>
      </div>
    </div>
    <div class="modal-footer">
      <button class="submit" data-dismiss="modal" ><g:message code="ddbnext.Close" /></button>
      <button class="submit" id="create-confirm"><g:message code="ddbnext.Save" /> </button> 
    </div>
  </div>
  
  
  <%-- Modal "Delete folder" --%>
  <div class="modal hide fade" id="folderDeleteConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderDeleteConfirmLabel" aria-hidden="true">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3 id="folderDeleteConfirmLabel">
        <g:message code="ddbnext.Delete_Folder_Title" />
      </h3>
    </div>
    <div class="modal-body">
      <div>
        <div class="folder-delete-row">
          <g:message code="ddbnext.Delete_Folder_Confirm" />
        </div>
        <div class="folder-delete-row">
          <input type="checkbox" class="folder-delete-check" id="folder-delete-check"/>
          <g:message code="ddbnext.Delete_Folder_Checkbox" />
        </div>
      </div>
    </div>
    <div class="modal-footer">
      <button class="submit" data-dismiss="modal" ><g:message code="ddbnext.Cancel" /></button>
      <button class="submit" id="delete-confirm"><g:message code="ddbnext.Confirm_Short" /> </button> 
    </div>
  </div>

  
</body>
</html>
