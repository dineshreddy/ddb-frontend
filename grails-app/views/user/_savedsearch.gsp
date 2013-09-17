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
<ul class="results-list unstyled" id="slaves">
  <g:set var="index" value="${0}"/>
  <g:each var="search" in="${results}">
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span7">
          <input type="checkbox" name="id[${index++}]" value="${search.id}" class="remove-item-check">
          <div class="summary-main">
            <h2 class="title">
              <a class="persist" href="${request.contextPath + '/search?' + (search.query.queryString).encodeAsHTML()}" title="${truncateHovercardTitle(title: search.label, length: 350)}">
                <g:truncateItemTitle title="${search.label}" length="${100}"/>
              </a>
            </h2>
            <div class="subtitle">
              <g:set var="facetValues" value=""/>
              <g:each var="mapEntry" in="${search.query.queryMap}">
                <g:if test="${mapEntry.key.startsWith("facetValues")}">
                  <g:set var="index" value="${mapEntry.value.indexOf('=')}"/>
                  <g:set var="facetName" value="${mapEntry.value.substring(0, index)}"/>
                  <g:set var="rawFacetValue" value="${mapEntry.value.substring(index + 1)}"/>
                  <g:set var="translatedFacetKey" value="ddbnext.type_fct_${rawFacetValue}"/>
                  <g:set var="translatedFacetValue" value="${message(code: translatedFacetKey)}"/>
                  <g:set var="facetValue" value="${translatedFacetValue != translatedFacetKey ? translatedFacetValue : rawFacetValue}"/>
                  <g:set var="facetValues" value="${facetValues + '; <span class=\"bold\">' + message(code: 'ddbnext.facet_' + facetName) + ':</span> ' + facetValue}"/>
                </g:if>
              </g:each>
              <span class="bold"><g:message code="ddbnext.Search_term"/>:</span> ${search.query.query}${facetValues}
            </div>
          </div>
        </div>
        <div class="span2 created-at">
          <div><g:formatDate format="dd.MM.yyyy HH:mm" date="${search.creationDate}"/></div>
        </div>
      </div>
    </li>
  </g:each>
</ul>