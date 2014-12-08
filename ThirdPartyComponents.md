Concept for the Integration of Third Party Components in the DDB-Frontend
================

#This article describes how to integrate third-paty-components into the DDB-Frontend.

A third-party-component might require its own JavaScript-files, css-files and server-actions to get the data for display.
It might want to use the whole page for display (still embedded between header and footer of the DDB-Frontend Main-Page) or it is embedded within an existing page.
Nevertheless it will have its own div, so its own template, which again is wrapped either directly into the main template or into an existing one.

#Prerequisites
Third-party Components are located in a subfolder below web-app/third-party, accessible via /static/third-party/...

#Own page for third-party-component
## Template and endpoint is needed. Add Endpoint to UrlMappings.groovy, link to template in third-party folder.
Endpoint-Example:
"/test"(view:'/third-party/test/views/some.gsp')


#Third-party-component included in existing template
## Template is needed. State Link to third-party-template in template for page (<g:render template="/third-party/test/views/testpart" />). Put template starting with underscore and ending with .gsp in appropriate folder (_testpart.gsp)

Third-Party Component can get data by requesting an URL via a ddb-next Wrapper Service.
Wrapper Service has static methods that return data eg as Json

Example:
public class ServiceWrapper {
    
    public static def getData(uri) {
        def config = org.codehaus.groovy.grails.commons.ConfigurationHolder.config
        def backendUrl = config.ddb.backend.url
        def json = ApiConsumer.getJson(backendUrl , uri)
        return json
    }
}

#Call Wrapper Service from within template:
<g:set var="facets" value="${de.ddb.next.ServiceWrapper.getData('/search/facets/place_fct')}" />

Iterate through JSON:
<g:each in="${facets.facetValues}" var="facetValue">
<h1>${facetValue}</h1>
</g:each>

#Include CSS and JavaScript:
locate CSS and JavaScript somewhere below /third-party/<yourcomponent>, e.g. in subdirectories css and js.
Import files in your template:
<link rel="stylesheet" href="${resource(dir: 'third-party/<yourcomponent>/css', file: 'test.css')}" />
<script src="${resource(dir: 'third-party/<yourcomponent>/js', file: 'test.js')}" /></script>

#Ajax-call from Javascript to Backend:
In directory grails-app/controllers/de/ddb/next exists a class ApisController.
This class is called when calling endpoint /apis
/apis/search calls method search in Class ApisController.
Maybe you have to implement new methods in this class.

/apis/institutions/something?test=eins&test1=zwei
-method institutions in Class ApisController is called
-all path + query-parameters come in variable params (params.id = something, params.test=eins ...)

From there we can execute requests to the backend via Class ApiConsumer:
eg:  def jsonResp = ApiConsumer.getJson(grailsApplication.config.ddb.backend.url.toString(),'/search', query)

grailsApplication.config.ddb.backend.url references the property ddb.backend.url in the file ddb-next.properties in Folder .grails

The response can get returned as JSON to the javascript:
render (contentType:"text/json"){resultList}




