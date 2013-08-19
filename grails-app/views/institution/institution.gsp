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

<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>

<html>
<head>
<title>${selectedOrgXML.name} - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

<meta name="page" content="institution" />
<meta name="layout" content="main" />

<r:require module="institution"/>
</head>
<body>
  <div class="institution-item-page">

    <div class="row">
       <div class="span12 institution">
         <div class="row">
           <div class="span9">
             <div>
               <g:message code="ddbnext.${selectedOrgXML.sector}"/>
             </div>
             <div>
                 <h2>${selectedOrgXML.name} 
                 <g:if test="${(countObjcs > 0)}">
                    <g:set var="facetvalue" value="provider_fct=${selectedOrgXML.name}"/>
                    <g:link class="count" controller="search" action="results" params="[query: '', offset: '0',
                               rows: '20', 'facetValues[]': facetvalue]" title="${message(code: 'ddbnext.InstitutionItem_IngestedObjectCountTitleText')}">
                        <g:set var="flashArgs" value='["${String.format(RequestContextUtils.getLocale(request),'%,d', countObjcs)}"]' />
                        <g:if test="${(countObjcs == 1)}">
                            <g:message args="${flashArgs}" code="ddbnext.InstitutionItem_IngestedObjectCountFormat" />
                        </g:if>
                        <g:if test="${(countObjcs > 1)}">
                            <g:message args="${flashArgs}" code="ddbnext.InstitutionItem_IngestedObjectCountFormat_Plural" />
                        </g:if>
                     </g:link>
                 </g:if>
                 </h2>
             </div>
             <div>
               <a href="${selectedOrgXML.uri }/">${String.valueOf(selectedOrgXML.uri).trim() }</a>
             </div>
           </div>
           <div class="span3">
             <img class="logo" alt="${selectedOrgXML.name}" src="${organisationLogo}">
           </div>
           
           <div class="span9">
           </div>
            <div class="span3">
              <div class="favorite">
                  
                <%--  
                <g:isNotLoggedIn>
                  <g:link controller="user" class="favorite-actions">
                    <span title="<g:message code='ddbnext.stat_010' />">
                      <g:message code="ddbnext.favorit" />
                    </span>
                  </g:link>
                </g:isNotLoggedIn>
                --%>
                <g:isLoggedIn>
                  <g:if test="${(isFavorite == 302) }">
                    <g:link controller="institution" action="showInstitutionsTreeByItemId" params="${params+[reqActn:'del']}" class="favorite-actions favorite-selected">
                      <span data-itemid="${selectedItemId}" data-actn="DELETE" title="<g:message code='ddbnext.stat_011' />" id="idFavorite" >
                        <g:message code="ddbnext.favorit" />
                      </span>
                    </g:link>
                  </g:if>
                  <g:else>
                    <g:link controller="institution" action="showInstitutionsTreeByItemId" params="${params+[reqActn:'add']}" class="favorite-actions favorite-add">
                      <span data-itemid="${selectedItemId}" data-actn="POST" title="<g:message code='ddbnext.Add_To_Favorites' />" id="idFavorite" >
                        <g:message code="ddbnext.favorit" />
                      </span>
                    </g:link>
                  </g:else>
                  <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-body">
                      <p><g:message code="ddbnext.Added_To_Favorites"/></p>
                    </div>
                    <%-- 
                    <div class="modal-footer">
                      <button id="modal-btn-ok" class="btn-padding" data-dismiss="modal">OK</button>
                    </div>
                    --%>
                  </div>
                </g:isLoggedIn>                
              </div>
            </div>
           
         </div>
       </div>
     </div>

     <div class="row">
       <div class="span12 locations">

            <div class="location-container span5">

              <div class="location" data-lat="${selectedOrgXML.locations.location.geocode.latitude }" data-lon="${selectedOrgXML.locations.location.geocode.longitude }">
                <p class="address">
                  <b>${selectedOrgXML.name}</b><br>
                  <span class="space">${selectedOrgXML.locations.location.address.street }</span>${selectedOrgXML.locations.location.address.houseIdentifier }<br>
                  <g:if test="${(selectedOrgXML.locations.location.address.addressSupplement)&&(selectedOrgXML.locations.location.address.addressSupplement.text().length() > 0)}">
                    ${selectedOrgXML.locations.location.address.addressSupplement}<br>
                  </g:if>
                  <span class="space">${selectedOrgXML.locations.location.address.postalCode }</span>${selectedOrgXML.locations.location.address.city }
                </p>
              </div>

              <g:if test="${((subOrg)&&(subOrg.size() > 0)&&(!(parentOrg[parentOrg.size() - 1].aggregationEntity)))}">
                <div class="hierarchy">
                  <span class="title"><g:message code="ddbnext.InstitutionItem_OtherLocations" /></span>
                  <ol class="institution-list">
                    <li class="institution-listitem">
                      <g:if test="${(selectedItemId == itemId)}">
                        <i class="icon-institution"></i>
                        <b>${parentOrg[parentOrg.size() - 1].label}</b>
                      </g:if>
                      <g:else>
                        <i class="icon-child-institution"></i>
                        <a href="${request.contextPath}/about-us/institutions/item/${parentOrg[parentOrg.size() - 1].id}">${parentOrg[parentOrg.size() - 1].label}</a>
                      </g:else>
                      <g:set var="maxDepthOfLoop" value="${10}" />
                      <g:set var="loopCount" value="${0}" />
                      <g:render template="subinstitutions" />
                    </li>
                  </ol>
                </div>
              </g:if>
            </div>

            <div id="divOSM" class="span5"></div>

        </div>
      </div>
  </div> 
  <div class="printViewUrl off">
    <strong><g:message code="ddbnext.CulturalItem_Deeplink"/></strong>: 
    <div>${url}</div>
  </div> 
</body>
</html>

