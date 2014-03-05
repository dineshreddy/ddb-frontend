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
<div class="external-links">
  <h3><g:message code="ddbnext.External_Links" />:</h3>
  <ul class="unstyled">  
    <g:each var="link" in="${entity.sameAs}">
      <ddb:isValidUrl url="${link.'@id'}">
	      <li class="external-link">
	        <a href="${link.'@id'}" rel="external" class="no-external-link-icon">
            <g:if test="${ link.publisher.icon != null}">
              <i class="external-icon"><img src="${link.publisher.icon}" alt="" /></i>
              <span>${link.publisher.name}</span>
            </g:if>
            <g:else>
              <div class="external-dummy-icon"></div>
              <span>${link.publisher.name}</span>
            </g:else>
	        </a>
	      </li>
      </ddb:isValidUrl>
    </g:each>
  </ul>
</div>