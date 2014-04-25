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

<g:set var="nonJsFacetsList" value="${[FacetEnum.PLACE.getName(), FacetEnum.AFFILIATE.getName(), FacetEnum.KEYWORDS.getName(), FacetEnum.LANGUAGE.getName(), FacetEnum.TYPE.getName(), FacetEnum.SECTOR.getName(), FacetEnum.PROVIDER.getName()]}"></g:set>
<g:set var="jsFacetsList" value="${[FacetEnum.PLACE.getName(), FacetEnum.AFFILIATE_ROLE.getName(), FacetEnum.KEYWORDS.getName(), FacetEnum.LANGUAGE.getName(), FacetEnum.TYPE.getName(), FacetEnum.SECTOR.getName(), FacetEnum.PROVIDER.getName(), FacetEnum.LICENSE_GROUP.getName()]}"></g:set>

<html>
<head>
<title>${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

<meta name="page" content="results" />
<meta name="layout" content="main" />

</head>

<body>
  <div class="row search-results-container">
  
    <div class="span3 facets-container hidden-phone">
      <div class="facets-head">
        <h3><g:message encodeAs="html" code="ddbnext.SearchResultsFacetHeading_Filter_Results" /></h3>
        <ddb:renderInfoTooltip messageCode="ddbnext.SearchResultsFacetHeading_TooltipContent" infoId="search-filters" infoDir="help" hasArrow="true"/>
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
      <div class="facets-item bt bb bl br">
        <input id="thumbnail-filter" type="checkbox" <g:if test='${isThumbnailFiltered == 'true'}'>checked</g:if>>
        <label for="thumbnail-filter" title="<g:message encodeAs="html" code="ddbnext.Show_items_with_thumbnails" />"><g:message encodeAs="html" code="ddbnext.Show_items_with_thumbnails" /></label>
      </div>
    </div>

      <div class="clear-filters">
        <a href="${clearFilters.encodeAsHTML()}">
            <g:message encodeAs="html" code="ddbnext.Clear_filters"/>
        </a>
      </div>

      <ddb:isLoggedIn>
        <div id="addToSavedSearches">
          <div class="add-to-saved-searches"></div>
          <a id="addToSavedSearchesAnchor"><g:message encodeAs="html" code="ddbnext.Save_Savedsearch"/></a>
          <span id="addToSavedSearchesSpan" class="off"><g:message encodeAs="html" code="ddbnext.Saved_Savedsearch"/></span>
        </div>
      
        <div id="addToSavedSearchesModal" class="modal hide fade" tabindex="-1" role="dialog"
           aria-labelledby="addToSavedSearchesLabel" aria-hidden="true">
          <div class="modal-header">
            <span title="<g:message encodeAs="html" code="ddbnext.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
            <h3 id="addToSavedSearchesLabel">
              <g:message encodeAs="html" code="ddbnext.Save_Savedsearch"/>
            </h3>
          </div>
          <div class="modal-body">
            <div><b><g:message encodeAs="html" code="ddbnext.Mandatory"/></b></div>
            <br/>
            <div><g:message encodeAs="html" code="ddbnext.Savedsearch_Title"/>*</div>
            <div><input id="addToSavedSearchesTitle" type="text"></div>
          </div>
          <div class="modal-footer">
            <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
              <g:message encodeAs="html" code="ddbnext.Close"/>
            </button>
            <button class="btn-padding" type="submit" id="addToSavedSearchesConfirm">
              <g:message encodeAs="html" code="ddbnext.Save"/>
            </button>
          </div>
        </div>
      </ddb:isLoggedIn>
      
      <div class="compare-objects bt br bb bl off">
        <div class="compare-header">
          <b><g:message encodeAs="html" code="ddbnext.SearchResultsCompareObjects"/></b>
          <ddb:renderInfoTooltip messageCode="ddbnext.Compare_Tooltip" hasArrow="true"/>
        </div>
        <div class="compare-main">
          <div id="compare-object1" class="compare-object bt br bb bl">
            <div class="compare-table">
              <span class="compare-default-pic"></span>
              <span class="compare-default"><g:message encodeAs="html" code="ddbnext.SearchResultsChooseObject1" /></span>
              <a class="compare-link">
                <span class="compare-text"></span>
                <img class="compare-img" src="#" alt="" />
              </a>
              <span data-index="1" class="fancybox-toolbar-close"></span>
            </div>
          </div>
          <div id="compare-object2" class="compare-object bt br bb bl">
            <div class="compare-table">
              <span class="compare-default-pic"></span>
              <span class="compare-default"><g:message encodeAs="html" code="ddbnext.SearchResultsChooseObject2" /></span>
              <a class="compare-link">
                <span class="compare-text"></span>
                <img class="compare-img" src="#" alt="" />
              </a>
              <span data-index="2" class="fancybox-toolbar-close"></span>
            </div>
          </div>
        </div>
        <div class="compare-footer bt bb bl br">
            <a id="compare-button">
                <div>
                    <g:message encodeAs="html" code="ddbnext.SearchResultsStartComparison"/>
                </div>
            </a>
        </div>
      </div>
    </div>
    
    <div class="span9 search-noresults-content <g:if test="${results.numberOfResults != 0}">off</g:if>">
      <g:if test="${correctedQuery!='null'}">
        <g:if test="${correctedQuery}">
          <ddb:renderSearchSuggestion correctedQuery="${correctedQuery}" />
        </g:if>
      </g:if>
      <g:render template="noResults" />
    </div>
    <div class="span9 search-results-content <g:if test="${results.numberOfResults == 0}">off</g:if>">
      <div class="off result-pages-count">${totalPages}</div>
    
      <ddb:renderResultsPaginatorOptions paginatorData="${resultsPaginatorOptions}" />
      
      <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResultsFormatted, page: page, totalPages: totalPages, paginationURL: paginationURL]}" />
      
      <div class="row">
        <div class="span9">
          <div class="results-paginator-view off">
            <div class="group-actions">
              <%-- HLA: deactivated until there is the possibility to cluster results (See DDBNEXT-328) 
              <input class="disabled" id="toggle-cluster" type="checkbox" disabled="disabled">
              <label class="disabled" for="toggle-cluster" title="<g:message encodeAs="html" code="ddbnext.View_as_Cluster" />"><g:message encodeAs="html" code="ddbnext.View_as_Cluster" /></label>
              --%>
            </div>
            <div class="view-type-switch">
              <!--[if lt IE 9]>
              <div class="ie8-version">
              <![endif]-->
              <div>
                <button id="view-list" type="button" class="<g:if test='${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}'>selected</g:if>" title="<g:message encodeAs="html" code="ddbnext.View_as_List" />"><g:message encodeAs="html" code="ddbnext.View_as_List" /></button>
              </div>
              <!--[if lt IE 9]>
              </div>
              <div class="ie8-version">
              <![endif]-->
              <div>
                <button id="view-grid" type="button" class="<g:if test='${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}'>selected</g:if>" title="<g:message encodeAs="html" code="ddbnext.View_as_Grid" />"><g:message encodeAs="html" code="ddbnext.View_as_Grid" /></button>
              </div>
              <!--[if lt IE 9]>
              </div>
              <![endif]-->
            </div>
          </div>
        </div>
      </div>
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
      <div id="print-nav">
        <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResultsFormatted, page: page, totalPages: totalPages, paginationURL:paginationURL]}" />
      </div>
    </div>
  </div>
</body>
</html>
