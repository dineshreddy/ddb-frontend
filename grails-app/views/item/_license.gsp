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

<g:if test="${license}" >
  <div class="fields">
    <div class="row">
      <div class="span2"><strong><g:message encodeAs="html" code="ddbnext.License_Field" />: </strong><ddbcommon:renderInfoTooltip messageCode="ddbnext.Licence_TooltipContent" controllerAction="lizenzen" hasArrow="true" /></div>
      <div class="value <g:if test="${display}">span4</g:if><g:else>span10</g:else>">
        <a href="${license.url}" target="_blank" class="no-external-link-icon"><g:if test="${license.img}"><g:img uri="${license.img}" class="license-icon" alt="${g.message(code:"ddbnext.License_Field")}"/></g:if><span>${license.text}</span></a>
      </div>
    </div>
  </div>
</g:if>
