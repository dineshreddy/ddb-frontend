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
<g:set var="itemTitle" value="${ddb.getTruncatedItemTitle(title: title, length: (binaryList?271:351)) }" />
<html>
<head>
<title>
  ${itemTitle} - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<meta name="page" content="item" />
<meta name="layout" content="pdfTable" />
<style>
body {
	font-family: Calibri, Arial, sans-serif;
	font-size: 16px;
	color: #3e3a37;
}
h1, h2, h3, h4 {
  font-family: KarbidWeb, Calibri, Arial, sans-serif;
  font-size: 1.75em;
  font-weight: normal;
  color: rgb(62, 58, 55);
  line-height: 1.5em;
}
h2, h2 a {
  font-family: KarbidWeb, Calibri, Arial, sans-serif;
  font-weight: bold;
  font-size: 1.375em;
  color: #a5003b;
}
table {
	margin: 10px auto;
}


a {
	color: #3e3a37;
	text-decoration: none;
}

a[href^="http"]:not (.no-external-link-icon ):not ([href^="http://localhost"]
   ):not ([href^="http://dev.escidoc.org"] ):not ([href^="http://www.deutsche-digitale-bibliothek.de"]
   ):not ([href^="https://www.deutsche-digitale-bibliothek.de"] ):not ([href^="http://www-t1.deutsche-digitale-bibliothek.de"]
   ):not ([href^="http://www-t3.deutsche-digitale-bibliothek.de"] ),.external-link-icon
	{
	padding-left: 20px;
	background: url(../images/icons/objectViewerActions.png) no-repeat -2px
		-80px;
}

.institution-name {
	padding-left: 17px;
	height: 25px;
	line-height: 20px;
	background: url(../images/icons/listArrows.png) no-repeat 0 1px;
	font-weight: bold;
	font-size: 1.133em;
}

.item-detail .institution {
	margin-top: 1em;
	padding-bottom: 10px;
	border: 1px solid #e9e5e2;
	border-width: 0 0 5px;
	font-size: 0.938em;
}

.item-detail .institution .span3 img {
	margin: 0 15px 0 0;
}

.align-right {
	text-align: right;
}
.align-right {
  text-align: right;
}
.valign-top{
  vertical-align:text-top;
}

.origin {
	margin-top: 20px;
}
.origin span {
  background: url("../images/icons/objectViewerActions.png") no-repeat;
  padding-left: 23px;
  line-height: 20px;
  background-position: -2px -120px;
}
origin .show-origin span {
  background: none;
  padding-left: 0;
}
.show-origin {
  padding-left: 17px;
  background: url(../images/icons/objectViewerActions.png) no-repeat -2px -80px;
  margin-right: 10px;
}
</style>
</head>
<body>
  <table border="0" cellpadding="0" cellspacing="0" summary="Print View for items on DDB" width="1170">
    <tr>
      <td><r:img dir="images" file="logoHeader.png" alt="" /></td>
    </tr>
    <tr>
      <td><g:render template="pdfTableInstitution" /></td>
    </tr>
    <tr>
      <td>
        <table border="0" cellpadding="0" cellspacing="0" width="100%" class="item-detail item-content">
          <tr>
            <g:if test="${binaryList}">
              <td style="width: 570px;" class="item-description">
                <h2>${itemTitle}</h2>
                <table border="0" cellpadding="8" cellspacing="0" width="100%">
                  <g:each in="${fields}">
                    <tr>
                      <td style="width:35%" class="valign-top"><strong> ${it.name}:
                      </strong></td>
                      <td class="valign-top value <g:if test="${binaryList}">span4</g:if><g:else>span10</g:else>"><g:each
                          var="value" in="${it.value }">
                          <g:if test="${value.@entityId != null && !value.@entityId.isEmpty()}">
                            <g:link controller="entity" action="index" params="${["id": value.@entityId]}"
                              class="entity-link">
                              ${value}
                            </g:link>
                          </g:if>
                          <g:else>
                            ${value}
                          </g:else>
                          <br />
                        </g:each></td>
                    </tr>
                  </g:each>
                  <!-- Item Rights -->
                  <g:if test="${item.rights != null && !item.rights.toString().trim().isEmpty()}">
                    <tr>
                        <td style="width:35%" class="valign-top"><strong> <g:message code="ddbnext.stat_007" />:</strong></td>
                        <td style="width:65%" class="valign-top">${item.rights}</td>
                    </tr>
                  </g:if>
                  <!-- Item License -->
                  <g:if test="${license}" >
                  <tr>
                        <td style="width:35%" class="valign-top"><strong> <g:message code="ddbnext.License_Field" />:</strong></td>
                        <td style="width:65%" class="valign-top"><a href="${license.url}" target="_blank" class="no-external-link-icon"><g:if test="${license.img}"><g:img file="${license.img}" alt="${license.text}" class="license-icon" /></g:if><span>${license.text}</span></a></td>
                  </tr>
                </g:if>                  
                </table>
              </td>
              <td style="width: 570px;"></td></g:if>
            <g:else>
              <td style="width: 100%" class="item-description"></td>
            </g:else>
          </tr>
        </table>
        
          <!-- Original Object View -->
          <div class="origin">
          <g:if test="${!originUrl?.toString()?.isEmpty()}">
            <a class="show-origin" href="${originUrl.encodeAsHTML()}" title="<g:message code="ddbnext.stat_008" />">
              <span class="has-origin"><g:message code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
            </a>
          </g:if>
          <g:else>
            <span><g:message code="ddbnext.Link_to_data_supplier_not_available" /></span>
          </g:else>
        </div>
      </td>
    </tr>
  </table>
</body>
</html>