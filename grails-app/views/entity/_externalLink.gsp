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

<ddb:isValidUrl url="${url}">
  <li class="external-link">
    <a href="${url}" rel="external" class="no-external-link-icon">
      <g:if test="${JsonUtil.isAnyNull(publisher.icon)}">
        <div class="external-dummy-icon"></div>
      </g:if>
      <g:else>
        <i class="external-icon"><img src="${publisher.icon}" alt="${publisher.abbr}"/></i>
      </g:else>
      <span>${publisher.name}</span>
	  </a>
	</li>
</ddb:isValidUrl>
