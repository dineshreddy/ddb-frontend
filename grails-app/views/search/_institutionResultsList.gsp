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
  <g:each in="${results.entity}" var="entityItem">
    <g:set var="entityId" value="${entityItem.id}" />
    <g:set var="pageHitCounter" value="${pageHitCounter + 1}" />
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span6">
          <div class="summary-main">
            <h2 class="title">
              <g:link class="persist" controller="institution" action="showInstitutionsTreeByItemId" params="${params + [id:entityId]}">
                <strong><ddb:getTruncatedItemTitle title="${entityItem.preview.title}" length="${ 100 }" /></strong>
              </g:link>
            </h2>
            <div class="subtitle">
              <g:if test="${(entityItem.preview?.subtitle != null)}">
                <ddb:stripTags text="${entityItem.preview.subtitle.replaceAll('match', 'strong')}" allowedTags="strong" />
              </g:if>
            </div>
          </div>
        </div>
        <div class="thumbnail-wrapper span3">
          <div class="thumbnail-entity" id="thumbnail-${entityItem.id}">
            <g:link class="persist" controller="institution" action="showInstitutionsTreeByItemId" params="${params + [id:entityId]}">
              <g:if test="${entityItem.preview.thumbnail!="null"}">
	              <img src="${entityItem.preview.thumbnail}" alt="<ddb:getWithoutTags>${entityItem.preview.title}</ddb:getWithoutTags>" width="140" height="90" />
              </g:if><g:else>
                <g:img dir="images/icons" file="institution.png" width="140" height="90" />
               </g:else>
            </g:link>
          </div>
          <div class="item-options bl">
            <ul class="item-options-ul">
              <ddb:isLoggedIn>
                <li>
                  <div id="favorite-${entityItem.id}" class="add-to-favorites"title="<g:message code="ddbnext.Add_To_Favorites"/>"></div>
                </li>
                <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"aria-hidden="true">
                  <div class="modal-body">
                    <p>
                      <g:message encodeAs="html" code="ddbnext.Added_To_Favorites" />
                    </p>
                  </div>
                </div>
              </ddb:isLoggedIn>
            </ul>
            <div class="information<ddb:isLoggedIn> show-favorites</ddb:isLoggedIn> bb">
              <div class="hovercard-info-item" data-iid="${entityItem.id}">
                <h4><ddb:getTruncatedHovercardTitle title="${entityItem.preview.title }" length="${ 350 }" /></h4>
                <ul class="unstyled">
                  <li>
                    <div class="small-loader"></div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </li>
  </g:each>
</ul>