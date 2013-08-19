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
<title><g:message code="ddbnext.Registration" /> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="registration" />
<meta name="layout" content="main" />

</head>
<body>
    <g:if test="${errors != null && errors.size()>0}">
      <g:renderErrors errors="${errors}"></g:renderErrors>
    </g:if>
    <div class="well">
      <g:form method="post" id="registration-form" name="registration-form" class="form-horizontal" url="[controller:'user', action:'signup']" >

        <div><h1><g:message code="ddbnext.Sign_up_here" /></h1></div>
        <div class="reg-subtitle bt-white"><h3><g:message code="ddbnext.Mandatory" /></h3></div>

        <div class="control-group">
          <label class="control-label"><g:message code="ddbnext.Username" />*</label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-user"></i></span>
              <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message code="ddbnext.Username" />" value="${params.username}">
            </div>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><g:message code="ddbnext.User.First_Name" /></label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-user"></i></span>
              <input type="text" class="profile-input" id="fname" name="fname" placeholder="<g:message code="ddbnext.User.First_Name" />" value="${params.fname}">
            </div>
          </div>
        </div>

        <div class="control-group ">
          <label class="control-label"><g:message code="ddbnext.User.Last_Name" /></label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-user"></i></span>
              <input type="text" class="profile-input" id="lname" name="lname" placeholder="<g:message code="ddbnext.User.Last_Name" />" value="${params.lname}">
            </div>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><g:message code="ddbnext.Email" />*</label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-envelope"></i></span>
              <input type="text" class="profile-input" id="email" name="email" placeholder="<g:message code="ddbnext.Email" />" value="${params.email}">
            </div>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><g:message code="ddbnext.Your_Password" />*</label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-lock"></i></span>
              <input type="Password" id="passwd" class="profile-input" name="passwd" placeholder="<g:message code="ddbnext.Your_Password" />" value="${params.passwd}">
            </div>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label"><g:message code="ddbnext.Confirm_password" />*</label>
          <div class="controls">
            <div class="input-prepend">
              <span class="add-on"><i class="icon-lock"></i></span>
              <input type="Password" id="conpasswd" class="profile-input" name="conpasswd" placeholder="<g:message code="ddbnext.Confirm_password" />" value="${params.conpasswd}">
            </div>
          </div>
        </div>

        <div class="control-group">
          <div class="controls checkbox">
            <div class="input-prepend">
              <input type="checkbox" id="termOfUse" name="termOfUse">
            </div>
          </div>
          <label class="checkbox-label control-label">
            <g:message code="ddbnext.I_Have_Read" 
                       args="${['<a href="' + createLink(controller:"content", params:[dir:'terms']) + '">','</a>','<a href="' + createLink(controller:"content", params:[dir:'privacy']) + '">','</a>']}" 
                       default="ddbnext.I_Have_Read"/>*
          </label>
        </div>

        <div class="control-group">
          <label class="control-label"></label>
          <div class="controls">
            <button type="submit" class="btn-padding"><g:message code="ddbnext.User.Create_Account" /></button>
          </div>
        </div>
        <ul id="error-messages" class="off">
          <li><a><g:message code="ddbnext.Field_Required" /></a></li>
          <li><a><g:message code="ddbnext.Name_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message code="ddbnext.Password_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message code="ddbnext.Enter_A_Valid_Email" /></a></li>
          <li><a><g:message code="ddbnext.Insert_Again_The_Password" /></a></li>
        </ul>
      </g:form>
    </div>
</body>
</html>