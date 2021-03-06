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
<g:set var="config" bean="configurationService"/>
<div class="print-tabulator off">
  <span class="total-results"><ddb:getLocalizedNumber>${totalResults}</ddb:getLocalizedNumber></span> 
  <g:if test="${totalResults == 1 || totalResults == "1"}">
    <span class="total-results-label"><g:message code="ddbnext.Multi_Page_Result" /></span>
  </g:if>
  <g:else>
    <span class="total-results-label"><g:message code="ddbnext.Multi_Page_Results" /></span>
  </g:else>
</div>
<div class="results-paginator-options">
  <div class="row">
    <div class="span6 tabulator">
      <ul id="tabulator">
        <li>
          <span class="total-results"><ddb:getLocalizedNumber>${totalResults}</ddb:getLocalizedNumber></span> 
          <g:if test="${totalResults == 1 || totalResults == "1"}">
            <span class="total-results-label"><g:message code="ddbnext.Multi_Page_Result" /></span>:
          </g:if>
          <g:else>
            <span class="total-results-label"><g:message code="ddbnext.Multi_Page_Results" /></span>:
          </g:else>
        </li>
        <li>
          <g:link controller="search" action="results" params="[query:query]"
            class="${(active==Type.CULTURAL_ITEM.getName()) ? 'active-link' : '' }">
            <g:message code="ddbcommon.Entity_Objects"/>
          </g:link>
        </li>
        <li class="${config.isEntitySearchFeaturesEnabled() ? "" : "off"}">
          <g:link controller="entity" action="personsearch" params="[query:query]"
            class="${(active==Type.ENTITY.getName()) ? 'active-link' : '' }">
            <g:message code="ddbnext.entity.tabulator.persons" />
          </g:link>
        </li>
        <li class="${config.isInstitutionSearchFeaturesEnabled() ? "" : "off"}">
          <g:link controller="search" action="institution" params="[query: query]"
                  class="${(active==Type.INSTITUTION.getName()) ? 'active-link' : '' }">
            <g:message code="ddbnext.Institutions" />
          </g:link>
        </li>
      </ul>
    </div>
    <ddbcommon:isLoggedIn>
      <div class="span3"> 
        <div id="addToSavedSearches" data-type="${active}">
          <div class="add-to-saved-searches initializing"></div>
          <a id="addToSavedSearchesAnchor"><g:message encodeAs="html" code="ddbnext.Save_Savedsearch" /></a>
          <span id="addToSavedSearchesSpan" class="off">
            <g:message encodeAs="html" code="ddbnext.Saved_Savedsearch" />
          </span>
        </div>

        <div id="addToSavedSearchesModal" class="modal hide fade savesearch" tabindex="-1" role="dialog"
          aria-labelledby="addToSavedSearchesLabel" aria-hidden="true">
          <div class="modal-header modal-header-savesearch">
            <span title="<g:message encodeAs="html" code="ddbcommon.Close"/>" data-dismiss="modal"
              class="fancybox-toolbar-close"></span>
            <div id="addToSavedSearchesLabel">
              <g:message encodeAs="html" code="ddbnext.Save_Savedsearch" />
            </div>
          </div>
          <div class="modal-body-savesearch">
            <div class="savesearch-edit-title">
              <g:message encodeAs="html" code="ddbnext.Savedsearch_Title" />
            </div>
            <div>
              <input id="addToSavedSearchesTitle" type="text">
            </div>
          </div>
          <div class="modal-footer-savesearch">
            <button class="btn-padding" type="submit" id="addToSavedSearchesConfirm">
              <g:message encodeAs="html" code="ddbcommon.Save" />
            </button>
          </div>
        </div>
      </div>
    </ddbcommon:isLoggedIn>
  </div>
</div>
