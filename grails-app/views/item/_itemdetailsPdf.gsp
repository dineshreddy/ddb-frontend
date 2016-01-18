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
<%@ page import="de.ddb.common.JsonUtil" %>
<%@ page import="net.sf.json.*" %>
<%@ page defaultCodec="none" %>
<g:set var="config" bean="configurationService"/>
<table border="0" cellpadding="2" cellspacing="3" width="100%" class="fields-table">
  <g:each var="field" in="${fields}">
    <g:set var="isEvent" value="${field.id == 'flex_mus_neu_110' || field.id == 'flex_mus_neu_120' || field.id == 'flex_mus_neu_130'}"/>
    <tr>
      <td style="width: 35%" class="valign-top">
        <strong style="${isEvent ? 'margin-left: 1.1em' : ''}">
          <ddbcommon:wellFormedDocFromString text="${field.name}"/>:
        </strong>
      </td>
      <td class="valign-top value">
        <g:each var="value" in="${field.value}">
          <g:if test="${value.entityId}">
            <g:link controller="entity" action="index" params="${[id: value.entityId]}" class="entity-link">
              <ddbcommon:wellFormedDocFromString text="${value.text}"/>
            </g:link>
          </g:if>
          <g:else>
            <ddbcommon:stripTags text="${value.text}"/>
          </g:else>
          <br />
        </g:each>
      </td>
    </tr>
  </g:each>
  <!-- Item Rights -->
  <g:if test="${config.isRightsFacetFeaturesEnabled() && item.rights != null && !item.rights.toString().trim().isEmpty()}">
    <tr>
      <td style="width: 35%" class="valign-top">
        <strong>
          <g:message code="ddbnext.stat_007"/>:
        </strong>
      </td>
      <td style="width: 65%" class="valign-top">
        <ddbcommon:wellFormedDocFromString text="${item.rights}"/>
      </td>
    </tr>
  </g:if>
  <!-- Item License -->
  <g:if test="${license}">
    <tr>
      <td style="width: 30%" class="valign-top">
        <strong>
          <g:message code="ddbnext.License_Field"/>:
        </strong>
      </td>
      <td style="width: 60%" class="valign-top">
        <a href="${license.url}" class="no-external-link-icon">
          <g:if test="${license.img}">
            <rendering:inlinePng bytes="${licenseImage}" alt="${ddbcommon.wellFormedDocFromString(text: license.text)}"/>
          </g:if>
          <span>
            <ddbcommon:wellFormedDocFromString text="${license.text}"/>
          </span>
        </a>
      </td>
    </tr>
  </g:if>
</table>
