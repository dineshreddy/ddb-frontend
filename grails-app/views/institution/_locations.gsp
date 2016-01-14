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

      <g:render template="descriptionDesktop"/>
      <g:render template="descriptionPhone"/>

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