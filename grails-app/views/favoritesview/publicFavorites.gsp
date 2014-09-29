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
<g:set var="resultsPaginatorOptions" value="${[pageFilter: [10,20,40], pageFilterSelected: 20]}"></g:set>
<g:set var="navigationData" value="${[paginationURL: [firstPg: createAllFavoritesLink["firstPg"], lastPg: createAllFavoritesLink["lastPg"], prevPg: createAllFavoritesLink["prevPg"], nextPg: createAllFavoritesLink["nextPg"]], page: page, totalPages: totalPages ]}"></g:set>

<html>
  <head>
  
    <title>
      ${selectedFolder.title} - <g:message encodeAs="html" code="ddbnext.Public_List_Of" args="${[selectedUser.getFirstnameAndLastnameOrNickname()]}" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" />
    </title>

    <meta name="page" content="favorites">
    <meta name="layout" content="main">
    <meta name="page" content="publicFavorites">

    <ddb:getSocialmediaMeta likeTitle="${selectedFolder.title + " - " + g.message(code:"ddbnext.Public_List_Of", args:[selectedUser.getFirstnameAndLastnameOrNickname()]) + " - " + g.message(code:"ddbnext.Deutsche_Digitale_Bibliothek")}" likeUrl="${baseUrl + fullPublicLink}" />

  </head>
  <body>
    <div class="favorites-results-container public-favorites">
      <div class="row favorites-results-head">
        <div class="span12 public-favorites-header">
          <div class="title">
            <h1>
              ${selectedFolder.title.capitalize()}
            </h1>
          </div>
          <div class="print-header">
            <h3>
              <g:message encodeAs="html" code="ddbnext.Favorites_List_Of_Printed" args="${[selectedUser.username, dateString]}" default="ddbnext.Favorites_List_Of" />
            </h3>
          <%--
            <div class="page-info">
              <span class="results-overall-index">1-2 </span> 
              <span><g:message encodeAs="html" code="ddbnext.Of" /> </span> 
              <span><strong><span class="results-total">2 </span></strong> </span> 
              <g:if test="${numberOfResultsFormatted == '1'}"> 
                  <span id="results-label"><g:message encodeAs="html" code="ddbnext.Result_lowercase" /></span>
              </g:if>
              <g:else>
                  <span id="results-label"><g:message encodeAs="html" code="ddbnext.Results_lowercase" /></span>
              </g:else>
            </div>
            --%>
          </div>
          <div class="right-container">
            
            <a class="page-link page-link-popup-anchor" href="${fullPublicLink}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
              <span><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
            </a>
            
            <a class="saved-searches-list-envelope" id="favorites-list-send" href="#" title="<g:message encodeAs="html" code="ddbnext.favorites_list_send" />">
              <span><g:message encodeAs="html" code="ddbnext.favorites_list_send" /></span>
            </a>
            
<%--            <g:if test="${selectedFolder.isPublic && resultsNumber > 0}">--%>
<%--              <ddb:getSocialmediaBody/>--%>
<%--            </g:if>--%>
          </div>
        </div>
      </div>
      <div class="row favorites-results-container">
        <div class="span3 folder-information-container">
          <div class="folder-information bt bb bl br">
            <g:message encodeAs="html" code="ddbnext.List_Of"/> ${selectedUser.getFirstnameAndLastnameOrNickname()}
            <g:if test="${selectedFolder.description != null && !selectedFolder.description.trim().isEmpty()}">
              <br />
              <br />          
              <g:message encodeAs="html" code="ddbcommon.Create_Folder_Description"/>:
              <br />
              ${selectedFolder.description}
            </g:if>
          </div>
          <g:if test="${publicFolders != null && publicFolders.size() > 1}">
            <div class="folder-information bt bb bl br">
              <g:message encodeAs="html" code="ddbnext.Other_Lists_Of"/> ${selectedUser.getFirstnameAndLastnameOrNickname()}:
              <ul>
                <g:each var="publicFolder" in="${publicFolders}">
                  <g:if test="${publicFolder.folderId != selectedFolder.folderId}">
                    <li>
                      <g:link class="folder-siblings" controller="favoritesview" action="publicFavorites" params="${[userId: selectedUser.id, folderId: publicFolder.folderId]}">
                        ${publicFolder.title}
                      </g:link>
                    </li>
                  </g:if>
                </g:each>
            </ul>
          </div>
          </g:if>
          <div class="folder-information bt bb bl br">
            <%--         
            <a class="favorites-report" href="mailto:geschaeftsstelle@deutsche-digitale-bibliothek.de?subject=<g:message encodeAs="html" code="ddbnext.Report_Public_List" />: ${selectedFolder.title}&body=${contextUrl}${g.createLink(controller: "favoritesview", action:"publicFavorites", params: [userId: selectedUser.id, folderId: selectedFolder.folderId]) }" >
              <g:message encodeAs="html" code="ddbnext.Report_Public_List" />
            </a>
            --%>
            <g:link controller="favoritesview" action="publicFavorites" params="${[userId: selectedUser.id, folderId: selectedFolder.folderId, report: true]}" class="favorites-report">
              <g:message encodeAs="html" code="ddbnext.Report_Public_List" />
            </g:link>
          </div>
          <g:message code="ddbnext.List_Created_At" />:
          ${createdDateString}
          <br>
          <g:message code="ddbnext.List_Updated_At" />:
          ${updatedDateString}
          
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
              <div class="results-sorter">
                <span class="favorite-numberheader">
                  <g:if test="${order == "desc"}">
                    <a href="<ddb:doHtmlEncode url="${urlsForOrderNumber["asc"]}"/>">
                      #
                      <span>
                        <g:if test="${by == "number"}">
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
                        <g:if test="${by == "number"}">
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
                <g:if test="${order == 'desc'}" >
                    <a href="${urlsForOrderTitle["asc"].encodeAsHTML()}">
                      <g:message encodeAs="html" code="ddbnext.HierarchyHelp_Leaf"></g:message>
                      <span>
                       <g:if test="${by == "title"}">
                        <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="${urlsForOrderTitle["desc"].encodeAsHTML()}">
                      <g:message encodeAs="html" code="ddbnext.HierarchyHelp_Leaf"></g:message>
                      <span>
                       <g:if test="${by == "title"}">
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
                  <g:if test="${order == 'desc'}" >
                    <a href="${urlsForOrderDate["asc"].encodeAsHTML()}">
                      <g:message encodeAs="html" code="ddbnext.Added_On" />
                      <span>
                       <g:if test="${by == "date"}">
                        <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                       </g:if>
                       <g:else>
                        <g:img dir="images/icons" file="arrowsUpDown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                       </g:else>
                      </span>                    
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="${urlsForOrderDate["desc"].encodeAsHTML()}">
                      <g:message encodeAs="html" code="ddbnext.Added_On" />
                      <span>
                        <g:if test="${by == "date"}">
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
              <ddb:renderPublicFavoritesResults results="${results}" />
            </div>
          </g:if>
          <g:else>
            <div class="messages-container">
              <ul class="unstyled">
                <li><span><g:message encodeAs="html" code="ddbnext.no_favorites" /></span></li>
              </ul>
            </div>
          </g:else>
        </div>
      </div>
    </div>
    
    
<%--    <g:if test="${numberOfResults > 0}">--%>
    <div id="sendFavoriteListModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="sendSavedSearchesLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbcommon.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="sendSavedSearchesLabel">
          <g:message code="ddbnext.Send_Savedsearches" />
        </h3>
      </div>
      <form method="POST">
        <div class="modal-body">
          <fieldset>
            <input placeholder="<g:message code="ddbnext.send_favorites_email"/>" name="email" required="required"> 
            <br /> 
              <small class="muted">
                <g:message code="ddbnext.send_favorites_more_recipients" />
              </small> 
            <br />
          </fieldset>
        </div>
        <div class="modal-footer">
          <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
            <g:message code="ddbcommon.Close" />
          </button>
          <button class="btn-padding" type="submit" id="btnSubmit">
            <g:message code="ddbnext.send_now" />
          </button>
        </div>
      </form>
    </div>
<%--  </g:if>--%>
  
  </body>
</html>
