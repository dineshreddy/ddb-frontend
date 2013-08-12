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
<g:set var="offset" value="${0}" />
<g:set var="index" value="${0}" />
<div style="margin-top:20px; margin-bottom:20px">
<g:message code="ddbnext.send_favorites_email_body_pre" 
             args="${[userName]}" /><br />
</div>
<table border="1" style="margin-bottom:20px; border-spacing:0">
  <thead>
    <tr>
      <g:if test="${results.size() == 1}">
        <th width="70%" style="margin-top:20px"><g:message code="ddbnext.HierarchyHelp_Leaf" /></th>
      </g:if>
      <g:else>
        <th width="70%" style="margin-top:20px"><g:message code="ddbnext.Entity_Objects" /></th>
      </g:else>
      <th width="170px"></th>
    </tr>
  </thead>
  <tbody>
    <g:each in="${results}">
      <g:set var="controller" value="item" />
      <g:set var="action" value="findById" />
      <g:if test="${it.category == 'Institution'}">
          <g:set var="controller" value="institution" />
          <g:set var="action" value="showInstitutionsTreeByItemId" />
      </g:if>
      
      <tr>
        <td width="70%" height="130px" style="padding: 10px;">
          <h2>
            <g:link style="color:#a5003b" controller="${ controller }" base="${grailsApplication.config.ddb.favorites.basedomain}"
              action="${ action }" params="[id: it.id]"
              title="${truncateHovercardTitle(title: it.label, length: 350)}">
              <g:truncateItemTitle title="${ it.preview.title }"
                length="${ 100 }"></g:truncateItemTitle>
            </g:link>
          </h2>
          <div>
            ${it.preview.subtitle}
          </div>
        </td>
        <td width="170px" style="padding: 10px;">
          <g:link controller="${ controller }" action="${ action }" params="[id: it.id]" base="${grailsApplication.config.ddb.favorites.basedomain}">
            <img src="${grailsApplication.config.ddb.favorites.basedomain}<g:if test="${it.preview.thumbnail.contains('binary')}">${confBinary}</g:if>${it.preview.thumbnail}"
                 alt="<g:removeTags>${it.preview.title}</g:removeTags>" />
          </g:link>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>