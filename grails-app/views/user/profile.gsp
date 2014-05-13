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

<title><g:message encodeAs="html" code="ddbnext.Profile" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

<meta name="page" content="userprofile" />
<meta name="layout" content="main" />

</head>

<body>
    <g:form method="post" id="user-profile-form" name="user-profile-form" class="form-horizontal" url="[controller:'user', action:'saveProfile']" >
        <g:set var="disableForOpenId"></g:set>
        <g:set var="offForOpenId"></g:set>
        <g:set var="newsletterCheck"></g:set>
        <g:if test="${user.openIdUser == true}">
            <g:set var="disableForOpenId">disabled</g:set>
            <g:set var="offForOpenId">off</g:set>
        </g:if>
        <g:if test="${user.newsletterSubscribed == true}">
            <g:set var="newsletterCheck">checked="checked"</g:set>
        </g:if>
        <g:if test="${errors != null && errors.size()>0}">
          <ddb:renderErrors errors="${errors}" />
        </g:if>
        <g:if test="${messages != null && messages.size()>0}">
          <ddb:renderMessages messages="${messages}" />
        </g:if>
        <input type="hidden" name="id" value="${ user.id }"/>
        <div>
            <div>
              <div class="static_content control-groups">
                <div class="profile-nav bb">
                  <div><h1><g:message encodeAs="html" code="ddbnext.User_Profile"/></h1></div>
                  <div><h3><g:message encodeAs="html" code="ddbnext.User.Data"/></h3></div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbnext.Username" /></label>
                  </div>
                  <div>
                    <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message encodeAs="html" code="ddbnext.Username" />" value="${user.username}" ${disableForOpenId}>
                  </div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbnext.Email" /></label>
                  </div>
                  <div>
                    <input type="text" class="profile-input" id="email" name="email" placeholder="<g:message encodeAs="html" code="ddbnext.Email" />" value="${user.email}" ${disableForOpenId}>
                  </div>
                </div>
                <div class="control-group">
                  <div>
                    <label><g:message encodeAs="html" code="ddbnext.User.First_Name" /></label>
                  </div>
                      <input type="text" class="profile-input" id="fname" name="fname" placeholder="<g:message encodeAs="html" code="ddbnext.User.First_Name" />" value="${user.firstname}" ${disableForOpenId}>
                </div>
                <div class="control-group ">
                    <div>
                      <label><g:message encodeAs="html" code="ddbnext.User.Last_Name" /></label>
                    </div>
                    <div>
                      <input type="text" class="profile-input" id="lname" name="lname" placeholder="<g:message encodeAs="html" code="ddbnext.User.Last_Name" />" value="${user.lastname}" ${disableForOpenId}>
                    </div>
                </div>
                <div class="control-group">
                  <label></label>
                    <button type="submit" class="btn-padding" title="<g:message encodeAs="html" code="ddbnext.Save"/>"><g:message encodeAs="html" code="ddbnext.Save"/></button>
                </div>
              </div>
  
              <%-- Account menu --%>
              <g:render template="accountMenu" />
            </div>

            <%-- Commented out temporarily by request from Gerke, see DDBNEXT-751
            <div class="control-group">
              <div class="controls checkbox">
                <div class="input-prepend">
                  <input type="checkbox" id="newsletter" class="profile-checkbox" name="newsletter" ${newsletterCheck}>
                </div>
              </div>
              <label class="checkbox-label control-label"><g:message encodeAs="none" code="ddbnext.Newsletter_Subscription" /></label>
            </div>
            --%>

        </div>
        <ul id="error-messages" class="off">
          <li><a><g:message encodeAs="html" code="ddbnext.Field_Required" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbnext.Name_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message encodeAs="html" code="ddbnext.Enter_A_Valid_Email" /></a></li>
        </ul>
    </g:form>
    <div id="msDeleteAccount" class="modal hide fade" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-header">
        <span title="<g:message encodeAs="html" code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
        <h3>
          <g:message encodeAs="html" code="ddbnext.delete_confirmation" />
        </h3>
      </div>
      <div class="modal-body">
        <g:message encodeAs="html" code="ddbnext.User.Really_Delete_Account" />
      </div>
      <div class="modal-footer">
        <button class="submit" data-dismiss="modal" id="delete-account-confirm"><g:message encodeAs="html" code="ddbnext.Ok" /></button>
        <button class="submit" data-dismiss="modal"><g:message encodeAs="html" code="ddbnext.Cancel" /></button>
      </div>
    </div>
  </body>
</html>
