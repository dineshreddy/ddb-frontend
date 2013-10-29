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
<html>
<head>
<title><g:truncateItemTitle title="${title}" length="${binaryList?271:351}"></g:truncateItemTitle> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

<meta name="page" content="item" />
<meta name="layout" content="main" />

<g:socialmediaMeta likeTitle="${title}" likeUrl="${g.baseUrl() + request.forwardURI}"/>

</head>
<body>
  <g:render template="controls" />
  <g:render template="institution" />
  <g:render template="itemLinks" />
  <div class="row item-detail item-content">
    <div class="<g:if test="${binaryList}">span6</g:if><g:else>span12</g:else> item-description">
      <h2><g:truncateItemTitle title="${title}" length="${binaryList?271:351}"></g:truncateItemTitle></h2>
      <g:render template="fields" />
      <g:render template="rights" />
      <g:render template="license" />
      <g:render template="origin" />
      <g:render template="share" />
    </div>
    <g:if test="${binaryList}">
      <g:render template="binaries" />
    </g:if>
  </div>
  <g:render template="hierarchy" />
  <g:render template="linkurl" />
</body>
</html>
