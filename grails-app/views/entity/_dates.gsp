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

<%@page import="de.ddb.common.constants.SearchParamEnum" %>

<g:set var="hasProfessions" value="${entity.professionOrOccupation}"/>
<g:set var="hasBirthContent" value="${entity.dateOfBirth || entity.placeOfBirth}"/>
<g:set var="hasDeathContent" value="${entity.dateOfDeath || entity.placeOfDeath}"/>
<g:set var="hasVariantName" value="${false}"/>

<%--
commented out because of https://jira.deutsche-digitale-bibliothek.de/browse/DDBNEXT-2239

<g:if test="${entity.preferredName !=~ /.*<match>.*/}">
  <g:each in="${entity.variantName}">
    <g:if test="${it ==~ /.*<match>.*/}">
      <g:set var="hasVariantName" value="${true}"/>
    </g:if>
  </g:each>
</g:if>
--%>

<g:if test="${hasProfessions || hasBirthContent || hasDeathContent || hasVariantName}">
  <div class="profession-dates">
    <g:if test="${hasProfessions}">
      <div class="profession">
        <g:each var="link" status="i" in="${entity.professionOrOccupation}">
          <span>${link.preferredName}<g:if test="${i < (entity.professionOrOccupation.size() - 1)}">, </g:if></span>
        </g:each>
      </div>
    </g:if>

    <g:if test="${hasBirthContent || hasDeathContent || hasVariantName}">
      <div class="dates fields">  
        <g:if test="${hasBirthContent}">
          <div>
            <g:message code="ddbcommon.Entity_Birth" />: 
            ${entity.dateOfBirth}<g:if test="${entity.placeOfBirth}"><g:if test="${entity.dateOfBirth}">,</g:if>
              <g:link controller="search" action="results" params="${[(SearchParamEnum.QUERY.getName()):entity.placeOfBirth.preferredName]}" class="search_link">
                <g:each var="placeOfBirth" status="i" in="${entity.placeOfBirth}">
                  <span>${placeOfBirth.preferredName}<g:if test="${i < (entity.placeOfBirth.size() - 1)}">, </g:if></span>
                </g:each>
              </g:link>
            </g:if>
          </div>
        </g:if>
        <g:if test="${hasDeathContent}">
          <div>
            <g:message code="ddbcommon.Entity_Death" />: 
            ${entity.dateOfDeath}<g:if test="${entity.placeOfDeath}"><g:if test="${entity.dateOfDeath}">,</g:if>
              <g:link controller="search" action="results" params="${[(SearchParamEnum.QUERY.getName()):entity.placeOfDeath.preferredName]}" class="search_link">
                <g:each var="placeOfDeath" status="i" in="${entity.placeOfDeath}">
                  <span>${placeOfDeath.preferredName}<g:if test="${i < (entity.placeOfDeath.size() - 1)}">, </g:if></span>
                </g:each>
              </g:link>
            </g:if>
          </div>
        </g:if>

        <g:if test="${hasVariantName}">
          <g:set var="variantNameIndex" value="${0}"/>
          <g:message code="ddbcommon.Entity_OtherNames"/>:
          <g:each in="${entity.variantName}" var="variantName">
            <g:if test="${variantName ==~ /.*<match>.*/}">
              <g:if test="${variantNameIndex > 0}">
                ...
              </g:if>
              ${variantName}
              <g:set var="variantNameIndex" value="${variantNameIndex + 1}"/>
            </g:if>
          </g:each>
        </g:if>
      </div>
    </g:if>
  </div>
</g:if>