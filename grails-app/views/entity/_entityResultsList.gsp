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
                  <ddb:getTruncatedItemTitle title="${entityItem.preferredName}" length="${ 100 }" />
                </g:link>
              </h2>
              <div class="persons-font">
                <g:set var="last" value="${entityItem.professionOrOccupation.size() - 1}" />
                <g:each in="${entityItem.professionOrOccupation}" var="profession" status="i">
                  ${profession}<g:if test="${i != last}">, </g:if>
                </g:each>
                <br>
                <g:if test="${entityItem.dateOfBirth}">
                  <g:message code="ddbnext.Entity_Birth" />: ${entityItem.dateOfBirth},</g:if>
                <g:if test="${entityItem.dateOfDeath}">
                  <g:message code="ddbnext.Entity_Death" />: ${entityItem.dateOfDeath}
                </g:if>

              </div>
            </div>
          </div>
          <div class="thumbnail-wrapper span1 persons-results">
            <div class="thumbnail" id="thumbnail-${entityItem.id}">
              <g:link class="persist" controller="entity" action="index" params="${params + [id:entityId]}" class="no-external-link-icon">
                <img src="<ddb:fixWikimediaImageWidth thumbnail="${entityItem.thumbnail}" desiredWidth="55px" />" alt="${entityItem.preferredName }" width="55px" />
              </g:link>
            </div>
            <div class="item-options bl">
              <ul class="item-options-ul">
                <ddbcommon:isLoggedIn>
                  <li>
                    <div data-itemid="${entityItem.id.substring(cultureGraphUrl.length())}" data-objecttype="entity" data-actn="POST" class="add-to-favorites"
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
<ddbcommon:isLoggedIn>
<ddb:isPersonalFavoritesAvailable>
  <div id="favorite-confirmation" class="modal hide fade bb" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-body">
      <p>
        <g:message encodeAs="html" code="ddbnext.Added_To_Favorites" />
      </p>
      <p>
        <g:message encodeAs="html" code="ddbnext.Add_To_Personal_Favorites" />
      </p>
      <g:select name="favorite-folders" from="" size="10" multiple="multiple" />
      <div class="modal-footer">
        <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
          <g:message encodeAs="html" code="ddbnext.Close" />
        </button>
        <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
          <g:message encodeAs="html" code="ddbnext.Save" />
        </button>
      </div>
    </div>
  </div>
</ddb:isPersonalFavoritesAvailable>
</ddbcommon:isLoggedIn>