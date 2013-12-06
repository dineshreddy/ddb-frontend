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
<%@page import="de.ddb.next.constants.SearchParamEnum"%>
<%@page import="java.awt.event.ItemEvent"%>
<div class="thumbnail-wrapper <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">span3</g:if>">
  <div class="thumbnail" id="thumbnail-${item.id}">
    <g:link controller="${ controller }" action="${ action }" params="${params + [id: item.id, hitNumber: hitNumber]}">
      <img src="<g:if test="${item.preview.thumbnail.contains('binary')}">${confBinary}</g:if>${item.preview.thumbnail}" alt="<ddb:getWithoutTags>${item.preview.title}</ddb:getWithoutTags>" />
    </g:link>
  </div>
  <div class="item-options <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">bl</g:if>">
    <ul class="item-options-ul">
      <li>
        <div class="information<ddb:isLoggedIn> show-favorites</ddb:isLoggedIn> bb">
          <div class="hovercard-info-item" data-iid="${item.id}">
            <h4><ddb:getTruncatedHovercardTitle title="${ item.preview.title }" length="${ 350 }" /></h4>
            <ul class="unstyled">
              <li>
                <div class="small-loader"></div>
              </li>
            </ul>
          </div>
        </div>
      </li>
      <ddb:isLoggedIn>
        <li>
          <div id="favorite-${item.id}" class="add-to-favorites bb" title="<g:message code="ddbnext.Add_To_Favorites"/>" ></div>
          <div id="favorite-confirmation" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-body">
              <p><g:message code="ddbnext.Added_To_Favorites"/></p>
              <ddb:isPersonalFavoritesAvailable>
                <p><g:message code="ddbnext.Add_To_Personal_Favorites"/></p>
                <g:select name="favorite-folders" from="" size="10" multiple="multiple"/>
              </ddb:isPersonalFavoritesAvailable>
              <ddb:isPersonalFavoritesAvailable>
                <div class="modal-footer">
                  <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
                    <g:message code="ddbnext.Close"/>
                  </button>
                  <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
                    <g:message code="ddbnext.Save"/>
                  </button>
                </div>
              </ddb:isPersonalFavoritesAvailable>
            </div>
          </div>
        </li>
      </ddb:isLoggedIn>
      
      <li>
        <div class="compare bb off" data-iid="${item.id}"></div>
      </li>
      
    </ul>
  </div>
</div>
