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
<h2>
  ${itemTitle}
</h2>
<table border="0" cellpadding="8" cellspacing="0" width="100%">
  <g:each in="${fields}">
    <tr>
      <td style="width: 35%" class="valign-top"><strong> ${it.name}:
      </strong></td>
      <td class="valign-top value <g:if test="${binaryList}">span4</g:if><g:else>span10</g:else>"><g:each
          var="value" in="${it.value }">
          <g:if test="${value.@entityId != null && !value.@entityId.isEmpty()}">
            <g:link controller="entity" action="index" params="${["id": value.@entityId]}" class="entity-link">
              ${value}
            </g:link>
          </g:if>
          <g:else>
            ${value}
          </g:else>
          <br />
        </g:each></td>
    </tr>
  </g:each>
  <!-- Item Rights -->
  <g:if test="${item.rights != null && !item.rights.toString().trim().isEmpty()}">
    <tr>
      <td style="width: 35%" class="valign-top"><strong> <g:message code="ddbnext.stat_007" />:
      </strong></td>
      <td style="width: 65%" class="valign-top">
        ${item.rights}
      </td>
    </tr>
  </g:if>
  <!-- Item License -->
  <g:if test="${license}">
    <tr>
      <td style="width: 35%" class="valign-top"><strong> <g:message code="ddbnext.License_Field" />:
      </strong></td>
      <td style="width: 65%" class="valign-top"><a href="${license.url}" target="_blank"
        class="no-external-link-icon"><g:if test="${license.img}">
            <g:img file="${license.img}" alt="${license.text}" class="license-icon" />
          </g:if><span> ${license.text}
        </span></a></td>
    </tr>
  </g:if>
</table>
<!-- Original Object View -->
<div class="origin">
  <g:if test="${!originUrl?.toString()?.isEmpty()}">
    <a class="show-origin" href="${originUrl.encodeAsHTML()}" title="<g:message code="ddbnext.stat_008" />">
      <span class="has-origin"><g:message code="ddbnext.CulturalItem_LinkToOriginalItem_Label" /></span>
    </a>
  </g:if>
  <g:else>
    <span><g:message code="ddbnext.Link_to_data_supplier_not_available" /></span>
  </g:else>
</div>