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

<g:set var="facetValues" value="${new HashMap()}" />
<g:set var="isThumbnailFiltered" value="${search.queryMap[SearchParamEnum.IS_THUMBNAILS_FILTERED.name]}"/>

<g:each var="mapEntry" in="${search.queryMap}">
  <g:if test="${mapEntry.key == SearchParamEnum.FACETVALUES.getName()}">
    <g:each var="searchQueryTerm" in="${mapEntry.value}">
      <g:set var="facetName" value="${searchQueryTerm.name}" />
      <g:set var="facetValue" value="" />
      <g:if test="${searchQueryTerm.values.size() > 0}">
        <g:each var="rawFacetValue" in="${searchQueryTerm.values}">
          <g:set var="translatedFacetKey"
            value="ddbnext.${facetName}_${rawFacetValue}" />
          <g:set var="translatedFacetValue"
            value="${message(code: translatedFacetKey)}" />
          <g:set var="facetValue"
            value="${facetValue + (facetValue != "" ? ", " : "") +
                                  (translatedFacetValue != translatedFacetKey ? translatedFacetValue :
                                  rawFacetValue)}" />
        </g:each>
        ${facetValues.put(facetName, facetValue)}
      </g:if>
    </g:each>
  </g:if>
</g:each>
<span><b><g:message encodeAs="html" code="ddbnext.Search_term" />:</b></span>
${(search.query != null ? search.query : "*")}<g:each var="facetValue" in="${facetValues}">
  ;
  <span>
    <b>${message(code: 'ddbnext.facet_' + facetValue.key)}:</b>
  </span>
  ${facetValue.value}
</g:each><g:if test="${search.queryMap["time"]}">
  ;
  <span>
    <b>${message(code: 'ddbnext.facet_time')}:</b>
  </span>
  ${search.queryMap["time"][0].name}
</g:if><g:if test="${isThumbnailFiltered && isThumbnailFiltered[0].name.toBoolean()}">;
  <b>${message(code: 'ddbnext.Show_items_with_thumbnails')}</b>
</g:if>