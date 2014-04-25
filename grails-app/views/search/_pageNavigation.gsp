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
<div class="page-info-nav <g:if test='${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}'>grid</g:if>">
  <ddb:renderPaginationControls navData="${navData}"></ddb:renderPaginationControls>
  <g:if test="${paginatorOptions != null}">
    <ddb:renderResultsPaginatorOptions paginatorData="${paginatorOptions}"></ddb:renderResultsPaginatorOptions>
  </g:if>
</div>