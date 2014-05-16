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

<%@page import="de.ddb.common.constants.SupportedOpenIdProviders"%>
<%@page import="de.ddb.common.constants.LoginStatus"%>
<html>
  <head>
    <title><g:message encodeAs="html" code="ddbcommon.Login_Button" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

    <meta name="page" content="login" />
    <meta name="layout" content="main" />

  </head>
  <body>
    <div class="row login">
      <div class="span12">
        <div class="row heading">
          <div class="span6">
            <div class="fl"><h1><g:message encodeAs="html" code="ddbcommon.Login_Button"/></h1></div>
          </div>
        </div>
        <div class="row">
          <div class="span12">

            <ddbcommon:isNotLoggedIn>

              <div class="span4 dialog">
                <g:form controller="user" action="doLogin">
                  <g:hiddenField name="referrer" value="${referrer}" />
                  <g:if test="${loginStatus == LoginStatus.FAILURE}">
                    <div class="row login-error">
                      <div class="span4"> 
                        <g:message encodeAs="html" code="ddbcommon.Error_Email_Password_Combination" />
                      </div>
                    </div>
                  </g:if>
                  <g:if test="${loginStatus == LoginStatus.AUTH_PROVIDER_DENIED}">
                    <div class="row login-error">
                      <div class="span4">
                        <g:message encodeAs="html" code="ddbcommon.Error_Authentication_Provider_Denied" />
                      </div>
                    </div>
                  </g:if>
                  <g:if test="${loginStatus == LoginStatus.AUTH_PROVIDER_UNKNOWN}">
                    <div class="row login-error">
                      <div class="span4">
                        <g:message encodeAs="html" code="ddbcommon.Error_Authentication_Provider_Unknown" />
                      </div>
                    </div>
                  </g:if>
                  <g:if test="${loginStatus == LoginStatus.NO_COOKIES}">
                    <div class="row login-error">
                      <div class="span4">
                        <g:message encodeAs="html" code="ddbcommon.Error_Authentication_No_Cookies" />
                      </div>
                    </div>
                  </g:if>
                  <div class="row">
                    <div class="span4">
                      <label for="login-username"><g:message encodeAs="html" code="ddbcommon.Username_Or_Email" />:</label>
                    </div>  
                  </div>
                  <div class="row">
                    <div class="span4"> 
                      <input id="login-username" type="text" name="email" value=""/>
                    </div>
                  </div>
                  <div class="row spacer-vertical">
                    <div class="span4">
                      <label for="login-password"><g:message encodeAs="html" code="ddbcommon.Your_Password" />:</label>
                    </div>
                  </div>
                  <div class="row">
                    <div class="span4">
                      <input id="login-password" type="password" name="password" value=""/>
                    </div>
                  </div>
                  <div class="row spacer-vertical">
                    <div class="span4">
                      <button type="submit" class="login-button">
                        <g:message encodeAs="html" code="ddbcommon.Login_Button" />
                      </button>
                    </div>
                  </div>
                  <div class="row spacer-vertical">
                    <div class="span4">
                      <g:link controller="user" action="registration" class="login-link"><g:message encodeAs="html" code="ddbnext.Register" /></g:link>
                    </div>
                  </div>
                  <div class="row">
                    <div class="span4">
                      <g:link controller="user" action="passwordResetPage" class="login-link"><g:message encodeAs="html" code="ddbcommon.Forgot_Password" /></g:link>
                    </div>
                  </div>

                </g:form>
              </div>
              <div class="span4 openid" >
                <div class="row">
                  <div class="span4">
                    <g:message encodeAs="html" code="ddbcommon.Login_OpenID" />
                  </div>
                </div>
                <div class="row spacer-vertical">
                  <div class="span4">
                    <g:link controller="user" action="requestOpenIdLogin" params="${["provider": SupportedOpenIdProviders.GOOGLE, "referrer": referrer]}"><div class="openid-google"></div></g:link>
                  </div>
                </div>
                <div class="row">
                  <div class="span4">
                    <g:link controller="user" action="requestOpenIdLogin" params="${["provider": SupportedOpenIdProviders.YAHOO, "referrer": referrer]}"><div class="openid-yahoo"></div></g:link>
                  </div>
                </div>
              </div>

            </ddbcommon:isNotLoggedIn>

            <ddbcommon:isLoggedIn>
              <div class="span4 feedback">
                <g:if test="${loginStatus == LoginStatus.SUCCESS}">
                  <g:message encodeAs="html" code="ddbnext.Login_Success" />
                </g:if>
                <g:else>
                  <g:message encodeAs="html" code="ddbnext.Already_Logged_In" />
                </g:else>
              </div>

            </ddbcommon:isLoggedIn>

          </div>
        </div>
      </div>
    </div>

  </body>
</html>

