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
<meta name="layout" content="pdftable" />
<style type="text/css">
@media print {
body {
  font-family: "Arial Unicode MS", Arial, sans-serif;
  font-size: 16px;
  color: #3e3a37;
}
.page {
margin: 1in;
        width: 21cm;
        min-height: 29.7cm;
        margin: 1cm auto;
        border: 1px #D3D3D3 solid;
        border-radius: 5px;
        background: white;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}
.subpage {
        padding: 1cm;
        border: 5px red solid;
        height: 237mm;
        outline: 2cm #FFEAEA solid;
}

h1, h2, h3, h4 {
  font-family: Arial Unicode MS", Arial, sans-serif;
  font-size: 1.75em;
  font-weight: normal;
  color: rgb(62, 58, 55);
  line-height: 1.5em;
}
h2, h2 a {
  font-family: Arial Unicode MS", Arial, sans-serif;
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
.instititution{
  width:"60%"
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

.slide-viewer {
  width:445px;
  border: 3px solid #efebe8;
}
.slice-viewer-tabsection {
  padding:5px;
  background:#efebe8;
}
.slide-viewer ul {
  list-style: none;
  margin: 0;
}
.scroller ul {
  padding:0;
  white-space:nowrap;
  letter-spacing:-4px;
  left:15px !important;
}
.scroller li {
  letter-spacing:normal;
  display:inline-block;
  width:140px;
}
.scroller li .group {
  height:131px;
  width:120px;
  display:table-cell;
  vertical-align:middle;
}
.scroller li .group .thumbnail {
  display: block;
  text-align:center;
  width: 120px;
  height: 90px;
}
.scroller li .group .thumbnail.audio {
  background:#fff url("../images/icons/searchResultMediaAudio.png") no-repeat center center;
  background-size:90px auto;
}
.scroller li .group .thumbnail.video {
  background:#fff url("../images/icons/searchResultMediaVideo.png") no-repeat center center;
  background-size:90px auto;
}
.scroller li .group .thumbnail.image {
  background:#fff url("../images/icons/searchResultMediaImage.png") no-repeat center center;
  background-size:90px auto;
}
.scroller li .group .thumbnail img {
  margin:0;
  max-width:120px;
  max-height:90px;
  display: inline-block;
}
.previews img {
  max-width: 445px;
  max-height: 323px;
}
.slide-viewer button {
  position:absolute;
  top:0;
  width:24px;
  height:131px;
  text-indent:-999em;
  display:block;
  box-shadow:none;
  -moz-box-shadow:none;
  -webkit-box-shadow:none;
}
.slide-viewer .btn-prev,.slide-viewer .btn-next {
  background:white;
  overflow:hidden;
  outline:none;
}
.slide-viewer .btn-prev.disabled,.slide-viewer .btn-next.disabled {
  cursor:default;
}
.slide-viewer .btn-prev span,.slide-viewer .btn-next span {
  position:absolute;
  left:3px;
  height:24px;
  width:20px;
  background-image:url("../images/icons/objectViewerCarouselControls.png");
}
.slide-viewer .btn-prev span {
  background-position:0 -91px;
}
.slide-viewer .btn-prev.disabled span {
  background-position:0 -151px;
}
.slide-viewer .btn-prev:hover:not(.disabled) span {
  background-position:0 -121px;
}
.slide-viewer .btn-next {
  float:right;
  right:0;
}
.slide-viewer .btn-next span {
  background-position:0 -2px;
}
.slide-viewer .btn-next.disabled span {
  background-position:0 -62px;
}
.slide-viewer .btn-next:hover:not(.disabled) span {
  background-position:0 -32px;
}
.slide-viewer .tabs {
  position:relative;
  height:auto
}
.slide-viewer .fix {
  height:175px;
}
.slide-viewer .tab {
  cursor:pointer;
  float:left;
  color:#3e3a37;
  font-size:1em;
  margin-top:16px;
  line-height: 1em;
}
.slide-viewer .show-divider{
  border-right: #a5003b 2px solid;
  padding-right: 5px;
  margin-right: 5px;
}
.slide-viewer .tab.audios{
  border-right: none;
}
}
</style>
</head>
<body>
  <table border="0" cellpadding="0" cellspacing="0" summary="Print View for items on DDB" width="700px">
    <tr>
      <td><rendering:inlinePng bytes="${logo}" alt="Deutsche Digitale Bibliothek" /></td>
    </tr>
    <tr>
      <td><g:render template="pdfTableInstitution" /></td>
    </tr>
    <tr>
      <td >
        <table border="0" cellpadding="0" cellspacing="0" width="100%" class="item-detail item-content">
          <tr>
            <g:if test="${binaryList}">
              <td style="width: 50%;" class="item-description valign-top"><g:render
                  template="itemdetailsPdf" /></td>
              <td style="width: 50%;" class="valign-top"><g:render template="itemBinariesPdf" /></td>
            </g:if>
            <g:else>
              <td style="width: 100%" class="item-description"><g:render template="itemdetailsPdf" /></td>
            </g:else>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td><g:render template="linkurlPdf" /></td>
    </tr>
  </table>
</body>
</html>