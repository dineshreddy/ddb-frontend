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
<div class="print-logo off">
  <r:img dir="images" file="logo_header.png" alt="" />
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
        <r:img dir="images" file="mobile-logo.png"
          alt="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}" />
      </g:link>
      <div class="nav-collapse collapse">
        <ul class="nav nav-list">
          <li class=""><g:form class="navbar-search pull-left"
              method="get" role="search" id="form-search-header-mobile"
              url="[controller:'search', action:'results']">
              <input type="search" class="query" name="query"
                placeholder="Suche"
                value="<g:getCookieFieldValue fieldname="query" />">
              <button type="submit">
                <g:message code="ddbnext.Go_Button" />
              </button>
            </g:form></li>

          <li class="<g:isMappingActive context="${params}"
              testif="${[[controller: "content", dir: "about"]]}">active</g:isMappingActive>">
            <g:link controller="content" params="[dir: 'about']">
              <g:message code="ddbnext.AboutUs" />
            </g:link>
            <ul class="nav">
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active</g:isMappingActive>">
                <g:link controller="content" params="[dir: 'news']"><g:message code="ddbnext.News" /></g:link>
              </li>
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active</g:isMappingActive>">
                <g:link controller="institution" action="show" fragment="list"><g:message code="ddbnext.Institutions" /></g:link>
              </li>
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active</g:isMappingActive>">
                <g:link controller="content" params="[dir: 'ddb']"><g:message code="ddbnext.Participate" /></g:link>
              </li>
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active</g:isMappingActive>">
                <g:link controller="content" params="[dir: 'competence-network']"><g:message code="ddbnext.CompetenceNetwork" /></g:link>
              </li>
            </ul><!-- /end of .nav -->
          </li>
          <li class="<g:isMappingActive context="${params}"
            testif="${[[controller: "content", dir: "help"]]}">active</g:isMappingActive>">
            <g:link controller="content" params="[dir: 'help']"><g:message code="ddbnext.Help" /></g:link>
            <ul class="nav">
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active</g:isMappingActive>">
                <g:link controller="content" params="[dir: 'faq']"><g:message code="ddbnext.Faq" /></g:link>
              </li>
              <li class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "tutorial"]]}">active</g:isMappingActive>">
                <g:link controller="content" params="[dir: 'tutorial']"><g:message code="ddbnext.Tutorial" /></g:link>
              </li>
            </ul>
          </li><!-- /end of help -->
            <g:isLoggedIn>
              <li class="">
                <g:link controller="user" action="favorites"><g:message code="ddbnext.MyDDB" /></g:link>
                <ul class="nav">
                  <li class="">
                    <g:link controller="user" action="favorites"><g:message code="ddbnext.Favorites" /></g:link>
                  </li>
                  <li class="">
                    <g:link controller="user" action="profile"><g:message code="ddbnext.Profile" /></g:link>
                  </li>
                </ul>
              </li>
            </g:isLoggedIn>
            <li class=""><a>
              <g:message code="ddbnext.ChangeLanguage" />
            </a>
            <ul class="nav">
              <li class="<g:isCurrentLanguage locale="de">selected-language</g:isCurrentLanguage>">
                <g:languageLink params="${params}" locale="de" islocaleclass="nopointer">
                  <g:message code="ddbnext.language_de" />
                </g:languageLink>
              </li>
              <li class="<g:isCurrentLanguage locale="en">selected-language</g:isCurrentLanguage>">
                <g:languageLink params="${params}" locale="en" islocaleclass="nopointer">
                  <g:message code="ddbnext.language_en" />
                </g:languageLink>
              </li>
            </ul>
          </li>
          <li class="">
            <g:isNotLoggedIn>
              <g:link controller="user"><g:message code="ddbnext.Login" /></g:link>
            </g:isNotLoggedIn>
            <g:isLoggedIn>
              <g:link controller="user" action="doLogout"><g:message code="ddbnext.Logout" /> (<g:getUserName />)</g:link>
            </g:isLoggedIn>
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
              <r:img dir="images" file="logo_header.png"
                alt="${message(code: 'ddbnext.Deutsche_Digitale_Bibliothek')}" />
            </g:link>
            <div role="navigation">
              <ul class="navigation inline">
                <li
                  class="root <g:isMappingActive context="${params}" testif="${[[controller: "advancedsearch"]]}">active-default</g:isMappingActive><g:isMappingActive context="${params}" testif="${[[controller: "index"]]}">active-closed</g:isMappingActive>">
                  <g:link uri="/">
                    <g:message code="ddbnext.Search" />
                  </g:link>
                  <ul>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "advancedsearch"]]}">active-default</g:isMappingActive>">
                      <g:link controller="advancedsearch">
                        <g:message code="ddbnext.Advanced_search" />
                      </g:link>
                    </li>
                  </ul>
                </li>
                <li
                  class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "about"],[controller: "content", dir: "news"],[controller: "content", dir: "ddb"],[controller: "content", dir: "competence-network"],[controller: "institution"]]}">active-default</g:isMappingActive>">
                  <g:link controller="content" params="[dir: 'about']">
                    <g:message code="ddbnext.AboutUs" />
                  </g:link>
                  <ul>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "news"]]}">active-default</g:isMappingActive>">
                      <g:link controller="content"
                        params="[dir: 'news']">
                        <g:message code="ddbnext.News" />
                      </g:link>
                    </li>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "institution"]]}">active-default</g:isMappingActive>">
                      <g:link controller="institution" action="show">
                        <g:message code="ddbnext.Institutions" />
                      </g:link>
                    </li>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "ddb"]]}">active-default</g:isMappingActive>">
                      <g:link controller="content" params="[dir: 'ddb']">
                        <g:message code="ddbnext.Participate" />
                      </g:link>
                    </li>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "competence-network"]]}">active-default</g:isMappingActive>">
                      <g:link controller="content"
                        params="[dir: 'competence-network']">
                        <g:message code="ddbnext.CompetenceNetwork" />
                      </g:link>
                    </li>
                  </ul>
                </li>
                <li
                  class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "help"],[controller: "content", dir: "faq"],[controller: "content", dir: "tutorial"]]}">active-default</g:isMappingActive>">
                  <g:link controller="content" params="[dir: 'help']">
                    <g:message code="ddbnext.Help" />
                  </g:link>
                  <ul>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "faq"]]}">active-default</g:isMappingActive>">
                      <g:link controller="content" params="[dir: 'faq']">
                        <g:message code="ddbnext.Faq" />
                      </g:link>
                    </li>
                    <li
                      class="<g:isMappingActive context="${params}" testif="${[[controller: "content", dir: "tutorial"]]}">active-default</g:isMappingActive>">
                      <g:link controller="content"
                        params="[dir: 'tutorial']">
                        <g:message code="ddbnext.Tutorial" />
                      </g:link>
                    </li>
                  </ul>
                </li>
                <g:isLoggedIn>
                  <li
                    class="<g:isMappingActive context="${params}" testif="${[[controller: "user", action: "favorites"],[controller: "user", action: "profile"],[controller: "user", action: "passwordChangePage"]]}">active-default</g:isMappingActive>">
                    <g:link controller="user" action="favorites">
                      <g:message code="ddbnext.MyDDB" />
                    </g:link>
                    <ul>
                      <li
                        class="<g:isMappingActive context="${params}" testif="${[[controller: "user", action: "favorites"]]}">active-default</g:isMappingActive>">
                        <g:link controller="user" action="favorites">
                          <g:message code="ddbnext.Favorites" />
                        </g:link>
                      </li>
                      <li
                        class="<g:isMappingActive context="${params}" testif="${[[controller: "user", action: "profile"]]}">active-default</g:isMappingActive>">
                        <g:link controller="user" action="profile">
                          <g:message code="ddbnext.Profile" />
                        </g:link>
                      </li>
                      <li class="">
                        <g:link controller="user" action="doLogout">
                          <g:message code="ddbnext.Logout" />
                        </g:link>
                      </li>
                    </ul>
                  </li>
                </g:isLoggedIn>
              </ul>
            </div>
          </div>
          <div class="span5 toolbar">
            <g:isNotLoggedIn>
              <div class="login-wrapper">
                <g:link controller="user"><g:message code="ddbnext.Login" /></g:link>
              </div>
            </g:isNotLoggedIn>
            <g:isLoggedIn>
              <div class="login-wrapper">
                <g:message code="ddbnext.You_are_currently_logged_in_as" />
                <g:link controller="user" action="profile" class="login-username"><g:getUserName /></g:link><div class="login-dropdown"></div>
                <ul class="selector logout">
                  <li><g:link controller="user" action="doLogout"><g:message code="ddbnext.Logout" /></g:link></li>
                </ul>
              </div>
            </g:isLoggedIn>
            <div class="header-spacer"></div>
            <div class="language-wrapper">
              <a href="#"> <g:currentLanguage />
              </a>
              <ul class="selector language">
                <li><g:languageLink params="${params}" locale="de"
                    islocaleclass="nopointer">
                    <g:message code="ddbnext.language_de" />
                  </g:languageLink></li>
                <li><g:languageLink params="${params}" locale="en"
                    islocaleclass="nopointer">
                    <g:message code="ddbnext.language_en" />
                  </g:languageLink></li>
              </ul>
            </div>
            <div class="search-header hidden-phone">
              <g:form method="get" role="search" id="form-search-header"
                url="[controller:'search', action:'results']">
                <label for="search-small"> <span><g:message
                      code="ddbnext.Search_text_field" /></span>
                </label>
                <input type="hidden" id="querycache"
                  value="<g:getCookieFieldValue fieldname="query" />" />
                <input type="search" id="search-small" class="query"
                  name="query"
                  value="<g:getCookieFieldValue fieldname="query" />"
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
                  title="<g:message code="ddbnext.Search_Hint"
                          args="${[('<a href="' + createLink(controller:"content", params:[dir:'help', id:'search-simple']) + '">').encodeAsHTML(),('</a>').encodeAsHTML()]}"
                          default="ddbnext.Search_Hint"/>"
                  data-content="<g:message code="ddbnext.Search_Hint"
                          args="${[('<a href="' + createLink(controller:"content", params:[dir:'help', id:'search-simple']) + '">').encodeAsHTML(),('</a>').encodeAsHTML()]}"
                          default="ddbnext.Search_Hint"/>">
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
