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
<html>
  <head>
    <ddbcommon:doRedirectIfNotLoggedIn/>
    <title><g:message code="ddbcommon.Dashboard"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    <meta name="page" content="userdashboard"/>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h1><g:message code="ddbnext.Heading_Dashboard"/></h1>
    <h2><g:message code="ddbnext.Subheading_Dashboard_Items"/></h2>
    <g:each in="${savedSearches}" var="savedSearch">
    <h3>${savedSearch.name}</h3>
    <g:if test="${savedSearch.numberOfResults}">
      <ddb:renderPublicFavoritesResults results="${savedSearch.items}"/>
      <g:if test="${savedSearch.numberOfResults > savedSearch.items.size()}">
       XXX and more
      </g:if>
     </g:if>
    <g:else>
      <g:message code="ddbnext.Subheading_Dashboard_No_Items"/>
    </g:else>
    </g:each>
    <h2><g:message code="ddbnext.Subheading_Dashboard_Institutions"/></h2>
    <ddb:renderPublicFavoritesResults results="${institutions}"/>
  </body>
</html>
