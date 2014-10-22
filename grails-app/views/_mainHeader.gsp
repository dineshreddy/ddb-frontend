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
<div class="print-logo off">
  <r:img dir="images" file="logoHeaderSmall.png" alt="" />
</div>

<!--[if lt IE 9]>
  <div class="header" role="contentinfo">
<![endif]-->

<!--[if !IE]><!-->
<g:set var="config" bean="configurationService"/>
<div class="cookie-notice visible" id="cookie-notice">
  <div class="container">
    <div class="row">
      <div class="span12">
        <p>
          <g:message code="ddbcommon.Cookie_Acceptance" args="${[createLink(controller: 'content', params: [dir:'privacy'])]}"/>
        </p>
        <a class="close" aria-controls="cookie-notice"></a>
      </div>
    </div>
  </div>
</div>
<header class="navbar navbar-fixed-top visible-phone">
  <div class="navbar-inner">
    <div class="container">
      <button type="button" class="btn btn-nav" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar hidden"></span>
      </button>
      <g:link uri="/" class="brand"
        title="${message(code: 'ddbnext.Logo_Title')}"
        tabindex="-1">
        <r:img dir="images" file="mobileLogo.png"
          alt="${message(code: 'ddbnext.Logo_Description')}" />
      </g:link>
      <div class="nav-collapse collapse">
        <ul class="nav nav-list">
          <li><g:form class="navbar-search pull-left"
              method="get" role="search" id="form-search-header-mobile"
              url="[controller:'search', action:'results']">
              <input type="search" class="query" name="query"
                placeholder="Suche"
                value="<ddbcommon:getCookieFieldValue fieldname="query" />">
              <button type="submit">
                <g:message encodeAs="html" code="ddbnext.Go_Button" />
              </button>
            </g:form></li>

          <li class="highlight <ddb:isMappingActive context="${params}"
              testif="${[[controller: "content", dir: "about"]]}">active</ddb:isMappingActive>">
            <g:link controller="content" params="[dir: 'about']">
              ${message(code: 'ddbnext.AboutUs').toUpperCase()}
            </g:link>
            <ul class="nav">
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'news']"><g:message encodeAs="html" code="ddbnext.News" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active</ddb:isMappingActive>">
                <g:link controller="institution" action="show"><g:message encodeAs="html" code="ddbnext.Institutions" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'ddb']"><g:message encodeAs="html" code="ddbnext.Participate" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'competence-network']"><g:message encodeAs="html" code="ddbnext.CompetenceNetwork" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'faq']"><g:message encodeAs="html" code="ddbnext.Faq" /></g:link>
              </li>
            </ul><!-- /end of .nav -->
            <hr/>
          </li>
          
          <li class="highlight <ddb:isMappingActive context="${params}"
            testif="${[[controller: "content", dir: "help"]]}">active</ddb:isMappingActive>">
            <g:link controller="content" params="[dir: 'help']">${message(code: 'ddbnext.Help').toUpperCase()}</g:link>
            <hr/>
          </li><!-- /end of help -->
          
          <g:if test="${config.isExhibitionsFeaturesEnabled()}">
            <li class="highlight">
              <g:link controller="lists">${message(code: 'ddbnext.Discover').toUpperCase()}</g:link>
              <ul class="nav">
                <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "lists"]]}">active</ddb:isMappingActive>">
                  <g:link controller="lists" action="index"><g:message encodeAs="html" code="ddbnext.Favoriteslists" /></g:link>
                </li>
                <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "persons"]]}">active</ddb:isMappingActive>">
                  <g:link controller="persons"><g:message encodeAs="html" code="ddbnext.Personpages" /></g:link>
                </li>
                <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "exhibits"]]}">active</ddb:isMappingActive>">
                  <g:link controller="content" params="[dir: 'exhibits']"><g:message encodeAs="html" code="ddbnext.Exhibitions" /></g:link>
                </li>
              </ul>
              <hr/>
            </li><!-- /end of exhibitions -->
            
          </g:if>
            <ddbcommon:isLoggedIn>
              <li>
                <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.MyDDB" /></g:link>
                <ul class="nav">
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"]]}">active</ddb:isMappingActive>">
                    <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.Favorites" /></g:link>
                  </li>
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "getSavedSearches"]]}">active</ddb:isMappingActive>">
                    <g:link controller="user" action="getSavedSearches"><g:message encodeAs="html" code="ddbnext.Searches" /></g:link>
                  </li>
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "profile"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active</ddb:isMappingActive>">
                    <g:link controller="user" action="profile"><g:message encodeAs="html" code="ddbcommon.Profile" /></g:link>
                  </li>
                </ul>
              </li>
            </ddbcommon:isLoggedIn>
            <li class="highlight"><a>
              ${message(code: 'ddbnext.ChangeLanguage').toUpperCase()}
            </a>
            <ul class="nav">
              <li<ddb:isCurrentLanguage locale="de"> class="selected-language"</ddb:isCurrentLanguage>>
                <ddb:getLanguageLink params="${params}" locale="de" islocaleclass="nopointer">
                  <g:message encodeAs="html" code="ddbnext.language_de" />
                </ddb:getLanguageLink>
              </li>
              <li<ddb:isCurrentLanguage locale="en"> class="selected-language"</ddb:isCurrentLanguage>>
                <ddb:getLanguageLink params="${params}" locale="en" islocaleclass="nopointer">
                  <g:message encodeAs="html" code="ddbnext.language_en" />
                </ddb:getLanguageLink>
              </li>
            </ul>
          </li>
          <li class="highlight">
            <ddbcommon:isNotLoggedIn>
              <g:link class="login-link login-link-referrer" controller="user" params="${[referrer:grailsApplication.mainContext.getBean('de.ddb.common.GetCurrentUrlTagLib').getCurrentUrl()]}"> ${message(code: 'ddbcommon.Login_Button').toUpperCase()}</g:link>
            </ddbcommon:isNotLoggedIn>
            <ddbcommon:isLoggedIn>
              <g:link controller="user" action="doLogout"><g:message encodeAs="html" code="ddbcommon.Logout" /> (<ddbcommon:getUserName />)</g:link>
            </ddbcommon:isLoggedIn>
          </li>
        </ul>
      </div>
    </div>
  </div>
</header>
<header class="hidden-phone">
<!--<![endif]-->

<!--[if IE]>
<header class="ie-mobile">
<![endif]-->

  <h1 class="invisible-but-readable">
    <g:message encodeAs="html" code="ddbnext.Heading_Header" />
  </h1>
  <div class="container">
    <div class="row">
      <!--[if lt IE 9]>
          <div class="nav widget span12" data-widget="NavigationWidget">
        <![endif]-->
      <nav class="widget span12" data-widget="NavigationWidget">
        <div class="row">
          <div class="span7">
            <g:link uri="/" class="navigation-header-logo"
              title="${message(code: 'ddbnext.Logo_Title')}"
              tabindex="-1">
              <r:img dir="images" file="logoHeaderSmall.png"
                alt="${message(code: 'ddbnext.Logo_Description')}" />
            </g:link>
            <div role="navigation">
              <ul class="navigation inline">
                <li
                  class="<ddb:isMappingActive context="${params}" testif="${[[controller: "index"]]}">active-default</ddb:isMappingActive>">
                  <g:link controller="index"><g:message encodeAs="html" code="ddbnext.Homepage" /></g:link>
                </li>
                <li
                  class="keep-in-front <ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "about"],[controller: "content", dir: "news"],[controller: "content", dir: "ddb"],[controller: "content", dir: "competence-network"],[controller: "institution"],[controller: "content", dir: "faq"]]}">active-default</ddb:isMappingActive>">
                  <g:link controller="content" params="[dir: 'about']"><g:message encodeAs="html" code="ddbnext.AboutUs" /></g:link>
                  <div class="arrow-container">
                    <div class="arrow-up"></div>
                  </div>
                  <ul>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'news']"><g:message encodeAs="html" code="ddbnext.News" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="institution" action="show"><g:message encodeAs="html" code="ddbnext.Institutions" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'ddb']"><g:message encodeAs="html" code="ddbnext.Participate" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'competence-network']"><g:message encodeAs="html" code="ddbnext.CompetenceNetwork" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'faq']"><g:message encodeAs="html" code="ddbnext.Faq" /></g:link>
                    </li>
                  </ul>
                </li>
                <li
                  class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "help"]]}">active-default</ddb:isMappingActive>">
                  <g:link controller="content" params="[dir: 'help']"><g:message encodeAs="html" code="ddbnext.Help" /></g:link>
                </li>
                <g:if test="${config.isExhibitionsFeaturesEnabled()}">
                  <!-- TODO add link to person pages -->
                  <li
                    class="keep-in-front <ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "exhibits"],[controller: "lists"]]}">active-default</ddb:isMappingActive>">
                    <g:link controller="lists"><g:message encodeAs="html" code="ddbnext.Discover" /></g:link>
                    <div class="arrow-container">
                      <div class="arrow-up"></div>
                    </div>
                    <ul>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "lists"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="lists" action="index"><g:message encodeAs="html" code="ddbnext.Favoriteslists" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "persons"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="persons"><g:message encodeAs="html" code="ddbnext.Personpages" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "exhibits"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="content" params="[dir: 'exhibits']"><g:message encodeAs="html" code="ddbnext.Exhibitions" /></g:link>
                      </li>
                    </ul>
                  </li>
                </g:if>
                <ddbcommon:isLoggedIn>
                  <li
                    class="keep-in-front <ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"],[controller: "user", action: "getSavedSearches"],[controller: "user", action: "profile"],[controller: "user", action: "passwordChangePage"],[controller: "user", action: "showApiKey"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active-default</ddb:isMappingActive>">
                    <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.MyDDB" /></g:link>
                    <div class="arrow-container">
                      <div class="arrow-up"></div>
                    </div>
                    <ul>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.Favorites" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "getSavedSearches"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="user" action="savedsearches"><g:message encodeAs="html" code="ddbnext.Searches" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "profile"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="user" action="profile"><g:message encodeAs="html" code="ddbcommon.Profile" /></g:link>
                      </li>
                      <li>
                        <g:link controller="user" action="doLogout"><g:message encodeAs="html" code="ddbcommon.Logout" /></g:link>
                      </li>
                    </ul>
                  </li>
                </ddbcommon:isLoggedIn>
              </ul>
            </div>
          </div>
          <div class="span5 toolbar">
            <div class="status-bar">
              <ddbcommon:isNotLoggedIn>
                <div class="login-wrapper">
                  <g:link class="login-link login-link-referrer" controller="user" params="${[referrer:grailsApplication.mainContext.getBean('de.ddb.common.GetCurrentUrlTagLib').getCurrentUrl()]}"> <g:message encodeAs="html" code="ddbcommon.Login_Button" /></g:link>
                </div>
              </ddbcommon:isNotLoggedIn>
              <ddbcommon:isLoggedIn>
                <div class="login-wrapper">
                  <span style="vertical-align:top;"><g:message encodeAs="html" code="ddbcommon.You_are_currently_logged_in_as" /></span>
                  <g:link controller="user" action="profile" class="login-username"><ddbcommon:getUserName /></g:link>
                  <div class="login-dropdown"></div>
                  <div class="arrow-container">
                    <div class="arrow-up"></div>
                  </div>
                  <ul class="selector logout">
                    <li><g:link class="logout-link" controller="user" action="doLogout"><g:message encodeAs="html" code="ddbcommon.Logout" /></g:link></li>
                  </ul>
                </div>
              </ddbcommon:isLoggedIn>
              <div class="header-spacer"></div>
              <div class="language-wrapper">
                <a href="#"><ddb:getCurrentLanguage /></a>
                <div class="arrow-container">
                  <div class="arrow-up"></div>
                </div>
                <ul class="selector language">
                  <li><ddb:getLanguageLink params="${params}" locale="de"
                      islocaleclass="nopointer">
                      <g:message encodeAs="html" code="ddbnext.language_de" />
                    </ddb:getLanguageLink></li>
                    <li><ddb:getLanguageLink params="${params}" locale="en"
                      islocaleclass="nopointer">
                      <g:message encodeAs="html" code="ddbnext.language_en" />
                    </ddb:getLanguageLink></li>
                </ul>
              </div>
            </div>
            <div class="search-header hidden-phone">
              <form method="get" role="search" id="form-search-header"
                action="<ddb:getSearchUrl controllerName="${controllerName}" actionName="${actionName}"/>">
                <label for="search-small"> <span><g:message encodeAs="html"
                      code="ddbnext.Search_text_field" /></span>
                </label>
                <input type="hidden" id="querycache"
                  value="<ddbcommon:getCookieFieldValue fieldname="query" />" />
                <input type="search" id="search-small" class="query"
                  name="query"
                  value="<ddbcommon:getCookieFieldValue fieldname="query" />"
                  autocomplete="off" />
                <button type="submit">
                  <!--[if !IE]><!-->
                  <g:message encodeAs="html" code="ddbnext.Go_Button" />
                  <!--<![endif]-->
                  <!--[if gt IE 8]>
                        <g:message encodeAs="html" code="ddbnext.Go_Button"/>
                      <![endif]-->
                </button>
                <div class="search-small-bottom">
                  <div class="keep-filters off">
                    <label class="checkbox"> 
                      <input id="keep-filters" type="checkbox" name="keepFilters" <g:if test="${keepFiltersChecked}">checked="checked"</g:if> />
                      <g:message encodeAs="html" code="ddbnext.Keep_filters"/>
                    </label>
                  </div>
                  <g:link class="link-adv-search"
                    controller="advancedsearch">
                    <g:message encodeAs="html" code="ddbnext.Advanced_search" />
                  </g:link>
                </div>
              </form>
            </div>
          </div>
        </div>
      </nav>
      <!--[if lt IE 9]>
        </div>
      <![endif]-->
    </div>
  </div>
</header>
<!--[if lt IE 9]>
  </div>
<![endif]-->

