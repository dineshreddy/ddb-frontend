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
<div class="page-info-nav <g:if test='${viewType == 'grid'}'>grid</g:if>">
  <div class="page-info">
    <span class="results-overall-index">${navData.resultsOverallIndex} </span> 
    <span><g:message code="ddbnext.Of" /> </span> 
    <span><strong><span class="results-total">${numberOfResultsFormatted}</span></strong> </span> 
    <g:if test="${numberOfResultsFormatted == '1'}"> 
        <span id="results-label"><g:message code="ddbnext.Result_lowercase" /></span>
    </g:if>
    <g:else>
        <span id="results-label"><g:message code="ddbnext.Results_lowercase" /></span>
    </g:else>
  </div>
  <g:paginationControlsRender navData="${navData}"></g:paginationControlsRender>
</div>