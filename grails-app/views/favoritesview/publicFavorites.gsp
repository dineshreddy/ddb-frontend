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
      ${selectedFolder.title} - <g:message encodeAs="html" code="ddbnext.Public_List_Of" args="${[selectedFolder.publishingName]}" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" />
    </title>
    <meta name="layout" content="main">
    <meta name="page" content="publicFavorites">
    <ddb:getSocialmediaMeta likeTitle="${selectedFolder.title + " - " + g.message(code:"ddbnext.Public_List_Of", args:[selectedFolder.publishingName]) + " - " + g.message(code:"ddbnext.Deutsche_Digitale_Bibliothek")}" likeUrl="${baseUrl + fullPublicLink}" />
    <link rel="canonical" href="${createLink(controller: 'favoritesview',
                                             action: 'publicFavorites',
                                             params: [folderId: selectedFolder.folderId, userId: selectedUserId],
                                             base: domainCanonic)}"/>
  </head>
  <body>
    <div class="favorites-results-container public-favorites">
      <div class="row favorites-results-head">
        <div class="span12 public-favorites-header">
          <div class="print-header">
            <h3>
              <g:message encodeAs="html" code="ddbnext.Favorites_List_Of_Printed" args="${[selectedUserUserName, dateString]}" default="ddbnext.Favorites_List_Of" />
            </h3>
          </div>
          <div class="right-container">
            <div class="reportfav">
              <div class="report-locked">
                <div class="report-overlay-container">
                  <div class="report-overlay">
                    <b><g:message encodeAs="html" code="ddbnext.Report_Public_List" /></b>
                    <br />
                      ${raw(g.message(code: 'ddbnext.Report_Favorites'))}
                    <br />
                  </div>
                </div>
                  <a>
                    <span><g:message encodeAs="html" code="ddbnext.Report_Public_List" /></span>
                  </a>
              </div>
            </div>
            <a class="page-link page-link-popup-anchor" href="${fullPublicLink}"
               title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
              <span><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
            </a>
            <ddbcommon:isLoggedIn>
              <a class="saved-searches-list-envelope" id="favorites-list-send" href="#"
                 title="<g:message encodeAs="html" code="ddbnext.favorites_list_send" />">
                <span><g:message encodeAs="html" code="ddbnext.favorites_list_send" /></span>
              </a>
            </ddbcommon:isLoggedIn>
            <ddb:getSocialmediaBody />
          </div>
        </div>
      </div>
      <div class="title">
        <h1>
          ${selectedFolder.title.capitalize()}
        </h1>
      </div>
      <div class="row favorites-results-container">
        <div class="span3 folder-information-container">
          <div>
            <strong>
              <g:message encodeAs="html" code="ddbnext.List_Of"/> ${selectedFolder.publishingName}
            </strong>
            <g:if test="${selectedFolder.description != null && !selectedFolder.description.trim().isEmpty()}">
              <br />
              <br />
              <strong>
                <g:message encodeAs="html" code="ddbcommon.Create_Folder_Description"/>:
              </strong>
              <br />
              ${selectedFolder.description}
            </g:if>
              <br />
              <br />
          </div>
          <p class="date-info">
            <g:message code="ddbnext.List_Created_At" />:
            ${createdDateString}
            <br>
            <g:message code="ddbnext.List_Updated_At" />:
            ${updatedDateString}
          </p>
          <br />
          <br />
          <g:if test="${publicFolders}">
            <strong>
              <g:message encodeAs="html" code="ddbnext.Other_Lists_Of"/> ${selectedFolder.publishingName}
            </strong>
            <ul id="public-folders" class="other-folder-list">
              <g:render template="favoritesAllFolders" ></g:render>
            </ul>
          </g:if>
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
                <li><span><g:message encodeAs="html" code="${flash.error}" /></span></li>
              </ul>
            </div>
          </g:if>
          <g:if test="${resultsNumber > 0}">
            <div class="favorites-results-controls">
                    <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex,
                                           numberOfResults: numberOfResultsFormatted,
                                           page: page,
                                           totalPages: totalPages,
                                           paginationURL: paginationURL]}"/>
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

    <div id="sendFavoriteListModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="sendFavoriteListLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbcommon.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="sendFavoriteListLabel">
          <g:message code="ddbnext.send_favorites" />
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
        <div class="modal-footer-savesearch">
          <button class="btn-padding" type="submit" id="btnSubmit">
            <g:message code="ddbnext.favorites_list_send" />
          </button>
        </div>
      </form>
    </div>
  </body>
</html>
