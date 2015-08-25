<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<g:set var="config" bean="configurationService"/>
<g:set var="suggestionEnabled" value="${config.isSearchSuggestionFeaturesEnabled()}"/>
<div class="searchSuggestion bt ${suggestionEnabled ? "" : "off"}">
  <div>
    <g:message encodeAs="html" code="ddbnext.Search_Suggestion"/>
   <a href="${createLink(controller="search",action: 'results', params:[(SearchParamEnum.QUERY.getName()): correctedQuery])}" class="subtitle">${correctedQuery}</a> 
  </div>
</div>
