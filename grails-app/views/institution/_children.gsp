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
<!-- TODO: we can reuse the _listItem template instead this one. -->
<g:if test="${ children?.size() >0 }">
<ol>
<g:each var="child" in="${ children }">
  <li class="institution-listitem" data-sector="${ child?.sector }" 
    data-institution-id="${ child.id }">
    <i class="icon-child-institution"></i>
    <g:render template="listItem" model="['item': child]"/>
    <g:render template="children" model="['children': child?.children]"/>
  </li>
</g:each>
</ol>
</g:if>