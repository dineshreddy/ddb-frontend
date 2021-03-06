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
<g:set var="favoritesService" bean="favoritesService"/>

<ul class="results-list unstyled" id="slaves">
  <g:set var="index" value="${0}" />
  <g:each var="search" in="${results}">
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span7">
          <input type="checkbox" name="id[${index++}]"
            value="${search.id}" class="remove-item-check">
          <div class="summary-main">
            <h2 class="saved-search-title">
              <a class="persist"
                <g:if test="${search.type == null || search.type == Type.CULTURAL_ITEM}">
                  href="${request.contextPath + '/searchresults?' + search.queryString}"
                </g:if>
                <g:elseif test="${search.type == Type.ENTITY}">
                  href="${request.contextPath + '/entity/search/person?' + search.queryString}"
                </g:elseif>
                <g:elseif test="${search.type == Type.INSTITUTION}">
                  href="${request.contextPath + '/searchresults/institution?' + search.queryString}"
                </g:elseif>
                title="${ddbcommon.getTruncatedHovercardTitle(title: search.label, length: 350)}">
                <ddbcommon:getTruncatedItemTitle title="${search.label}" length="${100}" />
              </a>
              <a id="${search.id}" class="edit-saved-search" data-label="${search.label}"
                 data-querystring="${search.queryString}">
                <i class="icon-edit edit-saved-search-icon"
                   title="${message(code: 'ddbnext.Edit_Savedsearch')}"></i>
              </a>
            </h2>
            <div class="subtitle">
              <g:if test="${search.type == null || search.type == Type.CULTURAL_ITEM}">
                <g:message code="ddbcommon.Entity_Objects"/>
              </g:if>
              <g:elseif test="${search.type == Type.ENTITY}">
                <g:message code="ddbnext.entity.tabulator.persons" />
              </g:elseif>
              <g:elseif test="${search.type == Type.INSTITUTION}">
                <g:message code="ddbnext.Institutions" />
              </g:elseif>
            </div>
            <div class="subtitle">
              <g:render template="savedSearchEntry"
                model="['search':search]" />
            </div>
          </div>
        </div>
        <div class="span2 created-at">
          <div>
            ${favoritesService.formatDate(search.creationDate)}
          </div>
        </div>
      </div>
    </li>
  </g:each>
</ul>
