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
<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<%@page import="de.ddb.common.constants.FolderConstants"%>
<%@page import="org.h2.command.ddl.CreateLinkedTable"%>

<g:set var="resultsPaginatorOptions" value="${[pageFilter: [10,20,40], pageFilterSelected: 20]}"></g:set>
<g:set var="navigationData" value="${[paginationURL: [firstPg: createAllFavoritesLink["firstPg"], lastPg: createAllFavoritesLink["lastPg"], prevPg: createAllFavoritesLink["prevPg"], nextPg: createAllFavoritesLink["nextPg"]], page: page, totalPages: totalPages ]}"></g:set>

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
          <h3>
            <g:message encodeAs="html" code="ddbnext.Favorites_List_Of_Printed" args="${[userName, dateString]}" default="ddbnext.Favorites_List_Of" />
          </h3>
        </div>
      </div>
      <div class="row favorites-results-container">
        <div class="span3 bookmarks-container">
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

                  <g:if test="${it.folder.folderId != selectedFolder.folderId }">
                    <g:if test="${it.folder.folderId == mainFavoriteFolder.folderId}">
                      <g:link controller="favoritesview" action="favorites" params="${[id: it.folder.folderId]}" title="${folderTooltip}">
                        <g:message encodeAs="html" code="ddbnext.All_Favorites" />
                      </g:link>
                    </g:if>
                    <g:else>
                      <g:link controller="favoritesview" action="favorites" params="${[id: it.folder.folderId]}" title="${folderTooltip}">
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
                  <button id="button-new" type="submit" class="submit" title="<g:message encodeAs="html" code="ddbnext.Create_Folder_Title" />">
                    <span><g:message encodeAs="html" code="ddbnext.Create_Folder"></g:message></span>
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
                <li><i class="icon-ok-circle"></i><span><g:message encodeAs="html" code="${flash.message}" /></span></li>
              </ul>
            </div>
          </g:if>
          <g:if test="${flash.error}">
            <div class="errors-container">
              <ul class="unstyled">
                <li><i class="icon-exclamation-sign"></i><span><g:message encodeAs="html" code="${flash.error}" /></span></li>
              </ul>
            </div>
          </g:if>
          <g:if test="${resultsNumber > 0}">
            <div class="favorites-results-controls">
              <div class="row hidden-phone">
                <div class="span9 header-row">
                  <div class="list-name">
                    <span>
                      <g:if test="${selectedFolder.folderId == mainFavoriteFolder.folderId}">
                        <g:message encodeAs="html" code="ddbnext.All_Favorites" /> 
                      </g:if>
                      <g:else>
                        ${selectedFolder.title.capitalize()}
                      </g:else>
                    </span>
                    <g:each in="${allFolders}">
                      <g:if test="${it.folder.folderId == selectedFolder.folderId }">
                        (${it.count})
                      </g:if>
                    </g:each>
                  </div>
                  <div class="controls-container">
                    <g:if test="${selectedFolder.isPublic && resultsNumber > 0}">
                      <div class="link-block">
                        <a class="page-link page-link-popup-anchor" href="${fullPublicLink}" title="<g:message encodeAs="html" code="ddbnext.favorites_list_publiclink" />" data-title="${selectedFolder.title}" >
                          <span><g:message encodeAs="html" code="ddbnext.favorites_list_publiclink" /></span>
                        </a>
                      </div>
                    </g:if>
                    <g:each in="${allFolders}">
                      <g:if test="${it.folder.folderId == selectedFolder.folderId && it.folder.folderId != mainFavoriteFolder.folderId}">
                        <g:if test="${it.folder.isBlocked }">
                          <a class="bookmarks-list-publish" title="<g:message encodeAs="html" code="ddbnext.Blocked_Folder" />">
                            <g:message encodeAs="html" code="ddbnext.public"/>
                          </a>
                        </g:if>
                        <g:else>
                          <a href="#" class="bookmarks-list-publish cursor-pointer publishfolder <g:if test="${it.folder.isPublic}">open-lock</g:if>" data-folder-id="${it.folder.folderId}" 
                            <g:if test="${it.folder.isPublic}">
                              title="<g:message encodeAs="html" code="ddbnext.Hide_Folder" />"
                            </g:if>
                            <g:else>
                              title="<g:message encodeAs="html" code="ddbnext.Publish_Folder" />"
                            </g:else>
                          >
                            <g:message encodeAs="html" code="ddbnext.public"/>
                          </a>
                        </g:else>
                        <a href="#" class="bookmarks-list-edit cursor-pointer editfolder" data-folder-id="${it.folder.folderId}" title="<g:message encodeAs="html" code="ddbnext.Edit_Folder" />">  
                          <g:message encodeAs="html" code="ddbnext.properties"/>
                        </a>
                        <g:link controller="favorites" action="deleteFavoritesFolder" class="bookmarks-list-delete deletefolders" data-folder-id="${it.folder.folderId}" title="${message(code: 'ddbnext.delete_favorites')}">
                          <g:message encodeAs="html" code="ddbnext.Delete"/>
                        </g:link>
                      </g:if>
                    </g:each>
                  </div>
                </div>
              </div>
              <div class="delete-container row">
                <div class="span9">
                  <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResultsFormatted, page: page, totalPages: totalPages, paginationURL: paginationURL]}"/>
                  <div class="delete-btn">
                    <g:form id="favorites-copy" method="POST" name="favorites-copy" mapping="copyFavorites">
                      <button type="submit" class="submit disabled" title="<g:message encodeAs="html" code="ddbnext.Copy_Favorites" />">
                        <span><g:message encodeAs="html" code="ddbnext.Copy"></g:message></span>
                      </button>
                    </g:form>
                  </div>
                  <div class="delete-btn">
                    <g:form id="favorites-remove" method="POST" name="favorites-remove" mapping="delFavorites">
                      <button type="submit" class="submit disabled" title="<g:message encodeAs="html" code="ddbnext.Delete_Favorites" />">
                        <span><g:message encodeAs="html" code="ddbnext.Delete"></g:message></span>
                      </button>
                    </g:form>
                  </div>
                </div>
                <div class="span3">
                </div>
              </div>
              <div class="results-sorter">
                <span><input type="checkbox" class="select-all" id="checkall"></span> 
                <span class="favorite-numberheader">
                <g:if test="${params[SearchParamEnum.ORDER.getName()]== 'desc'}" >
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderNumber["asc"]}" />">
                      #
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "number"}">
                        <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderNumber["desc"]}" />">
                      #
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "number"}">
                        <g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:else>
                </span>
                <span>
                <g:if test="${params[SearchParamEnum.ORDER.getName()]== 'desc'}" >
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderTitle["asc"]}" />">
                      <g:message encodeAs="html" code="ddbnext.Savedsearch_Title"></g:message>
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "title"}">
                        <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderTitle["desc"]}" />">
                      <g:message encodeAs="html" code="ddbnext.Savedsearch_Title"></g:message>
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "title"}">
                        <g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:else>
                </span>
                <span class="favorite-dateheader"> 
                  <g:if test="${params[SearchParamEnum.ORDER.getName()] == 'desc'}" >
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderDate["asc"]}" />">
                      <g:message encodeAs="html" code="ddbnext.Added_On" />
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "date"}">
                        <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderDate["desc"]}" />">
                      <g:message encodeAs="html" code="ddbnext.Added_On" />
                      <span>
                       <g:if test="${params[SearchParamEnum.BY.getName()] == "date"}">
                        <span><g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/></span>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:else>
                </span>
              </div>
            </div>
            <div class="favorites-results">
              <ddb:renderFavoritesResults results="${results}" orderBy="${params[SearchParamEnum.BY.getName()]}"/>
            </div>
          </g:if>
          <g:else>
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
          </g:else>
        </div>
      </div>
    </div>
    
    <%-- Modal "Send email" --%>
    <g:if test="${resultsNumber > 0}">
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
          <div class="modal-footer">
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
          <g:message encodeAs="html" code="ddbnext.delete_favorites_from_all_dialog" />
        </g:if>
        <g:else>
          <g:message encodeAs="html" code="ddbnext.delete_favorites_dialog" />
        </g:else>
        <span class="totalNrSelectedObjects"></span>
      </div>
      <div class="modal-footer">
        <button class="submit" id="id-confirm"><g:message encodeAs="html" code="ddbnext.Yes" /></button> 
        <button class="submit" data-dismiss="modal" ><g:message encodeAs="html" code="ddbnext.No" /></button>
      </div>
    </div>
  
  
    <%-- Modal "Favorites copy" --%>
    <div class="modal hide fade" id="favoritesCopyDialog" tabindex="-1" role="dialog" aria-labelledby="favoritesCopyLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="favoritesCopyLabel">
          <g:message encodeAs="html" code="ddbnext.Copy_Confirmation" />
        </h3>
      </div>
      <div class="modal-body">
        <g:message encodeAs="html" code="ddbnext.Copy_Favorites" />
        <br />
        <select name="copyTargets" size="10" multiple="multiple" class="favorites-copy-selection">
          <g:each in="${allFolders}">
            <g:if test="${it.folder.folderId != selectedFolder.folderId && it.folder.folderId != mainFavoriteFolder.folderId}" >
              <option value="${it.folder.folderId}">${it.folder.title.capitalize()}</option>
            </g:if>
          </g:each>
        </select>
      </div>
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.Close" /></button>
        <button class="submit" id="copy-confirm"><g:message encodeAs="html" code="ddbnext.Save" /> </button> 
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
          <g:message encodeAs="html" code="ddbnext.Create_Folder_Name" />*
          <br />
          <input type="text" class="folder-create-name" id="folder-create-name"  required="required" />
        </div>
        <div>
          <g:message encodeAs="html" code="ddbnext.Create_Folder_Description" />
          <br />
          <textarea rows="10" cols="20" class="folder-create-description" id="folder-create-description"></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.Close" /></button>
        <button class="submit" id="create-confirm"><g:message encodeAs="html" code="ddbnext.Save" /> </button> 
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
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.Cancel" /></button>
        <button class="submit" id="delete-confirm"><g:message encodeAs="html" code="ddbnext.Confirm_Short" /> </button> 
      </div>
    </div>
  
  
    <%-- Modal "Edit folder" --%>
    <div class="modal hide fade" id="folderEditConfirmDialog" tabindex="-1" role="dialog" aria-labelledby="folderEditConfirmLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbcommon.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="folderEditConfirmLabel">
          <g:message encodeAs="html" code="ddbnext.Edit_Folder" />
        </h3>
      </div>
      <div class="modal-body">
        <div>
          <g:message encodeAs="html" code="ddbnext.Create_Folder_Name" />*
          <br />
          <input type="hidden" id="folder-edit-id" required="required" value="" />
          <input type="text" class="folder-edit-name" id="folder-edit-name" required="required" value="" />
        </div>
        <br />
        <div>
          <g:message encodeAs="html" code="ddbnext.Create_Folder_Description" />
          <br />
          <textarea rows="8" cols="20" class="folder-edit-description" id="folder-edit-description"></textarea>
        </div>
        <br />
        <div id="folder-edit-privacy-area">
          <div>
            <fieldset>
              <input type="radio" name="privacy" value="private" id="folder-edit-privacy-private">
              <label for="folder-edit-privacy-private"><g:message encodeAs="html" code="ddbnext.favorites_list_private"/></label>
              <br />
              <input type="radio" name="privacy" value="public" id="folder-edit-privacy-public">
              <label for="folder-edit-privacy-public"><g:message encodeAs="html" code="ddbnext.favorites_list_public"/></label>
            </fieldset>
          </div>
          <br />
          <div>
            <g:message encodeAs="html" code="ddbnext.favorites_list_publishtext"/>
            <br />
            <select name="publisher-name" size="1" id="folder-edit-publish-name">
              <option value="${FolderConstants.PUBLISHING_NAME_USERNAME.value}">${nickName}</option>
              <g:if test="${fullName}"> 
                <option value="${FolderConstants.PUBLISHING_NAME_FULLNAME.value}">${fullName}</option>
              </g:if>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" ><g:message encodeAs="html" code="ddbcommon.Close" /></button>
        <button class="submit" id="edit-confirm"><g:message encodeAs="html" code="ddbnext.Save" /> </button> 
      </div>
    </div>
  
    
  </body>
</html>
