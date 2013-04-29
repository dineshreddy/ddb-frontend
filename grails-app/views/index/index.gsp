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
<title><g:message code="ddbnext.Homepage"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

<meta name="page" content="index" />
<meta name="layout" content="main" />

<r:require module="startpage"/>
</head>
<body>
  <div class="row">
    <div class="span12 search-widget">
      <h1 class="invisible-but-readable"><g:message code="ddbnext.Heading_Search_Widget"/></h1>
      <div class="row">
        <img src="${resource(dir: 'images', file: 'logo_big.png')}" class="bigLogo" alt="<g:message code="ddbnext.Logo_Description"/>" title="<g:message code="ddbnext.Logo_Title"/>" />
      </div>
      <div class="row">
        <div class="span12">
          <g:form method="get" role="search" id="form-search" url="[controller:'search', action:'results']">
            <label> 
              <span><g:message code="ddbnext.Search_text_field"/></span>
            </label>
            <input type="search" class="query" name="query" value="" />
            <button type="submit"><g:message code="ddbnext.Go_Button"/></button>
            <span class="contextual-help hidden-phone hidden-tablet" 
                  title="<g:message code="ddbnext.Search_Hint" 
                  args="${[('<a href="' + createLink(controller:"content", params:[dir:'help', id:'search-simple']) + '">').encodeAsHTML(),('</a>').encodeAsHTML()]}" 
                  default="ddbnext.Search_Hint"/>" 
                  data-content="<g:message code="ddbnext.Search_Hint" 
                  args="${[('<a href="' + createLink(controller:"content", params:[dir:'help', id:'search-simple']) + '">').encodeAsHTML(),('</a>').encodeAsHTML()]}" 
                  default="ddbnext.Search_Hint"/>">
            </span>
            <div class="tooltip off"></div>
            <div class="link-adv-search"> 
              <g:link class="fl" controller="advancedsearch"><g:message code="ddbnext.Advanced_search"/></g:link>
            </div>
          </g:form>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="span12 teaser">
      <noscript>
        <g:if test="${articles}">
          <g:each in="${articles}">
            <div class="span3">
              <a href="${request.contextPath}${it.uri}" title="${it.title}">
                <img class="article" src="${staticUrl}${it.src}" alt="${it.title}"/>
              </a>
              <div class="caption">
                <a href="${request.contextPath}${it.uri}" title="${it.title}">${it.title}</a>
              </div>
            </div>
          </g:each>
      </g:if>
      </noscript>
      <div class="carousel">
        <div id="articles">
          <g:if test="${articles}">
            <g:each in="${articles}">
              <div class="article">
                <a href="${request.contextPath}${it.uri}" title="${it.title}" target="_self">
                  <img src="${staticUrl}${it.src}" alt="${it.title}" />
                </a>
                <div class="caption">
                  <a href="${request.contextPath}${it.uri}" title="${it.title}">${it.title}</a>
                </div>
              </div>
            </g:each>
          </g:if>
        </div>
        <div class="clearfix"></div>
        <a class="previous" id="articles-prev" href=""><span><g:message code="ddbnext.Homepage_Carousel_Previous"/></span></a>
        <a class="next" id="articles-next" href=""><span><g:message code="ddbnext.Homepage_Carousel_Next"/></span></a>
      </div>
    </div>
  </div>
</body>
</html>
