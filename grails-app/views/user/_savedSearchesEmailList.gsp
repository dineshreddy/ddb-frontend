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
<div style="margin-top: 20px; margin-bottom: 20px">
  <g:message encodeAs="html" code="ddbnext.Send_Savedsearches_Email_Body_Pre" args="${[user.getFirstnameAndLastnameOrNickname()]}" />
  <br />
</div>
<table border="1" style="width: 100%; margin-bottom: 20px; border-spacing: 0">
  <thead>
    <tr>
      <g:if test="${results.size() == 1}">
        <th style="margin-top: 20px">
          <g:message encodeAs="html" code="ddbnext.HierarchyHelp_Leaf" />
        </th>
      </g:if>
      <g:else>
        <th style="margin-top: 20px">
          <g:message encodeAs="html" code="ddbnext.Searches" />
        </th>
      </g:else>
    </tr>
  </thead>
  <tbody>
    <g:each var="search" in="${results}">
      <tr>
        <td style="height: 130px; padding: 10px;">
          <h2>
            <a style="color: #a5003b"
                <g:if test="${search.type == null || search.type == Type.CULTURAL_ITEM}">
                  href="${contextUrl + '/searchresults?' + search.queryString}"
                </g:if>
                <g:elseif test="${search.type == Type.ENTITY}">
                  href="${contextUrl + '/entity/search/person?' + search.queryString}"
                </g:elseif>
                <g:elseif test="${search.type == Type.INSTITUTION}">
                  href="${contextUrl + '/searchresults/institution?' + search.queryString}"
                </g:elseif>
                title="${ddbcommon.getTruncatedHovercardTitle(title: search.label, length: 350)}">
                <ddbcommon:getTruncatedItemTitle title="${search.label}" length="${100}" />
              </a>
          </h2>
          <div>
            <g:render template="savedSearchEntry" model="['search':search]" />
          </div>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>
