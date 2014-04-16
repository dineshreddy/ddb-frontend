<%@page import="de.ddb.common.exception.CultureGraphException.CultureGraphExceptionType"%>
<html>
  <head>
    <title>
      <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
        <g:message encodeAs="html" code="error.culturegraph.404.title"/>
      </g:if>
      <g:else>
        <g:message encodeAs="html" code="error.culturegraph.500.title"/>
      </g:else>
      - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/>
    </title>
    
    <meta name="page" content="500" />
    <meta name="layout" content="main" />
    
  </head>
  <body>
    <div class="errorpage">
      <h1>
        <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
          <g:message encodeAs="html" code="error.culturegraph.404.title"/>
        </g:if>
        <g:else>
          <g:message encodeAs="html" code="error.culturegraph.500.title"/>
        </g:else>
      </h1>
      <p>
        <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
          <g:message encodeAs="none" code="error.culturegraph.404.body"/>
        </g:if>
        <g:else>
          <g:message encodeAs="html" code="error.culturegraph.500.body"/>
        </g:else>
      </p>
      <hr />
      <g:if test="${exception}">
        <g:renderException exception="${exception}" />
      </g:if>
      <g:else>
        <b>DEV-Message:</b> No stacktrace available. Most likely, it was already consumed and logged to your console.
      </g:else>  
    </div>
    
  </body>
</html>
