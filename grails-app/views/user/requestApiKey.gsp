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
    
    <meta name="page" content="apirequest" />
    <meta name="layout" content="main" />
  
  </head>
  
  <body>
    <div class="row apirequest" >
      <div class="span12">
        <g:form method="post" name="user-api-form" class="form-horizontal" controller="user" action="requestApiKey" >
          <div class="well">
            <div class="profile-nav">
              <div>
                <h1><g:message code="ddbnext.Api_Access_Of"/> <g:getUserLabel /></h1>
              </div>
            </div>
            <div>
              <g:message code="ddbnext.Api_Description_Text" />
            </div>
            <div>
              <ul id="error-messages" class="off">
                <li><a><g:message code="ddbnext.Field_Required" /></a></li>
                <li><a><g:message code="ddbnext.Name_Compulsory_Characters_Number" /></a></li>
                <li><a><g:message code="ddbnext.Enter_A_Valid_Email" /></a></li>
              </ul>
            </div>

            
            <div class="control-group">
              <g:if test="${flash.error}">
                <div class="errors-container">
                  <ul class="unstyled">
                    <li><i class="icon-exclamation-sign"></i><span><g:message code="${flash.error}" /></span></li>
                  </ul>
                </div>
              </g:if>
              <div class="input-prepend">
                <input type="checkbox" class="api-checkbox" name="apiConfirmation">
              </div>
              <label class="checkbox-label control-label"><g:message code="ddbnext.Api_Confirmation" /></label>
            </div>

            <div class="control-group">
              <button type="submit" class="btn-padding" title="<g:message code="ddbnext.Api_Request"/>"><g:message code="ddbnext.Api_Request"/></button>
            </div>
          </div>
        </g:form>
      </div>
    </div>
  </body>
</html>