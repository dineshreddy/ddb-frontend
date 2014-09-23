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
<g:set var="itemTitle" value="${ddbcommon.getTruncatedItemTitle(title: title, length: (binaryList?271:351)) }" />
<html>
<head>
<title>${itemTitle} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<%-- Used for Canonical URL --%>
<link rel="canonical" href="${createLink(controller:'item',action:'findById',params: [id: itemId], base:domainCanonic)}" />
<meta name="page" content="item" />
<meta name="layout" content="main" />
<ddb:getSocialmediaMeta
  likeTitle="${itemTitle + " - " + g.message(code: "ddbnext.Deutsche_Digitale_Bibliothek")}"
  likeUrl="${baseUrl + request.forwardURI}" />
</head>
<body>
  <ddb:doHideIfEmbedded>
    <g:render template="controls" />
  </ddb:doHideIfEmbedded>
  <g:render template="institution" />
  <g:render template="itemLinks" />
  <div class="row item-detail item-content">
    <g:each in="${binaryList}">
      <g:if test="${!it.full.uri.isEmpty() || !it.preview.uri.isEmpty()}">
        <g:set var="hasBinary" value="${true}"/>
      </g:if>
    </g:each>
    <g:if test="${(hasBinary || !originUrl.isEmpty()) && (item.media!='no media type' && item.media!='unknown')}">
      <g:set var="display" value="${true}"/>
    </g:if>
    <div class="<g:if test="${display}">span6</g:if><g:else>span12</g:else> item-description">
      <h2>
        ${itemTitle.encodeAsHTML()}
      </h2>
      <g:render template="fields" />
      <g:render template="rights" />
      <g:render template="license" />
      <g:render template="origin" />
<%--
         Sending PDF per Mail Start    
      <div class="sendmail-block">
        <div id="i18ntranslateSend" data-val="<g:message encodeAs="html" code="ddbnext.Send_Button" />"></div>
        <div id="i18ntranslateValidEmail" data-val="<g:message encodeAs="html" code="ddbcommon.Enter_A_Valid_Email" />"></div>
        <a class="sendmail-link sendmail-link-popup-anchor"
          href="${createLink(controller: 'item', action: 'sendpdf', params:[id:itemId])}"
          title="<g:message encodeAs="html" code="ddbnext.item.sendPdf" />"> <span style="margin-left: 10px"></span>
        </a>
      </div>
         Sending PDF per Mail End    
--%>
    </div>
    <g:if test="${display}">
      <g:render template="binaries" />
    </g:if>
  </div>
<%-- https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-1627 <g:render template="similarObjects" /> --%>
  <g:render template="hierarchy" />
  <g:render template="linkurl" />
</body>
</html>
