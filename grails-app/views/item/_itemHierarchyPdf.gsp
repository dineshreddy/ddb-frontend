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
<g:if test="${hierarchy}">
  <div class="bt">
    <g:set var="i" value="${0}" />
    <div class="hierarchy-header">
      <g:message code="ddbnext.View_related_objects" />
    </div>
    <g:each var="${item}" in="${hierarchy}">
      <div class="element">
        <g:if test="${message(code: 'ddbnext.HierarchyType_'+item.type, default:'')}">
          <span class="group-name"><g:message code="${'ddbnext.HierarchyType_'+item.type}" default="" /></span>
          <br />
        </g:if>
        <div class="bullet-item">${item.label}</div>
        <g:if test="${item.children}">
            <ul>
              <g:each var="${child}" in="${item.children}">
                <li><g:if test="${child.id==itemId}">
                    <strong> ${ddbcommon.wellFormedDocFromString(text:child.label)}
                    </strong>
                  </g:if> <g:else>
                    <g:link controller="item" action="findById" id="${child.id}">
                      ${child.label}
                    </g:link>
                  </g:else></li>
              </g:each>
            </ul>
          </g:if>
        <div>
          <g:set var="i" value="${++i}" />
        </div>
    </g:each>
    <g:while test="${i.toInteger() > 0}">
      <%i--%>
  </div>
  </g:while>
  </div>
</g:if>