<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<div class="searchSuggestion">
  <div>
    <g:message encodeAs="html" code="ddbnext.Search_Suggestion"/>
   <a href="${createLink(controller="search",action: 'results', params:[(SearchParamEnum.QUERY.getName()): correctedQuery])}" class="subtitle">${correctedQuery}</a> 
  </div>
</div>
