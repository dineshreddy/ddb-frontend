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

<html>
  <head>
    <title><g:message code="ddbnext.Compare_Header" /> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    
    <meta name="page" content="compare" />
    <meta name="layout" content="main" />
    
    <ddb:getSocialmediaMeta likeTitle="${g.message(code: "ddbnext.Compare_Header") + " - " + g.message(code: "ddbnext.Deutsche_Digitale_Bibliothek")}" likeUrl="${baseUrl + request.forwardURI}"/>
    
  </head>
  <body>
    <div class="row">
      <div class="span12 compare">
        <div class="row">
          <div class="span12 compare-header">
            <h1><g:message code="ddbnext.Compare_Header" /></h1>    
          </div>
        </div>
        <g:render template="compareLinks" />
        <div class="row">
          <div class="span6">
            <div class="compare-body">
              <g:render template="compareItem" model="${modelItem1}"/>
            </div>
          </div>
          <div class="span6">
            <div class="compare-body">
              <g:render template="compareItem" model="${modelItem2}"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>