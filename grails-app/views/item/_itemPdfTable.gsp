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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page defaultCodec="none"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="${ddb.getCurrentLocale()}">
<head>

<g:set var="itemTitle" value="${ddbcommon.wellFormedDocFromString(text: ddbcommon.getTruncatedItemTitle(title: title, length: (binaryList?271:351)))}"/>
<title>
  ${ddbcommon.wellFormedDocFromString(text: itemTitle)} - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<style type="text/css">
@media print {
  @page {
    margin: 1in;
    width: 21cm;
    min-height: 29.7cm;
    margin: 1cm auto;
    border: 1px #D3D3D3 solid;
    border-radius: 5px;
    background: white;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  }
 
  body{
    font-family:'Calibri';
    font-size: 12pt;  
  }
  h1,h2,h3,h4 {
    font-family:'KarbidWeb';
    font-size: 1.75em;
    font-weight: normal;
    color: rgb(62, 58, 55);
    line-height: 1.5em;
  }
  h2,h2 a {
    font-family:'KarbidWeb';
    font-weight: bold;
    font-size: 1.375em;
    color: #a5003b;
  }
  @table {
    table-layout: auto !important;
    width: auto !important;
  }
  th,td {
    vertical-align: top; ! important;
    margin-top: 0;
    font-family:KarbidWeb;
    font-size: 12pt;
  }
  a {
    color: #3e3a37;
    text-decoration: none;
  }
  .instititution-logo {
    float:right
    
  }
  .institution-name {
    height: 25px;
    line-height: 20px;
    font-weight: bold;
    font-size: 1.133em;
  }
  .item-detail .institution {
    margin-top: 1em;
    padding-bottom: 10px;
    border: 1px solid #e9e5e2;
    border-width: 0 0 2px;
    font-size: 0.938em;
  }
  .item-detail .institution .span3 img {
    margin: 0 15px 0 0;
  }
  .align-right {
    text-align: right;
  }
  .valign-top {
    vertical-align: text-top;
  }
  .previews img {
    max-width: 445px;
    max-height: 323px;
  }
  .bb {
    border-bottom: solid 2px #efebe8;
    margin: 10px 0 5px 0;
  }
  .bt {
    border-top: solid 2px #efebe8;
    margin: 5px 0 5px 0;
    padding-top: 2px;
  }
  .origin, .fields-table {
    margin-top: 10px;
  }
  .hierarchy-header {
    font-size: 17px;
    font-weight: bold;
    color: #3e3a37;
    padding-top: 10px;
    margin: 20px 0 15px 0;
  }
  .group-name {
    font-style: italic;
    font-size: 15px;
    list-type:none;
  }
  .element{
    position: relative;
    padding-left:10px;
  }
  .element ul {
    margin-top: 0px;
  }
  .element .bullet-item{
    display: list-item;
    list-style-type: square;
    margin-left: 1.1em;
  
  }
  .item-title {
    font-size: 16px;
  }
  .binary-title{
    padding-top:6px;
  }
}
</style>
</head>
<body>
  <table border="0" width="100%">
    <tr>
      <td>
        <rendering:inlinePng bytes="${logo}" alt="${g.message(code: "ddbnext.Deutsche_Digitale_Bibliothek")}"
                             width="270px" height="68px"/>
      </td>
    </tr>
    <tr>
      <td><div class="bb"></div></td>
    </tr>
    <tr>
      <td><g:render template="pdfTableInstitution"/></td>
    </tr>
    <tr>
      <td>
        <div class="item-title">
          <h2>
            <ddbcommon:wellFormedDocFromString text="${itemTitle}"/>
          </h2>
        </div>
        <g:if test="${binaryList}"><g:render template="itemBinariesPdf"/></g:if>
     </td>
    </tr>
  </table>
  <g:render template="itemdetailsPdf"/>
  <g:render template="linkurlPdf"/>
</body>
</html>
