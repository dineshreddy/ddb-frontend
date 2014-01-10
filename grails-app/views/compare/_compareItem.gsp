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

<g:render template="institution" />
<div class="row item-detail item-content">
  <div class="span6 item-description">
    <h2 class="item-title ${position}"><span>${title}</span></h2>
    <g:if test="${binaryList}">
      <g:render template="binaries" />
    </g:if>
    <g:render template="fields" />
    <g:render template="rights" />
    <g:render template="license" />
    <g:render template="/item/origin" />
  </div>
</div>
