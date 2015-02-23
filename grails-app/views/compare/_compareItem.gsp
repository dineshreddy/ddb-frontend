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

<g:set var="config" bean="configurationService"/>
<g:render template="institution" />
<div class="row item-detail item-content">
  <g:each in="${binaryList}">
    <g:if test="${!it.full.uri.isEmpty() || !it.preview.uri.isEmpty()}">
      <g:set var="hasBinary" value="${true}"/>
    </g:if>
  </g:each>
  <g:if test="${(hasBinary || !originUrl.isEmpty()) && (item.media!='no media type' && item.media!='unknown')}">
    <g:set var="display" value="${true}"/>
  </g:if>
  <div class="span6 item-description">
    <h2 class="item-title ${position}"><span>${title.encodeAsHTML()}</span></h2>
    <g:if test="${display}">
      <g:render template="binaries" />
    </g:if>
    <g:render template="fields" />
    <g:if test="${config.isRightsFacetEnabled()}">
      <g:render template="rights" />
    </g:if>
    <g:render template="license" />
    <g:render template="/item/origin" />
  </div>
</div>
