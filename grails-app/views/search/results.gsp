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
<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<%@page import="de.ddb.common.constants.FacetEnum"%>
<%@page import="de.ddb.common.constants.Type"%>
<%@page import="de.ddb.next.SearchFacetLists"%>

<g:set var="nonJsFacetsList" value="${SearchFacetLists.itemSearchNonJavascriptFacetList}"></g:set>
<g:set var="jsFacetsList" value="${SearchFacetLists.itemSearchJavascriptFacetList}"></g:set>

<html>
<head>
<title>${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
<meta name="page" content="results" />
<meta name="layout" content="main" />
</head>

<body>
  <h1 class="invisible-but-readable">
    <g:message encodeAs="html" code="ddbnext.Search_results_objects" />
  </h1>
  <div class="row search-results-container">

    <div class="span3 facets-container hidden-phone">
      <div class="facets-head">
        <div class="facet-head-text">
          <g:message encodeAs="html" code="ddbnext.SearchResultsFacetHeading_Filter_Results" />
        </div>
        <ddbcommon:renderInfoTooltip messageCode="ddbnext.SearchResultsFacetHeading_TooltipContent"
                                     infoDir="hilfe/suchergebnisse-filtern" hasArrow="true"/>
      </div>
      
	    <%-- Shows the facets supported in the NON JS version--%>
      <noscript>
	      <div class="facets-list bt bb">
	        <g:each in="${nonJsFacetsList}" var="mit">
	          <g:each in="${(facets.selectedFacets)}">
	            <g:if test="${mit == it.field}">
	              <div class="facets-item ${(it.facetValues.size() > 0)?'active':'' } bt bb bl br">
	                <a class="h3" href="${facets.mainFacetsUrl[it.field].encodeAsHTML()}" data-fctName="${it.field}"><g:message encodeAs="html" code="ddbnext.facet_${it.field}" /></a>
	                <g:if test="${it.facetValues.size() > 0}">
	                  <ul class="unstyled">
	                    <ddb:renderFacetList facetValues="${facets.subFacetsUrl[it.field]}" facetType="${it.field}"></ddb:renderFacetList>
	                  </ul>
	                </g:if>
	              </div>
	            </g:if>
	          </g:each>
	        </g:each>
	      </div>
	    </noscript>

    <%-- Shows the facets supported in the JS version. --%>
    <div class="js facets-list bt bb off">
      <%-- TimeFacet is handle by its own template --%>
      <g:render template="timeFacet" />
      <%-- All other facets are handled in the same way --%>
      <ddb:renderFacets jsFacetsList="${jsFacetsList}"></ddb:renderFacets>
    </div>

    <div class="keep-filters off">
      <label>
        <g:set var="keepFiltersChecked"
               value="${ddbcommon.getCookieFieldValue(fieldname: SearchParamEnum.KEEPFILTERS.name).toBoolean()}"/>
        <input id="keep-filters" type="checkbox" name="keepFilters" ${keepFiltersChecked ? checked="checked" : ""}/>
        <g:message code="ddbnext.Keep_filters"/>
      </label>
    </div>

      <div class="clear-filters">
        <a href="${clearFilters.encodeAsHTML()}">
            <g:message encodeAs="html" code="ddbnext.Clear_filters"/>
        </a>
      </div>
      <div class="compare-objects bt br bb bl off">
        <div class="compare-header">
          <b><g:message encodeAs="html" code="ddbnext.SearchResultsCompareObjects"/></b>
          <ddbcommon:renderInfoTooltip messageCode="ddbnext.Compare_Tooltip" hasArrow="true"/>
        </div>
        <div class="compare-main">
          <div id="compare-object1" class="compare-object bt br bb bl">
            <div class="compare-table">
              <span class="compare-default-pic"></span>
              <span class="compare-default"><g:message encodeAs="html" code="ddbnext.SearchResultsChooseObject1" /></span>
              <a class="compare-link">
                <span class="compare-text"></span>
                <img class="compare-img" alt="" src="${request.contextPath}/images/bg/compare_icon.png"/>
              </a>
              <span data-index="1" class="comparison-cancel-button"></span>
            </div>
          </div>
          <div id="compare-object2" class="compare-object bt br bb bl">
            <div class="compare-table">
              <span class="compare-default-pic"></span>
              <span class="compare-default"><g:message encodeAs="html" code="ddbnext.SearchResultsChooseObject2" /></span>
              <a class="compare-link">
                <span class="compare-text"></span>
                <img class="compare-img" alt="" src="${request.contextPath}/images/bg/compare_icon.png"/>
              </a>
              <span data-index="2" class="comparison-cancel-button"></span>
            </div>
          </div>
        </div>
        <div class="compare-footer bt bb bl br">
            <a id="compare-button">
                <div class="button">
                    <g:message encodeAs="html" code="ddbnext.SearchResultsStartComparison"/>
                </div>
            </a>
        </div>
      </div>
    </div>

    <div class="span9">
      <div class="off result-pages-count"><ddb:getLocalizedNumber>${totalPages}</ddb:getLocalizedNumber></div>
      <ddb:renderSearchTabulation totalResults="${numberOfResultsFormatted}"
                                  query="${title}"
                                  active="${Type.CULTURAL_ITEM.getName()}"
                                  rows="${resultsPaginatorOptions.pageFilterSelected}"/>
      <div style="clear:both;"> 
      
      <%--   Search has results   --%>
      <div class="search-results-content<g:if test="${results.numberOfResults == 0}"> off</g:if>">
        <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex,
                                           numberOfResults: numberOfResultsFormatted,
                                           page: page,
                                           totalPages: totalPages,
                                           paginationURL: paginationURL,
                                           tabulatorActive: Type.CULTURAL_ITEM.getName()]}"
                               paginatorOptions="${resultsPaginatorOptions}" paginatorViewSwitch="${true}"/>
              
        <g:if test="${correctedQuery!='null'}">
            <g:if test="${correctedQuery}">
              <ddb:renderSearchSuggestion correctedQuery="${correctedQuery}" />
            </g:if>
        </g:if>
        
        <div class="row">
          <div class="span9">
            <div class="search-results">
              <div class="search-results-list">
                <g:if test="${results}">
                  <ddb:renderSearchResultsList results="${results.results["docs"]}" entities="${entities}" />
                </g:if>
              </div>
            </div>
          </div>
        </div>
            
        <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex,
                                           numberOfResults: numberOfResultsFormatted,
                                           page: page,
                                           totalPages: totalPages,
                                           paginationURL:paginationURL]}"/>
      </div>

      <%-- favorite confirmation dialog - only one per page --%>
      <g:render template="../common/addToFavorites"/>

    </div>
      <%--   Search has NO results   --%>
      <div class="search-noresults-content <g:if test="${results.numberOfResults != 0}">off</g:if>">
        <g:if test="${correctedQuery!='null'}">
          <g:if test="${correctedQuery}">
            <ddb:renderSearchSuggestion correctedQuery="${correctedQuery}" />
          </g:if>
        </g:if>
        <g:render template="noResults" />
      </div>
    </div>
  </div>
</body>
</html>
