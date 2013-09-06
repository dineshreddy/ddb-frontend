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
<ul class="types unstyled inline">
  <g:each var="mediaType" in="${mediaTypesArray}">
    <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_007" /></g:set>
    <g:if test="${mediaType == 'audio'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_001" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'image'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_002" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'text'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_003" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'fullText'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_004" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'video'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_005" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'other'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.type_fct_mediatype_006" /></g:set>
    </g:if>
    <g:if test="${mediaType == 'institution'}">
      <g:set var="mediaTitle"><g:message code="ddbnext.Institution" /></g:set>
    </g:if>
    <li class="${mediaType}" title="${mediaTitle}">${mediaTitle}</li>
  </g:each>
</ul>