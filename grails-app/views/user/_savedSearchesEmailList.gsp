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
<div style="margin-top:20px; margin-bottom:20px">
  <g:message code="ddbnext.Send_Savedsearches_Email_Body_Pre" args="${[userName]}"/>
  <br/>
</div>
<table border="1" width="100%" style="margin-bottom:20px; border-spacing:0">
  <thead>
    <tr>
      <g:if test="${results.size() == 1}">
        <th style="margin-top:20px"><g:message code="ddbnext.HierarchyHelp_Leaf"/></th>
      </g:if>
      <g:else>
        <th style="margin-top:20px"><g:message code="ddbnext.Entity_Objects"/></th>
      </g:else>
    </tr>
  </thead>
  <tbody>
    <g:each var="search" in="${results}">
      <tr>
        <td height="130px" style="padding: 10px;">
          <h2>
            <a style="color:#a5003b" href="${grailsApplication.config.ddb.favorites.basedomain + '/search?' +
               (search.queryString).encodeAsHTML()}"
               title="${truncateHovercardTitle(title: search.label, length: 350)}">
              <g:truncateItemTitle title="${search.label}" length="${100}"/>
            </a>
          </h2>
          <div>
            <g:set var="facetValues" value=""/>
            <g:each var="mapEntry" in="${search.queryMap}">
              <g:if test="${mapEntry.key == "facetValues[]"}">
                <g:each var="searchQueryTerm" in="${mapEntry.value}">
                  <g:set var="facetName" value="${searchQueryTerm.name}"/>
                  <g:set var="facetValue" value=""/>
                  <g:if test="${searchQueryTerm.values.size() > 0}">
                    <g:each var="rawFacetValue" in="${searchQueryTerm.values}">
                      <g:set var="translatedFacetKey" value="ddbnext.${facetName}_${rawFacetValue}"/>
                      <g:set var="translatedFacetValue" value="${message(code: translatedFacetKey)}"/>
                      <g:set var="facetValue" value="${facetValue + (facetValue != "" ? ", " : "") +
                                  (translatedFacetValue != translatedFacetKey ? translatedFacetValue :
                                  rawFacetValue)}"/>
                    </g:each>
                    <g:set var="facetValues" value="${facetValues + '; <span class=\"bold\">' +
                                message(code: 'ddbnext.facet_' + facetName) + ':</span> ' + facetValue}"/>
                  </g:if>
                </g:each>
              </g:if>
            </g:each>
            <span style="font-weight: bold"><g:message code="ddbnext.Search_term"/>:</span> ${search.query}${facetValues}
          </div>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>