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
               search.query.queryString}" title="${truncateHovercardTitle(title: search.label, length: 350)}">
              <g:truncateItemTitle title="${search.label}" length="${100}"/>
            </a>
          </h2>
          <div>
            <g:set var="facetValues" value=""/>
            <g:each var="mapEntry" in="${search.query.queryMap}">
              <g:if test="${mapEntry.key.startsWith("facetValues")}">
                <g:set var="index" value="${mapEntry.value.indexOf('=')}"/>
                <g:set var="facetName" value="${mapEntry.value.substring(0, index)}"/>
                <g:set var="rawFacetValue" value="${mapEntry.value.substring(index + 1)}"/>
                <g:set var="translatedFacetKey" value="ddbnext.type_fct_${rawFacetValue}"/>
                <g:set var="translatedFacetValue" value="${message(code: translatedFacetKey)}"/>
                <g:set var="facetValue" value="${translatedFacetValue != translatedFacetKey ? translatedFacetValue : rawFacetValue}"/>
                <g:set var="facetValues" value="${facetValues + '; <span style=\"font-weight: bold\">' +
                       message(code: 'ddbnext.facet_' + facetName) + ':</span> ' + facetValue}"/>
              </g:if>
            </g:each>
            <span style="font-weight: bold"><g:message code="ddbnext.Search_term"/>:</span> ${search.query.query}${facetValues}
          </div>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>