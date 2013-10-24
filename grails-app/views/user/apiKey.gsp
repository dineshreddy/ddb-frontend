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
  
    <g:redirectIfNotLoggedIn />
    
    <title><g:message code="ddbnext.Api" /> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
    
    <meta name="page" content="apikey" />
    <meta name="layout" content="main" />
  
  </head>
  
  <body>
    <div class="row apikey" >
      <div class="span12">
        <g:form method="post" name="user-api-form" class="form-horizontal" controller="user" action="deleteApiKey" >
          <div class="well">
            <div class="profile-nav">
              <div>
                <h1><g:message code="ddbnext.Api_Access_Of"/> <g:getUserLabel /></h1>
              </div>
            </div>
            
            <div class="control-group">
              <label class="control-label"><g:message code="ddbnext.Api_My_Key" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-wrench"></i></span>
                  <input type="text" class="profile-input apikey-input" name="apikey" placeholder="<g:message code="ddbnext.Api_My_Key" />" value="${user.apiKey}">
                  <button type="submit" class="apikey-delete" title="<g:message code="ddbnext.Api_Key_Delete"/>"></button>
                </div>
              </div>
            </div>

            <div>
              <g:message code="ddbnext.Api_Agreed" />
              <br />            
              <g:message code="ddbnext.Api_Dokumentation" />
            </div>            
            
          </div>
        </g:form>
      </div>
    </div>
  </body>
</html>
