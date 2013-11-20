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
<g:set var="upperBound" value="${(facetValues.size()<10)?facetValues.size():10}"></g:set>
<g:set var="i" value="0"></g:set>
<g:each var="i" in="${ (0..<upperBound) }">
  <li>
    <a href="${facetValues[i]['url']}" class="${facetValues[i]['selected']}">
      <span class="count"><ddb:getLocalizedNumber>${facetValues[i]['cnt']}</ddb:getLocalizedNumber></span>
      <g:if test="${facetType == 'affiliate_fct' || facetType == 'keywords_fct' || facetType == 'place_fct' || facetType == 'provider_fct'}">
        <span class="label">${facetValues[i]['fctValue'].encodeAsHTML()}</span>
      </g:if>
      <g:if test="${facetType == 'type_fct' }">
        <span class="label"><g:message code="${'ddbnext.type_fct_'+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == 'time_fct' }">
        <span class="label"><g:message code="${'ddbnext.time_fct_'+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == 'language_fct' }">
        <span class="label"><g:message code="${'ddbnext.language_fct_'+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == 'sector_fct' }">
        <span class="label"><g:message code="${'ddbnext.sector_fct_'+facetValues[i]['fctValue']}" /></span>
      </g:if>
    </a>
	
	<g:if test="${facetType == 'affiliate_fct' && roleFacetsUrl != null && roleFacetsUrl.size() > 0}">			
		<!-- FIXME Due to DDBNEXT-973 we do not show the role facet list -->
		<ul class="role-facets-list off">
		<g:each in="${(roleFacetsUrl)}" var="roleFacet">
			<g:if test="${facetType == roleFacet.parent && roleFacet.fctValue == facetValues[i]['fctValue']}">
				<li class="role-facet">
					<a href="${roleFacet['url']}" class="${roleFacet['selected']}">
						<span class="count"><ddb:getLocalizedNumber>${roleFacet.cnt}</ddb:getLocalizedNumber></span>
						<span class="label"><g:message code="${'ddbnext.facet_'+ roleFacet.field}" /></span>						
					</a>
				</li>
			</g:if>
		</g:each>
		</ul>
	</g:if>
  </li>
</g:each>
