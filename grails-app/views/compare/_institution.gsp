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
<div class="row item-detail">
  <div class="span6 institution">
    <div class="row">
      <div class="span6">
        <div><g:message encodeAs="html" code="ddbnext.Institution" /></div>
        <g:link class="institution-name" controller="institution" action="showInstitutionsTreeByItemId" params="[id: institution.id]">
          ${institution.name}
        </g:link>
        <a class="institution-link" href="${institution.url}">${institution.url}</a>
        <g:if test="${!originUrl?.toString()?.isEmpty() || !viewerUri?.isEmpty()}">
          <div class="origin">
            <g:if test="${!originUrl?.toString()?.isEmpty()}">
              <a target="_blank" class="show-origin" href="<ddb:doHtmlEncode url="${originUrl}" />" title="<g:message encodeAs="html" code="ddbnext.stat_008" />">
                <span class="has-origin"><g:message encodeAs="html" code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
              </a>
            </g:if>
            <!-- (DFG) viewer -->
            <g:if test="${!viewerUri?.isEmpty()}">
              <a target="_blank" href="${viewerUri}" class="no-external-link-icon">
                <span class="viewer dfg"><g:message encodeAs="html" code="ddbnext.ObjectViewer_dfgKey" /></span>
              </a>
            </g:if>
          </div>
        </g:if>
      </div>
    </div>
  </div>
</div>
<!-- /end of institution -->
