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
<%@page import="de.ddb.common.constants.FacetEnum"%>
<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<div class="objects">
  <g:set var="showPictures" value="${true}" />
  <g:set var="showVideos" value="${true}" />
  <g:set var="showAudios" value="${true}" />
  <g:set var="offset" value="${params[SearchParamEnum.OFFSET.getName()]?.toInteger()}" />
  <g:if test="${!offset}" >
    <g:set var="offset" value="${0}" />
  </g:if>
  <g:set var="rows" value="${params[SearchParamEnum.ROWS.getName()]?.toInteger()}" />
  <g:if test="${!rows}" >
    <g:set var="rows" value="${4}" />
  </g:if>
  <g:set var="nextOffset" value="${offset + rows}" />
  <g:set var="previousOffset" value="${offset - rows}" />
  <g:if test="${previousOffset < 0}">
    <g:set var="previousOffset" value="${0}" />
  </g:if>

  <h3>
    <g:message encodeAs="html" code="ddbnext.Entity_Objects" />: 
  </h3>
  <ddbcommon:renderInfoTooltip messageCode="ddbnext.Entity_Objects_Tooltip" hasArrow="true"/>

  <div class="carousel">
    <div id="items">
    <%--      Items are retrived via Javascript	  	  --%>
    </div>
    <div class="clearfix"></div>
    <div class="entity-controls">
      <a class="previous" id="previous"><span><g:message encodeAs="html" code="ddbnext.Previous_Label" /></span></a>
    </div>
    <div class="entity-controls next">
      <a class="next" id="next"><span><g:message encodeAs="html" code="ddbnext.Next_Label" /></span></a>
    </div>
  </div>

  <div class="media-bar">
    <g:if test="${searchPreview.pictureCount==0}">
      <g:set var="showPictures" value="${false}" />
    </g:if>
    <g:if test="${searchPreview.videoCount==0}">
      <g:set var="showVideos" value="${false}" />
    </g:if>
    <g:if test="${searchPreview.audioCount==0}">
      <g:set var="showAudios" value="${false}" />
    </g:if>
    <g:if test="${showPictures}">
      <g:link controller="search" action="results" params="${[
                (SearchParamEnum.QUERY.getName()): searchPreview.linkQuery.query,
                (SearchParamEnum.FACETVALUES.getName()): [FacetEnum.TYPE_FCT.getName() + "=mediatype_002"],
                (SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()): false
              ]}">
        <g:message encodeAs="html" code="ddbnext.Entity_All_Pictures" /> (${searchPreview.pictureCount})
      </g:link>
    </g:if>
    <g:if test="${(showPictures && showVideos) || (showPictures && showAudios)}">
      |
    </g:if>
    <g:if test="${showVideos}">
      <g:link controller="search" action="results" params="${[
                (SearchParamEnum.QUERY.getName()): searchPreview.linkQuery.query,
                (SearchParamEnum.FACETVALUES.getName()): [FacetEnum.TYPE_FCT.getName() + "=mediatype_005"],
                (SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()): false
              ]}">
        <g:message encodeAs="html" code="ddbnext.Entity_All_Videos" /> (${searchPreview.videoCount})
      </g:link>
    </g:if>
    <g:if test="${(showVideos && showAudios)}">
      |
    </g:if>
    <g:if test="${showAudios}">
      <g:link controller="search" action="results" params="${[
                (SearchParamEnum.QUERY.getName()): searchPreview.linkQuery.query,
                (SearchParamEnum.FACETVALUES.getName()): [FacetEnum.TYPE_FCT.getName() + "=mediatype_001"],
                (SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()): false
              ]}">
        <g:message encodeAs="html" code="ddbnext.Entity_All_Audios" /> (${searchPreview.audioCount})
      </g:link>
    </g:if>
  </div>
</div>