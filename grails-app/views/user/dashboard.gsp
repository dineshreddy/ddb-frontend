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
    <h1><g:message encodeAs="html" code="ddbnext.Heading_Dashboard"/></h1>
    <h3><g:message encodeAs="html" code="ddbnext.Subheading_Dashboard_Items"/></h3>
    <g:each in="${savedSearches}" var="savedSearch">
    ${savedSearch.key.label}: ${savedSearch.value.numberOfResults}
    </g:each>
    <h3><g:message encodeAs="html" code="ddbnext.Subheading_Dashboard_Institutions"/></h3>
    <ddb:renderPublicFavoritesResults results="${institutions}"/>
  </body>
</html>
