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
    
    <meta name="page" content="apirequest" />
    <meta name="layout" content="main" />
  
  </head>
  
  <body>
    <div class="apirequest" >
      <div class="static_content">
        <g:form method="post" name="user-api-form" class="form-horizontal" controller="user" action="requestApiKey" >
          <div>
            <div class="profile-nav bb">
              <div><h1><g:message encodeAs="html" code="ddbnext.User_Profile"/></h1></div>
              <div><h3><g:message encodeAs="html" code="ddbnext.Api_Access"/></h3></div>
            </div>
            
            <div>
              <p>
                <g:message encodeAs="none" code="ddbnext.Api_Description_Text" />
              </p>
            </div>
            <div>
              <ul id="error-messages" class="off">
                <li><a><g:message encodeAs="html" code="ddbnext.Field_Required" /></a></li>
                <li><a><g:message encodeAs="html" code="ddbnext.Name_Compulsory_Characters_Number" /></a></li>
                <li><a><g:message encodeAs="html" code="ddbnext.Enter_A_Valid_Email" /></a></li>
              </ul>
            </div>

            
            <div class="control-group">
              <g:if test="${flash.error}">
                <div class="errors-container">
                  <ul class="unstyled">
                    <li><i class="icon-exclamation-sign"></i><span><g:message encodeAs="html" code="${flash.error}" /></span></li>
                  </ul>
                </div>
              </g:if>
              <g:if test="${flash.message}">
                <div class="messages-container">
                  <ul class="unstyled">
                    <li><i class="icon-ok-circle"></i><span><g:message encodeAs="html" code="${flash.message}" /></span></li>
                  </ul>
                </div>
              </g:if>
              <div class="input-prepend">
                <input type="checkbox" class="api-checkbox" name="apiConfirmation">
              </div>
              <label class="checkbox-label control-label"><g:message encodeAs="none" code="ddbnext.Api_Confirmation" args="${[apiKeyTermsUrl]}"/></label>
            </div>

            <div class="control-group">
              <button type="submit" class="btn-padding" title="<g:message encodeAs="html" code="ddbnext.Api_Request"/>"><g:message encodeAs="html" code="ddbnext.Api_Request"/></button>
            </div>
          </div>
        </g:form>
      </div>
      <%-- Account menu --%>
      <g:render template="accountMenu" />
    </div>
  </body>
</html>
