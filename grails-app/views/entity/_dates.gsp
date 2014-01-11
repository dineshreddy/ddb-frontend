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

<g:set var="hasBirthContent" value="${entity.person.dateOfBirth != null || entity.person.placeOfBirth != null}"/>
<g:set var="hasDeathContent" value="${entity.person.dateOfDeath != null || entity.person.placeOfDeath != null}"/>

<g:if test="${hasBirthContent || hasDeathContent}">
  <div class="dates fields">  
    <g:if test="${hasBirthContent}">
      <div>
      	<g:message code="ddbnext.Entity_Birth" />: ${entity.person.dateOfBirth}
        <g:if test="${entity.person.placeOfBirth}">,
           <a href="${entity.person.placeOfBirth.'@id'}" rel="external" class="no-external-link-icon">
              <span>${entity.person.placeOfBirth.value}</span>
            </a>
        </g:if>
      </div>
    </g:if>
    <g:if test="${hasDeathContent}">
      <div>
      	<g:message code="ddbnext.Entity_Death" />: ${entity.person.dateOfDeath}
        <g:if test="${entity.person.placeOfDeath}">, 
      		<a href="${entity.person.placeOfDeath.'@id'}" rel="external" class="no-external-link-icon">
              <span>${entity.person.placeOfDeath.value}</span>
            </a>
        </g:if>
      </div>
    </g:if>
  </div>
</g:if>

    