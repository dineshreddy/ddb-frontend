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
<%@page import="de.ddb.common.constants.FacetEnum"%>
<html>
<head>
<title><g:message encodeAs="html" code="ddbnext.lists.page" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<meta name="page" content="lists" />
<meta name="layout" content="main" />

</head>
<body>
  <div class="row lists">
    <div class="span12 lists-header">
      <div>
        <h1>
          <g:message encodeAs="html" code="ddbnext.lists.page" />
        </h1>
      </div>
      <div class="right-container">
        <a class="page-link page-link-popup-anchor" href="${linkUri}" title="<g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Title" />">
          <span><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToThisPage_Label" /></span>
        </a>
        <ddb:getSocialmediaBody />
      </div>
    </div>
    <div class="lists-main">
      <%-- Lists menu on the left side  --%>
      <g:render template="listsMenu" />
  
      <%-- Lists content on the right side   --%>
      <g:render template="listsItems" />
    </div>
  </div>
</body>
</html>
