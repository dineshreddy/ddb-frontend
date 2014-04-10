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

<g:if test="${entityImageExists}">
  <div class="thumb">
    <hr>
    <div class="thumbinner">
      <!-- TODO: refactor to use figure element -->
      <img alt="${entity.title}" src="${entityImageUrl}" class="thumbimage">
      <div class="thumbcaption">
        ${entity.person.preferredName}
        <br>
        <g:message encodeAs="html" code="ddbnext.Entity_Source" />: <a href="${entity.person.depiction.url}" class="wiki-link no-external-link-icon">Wikimedia</a>
      </div>
    </div>
  </div>
  <hr>
</g:if>
