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
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" class="item-detail">
        <tr>
          <td class=" institution"><g:message code="ddbnext.Institution" /> <br /> <g:link
              class="institution-name" controller="institution" action="showInstitutionsTreeByItemId"
              params="[id: institution.id]">
              ${institution.name}
            </g:link> <br /> <a class="institution-link" href="${institution.url}"> ${institution.url}</a> <g:if
              test="${!originUrl?.toString()?.isEmpty() || !viewerUri?.isEmpty()}">
              <div class="origin">
                <g:if test="${!originUrl?.toString()?.isEmpty()}">
                  <a class="show-origin" href="${originUrl}" title="<g:message code="ddbnext.stat_008" />">
                    <span class="has-origin"><g:message
                        code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
                  </a>
                </g:if>
                <!-- (DFG) viewer -->
                <g:if test="${!viewerUri?.isEmpty()}">
                  <a href="${viewerUri}" class="no-external-link-icon"> <span class="viewer dfg"><g:message
                        code="ddbnext.ObjectViewer_dfgKey" /></span>
                  </a>
                </g:if>
              </div>
            </g:if></td>
          <td class="institution"><rendering:inlinePng bytes="${institutionImage}" alt="Deutsche Digitale Bibliothek" /></td>
        </tr>
      </table>
    </td>
  </tr>
</table>