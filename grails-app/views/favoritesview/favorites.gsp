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
<%@page import="de.ddb.common.constants.FolderConstants"%>

<g:set var="resultsPaginatorOptions" value="${[pageFilter: [10,20,40], pageFilterSelected: 20]}"></g:set>
<g:set var="navigationData" value="${[paginationURL: [firstPg: createAllFavoritesLink["firstPg"], lastPg: createAllFavoritesLink["lastPg"], prevPg: createAllFavoritesLink["prevPg"], nextPg: createAllFavoritesLink["nextPg"]], page: page, totalPages: totalPages ]}"></g:set>
<g:set var="folderEmpty" value="${resultsNumber == 0}"/>
<html>
  <head>
    <title>
      <g:message encodeAs="html" code="ddbnext.Favorites_List_Of" args="${[selectedFolder.folderId == mainFavoriteFolder.folderId ? message(code: 'ddbnext.All_Favorites') : selectedFolder.title.capitalize()]}"/> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" />
    </title>
    
    <meta name="page" content="favorites">
    <meta name="layout" content="main">
  </head>
  <body>
    <div class="favorites-results-container">
      <div class="row favorites-results-head">
        <div class="span8">
          <h1>
            <g:message encodeAs="html" code="ddbnext.Favorites_Header" />
            <g:message encodeAs="html" code="ddbnext.Favorites"/>
          </h1>
        </div>
        <div class="print-header">
          <g:message encodeAs="html" code="ddbnext.Favorites_List_Of_Printed" args="${[userName, dateString]}" default="ddbnext.Favorites_List_Of" />
        </div>
      </div>
      <div class="row favorites-results-container">
        <div class="span3 bookmarks-container">
          <g:render template="favoritesFolders" model="${pageScope.variables}"/>
        </div>
        <div class="span9 favorites-results-content">
          <g:if test="${flash.error}">
            <div class="errors-container">
              <ul class="unstyled">
                <li></i><span><g:message encodeAs="html" code="${flash.error}" /></span></li>
              </ul>
            </div>
          </g:if>
          <g:elseif test="${flash.message}">
            <div class="messages-container">
              <ul class="unstyled">
                <li><i class="icon-ok-circle"></i><span><g:message encodeAs="html" code="${flash.message}" /></span></li>
              </ul>
            </div>
          </g:elseif>
          <g:else>
            <g:render template="favoritesControls"/>
            <g:if test="${folderEmpty}">
              <div class="messages-container">
                <ul class="unstyled">
                  <g:if test="${selectedFolder.folderId == mainFavoriteFolder.folderId}">
                    <li><span><g:message encodeAs="html" code="ddbnext.no_favorites_general" /></span></li>
                  </g:if>
                  <g:else>
                    <li><span><g:message encodeAs="html" code="ddbnext.no_favorites" /></span></li>
                  </g:else>
                </ul>
              </div>
            </g:if>
            <g:else>
              <div class="favorites-results">
                <ddb:renderFavoritesResults results="${results}" orderBy="${by}"/>
              </div>
            </g:else>
          </g:else>
        </div>
      </div>
    </div>

    <%-- Modal "Send email" --%>
    <g:if test="${!folderEmpty}">
      <div id="favoritesModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="favoritesLabel" aria-hidden="true">
        <div class="modal-header">
          <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
          <h3 id="favoritesLabel">
            <g:message encodeAs="html" code="ddbnext.send_favorites" />
          </h3>
        </div>
        <form method="POST" id="sendFavorites">
          <div class="modal-body">
            <fieldset>
              <input placeholder="<g:message encodeAs="html" code="ddbnext.send_favorites_email" />" name="email" required="required" />
              <br /> 
              <small class="muted"><g:message encodeAs="html" code="ddbnext.send_favorites_more_recipients" /></small>
              <br />
            </fieldset>
          </div>
          <div class="modal-footer-savesearch">
            <button class="btn-padding" data-dismiss="modal" aria-hidden="true"><g:message encodeAs="html" code="ddbcommon.Close" /></button>
            <button class="btn-padding" type="submit" id="btnSubmit"><g:message encodeAs="html" code="ddbnext.send_now" /></button>
          </div>
        </form>
      </div>
    </g:if>

    <%-- Modal "Confirm favorites delete" --%>
    <div class="modal hide fade" id="favoritesDeleteConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="favoritesDeleteConfirmLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="favoritesDeleteConfirmLabel">
          <g:message encodeAs="html" code="ddbcommon.delete_confirmation" />
        </h3>
      </div>
      <div class="modal-body">
        <g:if test="${selectedFolder.folderId == mainFavoriteFolder.folderId}">
          <g:message encodeAs="html" code="ddbcommon.delete_favorites_from_all_dialog" />
        </g:if>
        <g:else>
          <g:message encodeAs="html" code="ddbcommon.delete_favorites_dialog" />
        </g:else>
        <span class="totalNrSelectedObjects"></span>
      </div>
      <div class="modal-footer-savesearch">
        <button class="submit grey" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.No" /></button>
        <button class="submit" id="id-confirm"><g:message encodeAs="html" code="ddbcommon.Yes" /></button>
      </div>
    </div>
  
  
    <%-- Modal "Favorites copy" --%>
    <div class="modal hide fade" id="favoritesCopyDialog" tabindex="-1" role="dialog" aria-labelledby="favoritesCopyLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="favoritesCopyLabel">
          <g:message encodeAs="html" code="ddbcommon.favorites_copy_confirmation_title" />
        </h3>
      </div>
      <div class="modal-body">
        <g:message encodeAs="html" code="ddbcommon.favorites_copy" />
        <br />
        <select name="copyTargets" size="10" multiple="multiple" class="favorites-copy-selection">
          <g:each in="${allFolders}">
            <g:if test="${it.folder.folderId != selectedFolder.folderId && it.folder.folderId != mainFavoriteFolder.folderId}" >
              <option value="${it.folder.folderId}">${it.folder.title.capitalize()}</option>
            </g:if>
          </g:each>
        </select>
      </div>
      <div class="modal-footer-savesearch">
        <button class="submit" id="copy-confirm"><g:message encodeAs="html" code="ddbcommon.Save" /> </button> 
      </div>
    </div>
    
    
    <%-- Modal "Create folder" --%>
    <div class="modal hide fade" id="folderCreateConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderCreateConfirmLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="folderCreateConfirmLabel">
          <g:message encodeAs="html" code="ddbnext.Create_Folder_Title" />
        </h3>
      </div>
      <div class="modal-body">
        <div>
          <g:message encodeAs="html" code="ddbcommon.Create_Folder_Name" />*
          <br />
          <input type="text" class="folder-create-name" id="folder-create-name"  required="required" />
        </div>
        <div>
          <g:message encodeAs="html" code="ddbcommon.Create_Folder_Description" />
          <br />
          <textarea rows="10" cols="20" class="folder-create-description" id="folder-create-description"></textarea>
        </div>
      </div>
      <div class="modal-footer-savesearch">
        <button class="submit" id="create-confirm"><g:message encodeAs="html" code="ddbcommon.Save" /> </button> 
      </div>
    </div>
    
    
    <%-- Modal "Delete folder" --%>
    <div class="modal hide fade" id="folderDeleteConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderDeleteConfirmLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="folderDeleteConfirmLabel">
          <g:message encodeAs="html" code="ddbnext.Delete_Folder_Title" />
        </h3>
      </div>
      <div class="modal-body">
        <div>
          <div class="folder-delete-row">
            <g:message encodeAs="html" code="ddbnext.Delete_Folder_Confirm" />
          </div>
          <div class="folder-delete-row">
            <input type="checkbox" class="folder-delete-check" id="folder-delete-check"/>
            <g:message encodeAs="html" code="ddbnext.Delete_Folder_Checkbox" />
          </div>
        </div>
      </div>
      <div class="modal-footer-savesearch">
        <button class="submit grey" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.Cancel" /></button>
        <button class="submit" id="delete-confirm"><g:message encodeAs="html" code="ddbcommon.Delete" /> </button> 
      </div>
    </div>
  
  
    <%-- Modal "Edit folder" --%>
    <div class="modal hide fade" id="folderEditConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderEditConfirmLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="folderEditConfirmLabel">
          <g:message encodeAs="html" code="ddbcommon.Edit_Folder" />
        </h3>
      </div>
      <div class="modal-body">
        <div>
          <g:message encodeAs="html" code="ddbcommon.Create_Folder_Name" />*
          <br />
          <input type="hidden" id="folder-edit-id" required="required" value="" />
          <input type="text" class="folder-edit-name" id="folder-edit-name" required="required" value="" />
        </div>
        <br />
        <div>
          <g:message encodeAs="html" code="ddbcommon.Create_Folder_Description" />
          <br />
          <textarea rows="8" cols="20" class="folder-edit-description" id="folder-edit-description"></textarea>
        </div>
        <br />
        <div id="folder-edit-privacy-area">
          <div>
            <fieldset>
              <input type="radio" name="privacy" value="private" id="folder-edit-privacy-private">
              <label for="folder-edit-privacy-private"><g:message encodeAs="html" code="ddbcommon.favorites_list_private"/></label>
              <br />
              <input type="radio" name="privacy" value="public" id="folder-edit-privacy-public">
              <label for="folder-edit-privacy-public"><g:message encodeAs="html" code="ddbcommon.favorites_list_public"/></label>
            </fieldset>
          </div>
          <br />
          <div>
            <g:message encodeAs="html" code="ddbcommon.favorites_list_publishtext"/>
            <br />
            <select name="publisher-name" size="1" id="folder-edit-publish-name">
              <option value="${FolderConstants.PUBLISHING_NAME_USERNAME.value}"
                      <g:if test="${selectedFolder.publishingName == nickName}"> selected</g:if>>${nickName}</option>
              <g:if test="${fullName}"> 
                <option value="${FolderConstants.PUBLISHING_NAME_FULLNAME.value}"
                        <g:if test="${selectedFolder.publishingName == fullName}"> selected</g:if>>${fullName}</option>
              </g:if>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer-savesearch">
        <button class="submit" id="edit-confirm"><g:message encodeAs="html" code="ddbcommon.Save" /> </button> 
      </div>
    </div>
    <div class="row item-detail linkurl off">
      <div><strong><g:message code="ddbcommon.CulturalItem_Deeplink" />: </strong></div>
      <div class="value">http<g:if test="${request.isSecure()}">s</g:if>://${request.serverName}${itemUri}</div>
    </div>
  </body>
</html>
