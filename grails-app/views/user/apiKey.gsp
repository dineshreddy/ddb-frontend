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
  
    <ddb:doRedirectIfNotLoggedIn />
    
    <title><g:message encodeAs="html" code="ddbnext.Api" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>
    
    <meta name="page" content="apikey" />
    <meta name="layout" content="main" />
  
  </head>
  
  <body>
    <div class="row apikey" >
      <div class="span12">
        <g:form method="post" name="apikey-form" id="apikey-form" class="form-horizontal" controller="user" action="deleteApiKey" >
          <div class="well">
            <div class="profile-nav">
              <div>
                <h1><g:message encodeAs="html" code="ddbnext.Api_Access_Of"/> <ddb:getUserLabel /></h1>
              </div>
            </div>
            
            <div class="control-group">
              <label class="control-label"><g:message encodeAs="html" code="ddbnext.Api_My_Key" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-wrench"></i></span>
                  <input type="text" class="profile-input apikey-input" name="apikey" placeholder="<g:message encodeAs="html" code="ddbnext.Api_My_Key" />" value="${user.apiKey}">
                  <button type="submit" class="apikey-delete" id="apikey-delete" title="<g:message encodeAs="html" code="ddbnext.Api_Key_Delete"/>"></button>
                </div>
              </div>
            </div>

            <div>
              <g:message encodeAs="html" code="ddbnext.Api_Agreed" args="${[apiKeyTermsUrl]}" />
              <br />            
              <g:message encodeAs="html" code="ddbnext.Api_Dokumentation" args="${[apiKeyDocUrl]}" />
            </div>            
            
          </div>
        </g:form>
      </div>
    </div>
    <div id="msDeleteApiKey" class="modal hide fade" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3>
          <g:message encodeAs="html" code="ddbnext.delete_confirmation" />
        </h3>
      </div>
      <div class="modal-body">
        <g:message encodeAs="html" code="ddbnext.Api_Key_Delete_Confirmation" />
      </div>
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" id="delete-apikey-confirm"><g:message encodeAs="html" code="ddbnext.Ok" /></button>
        <button class="submit" data-dismiss="modal"><g:message encodeAs="html" code="ddbnext.Cancel" /></button>
      </div>
    </div>
  </body>
</html>
