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

<%@page import="de.ddb.next.constants.SearchParamEnum"%>

<g:set var="hasProfessions" value="${entity.person.professionOrOccupation != null && entity.person.professionOrOccupation.size() > 0}"/>
<g:set var="hasBirthContent" value="${entity.person.dateOfBirth != null || entity.person.placeOfBirth != null}"/>
<g:set var="hasDeathContent" value="${entity.person.dateOfDeath != null || entity.person.placeOfDeath != null}"/>


<g:if test="${hasProfessions || hasBirthContent || hasDeathContent}">
  <div class="profession-dates">
    <g:if test="${hasProfessions}">
      <div class="profession">
        <g:each var="link" status="i" in="${entity.person.professionOrOccupation}">
          <span>${link.value}<g:if test="${i < (entity.person.professionOrOccupation.size()-1)}">, </g:if></span>
        </g:each>
      </div>
    </g:if>
    
    <g:if test="${hasBirthContent || hasDeathContent}">
      <div class="dates fields">  
        <g:if test="${hasBirthContent}">
          <div>
            <g:message code="ddbnext.Entity_Birth" />: 
            ${entity.person.dateOfBirth}<g:if test="${entity.person.placeOfBirth}">,
              <g:link controller="search" action="results" params="${[(SearchParamEnum.QUERY.getName()):entity.person.placeOfBirth.value]}" class="search_link">
                <span>${entity.person.placeOfBirth.value}</span>
              </g:link>
            </g:if>
          </div>
        </g:if>
        <g:if test="${hasDeathContent}">
          <div>
            <g:message code="ddbnext.Entity_Death" />: 
            ${entity.person.dateOfDeath}<g:if test="${entity.person.placeOfDeath}">, 
              <g:link controller="search" action="results" params="${[(SearchParamEnum.QUERY.getName()):entity.person.placeOfDeath.value]}" class="search_link">
                <span>${entity.person.placeOfDeath.value}</span>
              </g:link>
            </g:if>
          </div>
        </g:if>
      </div>
    </g:if>
  </div>
</g:if>
