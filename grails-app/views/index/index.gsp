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
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>

<g:set var="config" bean="configurationService"/>

<html>
<head>
<title><g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek_Title"/></title>
<meta name="page" content="index" />
<meta name="description" content="${g.message(code: "ddbnext.Meta_Description")}"/>
<meta name="layout" content="main" />
<link rel="canonical" href="${createLink(controller: 'index', base: domainCanonic)}"/>
<r:require module="startpage"/>
</head>
<body>
  <div class="row">
    <div class="span12 search-widget">
      <div class="row">
        <img src="${resource(dir: 'images', file: 'logoBig.png')}" class="bigLogo" alt="${g.message(code:"ddbnext.Logo_Description")}"/>
      </div>
      <div class="row">
        <div class="span12">
          <g:form method="get" role="search" id="form-search" url="[controller:'search', action:'results']">
            <div class="link-stats">
              <g:link class="fl" controller="content" action="faq" fragment="189"
                      title="${g.message(code: "ddbnext.Homepage_Statistics_Whats_Offered")}">
                <g:message args='["${String.format(RequestContextUtils.getLocale(request),'%,d', stats.total)}"]'
                           code="ddbnext.Homepage_Statistics_Total"/>
                <g:message args='["${String.format(RequestContextUtils.getLocale(request),'%,d', stats.withDigitizedMedia)}"]'
                           code="ddbnext.Homepage_Statistics_With_Digitized_Media"/>
              </g:link>
            </div>
            <label id="search-label">
              <span><g:message encodeAs="html" code="ddbnext.Search_text_field"/></span>
            </label>
            <input type="hidden" name="isThumbnailFiltered" value="${config.isOnlyWithThumbnailsFeaturesEnabled()}"/>
            <input type="search" class="query" name="query" <%-- autocomplete="off" --%> value="" aria-label="Search"/>
            <button type="submit">
              <!--[if !IE]><!-->
                <g:message encodeAs="html" code="ddbnext.Go_Button"/>
              <!--<![endif]-->
              <!--[if gt IE 8]>
                <g:message encodeAs="html" code="ddbnext.Go_Button"/>
              <![endif]-->
            </button>
            <div class="fl thumbnail-filter-container">
              <label title="${g.message(code: "ddbnext.Show_items_with_thumbnails")}">
                <input type="checkbox" ${config.isOnlyWithThumbnailsFeaturesEnabled() ? "checked" : ""}>
                <g:message code="ddbnext.Show_items_with_thumbnails"/>
              </label>
            </div>
            <div class="link-adv-search fr">
              <g:link controller="advancedsearch"><g:message encodeAs="html" code="ddbnext.AdvancedSearch"/></g:link>
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
              <a href="${it.uri}" title="${it.caption}">
                <img class="article" src="${it.imageUri}" alt="${it.caption}"/>
              </a>
              <div class="caption">
                <a href="${it.uri}" title="${it.caption}">${it.caption}</a>
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
                <a href="${it.uri}" title="${it.caption}" target="_self">
                  <img src="${it.imageUri}" alt="${it.caption}" />
                </a>
                <div class="caption">
                  <a href="${it.uri}" title="${it.caption}">${it.caption}</a>
                </div>
              </div>
            </g:each>
          </g:if>
        </div>
        <div class="clearfix"></div>
        <a class="previous" id="articles-prev" href="" role="button"><span><g:message encodeAs="html" code="ddbnext.Homepage_Carousel_Previous"/></span></a>
        <a class="next" id="articles-next" href="" role="button"><span><g:message encodeAs="html" code="ddbnext.Homepage_Carousel_Next"/></span></a>
      </div>
    </div>
  </div>
</body>
</html>
