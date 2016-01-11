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

<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>

<html>
<head>
<title>${institution.name} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
<%-- Used for Canonical URL --%>
<link rel="canonical" href="${createLink(controller: 'institution',
                                         action: 'showInstitutionsTreeByItemId',
                                         params: [id: itemId],
                                         base: domainCanonic)}"/>
<meta name="page" content="institution" />
<meta name="layout" content="main" />

<r:require module="institution"/>
</head>
<body>
  <div class="institution-item-page">
    <a id="institution-id" data-institutionid="${itemId}" data-selectedinstitutionid="${selectedItemId}"></a>
    <g:render template="institutionLinks" />
    
    <div class="row">
       <div class="span12 institution">
         <div class="row">
           <div class="span9">
             <div class="sector">
               <g:message encodeAs="html" code="ddbnext.${institution.sector}"/>
             </div>
             <div>
               <h1>${institution.name}
                 <g:if test="${(countObjcs > 0)}">
                   <g:set var="facetvalue" value="provider_id=${selectedItemId}"/>
                   <g:link class="count" controller="search" action="results"
                           params="${[(SearchParamEnum.QUERY.getName()): '',
                                      (SearchParamEnum.OFFSET.getName()): '0',
                                      (SearchParamEnum.ROWS.getName()): '20',
                                      (SearchParamEnum.FACETVALUES.getName()): facetvalue,
                                      (SearchParamEnum.IS_THUMBNAILS_FILTERED.getName()): false]}"
                           title="${message(code: 'ddbnext.InstitutionItem_IngestedObjectCountTitleText')}">
                     <g:set var="flashArgs" value='["${String.format(RequestContextUtils.getLocale(request),'%,d', countObjcs)}"]' />
                     <g:if test="${(countObjcs == 1)}">
                       <g:message encodeAs="html" args="${flashArgs}" code="ddbnext.InstitutionItem_IngestedObjectCountFormat" />
                     </g:if>
                     <g:if test="${(countObjcs > 1)}">
                       <g:message encodeAs="html" args="${flashArgs}" code="ddbnext.InstitutionItem_IngestedObjectCountFormat_Plural" />
                     </g:if>
                   </g:link>
                 </g:if>
               </h1>
             </div>
             <div>
               <a class="external-dummy-icon" href="${institution.uri}"><ddb:removeUrlProtocol url="${institution.uri.trim()}"/></a>
             </div>
             <div>
               <g:if test="${institution.facebook}">
                 <a class="facebook-icon" href="${institution.facebook}" target="_blank"><ddb:removeUrlProtocol url="${institution.facebook.trim()}"/></a>
               </g:if>
             </div>
             <div>
               <g:if test="${institution.twitter}">
                 <a class="twitter-icon" href="${institution.twitter}" target="_blank"><ddb:removeUrlProtocol url="${institution.twitter.trim()} "/></a>
               </g:if>
             </div>
           </div>
           <div class="span3">
             <img class="logo" alt="${institution.name}" src="${organisationLogo}">
           </div>
         </div>
       </div>
     </div>

     <g:render template="highlights" />
     
     <div class="row">
       <div class="span12 locations">

            <div class="location-container span5">
              <g:if test="${institution.locations.location}">
                <g:set var="location" value="${institution.locations.location[0]}"/>
                <div class="location" data-lat="${location.geocode.latitude}" data-lon="${location.geocode.longitude}">
                  <p class="address">
                    <b>${institution.name}</b><br>
                    <span class="space">${location.address.street}</span>${location.address.houseIdentifier}<br>
                    <g:if test="${location.address.addressSupplement}">
                      ${location.address.addressSupplement}<br>
                    </g:if>
                    <span class="space">${location.address.postalCode}</span>${location.address.city}
                  </p>
                </div>
              </g:if>

              <g:if test="${subinstitutions.size() > 0 && !parentOrg[parentOrg.size() - 1].aggregationEntity}">
                <div class="hierarchy">
                  <span class="title"><g:message encodeAs="html" code="ddbnext.InstitutionItem_OtherLocations" /></span>
                  <ol class="institution-list">
                    <li class="institution-listitem">
                      <g:if test="${(selectedItemId == itemId)}">
                        <i class="icon-institution"></i>
                        <div>
                          <b>${parentOrg[parentOrg.size() - 1].label}</b>
                        </div>
                      </g:if>
                      <g:else>
                        <i class="icon-child-institution"></i>
                        <div>
                          <a href="${request.contextPath}/about-us/institutions/item/${parentOrg[parentOrg.size() - 1].id}">${parentOrg[parentOrg.size() - 1].label}</a>
                        </div>
                      </g:else>
                      <g:render template="subinstitutions" model="[subinstitutions: subinstitutions]"/>
                    </li>
                  </ol>
                </div>
              </g:if>
            </div>
            <div id="divOSM" class="span5 script">
              <div id="ddb-map"></div>
            </div>
        </div>
      </div>
  </div> 
  <div class="printViewUrl off">
    <strong><g:message encodeAs="html" code="ddbnext.CulturalItem_Deeplink"/></strong>: 
    <div>${url}</div>
  </div> 
</body>
</html>

