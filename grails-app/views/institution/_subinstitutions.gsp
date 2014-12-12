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
<ol class="institution-list">
  <g:each var="subinstitution" in="${subinstitutions}" >
    <li class="institution-listitem" data-sector="${subinstitution.sector}" data-institution-id="${subinstitution.id}">
      <g:if test="${(selectedItemId == subinstitution.id)}">
        <i class="icon-institution"></i>
        <div>
          <b>${subinstitution.name}</b>
        </div>
      </g:if>
      <g:else>
        <i class="icon-child-institution"></i>
        <div>
          <g:link controller="institution" action="showInstitutionsTreeByItemId" params="[id: subinstitution.id]">
            ${subinstitution.name}
          </g:link>
        </div>
      </g:else>
      <g:if test="${subinstitution.children.size() > 0}">
        <g:render template="subinstitutions" model="[subinstitutions: subinstitution.children]"/>
      </g:if>
    </li>
  </g:each>
</ol>
