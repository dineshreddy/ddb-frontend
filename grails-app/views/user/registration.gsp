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
<title><g:message encodeAs="html" code="ddbcommon.Registration" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="registration" />
<meta name="layout" content="main" />

</head>
<body>
    <g:if test="${errors != null && errors.size()>0}">
      <ddbcommon:renderErrors errors="${errors}" />
    </g:if>
    <div class="registration-container">
      <g:form method="post" id="registration-form" name="registration-form" class="form-horizontal" url="[controller:'user', action:'signup']" >

        <div><h1><g:message encodeAs="html" code="ddbcommon.Registration" /></h1></div>
        <div id="enduser" class="reg-subtitle"><span><g:message encodeAs="none" code="ddbcommon.register_enduser" args="${[registrationInfoUrl]}"/></span></div>

        <div class="control-group">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.Username" />*</label>
          <div>
            <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message encodeAs="html" code="ddbcommon.Username" />" value="${params.username}">
          </div>
        </div>

        <div class="control-group">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.User.First_Name" /></label>
          <div>
            <input type="text" class="profile-input" id="fname" name="fname" placeholder="<g:message encodeAs="html" code="ddbcommon.User.First_Name" />" value="${params.fname}">
          </div>
        </div>

        <div class="control-group ">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.User.Last_Name" /></label>
          <div>
            <input type="text" class="profile-input" id="lname" name="lname" placeholder="<g:message encodeAs="html" code="ddbcommon.User.Last_Name" />" value="${params.lname}">
          </div>
        </div>

        <div class="control-group">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.Email" />*</label>
          <div>
            <input type="text" class="profile-input" id="email" name="email" placeholder="<g:message encodeAs="html" code="ddbcommon.Email" />" value="${params.email}">
          </div>
        </div>

        <div class="control-group">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.Your_Password" />*</label>
          <div>
            <input type="Password" id="passwd" class="profile-input" name="passwd" placeholder="<g:message encodeAs="html" code="ddbcommon.Your_Password" />" value="${params.passwd}">
          </div>
        </div>

        <div class="control-group">
          <label class="reg-label"><g:message encodeAs="html" code="ddbcommon.Confirm_password" />*</label>
          <div>
            <input type="Password" id="conpasswd" class="profile-input" name="conpasswd" placeholder="<g:message encodeAs="html" code="ddbcommon.Confirm_password" />" value="${params.conpasswd}">
          </div>
        </div>

        <div class="control-group">
          <input type="checkbox" id="termOfUse" name="termOfUse">
          <label class="reg-checkbox-label">
            <g:message encodeAs="none" code="ddbcommon.I_Have_Read" args="${[accountTermsUrl, accountPrivacyUrl]}"/>*
          </label>
        </div>

        <div class="control-group">
          <input type="checkbox" id="newsletter" name="newsletter">
          <label class="reg-checkbox-label">
            <g:message encodeAs="none" code="ddbnext.Newsletter_Subscription"/>
          </label>
        </div>

        <div class="control-group">
          <button type="submit" class="btn-padding"><g:message encodeAs="html" code="ddbcommon.Register" /></button>
        </div>
        <ul id="error-messages" class="off">
          <li><a><g:message encodeAs="html" code="ddbcommon.Field_Required" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Name_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Password_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Enter_A_Valid_Email" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Insert_Again_The_Password" /></a></li>
        </ul>
      </g:form>
    </div>
</body>
</html>