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
  <title>${entity.preferredName} - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
  <%-- Used for Canonical URL --%>
  <link rel="canonical" href="${createLink(controller:'entity',action:'index',params: [id: entityId], base:domainCanonic)}"/>
  <meta name="page" content="entity"/>
  <meta name="layout" content="main"/>

</head> 
<body>
  <div class="entity-page main">
    <div id="entity-title" class="off">${entity.preferredName}</div>
    <a href="#" id="entity-id" data-entityid="${entityId}"></a>
    <g:render template="controls" />
    <hr>
    <div class="row">
      <div class="span9">
        <g:render template="name" />
        <g:render template="dates" />
        <g:if test="${searchPreview.resultCount > 0}">
          <hr>
          <g:render template="objects" />
        </g:if>
        <g:if test="${searchInvolved.resultCount > 0}">
          <hr>
            <g:render template="affiliateInvolvedObjects" />
        </g:if>        
        <g:if test="${searchSubject.resultCount > 0}">
          <hr>
            <g:render template="affiliateSubjectObjects" />
        </g:if>
      </div>
      <div class="span3">
        <g:render template="thumb" />
        <g:render template="search" />
        <g:render template="externalLinks" />
      </div>
    </div>
  </div>
  <div class="row item-detail linkurl off">
    <div><strong><g:message code="ddbcommon.CulturalItem_Deeplink" />: </strong></div>
    <div class="value">http<g:if test="${request.isSecure()}">s</g:if>://${request.serverName}${entityUri}</div>
  </div>
</body>
</html>
