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
<title><g:message code="ddbnext.Favorites_List_Of" args="${[selectedUser.username]}" default="ddbnext.Favorites_List_Of" /> - <g:message
    code="ddbnext.Deutsche_Digitale_Bibliothek"
  /></title>
<meta name="page" content="favorites">
<meta name="layout" content="main">
</head>
<body>
  <div class="favorites-results-container public-favorites">
    <div class="row favorites-results-head">
      <div class="span8">
        <h1>
          ${selectedFolder.title.capitalize()}
        </h1>
      </div>
      <div class="print-header">
        <h3>
          <g:message code="ddbnext.Favorites_List_Of_Printed" args="${[selectedUser.username, dateString]}" default="ddbnext.Favorites_List_Of" />
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
          <label><g:message code="ddbnext.Items_Per_Page" /></label> <span> <select class="select">
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
      <div class="span3 folder-information-container">
        <div class="folder-information bt bb bl br">
          <g:message code="ddbnext.List_Of"/> ${selectedUser.username}
          <br />
          <br />          
          <g:message code="ddbnext.Create_Folder_Description"/>:
          <br />
          ${selectedFolder.description}
        </div>
        <div class="folder-information bt bb bl br">
          <g:message code="ddbnext.Other_Lists_Of"/> ${selectedUser.username}:
          <ul>
            <g:each var="publicFolder" in="${publicFolders}">
              <g:if test="${publicFolder.folderId != selectedFolder.folderId}">
                <li>
                  <g:link controller="favorites" action="publicFavorites" params="${[userId: selectedUser.id, folderId: publicFolder.folderId]}">
                    ${publicFolder.title}
                  </g:link>
                </li>
              </g:if>
            </g:each>
          </ul>
        </div>
        <div class="folder-information bt bb bl br">        
          <a href="mailto:geschaeftsstelle@deutsche-digitale-bibliothek.de?subject=<g:message code="ddbnext.Report_Public_List" />: ${selectedFolder.title}&body=${baseDomain}${g.createLink(controller: "favorites", action:"publicFavorites", params: [userId: selectedUser.id, folderId: selectedFolder.folderId]) }" >
            <g:message code="ddbnext.Report_Public_List" />
          </a>
        </div>
      </div>
      <div class="span9 favorites-results-content">
        <g:if test="${flash.message}">
          <div class="messages-container">
            <ul class="unstyled">
              <li><i class="icon-ok-circle"></i><span><g:message code="${flash.message}" /></span></li>
            </ul>
          </div>
        </g:if>
        <g:if test="${resultsNumber > 0}">
          <div class="favorites-results-controls">
            <div class="deleteContainer row">
              <div class="results-pagination">
                <g:paginationControlsRender navData="${navigationData}"></g:paginationControlsRender>
              </div>
            </div>
            <div class="results-sorter">
              <span>
              <g:if test="${params.order== 'desc'}" >
                  <a href="${urlsForOrderTitle["asc"].encodeAsHTML()}">
                    <g:message code="ddbnext.HierarchyHelp_Leaf"></g:message>
                    <span>
                     <g:if test="${params.by == "title"}">
                      <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                     </g:if>
                     <g:else>
                      <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                     </g:else>
                    </span>
                  </a>
                </g:if> 
                <g:else>
                  <a href="${urlsForOrderTitle["desc"].encodeAsHTML()}">
                    <g:message code="ddbnext.HierarchyHelp_Leaf"></g:message>
                    <span>
                     <g:if test="${params.by == "title"}">
                      <g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/>
                     </g:if>
                     <g:else>
                      <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                     </g:else>
                    </span>
                    
                  </a>
                </g:else>
              </span>
              <span class="favorite-dateheader"> 
                <g:if test="${params.order== 'desc'}" >
                  <a href="${urlsForOrder["asc"].encodeAsHTML()}">
                    <g:message code="ddbnext.Added_On" />
                    <span>
                     <g:if test="${params.by == "date"}">
                      <g:img dir="images/icons" file="asc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Ascending')}"/>
                     </g:if>
                     <g:else>
                      <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                     </g:else>
                    </span>                    
                  </a>
                </g:if> 
                <g:else>
                  <a href="${urlsForOrder["desc"].encodeAsHTML()}">
                    <g:message code="ddbnext.Added_On" />
                    <span>
                      <g:if test="${params.by == "date"}">
                        <span><g:img dir="images/icons" file="desc.gif" class="orderList" alt="${message(code: 'ddbnext.Order_Descending')}"/></span>                     
                      </g:if>
                      <g:else>
                        <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="${message(code: 'ddbnext.No_Order')}"/>
                      </g:else>
                    </span>
                  </a>
                </g:else>
              </span>
            </div>
          </div>
          <div class="favorites-results">
            <g:publicFavoritesResultsRender results="${results}"></g:publicFavoritesResultsRender>
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
  
  
  
</body>
</html>