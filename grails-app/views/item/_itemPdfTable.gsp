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

<g:set var="itemTitle" value="${ddbcommon.getTruncatedItemTitle(title: title, length: (binaryList?271:351)) }" />
<title>
  ${itemTitle} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
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
  h1,h2,h3,h4 {
    font-family: Arial Unicode MS ", Arial, sans-serif;
    font-size: 1.75em;
    font-weight: normal;
    color: rgb(62, 58, 55);
    line-height: 1.5em;
  }
  h2,h2 a {
    font-family: Arial Unicode MS ", Arial, sans-serif;
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
    font-family: "Arial Unicode MS", Arial, sans-serif;
    font-size: 9pt;
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
    border-width: 0 0 5px;
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

  .slide-viewer {
    width: 445px;
    border: 3px solid #efebe8;
  }
  .slice-viewer-tabsection {
    padding: 5px;
    background: #efebe8;
  }
  .slide-viewer ul {
    list-style: none;
    margin: 0;
  }
  .scroller ul {
    padding: 0;
    white-space: nowrap;
    letter-spacing: -4px;
    left: 15px !important;
  }
  .scroller li {
    letter-spacing: normal;
    display: inline-block;
    width: 140px;
  }
  .scroller li .group {
    height: 131px;
    width: 120px;
    display: table-cell;
    vertical-align: middle;
  }
  .scroller li .group .thumbnail {
    display: block;
    text-align: center;
    width: 120px;
    height: 90px;
  }
  .previews img {
    max-width: 445px;
    max-height: 323px;
  }
  .slide-viewer button {
    position: absolute;
    top: 0;
    width: 24px;
    height: 131px;
    text-indent: -999em;
    display: block;
    box-shadow: none;
    -moz-box-shadow: none;
    -webkit-box-shadow: none;
  }
  .slide-viewer .btn-prev,.slide-viewer .btn-next {
    background: white;
    overflow: hidden;
    outline: none;
  }
  .slide-viewer .btn-prev.disabled,.slide-viewer .btn-next.disabled {
    cursor: default;
  }
  .slide-viewer .tabs {
    position: relative;
    height: auto
  }
  .slide-viewer .fix {
    height: 175px;
  }
  .slide-viewer .tab {
    cursor: pointer;
    float: left;
    color: #3e3a37;
    font-size: 1em;
    margin-top: 16px;
    line-height: 1em;
  }
  .slide-viewer .show-divider {
    border-right: #a5003b 2px solid;
    padding-right: 5px;
    margin-right: 5px;
  }
}
</style>
</head>
<body>
  <table border="0" width="100%">
    <tr>
      <td><rendering:inlinePng bytes="${logo}" alt="Deutsche Digitale Bibliothek" width="270px" height="68px" /></td>
    </tr>
    <tr>
      <td><g:render template="pdfTableInstitution" /></td>
    </tr>
    <tr>
      <td>
        <h2>${itemTitle}</h2>
        <g:if test="${binaryList}"> <g:render template="itemBinariesPdf" /></g:if>
     </td>
    </tr>    
  </table>
  <g:render template="itemdetailsPdf" />
  <g:render template="linkurlPdf" />
</body>
</html>
