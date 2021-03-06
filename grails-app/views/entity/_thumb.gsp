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

<g:if test="${entityImageUrl}">
  <div class="thumb">
    <hr>
    <div class="thumbinner">
      <!-- TODO: refactor to use figure element -->
      <a href="${entity.depiction.url}" class="wiki-link no-external-link-icon">
        <img alt="" src="${entityImageUrl}" class="thumbimage">
      </a>
      <div class="thumbcaption">
        ${entity.preferredName}
        <br>
        <g:message code="ddbcommon.Entity_Source"/>: <a href="${entity.depiction.url}" class="wiki-link no-external-link-icon">Wikimedia Commons</a>
      </div>
      <div class="thumblicense">
        <g:message code="ddbcommon.Entity_MediaLicence"/>
      </div>
    </div>
  </div>
  <hr>
</g:if>
