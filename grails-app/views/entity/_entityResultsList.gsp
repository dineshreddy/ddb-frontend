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
<%@page import="de.ddb.common.constants.Type"%>
<g:set var="pageHitCounter" value="0" />
<ul class="results-list unstyled">
  <g:each in="${entities.entity.docs}" var="entityItems">
    <g:each in="${entityItems}" var="entityItem">
      <g:set var="entityId" value="${entityItem.id.substring(cultureGraphUrl.length())}" />
      <g:set var="pageHitCounter" value="${pageHitCounter + 1}" />
      <li class="item bt">
        <div class="summary row">
          <div class="summary-main-wrapper span7">
            <div class="summary-main">
              <h2 class="title">
                <g:link class="persist" controller="entity" action="index" params="${params + [id:entityId]}">
                  <ddbcommon:getTruncatedItemTitle title="${entityItem.preferredName}" length="${100}"/>
                  <g:set var="matchFound" value="${entityItem.preferredName ==~ /.*<match>.*/}"/>
                </g:link>
              </h2>
              <div class="persons-font">
                <g:set var="last" value="${entityItem.professionOrOccupation.size() - 1}" />
                <g:set var="needBreak" value="${false}"/>
                <g:each in="${entityItem.professionOrOccupation}" var="profession" status="i">
                  <ddbcommon:stripTags text="${profession}" replaceTags="match,strong"/><g:if test="${i != last}">, </g:if>
                  <g:set var="needBreak" value="${true}"/>
                </g:each>

                <g:if test="${needBreak}">
                  <br/>
                  <g:set var="needBreak" value="${false}"/>
                </g:if>

                <g:set var="hasBirthDate" value="${entityItem.dateOfBirth || entityItem.placeOfBirth}"/>
                <g:if test="${hasBirthDate}">
                  <g:set var="placeOfBirth" value="${entityItem.placeOfBirth?.getAt(0)}"/>
                  <g:message code="ddbnext.Entity_Birth"/>: 
                  <g:if test="${entityItem.dateOfBirth}">
                    <ddbcommon:stripTags text="${entityItem.dateOfBirth}" replaceTags="match,strong"/>
                  </g:if><g:if test="${entityItem.dateOfBirth && placeOfBirth}">, </g:if>
                  <g:if test="${placeOfBirth}">
                    <ddbcommon:stripTags text="${placeOfBirth}" replaceTags="match,strong"/>
                  </g:if>
                  <g:set var="needBreak" value="${true}"/>
                </g:if>

                <g:if test="${entityItem.dateOfDeath || entityItem.placeOfDeath}">
                  <g:set var="placeOfDeath" value="${entityItem.placeOfDeath?.getAt(0)}"/>
                  <g:if test="${hasBirthDate}"> - </g:if>
                  <g:message code="ddbnext.Entity_Death"/>: 
                  <g:if test="${entityItem.dateOfDeath}">
                    <ddbcommon:stripTags text="${entityItem.dateOfDeath}" replaceTags="match,strong"/>
                  </g:if><g:if test="${entityItem.dateOfDeath && placeOfDeath}">,</g:if>
                  <g:if test="${placeOfDeath}">
                    <ddbcommon:stripTags text="${placeOfDeath}" replaceTags="match,strong"/>
                  </g:if>
                  <g:set var="needBreak" value="${true}"/>
                </g:if>

                <g:each in="${entityItem.variantName}" var="variantName">
                  <g:if test="${!matchFound && variantName ==~ /.*<match>.*/}">
                    <g:if test="${needBreak}">
                      <br/>
                    </g:if>
                    <g:message code="ddbnext.Entity_OtherNames"/>:
                    <ddbcommon:stripTags text="${variantName}" replaceTags="match,strong"/>
                    <g:set var="matchFound" value="${true}"/>
                  </g:if>
                </g:each>

              </div>
            </div>
          </div>
          <div class="thumbnail-wrapper span1 persons-results">
            <div class="thumbnail" id="thumbnail-${entityItem.id}">
              <g:link class="persist" controller="entity" action="index" params="${params + [id:entityId]}" class="no-external-link-icon">
                <g:if test="${entityItem.thumbnail}">
                    <img src="<ddb:fixWikimediaImageWidth thumbnail="${entityItem.thumbnail}" desiredWidth="55" />" alt="${entityItem.preferredName }" width="55" />
                </g:if>
                <g:else>
                <g:img dir="images/placeholder" file="entity.png" width="55" height="90" alt="${entityItem.preferredName }" />
              </g:else>
              </g:link>
            </div>
            <div class="item-options bl">
              <ul class="item-options-ul">
                <ddbcommon:isLoggedIn>
                  <li>
                    <div data-itemid="${entityItem.id.substring(cultureGraphUrl.length())}" data-objecttype="${Type.ENTITY.name}" data-actn="POST" class="add-to-favorites"
                      title="<g:message code="ddbnext.Add_To_Favorites"/>"></div>
                  </li>
                </ddbcommon:isLoggedIn>
              </ul>
            </div>
          </div>
        </div>
      </li>
    </g:each>
  </g:each>
</ul>
<div class="bb end-result-border"></div>
<g:render template="../common/addToFavorites"/>
