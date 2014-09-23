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
<%@page import="de.ddb.common.CultureGraphService"%>
<%@page import="de.ddb.common.JsonUtil"%>
<%@ page import="net.sf.json.*" %>
<div class="fields">
  <g:each in="${fields}">
    <div class="row">
      <div class="span2"><strong>${it.name}: </strong></div>
      <div class="value span4">
        <div>
          <g:if test="${it.value instanceof JSONArray}"> 
            <g:each var="value" in="${it.value }">
              <g:if test="${value instanceof JSONObject && !JsonUtil.isAnyNull(value."@entityId")}"> 
                <g:link controller="entity" action="index" params="${["id": value."@entityId"]}" class="entity-link">${ddb.encodeInvalidHtml(text:value."\$")}</g:link>
              </g:if>
              <g:else>
                ${raw(ddbcommon.encodeInvalidHtml(text:value))}
              </g:else>
              <br />
            </g:each>
          </g:if>
          <g:else>
            <g:if test="${it.value instanceof JSONObject && it.value."@resource" != null && !it.value."@resource".isEmpty()}">
              <g:link 
                controller="entity" 
                action="index" 
                params="${["id": it.value."@resource".substring(CultureGraphService.GND_URI_PREFIX.length())]}" 
                class="entity-link">
                ${it.value."\$"}
              </g:link>
            </g:if>
            <g:else>
              ${raw(ddbcommon.encodeInvalidHtml(text:it.value))}
            </g:else>
          </g:else>
        </div>
      </div>
    </div>
  </g:each>
</div>
