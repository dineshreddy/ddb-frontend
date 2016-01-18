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
  <div class="static_marginal">
    <div><h3><g:message encodeAs="html" code="ddbcommon.User_MyContent"/></h3></div>
    <ul class="plum-arrow">
      <li>
        <a class="profile-link" title="<g:message encodeAs="html" code="ddbnext.Favorites" />" class="persist" href="${createLink(controller="favoritesview", action: 'favorites', params:[:])}">
          <g:message encodeAs="html" code="ddbnext.Favorites" /> (${favoritesCount})
        </a>
      </li>
      <li>
        <a class="profile-link" title="<g:message encodeAs="html" code="ddbnext.SavedSearches" />" class="persist" href="${createLink(controller="user", action: 'savedsearches', params:[:])}">
          <g:message encodeAs="html" code="ddbnext.SavedSearches" /> (${savedSearchesCount})
        </a>
      </li>
      <li>
        <a class="profile-link" title="<g:message encodeAs="html" code="ddbcommon.User.Data" />" class="persist" href="${createLink(controller="user", action: 'profile', params:[:])}">
          <g:message encodeAs="html" code="ddbcommon.User.Data" />
        </a>
      </li>    
      <ddbcommon:isNotOpenIdUser>
        <li>
            <a class="profile-link" title="<g:message encodeAs="html" code="ddbnext.Api_Access" />" class="persist" href="${createLink(controller="user", action: 'showApiKey', params:[:])}">
              <g:message encodeAs="html" code="ddbnext.Api_Access" /> 
            </a>
        </li>
        <li>
          <a class="profile-link" title="<g:message encodeAs="html" code="ddbcommon.Change_Password_Link" />" class="persist" href="${createLink(controller="user",action: 'passwordChangePage', params:[:])}">
            <g:message encodeAs="html" code="ddbcommon.Change_Password_Link" />
          </a>
        </li>
        <li>
          <a class="profile-link" id="delete-account" title="<g:message encodeAs="html" code="ddbcommon.User.Delete_Account" />" class="persist" href="#">
            <g:message encodeAs="html" code="ddbcommon.User.Delete_Account" />
          </a>
        </li>
      </ddbcommon:isNotOpenIdUser>
      <li>
        <a class="profile-link" id="newsletter" title="${g.message(code: 'ddbnext.Newsletter')}" class="persist"
           href="${createLink(controller: 'newsletter', action: 'index')}">
          <g:message code="ddbnext.Newsletter"/>
        </a>
      </li>
    </ul>
  </div>
  
  <%-- Delete Account modal dialog --%>
  <g:render template="deleteAccount" />
</ddbcommon:isLoggedIn>
