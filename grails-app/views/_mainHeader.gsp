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
  <r:img dir="images" file="logoHeader.png" alt="" />
</div>

<!--[if lt IE 9]>
  <div class="header" role="contentinfo">
<![endif]-->

<!--[if !IE]><!-->
<header class="navbar navbar-fixed-top visible-phone">
  <div class="navbar-inner">
    <div class="container">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar" style="visibility: hidden;"></span>
      </button>
      <g:link uri="/" class="brand"
        title="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}"
        tabindex="-1">
        <r:img dir="images" file="mobileLogo.png"
          alt="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}" />
      </g:link>
      <div class="nav-collapse collapse">
        <ul class="nav nav-list">
          <li class=""><g:form class="navbar-search pull-left"
              method="get" role="search" id="form-search-header-mobile"
              url="[controller:'search', action:'results']">
              <input type="search" class="query" name="query"
                placeholder="Suche"
                value="<ddb:getCookieFieldValue fieldname="query" />">
              <button type="submit">
                <g:message code="ddbnext.Go_Button" />
              </button>
            </g:form></li>

          <li class="<ddb:isMappingActive context="${params}"
              testif="${[[controller: "content", dir: "about"]]}">active</ddb:isMappingActive>">
            <g:link controller="content" params="[dir: 'about']">
              <g:message code="ddbnext.AboutUs" />
            </g:link>
            <ul class="nav">
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'news']"><g:message code="ddbnext.News" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active</ddb:isMappingActive>">
                <g:link controller="institution" action="show" fragment="list"><g:message code="ddbnext.Institutions" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'ddb']"><g:message code="ddbnext.Participate" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'competence-network']"><g:message code="ddbnext.CompetenceNetwork" /></g:link>
              </li>
              <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active</ddb:isMappingActive>">
                <g:link controller="content" params="[dir: 'faq']"><g:message code="ddbnext.Faq" /></g:link>
              </li>
            </ul><!-- /end of .nav -->
          </li>
          <li class="<ddb:isMappingActive context="${params}"
            testif="${[[controller: "content", dir: "help"]]}">active</ddb:isMappingActive>">
            <g:link controller="content" params="[dir: 'help']"><g:message code="ddbnext.Help" /></g:link>
          </li><!-- /end of help -->
            <ddb:isLoggedIn>
              <li class="">
                <g:link controller="favoritesview" action="favorites"><g:message code="ddbnext.MyDDB" /></g:link>
                <ul class="nav">
                  <li class="">
                    <g:link controller="favoritesview" action="favorites"><g:message code="ddbnext.Favorites" /></g:link>
                  </li>
                  <li class="">
                    <g:link controller="user" action="savedsearches"><g:message code="ddbnext.Searches" /></g:link>
                  </li>
                  <li class="">
                    <g:link controller="user" action="profile"><g:message code="ddbnext.Profile" /></g:link>
                  </li>
                </ul>
              </li>
            </ddb:isLoggedIn>
            <li class=""><a>
              <g:message code="ddbnext.ChangeLanguage" />
            </a>
            <ul class="nav">
              <li class="<ddb:isCurrentLanguage locale="de">selected-language</ddb:isCurrentLanguage>">
                <ddb:getLanguageLink params="${params}" locale="de" islocaleclass="nopointer">
                  <g:message code="ddbnext.language_de" />
                </ddb:getLanguageLink>
              </li>
              <li class="<<ddb:isCurrentLanguage locale="en">selected-language</ddb:isCurrentLanguage>">
                <ddb:getLanguageLink params="${params}" locale="en" islocaleclass="nopointer">
                  <g:message code="ddbnext.language_en" />
                </ddb:getLanguageLink>
              </li>
            </ul>
          </li>
          <li class="">
            <ddb:isNotLoggedIn>
              <g:link controller="user"><g:message code="ddbnext.Login" /></g:link>
            </ddb:isNotLoggedIn>
            <ddb:isLoggedIn>
              <g:link controller="user" action="doLogout"><g:message code="ddbnext.Logout" /> (<ddb:getUserName />)</g:link>
            </ddb:isLoggedIn>
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
    <g:message code="ddbnext.Heading_Header" />
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
              title="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}"
              tabindex="-1">
              <r:img dir="images" file="logoHeader.png"
                alt="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}" />
            </g:link>
            <div role="navigation">
              <ul class="navigation inline">
                <li
                  class="root <ddb:isMappingActive context="${params}" testif="${[[controller: "advancedsearch"]]}">active-default</ddb:isMappingActive><ddb:isMappingActive context="${params}" testif="${[[controller: "index"]]}">active-closed</ddb:isMappingActive>">
                  <g:link uri="/"><g:message code="ddbnext.Search" /></g:link>
                  <ul>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "advancedsearch"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="advancedsearch"><g:message code="ddbnext.Advanced_search" /></g:link>
                    </li>
                  </ul>
                </li>
                <li
                  class="keep-in-front <ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "about"],[controller: "content", dir: "news"],[controller: "content", dir: "ddb"],[controller: "content", dir: "competence-network"],[controller: "institution"],[controller: "content", dir: "faq"]]}">active-default</ddb:isMappingActive>">
                  <g:link controller="content" params="[dir: 'about']"><g:message code="ddbnext.AboutUs" /></g:link>
                  <ul>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'news']"><g:message code="ddbnext.News" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="institution" action="show"><g:message code="ddbnext.Institutions" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'ddb']"><g:message code="ddbnext.Participate" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'competence-network']"><g:message code="ddbnext.CompetenceNetwork" /></g:link>
                    </li>
                    <li
                      class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active-default</ddb:isMappingActive>">
                      <g:link controller="content" params="[dir: 'faq']"><g:message code="ddbnext.Faq" /></g:link>
                    </li>
                  </ul>
                </li>
                <li
                  class="<ddb:isMappingActive context="${params}" testif="${[[controller: "content", dir: "help"]]}">active-default</ddb:isMappingActive>">
                  <g:link controller="content" params="[dir: 'help']"><g:message code="ddbnext.Help" /></g:link>
                </li>
                <ddb:isLoggedIn>
                  <li
                    class="keep-in-front <ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"],[controller: "user", action: "getSavedSearches"],[controller: "user", action: "profile"],[controller: "user", action: "passwordChangePage"],[controller: "user", action: "showApiKey"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active-default</ddb:isMappingActive>">
                    <g:link controller="favoritesview" action="favorites"><g:message code="ddbnext.MyDDB" /></g:link>
                    <ul>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="favoritesview" action="favorites"><g:message code="ddbnext.Favorites" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "getSavedSearches"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="user" action="savedsearches"><g:message code="ddbnext.Searches" /></g:link>
                      </li>
                      <li
                        class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "profile"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active-default</ddb:isMappingActive>">
                        <g:link controller="user" action="profile"><g:message code="ddbnext.Profile" /></g:link>
                      </li>
                      <li class="">
                        <g:link controller="user" action="doLogout"><g:message code="ddbnext.Logout" /></g:link>
                      </li>
                    </ul>
                  </li>
                </ddb:isLoggedIn>
              </ul>
            </div>
          </div>
          <div class="span5 toolbar">
            <div class="status-bar">
              <ddb:isNotLoggedIn>
                <div class="login-wrapper">
                  <g:link controller="user"><g:message code="ddbnext.Login" /></g:link>
                </div>
              </ddb:isNotLoggedIn>
              <ddb:isLoggedIn>
                <div class="login-wrapper">
                  <span style="vertical-align:top;"><g:message code="ddbnext.You_are_currently_logged_in_as" /></span>
                  <g:link controller="user" action="profile" class="login-username"><ddb:getUserName /></g:link>
                  <div class="login-dropdown"></div>
                  <ul class="selector logout">
                    <li><g:link controller="user" action="doLogout"><g:message code="ddbnext.Logout" /></g:link></li>
                  </ul>
                </div>
              </ddb:isLoggedIn>
              <div class="header-spacer"></div>
              <div class="language-wrapper">
                <a href="#"> <ddb:getCurrentLanguage />
                </a>
                <ul class="selector language">
                  <li><ddb:getLanguageLink params="${params}" locale="de"
                      islocaleclass="nopointer">
                      <g:message code="ddbnext.language_de" />
                    </ddb:getLanguageLink></li>
                    <li><ddb:getLanguageLink params="${params}" locale="en"
                      islocaleclass="nopointer">
                      <g:message code="ddbnext.language_en" />
                    </ddb:getLanguageLink></li>
                </ul>
              </div>
            </div>
            <div class="search-header hidden-phone">
              <g:form method="get" role="search" id="form-search-header"
                url="[controller:'search', action:'results']">
                <label for="search-small"> <span><g:message
                      code="ddbnext.Search_text_field" /></span>
                </label>
                <input type="hidden" id="querycache"
                  value="<ddb:getCookieFieldValue fieldname="query" />" />
                <input type="search" id="search-small" class="query"
                  name="query"
                  value="<ddb:getCookieFieldValue fieldname="query" />"
                  autocomplete="off" />
                <button type="submit">
                  <!--[if !IE]><!-->
                  <g:message code="ddbnext.Go_Button" />
                  <!--<![endif]-->
                  <!--[if gt IE 8]>
                        <g:message code="ddbnext.Go_Button"/>
                      <![endif]-->
                </button>
                <span class="contextual-help hidden-phone hidden-tablet"
                  title="${g.message(code: "ddbnext.Search_Hint", 
                                     args: [('<a href="' + createLink(controller: "content",
                                                                      params: [dir: 'help', id: 'search-simple']) + '">').encodeAsHTML(),
                                            '</a>'],
                                     encodeAs: "none")}"
                  data-content="${g.message(code: "ddbnext.Search_Hint", 
                                            args: [('<a href="' + createLink(controller: "content",
                                                                             params: [dir: 'help', id: 'search-simple']) + '">').encodeAsHTML(),
                                                   '</a>'],
                                            encodeAs: "none")}">
                </span>
                <g:link class="link-adv-search"
                  controller="advancedsearch">
                  <g:message code="ddbnext.Advanced_search" />
                </g:link>
                <div class="tooltip off hasArrow"></div>
              </g:form>
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

