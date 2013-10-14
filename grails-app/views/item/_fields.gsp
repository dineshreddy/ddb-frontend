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
<div class="fields">
  <g:each in="${fields}">
    <div class="row">
      <div class="span2"><strong>${it.name}: </strong></div>
      <g:if test="${it.value.@entityId != null && !it.value.@entityId.isEmpty()}"> 
        <div class="value <g:if test="${binaryList}">span4</g:if><g:else>span10</g:else>">
          <g:link controller="entity" action="index" params="${["id": it.value.@entityId]}" class=" entity-link">${it.value}</g:link>
        </div>
      </g:if>
      <g:else>
        <div class="value <g:if test="${binaryList}">span4</g:if><g:else>span10</g:else>">${it.value}</div>
      </g:else>
    </div>
  </g:each>
</div>
