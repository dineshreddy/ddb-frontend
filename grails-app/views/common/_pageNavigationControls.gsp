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
<g:if test="${navData.paginationURL.firstPg == null}">
  <g:set var="displayLeftPagination" value="off"></g:set>
  <g:set var="enableLeftPagination" value="${false}"></g:set>
</g:if>
<g:else>
  <g:set var="enableLeftPagination" value="${true}"></g:set>
</g:else>
<g:if test="${navData.paginationURL.lastPg == null}">
  <g:set var="displayRightPagination" value="off"></g:set>
  <g:set var="enableRightPagination" value="${false}"></g:set>
</g:if>
<g:else>
  <g:set var="enableRightPagination" value="${true}"></g:set>
</g:else>
<div class="page-nav">
  <ul class="inline">
    <li class="first-page ${displayLeftPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.firstPg.encodeAsHTML()}"><g:message code="ddbnext.First_Label" /></a>  
    </li>
    <li class="prev-page br ${displayLeftPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.prevPg.encodeAsHTML()}"><g:message code="ddbnext.Previous_Label" /></a> 
    </li>
    <li class="pages-overall-index">
      <span>
          <g:message code="ddbnext.Page" /> 
          <input type="text" class="page-input off" maxlength="10" value="${navData.page}"/>
          <span class="page-nonjs">${navData.page}</span> 
          <g:message code="ddbnext.Of" /> 
          <span class="total-pages"><ddb:getLocalizedNumber>${navData.totalPages}</ddb:getLocalizedNumber></span>
      </span>
    </li>
    <li class="next-page bl ${displayRightPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.nextPg.encodeAsHTML()}"><g:message code="ddbnext.Next_Label" /></a> 
    </li>
    <li class="last-page ${displayRightPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.lastPg.encodeAsHTML()}"><g:message code="ddbnext.Last_Label" /></a> 
    </li>
  </ul>
</div>
<div class="page-nav-mob">
  <ul class="inline">
    <li class="prev-page bl">
      <g:if test="${enableLeftPagination}">
        <a class="page-nav-result noclickfocus" href="${navData.paginationURL.prevPg.encodeAsHTML()}"><div><span><g:message code="ddbnext.Previous_Label" /></span></div></a>
        <div class="disabled-arrow off"></div> 
      </g:if>
      <g:else>
        <a class="page-nav-result noclickfocus off" href="${navData.paginationURL.prevPg.encodeAsHTML()}"><div><span><g:message code="ddbnext.Previous_Label" /></span></div></a>
        <div class="disabled-arrow"></div>
      </g:else>
    </li>
    <li class="next-page bl">
      <g:if test="${enableRightPagination}">
        <a class="page-nav-result noclickfocus" href="${navData.paginationURL.nextPg.encodeAsHTML()}"><div><span><g:message code="ddbnext.Next_Label" /></span></div></a>
        <div class="disabled-arrow off"></div>
      </g:if>
      <g:else>
        <a class="page-nav-result noclickfocus off" href="${navData.paginationURL.nextPg.encodeAsHTML()}"><div><span><g:message code="ddbnext.Next_Label" /></span></div></a>
        <div class="disabled-arrow"></div>
      </g:else> 
    </li>
  </ul>
</div>