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

<ddbcommon:doRedirectIfNotLoggedIn />

<title><g:message encodeAs="html" code="ddbcommon.Profile" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="userprofile" />
<meta name="layout" content="main" />

</head>

<body>
    <g:form method="post" id="user-profile-form" name="user-profile-form" class="form-horizontal" url="[controller:'user', action:'saveProfile']" >
        <g:set var="disableForOpenId"></g:set>
        <ddbcommon:isOpenIdUser>
            <g:set var="disableForOpenId">disabled</g:set>
        </ddbcommon:isOpenIdUser>
        <ddbcommon:renderErrors errors="${errors}"/>
        <ddbcommon:renderMessages messages="${messages}"/>
        <input type="hidden" name="id" value="${ user.id }"/>
        <div>
            <div>
              <div class="static_content control-groups">
                <div class="profile-nav bb">
                  <div><h1><g:message encodeAs="html" code="ddbcommon.User_Profile"/></h1></div>
                  <div><h3><g:message encodeAs="html" code="ddbcommon.User.Data"/></h3></div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbcommon.Username" />*</label>
                  </div>
                  <div>
                    <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message encodeAs="html" code="ddbcommon.Username" />" value="${user.username}" ${disableForOpenId}>
                  </div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbcommon.Email" />*</label>
                  </div>
                  <div>
                    <input type="text" class="profile-input" id="email" name="email" placeholder="<g:message encodeAs="html" code="ddbcommon.Email" />" value="${user.email}" ${disableForOpenId}>
                  </div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbcommon.User.First_Name" /></label>
                  </div>
                      <input type="text" class="profile-input" id="fname" name="fname" placeholder="<g:message encodeAs="html" code="ddbcommon.User.First_Name" />" value="${user.firstname}" ${disableForOpenId}>
                </div>
                <div class="control-group ">
                    <div>
                      <label><g:message encodeAs="html" code="ddbcommon.User.Last_Name" /></label>
                    </div>
                    <div>
                      <input type="text" class="profile-input" id="lname" name="lname" placeholder="<g:message encodeAs="html" code="ddbcommon.User.Last_Name" />" value="${user.lastname}" ${disableForOpenId}>
                    </div>
                </div>
                <div class="control-group">
                    <button type="submit" class="submit" title="<g:message encodeAs="html" code="ddbcommon.Save"/>"><g:message encodeAs="html" code="ddbcommon.Save"/></button>
                </div>
              </div>

              <%-- Account menu --%>
              <g:render template="accountMenu" />
            </div>

        </div>
        <ul id="error-messages" class="off">
          <li><a><g:message encodeAs="html" code="ddbcommon.Field_Required" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Name_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbcommon.Enter_A_Valid_Email" /></a></li>
        </ul>
    </g:form>
  </body>
</html>
