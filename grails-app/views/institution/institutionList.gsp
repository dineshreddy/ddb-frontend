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
<title><g:message encodeAs="html" code="ddbnext.Institutions" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="institutionList" />
<meta name="layout" content="main" />

<r:require module="institutionlist"/>

</head>
<body>
  <h1><g:message encodeAs="html" code="ddbnext.Institutions" /></h1>
  <div class="row">
    <div class="view-type-switch span12 off">
      <!--[if lt IE 9]>
      <div class="ie8-version">
      <![endif]-->
        <button id="view-institution-map" class="selected"
          type="button" title="<g:message encodeAs="html" code="ddbnext.InstitutionsList_ViewAsMapButton_Title" />">
          <g:message encodeAs="html" code="ddbnext.InstitutionsList_ViewAsMapButton_Label" />
        </button>
      <!--[if lt IE 9]>
      </div>
      <div class="ie8-version">
      <![endif]-->
        <button id="view-institution-list"
          type="button" title="<g:message encodeAs="html" code="ddbnext.InstitutionsList_ViewAsListButton_Title" />">
          <g:message encodeAs="html" code="ddbnext.InstitutionsList_ViewAsListButton_Label" />
        </button>
      <!--[if lt IE 9]>
      </div>
      <![endif]-->
    </div>
  </div>
  <div class="row institutionlist">
    <div class="span3">
      <g:render template="filterNotPhone" />
    </div>
    <div class="span9">
    <noscript>
      <div class="off">
    </noscript>
    <div class="loader">
      <img alt="" src="../images/icons/loaderSmall.gif">
    </div>
    <noscript>
      </div>
    </noscript>
    <g:render template="filterPhone" />
    <g:render template="pagination" />
      <div id="no-match-message" class="off">
        <g:message encodeAs="html" code="ddbnext.InstitutionPage_NoMatches" />
      </div>
      <ol id="institution-list">
        <g:each in="${ all }">
        <li class="institution-listitem" data-sector="${ it?.sector }" 
          data-institution-id="${ it?.id }" data-first-char="${ it?.firstChar }">
          <i class="icon-institution"></i>
          <g:render template="listItem" model="['item': it]"/>
          <g:render template="children" model="['children': it?.children]"/>
        </li>
        </g:each>
      </ol>
    </div>
    <div id="institution-map" class="span9 off">
      <div id="mapview">
        <%--<div id="mapContainerDiv"></div>--%>
        <div id="ddb-map"></div>
      </div>
    </div>
  </div>
</body>
</html>
