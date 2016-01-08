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
    <title><g:message code="ddbnext.Newsletter"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    <meta name="page" content="newsletter"/>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <div class="newsletter-container">
      <div class="container confirmation">
        <div class="static_content">
          <div class="profile-nav newsletter-header">
            <div><h1><g:message code="ddbnext.Newsletter"/></h1></div>
          </div>
          <ddbcommon:renderErrors errors="${errors}"/>
          <ddbcommon:renderMessages messages="${messages}"/>

          <%-- subscribe form --%>
          <g:form method="post" id="subscribe-form" name="subscribe-form" class="form-horizontal bb"
                  url="[controller: 'newsletter', action: 'subscribe']">
            <div class="control-group">
              <p><g:message code="ddbnext.Newsletter_Subscribe_Text1"/></p>
              <p><g:message code="ddbnext.Newsletter_Subscribe_Text2"/></p>
              <p><g:message code="ddbnext.Newsletter_Subscribe_Text3"/></p>
            </div>

            <div class="control-group">
              <label class="reg-label"><g:message code="ddbcommon.Email"/></label>
              <div><input type="text" class="profile-input" name="email"/></div>
            </div>

            <div class="control-group">
              <button type="submit" class="btn-padding"><g:message code="ddbnext.Newsletter_Subscribe"/></button>
            </div>
          </g:form>

          <%-- unsubscribe form --%>
          <g:form method="post" id="unsubscribe-form" name="unsubscribe-form" class="form-horizontal bb"
                  url="[controller: 'newsletter', action: 'unsubscribe']">
            <div class="control-group">
              <p><g:message code="ddbnext.Newsletter_Unsubscribe_Text1"/></p>
            </div>

            <div class="control-group">
              <label class="reg-label"><g:message code="ddbcommon.Email"/></label>
              <div><input type="text" class="profile-input" name="email"/></div>
            </div>

            <div class="control-group">
              <button type="submit" class="btn-padding"><g:message code="ddbnext.Newsletter_Unsubscribe"/></button>
            </div>
          </g:form>

          <%-- comment --%>
          <div class="control-group">
            <p><g:message code="ddbnext.Newsletter_Text"/></p>
          </div>
        </div>

        <%-- menu --%>
        <div class="static_marginal">
          <div><h3><g:message code="ddbnext.Show_more_information"/></h3></div>
          <ul class="plum-arrow">
            <li>
              <a class="profile-link" title="${g.message(code: 'ddbnext.Newsletter_Archive')}" class="persist"
                 href="../../content/newsletter/newsletter-archiv">
                <g:message code="ddbnext.Newsletter_Archive"/>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <ul id="error-messages" class="off">
      <li><a><g:message code="ddbcommon.Enter_A_Valid_Email"/></a></li>
      <li><a><g:message code="ddbcommon.User.Newsletter_Subscribe_Email_Required"/></a></li>
      <li><a><g:message code="ddbcommon.User.Newsletter_Unsubscribe_Email_Required"/></a></li>
    </ul>
  </body>
</html>