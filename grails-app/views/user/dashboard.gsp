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
<%@page import="de.ddb.common.constants.Type"%>
<html>
  <head>
    <ddbcommon:doRedirectIfNotLoggedIn/>
    <title><g:message code="ddbcommon.Dashboard"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    <meta name="page" content="userdashboard"/>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h1><g:message code="ddbnext.MyDDB"/></h1>
    <div class="row">
      <div class="span12">
        <h3><g:message code="ddbnext.Favorites"/></h3>
        <div class="dashboard-favorites">
          <g:if test="${favoritesNumber}">
            <ddb:renderDashboardList results="${favorites}" viewType="grid" limit="${maxToDisplay}"/>
            <g:if test="${favoritesNumber > maxToDisplay}">
              <div class="dashboard-message"><g:message code="ddbnext.Subheading_Dashboard_More_Items"/></div>
            </g:if>
          </g:if>
          <g:else>
            <div class="dashboard-message"><g:message code="ddbnext.Subheading_Dashboard_No_Items"/></div>
          </g:else>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="span12">
        <h3><g:message code="ddbnext.Searches"/></h3>
        <div class="dashboard-saved-searches">
          <g:each in="${savedSearches}" var="savedSearch">
            <h3 class="dashboard-message">${savedSearch.name}</h3>
            <g:if test="${savedSearch.numberOfItems}">
              <ddb:renderDashboardList results="${savedSearch.items}" viewType="grid"/>
              <g:if test="${savedSearch.numberOfItems > savedSearch.items.size()}">
                <div class="dashboard-message"><g:message code="ddbnext.Subheading_Dashboard_More_Items"/></div>
              </g:if>
            </g:if>
            <g:else>
              <div class="dashboard-message"><g:message code="ddbnext.Subheading_Dashboard_No_Items"/></div>
            </g:else>
          </g:each>
        </div>
      </div>
    </div>
    <%-- 
    <div class="row">
      <div class="span12"><h3><g:message code="ddbnext.Subheading_Dashboard_Institutions"/></h3></div>
      <div class="span10 offset1">
        <div class="dashboard-institution">
          <ddb:renderDashboardList results="${institutions}"/>
        </div>
      </div>
    </div>
    --%>
  </body>
</html>
