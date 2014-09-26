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
<%@page defaultCodec="none" %>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" class="item-detail">
        <tr>
          <td class="institution"><g:message encodeAs="html" code="ddbnext.Institution" /> <br /> <g:link
              class="institution-name" controller="institution" action="showInstitutionsTreeByItemId"
              params="[id: institution.id]">
              ${institution.name}
            </g:link>
            <br />
            <a class="institution-link" href="<ddb:doHtmlEncode url="${institution.url}"/>"><ddb:doHtmlEncode url="${institution.url}"/></a>
            <g:if test="${!originUrl?.toString()?.isEmpty() || !viewerUri?.isEmpty()}">
              <div class="origin">
                <g:if test="${!originUrl?.toString()?.isEmpty()}">
                  <a href="<ddb:doHtmlEncode url="${originUrl}" />" title="<g:message encodeAs="html" code="ddbnext.stat_008" />">
                    <span ><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
                  </a>
                </g:if>
                <!-- (DFG) viewer -->
                <g:if test="${!viewerUri?.isEmpty()}">
                  <a href="${viewerUri}" class="show-origin"> <span class="viewer dfg"><g:message encodeAs="html"
                        code="ddbnext.ObjectViewer_dfgKey" /></span>
                  </a>
                </g:if>
              </div>
            </g:if></td>
          <td class="institution"><div style="float:right"><rendering:inlinePng bytes="${institutionImage}" alt="Deutsche Digitale Bibliothek" /></div></td>
        </tr>
      </table>
    </td>
  </tr>
</table>