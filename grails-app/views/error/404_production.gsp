<%@page import="de.ddb.next.ErrorController.Type404"%>
<html>
  <head>
    <title><g:message code="error.notfound.title"/> - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    
    <meta name="page" content="404" />
    <meta name="layout" content="main" />
    
  </head>
  <body>
    <div class="errorpage">

      <g:if test="${type==Type404.ITEM_NOT_FOUND}">
        <h1>
          <g:message code="error.notfound.title"/>
        </h1>
        <p>
          <g:message code="error.notfound.body"/>
        </p>
      </g:if>
      <g:elseif test="${type==Type404.FAVORITELIST_NOT_FOUND}">
        <h1>
          <g:message code="error.favlistnotfound.title"/>
        </h1>
        <p>
          <g:message code="error.favlistnotfound.body"/>
        </p>
      </g:elseif>
      <g:else>
        <h1>
          <g:message code="error.notfound.title"/>
        </h1>
        <p>
          <g:message code="error.notfound.body"/>
        </p>
      </g:else>
      
      <g:if test="${flash.message}">
        <div class="messages-container">
          <ul class="unstyled">
            <li><i class="icon-ok-circle"></i><span><g:message code="${flash.message}" /></span></li>
          </ul>
        </div>
      </g:if>
      <g:if test="${flash.error}">
        <div class="errors-container">
          <ul class="unstyled">
            <li><i class="icon-exclamation-sign"></i><span><g:message code="${flash.error}" /></span></li>
          </ul>
        </div>
      </g:if>
      
    </div>
  </body>
</html>
