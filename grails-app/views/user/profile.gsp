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

<title><g:message code="ddbnext.Profile" /> - <g:message
    code="ddbnext.Deutsche_Digitale_Bibliothek" /></title>

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
          <g:renderErrors errors="${errors}"></g:renderErrors>
        </g:if>
        <g:if test="${messages != null && messages.size()>0}">
          <g:renderMessages messages="${messages}"></g:renderMessages>
        </g:if>
        <input type="hidden" name="id" value="${ user.id }"/>
        <div class="well">
            <div class="profile-nav">
                <div><h1><g:message code="ddbnext.User_Profile"/> <g:getUserLabel /></h1></div>
            </div>
            <div class="profile-links bt-white">
                <a class="profile-link" title="<g:message code="ddbnext.Favorites" />" class="persist" href="${createLink(controller="user",action: 'favorites', params:[:])}">
                    <g:message code="ddbnext.Favorites" /> (${favoritesCount})
                </a>
                <a class="profile-link ${offForOpenId}" title="<g:message code="ddbnext.Change_Password_Link" />" class="persist" href="${createLink(controller="user",action: 'passwordChangePage', params:[:])}">
                    <g:message code="ddbnext.Change_Password_Link" />
                </a>
                <a class="profile-link ${offForOpenId}" id="delete-account" title="<g:message code="ddbnext.User.Delete_Account" />" class="persist" href="${createLink(controller="user",action: 'delete')}">
                    <g:message code="ddbnext.User.Delete_Account" />
                </a>
            </div>

            <div class="control-group">
              <label class="control-label"><g:message code="ddbnext.Username" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-user"></i></span>
                  <input type="text" class="profile-input" id="username" name="username" placeholder="<g:message code="ddbnext.Username" />" value="${user.username}" ${disableForOpenId}>
                </div>
              </div>
            </div>
    
            <div class="control-group">
              <label class="control-label"><g:message code="ddbnext.User.First_Name" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-user"></i></span>
                  <input type="text" class="profile-input" id="fname" name="fname" placeholder="<g:message code="ddbnext.User.First_Name" />" value="${user.firstname}" ${disableForOpenId}>
                </div>
              </div>
            </div>
    
            <div class="control-group ">
              <label class="control-label"><g:message code="ddbnext.User.Last_Name" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-user"></i></span>
                  <input type="text" class="profile-input" id="lname" name="lname" placeholder="<g:message code="ddbnext.User.Last_Name" />" value="${user.lastname}" ${disableForOpenId}>
                </div>
              </div>
            </div>
    
            <div class="control-group">
              <label class="control-label"><g:message code="ddbnext.Email" /></label>
              <div class="controls">
                <div class="input-prepend">
                  <span class="add-on"><i class="icon-envelope"></i></span>
                  <input type="text" class="profile-input" id="email" name="email" placeholder="<g:message code="ddbnext.Email" />" value="${user.email}" ${disableForOpenId}>
                </div>
              </div>
            </div>

            <div class="control-group">
              <div class="controls checkbox">
                <div class="input-prepend">
                  <input type="checkbox" id="newsletter" class="profile-input" name="newsletter" ${newsletterCheck}>
                </div>
              </div>
              <label class="checkbox-label control-label"><g:message code="ddbnext.Newsletter_Subscription" /></label>
            </div>

            <div class="control-group">
              <label class="control-label"></label>
              <div class="controls">
                <button type="submit" class="btn-padding" title="<g:message code="ddbnext.Save_Changes"/>"><g:message code="ddbnext.Save_Changes"/></button>
              </div>
            </div>
        </div>
        <ul id="error-messages" class="off">
          <li><a><g:message code="ddbnext.Field_Required" /></a></li>
          <li><a><g:message code="ddbnext.Name_Compulsory_Characters_Number" /></a></li>
          <li><a><g:message code="ddbnext.Enter_A_Valid_Email" /></a></li>
        </ul>
    </g:form>
</body>
</html>
