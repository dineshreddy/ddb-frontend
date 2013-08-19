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

<title><g:message code="ddbnext.Password_Reset" /> - <g:message
    code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="passwordreset" />
<meta name="layout" content="main" />

</head>

<body>
    <g:form method="post" id="password-reset-form" name="password-reset-form" class="form-horizontal" url="[controller:'user', action:'passwordReset']" >
        <g:if test="${errors != null && errors.size()>0}">
          <g:renderErrors errors="${errors}"></g:renderErrors>
        </g:if>
        <g:if test="${messages != null && messages.size()>0}">
          <g:renderMessages messages="${messages}"></g:renderMessages>
        </g:if>
        <div class="well">
            <div class="profile-nav">
                <div><h1><g:message code="ddbnext.Reset_Password_Label"/></h1></div>
            </div>

            <div class="control-group bt-white">
              <label class="control-label"><g:message code="ddbnext.Username_Or_Email" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-user"></i></span>
                  <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message code="ddbnext.Username_Or_Email" />" value="${params.username}">
                </div>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label"></label>
              <div class="controls">
                <button type="submit" class="btn-padding" title="<g:message code="ddbnext.Reset_Password_Commit"/>"><g:message code="ddbnext.Reset_Password_Commit"/></button>
              </div>
            </div>
        </div>
    </g:form>
</body>
</html>
