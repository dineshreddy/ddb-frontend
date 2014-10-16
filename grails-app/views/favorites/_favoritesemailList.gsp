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
<%! import org.codehaus.groovy.grails.validation.routines.UrlValidator %> 
<g:set var="offset" value="${0}" />
<g:set var="index" value="${0}" />
<div style="margin-top:20px; margin-bottom:20px">
<g:message encodeAs="html" code="ddbnext.send_favorites_email_body_pre" 
             args="${[folderTitle, userName]}" encodeAs="none" /><br />
</div>
<g:message encodeAs="html" code="ddbcommon.Create_Folder_Description" />: ${folderDescription}<br /><br />

<table border="1" style="margin-bottom:20px; border-spacing:0">
  <thead>
    <tr>
      <g:if test="${results.size() == 1}">
        <th style="width: 70%; margin-top:20px"><g:message encodeAs="html" code="ddbnext.HierarchyHelp_Leaf" /></th>
      </g:if>
      <g:else>
        <th style="width: 70%; margin-top:20px"><g:message encodeAs="html" code="ddbnext.Entity_Objects" /></th>
      </g:else>
      <th style="width: 170px; margin-top:20px"><g:message encodeAs="html" code="ddbnext.Added_On" /></th>
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
      <g:if test="${it.category == 'Entity'}">
          <g:set var="controller" value="entity" />
          <g:set var="action" value="index" />
      </g:if>
      <tr>
      
        <td style="width: 70%; height: 130px; padding: 10px;">
          <h2>
            <g:link style="color:#a5003b" controller="${ controller }" base="${contextUrl}"
              action="${ action }" params="[id: it.id]"
              title="${ddbcommon.getTruncatedHovercardTitle(title: it.label, length: 350)}">
              <ddbcommon:getTruncatedItemTitle title="${ it.preview.title }"
                length="${ 100 }" />
            </g:link>
          </h2>
          <g:if test="${!(it.preview.subtitle instanceof net.sf.json.JSONNull)}">
            <div>
              ${it.preview.subtitle}<br />
              <br /><br /><br />
              <span style="font-size:x-small;color:#333333">
                <g:if test="${!it.bookmark.description.isEmpty()}">
                  <g:message encodeAs="html" code="ddbnext.Favorites_Comment_Of" /> ${it.folder.publishingName}, ${it.bookmark.updateDateFormatted}:
                </g:if>
              </span><br />
              <span>
                <g:if test="${!it.bookmark.description.isEmpty()}">
                  ${it.bookmark.description.trim()}
                </g:if>
              </span>
            </div>
          </g:if>
        </td>
        <td style="width: 170px; padding: 10px;">
          <g:link controller="${ controller }" action="${ action }" params="[id: it.id]" base="${contextUrl}">
            <g:if test="${new UrlValidator().isValid(it.preview.thumbnail)}">
              <img src="${it.preview.thumbnail}" alt="${ddb.getWithoutTags(body:it.preview.title)}"></img>
            </g:if>
            <g:else>
              <img src="${baseUrl + it.preview.thumbnail}" alt="${ddb.getWithoutTags(body:it.preview.title)}"></img>
            </g:else>
          </g:link>
          <br>
         <g:if test="${!it.bookmark.creationDateFormatted.isEmpty()}">
            ${it.bookmark.creationDateFormatted}
          </g:if>
        </td>
      </tr>
    </g:each>
  </tbody>
</table>
