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
<footer class="container">
  <div class="row">
    <h1 class="invisible-but-readable"><g:message encodeAs="html" code="ddbnext.Heading_Footer"/></h1>
    <div class="span12 legal">
      <div class="inner">
        <small><g:message encodeAs="html" code="ddbnext.Copyright_Deutsche_Digitale_Bibliothek"/></small>
        <ul>
          <li><g:link controller="content" params="[dir: 'terms']"><g:message encodeAs="html" code="ddbnext.Terms_of_Use"/></g:link></li>
          <li><g:link controller="content" params="[dir: 'privacy']"><g:message encodeAs="html" code="ddbnext.Privacy_Policy"/></g:link></li>
          <li><g:link controller="content" params="[dir: 'publisher']"><g:message encodeAs="html" code="ddbnext.Publisher"/></g:link></li>
          <li><g:link controller="content" params="[dir: 'sitemap']"><g:message encodeAs="html" code="ddbnext.Sitemap"/></g:link></li>
          <li><g:link controller="content" params="[dir: 'contact']"><g:message encodeAs="html" code="ddbnext.Contact"/></g:link></li>
        </ul>
        <div class="build"><ddb:getFrontendVersion /> / <ddb:getBackendVersion/></div>
        <div class="twitter">
          <g:message code="ddbnest.follow"/>:
          <a href="https://twitter.com/ddbkultur" target="_blank">
            Twitter
          </a>
        </div>
      </div>
    </div>
  </div>
</footer>
<!--[if lt IE 9]>
  </div>
<![endif]-->
