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
<html>
  <head>
    <title><g:message code="ddbnext.Sitemap"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    <meta name="page" content="sitemap"/>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h1>
      <g:message code="ddbnext.Sitemap"/>
    </h1>
    <ul class="plum-arrow">
      <g:each var="url" in="${urlset.url}">
        <li>
          <a href="${url.loc}">${url.loc}</a>
        </li>
      </g:each>
    </ul>
  </body>
</html>