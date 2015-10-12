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
<!--[if lt IE 9]>
  <div class="footer container" role="contentinfo">
<![endif]-->
<g:set var="menu" bean="mainMenuService"/>

<footer class="container">
  <div class="row">
    <h1 class="invisible-but-readable"><g:message encodeAs="html" code="ddbnext.Heading_Footer"/></h1>
    <div class="span12 legal">
      <div class="inner">
        <ul>
          <g:each var="menuItem" in="${menu.footerMenu}">
            <li>
              <g:link uri="${menuItem.uri}">${menuItem.label[ddb.getCurrentLocale()]}</g:link>
            </li>
          </g:each>
        </ul>
        <div class="build"><ddbcommon:getFrontendVersion /> / <ddbcommon:getBackendVersion/></div>
        <ddbcommon:getSocialIconsUrl />
      </div>
    </div>
  </div>
</footer>
<!--[if lt IE 9]>
  </div>
<![endif]-->
