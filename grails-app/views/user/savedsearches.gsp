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
<g:set var="resultsPaginatorOptions" value="${[pageFilter: [10,20,40], pageFilterSelected: 20]}"/>
<g:set var="navigationData" value="${[paginationURL: [firstPg: paginationUrls["firstPg"],
                                                      lastPg: paginationUrls["lastPg"],
                                                      prevPg: paginationUrls["prevPg"],
                                                      nextPg: paginationUrls["nextPg"]
                                                     ],
                                                     page: page,
                                                     totalPages: totalPages]}"/>
<html>
  <head>
    <title>
      <g:message code="ddbnext.Savedsearches_Of" args="${[userName]}" default="ddbnext.Savedsearches_Of"/>
      - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/>
    </title>
    <meta name="page" content="savedsearches">
    <meta name="layout" content="main">
  </head>
  <body>
    <div class="favorites-results-container">
      <div class="row favorites-results-head">
        <div class="span8">
          <h1><g:message code="ddbnext.Savedsearches_Header"/></h1>
        </div>
        <div class="print-header">
          <h3>
            <g:message code="ddbnext.Savedsearches_Of_Printed" args="${[userName, dateString]}"
                       default="ddbnext.Savedsearches_Of"/>
          </h3>
        </div>
        <div class="span4 results-paginator-options">
          <div class="page-filter">
            <label><g:message code="ddbnext.SearchResultsPagination_Display"/></label>
            <span>
              <select class="select">
                <g:each in="${resultsPaginatorOptions.pageFilter}">
                  <option value="${it}" <g:if test="${rows == it}">selected</g:if>>${it}</option>
                </g:each>
              </select>
            </span>
          </div>
        </div>
        <div class="span12">
          <hr/>
        </div>
      </div>
      <div class="row favorites-results-container">
        <div class="span3 bookmarks-container">
          <ul class="bookmarks-lists unstyled">
            <li class="bookmarks-list bt bb bl br">
              <span class="h3"><g:message code="ddbnext.All_Savedsearches"/></span>
              <span class="bookmarks-list-number"> ${numberOfResults}</span>
              <a class="bookmarks-list-envelope" id="sendSavedSearches">
                <i class="icon-envelope" title="<g:message code="ddbnext.Send_Savedsearches"/>"></i>
              </a>
            </li>
          </ul>
        </div>
        <div class="span9 favorites-results-content">
          <g:if test="${flash.message}">
            <div class="messages-container">
              <ul class="unstyled">
                <li>
                  <i class="icon-ok-circle"></i><span><g:message code="${flash.message}"/></span>
                </li>
              </ul>
            </div>
          </g:if>
          <g:elseif test="${flash.email_error}">
            <div class="errors-container">
              <ul class="unstyled">
                <li>
                  <i class="icon-exclamation-sign"></i><span><g:message code="${flash.email_error}"/></span>
                </li>
              </ul>
            </div>
          </g:elseif>
          <g:if test="${results.size() > 0}">
            <div class="favorites-results-controls">
              <div class="deleteContainer row">
                <div class="deleteBtn span1">
                  <g:form id="deleteSavedSearches" method="POST" name="deleteSavedSearches" mapping="delSavedSearches">
                    <button type="submit" class="submit" title="<g:message code="ddbnext.Delete_Savedsearches"/>">
                      <span><g:message code="ddbnext.Delete"/></span>
                    </button>
                  </g:form>
                </div>
                <div class="results-pagination">
                  <g:paginationControlsRender navData="${navigationData}"/>
                </div>
              </div>
              <div class="results-sorter">
                <span><input type="checkbox" class="select-all" id="checkall"></span>
                <span>
                  <g:if test="${params.order == "desc"}">
                    <a href="${(urlsForOrder["asc"] + "&criteria=label").encodeAsHTML()}">
                      <g:message code="ddbnext.HierarchyHelp_Leaf"/>
                      <span>
                        <g:if test="${params.criteria == "label"}">
                          <g:img dir="images/icons" file="asc.gif" class="orderList" alt="order asc"/>
                        </g:if>
                        <g:else>
                          <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="no order"/>
                        </g:else>
                      </span>
                    </a>
                  </g:if>
                  <g:else>
                    <a href="${(urlsForOrder["desc"] + "&criteria=label").encodeAsHTML()}">
                      <g:message code="ddbnext.HierarchyHelp_Leaf"/>
                      <span>
                        <g:if test="${params.criteria == "label"}">
                          <g:img dir="images/icons" file="desc.gif" class="orderList" alt="order desc"/>
                        </g:if>
                        <g:else>
                          <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="no order"/>
                        </g:else>
                      </span>
                    </a>
                  </g:else>
                </span>
                <span class="favorite-dateheader"> 
                  <g:if test="${params.order == "desc"}">
                    <a href="${(urlsForOrder["asc"] + "&criteria=creationDate").encodeAsHTML()}">
                      <g:message code="ddbnext.Added_On"/>
                      <span>
                        <g:if test="${params.criteria == "creationDate"}">
                          <g:img dir="images/icons" file="asc.gif" class="orderList" alt="order asc"/>
                        </g:if>
                        <g:else>
                          <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="no order"/>
                        </g:else>
                      </span>
                    </a>
                  </g:if>
                  <g:else>
                    <a href="${(urlsForOrder["desc"] + "&criteria=creationDate").encodeAsHTML()}">
                      <g:message code="ddbnext.Added_On"/>
                      <span>
                        <g:if test="${params.criteria == "creationDate"}">
                          <g:img dir="images/icons" file="desc.gif" class="orderList" alt="order desc"/>
                        </g:if>
                        <g:else>
                          <g:img dir="images/icons" file="arrowsupdown.png" class="orderList" alt="no order"/>
                        </g:else>
                      </span>
                    </a>
                  </g:else>
                </span>
              </div>
            </div>
            <div class="favorites-results">
              <g:render template="savedsearch" bean="${it}"/>
            </div>
          </g:if>
          <g:else>
            <div class="messages-container">
              <ul class="unstyled">
                <li>
                  <span><g:message code="ddbnext.No_Savedsearches"/></span>
                </li>
              </ul>
            </div>
          </g:else>
        </div>
      </div>
    </div>

    <div id="sendSavedSearchesModal" class="modal hide fade" tabindex="-1" role="dialog"
         aria-labelledby="sendSavedSearchesLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbnext.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="sendSavedSearchesLabel">
          <g:message code="ddbnext.Send_Savedsearches"/>
        </h3>
      </div>
      <form method="POST">
        <div class="modal-body">
          <fieldset>
            <input placeholder="<g:message code="ddbnext.send_favorites_email"/>" type="email" name="email" required>
            <br/>
            <small class="muted"><g:message code="ddbnext.send_favorites_more_recipients"/></small>
            <br/>
          </fieldset>
        </div>
        <div class="modal-footer">
          <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
            <g:message code="ddbnext.Close"/>
          </button>
          <button class="btn-padding" type="submit" id="btnSubmit">
            <g:message code="ddbnext.send_now"/>
          </button>
        </div>
      </form>
    </div>

    <div id="deleteSavedSearchesModal" class="modal hide fade" tabindex="-1" role="dialog"
         aria-labelledby="deleteSavedSearchesLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbnext.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="deleteSavedSearchesLabel">
          <g:message code="ddbnext.delete_confirmation"/>
        </h3>
      </div>
      <div class="modal-body">
        <g:message code="ddbnext.Delete_Savedsearches_Dialog"/>
        <span id="totalNrSelectedObjects"></span>
      </div>
      <div class="modal-footer">
        <button class="submit" id="id-confirm">
          <g:message code="ddbnext.Yes"/>
        </button>
        <button class="submit" data-dismiss="modal">
          <g:message code="ddbnext.No"/>
        </button>
      </div>
    </div>
  </body>
</html>
