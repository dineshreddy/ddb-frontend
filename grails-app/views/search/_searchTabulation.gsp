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
<div class="results-paginator-options bb">
  <div class="span6">
    <span>Alle Ergebnisse(${allCount})</span> <span>Personen(${entitiesCount})</span> <span> Orte(${placesCount})</span> <span> Institutionen(${institutionsCount})</span>
  </div>
  <div class="span2">
  <%-- <ddb:isLoggedIn> --%>
    <div id="addToSavedSearches">
      <div class="add-to-saved-searches"></div>
      <a id="addToSavedSearchesAnchor">
        <g:message encodeAs="html" code="ddbnext.Save_Savedsearch" />
      </a> 
      <span id="addToSavedSearchesSpan" class="off"><g:message encodeAs="html" code="ddbnext.Saved_Savedsearch" /></span>
    </div>

    <div id="addToSavedSearchesModal" class="modal hide fade" tabindex="-1"
      role="dialog" aria-labelledby="addToSavedSearchesLabel" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbnext.Close"/>" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3 id="addToSavedSearchesLabel">
          <g:message encodeAs="html" code="ddbnext.Save_Savedsearch" />
        </h3>
      </div>
      <div class="modal-body">
        <div>
          <b><g:message encodeAs="html" code="ddbnext.Mandatory" /></b>
        </div>
        <br />
        <div>
          <g:message encodeAs="html" code="ddbnext.Savedsearch_Title" />
          *
        </div>
        <div>
          <input id="addToSavedSearchesTitle" type="text">
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-padding" data-dismiss="modal" aria-hidden="true">
          <g:message encodeAs="html" code="ddbnext.Close" />
        </button>
        <button class="btn-padding" type="submit" id="addToSavedSearchesConfirm">
          <g:message encodeAs="html" code="ddbnext.Save" />
        </button>
      </div>
    </div>
  <%-- </ddb:isLoggedIn> --%>
  </div>
</div>