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
<div class="external-links off">
  <hr>
  <h3><g:message encodeAs="html" code="ddbnext.External_Links"/>:</h3>
  <ul class="unstyled">
    <g:each var="link" in="${entity.sameAs}">
      <g:if test="${[Collection, Object[]].any {it.isAssignableFrom(link.'@id'.getClass())}}">
        <g:each in="${link.'@id'}">
          <g:render template="externalLink" model="[publisher: link.publisher, url: it]"/>
        </g:each>
      </g:if>
      <g:else>
        <g:render template="externalLink" model="[publisher: link.publisher, url: link.'@id']"/>
      </g:else>
    </g:each>
  </ul>
</div>