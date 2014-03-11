<%@page import="de.ddb.next.exception.CultureGraphException.CultureGraphExceptionType"%>
<html>
  <head>
    <title>
      <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
        <g:message code="error.culturegraph.404.title"/>
      </g:if>
      <g:else>
        <g:message code="error.culturegraph.500.title"/>
      </g:else>
      - <g:message code="ddbnext.Deutsche_Digitale_Bibliothek"/>
    </title>
    
    <meta name="page" content="500" />
    <meta name="layout" content="main" />
    
  </head>
  <body>
    <div class="errorpage">
      <h1>
        <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
          <g:message code="error.culturegraph.404.title"/>
        </g:if>
        <g:else>
          <g:message code="error.culturegraph.500.title"/>
        </g:else>
      </h1>
      <p>
        <g:if test="${exceptionType == CultureGraphExceptionType.RESPONSE_404 }">
          <g:message code="error.culturegraph.404.body"/>
        </g:if>
        <g:else>
          <g:message code="error.culturegraph.500.body"/>
        </g:else>
      </p>
    </div>    
  </body>
</html>
