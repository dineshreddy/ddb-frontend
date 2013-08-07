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
<meta name="page" content="favorites" />
<meta name="layout" content="main" />
</head>
<body>
  <div class="favorites-results-container">
    <div class="row favorites-results-head">
      <div class="span8">
        <h1>
          <g:message code="ddbnext.Favorites_Header" />
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
        <ul class="bookmarks-lists unstyled">
          <g:each in="${allFolders}">
            <li class="bookmarks-list bt bb bl br"><span class="h3"> ${it.title.capitalize()}
            </span> <span class="bookmarks-list-number"> ${resultsNumber}</span> <g:if test="${resultsNumber > 0}">
                <a class="bookmarks-list-envelope cursor-pointer" id="sendbookmarks" ${createLink(controller:'user',action:'sendfavorites')}> <i
                  class="icon-envelope"
                ></i>
                </a>
              </g:if> <g:else>
                <a class="bookmarks-list-envelope" id="sendbookmarks" ${createLink(controller:'user',action:'sendfavorites')}> <i
                  class="icon-envelope"
                ></i>
                </a>
              </g:else></li>
          </g:each>
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
          <g:form id="favorites-remove" name="favorites-remove">
            <div class="favorites-results-controls">
              <div class="results-pagination">
                <button type="submit" class="submit" title="<g:message code="ddbnext.Delete"/>">
                  <span><g:message code="ddbnext.Delete"></g:message></span>
                </button>
          </g:form>
          <g:paginationControlsRender navData="${navigationData}"></g:paginationControlsRender>
      </div>
      <div class="results-sorter">
        <span><input type="checkbox" class="select-all" id="checkall"></span> <span><g:message code="ddbnext.HierarchyHelp_Leaf"></g:message></span>
        <span class="favorite-dateheader"> <g:message code="ddbnext.Added_On"></g:message> <span> <g:if test="${params.order== 'desc'}">
              <a href="${urlsForOrder["asc"]}"><g:img dir="images/icons" file="asc.gif" /></a>
            </g:if> <g:else>
              <a href="${urlsForOrder["desc"]}"><g:img dir="images/icons" file="desc.gif" /></a>
            </g:else>
        </span>
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
  <g:if test="${resultsNumber > 0}">
    <div id="favoritesModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3>
          <g:message code="ddbnext.send_favorites" />
        </h3>
      </div>
      <form method="POST" id="sendFavorites">
        <div class="modal-body">
          <fieldset>
            <input type="text" placeholder="<g:message code="ddbnext.send_favorites_email" />" type="email" name="email" required><br /> <small
              class="muted"
            ><g:message code="ddbnext.send_favorites_more_recipients" /></small><br />
            <hr />
            <blockquote>
              <br />
              <div class="favorites-results">
                <g:message code="ddbnext.favorites_email_body" args="${[userName,dateString]}" default="ddbnext.Favorites_List_Of" />
              </div>
              <br />
            </blockquote>
          </fieldset>
        </div>
        <div class="modal-footer">
          <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
            <g:message code="ddbnext.Close" />
          </button>
          <button class="btn-padding" type="submit" id="btnSubmit">
            <g:message code="ddbnext.send_now" />
          </button>
        </div>
      </form>
    </div>
  </g:if>
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
  <div class="modal hide fade" id="confirm-dialog">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3>
        <g:message code="ddbnext.delete_confirmation" />
      </h3>
    </div>
    <div class="modal-body">
      <g:message code="ddbnext.delete_favorites_dialog" />
      <span id="totalNrSelectedObjects"></span>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn btn-danger" id="id-confirm"><g:message code="ddbnext.Yes" /> </a> <a href="#" class="btn btn-cancel"
        data-dismiss="modal"
      ><g:message code="ddbnext.No" /></a>
    </div>
  </div>
  <div class="modal hide fade" id="noSelection">
    <div class="modal-header">
      <span title="<g:message code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
      <h3>
        <g:message code="ddbnext.delete_confirmation" />
      </h3>
    </div>
    <div class="modal-body">
      <g:message code="ddbnext.delete_favorites_dialog" />
      <span id="totalNrSelectedObjects"></span>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn btn-danger" id="id-confirm"><g:message code="ddbnext.Yes" /> </a> <a href="#" class="btn btn-cancel"
        data-dismiss="modal"
      ><g:message code="ddbnext.No" /></a>
    </div>
  </div>
</body>
</html>