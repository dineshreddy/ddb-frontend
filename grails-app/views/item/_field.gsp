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
<%@ page import="net.sf.json.JSONObject" %>
<g:if test="${value instanceof JSONObject && !JsonUtil.isAnyNull(value."@entityId")}">
  <g:if test="${value."@isLink"}">
    <g:link controller="entity" action="index" params="${["id": value."@entityId"]}" class="entity-link">
      ${value."\$"}
    </g:link>
  </g:if>
  <g:else>
    ${value."\$"}
  </g:else>
</g:if>
<g:else>
  <g:if test="${value instanceof JSONObject}">
    ${raw(ddbcommon.encodeInvalidHtml(text:value."\$"))}
  </g:if>
  <g:else>
    ${raw(ddbcommon.encodeInvalidHtml(text:value))}
  </g:else>
</g:else>
