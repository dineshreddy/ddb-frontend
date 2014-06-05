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
<%@page import="de.ddb.common.constants.Type"%>
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
    <li class="prev-page ${displayLeftPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.prevPg}"><g:message encodeAs="html" code="ddbnext.Previous_Label" /></a> 
    </li>
    <li class="pages-overall-index">
      <span>
        <g:each in="${navData.paginationURL.pages}">
            <a <g:if test="${!it.active}">href="${it.url}"</g:if> class="page-nav-result <g:if test="${it.active}">active</g:if>">
                ${it.pageNumber}
            </a>
        </g:each>
      </span>
    </li>
    <li class="next-page ${displayRightPagination}">
      <a class="page-nav-result noclickfocus" href="${navData.paginationURL.nextPg}"><g:message encodeAs="html" code="ddbnext.Next_Label" /></a> 
    </li>
    <li class="extra-controls <g:if test="${navData.totalPages<6}">off</g:if>">
        <div>
          <div class="arrow-container">
              <div class="arrow-up"></div>
          </div>
          <ul>
            <li class="first-page ${displayLeftPagination}">
              <a class="page-nav-result noclickfocus" href="${navData.paginationURL.firstPg}"><g:message encodeAs="html" code="ddbnext.First_Page_Label" /></a>  
            </li>
            <li class="last-page ${displayRightPagination}">
              <a class="page-nav-result noclickfocus" href="${navData.paginationURL.lastPg}"><g:message encodeAs="html" code="ddbnext.Last_Page_Label" /></a> 
            </li>
            <li>
              <span>
                  <g:message encodeAs="html" code="ddbnext.Go_To_Page" /> 
                  <input type="text" class="page-input off" maxlength="10" value="${navData.page}"/>
                  <span class="page-nonjs">${navData.page}</span> 
                  <g:message encodeAs="html" code="ddbnext.Of" /> 
                  <span class="total-pages"><ddb:getLocalizedNumber>${navData.totalPages}</ddb:getLocalizedNumber></span>
                  <span class="go-to-page"></span>
              </span>
            </li>
          </ul>
        </div>
    </li>
  </ul>
</div>
<div class="page-nav-mob">
  <g:if test="${navData.tabulatorActive!=null}">
    <div>
      <select class="type-selection">
        <option value="${createLink(controller: 'search', action: 'results')}" <g:if test="${navData.tabulatorActive==Type.CULTURAL_ITEM.getName()}">selected</g:if>><g:message code="ddbnext.Entity_Objects" /></option>
        <option value="${createLink(controller: 'entity', action: 'personsearch')}" <g:if test="${navData.tabulatorActive==Type.ENTITY.getName()}">selected</g:if>><g:message code="ddbnext.entity.tabulator.persons" /></option>
        <option value="${createLink(controller: 'search', action: 'institution')}" <g:if test="${navData.tabulatorActive==Type.INSTITUTION.getName()}">selected</g:if>><g:message code="ddbnext.Institutions" /></option>
      </select>
    </div>
  </g:if>
  <div class="page-info">
    <span class="results-overall-index">${navData.resultsOverallIndex}</span> 
    <span> / </span> 
    <span><strong><span class="results-total"><ddb:getLocalizedNumber>${navData.totalPages}</ddb:getLocalizedNumber></span></strong> </span> 
  </div>
  <ul class="inline">
    <li class="prev-page bl">
      <g:if test="${enableLeftPagination}">
        <a class="page-nav-result noclickfocus" href="${navData.paginationURL.prevPg}"><div><span><g:message encodeAs="html" code="ddbnext.Previous_Label" /></span></div></a>
        <div class="disabled-arrow off"></div> 
      </g:if>
      <g:else>
        <a class="page-nav-result noclickfocus off" href="${navData.paginationURL.prevPg}"><div><span><g:message encodeAs="html" code="ddbnext.Previous_Label" /></span></div></a>
        <div class="disabled-arrow"></div>
      </g:else>
    </li>
    <li class="next-page bl">
      <g:if test="${enableRightPagination}">
        <a class="page-nav-result noclickfocus" href="${navData.paginationURL.nextPg}"><div><span><g:message encodeAs="html" code="ddbnext.Next_Label" /></span></div></a>
        <div class="disabled-arrow off"></div>
      </g:if>
      <g:else>
        <a class="page-nav-result noclickfocus off" href="${navData.paginationURL.nextPg}"><div><span><g:message encodeAs="html" code="ddbnext.Next_Label" /></span></div></a>
        <div class="disabled-arrow"></div>
      </g:else> 
    </li>
  </ul>
</div>