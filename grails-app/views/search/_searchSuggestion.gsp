<%@page import="de.ddb.next.constants.SearchParamEnum"%>
<div class="searchSuggestion">
  <div>
    <g:message code="ddbnext.Search_Suggestion"/>
   <a href="${createLink(controller="search",action: 'results', params:[(SearchParamEnum.QUERY.getName()): correctedQuery])}" class="subtitle">${correctedQuery}</a> 
  </div>
</div>