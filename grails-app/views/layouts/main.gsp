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
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    
    <title><g:layoutTitle default="Deutsche Digitale Bibliothek" /></title>

    <meta name="description" content="Deutsche Digitale Bibliothek" />
    <meta http-equiv="content-language" content="${g.currentLocale()}">    
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default" />

    <!-- TODO: uncomment the next two lines, once we find the icons -->
    <!-- link rel="apple-touch-icon" href="/apple-touch-icon.png"/ -->
    <!-- link rel="apple-touch-startup-image" href="320x460-ipad1004x768.png"/ -->

    <link rel="search" title="Deutsche Digitale Bibliothek" href=${resource(dir: '/', file: 'opensearch.osdx')} type="application/opensearchdescription+xml" />
    <r:require module="ddbnext" />
    <r:layoutResources />
    <g:layoutHead />

  </head>
  <body>
    <noscript>
      <div class="container">
        <div class="row">
          <div class="span12 warning">
            <span><g:message code="ddbnext.Warning_Non_Javascript"/></span>
          </div>
        </div>
      </div>
    </noscript>
    <g:render template="/mainHeader" />
    <div id="main-container" class="container" role="main">
      <g:layoutBody/>
    </div>
    <g:render template="/footer" />
    <g:render template="/jsVariables" />
    <jawr:script src="/i18n/messages.js"/>
    <r:layoutResources />
    
    <%-- 
    <script>
        $('#header-menu-btn').click(function(event) {
            console.log('menu btn clicked');
        });
    </script>
    --%>
    
    <g:piwik />
  </body>
</html>
