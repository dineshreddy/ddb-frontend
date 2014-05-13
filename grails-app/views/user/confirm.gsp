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

    <title><g:message encodeAs="html" code="ddbnext.Confirm" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

    <meta name="page" content="confirm" />
    <meta name="layout" content="main" />

  </head>

<body>
  <div class="container confirmation">
    <g:if test="${errors != null && errors.size()>0}">
      <ddbcommon:renderErrors errors="${errors}" />
    </g:if>
    <g:if test="${messages != null && messages.size()>0}">
      <ddbcommon:renderMessages messages="${messages}" />
    </g:if>
  </div>
</body>
</html>
