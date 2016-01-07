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
<ddbcommon:isLoggedIn>
  <ddbcommon:isPersonalFavoritesAvailable>
    <div id="favorite-confirmation" class="modal hide fade bb" tabindex="-1" role="dialog"
         aria-labelledby="favorite-confirmation-label" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message code="ddbcommon.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <div id="sendSavedSearchesLabel">
          <g:message code="ddbnext.Save_favorite" />
        </div>
      </div>
      <div class="modal-body">
        <p id="favorite-confirmation-label"><g:message code="ddbnext.Added_To_Favorites"/></p>
        <p><g:message code="ddbnext.Add_To_Personal_Favorites"/></p>
        <g:select name="favorite-folders" from="" size="10" multiple="multiple"/>
        <div class="modal-footer-savesearch">
          <button class="btn-padding" type="submit" id="addToFavoritesConfirm">
            <g:message code="ddbcommon.Save"/>
          </button>
        </div>
      </div>
    </div>
  </ddbcommon:isPersonalFavoritesAvailable>
</ddbcommon:isLoggedIn>
