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
<g:set var="nonJsFacetsList" value="${[]}"></g:set>
<g:set var="jsFacetsList" value="${["Name", "Beruf", FacetEnum.PLACE.getName()]}"></g:set>
<html>
<head>
<title>
  ${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
<meta name="page" content="searchperson" />
<meta name="layout" content="main" />
</head>
<body>
  <div class="row search-results-container entities">
    <div class="span3 facets-container hidden-phone">
      <div class="facets-head">
        <h3>
          <g:message encodeAs="html" code="ddbnext.SearchResultsFacetHeading_Filter_Results" />
        </h3>
      </div>
      <%-- Shows the facets supported in the NON JS version--%>
      <noscript>
        <div class="facets-list bt">
          <g:each in="${nonJsFacetsList}" var="mit">
            <g:each in="${(facets.selectedFacets)}">
              <g:if test="${mit == it.field}">
                <div class="facets-item ${(it.facetValues.size() > 0)?'active':'' } bt bb bl br">
                  <a class="h3" href="${facets.mainFacetsUrl[it.field].encodeAsHTML()}"
                    data-fctName="${it.field}"><g:message encodeAs="html" code="ddbnext.facet_${it.field}" /></a>
                  <g:if test="${it.facetValues.size() > 0}">
                    <ul class="unstyled">
                      <ddb:renderFacetList facetValues="${facets.subFacetsUrl[it.field]}"
                        facetType="${it.field}"></ddb:renderFacetList>
                    </ul>
                  </g:if>
                </div>
              </g:if>
            </g:each>
          </g:each>
        </div>
      </noscript>
      <%-- Shows the facets supported in the JS version. --%>
      <div class="js facets-list bt off">
        <ddb:renderFacets jsFacetsList="${jsFacetsList}"></ddb:renderFacets>
      </div>
      <div class="clear-filters">
        <a href="${clearFilters.encodeAsHTML()}"><g:message encodeAs="html" code="ddbnext.Clear_filters" /></a>
      </div>
    </div>
    <div class="span9 search-noresults-content <g:if test="${results.numberOfResults != 0}">off</g:if>">
      <g:if test="${correctedQuery!='null'}">
        <g:if test="${correctedQuery}">
          <ddb:renderSearchSuggestion correctedQuery="${correctedQuery}" />
        </g:if>
      </g:if>
      <div class="noresults">
        <div>
          <g:message encodeAs="none" code="ddbnext.No_results_found_for_the_search" />
        </div>
      </div>
    </div>
    <div class="span9 search-results-content <g:if test="${results.numberOfResults == 0}">off</g:if>">
      <div class="off result-pages-count">
        ${totalPages}
      </div>
            <ddb:renderSearchTabulation totalResults="${results.totalResults}" query="${title}" active="person" />
      <%-- 
      <ddb:renderResultsPaginatorOptions paginatorData="${resultsPaginatorOptions}" />

      <ddb:renderPageInfoNav navData="${[resultsOverallIndex: resultsOverallIndex, numberOfResults: numberOfResultsFormatted, page: page, totalPages: totalPages, paginationURL: paginationURL]}" />
--%>
      <div class="row">
        <div class="span9">
          <div class="results-paginator-view">
            <div class="group-actions">
              <strong>1</strong> <strong>2</strong> <strong>3</strong> <strong>4</strong> <strong>5</strong> <strong>Weiter</strong>
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
                <g:render template="entityResultsList" model="${[entities: results]}" />
              </g:if>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
